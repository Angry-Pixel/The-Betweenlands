package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.LeechModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Leech;

public class LeechRenderer extends MobRenderer<Leech, LeechModel> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/leech.png");

	public LeechRenderer(EntityRendererProvider.Context context) {
		super(context, new LeechModel(context.bakeLayer(BLModelLayers.LEECH)), 0.3F);
	}

	@Override
	protected void scale(Leech entity, PoseStack stack, float partialTick) {
		if (!entity.isPassenger()) {
			stack.scale(1 + entity.getBloodConsumed() * 0.1F, 1 + entity.getBloodConsumed() * 0.1F, (entity.moveProgress + 3F) / 3F);
		} else {
			stack.scale(entity.moveProgress * entity.moveProgress / 2 + 0.5F, entity.moveProgress * entity.moveProgress / 2 + 0.5F, 1F);
			stack.mulPose(Axis.YP.rotationDegrees(180));
			stack.translate(0, 0, 0.5F);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Leech entity) {
		return TEXTURE;
	}
}
