package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelChiromawMatriarch;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;

@SideOnly(Side.CLIENT)
public class RenderChiromawMatriarch extends RenderLiving<EntityChiromawMatriarch> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw_matriarch.png");

	public RenderChiromawMatriarch(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelChiromawMatriarch(), 1.5F);
		this.addLayer(new LayerOverlay<>(this, new ResourceLocation("thebetweenlands:textures/entity/chiromaw_matriarch_glow.png")).setGlow(true));
	}

	@Override
	protected void preRenderCallback(EntityChiromawMatriarch chiromaw, float partialTickTime) {
		GlStateManager.scale(1.5F, 1.5F, 1.5F);
		
		float flyingPercent = Math.max(0, 1.0f - chiromaw.landingTimer.getAnimationProgressSmooth(partialTickTime) - chiromaw.nestingTimer.getAnimationProgressSmooth(partialTickTime));
		
		float flap = MathHelper.sin((chiromaw.ticksExisted + partialTickTime) * 0.5F) * 0.6F;
		GlStateManager.translate(0.0F, -flap * 0.5F * flyingPercent, 0.0F);
		
		GlStateManager.rotate(20F * flyingPercent, 1F, 0F, 0F);

		if (chiromaw.getIsSpinning()) {
			float spinningRotation = chiromaw.previousSpinAngle + (chiromaw.spinAngle - chiromaw.previousSpinAngle) * partialTickTime;
			//GlStateManager.rotate(spinningRotation, 1F, 0F, 0F);
			//GlStateManager.rotate(spinningRotation * 0.125F, 0F, 0F, 1F);
			GlStateManager.rotate(spinningRotation, 0F, 1F, 0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromawMatriarch entity) {
		return TEXTURE;
	}
}
