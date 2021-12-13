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

package com.matyrobbrt.urg.data;

import java.util.function.Consumer;

import com.matyrobbrt.urg.UltimateResourceGenerators;
import com.matyrobbrt.urg.generator.misc.GeneratorInfo;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class URGDataGen extends URGGeneratorProvider {

	public URGDataGen(DataGenerator gen) {
		super(gen, "vanilla_generators");
	}

	public static final String VANILLA_GENERATORS = "vanilla_urg";

	@SuppressWarnings("deprecation")
	@Override
	protected void createInfo() {
		createVanillaGenerator("cobblestone_generator", info -> {
			info.producedItem = Items.COBBLESTONE.getRegistryName().toString();
			info.ticksPerOperation = 100;
			defaultRender(info);
		});

		createVanillaGenerator("gravel_generator", info -> {
			info.producedItem = Items.GRAVEL.getRegistryName().toString();
			info.ticksPerOperation = 140;
			defaultRender(info);
		});

		createVanillaGenerator("sand_generator", info -> {
			info.producedItem = Items.SAND.getRegistryName().toString();
			info.ticksPerOperation = 200;

			info.blockProperties.material = "sand";
			defaultRender(info);
		});

		createVanillaGenerator("clay_ball_generator", info -> {
			info.producedItem = Items.CLAY_BALL.getRegistryName().toString();
			info.ticksPerOperation = 220;

			info.blockProperties.material = "clay";
			defaultRender(info);
		});

		createVanillaGenerator("diamond_generator", info -> {
			info.producedItem = registryName(Items.DIAMOND);
			info.ticksPerOperation = 500;
			info.maxProduced = 350;

			info.blockProperties.harvestLevel = 3;
			info.blockProperties.strength = 5f;
			defaultRender(info);
		});

		createVanillaGenerator("redstone_generator", info -> {
			info.producedItem = registryName(Items.REDSTONE);
			info.ticksPerOperation = 290;

			info.blockProperties.harvestLevel = 2;
			info.blockProperties.strength = 3f;

			defaultRender(info);
		});
	}

	private void createVanillaGenerator(String name, Consumer<GeneratorInfo> info) {
		generatorInfo.put(vanillaGenerators(name), createInfo(info));
		generateModelsGenerators.add(vanillaGenerators(name));
	}

	private static void defaultRender(GeneratorInfo info) {
		info.tileRenderInfo.usesDefaultRender = true;
	}

	private static String registryName(IForgeRegistryEntry<?> obj) {
		return obj.getRegistryName().toString();
	}

	private static ResourceLocation vanillaGenerators(String name) {
		return new ResourceLocation(VANILLA_GENERATORS, name);
	}

	@EventBusSubscriber(bus = Bus.MOD, modid = UltimateResourceGenerators.MOD_ID)
	public static final class EventHandler {

		@SubscribeEvent
		public static void onDatagen(final GatherDataEvent event) {
			DataGenerator gen = event.getGenerator();
			gen.addProvider(new URGDataGen(gen));
		}
	}

}
