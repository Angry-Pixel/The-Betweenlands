package thebetweenlands.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelAlembic;
import thebetweenlands.herblore.elixirs.ElixirRecipe;
import thebetweenlands.tileentities.TileEntityAlembic;

@SideOnly(Side.CLIENT)
public class TileEntityAlembicRenderer extends TileEntitySpecialRenderer {

	private final ModelAlembic model = new ModelAlembic();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/alembic.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityAlembic alembic = (TileEntityAlembic) tile;
		int meta = alembic.getBlockMetadata();
		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		switch (meta) {
		case 2:
			GL11.glRotatef(180F, 0.0F, 1F, 0F);
			break;
		case 3:
			GL11.glRotatef(0F, 0.0F, 1F, 0F);
			break;
		case 4:
			GL11.glRotatef(90F, 0.0F, 1F, 0F);
			break;
		case 5:
			GL11.glRotatef(-90F, 0.0F, 1F, 0F);
			break;
		}
		if(alembic.isFull()) {
			float[] colors = ElixirRecipe.getInfusionColor(alembic.getElixirRecipe(), alembic.getInfusionTime());
			model.renderWithLiquid(colors[0], colors[1], colors[2], alembic.getProgress());
		} else {
			model.render();
		}
		GL11.glPopMatrix();
	}
}