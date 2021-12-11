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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;

public class URGResourceManager {

	private static URGResourceManager instance = new URGResourceManager();

	public static URGResourceManager instance() {
		return instance;
	}

	private final SimpleReloadableResourceManager resourceManager;
	private final URGPackFinder packFinder;
	private final ResourcePackList packList;

	private URGResourceManager() {
		resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
		packFinder = URGPackFinder.FINDER;
		packList = new ResourcePackList(packFinder);
	}

	public IPackFinder getWrappedPackFinder() {
		return (infoConsumer, infoFactory) -> packFinder.loadPacks(infoConsumer::accept,
				(a, n, b, c, d, e, f) -> infoFactory.create("urg:" + a, true, b, c, d, e, f));
	}

	/**
	 * Call during mod construction **without enqueueWork**!
	 */
	public synchronized void addPackFinder(IPackFinder finder) {
		packList.addPackFinder(finder);
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
