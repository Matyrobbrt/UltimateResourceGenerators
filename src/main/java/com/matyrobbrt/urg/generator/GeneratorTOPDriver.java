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

import com.matyrobbrt.lib.compat.top.ITOPDriver;
import com.matyrobbrt.lib.util.ColourCodes;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;

public class GeneratorTOPDriver implements ITOPDriver {

	private final GeneratorBlock generator;

	public GeneratorTOPDriver(GeneratorBlock generator) {
		this.generator = generator;
	}

	@Override
	public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, PlayerEntity player, World level,
			BlockState blockState, IProbeHitData probeData) {
		GeneratorTileEntity tile = (GeneratorTileEntity) level.getBlockEntity(probeData.getPos());
		probeInfo.text("Produces " + ColourCodes.LIGHT_PURPLE
				+ generator.producedItem.getName(new ItemStack(generator.producedItem, generator.producedPerOperation))
						.getString()
				+ " x" + generator.producedPerOperation + ColourCodes.WHITE + " once every "
				+ generator.ticksPerOperation + " ticks");
		probeInfo.text("Ticks until next production: " + (generator.ticksPerOperation - tile.ticksSinceLastProduction));

		if (generator.maxProduced != -1) {
			probeInfo.text(
					"Can produce " + ColourCodes.GOLD + generator.maxProduced + ColourCodes.WHITE + " total items");
			probeInfo.text("Items remaining to produce: " + (generator.maxProduced - tile.alreadyProduced));
		}

		if (generator.feInfo != null && generator.feInfo.uses_forge_energy) {
			probeInfo.text("Uses " + ColourCodes.DARK_GREEN + generator.feInfo.fe_used_per_tick + "FE"
					+ ColourCodes.WHITE + " / tick");
		}

		if (!tile.inventory.getStackInSlot(0).isEmpty()) {
			probeInfo.item(tile.inventory.getStackInSlot(0));
		}

	}

}
