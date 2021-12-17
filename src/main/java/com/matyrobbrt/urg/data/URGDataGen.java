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

import javax.annotation.Nonnull;

import com.matyrobbrt.lib.datagen.recipe.Output;
import com.matyrobbrt.lib.datagen.recipe.vanilla.KeyIngredient;
import com.matyrobbrt.lib.datagen.recipe.vanilla.Pattern;
import com.matyrobbrt.lib.util.helper.DataGenHelper;
import com.matyrobbrt.urg.UltimateResourceGenerators;
import com.matyrobbrt.urg.generator.misc.GeneratorInfo;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

@SuppressWarnings("deprecation")
public class URGDataGen extends URGGeneratorProvider {

	public URGDataGen(DataGenerator gen) {
		super(gen, "vanilla_generators");
	}

	public static final String VANILLA_GENERATORS = "vanilla_urg";

	public Consumer<IFinishedRecipe> recipeConsumer;

	@Override
	protected void createInfo() {
		createVanillaGenerator(Items.COBBLESTONE);
		createVanillaGenerator(Items.GRAVEL);
		createVanillaGenerator(Items.SAND);
		createVanillaGenerator("diamond_generator", info -> {
			info.producedItem = registryName(Items.DIAMOND);
			info.ticksPerOperation = 120;
			info.maxProduced = 350;

			info.blockProperties.harvestLevel = 3;
			info.blockProperties.strength = 5f;
			defaultRender(info);
		});
		createVanillaGenerator(Items.REDSTONE);
		createVanillaGenerator(Items.DIRT);
		createVanillaGenerator(Items.END_STONE);
		createVanillaGenerator(Items.GRASS);
		createVanillaGenerator(Items.GLOWSTONE);
		createVanillaGenerator(Items.CLAY);
		createVanillaGenerator(Items.MYCELIUM);
		createVanillaGenerator(Items.NETHERRACK);
		createVanillaGenerator(Items.OBSIDIAN);
		createVanillaGenerator(Items.QUARTZ);
		createVanillaGenerator(Items.SOUL_SAND);
		createVanillaGenerator(Items.SNOW);
		createVanillaGenerator(Items.ICE);

		/*
		 * createVanillaGenerator(Items.NETHERITE_BLOCK, info -> {
		 * info.blockItemInfo.rarity = "EPIC"; info.blockItemInfo.stackSize = 12;
		 * 
		 * info.feInfo.feUsedPerTick = 400; info.feInfo.feTransferRate = 410;
		 * info.feInfo.usesFE = true;
		 * 
		 * info.ticksPerOperation = 400; info.autoOutput = false;
		 * info.blockProperties.lightLevel = 7; info.maxProduced = 16; });
		 */
	}

	private void createVanillaGenerator(Item item) {
		createVanillaGenerator(item, info -> {});
	}

	private void createVanillaGenerator(Item item, Consumer<GeneratorInfo> extraInfo) {
		createVanillaGenerator(item.getRegistryName().getPath() + "_generator", info -> {
			info.producedItem = registryName(item);
			info.ticksPerOperation = 40;

			info.blockProperties.harvestLevel = 1;
			info.blockProperties.strength = 2f;
			info.blockProperties.harvestTool = "pickaxe";

			defaultRender(info);
			extraInfo.accept(info);
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

	private static final class RecipeProvider extends URGRecipeProvider {

		public RecipeProvider(DataGenerator pGenerator) {
			super(pGenerator, "vanilla_generators");
		}

		@Override
		protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
			normalRecipe(consumer, Items.COBBLESTONE, Tags.Items.COBBLESTONE);
			normalRecipe(consumer, Items.GRAVEL, Tags.Items.GRAVEL);
			normalRecipe(consumer, Items.SAND, Tags.Items.SAND);
			normalRecipe(consumer, Items.DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
			normalRecipe(consumer, Items.REDSTONE, Tags.Items.DUSTS_REDSTONE);
			normalRecipe(consumer, Items.DIRT);
			normalRecipe(consumer, Items.GRASS);
			normalRecipe(consumer, Items.GLOWSTONE);
			normalRecipe(consumer, Items.CLAY);
			normalRecipe(consumer, Items.MYCELIUM);
			normalRecipe(consumer, Items.NETHERRACK, Tags.Items.NETHERRACK);
			normalRecipe(consumer, Items.OBSIDIAN, Tags.Items.OBSIDIAN);
			normalRecipe(consumer, Items.QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ);
			normalRecipe(consumer, Items.SOUL_SAND);
			normalRecipe(consumer, Items.SNOW);
			normalRecipe(consumer, Items.ICE);
		}

		public static void normalRecipe(Consumer<IFinishedRecipe> consumer, Item producedItem) {
			newShapedRecipe(consumer, new Output(getVPGenerator(producedItem), 1), new Pattern("CCC", "WGL", "CCC"),
					new KeyIngredient('C', producedItem), new KeyIngredient('W', Items.WATER_BUCKET),
					new KeyIngredient('G', Tags.Items.GLASS), new KeyIngredient('L', Items.LAVA_BUCKET));
		}

		public static void normalRecipe(Consumer<IFinishedRecipe> consumer, Item producedItem, ITag<Item> tag) {
			newShapedRecipe(consumer, new Output(getVPGenerator(producedItem), 1), new Pattern("CCC", "WGL", "CCC"),
					new KeyIngredient('C', tag), new KeyIngredient('W', Items.WATER_BUCKET),
					new KeyIngredient('G', Tags.Items.GLASS), new KeyIngredient('L', Items.LAVA_BUCKET));
		}

		public static void newShapedRecipe(Consumer<IFinishedRecipe> consumer, Output output, Pattern pattern,
				@Nonnull KeyIngredient... ingredients) {
			newShapedRecipe(consumer, output, pattern, output.getItem().getRegistryName(), ingredients);
		}

		public static void newShapedRecipe(Consumer<IFinishedRecipe> consumer, Output output, Pattern pattern,
				ResourceLocation name, @Nonnull KeyIngredient... ingredients) {
			ShapedRecipeBuilder recipe = new ShapedRecipeBuilder(output.getItem(), output.getCount());
			pattern.getShapedRecipePattern(recipe);
			for (KeyIngredient ingredient : ingredients) {
				ingredient.defineShapedRecipe(recipe);
			}
			recipe.unlockedBy("has_item", DataGenHelper.Criterion.hasAir());
			recipe.save(consumer, name);
		}

		public static Item getVPGenerator(Item item) {
			return ForgeRegistries.ITEMS.getValue(
					new ResourceLocation(VANILLA_GENERATORS, item.getRegistryName().getPath() + "_generator"));
		}

	}

	@EventBusSubscriber(bus = Bus.MOD, modid = UltimateResourceGenerators.MOD_ID)
	public static final class EventHandler {

		@SubscribeEvent
		public static void onDatagen(final GatherDataEvent event) {
			DataGenerator gen = event.getGenerator();
			gen.addProvider(new URGDataGen(gen));
			gen.addProvider(new RecipeProvider(gen));
		}
	}

}
