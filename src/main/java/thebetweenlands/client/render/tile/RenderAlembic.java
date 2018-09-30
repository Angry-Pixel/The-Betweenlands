package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelAlembic;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.tile.TileEntityAlembic;

@SideOnly(Side.CLIENT)
public class RenderAlembic extends TileEntitySpecialRenderer<TileEntityAlembic> {

	private final ModelAlembic model = new ModelAlembic();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/alembic.png");

	@Override
	public void render(TileEntityAlembic te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int meta = te != null ? te.getBlockMetadata() : 0;
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		switch (meta) {
		case 2:
			GlStateManager.rotate(180F, 0.0F, 1F, 0F);
			break;
		case 3:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case 4:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			break;
		case 5:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			break;
		}
		if(te != null && te.isFull()) {
			float[] colors = ElixirRecipe.getInfusionColor(te.getElixirRecipe(), te.getInfusionTime());
			model.renderWithLiquid(colors[0], colors[1], colors[2], te.getProgress());
		} else {
			model.render();
		}
		GlStateManager.popMatrix();
	}
}
