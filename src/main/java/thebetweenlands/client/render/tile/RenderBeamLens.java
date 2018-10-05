package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityBeamLens;

@SideOnly(Side.CLIENT)
public class RenderBeamLens extends TileEntitySpecialRenderer<TileEntityBeamLens> {

	@Override
	public void render(TileEntityBeamLens tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tile == null || !tile.hasWorld())
			return;

		if (!tile.showRenderBox)
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x-0.0005D, y-0.0005D, z-0.0005D);
		GlStateManager.scale(0.999D, 0.999D, 0.999D);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GlStateManager.disableCull();
		int i = 61680;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		RenderGlobal.renderFilledBox(tile.getAABBForRender(), 0F, 0F, 1F, 0.75F);
		RenderGlobal.drawSelectionBoundingBox(tile.getAABBForRender(), 1F, 1F, 1F, 1F);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}
	
	
}