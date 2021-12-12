package com.matyrobbrt.urg.network;

import com.matyrobbrt.lib.network.BaseNetwork;
import com.matyrobbrt.urg.UltimateResourceGenerators;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class URGNetwork extends BaseNetwork {

	public static final String NETWORK_VERSION = "0.1.0";

	public static final SimpleChannel CHANNEL = newSimpleChannel("channel");

	public static void register() {
		registerServerToClient(CHANNEL, URGSyncValuesMessage.class, URGSyncValuesMessage::decode);
	}

	private static SimpleChannel newSimpleChannel(String name) {
		return NetworkRegistry.newSimpleChannel(new ResourceLocation(name, UltimateResourceGenerators.MOD_ID),
				() -> NETWORK_VERSION, version -> version.equals(NETWORK_VERSION),
				version -> version.equals(NETWORK_VERSION));
	}

}
