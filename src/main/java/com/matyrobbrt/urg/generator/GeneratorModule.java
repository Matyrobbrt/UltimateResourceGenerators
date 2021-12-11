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

package com.matyrobbrt.urg.generator;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import com.matyrobbrt.lib.annotation.RL;
import com.matyrobbrt.lib.module.IModule;
import com.matyrobbrt.lib.module.Module;
import com.matyrobbrt.urg.UltimateResourceGenerators;
import com.matyrobbrt.urg.client.DefaultGeneratorTER;
import com.matyrobbrt.urg.packs.URGGeneratorsReloadListener;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Module(id = @RL(modid = UltimateResourceGenerators.MOD_ID, path = "generator"))
@Mod.EventBusSubscriber(bus = Bus.MOD, modid = UltimateResourceGenerators.MOD_ID)
public class GeneratorModule implements IModule {

	public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

	public static TileEntityType<GeneratorTileEntity> GENERATOR_TILE_ENTITY_TYPE;

	@Override
	public void register(IEventBus modBus, IEventBus forgeBus) {
		IModule.super.register(modBus, forgeBus);
		modBus.addGenericListener(Block.class, this::registerGenerators);
		modBus.addGenericListener(Item.class, this::registerItems);
		modBus.addGenericListener(TileEntityType.class, GeneratorModule::registerTEType);
	}

	public void registerGenerators(final RegistryEvent.Register<Block> event) {
		LOGGER.info("Started Registering URG Generators. Errors about unexpected registry domains are harmless...");
		URGGeneratorsReloadListener.INSTANCE.streamGenerators()
				.map(entry -> entry.getValue().setRegistryName(entry.getKey())).forEach(event.getRegistry()::register);
		URGGeneratorsReloadListener.INSTANCE.registered = true;
		LOGGER.info("Done registering URG Generators");
	}

	public void registerItems(final RegistryEvent.Register<Item> event) {
		LOGGER.info(
				"Started Registering URG Generators BlockItems. Errors about unexpected registry domains are harmless...");
		URGGeneratorsReloadListener.INSTANCE.streamGenerators()
				.map(entry -> new BlockItem(entry.getValue(),
						new Item.Properties().tab(UltimateResourceGenerators.URG_TAB)
								.stacksTo(entry.getValue().blockItemInfo.stack_size)).setRegistryName(entry.getKey()))
				.forEach(event.getRegistry()::register);
		LOGGER.info("Done registering URG Generators BlockItems");
	}

	private static void registerTEType(final RegistryEvent.Register<TileEntityType<?>> event) {
		GENERATOR_TILE_ENTITY_TYPE = TileEntityType.Builder
				.of(GeneratorTileEntity::new, URGGeneratorsReloadListener.INSTANCE.streamGenerators()
						.map(Entry::getValue).collect(Collectors.toList()).toArray(new GeneratorBlock[] {}))
				.build(null);

		event.getRegistry().register(GENERATOR_TILE_ENTITY_TYPE
				.setRegistryName(new ResourceLocation(UltimateResourceGenerators.MOD_ID, "resource_generator")));
	}

	@Override
	public void onClientSetup(FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntityRenderer(GENERATOR_TILE_ENTITY_TYPE, DefaultGeneratorTER::new);
	}

}
