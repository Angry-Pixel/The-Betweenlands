package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.common.tile.TileEntityWindChime;

@SideOnly(Side.CLIENT)
public class RenderWindChime extends TileEntitySpecialRenderer<TileEntityWindChime> {
	@Override
	public void render(TileEntityWindChime te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();

		BatchedParticleRenderer.INSTANCE.renderBatch(te.getParticleBatch(), Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);

		RenderHelper.enableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}
}
