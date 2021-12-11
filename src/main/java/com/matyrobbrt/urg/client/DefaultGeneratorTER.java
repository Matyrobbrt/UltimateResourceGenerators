package com.matyrobbrt.urg.client;

import com.matyrobbrt.urg.generator.GeneratorBlock;
import com.matyrobbrt.urg.generator.GeneratorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultGeneratorTER extends TileEntityRenderer<GeneratorTileEntity> {

	private Minecraft mc = Minecraft.getInstance();

	public DefaultGeneratorTER(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	public float rotatedDegrees = 0f;

	@Override
	public void render(GeneratorTileEntity generator, float pPartialTicks, MatrixStack pMatrixStack,
			IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay) {
		GeneratorBlock generatorBlock = generator.getLevel().getBlockState(generator.getBlockPos())
				.getBlock() instanceof GeneratorBlock
						? (GeneratorBlock) generator.getLevel().getBlockState(generator.getBlockPos()).getBlock()
						: null;
		if (generatorBlock == null
				|| (generatorBlock.getProducedItem() == null || generatorBlock.getProducedItem() == Items.AIR)) {
			return;
		}

		rotatedDegrees++;
		if (rotatedDegrees > 360f) {
			rotatedDegrees = 0f;
		}

		int lightLevel = getLightLevel(generator.getLevel(), generator.getBlockPos());

		renderItem(new ItemStack(generatorBlock.getProducedItem(), generatorBlock.getProducedPerOperation()),
				new double[] {
						0.5d, .3d, 0.5d
				}, Vector3f.YP.rotationDegrees(rotatedDegrees), pMatrixStack, pBuffer, pPartialTicks, pCombinedOverlay,
				lightLevel, 0.8f);
	}

	private void renderItem(ItemStack stack, double[] translation, Quaternion rotation, MatrixStack matrixStack,
			IRenderTypeBuffer buffer, float partialTicks, int combinedOverlay, int lightLevel, float scale) {
		matrixStack.pushPose();
		matrixStack.translate(translation[0], translation[1], translation[2]);
		matrixStack.mulPose(rotation);
		matrixStack.scale(scale, scale, scale);

		IBakedModel model = mc.getItemRenderer().getModel(stack, null, null);
		mc.getItemRenderer().render(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStack, buffer,
				lightLevel, combinedOverlay, model);
		matrixStack.popPose();
	}

	private static int getLightLevel(World world, BlockPos pos) {
		int bLight = world.getBrightness(LightType.BLOCK, pos);
		int sLight = world.getBrightness(LightType.SKY, pos);
		return LightTexture.pack(bLight, sLight);
	}

}
