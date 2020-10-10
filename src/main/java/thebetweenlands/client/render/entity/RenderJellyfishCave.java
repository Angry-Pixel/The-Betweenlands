package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelJellyfishCave;
import thebetweenlands.common.entity.mobs.EntityJellyfishCave;

@SideOnly(Side.CLIENT)
public class RenderJellyfishCave extends RenderLiving<EntityJellyfishCave> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/jellyfish_cave.png");

	public final static ModelJellyfishCave MODEL = new ModelJellyfishCave();

	public RenderJellyfishCave(RenderManager manager) {
		super(manager, MODEL, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityJellyfishCave entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityJellyfishCave jellyfish, double x, double y, double z, float entityYaw, float partialTicks) {
		float smoothedYaw = jellyfish.prevRotationYaw + (jellyfish.rotationYaw - jellyfish.prevRotationYaw) * partialTicks;
		//float smoothedPitch = jellyfish.prevRotationPitch + (jellyfish.rotationPitch - jellyfish.prevRotationPitch) * partialTicks;
		float scale = jellyfish.getJellyfishSize();
		shadowSize = scale * 0.5F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + scale * 1.5F, z);
		GlStateManager.scale(scale, -scale, -scale);
		GlStateManager.rotate(smoothedYaw, 0F, 1F, 0F);
		//GlStateManager.rotate(smoothedPitch, 1F, 0F, 0F);
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(!jellyfish.isInvisible());
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.translate(0F, 0.5F, 0F);
		MODEL.render(jellyfish, jellyfish.limbSwing, jellyfish.limbSwingAmount, jellyfish.ticksExisted, 0F, 0F, 0.0625F);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		MODEL.setLivingAnimations(jellyfish, jellyfish.limbSwing, jellyfish.limbSwingAmount, partialTicks);
	}
}
