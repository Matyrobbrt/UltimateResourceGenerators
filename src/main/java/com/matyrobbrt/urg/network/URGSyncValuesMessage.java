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

package com.matyrobbrt.urg.network;

import com.matyrobbrt.lib.annotation.SyncValue;
import com.matyrobbrt.lib.network.BaseNetwork;
import com.matyrobbrt.lib.network.INetworkMessage;
import com.matyrobbrt.lib.tile_entity.BaseTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class URGSyncValuesMessage implements INetworkMessage {

	public final BlockPos pos;
	public final CompoundNBT nbt;

	public URGSyncValuesMessage(BlockPos pos, BaseTileEntity te) {
		this.pos = pos;
		this.nbt = SyncValue.Helper.writeSyncValues(te.getSyncFields(), te, te.save(new CompoundNBT()),
				SyncValue.SyncType.PACKET);
	}

	public URGSyncValuesMessage(BlockPos pos, CompoundNBT nbt) {
		this.pos = pos;
		this.nbt = nbt;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeNbt(nbt);
	}

	@Override
	public void handle(Context context) {
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(this, context)));
	}

	@SuppressWarnings("resource")
	private static void handleClient(URGSyncValuesMessage syncValuesMessage, Context context) {
		ClientWorld client = Minecraft.getInstance().level;
		if (client == null) { return; }
		TileEntity tile = client.getBlockEntity(syncValuesMessage.pos);
		if (tile != null && client.isClientSide()) {
			tile.load(client.getBlockState(syncValuesMessage.pos), syncValuesMessage.nbt);
		}
	}

	public static URGSyncValuesMessage decode(PacketBuffer buffer) {
		return new URGSyncValuesMessage(buffer.readBlockPos(), buffer.readNbt());
	}

	public static void send(BaseTileEntity tile) {
		BaseNetwork.sendToAllTracking(URGNetwork.CHANNEL, new URGSyncValuesMessage(tile.getBlockPos(), tile), tile);
	}

}
