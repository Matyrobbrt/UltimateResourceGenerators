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

package com.matyrobbrt.urg.generator;

import java.util.List;

import com.google.common.collect.Lists;
import com.matyrobbrt.lib.compat.top.ITOPDriver;
import com.matyrobbrt.lib.compat.top.ITOPInfoProvider;
import com.matyrobbrt.lib.util.ColourCodes;
import com.matyrobbrt.lib.wrench.DefaultWrenchBehaviours;
import com.matyrobbrt.lib.wrench.IWrenchBehaviour;
import com.matyrobbrt.lib.wrench.IWrenchUsable;
import com.matyrobbrt.urg.generator.misc.GeneratorInfo;
import com.matyrobbrt.urg.generator.misc.RenderInfo;
import com.matyrobbrt.urg.packs.URGGeneratorsReloadListener;
import com.matyrobbrt.urg.util.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GeneratorBlock extends Block implements ITOPInfoProvider, IWrenchUsable {

	ResourceLocation infoLocation;

	public void copy(GeneratorBlock other) {
		infoLocation = other.infoLocation;
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
		TileEntity tile = pBuilder.getOptionalParameter(LootParameters.BLOCK_ENTITY) != null
				? pBuilder.getParameter(LootParameters.BLOCK_ENTITY)
				: pBuilder.getLevel().getBlockEntity(new BlockPos(pBuilder.getParameter(LootParameters.ORIGIN)));
		if (tile instanceof GeneratorTileEntity) {
			GeneratorTileEntity generatorTile = (GeneratorTileEntity) tile;
			ItemStack stack = new ItemStack(asItem());
			generatorTile.saveToNBT(stack.getOrCreateTag());
			drops.add(stack);
			if (!getInfo().keepInventory) {
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
				+ getInfo().getProducedItem()
						.getName(new ItemStack(getInfo().getProducedItem(), getInfo().producedPerOperation)).getString()
				+ " x" + getInfo().producedPerOperation + ColourCodes.WHITE + " once every "
				+ getInfo().ticksPerOperation + " ticks"));

		com.matyrobbrt.urg.util.Utils.appendShiftTooltip(pTooltip, Utils.conditionedList(list -> {
			if (getInfo().feInfo != null && getInfo().feInfo.usesFE) {
				list.add(new StringTextComponent(
						Utils.fromLang("tooltip.urg.fe_usage", getInfo().feInfo.feUsedPerTick + "")));
			}
			if (getInfo().maxProduced != -1) {
				list.add(new StringTextComponent(
						Utils.fromLang("tooltip.urg.max_produced", getInfo().maxProduced + "")));
			} else {
				list.add(new TranslationTextComponent("tooltip.urg.max_produced.infinite"));
			}
			if (getInfo().autoOutput) {
				list.add(new TranslationTextComponent("tooltip.urg.auto_output"));
			}
		}));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, IBlockReader pReader, BlockPos pPos) {
		return getInfo().propagatesSkyLight;
	}

	public GeneratorInfo getInfo() { return URGGeneratorsReloadListener.INSTANCE.getInfoForRL(infoLocation); }

	public Item getProducedItem() { return getInfo().getProducedItem(); }

	public int getProducedPerOperation() { return getInfo().producedPerOperation; }

	public RenderInfo getTileRenderInfo() { return getInfo().tileRenderInfo; }

	@Override
	public ITOPDriver getTheOneProbeDriver() { return new GeneratorTOPDriver(this); }

	@Override
	public IWrenchBehaviour getBehaviour() { return DefaultWrenchBehaviours.normalDismantle(this); }

}
