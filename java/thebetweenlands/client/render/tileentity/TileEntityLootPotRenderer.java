package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelLootPot1;
import thebetweenlands.client.model.block.ModelLootPot2;
import thebetweenlands.client.model.block.ModelLootPot3;
import thebetweenlands.tileentities.TileEntityLootPot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityLootPotRenderer extends TileEntitySpecialRenderer {

	private final ModelLootPot1 LOOT_POT_1 = new ModelLootPot1();
	private final ModelLootPot2 LOOT_POT_2 = new ModelLootPot2();
	private final ModelLootPot3 LOOT_POT_3 = new ModelLootPot3();
	private final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/lootPot1.png");
	private final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/lootPot2.png");
	private final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/lootPot3.png");

	public void renderAModelAt(TileEntityLootPot tile, double x, double y, double z, float f) {
		int meta = tile.getBlockMetadata();
		byte type = tile.getPotModelType();
		bindTexture(getTexture(type));		
		switch (meta) {
			case 2:
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
				GL11.glScalef(1F, -1F, -1F);
				GL11.glRotatef(180F, 0.0F, 1F, 0F);
				getModelToRender(type);
				GL11.glPopMatrix();
				break;
			case 3:
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
				GL11.glScalef(1F, -1F, -1F);
				GL11.glRotatef(0F, 0.0F, 1F, 0F);
				getModelToRender(type);
				GL11.glPopMatrix();
				break;
			case 4:
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
				GL11.glScalef(1F, -1F, -1F);
				GL11.glRotatef(90F, 0.0F, 1F, 0F);
				getModelToRender(type);
				GL11.glPopMatrix();
				break;
			case 5:
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5D, y + 1.5F, z + 0.5D);
				GL11.glScalef(1F, -1F, -1F);
				GL11.glRotatef(-90F, 0.0F, 1F, 0F);
				getModelToRender(type);
				GL11.glPopMatrix();
				break;
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		renderAModelAt((TileEntityLootPot)tile, x, y, z, partialTickTime);
	}

	public ResourceLocation getTexture(byte type) {
		return type == 0 ? TEXTURE_1 : type == 1 ? TEXTURE_2 : TEXTURE_3;
	}

	public void getModelToRender(byte type) {
		switch (type) {
		case 0:
			LOOT_POT_1.render();
			break;
		case 1:
			LOOT_POT_2.render();
			break;
		case 2:
			LOOT_POT_3.render();
			break;
		}
	}
}