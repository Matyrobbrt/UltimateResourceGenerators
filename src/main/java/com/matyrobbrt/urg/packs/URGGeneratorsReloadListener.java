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

package com.matyrobbrt.urg.packs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.matyrobbrt.urg.UltimateResourceGenerators;
import com.matyrobbrt.urg.generator.GeneratorBlock;
import com.matyrobbrt.urg.generator.GeneratorBlockParser;
import com.matyrobbrt.urg.generator.misc.GeneratorInfo;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class URGGeneratorsReloadListener extends JsonReloadListener {

	public static URGGeneratorsReloadListener INSTANCE = new URGGeneratorsReloadListener();

	public Map<ResourceLocation, GeneratorInfo> generatorInfos = new HashMap<>();

	public URGGeneratorsReloadListener() {
		super((new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create(), "urg_generators");
	}

	private final Map<ResourceLocation, GeneratorBlock> generators = new HashMap<>();

	public boolean registered = false;

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> pObject, IResourceManager pResourceManager,
			IProfiler pProfiler) {
		update(pObject);
		if (!registered) {
			pObject.forEach(
					(rl, json) -> generators.put(rl, GeneratorBlockParser.generatorFromJson((JsonObject) json, rl)));
		}
	}

	public void update(Map<ResourceLocation, JsonElement> objects) {
		UltimateResourceGenerators.LOGGER.info("Started updating generators information...");
		objects.forEach((rl, obj) -> generatorInfos.put(rl, GeneratorBlockParser.infoFromJson((JsonObject) obj)));
		generatorInfos.forEach((rl, info) -> {
			info.redirect(URGPackFinder.getRedirectForName(rl));
			if (!info.copyFrom.isEmpty()) {
				info.redirect(getInfoForRL(new ResourceLocation(info.copyFrom)));
			}
		});
		UltimateResourceGenerators.LOGGER.info("Done updating generators information!");
	}

	public GeneratorInfo getInfoForRL(ResourceLocation rl) {
		return generatorInfos.computeIfAbsent(rl, newRL -> new GeneratorInfo());
	}

	public void forEachGenerator(BiConsumer<? super ResourceLocation, ? super GeneratorBlock> consumer) {
		generators.forEach(consumer);
	}

	public Stream<Entry<ResourceLocation, GeneratorBlock>> streamGenerators() {
		return generators.entrySet().stream();
	}
}
