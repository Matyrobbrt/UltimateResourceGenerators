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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.matyrobbrt.urg.generator.misc.GeneratorInfo;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.loading.FMLPaths;

public class URGGeneratorProvider implements IDataProvider {

	public final String packName;

	public static final String BLOCKSTATE = "{\r\n" + "	\"variants\": {\r\n"
			+ "		\"\": { \"model\": \"vanilla_urg:block/base_generator\" }\r\n" + "	}\r\n" + "}";

	public static final String ITEM_MODEL = "{\r\n" + "	\"parent\": \"vanilla_urg:block/base_generator\"\r\n" + "}";

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public final DataGenerator dataGenerator;

	protected URGGeneratorProvider(DataGenerator dataGenerator, String packName) {
		this.dataGenerator = dataGenerator;
		this.packName = packName;
	}

	public final Map<ResourceLocation, GeneratorInfo> generatorInfo = new HashMap<>();
	public final List<ResourceLocation> generateModelsGenerators = new LinkedList<>();

	@Override
	public void run(DirectoryCache pCache) throws IOException {
		createInfo();

		generateGenerators(pCache);
	}

	protected void createInfo() {
	}

	private void generateGenerators(DirectoryCache cache) {
		Path outputFolder = Paths.get(FMLPaths.GAMEDIR.get().resolve("../../dev_urg_packs").toFile().getPath());
		generatorInfo.forEach((rl, info) -> {
			Path generatorPath = outputFolder
					.resolve(packName + "/data/" + rl.getNamespace() + "/urg_generators/" + rl.getPath() + ".json");
			try {
				IDataProvider.save(GSON, cache, GSON.toJsonTree(info), generatorPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		generateModelsGenerators.forEach(rl -> {
			Path blockStatePath = outputFolder
					.resolve(packName + "/assets/" + rl.getNamespace() + "/blockstates/" + rl.getPath() + ".json");
			Path itemModelPath = outputFolder
					.resolve(packName + "/assets/" + rl.getNamespace() + "/models/item/" + rl.getPath() + ".json");

			try {
				IDataProvider.save(GSON, cache, GSON.fromJson(BLOCKSTATE, JsonElement.class), blockStatePath);
				IDataProvider.save(GSON, cache, GSON.fromJson(ITEM_MODEL, JsonElement.class), itemModelPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static GeneratorInfo createInfo(Consumer<GeneratorInfo> consumer) {
		GeneratorInfo info = new GeneratorInfo();
		consumer.accept(info);
		return info;
	}

	@Override
	public String getName() { return "URGGeneratorProvider"; }

}
