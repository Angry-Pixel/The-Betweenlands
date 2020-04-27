package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelGreeblingCorpse;
import thebetweenlands.common.entity.EntityGreeblingCorpse;

@SideOnly(Side.CLIENT)
public class RenderGreeblingCorpse extends Render<EntityGreeblingCorpse> {
	public static final ResourceLocation TEXUTURE = new ResourceLocation("thebetweenlands:textures/entity/greebling_corpse.png");

	private final ModelGreeblingCorpse model = new ModelGreeblingCorpse();

	public RenderGreeblingCorpse(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntityGreeblingCorpse entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		GlStateManager.pushMatrix();
		
		GlStateManager.translate((float)x, (float)y, (float)z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1, -1, 1);
		GlStateManager.translate(0.0F, -1.501F, 0.0F);
		
		GlStateManager.rotate(entity.rotationYaw, 0, 1, 0);

		if(this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		this.bindEntityTexture(entity);
		
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		
		
		float alpha = 1.0f;
		if(entity.fadeTimer > 0) {
			alpha = 1.0f - Math.min(1, (entity.fadeTimer - 1 + partialTicks) / 40.0f);
		}
		
		GlStateManager.color(1, 1, 1, alpha);
		
		this.model.render(entity, 0, 0, 0, 0, 0, 0.0625f);

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
		GlStateManager.disableBlend();
		
		if(this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGreeblingCorpse entity) {
		return TEXUTURE;
	}
}
