package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelChiromaw;
import thebetweenlands.common.entity.mobs.EntityLargeChiromaw;

@SideOnly(Side.CLIENT)
public class RenderLargeChiromaw extends RenderLiving<EntityLargeChiromaw> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw.png");

	public RenderLargeChiromaw(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelChiromaw(), 0.5F);
		this.addLayer(new LayerOverlay<>(this, new ResourceLocation("thebetweenlands:textures/entity/chiromaw_glow.png")).setGlow(true));
	}

	@Override
	protected void preRenderCallback(EntityLargeChiromaw chiromaw, float partialTickTime) {
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		if (!chiromaw.getIsHanging()) {
			float flap = MathHelper.sin((chiromaw.ticksExisted + partialTickTime) * 0.5F) * 0.6F;
			GlStateManager.translate(0.0F, 0F - flap * 0.5F, 0.0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLargeChiromaw entity) {
		return TEXTURE;
	}
}
