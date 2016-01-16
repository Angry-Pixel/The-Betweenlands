package thebetweenlands.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.crops.ModelAspectCrop;
import thebetweenlands.tileentities.TileEntityAspectCrop;

public class TileEntityAspectCropRenderer extends TileEntitySpecialRenderer {
	private static final RenderBlocks renderBlocks = new RenderBlocks();

	private static final ModelAspectCrop MODEL = new ModelAspectCrop();

	private static final ResourceLocation TEXTURE_0 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectCrop0.png");
	private static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectCrop1.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityAspectCrop cropTile = (TileEntityAspectCrop) tile;
		int meta = cropTile.getBlockMetadata();
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glDisable(GL11.GL_CULL_FACE);

		this.bindTexture(TEXTURE_1);
		MODEL.render();


		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();

		this.bindTexture(TextureMap.locationBlocksTexture);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);

		this.bindTexture(TextureMap.locationBlocksTexture);
		Tessellator tessellator = Tessellator.instance;

		float min = 0.375F;
		float max = 0.625F;

		IIcon icon = BLBlockRegistry.rubberTreePlankFence.getBlockTextureFromSide(0);

		tessellator.startDrawingQuads();
		//South
		tessellator.addVertexWithUV(max, 0, max, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(max, 1, max, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 1, max, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 0, max, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//North
		tessellator.addVertexWithUV(min, 0, min, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(min, 1, min, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 1, min, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 0, min, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//Eeast
		tessellator.addVertexWithUV(max, 0, min, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(max, 1, min, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 1, max, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 0, max, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//West
		tessellator.addVertexWithUV(min, 0, max, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(min, 1, max, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 1, min, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 0, min, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//Top
		tessellator.addVertexWithUV(min, 1, min, icon.getInterpolatedU(max*16.0F), icon.getInterpolatedV(min*16.0F));
		tessellator.addVertexWithUV(min, 1, max, icon.getInterpolatedU(max*16.0F), icon.getInterpolatedV(max*16.0F));
		tessellator.addVertexWithUV(max, 1, max, icon.getInterpolatedU(min*16.0F), icon.getInterpolatedV(max*16.0F));
		tessellator.addVertexWithUV(max, 1, min, icon.getInterpolatedU(min*16.0F), icon.getInterpolatedV(min*16.0F));
		tessellator.draw();

		GL11.glPopMatrix();
	}
}
