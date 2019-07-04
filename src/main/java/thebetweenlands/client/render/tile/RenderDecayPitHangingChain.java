package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDecayPitChain;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityDecayPitHangingChain;
@SideOnly(Side.CLIENT)
public class RenderDecayPitHangingChain extends TileEntitySpecialRenderer<TileEntityDecayPitHangingChain > {
	public static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_chain.png");
	private final ModelDecayPitChain CHAIN_MODEL = new ModelDecayPitChain();

	@Override
	public void render(TileEntityDecayPitHangingChain tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(tile == null || !tile.hasWorld())
			return;
		float scroll = tile.animationTicksChainPrev * 0.0078125F + (tile.animationTicksChain * 0.0078125F - tile.animationTicksChainPrev * 0.0078125F) * partialTicks;
		for (int len = 0; len <= 2 + MathHelper.floor(tile.getProgress()* 0.0078125F); len++) {
			renderChainpart(tile, x + 1.5D, y + len - tile.getProgress()* 0.0078125, z + 0.5D, scroll, 0F);
			renderChainpart(tile, x - 0.5D, y + len - tile.getProgress()* 0.0078125, z + 0.5D, scroll, 180F);
			renderChainpart(tile, x + 0.5D, y + len - tile.getProgress()* 0.0078125, z + 1.5D, scroll, 90F);
			renderChainpart(tile, x + 0.5D, y + len - tile.getProgress()* 0.0078125, z - 0.5D, scroll, 270F);
		}

		renderDebugBoundingBox(tile, x, y, z);
	}

	private void renderChainpart(TileEntityDecayPitHangingChain tile, double x, double y, double z, float scroll, float angle) {
		bindTexture(CHAIN_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y -0.5D, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(angle, 0F, 1F, 0F);
		CHAIN_MODEL.render(0.0625F);
		GlStateManager.popMatrix();
	}
	
	private void renderDebugBoundingBox(TileEntityDecayPitHangingChain tile, double x, double y, double z) {
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
		RenderGlobal.renderFilledBox(tile.getAABBForRender1(), 0F, 0F, 1F, 0.75F);
		RenderGlobal.drawSelectionBoundingBox(tile.getAABBForRender1(), 1F, 1F, 1F, 1F);
		RenderGlobal.renderFilledBox(tile.getAABBForRender2(), 0F, 0F, 1F, 0.75F);
		RenderGlobal.drawSelectionBoundingBox(tile.getAABBForRender2(), 1F, 1F, 1F, 1F);
		RenderGlobal.renderFilledBox(tile.getAABBForRender3(), 0F, 0F, 1F, 0.75F);
		RenderGlobal.drawSelectionBoundingBox(tile.getAABBForRender3(), 1F, 1F, 1F, 1F);
		RenderGlobal.renderFilledBox(tile.getAABBForRender4(), 0F, 0F, 1F, 0.75F);
		RenderGlobal.drawSelectionBoundingBox(tile.getAABBForRender4(), 1F, 1F, 1F, 1F);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(TileEntityDecayPitHangingChain tile) {
		return true;
	}
}