package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelPestleAndMortar;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityPestleAndMortarRenderer extends TileEntitySpecialRenderer {

	private final ModelPestleAndMortar model = new ModelPestleAndMortar();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/pestleAndMortar.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityPestleAndMortar mortar = (TileEntityPestleAndMortar) tile;
		int meta = mortar.getBlockMetadata();
		bindTexture(TEXTURE);
		GL11.glPushMatrix();
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
		model.render();
		GL11.glPopMatrix();
	}
}