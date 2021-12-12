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

import java.util.List;

import com.google.common.collect.Lists;
import com.matyrobbrt.lib.compat.top.ITOPDriver;
import com.matyrobbrt.lib.compat.top.ITOPInfoProvider;
import com.matyrobbrt.lib.util.ColourCodes;
import com.matyrobbrt.urg.generator.misc.BlockItemInfo;
import com.matyrobbrt.urg.generator.misc.FEInfo;
import com.matyrobbrt.urg.generator.misc.RenderInfo;
import com.matyrobbrt.urg.util.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GeneratorBlock extends Block implements ITOPInfoProvider {

	RenderType renderType = RenderType.cutout();

	Item producedItem = Items.AIR;
	int producedPerOperation = 1;
	int maxProduced = -1;

	int ticksPerOperation = 200;

	boolean keepInventory = true;
	boolean autoOutput = true;

	FEInfo feInfo = defaultFeInfo();

	BlockItemInfo blockItemInfo = new BlockItemInfo();

	boolean propagatesSkyLight = true;

	RenderInfo tileRenderInfo = new RenderInfo();

	public void copy(GeneratorBlock other) {
		producedItem = other.producedItem;
		producedPerOperation = other.producedPerOperation;
		maxProduced = other.maxProduced;

		ticksPerOperation = other.ticksPerOperation;
		feInfo = other.feInfo;
		blockItemInfo = other.blockItemInfo;
		propagatesSkyLight = other.propagatesSkyLight;
		tileRenderInfo = other.tileRenderInfo;
		autoOutput = other.autoOutput;
	}

	public static FEInfo defaultFeInfo() {
		FEInfo info = new FEInfo();
		info.usesFE = false;
		info.feUsedPerTick = 0;
		info.feCapacity = 0;
		info.feTransferRate = 0;
		return info;
	}

	public GeneratorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new GeneratorTileEntity(this);
	}

	@Override
	public void setPlacedBy(World pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pLevel.getBlockEntity(pPos) instanceof GeneratorTileEntity) {
			((GeneratorTileEntity) pLevel.getBlockEntity(pPos)).generatorBlock = this;
			((GeneratorTileEntity) pLevel.getBlockEntity(pPos)).load(pStack.getOrCreateTag());
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState, Builder pBuilder) {
		List<ItemStack> drops = Lists.newArrayList();
		TileEntity tile = pBuilder.getParameter(LootParameters.BLOCK_ENTITY);
		if (tile instanceof GeneratorTileEntity) {
			GeneratorTileEntity generatorTile = (GeneratorTileEntity) tile;
			ItemStack stack = new ItemStack(asItem());
			generatorTile.saveToNBT(stack.getOrCreateTag());
			drops.add(stack);
			if (!keepInventory) {
				for (int i = 0; i < generatorTile.inventory.getSlots(); i++) {
					drops.add(generatorTile.inventory.getStackInSlot(i));
				}
			}
		}
		return drops;
	}

	@Override
	public void appendHoverText(ItemStack pStack, IBlockReader pLevel, List<ITextComponent> pTooltip,
			ITooltipFlag pFlag) {
		pTooltip.add(new StringTextComponent("Produces " + ColourCodes.LIGHT_PURPLE
				+ producedItem.getName(new ItemStack(producedItem, producedPerOperation)).getString() + " x"
				+ producedPerOperation + ColourCodes.WHITE + " once every " + ticksPerOperation + " ticks"));

		com.matyrobbrt.urg.util.Utils.appendShiftTooltip(pTooltip, Utils.conditionedList(list -> {
			if (feInfo != null && feInfo.usesFE) {
				list.add(new StringTextComponent(Utils.fromLang("tooltip.urg.fe_usage", feInfo.feUsedPerTick + "")));
			}
			if (maxProduced != -1) {
				list.add(new StringTextComponent(Utils.fromLang("tooltip.urg.max_produced", maxProduced + "")));
			} else {
				list.add(new TranslationTextComponent("tooltip.urg.max_produced.infinite"));
			}
			if (autoOutput) {
				list.add(new TranslationTextComponent("tooltip.urg.auto_output"));
			}
		}));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, IBlockReader pReader, BlockPos pPos) {
		return propagatesSkyLight;
	}

	public Item getProducedItem() { return producedItem; }

	public int getProducedPerOperation() { return producedPerOperation; }

	public RenderInfo getTileRenderInfo() { return tileRenderInfo; }

	@Override
	public ITOPDriver getTheOneProbeDriver() { return new GeneratorTOPDriver(this); }

}
