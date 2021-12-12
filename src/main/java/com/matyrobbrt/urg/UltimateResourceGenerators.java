/**
 * This file is part of the Machina Minecraft (Java Edition) mod and is licensed
 * under the MIT license:
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
 *
 * If you want to contribute please join https://discord.com/invite/x9Mj63m4QG.
 * More information can be found on Github: https://github.com/Cy4Shot/MACHINA
 */

package com.matyrobbrt.urg;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.matyrobbrt.lib.ClientSetup;
import com.matyrobbrt.lib.ModSetup;
import com.matyrobbrt.lib.annotation.SyncValue;
import com.matyrobbrt.lib.registry.annotation.AnnotationProcessor;
import com.matyrobbrt.lib.registry.annotation.RegisterItem;
import com.matyrobbrt.lib.registry.annotation.RegistryHolder;
import com.matyrobbrt.lib.util.extender.CustomPackTypes;
import com.matyrobbrt.urg.generator.GeneratorTileEntity.ItemHandler;
import com.matyrobbrt.urg.generator.misc.URGEnergyStorage;
import com.matyrobbrt.urg.network.URGNetwork;
import com.matyrobbrt.urg.packs.URGGeneratorsReloadListener;
import com.matyrobbrt.urg.packs.URGPackFinder;
import com.matyrobbrt.urg.packs.URGResourceManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.Util;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@RegistryHolder(modid = UltimateResourceGenerators.MOD_ID)
@Mod(value = UltimateResourceGenerators.MOD_ID)
public class UltimateResourceGenerators extends ModSetup {

	public static final ResourcePackType PACK_TYPE = CustomPackTypes.create("URG", "urg");

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
		event.getServer().getPackRepository().addPackFinder(URGPackFinder.FINDER);
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

	@RegisterItem("tab_icon")
	public static final Item TAB_ICON = new Item(new Item.Properties()) {

		@Override
		public void fillItemCategory(ItemGroup pGroup, net.minecraft.util.NonNullList<ItemStack> pItems) {
			// DO NOT SHOW
		}
	};

	public static final ItemGroup URG_TAB = new ItemGroup(ItemGroup.TABS.length, "urg") {

		@Override
		public ItemStack makeIcon() {
			return TAB_ICON.getDefaultInstance();
		}
	};

	private static final class URGClientSetup extends ClientSetup {

		public URGClientSetup(IEventBus modBus) {
			super(modBus);
			Minecraft.getInstance().getResourcePackRepository().addPackFinder(URGPackFinder.FINDER);
		}

		@Override
		public void onClientSetup(FMLClientSetupEvent event) {
			URGGeneratorsReloadListener.INSTANCE.forEachGenerator(
					(rl, generator) -> RenderTypeLookup.setRenderLayer(generator, RenderType.cutoutMipped()));
		}

	}

}
