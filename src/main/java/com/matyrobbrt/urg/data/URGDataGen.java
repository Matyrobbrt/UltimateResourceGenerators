package com.matyrobbrt.urg.data;

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
		generatorInfo.put(vanillaGenerators("cobblestone_generator"), createInfo(info -> {
			info.producedItem = Items.COBBLESTONE.getRegistryName().toString();
			info.ticksPerOperation = 100;
			defaultRender(info);
		}));

		generatorInfo.put(vanillaGenerators("gravel_generator"), createInfo(info -> {
			info.producedItem = Items.GRAVEL.getRegistryName().toString();
			info.ticksPerOperation = 140;
			defaultRender(info);
		}));

		generatorInfo.put(vanillaGenerators("sand_generator"), createInfo(info -> {
			info.producedItem = Items.SAND.getRegistryName().toString();
			info.ticksPerOperation = 200;

			info.blockProperties.material = "sand";
			defaultRender(info);
		}));

		generatorInfo.put(vanillaGenerators("clay_ball_generator"), createInfo(info -> {
			info.producedItem = Items.CLAY_BALL.getRegistryName().toString();
			info.ticksPerOperation = 220;

			info.blockProperties.material = "clay";
			defaultRender(info);
		}));

		generatorInfo.put(vanillaGenerators("diamond_generator"), createInfo(info -> {
			info.producedItem = registryName(Items.DIAMOND);
			info.ticksPerOperation = 500;
			info.maxProduced = 350;

			info.blockProperties.harvestLevel = 3;
			info.blockProperties.strength = 5f;
			defaultRender(info);
		}));
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
