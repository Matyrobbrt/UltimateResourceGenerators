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

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matyrobbrt.urg.generator.GeneratorBlockParser;
import com.matyrobbrt.urg.registries.URGRegistries;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistries;

public class GeneratorInfo implements Cloneable {

	@Expose
	@SerializedName("block_properties")
	public BlockProperties blockProperties = BlockProperties.DEFAULT;

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
	public FEInfo feInfo = FEInfo.DEFAULT;

	@Expose
	@SerializedName("block_item")
	public BlockItemInfo blockItemInfo = BlockItemInfo.DEFAULT;

	@Expose
	@SerializedName("propagates_sky_light")
	public boolean propagatesSkyLight = true;

	@Expose
	@SerializedName("tile_entity_renderer")
	public RenderInfo tileRenderInfo = RenderInfo.DEFAULT;

	@Expose
	@SerializedName("copy_from")
	public String copyFrom = "";

	public Item getProducedItem() { return ForgeRegistries.ITEMS.getValue(new ResourceLocation(producedItem)); }

	public RenderType getRenderType() { return URGRegistries.RENDER_TYPES.get(new ResourceLocation(renderType)); }

	public static final GeneratorInfo DEFAULT = new GeneratorInfo();

	public GeneratorInfo redirect(GeneratorInfo other) {
		if (other == null) { return this; }
		redirectField(ge -> ge.blockProperties, (ge, toSet) -> ge.blockProperties = toSet, other);
		redirectField(ge -> ge.renderType, (ge, toSet) -> ge.renderType = toSet, other);
		redirectField(ge -> ge.producedItem, (ge, toSet) -> ge.producedItem = toSet, other);
		redirectField(ge -> ge.producedPerOperation, (ge, toSet) -> ge.producedPerOperation = toSet, other);
		redirectField(ge -> ge.maxProduced, (ge, toSet) -> ge.maxProduced = toSet, other);
		redirectField(ge -> ge.ticksPerOperation, (ge, toSet) -> ge.ticksPerOperation = toSet, other);
		redirectField(ge -> ge.keepInventory, (ge, toSet) -> ge.keepInventory = toSet, other);
		redirectField(ge -> ge.autoOutput, (ge, toSet) -> ge.autoOutput = toSet, other);
		redirectField(ge -> ge.feInfo, (ge, toSet) -> ge.feInfo = toSet, other);
		redirectField(ge -> ge.blockItemInfo, (ge, toSet) -> ge.blockItemInfo = toSet, other);
		redirectField(ge -> ge.propagatesSkyLight, (ge, toSet) -> ge.propagatesSkyLight = toSet, other);
		redirectField(ge -> ge.tileRenderInfo, (ge, toSet) -> ge.tileRenderInfo = toSet, other);
		redirectField(ge -> ge.copyFrom, (ge, toSet) -> ge.copyFrom = toSet, other);
		return this;
	}

	private <T> void redirectField(Function<GeneratorInfo, T> getter, BiConsumer<GeneratorInfo, T> setter,
			GeneratorInfo other) {
		T toSet = getter.apply(other);
		if (toSet != getter.apply(DEFAULT) && toSet != getter.apply(this)) {
			setter.accept(this, toSet);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static final class Redirect {

		private final JsonObject json;

		public Redirect(JsonObject json) {
			this.json = json;
		}

		public GeneratorInfo getRedirectFor(ResourceLocation name) {
			if (json.has(name.toString())) {
				return GeneratorBlockParser.GSON.fromJson(json.get(name.toString()), GeneratorInfo.class);
			}
			return null;
		}

	}
}
