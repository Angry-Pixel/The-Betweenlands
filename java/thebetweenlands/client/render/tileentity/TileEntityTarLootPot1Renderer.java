package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelTarLootPot1;
import thebetweenlands.tileentities.TileEntityTarLootPot1;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityTarLootPot1Renderer extends TileEntitySpecialRenderer {

	private final ModelTarLootPot1 LOOT_POT = new ModelTarLootPot1();
	private final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/tarLootPot1.png");

	public void renderAModelAt(TileEntityTarLootPot1 tile, double x, double y, double z, float partialTickTime) {
		int meta = tile.getBlockMetadata();
		bindTexture(TEXTURE);
		switch (meta) {
		case 2:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(180F, 0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		case 3:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(0F, 0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		case 4:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(90F, 0.0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		case 5:
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
			GL11.glScalef(1F, -1F, -1F);
			GL11.glRotatef(-90F, 0F, 1F, 0F);
			LOOT_POT.render();
			GL11.glPopMatrix();
			break;
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		renderAModelAt((TileEntityTarLootPot1) tile, x, y, z, partialTickTime);
	}
}