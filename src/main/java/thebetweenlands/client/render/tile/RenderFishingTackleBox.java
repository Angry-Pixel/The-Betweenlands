package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelFishingTackleBox;
import thebetweenlands.common.tile.TileEntityFishingTackleBox;

public class RenderFishingTackleBox extends TileEntitySpecialRenderer<TileEntityFishingTackleBox> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/fishing_tackle_box.png");
	public static final ModelFishingTackleBox MODEL = new ModelFishingTackleBox();

	@Override
	public void render(TileEntityFishingTackleBox te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
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

		this.bindTexture(TEXTURE);
		MODEL.render(te != null ? te.getLidAngle(partialTicks) : 0);

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