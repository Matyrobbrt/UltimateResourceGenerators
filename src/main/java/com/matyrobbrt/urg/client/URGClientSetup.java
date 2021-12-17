package com.matyrobbrt.urg.client;

import com.matyrobbrt.lib.ClientSetup;
import com.matyrobbrt.urg.UltimateResourceGenerators;
import com.matyrobbrt.urg.generator.misc.GeneratorInfo;
import com.matyrobbrt.urg.packs.URGGeneratorsReloadListener;
import com.matyrobbrt.urg.registries.URGClientRegistries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class URGClientSetup extends ClientSetup {

	public URGClientSetup(IEventBus modBus) {
		super(modBus);
		if (Minecraft.getInstance() != null) {
			ResourcePackList packs = Minecraft.getInstance().getResourcePackRepository();
			UltimateResourceGenerators.addPacks(packs);
		}
	}

	@Override
	public void onClientSetup(FMLClientSetupEvent event) {
		try {
			URGGeneratorsReloadListener.INSTANCE.forEachGenerator((rl, generator) -> {
				if (URGClientSetup.getRenderType(generator.getInfo()) != null) {
					RenderTypeLookup.setRenderLayer(generator, URGClientSetup.getRenderType(generator.getInfo()));
				}
			});
		} catch (IllegalArgumentException e) {
			UltimateResourceGenerators.LOGGER.info("Caught exception while trying to set render layers!", e);
		}
	}

	@SuppressWarnings("deprecation")
	public static RenderType getRenderType(GeneratorInfo info) {
		return URGClientRegistries.RENDER_TYPES.get(new ResourceLocation(info.renderType));
	}
}
