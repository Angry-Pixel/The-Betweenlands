package thebetweenlands.client.renderer.entity.rowboat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.rowboat.ArmArticulation;
import thebetweenlands.client.model.entity.rowboat.PlayerRowerModel;

public class RowerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerRowerModel> {
	public RowerRenderer(EntityRendererProvider.Context context, boolean slimArms) {
		super(context, new PlayerRowerModel(context.bakeLayer(slimArms ? BLModelLayers.SLIM_PLAYER_ROWER : BLModelLayers.PLAYER_ROWER)), 0.5F);
		//TODO figure this out.
		//currently, the armor sleeves dont render and the helmet does not properly follow the head.
		//I suspect this has to do with the fact that all those pieces are parented to the body, but I dont think I can make the model work any other way
//		this.addLayer(
//			new HumanoidArmorLayer<>(
//				this,
//				new HumanoidRowerModel<>(context.bakeLayer(slimArms ? BLModelLayers.SLIM_PLAYER_ROWER_INNER_ARMOR : BLModelLayers.PLAYER_ROWER_INNER_ARMOR)),
//				new HumanoidRowerModel<>(context.bakeLayer(slimArms ? BLModelLayers.SLIM_PLAYER_ROWER_OUTER_ARMOR : BLModelLayers.PLAYER_ROWER_OUTER_ARMOR)),
//				context.getModelManager()
//			)
//		);
		this.addLayer(new RowerHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
		//TODO elytra does not follow the body rotation
		//this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
	}

	public void render(AbstractClientPlayer entity, ArmArticulation leftArm, ArmArticulation rightArm, float bodyRotateAngleX, float bodyRotateAngleY, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		this.getModel().setModelProperties(entity);
		this.getModel().animate(leftArm, rightArm, bodyRotateAngleX, bodyRotateAngleY);
		super.render(entity, Mth.lerp(partialTick, entity.yRotO, entity.getYRot()), partialTick, stack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractClientPlayer entity) {
		return entity.getSkin().texture();
	}

	@Override
	protected boolean shouldShowName(AbstractClientPlayer entity) {
		return !entity.isLocalPlayer() && super.shouldShowName(entity);
	}
}
