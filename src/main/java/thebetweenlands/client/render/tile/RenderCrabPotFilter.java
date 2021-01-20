package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelCrabPotFilter;
import thebetweenlands.common.tile.TileEntityCrabPotFilter;

public class RenderCrabPotFilter extends TileEntitySpecialRenderer<TileEntityCrabPotFilter> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/crab_pot_filter.png");
	public static final ModelCrabPotFilter MODEL = new ModelCrabPotFilter();

	@Override
	public void render(TileEntityCrabPotFilter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int index = te != null ? te.getRotation() : 0;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5f, (float) y, (float) z + 0.5f);
		GlStateManager.pushMatrix();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 1.5f, 0);
		GlStateManager.rotate(getRotation(index), 0.0F, 1F, 0F);
		GlStateManager.scale(1F, -1F, -1F);
		bindTexture(TEXTURE);
		MODEL.render();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		// TODO more things needed here for some render stuffs and whatnot ;P
	}

	public static float getRotation(int index) {
		switch (index) {
		case 3:
			return 180F;
		case 2:
		default:
			return 90F;
		case 1:
			return 0F;
		case 0:
			return -90F;
		}
	}
}