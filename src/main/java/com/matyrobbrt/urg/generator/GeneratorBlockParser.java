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

package com.matyrobbrt.urg.generator;

import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.matyrobbrt.urg.generator.misc.BlockItemInfo;
import com.matyrobbrt.urg.generator.misc.RenderInfo;
import com.matyrobbrt.urg.registries.URGRegistries;
import com.matyrobbrt.urg.util.JsonParser;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

public class GeneratorBlockParser {

	public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public static GeneratorBlock generatorFromJson(JsonObject obj) {
		AtomicReference<Material> material = new AtomicReference<>(Material.HEAVY_METAL);
		JsonParser.begin(obj.has("block_properties") ? obj.getAsJsonObject("block_properties") : new JsonObject())
				.ifKey("material",
						val -> material.set(URGRegistries.BLOCK_MATERIALS.get(new ResourceLocation(val.string()))));
		Properties properties = Properties.of(material.get());
		//@formatter:off
		JsonParser.begin(obj.has("block_properties") ? obj.getAsJsonObject("block_properties") : new JsonObject())
			.ifKey("strength", val -> properties.strength(val.floatValue()))
			.ifKey("harvest_tool", val -> properties.harvestTool(ToolType.get(val.string())), () -> properties.harvestTool(ToolType.PICKAXE))
			.ifKey("harvest_level", val -> properties.harvestLevel(val.integerValue()), () -> properties.harvestLevel(1))
			.ifKey("requires_correct_tool_for_drops", val -> {
				if (val.booleanValue()) {
					properties.requiresCorrectToolForDrops();
				}
			})
			.ifKey("no_occlusion", val -> {
				if (val.booleanValue()) {
					properties.noOcclusion();
				}
			}, properties::noOcclusion);
		GeneratorBlock generator = new GeneratorBlock(properties);
		JsonParser.begin(obj)
		.ifKey("produced_item",
				val -> generator.producedItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(val.string())))
		.ifKey("produced_per_operation", val -> {
			if (val.integerValue() > 64) {
				generator.producedPerOperation = 64;
			} else {
				generator.producedPerOperation = val.integerValue();
			}
		})
		.ifKey("max_produced", val -> generator.maxProduced = val.integerValue())
		.ifKey("ticks_per_operation", val -> generator.ticksPerOperation = val.integerValue())
		.ifKey("keep_inventory", val -> generator.keepInventory = val.booleanValue())
		.ifKey("auto_output", val -> generator.autoOutput = val.booleanValue())
		//TODO: figure out why the energy mode doesnt fully work 
		//.ifKey("fe_info", val -> generator.feInfo = GSON.fromJson(val.jsonElement(), FEInfo.class))
		.ifKey("block_item", val -> generator.blockItemInfo = GSON.fromJson(val.jsonElement(), BlockItemInfo.class))
		.ifKey("propagates_sky_light", val -> generator.propagatesSkyLight = val.booleanValue())
		.ifKey("tile_entity_renderer", val -> generator.tileRenderInfo = GSON.fromJson(val.jsonElement(), RenderInfo.class))
		.ifKey("render_types", val -> generator.renderType = URGRegistries.RENDER_TYPES.get(new ResourceLocation(val.string())));
		//@formatter:on
		return generator;
	}

}
