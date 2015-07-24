package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelPestleAndMortar;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import thebetweenlands.utils.ItemRenderHelper;
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
		if(mortar.hasPestle) {
			GL11.glPushMatrix();
			float rise = (float)(mortar.progress -42F) * -0.03F;
			if(mortar.progress <= 42)
				GL11.glRotatef(mortar.progress * 4.2857142857142857142857142857143F * 2F, 0.0F, 1F, 0F);
			if(mortar.progress > 42 && mortar.progress < 54)
				GL11.glTranslatef(0F, rise, 0F);
			if(mortar.progress >= 54 && mortar.progress < 64)
				GL11.glTranslatef(0F, -0.63F - rise, 0F);
			if(mortar.progress >= 64 && mortar.progress < 74)
				GL11.glTranslatef(0F, 0.63F + rise, 0F);
			if(mortar.progress >= 74 && mortar.progress < 84)
				GL11.glTranslatef(0F, -1.23F - rise, 0F);
			model.renderPestle();
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		if (mortar.getStackInSlot(3) != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.43D, z + 0.5D);
			GL11.glScaled(0.15D, 0.15D, 0.15D);
			GL11.glTranslated(0D, mortar.itemBob * 0.01F, 0D);
			GL11.glRotatef(mortar.crystalRotation, 0, 1, 0);
			ItemRenderHelper.renderItem(mortar.getStackInSlot(3), 0);
			GL11.glPopMatrix();
		}
	}
}