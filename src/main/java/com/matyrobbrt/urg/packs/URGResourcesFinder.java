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

package com.matyrobbrt.urg.packs;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.matyrobbrt.urg.UltimateResourceGenerators;

import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;

import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;

public class URGResourcesFinder {

	public static ResourcePackLoader.IPackInfoFinder buildPackFinder(
			Map<ModFile, ? extends ModFileResourcePack> modResourcePacks,
			BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter) {
		return (packList, factory) -> serverPackFinder(modResourcePacks, packSetter, packList, factory);
	}

	private static void serverPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks,
			BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter, Consumer<ResourcePackInfo> consumer,
			ResourcePackInfo.IFactory factory) {
		for (Map.Entry<ModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet()) {
			IModInfo mod = e.getKey().getModInfos().get(0);
			if (Objects.equals(mod.getModId(), "minecraft")) {
				continue; // minecraft
			}
			final String name = "mod:" + mod.getModId();
			final ResourcePackInfo packInfo = ResourcePackInfo.create(name, false, e::getValue, factory,
					ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.DEFAULT);
			if (packInfo == null) {
				ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR,
						"fml.modloading.brokenresources", e.getKey()));
				continue;
			}
			packSetter.accept(e.getValue(), packInfo);
			UltimateResourceGenerators.LOGGER.debug("Generating PackInfo named {} for mod file {}", name,
					e.getKey().getFilePath());
			consumer.accept(packInfo);
		}
	}
}
