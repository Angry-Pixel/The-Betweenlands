package thebetweenlands.client.render.tileentity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.crops.ModelAspectrusCrop1;
import thebetweenlands.client.model.block.crops.ModelAspectrusCrop2;
import thebetweenlands.client.model.block.crops.ModelAspectrusCrop3;
import thebetweenlands.client.model.block.crops.ModelAspectrusCrop4;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.tileentities.TileEntityAspectrusCrop;
import thebetweenlands.utils.LightingUtil;

public class TileEntityAspectrusCropRenderer extends TileEntitySpecialRenderer {
	private static final RenderBlocks renderBlocks = new RenderBlocks();

	private static final ModelAspectrusCrop1 MODEL1 = new ModelAspectrusCrop1();
	private static final ModelAspectrusCrop2 MODEL2 = new ModelAspectrusCrop2();
	private static final ModelAspectrusCrop3 MODEL3 = new ModelAspectrusCrop3();
	private static final ModelAspectrusCrop4 MODEL4 = new ModelAspectrusCrop4();

	private static final ResourceLocation TEXTURE_0 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectrusCrop0.png");
	private static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectrusCrop1.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectrusCrop2.png");
	private static final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectrusCrop3.png");
	private static final ResourceLocation TEXTURE_4 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectrusCrop4.png");
	private static final ResourceLocation TEXTURE_5 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectrusCrop5.png");
	private static final ResourceLocation TEXTURE_6 = new ResourceLocation("thebetweenlands:textures/blocks/crops/aspectrusCrop6.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		///// Fence /////

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);

		this.bindTexture(TextureMap.locationBlocksTexture);
		Tessellator tessellator = Tessellator.instance;

		float min = 0.375F;
		float max = 0.625F;

		IIcon icon = BLBlockRegistry.rubberTreePlankFence.getBlockTextureFromSide(0);

		tessellator.startDrawingQuads();
		//South
		tessellator.setNormal(0, 0, 1);
		tessellator.addVertexWithUV(max, 0, max, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(max, 1, max, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 1, max, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 0, max, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//North
		tessellator.setNormal(0, 0, -1);
		tessellator.addVertexWithUV(min, 0, min, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(min, 1, min, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 1, min, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 0, min, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//Eeast
		tessellator.setNormal(1, 0, 0);
		tessellator.addVertexWithUV(max, 0, min, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(max, 1, min, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 1, max, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(max, 0, max, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//West
		tessellator.setNormal(-1, 0, 0);
		tessellator.addVertexWithUV(min, 0, max, icon.getInterpolatedU(max*16.0F), icon.getMaxV());
		tessellator.addVertexWithUV(min, 1, max, icon.getInterpolatedU(max*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 1, min, icon.getInterpolatedU(min*16.0F), icon.getMinV());
		tessellator.addVertexWithUV(min, 0, min, icon.getInterpolatedU(min*16.0F), icon.getMaxV());
		//Top
		tessellator.setNormal(0, 1, 0);
		tessellator.addVertexWithUV(min, 1, min, icon.getInterpolatedU(max*16.0F), icon.getInterpolatedV(min*16.0F));
		tessellator.addVertexWithUV(min, 1, max, icon.getInterpolatedU(max*16.0F), icon.getInterpolatedV(max*16.0F));
		tessellator.addVertexWithUV(max, 1, max, icon.getInterpolatedU(min*16.0F), icon.getInterpolatedV(max*16.0F));
		tessellator.addVertexWithUV(max, 1, min, icon.getInterpolatedU(min*16.0F), icon.getInterpolatedV(min*16.0F));
		tessellator.draw();

		GL11.glPopMatrix();


		///// Crop /////

		TileEntityAspectrusCrop cropTile = (TileEntityAspectrusCrop) tile;
		int meta = cropTile.getBlockMetadata();

		Random rnd = new Random();
		long seed = tile.xCoord * 0x2FC20FL ^ tile.yCoord * 0x6EBFFF5L ^ tile.zCoord;
		rnd.setSeed(seed * seed * 0x285B825L + seed * 11L);
		int rndRot = rnd.nextInt(4);

		Aspect aspect = cropTile.getAspect();
		Random colorRnd = new Random();
		if(aspect != null)
			colorRnd.setSeed(aspect.type.getName().hashCode());

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glRotatef(rndRot * 90.0F, 0, 1, 0);
		GL11.glDisable(GL11.GL_CULL_FACE);

		int index = meta;
		if(index <= 4) {
			index = index / 2;
		} else {
			index = index - 2;
		}

		switch(index) {
		case 0:
			this.bindTexture(TEXTURE_0);
			break;
		case 1:
			this.bindTexture(TEXTURE_1);
			break;
		case 2:
			this.bindTexture(TEXTURE_2);
			break;
		case 3:
			this.bindTexture(TEXTURE_3);
			break;
		case 4:
			this.bindTexture(TEXTURE_4);
			break;
		case 5:
			this.bindTexture(TEXTURE_5);
			break;
		case 6:
			this.bindTexture(TEXTURE_6);
			break;
		}

		switch(index) {
		case 0:
		case 1:
			MODEL1.render();
			break;
		case 2:
		case 3:
			MODEL2.render();
			break;
		case 4:
			if(aspect != null) {
				GL11.glColor4f(colorRnd.nextFloat(), colorRnd.nextFloat(), colorRnd.nextFloat(), 0.65F);
				LightingUtil.INSTANCE.setLighting(255);
			}
			MODEL3.renderFruitAspects();
			if(aspect != null) {
				LightingUtil.INSTANCE.revert();
			}
			GL11.glColor4f(1, 1, 1, 1);
			MODEL3.renderPlant();
			break;
		case 5:
		default:
			if(aspect != null) {
				GL11.glColor4f(colorRnd.nextFloat(), colorRnd.nextFloat(), colorRnd.nextFloat(), 0.65F);
				LightingUtil.INSTANCE.setLighting(255);
			}
			MODEL4.renderFruitAspects();
			if(aspect != null) {
				LightingUtil.INSTANCE.revert();
			}
			GL11.glColor4f(1, 1, 1, 1);
			MODEL4.renderPlant();
			break;
		case 6:
			//Decay
			MODEL4.renderPlant();
			break;
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
}
