package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelWindChime;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.common.tile.TileEntityWindChime;

@SideOnly(Side.CLIENT)
public class RenderWindChime extends TileEntitySpecialRenderer<TileEntityWindChime> {
	private static final ModelWindChime MODEL = new ModelWindChime();
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/wind_chime.png");

	@Override
	public void render(TileEntityWindChime te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);

		boolean isBreakingAnimation = te != null && destroyStage >= 0;

		if(isBreakingAnimation) {
			this.bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.scale(8.0F, 8.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		} else {
			this.bindTexture(TEXTURE);
		}

		if(te != null) {
			MODEL.render(te.renderTicks + partialTicks, Math.min((te.prevChimeTicks + (te.chimeTicks - te.prevChimeTicks) * partialTicks) / 100.0f, 1.5f));
		} else {
			MODEL.render(0, 0);
		}

		if(isBreakingAnimation) {
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();

		if(!isBreakingAnimation && te != null) {
			ParticleBatch batch = te.getParticleBatch();

			if(batch != null) {
				RenderHelper.disableStandardItemLighting();
				Minecraft.getMinecraft().entityRenderer.enableLightmap();

				BatchedParticleRenderer.INSTANCE.renderBatch(batch, Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);

				RenderHelper.enableStandardItemLighting();
				Minecraft.getMinecraft().entityRenderer.enableLightmap();
			}
		}
	}
}
