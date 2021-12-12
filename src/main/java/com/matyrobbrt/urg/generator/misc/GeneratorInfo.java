package com.matyrobbrt.urg.generator.misc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matyrobbrt.urg.registries.URGRegistries;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistries;

public class GeneratorInfo {

	@Expose
	@SerializedName("block_properties")
	public BlockProperties blockProperties = new BlockProperties();

	/**
	 * @deprecated Use {@link #getRenderType()}
	 */
	@Expose
	@SerializedName("render_type")
	@Deprecated
	public String renderType = "cutout";

	/**
	 * @deprecated Use {@link #getProducedItem()}
	 */
	@Expose
	@SerializedName("produced_item")
	@Deprecated
	public String producedItem = "air";

	@Expose
	@SerializedName("produced_per_operation")
	public int producedPerOperation = 1;

	@Expose
	@SerializedName("max_produced")
	public int maxProduced = -1;

	@Expose
	@SerializedName("ticks_per_operation")
	public int ticksPerOperation = 200;

	@Expose
	@SerializedName("keep_inventory")
	public boolean keepInventory = true;

	@Expose
	@SerializedName("auto_output")
	public boolean autoOutput = true;

	@Expose
	@SerializedName("fe_info")
	public FEInfo feInfo = new FEInfo();

	@Expose
	@SerializedName("block_item")
	public BlockItemInfo blockItemInfo = new BlockItemInfo();

	@Expose
	@SerializedName("propagates_sky_light")
	public boolean propagatesSkyLight = true;

	@Expose
	@SerializedName("tile_entity_renderer")
	public RenderInfo tileRenderInfo = new RenderInfo();

	public Item getProducedItem() { return ForgeRegistries.ITEMS.getValue(new ResourceLocation(producedItem)); }

	public RenderType getRenderType() { return URGRegistries.RENDER_TYPES.get(new ResourceLocation(renderType)); }
}
