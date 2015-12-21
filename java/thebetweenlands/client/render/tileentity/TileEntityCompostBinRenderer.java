package thebetweenlands.client.render.tileentity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.ModelCompostBin;
import thebetweenlands.tileentities.TileEntityCompostBin;
import thebetweenlands.utils.ItemRenderHelper;

@SideOnly(Side.CLIENT)
public class TileEntityCompostBinRenderer extends TileEntitySpecialRenderer {
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands", "textures/tiles/compostBin.png");
	private final ModelCompostBin model = new ModelCompostBin();

	private RenderBlocks blockRenderer;

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityCompostBin compostBin = (TileEntityCompostBin) tile;
		int meta = compostBin.getBlockMetadata();
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + .5f, (float) y, (float) z + .5f);
		GL11.glRotatef(getRotation(meta), 0.0F, 1F, 0F);

		float compostHeight = Math.min(compostBin.getTotalCompostedAmount() / (float) TileEntityCompostBin.MAX_COMPOST_AMOUNT, 0.82f);

		if (compostHeight > 0.01f) {
			Tessellator tessellator = Tessellator.instance;

			tessellator.setBrightness(tile.getWorldObj().getBlock((int)x, (int)y, (int)z).getMixedBrightnessForBlock(tile.getWorldObj(), (int)x, (int)y, (int)z));

			GL11.glPushMatrix();
			GL11.glScaled(0.8f, compostHeight, 0.8f);
			GL11.glTranslated(-tile.xCoord - 0.5, -tile.yCoord, -tile.zCoord - 0.5);

			tessellator.setColorOpaque_F(0.5f, 0.8f, 0.1f);

			this.bindTexture(TextureMap.locationBlocksTexture);
			tessellator.startDrawingQuads();
			blockRenderer.renderBlockAllFaces(BLBlockRegistry.weedwoodLeaves, tile.xCoord, tile.yCoord, tile.zCoord);
			tessellator.draw();

			GL11.glPopMatrix();
		}

		GL11.glColor3f(1, 1, 1);

		GL11.glPushMatrix();
		GL11.glTranslatef(0, 1.5f, 0);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glDisable(GL11.GL_CULL_FACE);
		bindTexture(TEXTURE);
		model.render(compostBin.getLidAngle(partialTickTime));
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();

		for (int i = 0; i < compostBin.getSizeInventory(); i++) {
			ItemStack stack = compostBin.getStackInSlot(i);

			if (stack != null) {
				GL11.glPushMatrix();

				// 0.4 for items, 0.5 for compost
				GL11.glTranslatef(0, .1f + compostHeight + i * 0.4f / compostBin.getSizeInventory(), .08f);
				GL11.glScalef(.36f, .36f, .36f);
				GL11.glRotatef(new Random(i * 12315).nextFloat() * 360f, 0, 1, 0);
				GL11.glRotatef(90.0f, 1, 0, 0);
				ItemRenderHelper.renderItem(stack, 0);

				GL11.glPopMatrix();
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	public void func_147496_a(World world) {
		blockRenderer = new RenderBlocks(world); // onWorldChange
	}

	public static float getRotation(int meta) {
		switch (meta) {
		case 5:
			return 180F;
		case 4:
		default:
			return 0F;
		case 3:
			return 90F;
		case 2:
			return -90F;
		}
	}
}