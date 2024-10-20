package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.RootSpriteModel;
import thebetweenlands.client.model.entity.SporelingModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.RootSprite;
import thebetweenlands.common.entity.creature.Sporeling;

public class SporelingRenderer extends MobRenderer<Sporeling, SporelingModel> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/sporeling.png");

	public SporelingRenderer(EntityRendererProvider.Context context) {
		super(context, new SporelingModel(context.bakeLayer(BLModelLayers.SPORELING)), 0.2F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/sporeling_glow.png")));
	}

	@Override
	protected void setupRotations(Sporeling entity, PoseStack stack, float bob, float yBodyRot, float partialTick, float scale) {
		if (entity.getIsFalling()) {
			stack.mulPose(Axis.YP.rotationDegrees(entity.smoothedAngle(partialTick)));
		}
		super.setupRotations(entity, stack, bob, yBodyRot, partialTick, scale);
	}

	@Override
	public ResourceLocation getTextureLocation(Sporeling entity) {
		return TEXTURE;
	}
}
