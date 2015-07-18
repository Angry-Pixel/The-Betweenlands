package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelPestleAndMortar;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityLifeCrystal;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import thebetweenlands.utils.ItemRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityLifeCrystalRenderer extends TileEntitySpecialRenderer {
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		double viewRot = 180D + Math.toDegrees(Math.atan2(RenderManager.instance.viewerPosX - tile.xCoord - 0.5D, RenderManager.instance.viewerPosZ - tile.zCoord - 0.5D));
		int life = (int)Math.floor(4.0F - ((TileEntityLifeCrystal)tile).getLife() / (float)TileEntityLifeCrystal.MAX_LIFE * 4.0F);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y + 0.42D + Math.sin(System.nanoTime() / 1000000000.0F) / 8.0D, z + 0.5D);
		GL11.glRotated(viewRot, 0, 1, 0);
		GL11.glScaled(0.25, 0.25, 0.25);
		GL11.glColor4f(1, 1, 1, 0.25F + (((TileEntityLifeCrystal)tile).getLife() / (float)TileEntityLifeCrystal.MAX_LIFE) / 2.0F);
		ItemRenderHelper.renderItem(new ItemStack(BLItemRegistry.lifeCrystal, 1, life), 0);
		GL11.glPopMatrix();
	}
}