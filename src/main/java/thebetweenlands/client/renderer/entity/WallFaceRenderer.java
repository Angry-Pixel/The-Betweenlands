package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.wall.AbstractWallCreature;

public abstract class WallFaceRenderer<T extends AbstractWallCreature, M extends MowzieModelBase<T>> extends MultiPieceMobModelRenderer<T, M> {
	public WallFaceRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
		super(context, model, shadowRadius);
	}

	@Override
	protected void scale(T entity, PoseStack stack, float partialTick) {
		stack.translate(0, -entity.getBbWidth() / 2, 0);
		stack.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(partialTick)));
		stack.translate(0, entity.getBbWidth() / 2, 0);
	}

	@Override
	protected void setupRotations(T entity, PoseStack stack, float bob, float yBodyRot, float partialTick, float scale) {
		stack.mulPose(Axis.YP.rotationDegrees(180.0F - yBodyRot));
	}
}
