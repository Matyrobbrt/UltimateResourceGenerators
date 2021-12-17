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

package com.matyrobbrt.urg.client;

import com.matyrobbrt.urg.generator.GeneratorBlock;
import com.matyrobbrt.urg.generator.GeneratorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultGeneratorTER extends TileEntityRenderer<GeneratorTileEntity> {

	public static final int MAX_LIGHT_LEVEL = 15728640;

	private static final Minecraft mc = Minecraft.getInstance();

	public DefaultGeneratorTER(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(GeneratorTileEntity generator, float pPartialTicks, MatrixStack pMatrixStack,
			IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay) {
		GeneratorBlock generatorBlock = generator.getLevel().getBlockState(generator.getBlockPos())
				.getBlock() instanceof GeneratorBlock
						? (GeneratorBlock) generator.getLevel().getBlockState(generator.getBlockPos()).getBlock()
						: null;
		if (generatorBlock == null
				|| (generatorBlock.getProducedItem() == null || generatorBlock.getProducedItem() == Items.AIR)
				|| !generatorBlock.getTileRenderInfo().usesDefaultRender) {
			return;
		}

		generator.renderDegrees += generatorBlock.getTileRenderInfo().rotationSpeed;
		if (generator.renderDegrees > 360f) {
			generator.renderDegrees = 0f;
		}

		renderItem(new ItemStack(generatorBlock.getProducedItem(), generatorBlock.getProducedPerOperation()),
				generatorBlock.getTileRenderInfo().translation, Vector3f.YP.rotationDegrees(generator.renderDegrees),
				pMatrixStack, pBuffer, pCombinedOverlay, MAX_LIGHT_LEVEL, generatorBlock.getTileRenderInfo().scale);
	}

	public static void renderItem(ItemStack stack, double[] translation, Quaternion rotation, MatrixStack matrixStack,
			IRenderTypeBuffer buffer, int combinedOverlay, int lightLevel, float scale) {
		matrixStack.pushPose();
		matrixStack.translate(translation[0], translation[1], translation[2]);
		matrixStack.mulPose(rotation);
		matrixStack.scale(scale, scale, scale);

		IBakedModel model = mc.getItemRenderer().getModel(stack, null, null);
		mc.getItemRenderer().render(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStack, buffer,
				lightLevel, combinedOverlay, model);
		matrixStack.popPose();
	}

}
