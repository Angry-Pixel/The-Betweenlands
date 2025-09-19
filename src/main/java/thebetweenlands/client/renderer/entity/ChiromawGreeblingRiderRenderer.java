package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ChiromawGreeblingRiderModel;
import thebetweenlands.client.model.entity.ChiromawModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.chiromaw.Chiromaw;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawGreeblingRider;

public class ChiromawGreeblingRiderRenderer extends MobRenderer<ChiromawGreeblingRider, ChiromawGreeblingRiderModel> {

	public ChiromawGreeblingRiderRenderer(EntityRendererProvider.Context context) {
		super(context, new ChiromawGreeblingRiderModel(context.bakeLayer(BLModelLayers.CHIROMAW_GREEBLING_RIDER)), 0.5F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_glow.png")));
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
	}

	@Override
	protected void scale(ChiromawGreeblingRider entity, PoseStack stack, float partialTick) {
		float flap = Mth.sin((entity.tickCount + partialTick) * 0.5F) * 0.6F;
		stack.translate(0.0F, 0.25F - flap * 0.4F, 0.0F);
		stack.mulPose(Axis.XP.rotationDegrees(40));
		stack.translate(0.0F, 0.0F, 0.4F);
	}

	@Override
	public ResourceLocation getTextureLocation(ChiromawGreeblingRider entity) {
		return ChiromawRenderer.TEXTURE;
	}
}
