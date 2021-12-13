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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.matyrobbrt.lib.util.helper.TernaryHelper;

import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class URGResourceManager {

	private static URGResourceManager instance = new URGResourceManager();

	public static URGResourceManager instance() {
		return instance;
	}

	private final SimpleReloadableResourceManager resourceManager;
	private final URGPackFinder mainPackFinder;
	private final ResourcePackList packList;

	private URGResourceManager() {
		resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
		mainPackFinder = URGPackFinder.FINDER;
		packList = new ResourcePackList(mainPackFinder);

		if (URGPackFinder.DEV_ENVIRONMENT.getLoaderDirectory() != URGPackFinder.FINDER.getLoaderDirectory()
				&& !FMLEnvironment.production) {
			addPackFinder(URGPackFinder.DEV_ENVIRONMENT);
		}
	}

	public IPackFinder getWrappedPackFinder() {
		return (infoConsumer, infoFactory) -> mainPackFinder.loadPacks(infoConsumer::accept,
				(a, n, b, c, d, e, f) -> infoFactory.create("urg:" + a, true, b, c, d, e, f));
	}

	/**
	 * Call during mod construction **without enqueueWork**!
	 */
	public synchronized void addPackFinder(IPackFinder finder) {
		if (finder != mainPackFinder && Boolean.TRUE.equals(TernaryHelper.supplier(() -> {
			if (finder instanceof URGPackFinder) {
				return ((URGPackFinder) finder).getLoaderDirectory() != mainPackFinder.getLoaderDirectory();
			}
			return true;
		}))) {
			packList.addPackFinder(finder);
		}
	}

	/**
	 * Call during mod construction **without enqueueWork**!
	 */
	public synchronized void addResourceReloadListener(IFutureReloadListener listener) {
		resourceManager.registerReloadListener(listener);
	}

	public CompletableFuture<URGResourceManager> beginLoading(Executor backgroundExecutor, Executor gameExecutor) {
		packList.reload();

		return resourceManager.reload(backgroundExecutor, gameExecutor, packList.openAllSelected(),
				CompletableFuture.completedFuture(Unit.INSTANCE)).whenComplete((unit, throwable) -> {
					if (throwable != null) {
						resourceManager.close();
					}
				}).thenApply(unit -> this);
	}

	public ResourcePackList getRepository() { return packList; }

	public void finishLoading() {
		//
	}

}
