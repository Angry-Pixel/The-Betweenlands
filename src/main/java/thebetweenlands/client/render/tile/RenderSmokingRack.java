package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelSmokingRack;
import thebetweenlands.common.tile.TileEntitySmokingRack;

public class RenderSmokingRack extends TileEntitySpecialRenderer<TileEntitySmokingRack> {
	public static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("thebetweenlands:textures/tiles/smoking_rack.png");
	public static final ResourceLocation TEXTURE_SMOKED = new ResourceLocation("thebetweenlands:textures/tiles/smoking_rack_smoked.png");
	public static final ModelSmokingRack MODEL = new ModelSmokingRack();

	@Override
	public void render(TileEntitySmokingRack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int meta = te != null ? te.getBlockMetadata() : 0;

		GlStateManager.pushMatrix();
		
		GlStateManager.translate((float) x + 0.5f, (float) y, (float) z + 0.5f);
		GlStateManager.rotate(getRotation(meta), 0.0F, 1F, 0F);

		GlStateManager.pushMatrix();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 1.5f, 0);
		GlStateManager.scale(1F, -1F, -1F);
	//	GlStateManager.disableCull();

		this.bindTexture(TEXTURE_NORMAL);
		MODEL.render();

	//	GlStateManager.enableCull();
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

	public static float getRotation(int meta) {
		switch (meta) {
		case 5:
			return 180F;
		case 4:
		default:
			return 0F;
		case 3:
			return 90F;
		case 2:
			return -90F;
		}
	}
}