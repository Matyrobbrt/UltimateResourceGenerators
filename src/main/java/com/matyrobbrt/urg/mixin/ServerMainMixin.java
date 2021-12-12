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

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.Main;

import net.minecraftforge.fml.loading.FMLEnvironment;

@Mixin(Main.class)
public class ServerMainMixin {

	@Redirect(method = "main([Ljava/lang/String;)V", at = @At(value = "NEW", target = "([Lnet/minecraft/resources/IPackFinder;)Lnet/minecraft/resources/ResourcePackList;"))
	private static ResourcePackList redirectPackListCreation(IPackFinder... finders) {
		ResourcePackList list = new ResourcePackList(finders);
		list.addPackFinder(URGPackFinder.FINDER);
		if (!FMLEnvironment.production) {
			list.addPackFinder(URGPackFinder.DEV_ENVIRONMENT);
		}
		return list;
	}

}
