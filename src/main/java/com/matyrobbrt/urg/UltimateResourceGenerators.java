/**
 * This file is part of the Ultimate Resource Generators Minecraft mod and is
 * licensed under the MIT license:
 *
 * MIT License
 *
 * Copyright (c) 2021 Matyrobbrt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.matyrobbrt.urg;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.matyrobbrt.lib.ClientSetup;
import com.matyrobbrt.lib.MatyLib;
import com.matyrobbrt.lib.ModSetup;
import com.matyrobbrt.lib.annotation.SyncValue;
import com.matyrobbrt.lib.registry.annotation.AnnotationProcessor;
import com.matyrobbrt.lib.registry.annotation.RegisterItem;
import com.matyrobbrt.lib.registry.annotation.RegistryHolder;
import com.matyrobbrt.lib.wrench.DefaultWrenchBehaviours;
import com.matyrobbrt.lib.wrench.IWrenchBehaviour;
import com.matyrobbrt.lib.wrench.WrenchIMC;
import com.matyrobbrt.lib.wrench.WrenchMode;
import com.matyrobbrt.lib.wrench.WrenchResult;
import com.matyrobbrt.urg.client.URGClientSetup;
import com.matyrobbrt.urg.generator.GeneratorBlock;
import com.matyrobbrt.urg.generator.GeneratorTileEntity.ItemHandler;
import com.matyrobbrt.urg.generator.misc.URGEnergyStorage;
import com.matyrobbrt.urg.network.URGNetwork;
import com.matyrobbrt.urg.packs.URGGeneratorsReloadListener;
import com.matyrobbrt.urg.packs.URGPackFinder;
import com.matyrobbrt.urg.packs.URGResourceManager;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

@RegistryHolder(modid = UltimateResourceGenerators.MOD_ID)
@Mod(value = UltimateResourceGenerators.MOD_ID)
public class UltimateResourceGenerators extends ModSetup {

	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "urg";

	public static final AnnotationProcessor ANN_PROCESSOR = new AnnotationProcessor(MOD_ID);

	public UltimateResourceGenerators() {
		super(MOD_ID);
		modBus.addListener(this::onConstructMod);
		modBus.addListener(this::newRegistry);

		forgeBus.addListener(this::onServerAboutToStart);
		forgeBus.addListener(this::addReloadListener);

		URGResourceManager.instance().addResourceReloadListener(URGGeneratorsReloadListener.INSTANCE);

		SyncValue.Helper.registerSerializer(ItemHandler.class, nbt -> {
			ItemHandler handler = new ItemHandler(nbt.getInt("slotsSize"));
			handler.deserializeNBT(nbt.getCompound("handler"));
			return handler;
		}, (nbt, handler) -> {
			nbt.put("handler", handler.serializeNBT());
			nbt.putInt("slotsSize", handler.getSlots());
		});

		SyncValue.Helper.registerSerializer(URGEnergyStorage.class, URGEnergyStorage::fromNbt,
				(nbt, energy) -> energy.deserialize(nbt));
	}

	private void addReloadListener(final AddReloadListenerEvent event) {
		event.addListener(URGGeneratorsReloadListener.INSTANCE);
	}

	private void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		addPacks(event.getServer().getPackRepository());
	}

	private static CompletableFuture<URGResourceManager> loaderFuture;

	private void onConstructMod(final FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			URGResourceManager instance = URGResourceManager.instance();
			loaderFuture = instance.beginLoading(Util.backgroundExecutor(), Runnable::run);
		});
	}

	private void newRegistry(RegistryEvent.NewRegistry event) {
		try {
			loaderFuture.get().finishLoading();
			loaderFuture = null;
		} catch (InterruptedException e) {
			LOGGER.error("URG packs loader future interrupted!");
		} catch (ExecutionException e) {
			Throwable pCause = e.getCause();
			throw new ReportedException(CrashReport.forThrowable(pCause, "Error loading URG packs!"));
		}
	}

	@Override
	public void onCommonSetup(FMLCommonSetupEvent event) {
		URGNetwork.register();
	}

	@Override
	public AnnotationProcessor annotationProcessor() {
		return ANN_PROCESSOR;
	}

	@Override
	public Optional<Supplier<ClientSetup>> clientSetup() {
		return Optional.of(() -> new URGClientSetup(modBus));
	}

	@Override
	public void onInterModEnqueue(InterModEnqueueEvent event) {
		InterModComms.sendTo(MatyLib.MOD_ID, WrenchIMC.REGISTER_WRENCH_BEHAVIOUR_METHOD,
				() -> normalDismantleDep(URGGeneratorsReloadListener.INSTANCE.streamGenerators().map(Entry::getValue)
						.collect(Collectors.toList()).toArray(new GeneratorBlock[] {})));
	}

	/**
	 * Should be in {@link DefaultWrenchBehaviours} in the next MatyLib version
	 * 
	 * @deprecated
	 * @param blocks
	 * @return
	 */
	@Deprecated
	public static final IWrenchBehaviour normalDismantleDep(Block... blocks) {
		return (wrench, mode, player, state, pos, level) -> {
			if ((mode != WrenchMode.DISMANTALE) || !Arrays.asList(blocks).contains(state.getBlock())
					|| level.isClientSide()) {
				return WrenchResult.FAIL;
			}
			List<ItemStack> drops = state.getDrops(
					new LootContext.Builder((ServerWorld) level).withParameter(LootParameters.TOOL, ItemStack.EMPTY)
							.withParameter(LootParameters.BLOCK_ENTITY, level.getBlockEntity(pos))
							.withParameter(LootParameters.ORIGIN, new Vector3d(pos.getX(), pos.getY(), pos.getZ())));
			drops.forEach(stack -> {
				ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
				level.addFreshEntity(item);
			});
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return WrenchResult.CONSUME;
		};
	}

	@RegisterItem("tab_icon")
	public static final Item TAB_ICON = new Item(new Item.Properties()) {

		@Override
		public void fillItemCategory(ItemGroup pGroup, net.minecraft.util.NonNullList<ItemStack> pItems) {
			// DO NOT SHOW
		}
	};

	public static final ItemGroup URG_TAB = new ItemGroup(ItemGroup.TABS.length, "urg") {

		@Override
		public void fillItemList(NonNullList<ItemStack> pItems) {
			super.fillItemList(pItems);
			pItems.sort((stack1, stack2) -> stack1.getItem().getRegistryName().getPath()
					.compareTo(stack2.getItem().getRegistryName().getPath()));
		}

		@Override
		public ItemStack makeIcon() {
			return TAB_ICON.getDefaultInstance();
		}
	};

	public static boolean isProduction() { return true; }

	public static void addPacks(ResourcePackList packs) {
		packs.addPackFinder(URGPackFinder.FINDER);
		if (URGPackFinder.DEV_ENVIRONMENT.getLoaderDirectory() != URGPackFinder.FINDER.getLoaderDirectory()
				&& !FMLEnvironment.production) {
			packs.addPackFinder(URGPackFinder.DEV_ENVIRONMENT);
		}
	}

}
