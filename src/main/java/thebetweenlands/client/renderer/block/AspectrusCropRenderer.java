package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.block.farming.AspectrusCropBlock;
import thebetweenlands.common.block.farming.DecayableCropBlock;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.function.Function;

public class AspectrusCropRenderer implements BlockEntityRenderer<AspectrusCropBlockEntity> {

	protected static final Function<Integer, ResourceLocation> TEXTURE = i -> TheBetweenlands.prefix("textures/entity/block/aspectrus_crop_" + i + ".png");
	private final ModelPart model1;
	private final ModelPart model2;
	private final ModelPart model3;
	private final ModelPart model3Aspect;
	private final ModelPart model4;
	private final ModelPart model4Aspect;

	public AspectrusCropRenderer(BlockEntityRendererProvider.Context context) {
		this.model1 = context.bakeLayer(BLModelLayers.ASPECTRUS_CROP_1);
		this.model2 = context.bakeLayer(BLModelLayers.ASPECTRUS_CROP_2);
		this.model3 = context.bakeLayer(BLModelLayers.ASPECTRUS_CROP_3);
		this.model3Aspect = context.bakeLayer(BLModelLayers.ASPECTRUS_CROP_3_ASPECT);
		this.model4 = context.bakeLayer(BLModelLayers.ASPECTRUS_CROP_4);
		this.model4Aspect = context.bakeLayer(BLModelLayers.ASPECTRUS_CROP_4_ASPECT);
	}

	@Override
	public void render(AspectrusCropBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		RandomSource rnd = RandomSource.create();
		long seed = entity.getBlockPos().getX() * 0x2FC20FL ^ entity.getBlockPos().getY() * 0x6EBFFF5L ^ entity.getBlockPos().getZ();
		rnd.setSeed(seed * seed * 0x285B825L + seed * 11L);
		int rndRot = rnd.nextInt(4);

		Aspect aspect = entity.getAspect();

		if (entity.getFence() != null) {
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(entity.getFence(), stack, bufferSource, packedLight, packedOverlay);
		} else {
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.RUBBER_TREE_FENCE.get().defaultBlockState(), stack, bufferSource, packedLight, packedOverlay);
		}

		stack.pushPose();
		stack.translate(0.5F, 1.5F, 0.5F);
		stack.scale(1.0F, -1.0F, -1.0F);
		stack.mulPose(Axis.YP.rotationDegrees(rndRot * 90.0F));

		int index = AspectrusCropBlock.getMagicIndex(entity.getBlockState());
		int color = -1;
		if (aspect != null) {
			color = aspect.type().value().color();
			if (index >= 4 && ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();
				float brightness = ((float) Math.sin((entity.glowTicks + partialTick) / 15.0F) * (float) Math.cos((entity.glowTicks + partialTick + 4) / 80.0F) + 1.0F) / 2.0F;
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getBlockPos().getCenter(), brightness * brightness * 4.0F, color));
			}
		}

		VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE.apply(index)));
		switch (index) {
			case 0, 1 -> this.model1.render(stack, consumer, packedLight, packedOverlay);
			case 2, 3 -> this.model2.render(stack, consumer, packedLight, packedOverlay);
			case 4 -> {
				this.model3Aspect.render(stack, consumer, LightTexture.FULL_BRIGHT, packedOverlay, color);
				this.model3.render(stack, consumer, packedLight, packedOverlay);
			}
			case 5 -> {
				this.model4Aspect.render(stack, consumer, LightTexture.FULL_BRIGHT, packedOverlay, color);
				this.model4.render(stack, consumer, packedLight, packedOverlay);
			}
			case 6 -> this.model4.render(stack, consumer, packedLight, packedOverlay);
		}

		stack.popPose();
	}
}