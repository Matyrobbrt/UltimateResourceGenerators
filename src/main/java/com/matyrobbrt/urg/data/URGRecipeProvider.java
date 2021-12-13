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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.loading.FMLPaths;

public class URGRecipeProvider extends RecipeProvider {

	public final String packName;

	public URGRecipeProvider(DataGenerator pGenerator, String packName) {
		super(pGenerator);
		this.packName = packName;
	}

	@Override
	public void run(DirectoryCache pCache) throws IOException {
		Path path = Paths.get(FMLPaths.GAMEDIR.get().resolve("../../dev_urg_packs").toFile().getPath());
		Set<ResourceLocation> set = Sets.newHashSet();
		buildShapelessRecipes(recipe -> {
			if (!set.add(recipe.getId())) {
				throw new IllegalStateException("Duplicate recipe " + recipe.getId());
			} else {
				saveRecipe(pCache, recipe.serializeRecipe(), path.resolve(packName + "/data/"
						+ recipe.getId().getNamespace() + "/recipes/" + recipe.getId().getPath() + ".json"));
				JsonObject jsonobject = recipe.serializeAdvancement();
				if (jsonobject != null) {
					saveAdvancement(pCache, jsonobject, path.resolve(packName + "/data/" + recipe.getId().getNamespace()
							+ "/advancements/" + recipe.getAdvancementId().getPath() + ".json"));
				}

			}
		});
		if (this.getClass() == URGRecipeProvider.class) // Forge: Subclasses don't need this.
			saveAdvancement(
					pCache, Advancement.Builder.advancement()
							.addCriterion("impossible", new ImpossibleTrigger.Instance()).serializeToJson(),
					path.resolve("data/minecraft/advancements/recipes/root.json"));
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		// DO NOT CALL THE SUPER
	}

}
