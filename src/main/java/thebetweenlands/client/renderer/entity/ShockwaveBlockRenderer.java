package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.entity.projectile.ShockwaveBlock;

public class ShockwaveBlockRenderer extends EntityRenderer<ShockwaveBlock> {

	private final BlockRenderDispatcher dispatcher;

	public ShockwaveBlockRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.dispatcher = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(ShockwaveBlock entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		if (entity.origin == null || entity.getY() != entity.origin.getY()) {
			if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY() + 0.5D, entity.getZ(),
					(entity.tickCount + partialTick) / 12.0F + 1F,
					10.0f / 255.0f * 4.0F,
					40.0f / 255.0f * 4.0F,
					160.0f / 255.0f * 4.0F));
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY() + 0.5D, entity.getZ(),
					(entity.tickCount + partialTick) / 35.0F + 0.6F,
					-3.4F,
					-3.4F,
					-3.4F));
			}

			RenderSystem.disableBlend();
			Lighting.setupForFlatItems();
			BlockPos startPos = entity.origin == null ? entity.blockPosition() : entity.origin;
			BlockState blockstate = entity.getBlock();
			stack.pushPose();
			stack.translate(-0.5D, 0.0D, -0.5D);
			stack.scale(1.01F, 1.01F, 1.01F);
			var model = this.dispatcher.getBlockModel(blockstate);
			for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(startPos)), ModelData.EMPTY))
				this.dispatcher.getModelRenderer().tesselateBlock(entity.level(), this.dispatcher.getBlockModel(blockstate), blockstate, entity.blockPosition(), stack, buffer.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType)), false, RandomSource.create(), blockstate.getSeed(startPos), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
			stack.popPose();
			Lighting.setupFor3DItems();
			RenderSystem.enableBlend();
		}
		super.render(entity, entityYaw, partialTick, stack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(ShockwaveBlock entity) {
		return null;
	}
}
