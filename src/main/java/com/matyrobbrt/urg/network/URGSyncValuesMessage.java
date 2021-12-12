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
