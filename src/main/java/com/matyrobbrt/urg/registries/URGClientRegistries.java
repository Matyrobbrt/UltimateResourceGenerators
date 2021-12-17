package com.matyrobbrt.urg.registries;

import static net.minecraft.util.registry.Registry.register;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;

public class URGClientRegistries {

	public static final RegistryKey<Registry<RenderType>> RENDER_TYPE_REGISTRY = URGRegistries.createKey("render_type");
	public static final Registry<RenderType> RENDER_TYPES = URGRegistries.makeRegistry(RENDER_TYPE_REGISTRY);

	static {
		registerRenderTypes();
	}

	private static void registerRenderTypes() {
		register(RENDER_TYPES, "cutout", RenderType.cutout());
		register(RENDER_TYPES, "cutout_mipped", RenderType.cutoutMipped());
		register(RENDER_TYPES, "solid", RenderType.solid());

		register(RENDER_TYPES, "translucent", RenderType.translucent());
		register(RENDER_TYPES, "translucent_moving_block", RenderType.translucentMovingBlock());
		// register(RENDER_TYPES, "translucent_no_crumbling",
		// RenderType.translucentNoCrumbling());

		register(RENDER_TYPES, "leash", RenderType.leash());
		register(RENDER_TYPES, "water_mask", RenderType.waterMask());

		register(RENDER_TYPES, "armor_glint", RenderType.armorGlint());
		register(RENDER_TYPES, "armor_entity_glint", RenderType.armorEntityGlint());

		register(RENDER_TYPES, "glint", RenderType.glint());
		register(RENDER_TYPES, "glint_translucent", RenderType.glintTranslucent());
		register(RENDER_TYPES, "glint_direct", RenderType.glintDirect());

		register(RENDER_TYPES, "entity_glint_direct", RenderType.entityGlintDirect());
		register(RENDER_TYPES, "entity_glint", RenderType.entityGlint());

		register(RENDER_TYPES, "lighting", RenderType.lightning());
		register(RENDER_TYPES, "tripwire", RenderType.tripwire());
		register(RENDER_TYPES, "lines", RenderType.lines());
	}
}
