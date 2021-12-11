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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.matyrobbrt.urg.generator.misc.BlockItemInfo;
import com.matyrobbrt.urg.generator.misc.FEInfo;
import com.matyrobbrt.urg.util.JsonParser;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistries;

public class GeneratorBlockParser {

	public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public static GeneratorBlock generatorFromJson(JsonObject obj) {
		GeneratorBlock generator = new GeneratorBlock(Properties.of(Material.METAL));
		//@formatter:off
		JsonParser.begin(obj)
		.ifKey("produced_item",
				val -> generator.producedItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(val.string())))
		.ifKey("produced_per_operation", val -> {
			if (val.integer() > 64) {
				generator.producedPerOperation = 64;
			} else {
				generator.producedPerOperation = val.integer();
			}
		})
		.ifKey("max_produced", val -> generator.maxProduced = val.integer())
		.ifKey("ticks_per_operation", val -> generator.ticksPerOperation = val.integer())
		.ifKey("keep_inventory", val -> generator.keepInventory = val.booleanValue())
		.ifKey("fe_info", val -> generator.feInfo = GSON.fromJson(val.jsonElement(), FEInfo.class))
		.ifKey("block_item", val -> generator.blockItemInfo = GSON.fromJson(val.jsonElement(), BlockItemInfo.class));
		//@formatter:on
		return generator;
	}

}
