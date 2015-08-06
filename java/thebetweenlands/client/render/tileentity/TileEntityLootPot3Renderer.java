package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelLootPot3;
import thebetweenlands.tileentities.TileEntityLootPot3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityLootPot3Renderer extends TileEntitySpecialRenderer {

	private final ModelLootPot3 LOOT_POT = new ModelLootPot3();
	private final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/lootPot3.png");

	public void renderAModelAt(TileEntityLootPot3 tile, double x, double y, double z, float partialTickTime) {
		int meta = tile.getBlockMetadata();
		int offset = tile.getModelRotationOffset();
		bindTexture(TEXTURE);
		switch (meta) {
		case 2:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(180F + offset, 0.0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		case 3:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(offset, 0.0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		case 4:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(90F + offset, 0.0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		case 5:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(-90F + offset, 0.0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		renderAModelAt((TileEntityLootPot3)tile, x, y, z, partialTickTime);
	}
}