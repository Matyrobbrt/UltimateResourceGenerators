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

import com.matyrobbrt.lib.annotation.SyncValue;
import com.matyrobbrt.urg.client.DefaultGeneratorTER;
import com.matyrobbrt.urg.generator.misc.FEInfo;
import com.matyrobbrt.urg.generator.misc.URGEnergyStorage;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Sync values not working yet, some weird thing with BaseTileEntity i need to
 * fix
 *
 */

/**
 * @author matyrobbrt
 */
public class GeneratorTileEntity extends TileEntity implements ITickableTileEntity {

	/**
	 * Only used client-side in {@link DefaultGeneratorTER}
	 */
	public float renderDegrees;

	private Item producedItem;
	private int producedPerOperation = 1;
	private int maxProduced = -1;

	private int ticksPerOperation = 200;

	public int alreadyProduced;
	public int ticksSinceLastProduction;

	private FEInfo feInfo = new FEInfo();

	@SyncValue(name = "invSync", onPacket = true)
	public final ItemHandler inventory = new ItemHandler(1) {

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			GeneratorTileEntity.this.setChanged();
			// sync(com.matyrobbrt.lib.network.matylib.SyncValuesMessage.Direction.SERVER_TO_CLIENT);
		}
	};

	@SyncValue(name = "energySync", onPacket = true)
	public URGEnergyStorage energyStorage = createEnergyStorage();

	public GeneratorBlock generatorBlock;

	private final LazyOptional<IItemHandler> itemHandlerOptional = LazyOptional.of(() -> inventory);
	private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

	public GeneratorTileEntity() {
		super(GeneratorModule.GENERATOR_TILE_ENTITY_TYPE);
	}

	private URGEnergyStorage createEnergyStorage() {
		return new URGEnergyStorage(feInfo.feCapacity, feInfo.feTransferRate, 0) {

			@Override
			public void setChanged() {
				super.setChanged();
				GeneratorTileEntity.this.setChanged();
				// sync(com.matyrobbrt.lib.network.matylib.SyncValuesMessage.Direction.SERVER_TO_CLIENT);
			}
		};
	}

	public GeneratorTileEntity(GeneratorBlock block) {
		this();
		fromBlock(block);
	}

	public void fromBlock(GeneratorBlock block) {
		if (block == null) { return; }
		producedItem = block.getInfo().getProducedItem();
		producedPerOperation = block.getInfo().producedPerOperation;
		maxProduced = block.getInfo().maxProduced;

		ticksPerOperation = block.getInfo().ticksPerOperation;
		if (block.getInfo().feInfo != null) {
			feInfo = block.getInfo().feInfo;
		}
		energyStorage = createEnergyStorage();
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		saveToNBT(nbt);
		return nbt;
	}

	public void saveToNBT(CompoundNBT nbt) {
		nbt.putInt("ticksSinceLastProduction", ticksSinceLastProduction);
		if (generatorBlock == null || generatorBlock.getInfo().keepInventory) {
			nbt.put("inventory", inventory.serializeNBT());
		}
		nbt.putInt("alreadyProduced", alreadyProduced);
		if (generatorBlock != null) {
			nbt.putString("ownerBlock", generatorBlock.getRegistryName().toString());
		}

		nbt.put("energy", energyStorage.serialize(new CompoundNBT()));
	}

	@Override
	public void load(BlockState p_230337_1_, CompoundNBT nbt) {
		super.load(p_230337_1_, nbt);
		load(nbt);
		fromBlock(generatorBlock);
	}

	public void load(CompoundNBT nbt) {
		if (nbt.contains("ownerBlock")) {
			generatorBlock = ForgeRegistries.BLOCKS
					.getValue(new ResourceLocation(nbt.getString("ownerBlock"))) instanceof GeneratorBlock
							? (GeneratorBlock) ForgeRegistries.BLOCKS
									.getValue(new ResourceLocation(nbt.getString("ownerBlock")))
							: null;
		}
		if (nbt.contains("ticksSinceLastProduction")) {
			ticksSinceLastProduction = nbt.getInt("ticksSinceLastProduction");
		}
		if (nbt.contains("inventory") && (generatorBlock == null || generatorBlock.getInfo().keepInventory)) {
			inventory.deserializeNBT(nbt.getCompound("inventory"));
		}
		if (nbt.contains("alreadyProduced")) {
			alreadyProduced = nbt.getInt("alreadyProduced");
		}
		if (nbt.contains("energy")) {
			energyStorage.deserialize(nbt.getCompound("energy"));
		}
	}

	@Override
	public void tick() {
		if (!level.isClientSide()) {
			serverTick();
		}
	}

	public void serverTick() {
		if (!autoOutputs()) {
			handleAutoOutput(Direction.UP);
		}

		if (alreadyProduced >= maxProduced && maxProduced != -1) { return; }

		if (ticksSinceLastProduction >= ticksPerOperation) {
			int produceCount = Math.min(producedPerOperation,
					maxProduced != -1 ? maxProduced - alreadyProduced : producedPerOperation);
			ItemStack toInsert = new ItemStack(producedItem, produceCount);
			if (inventory.insertInternal(0, toInsert, true) != toInsert) {
				inventory.insertInternal(0, toInsert, false);
				ticksSinceLastProduction = 0;
				alreadyProduced += produceCount;
			}
		} else {
			if (feInfo.usesFE) {
				if (energyStorage.getEnergyStored() >= feInfo.feUsedPerTick) {
					energyStorage.extractEnergyInternal(feInfo.feUsedPerTick);
					ticksSinceLastProduction++;
				}
			} else {
				ticksSinceLastProduction++;
			}
		}
		setChanged();
	}

	private void handleAutoOutput(Direction direction) {
		TileEntity tile = level.getBlockEntity(worldPosition.relative(direction));
		if (tile != null) {
			tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite())
					.ifPresent(handler -> {
						if (inventory.getStackInSlot(0).isEmpty()) { return; }
						for (int slot = 0; slot < handler.getSlots(); slot++) {
							if (handler.insertItem(slot, inventory.getStackInSlot(0), true) != inventory
									.getStackInSlot(0)) {
								inventory.setStackInSlot(0,
										handler.insertItem(slot, inventory.getStackInSlot(0), false));
								setChanged();
							}
						}
					});
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return itemHandlerOptional.cast(); }
		if (cap == CapabilityEnergy.ENERGY && feInfo.usesFE) { return energyOptional.cast(); }
		return super.getCapability(cap, side);
	}

	public boolean autoOutputs() {
		return generatorBlock == null || generatorBlock.getInfo().autoOutput;
	}

	/*
	 * @Override public void
	 * sync(com.matyrobbrt.lib.network.matylib.SyncValuesMessage.Direction
	 * direction) { URGSyncValuesMessage.send(this); }
	 */

	public static class ItemHandler extends ItemStackHandler {

		public ItemHandler(int size) {
			super(size);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return stack;
		}

		public ItemStack insertInternal(int slot, ItemStack stack, boolean simulate) {
			if (stack.isEmpty()) { return ItemStack.EMPTY; }

			if (!isItemValid(slot, stack)) { return stack; }

			validateSlotIndex(slot);

			ItemStack existing = stacks.get(slot);

			int limit = getStackLimit(slot, stack);

			if (!existing.isEmpty()) {
				if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) { return stack; }

				limit -= existing.getCount();
			}

			if (limit <= 0) { return stack; }

			boolean reachedLimit = stack.getCount() > limit;

			if (!simulate) {
				if (existing.isEmpty()) {
					stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				} else {
					existing.grow(reachedLimit ? limit : stack.getCount());
				}
				onContentsChanged(slot);
			}

			return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit)
					: ItemStack.EMPTY;
		}

	}

}
