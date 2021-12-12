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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.matyrobbrt.urg.UltimateResourceGenerators;

import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;

import net.minecraftforge.fml.loading.FMLPaths;

public class URGPackFinder implements IPackFinder {

	public static final URGPackFinder FINDER = new URGPackFinder(
			FMLPaths.GAMEDIR.get().resolve("urg_packs").toFile());

	private final File loaderDirectory;

	private URGPackFinder(File loaderDirectory) {
		this.loaderDirectory = loaderDirectory;

		try {
			Files.createDirectories(loaderDirectory.toPath());
		} catch (final IOException e) {
			UltimateResourceGenerators.LOGGER.error("Failed to initialize loader.", e);
		}
	}

	@Override
	public void loadPacks(Consumer<ResourcePackInfo> packs, IFactory factory) {
		for (final File packCandidate : getFilesFromDir(loaderDirectory)) {
			final boolean isFilePack = packCandidate.isFile() && packCandidate.getName().endsWith(".zip");
			final boolean isFolderPack = !isFilePack && packCandidate.isDirectory()
					&& new File(packCandidate, "pack.mcmeta").isFile();

			if (isFilePack || isFolderPack) {
				final String packName = "urg_packs/" + packCandidate.getName();

				UltimateResourceGenerators.LOGGER.info("Loading {}.", packName);
				final ResourcePackInfo packInfo = ResourcePackInfo.create(packName, true,
						getAsPack(packCandidate), factory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILT_IN);

				/* Could be null */
				if (packInfo != null) {
					packs.accept(packInfo);
				}
			} else {
				UltimateResourceGenerators.LOGGER.error(
						"Failed to load from {}. Archive packs must be zips. Folder packs must have a valid pack.mcmeta file.",
						packCandidate.getAbsolutePath());
			}
		}
	}

	private static Supplier<IResourcePack> getAsPack(File file) {
		return file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file);
	}

	private static File[] getFilesFromDir(File file) {
		File[] files = new File[0];

		if (file == null) {
			UltimateResourceGenerators.LOGGER.error("Attempted to read from a null file.");
		} else if (!file.isDirectory()) {
			UltimateResourceGenerators.LOGGER.error("Can not read from {}. It's not a directory.",
					file.getAbsolutePath());
		}

		else {
			try {
				final File[] readFiles = file.listFiles();
				if (readFiles == null) {
					UltimateResourceGenerators.LOGGER.error(
							"Could not read from {} due to a system error. This is likely an issue with your computer.",
							file.getAbsolutePath());
				} else {
					files = readFiles;
				}
			} catch (final SecurityException e) {
				UltimateResourceGenerators.LOGGER.error(
						"Could not read from {}. Blocked by system level security. This is likely an issue with your computer.",
						file.getAbsolutePath(), e);
			}
		}

		return files;
	}

}
