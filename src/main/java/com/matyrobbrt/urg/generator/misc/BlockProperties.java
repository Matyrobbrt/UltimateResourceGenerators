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

package com.matyrobbrt.urg.generator.misc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matyrobbrt.urg.registries.URGRegistries;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ToolType;

public class BlockProperties {

	public static final BlockProperties DEFAULT = new BlockProperties();

	/**
	 * @deprecated Use {@link #getMaterial()}
	 */
	@Deprecated
	@Expose
	public String material = "heavy_metal";

	@Expose
	public float strength = 1.0f;

	/**
	 * @deprecated Use {@link #getHarvestTool()}
	 */
	@Deprecated
	@Expose
	@SerializedName("harvest_tool")
	public String harvestTool = "pickaxe";

	@Expose
	@SerializedName("harvest_level")
	public int harvestLevel = 1;

	@Expose
	@SerializedName("requires_correct_tool_for_drops")
	public boolean requiresCorrectToolForDrops = false;

	@Expose
	@SerializedName("no_occlusion")
	public boolean noOcclusion = true;

	@Expose
	@SerializedName("no_collission")
	public boolean noCollission = false;

	@Expose
	@SerializedName("light_level")
	public int lightLevel = 0;

	public Material getMaterial() { return URGRegistries.BLOCK_MATERIALS.get(new ResourceLocation(material)); }

	public ToolType getHarvestTool() { return ToolType.get(harvestTool); }

	public void applyProperties(Properties properties) {
		properties.strength(strength);
		properties.harvestTool(getHarvestTool());
		properties.harvestLevel(harvestLevel);
		if (requiresCorrectToolForDrops) {
			properties.requiresCorrectToolForDrops();
		}
		if (noOcclusion) {
			properties.noOcclusion();
		}
		if (noCollission) {
			properties.noCollission();
		}
		properties.lightLevel(state -> lightLevel);
	}
}
