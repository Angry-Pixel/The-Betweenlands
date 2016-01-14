package thebetweenlands.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelPurifier;
import thebetweenlands.tileentities.TileEntityGeckoCage;

@SideOnly(Side.CLIENT)
public class TileEntityGeckoCageRenderer extends TileEntitySpecialRenderer {
	private final RenderBlocks blockRenderer = new RenderBlocks();
	private final ModelPurifier model = new ModelPurifier();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/purifier.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityGeckoCage purifier = (TileEntityGeckoCage) tile;
		int meta = purifier.getBlockMetadata();
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
		model.renderAll();
		GL11.glPopMatrix();
	}
}

