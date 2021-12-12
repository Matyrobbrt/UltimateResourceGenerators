package com.matyrobbrt.urg.generator.misc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matyrobbrt.urg.registries.URGRegistries;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ToolType;

public class BlockProperties {

	/**
	 * @deprecated Use {@link #getMaterial()}
	 */
	@Deprecated
	@Expose
	public String material = "heavy_metal";

	@Expose
	public float strength = 1.0f;

	/**
	 * @deprecated Use {@link #getHarvestTool()}
	 */
	@Deprecated
	@Expose
	@SerializedName("harvest_tool")
	public String harvestTool = "pickaxe";

	@Expose
	@SerializedName("harvest_level")
	public int harvestLevel = 1;

	@Expose
	@SerializedName("requires_correct_tool_for_drops")
	public boolean requiresCorrectToolForDrops = false;

	@Expose
	@SerializedName("no_occlusion")
	public boolean noOcclusion = true;

	@Expose
	@SerializedName("no_collission")
	public boolean noCollission = false;

	@Expose
	@SerializedName("light_level")
	public int lightLevel = 0;

	public Material getMaterial() { return URGRegistries.BLOCK_MATERIALS.get(new ResourceLocation(material)); }

	public ToolType getHarvestTool() { return ToolType.get(harvestTool); }

	public void applyProperties(Properties properties) {
		properties.strength(strength);
		properties.harvestTool(getHarvestTool());
		properties.harvestLevel(harvestLevel);
		if (requiresCorrectToolForDrops) {
			properties.requiresCorrectToolForDrops();
		}
		if (noOcclusion) {
			properties.noOcclusion();
		}
		if (noCollission) {
			properties.noCollission();
		}
		properties.lightLevel(state -> lightLevel);
	}
}
