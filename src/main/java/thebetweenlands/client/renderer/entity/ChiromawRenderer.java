package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ChiromawModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.chiromaw.Chiromaw;

public class ChiromawRenderer extends MobRenderer<Chiromaw, ChiromawModel<Chiromaw>> {

	protected static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw.png");

	public ChiromawRenderer(EntityRendererProvider.Context context) {
		super(context, new ChiromawModel<>(context.bakeLayer(BLModelLayers.CHIROMAW)), 0.5F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_glow.png")));
	}

	@Override
	protected void scale(Chiromaw entity, PoseStack stack, float partialTick) {
		if (!entity.isHanging()) {
			float flap = Mth.sin((entity.tickCount + partialTick) * 0.5F) * 0.6F;
			stack.translate(0.0F, 0.25F - flap * 0.4F, 0.0F);
			stack.mulPose(Axis.XP.rotationDegrees(40));
			stack.translate(0.0F, 0.0F, 0.4F);
		} else {
			stack.translate(0.0F, 2.125F, 0.0F);
			stack.mulPose(Axis.XP.rotationDegrees(180));
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Chiromaw entity) {
		return TEXTURE;
	}
}
