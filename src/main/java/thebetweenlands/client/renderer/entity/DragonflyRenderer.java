package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.DragonflyModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Dragonfly;

public class DragonflyRenderer extends MobRenderer<Dragonfly, DragonflyModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/dragonfly.png");

	public DragonflyRenderer(EntityRendererProvider.Context context) {
		super(context, new DragonflyModel(context.bakeLayer(BLModelLayers.DRAGONFLY)), 0.5F);
	}

	@Override
	protected void scale(Dragonfly entity, PoseStack stack, float partialTick) {
		if (entity.isFlying()) stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
		stack.scale(0.6F, 0.6F, 0.6F);
	}

	@Override
	public ResourceLocation getTextureLocation(Dragonfly entity) {
		return TEXTURE;
	}
}
