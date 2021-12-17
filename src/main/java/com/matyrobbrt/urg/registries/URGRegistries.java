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

package com.matyrobbrt.urg.registries;

import static net.minecraft.util.registry.Registry.register;

import com.matyrobbrt.urg.UltimateResourceGenerators;
import com.mojang.serialization.Lifecycle;

import net.minecraft.block.material.Material;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

public class URGRegistries {

	public static final RegistryKey<Registry<Registry<?>>> URG_REGISTRIES_REGISTRY = createKey("registries");
	public static final RegistryKey<Registry<Material>> BLOCK_MATERIAL_REGISTRY = createKey("block_material");

	public static final Registry<Registry<?>> URG_REGISTRIES = new SimpleRegistry<>(URG_REGISTRIES_REGISTRY,
			Lifecycle.experimental());
	public static final Registry<Material> BLOCK_MATERIALS = makeRegistry(BLOCK_MATERIAL_REGISTRY);

	static <T> RegistryKey<Registry<T>> createKey(String name) {
		return RegistryKey.createRegistryKey(new ResourceLocation(UltimateResourceGenerators.MOD_ID, name));
	}

	static <T> Registry<T> makeRegistry(RegistryKey<Registry<T>> key) {
		SimpleRegistry<T> registry = new SimpleRegistry<>(key, Lifecycle.experimental());
		return Registry.register(URG_REGISTRIES, key.location().toString(), registry);
	}

	static {
		registerMaterials();
	}

	private static void registerMaterials() {
		register(BLOCK_MATERIALS, "air", Material.AIR);
		register(BLOCK_MATERIALS, "structural_air", Material.STRUCTURAL_AIR);
		register(BLOCK_MATERIALS, "portal", Material.PORTAL);
		register(BLOCK_MATERIALS, "cloth_decoration", Material.CLOTH_DECORATION);
		register(BLOCK_MATERIALS, "plant", Material.PLANT);
		register(BLOCK_MATERIALS, "water_plant", Material.WATER_PLANT);
		register(BLOCK_MATERIALS, "replaceable_plant", Material.REPLACEABLE_PLANT);
		register(BLOCK_MATERIALS, "replaceable_fireproof_plant", Material.REPLACEABLE_FIREPROOF_PLANT);
		register(BLOCK_MATERIALS, "replaceable_water_plant", Material.REPLACEABLE_WATER_PLANT);

		register(BLOCK_MATERIALS, "water", Material.WATER);
		register(BLOCK_MATERIALS, "bubble_column", Material.BUBBLE_COLUMN);
		register(BLOCK_MATERIALS, "lava", Material.LAVA);
		register(BLOCK_MATERIALS, "top_snow", Material.TOP_SNOW);
		register(BLOCK_MATERIALS, "fire", Material.FIRE);
		register(BLOCK_MATERIALS, "decoration", Material.DECORATION);
		register(BLOCK_MATERIALS, "web", Material.WEB);

		register(BLOCK_MATERIALS, "buildable_glass", Material.BUILDABLE_GLASS);
		register(BLOCK_MATERIALS, "clay", Material.CLAY);
		register(BLOCK_MATERIALS, "dirt", Material.DIRT);
		register(BLOCK_MATERIALS, "grass", Material.GRASS);
		register(BLOCK_MATERIALS, "ice_solid", Material.ICE_SOLID);
		register(BLOCK_MATERIALS, "sand", Material.SAND);
		register(BLOCK_MATERIALS, "sponge", Material.SPONGE);

		register(BLOCK_MATERIALS, "shulker_shell", Material.SHULKER_SHELL);
		register(BLOCK_MATERIALS, "explosive", Material.EXPLOSIVE);
		register(BLOCK_MATERIALS, "leaves", Material.LEAVES);
		register(BLOCK_MATERIALS, "glass", Material.GLASS);
		register(BLOCK_MATERIALS, "ice", Material.ICE);

		register(BLOCK_MATERIALS, "wood", Material.WOOD);
		register(BLOCK_MATERIALS, "nether_wood", Material.NETHER_WOOD);
		register(BLOCK_MATERIALS, "bamboo_sapling", Material.BAMBOO_SAPLING);
		register(BLOCK_MATERIALS, "bamboo", Material.BAMBOO);
		register(BLOCK_MATERIALS, "wool", Material.WOOL);

		register(BLOCK_MATERIALS, "cactus", Material.CACTUS);
		register(BLOCK_MATERIALS, "stone", Material.STONE);
		register(BLOCK_MATERIALS, "metal", Material.METAL);
		register(BLOCK_MATERIALS, "snow", Material.SNOW);
		register(BLOCK_MATERIALS, "heavy_metal", Material.HEAVY_METAL);
		register(BLOCK_MATERIALS, "barrier", Material.BARRIER);
		register(BLOCK_MATERIALS, "piston", Material.PISTON);
		register(BLOCK_MATERIALS, "vegetable", Material.VEGETABLE);
		register(BLOCK_MATERIALS, "egg", Material.EGG);
		register(BLOCK_MATERIALS, "cake", Material.CAKE);
	}

}
