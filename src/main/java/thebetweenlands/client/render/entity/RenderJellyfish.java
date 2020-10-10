package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelJellyfish;
import thebetweenlands.common.entity.mobs.EntityJellyfish;

@SideOnly(Side.CLIENT)
public class RenderJellyfish extends RenderLiving<EntityJellyfish> {
	private static final ResourceLocation[] TEXTURE = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_1.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_2.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_3.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_4.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_5.png")
			};

	public final static ModelJellyfish MODEL = new ModelJellyfish();

	public RenderJellyfish(RenderManager manager) {
		super(manager, MODEL, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityJellyfish entity) {
		return TEXTURE[0];
	}

	@Override
	public void doRender(EntityJellyfish jellyfish, double x, double y, double z, float entityYaw, float partialTicks) {
		float smoothedYaw = jellyfish.prevRotationYaw + (jellyfish.rotationYaw - jellyfish.prevRotationYaw) * partialTicks;
		float smoothedPitch = jellyfish.prevRotationPitch + (jellyfish.rotationPitch - jellyfish.prevRotationPitch) * partialTicks + 90F;
		float scale = jellyfish.getJellyfishSize();
		shadowSize = scale * 0.5F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + scale * 0.5F, z);
		GlStateManager.scale(scale, -scale, -scale);
		GlStateManager.rotate(smoothedYaw, 0F, 1F, 0F);
		GlStateManager.rotate(smoothedPitch, 1F, 0F, 0F);
		bindTexture(TEXTURE[jellyfish.getJellyfishColour()]);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(!jellyfish.isInvisible());
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.translate(0F, -0.5F, 0F);
		GlStateManager.scale(2F + MathHelper.sin(jellyfish.limbSwing * 1.0F) * jellyfish.limbSwingAmount * 2F, 1F - MathHelper.sin(jellyfish.limbSwing * 1.0F) * jellyfish.limbSwingAmount, 2F + MathHelper.sin(jellyfish.limbSwing * 1.0F)  * jellyfish.limbSwingAmount * 2F);
		MODEL.render(jellyfish, jellyfish.limbSwing, jellyfish.limbSwingAmount, jellyfish.ticksExisted, 0F, 0F, 0.0625F);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		MODEL.setLivingAnimations(jellyfish, jellyfish.limbSwing, jellyfish.limbSwingAmount, partialTicks);
	}
}
