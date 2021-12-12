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

package com.matyrobbrt.urg.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.matyrobbrt.urg.packs.URGPackFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;

import net.minecraftforge.fml.loading.FMLEnvironment;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Redirect(method = "<init>(Lnet/minecraft/client/GameConfiguration;)V", at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourcePackInfo$IFactory;[Lnet/minecraft/resources/IPackFinder;)Lnet/minecraft/resources/ResourcePackList;"))
	public ResourcePackList redirectClientPackListCreation(ResourcePackInfo.IFactory factory, IPackFinder... finders) {
		ResourcePackList list = new ResourcePackList(factory, finders);
		list.addPackFinder(URGPackFinder.FINDER);
		if (!FMLEnvironment.production) {
			list.addPackFinder(URGPackFinder.DEV_ENVIRONMENT);
		}
		return list;
	}

	@Redirect(method = "makeServerStem(Lnet/minecraft/util/registry/DynamicRegistries$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/world/storage/SaveFormat$LevelSave;)Lnet/minecraft/client/Minecraft$PackManager;", at = @At(value = "NEW", target = "([Lnet/minecraft/resources/IPackFinder;)Lnet/minecraft/resources/ResourcePackList;"))
	public ResourcePackList redirectPackListCreation(IPackFinder... finders) {
		ResourcePackList list = new ResourcePackList(finders);
		list.addPackFinder(URGPackFinder.FINDER);
		if (!FMLEnvironment.production) {
			list.addPackFinder(URGPackFinder.DEV_ENVIRONMENT);
		}
		return list;
	}

}
