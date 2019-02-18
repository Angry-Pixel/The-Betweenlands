package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.registries.CapabilityRegistry;

public class LayerPuppetOverlay extends LayerAnimatedOverlay<EntityLivingBase> {
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/puppet_overlay.png");

	public LayerPuppetOverlay(RenderLivingBase<EntityLivingBase> renderer) {
		super(renderer, OVERLAY_TEXTURE);
	}

	@Override
	public void render(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if(entity.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null) && entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null).hasPuppeteer()) {
			super.render(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}
}
