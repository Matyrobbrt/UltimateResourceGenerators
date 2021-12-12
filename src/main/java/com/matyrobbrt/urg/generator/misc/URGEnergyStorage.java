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

package com.matyrobbrt.urg.generator.misc;

import net.minecraft.nbt.CompoundNBT;

import net.minecraftforge.energy.EnergyStorage;

public class URGEnergyStorage extends EnergyStorage {

	public URGEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
	}

	public URGEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
		super(capacity, maxReceive, maxExtract, energy);
	}

	public CompoundNBT serialize(CompoundNBT nbt) {
		nbt.putInt("energy", getEnergyStored());
		nbt.putInt("maxExtract", maxExtract);
		nbt.putInt("maxReceive", maxReceive);
		nbt.putInt("capacity", capacity);
		return nbt;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int toReturn = super.receiveEnergy(maxReceive, simulate);
		setChanged();
		return toReturn;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int toReturn = super.extractEnergy(maxExtract, simulate);
		setChanged();
		return toReturn;
	}

	public void extractEnergyInternal(int amount) {
		energy -= amount;
		setChanged();
	}

	public static URGEnergyStorage fromNbt(CompoundNBT nbt) {
		return new URGEnergyStorage(nbt.getInt("capacity"), nbt.getInt("maxReceive"), nbt.getInt("maxExtract"),
				nbt.getInt("energy"));
	}

	public void deserialize(CompoundNBT nbt) {
		energy = nbt.getInt("energy");
		maxExtract = nbt.contains("maxExtract") ? nbt.getInt("maxExtract") : maxExtract;
		maxReceive = nbt.contains("maxReceive") ? nbt.getInt("maxReceive") : maxReceive;
		capacity = nbt.contains("capacity") ? nbt.getInt("capacity") : capacity;
	}

	public void setChanged() {

	}

}
