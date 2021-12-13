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

import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistries;

public class GeneratorInfo {

	@Expose
	@SerializedName("block_properties")
	public BlockProperties blockProperties = new BlockProperties();

	/**
	 * @deprecated Use {@link #getRenderType()}
	 */
	@Expose
	@SerializedName("render_type")
	@Deprecated
	public String renderType = "cutout";

	/**
	 * @deprecated Use {@link #getProducedItem()}
	 */
	@Expose
	@SerializedName("produced_item")
	@Deprecated
	public String producedItem = "air";

	@Expose
	@SerializedName("produced_per_operation")
	public int producedPerOperation = 1;

	@Expose
	@SerializedName("max_produced")
	public int maxProduced = -1;

	@Expose
	@SerializedName("ticks_per_operation")
	public int ticksPerOperation = 200;

	@Expose
	@SerializedName("keep_inventory")
	public boolean keepInventory = true;

	@Expose
	@SerializedName("auto_output")
	public boolean autoOutput = true;

	@Expose
	@SerializedName("fe_info")
	public FEInfo feInfo = new FEInfo();

	@Expose
	@SerializedName("block_item")
	public BlockItemInfo blockItemInfo = new BlockItemInfo();

	@Expose
	@SerializedName("propagates_sky_light")
	public boolean propagatesSkyLight = true;

	@Expose
	@SerializedName("tile_entity_renderer")
	public RenderInfo tileRenderInfo = new RenderInfo();

	public Item getProducedItem() { return ForgeRegistries.ITEMS.getValue(new ResourceLocation(producedItem)); }

	public RenderType getRenderType() { return URGRegistries.RENDER_TYPES.get(new ResourceLocation(renderType)); }
}
