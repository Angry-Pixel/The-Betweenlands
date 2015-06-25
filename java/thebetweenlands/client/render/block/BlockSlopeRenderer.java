package thebetweenlands.client.render.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.ModelConverter;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockSlopeRenderer implements ISimpleBlockRenderingHandler {
	private Block currentBlock = null;
	private float slopeEdge = 1.0F / 16.0F * 3.0F;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		Minecraft mc = Minecraft.getMinecraft();
		World world = Minecraft.getMinecraft().theWorld;
		if(world != null && mc.thePlayer != null) {
			Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.startDrawingQuads();
		Tessellator.instance.addTranslation(0, -0.05F, 0);
		IIcon iconXP = block.getIcon(5, 0);
		IIcon iconXN = block.getIcon(4, 0);
		IIcon iconYP = block.getIcon(1, 0);
		IIcon iconYN = block.getIcon(0, 0);
		IIcon iconZP = block.getIcon(3, 0);
		IIcon iconZN = block.getIcon(2, 0);
		this.renderSlope(true, false, false, true, false, iconXP, iconXN, iconYP, iconYN, iconZP, iconZN);
		Tessellator.instance.addTranslation(0, 0.05F, 0);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(world == null) {
			return false;
		}

		this.currentBlock = block;

		Tessellator tessellator = Tessellator.instance;
		tessellator.setColorRGBA_F(1,1,1,1);
		tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		tessellator.addTranslation(x, y, z);

		int meta = world.getBlockMetadata(x, y, z);
		int metaFront = -1;
		int metaBack = -1;
		int metaLeft = -1;
		int metaRight = -1;

		//x, z
		//0, 0
		boolean corner1 = false;
		//1, 0
		boolean corner2 = false;
		//1, 1
		boolean corner3 = false;
		//0, 1
		boolean corner4 = false;

		boolean upsidedown = false;

		switch(meta) {
		case 4:
			upsidedown = true;
		case 0:
			//b +x f -x
			corner2 = true;
			corner3 = true;
			metaFront = this.getBlockMeta(world, x-1, y, z);
			metaBack = this.getBlockMeta(world, x+1, y, z);
			metaLeft = this.getBlockMeta(world, x, y, z-1);
			metaRight = this.getBlockMeta(world, x, y, z+1);
			if((metaBack == 2 || metaBack == 6) && metaLeft != 0 && metaLeft != 4) {
				corner2 = false;
			} else if((metaBack == 3 || metaBack == 7) && metaRight != 0 && metaRight != 4) {
				corner3 = false;
			}
			if((metaFront == 2 || metaFront == 6) && metaRight != 0 && metaRight != 4) {
				corner4 = true;
			} else if((metaFront == 3 || metaFront == 7) && metaLeft != 0 && metaLeft != 4) {
				corner1 = true;
			}
			break;
		case 5:
			upsidedown = true;
		case 1:
			//b -x f +x
			corner1 = true;
			corner4 = true;
			metaFront = this.getBlockMeta(world, x+1, y, z);
			metaBack = this.getBlockMeta(world, x-1, y, z);
			metaLeft = this.getBlockMeta(world, x, y, z+1);
			metaRight = this.getBlockMeta(world, x, y, z-1);
			if((metaBack == 3 || metaBack == 7) && metaLeft != 1 && metaLeft != 5) {
				corner4 = false;
			} else if((metaBack == 2 || metaBack == 6) && metaRight != 1 && metaRight != 5) {
				corner1 = false;
			}
			if((metaFront == 3 || metaFront == 7) && metaRight != 1 && metaRight != 5) {
				corner2 = true;
			} else if((metaFront == 2 || metaFront == 6) && metaLeft != 1 && metaLeft != 5) {
				corner3 = true;
			}
			break;
		case 6:
			upsidedown = true;
		case 2:
			//b +z f -z
			corner3 = true;
			corner4 = true;
			metaFront = this.getBlockMeta(world, x, y, z-1);
			metaBack = this.getBlockMeta(world, x, y, z+1);
			metaLeft = this.getBlockMeta(world, x+1, y, z);
			metaRight = this.getBlockMeta(world, x-1, y, z);
			if((metaBack == 0 || metaBack == 4) && metaRight != 2 && metaRight != 6) {
				corner4 = false;
			} else if((metaBack == 1 || metaBack == 5) && metaLeft != 2 && metaLeft != 6) {
				corner3 = false;
			}
			if((metaFront == 0 || metaFront == 4) && metaLeft != 2 && metaLeft != 6) {
				corner2 = true;
			} else if((metaFront == 1 || metaFront == 5) && metaRight != 2 && metaRight != 6) {
				corner1 = true;
			}
			break;
		case 7:
			upsidedown = true;
		case 3:
			//b -z f +z
			corner1 = true;
			corner2 = true;
			metaFront = this.getBlockMeta(world, x, y, z+1);
			metaBack = this.getBlockMeta(world, x, y, z-1);
			metaLeft = this.getBlockMeta(world, x-1, y, z);
			metaRight = this.getBlockMeta(world, x+1, y, z);
			if((metaBack == 0 || metaBack == 4) && metaLeft != 3 && metaLeft != 7) {
				corner1 = false;
			} else if((metaBack == 1 || metaBack == 5) && metaRight != 3 && metaRight != 7) {
				corner2 = false;
			}
			if((metaFront == 0 || metaFront == 4) && metaRight != 3 && metaRight != 7) {
				corner3 = true;
			} else if((metaFront == 1 || metaFront == 5) && metaLeft != 3 && metaLeft != 7) {
				corner4 = true;
			}
			break;
		}
		
		IIcon iconXP = block.getIcon(world, x, y, z, 5);
		IIcon iconXN = block.getIcon(world, x, y, z, 4);
		IIcon iconYP = block.getIcon(world, x, y, z, 1);
		IIcon iconYN = block.getIcon(world, x, y, z, 0);
		IIcon iconZP = block.getIcon(world, x, y, z, 3);
		IIcon iconZN = block.getIcon(world, x, y, z, 2);
		this.renderSlope(corner1, corner2, corner3, corner4, upsidedown, iconXP, iconXN, iconYP, iconYN, iconZP, iconZN);

		tessellator.addTranslation(-x, -y, -z);

		return true;
	}

	public void renderSlope(boolean corner1, boolean corner2, boolean corner3, boolean corner4, boolean upsidedown, IIcon iconXP, IIcon iconXN, IIcon iconYP, IIcon iconYN, IIcon iconZP, IIcon iconZN) {
		float cornerHeight1 = corner1 ? 1.0F : this.slopeEdge;
		float cornerHeight2 = corner2 ? 1.0F : this.slopeEdge;
		float cornerHeight3 = corner3 ? 1.0F : this.slopeEdge;
		float cornerHeight4 = corner4 ? 1.0F : this.slopeEdge;

		Tessellator tessellator = Tessellator.instance;

		if(!upsidedown) {
			//z- face
			tessellator.addVertexWithUV(0, 0, 0, iconZN.getMinU(), iconZN.getMinV());
			tessellator.addVertexWithUV(0, cornerHeight1, 0, iconZN.getMinU(), iconZN.getInterpolatedV(cornerHeight1*16.0F));
			tessellator.addVertexWithUV(1, cornerHeight2, 0, iconZN.getMaxU(), iconZN.getInterpolatedV(cornerHeight2*16.0F));
			tessellator.addVertexWithUV(1, 0, 0, iconZN.getMaxU(), iconZN.getMinV());

			//z+ face
			tessellator.addVertexWithUV(0, 0, 1, iconZP.getMinU(), iconZP.getMinV());
			tessellator.addVertexWithUV(1, 0, 1, iconZP.getMaxU(), iconZP.getMinV());
			tessellator.addVertexWithUV(1, cornerHeight3, 1, iconZP.getMaxU(), iconZP.getInterpolatedV(cornerHeight3*16.0F));
			tessellator.addVertexWithUV(0, cornerHeight4, 1, iconZP.getMinU(), iconZP.getInterpolatedV(cornerHeight4*16.0F));

			//x- face
			tessellator.addVertexWithUV(1, 0, 0, iconXN.getMinU(), iconXN.getMinV());
			tessellator.addVertexWithUV(1, cornerHeight2, 0, iconXN.getMinU(), iconXN.getInterpolatedV(cornerHeight2*16.0F));
			tessellator.addVertexWithUV(1, cornerHeight3, 1, iconXN.getMaxU(), iconXN.getInterpolatedV(cornerHeight3*16.0F));
			tessellator.addVertexWithUV(1, 0, 1, iconXN.getMaxU(), iconXN.getMinV());

			//x+ face
			tessellator.addVertexWithUV(0, 0, 0, iconXP.getMinU(), iconXP.getMinV());
			tessellator.addVertexWithUV(0, 0, 1, iconXP.getMaxU(), iconXP.getMinV());
			tessellator.addVertexWithUV(0, cornerHeight4, 1, iconXP.getMaxU(), iconXP.getInterpolatedV(cornerHeight4*16.0F));
			tessellator.addVertexWithUV(0, cornerHeight1, 0, iconXP.getMinU(), iconXP.getInterpolatedV(cornerHeight1*16.0F));

			//top face
			if((corner2 || corner4) && (!corner1 && !corner3) || (corner1 == corner3)) {
				tessellator.addVertexWithUV(0, cornerHeight4, 1, iconYP.getMaxU(), iconYP.getMinV());
				tessellator.addVertexWithUV(1, cornerHeight3, 1, iconYP.getMaxU(), iconYP.getMaxV());
				tessellator.addVertexWithUV(1, cornerHeight2, 0, iconYP.getMinU(), iconYP.getMaxV());
				tessellator.addVertexWithUV(0, cornerHeight1, 0, iconYP.getMinU(), iconYP.getMinV());
			} else {
				tessellator.addVertexWithUV(0, cornerHeight1, 0, iconYP.getMinU(), iconYP.getMinV());
				tessellator.addVertexWithUV(0, cornerHeight4, 1, iconYP.getMaxU(), iconYP.getMinV());
				tessellator.addVertexWithUV(1, cornerHeight3, 1, iconYP.getMaxU(), iconYP.getMaxV());
				tessellator.addVertexWithUV(1, cornerHeight2, 0, iconYP.getMinU(), iconYP.getMaxV());
			}

			//bottom face
			tessellator.addVertexWithUV(0, 0, 0, iconYN.getMinU(), iconYN.getMinV());
			tessellator.addVertexWithUV(1, 0, 0, iconYN.getMinU(), iconYN.getMaxV());
			tessellator.addVertexWithUV(1, 0, 1, iconYN.getMaxU(), iconYN.getMaxV());
			tessellator.addVertexWithUV(0, 0, 1, iconYN.getMaxU(), iconYN.getMinV());
		} else {
			//z- face
			tessellator.addVertexWithUV(0, 1, 0, iconZN.getMinU(), iconZN.getMinV());
			tessellator.addVertexWithUV(1, 1, 0, iconZN.getMaxU(), iconZN.getMinV());
			tessellator.addVertexWithUV(1, 1-cornerHeight2, 0, iconZN.getMaxU(), iconZN.getInterpolatedV(cornerHeight2*16.0F));
			tessellator.addVertexWithUV(0, 1-cornerHeight1, 0, iconZN.getMinU(), iconZN.getInterpolatedV(cornerHeight1*16.0F));

			//z+ face
			tessellator.addVertexWithUV(0, 1, 1, iconZP.getMinU(), iconZP.getMinV());
			tessellator.addVertexWithUV(0, 1-cornerHeight4, 1, iconZP.getMinU(), iconZP.getInterpolatedV(cornerHeight4*16.0F));
			tessellator.addVertexWithUV(1, 1-cornerHeight3, 1, iconZP.getMaxU(), iconZP.getInterpolatedV(cornerHeight3*16.0F));
			tessellator.addVertexWithUV(1, 1, 1, iconZP.getMaxU(), iconZP.getMinV());

			//x- face
			tessellator.addVertexWithUV(1, 1, 0, iconXN.getMinU(), iconXN.getMinV());
			tessellator.addVertexWithUV(1, 1, 1, iconXN.getMaxU(), iconXN.getMinV());
			tessellator.addVertexWithUV(1, 1-cornerHeight3, 1, iconXN.getMaxU(), iconXN.getInterpolatedV(cornerHeight3*16.0F));
			tessellator.addVertexWithUV(1, 1-cornerHeight2, 0, iconXN.getMinU(), iconXN.getInterpolatedV(cornerHeight2*16.0F));

			//x+ face
			tessellator.addVertexWithUV(0, 1, 0, iconXP.getMinU(), iconXP.getMinV());
			tessellator.addVertexWithUV(0, 1-cornerHeight1, 0, iconXP.getMinU(), iconXP.getInterpolatedV(cornerHeight1*16.0F));
			tessellator.addVertexWithUV(0, 1-cornerHeight4, 1, iconXP.getMaxU(), iconXP.getInterpolatedV(cornerHeight4*16.0F));
			tessellator.addVertexWithUV(0, 1, 1, iconXP.getMaxU(), iconXP.getMinV());

			//bottom face
			if((corner2 || corner4) && (!corner1 && !corner3) || (corner1 == corner3)) {
				tessellator.addVertexWithUV(0, 1-cornerHeight4, 1, iconYP.getMaxU(), iconYP.getMinV());
				tessellator.addVertexWithUV(0, 1-cornerHeight1, 0, iconYP.getMinU(), iconYP.getMinV());
				tessellator.addVertexWithUV(1, 1-cornerHeight2, 0, iconYP.getMinU(), iconYP.getMaxV());
				tessellator.addVertexWithUV(1, 1-cornerHeight3, 1, iconYP.getMaxU(), iconYP.getMaxV());
			} else {
				tessellator.addVertexWithUV(0, 1-cornerHeight1, 0, iconYP.getMinU(), iconYP.getMinV());
				tessellator.addVertexWithUV(1, 1-cornerHeight2, 0, iconYP.getMinU(), iconYP.getMaxV());
				tessellator.addVertexWithUV(1, 1-cornerHeight3, 1, iconYP.getMaxU(), iconYP.getMaxV());
				tessellator.addVertexWithUV(0, 1-cornerHeight4, 1, iconYP.getMaxU(), iconYP.getMinV());
			}

			//top face
			tessellator.addVertexWithUV(0, 1, 0, iconYN.getMinU(), iconYN.getMinV());
			tessellator.addVertexWithUV(0, 1, 1, iconYN.getMaxU(), iconYN.getMinV());
			tessellator.addVertexWithUV(1, 1, 1, iconYN.getMaxU(), iconYN.getMaxV());
			tessellator.addVertexWithUV(1, 1, 0, iconYN.getMinU(), iconYN.getMaxV());
		}
	}

	public int getBlockMeta(IBlockAccess world, int x, int y, int z) {
		if(world.getBlock(x, y, z) == this.currentBlock) {
			return world.getBlockMetadata(x, y, z);
		}
		return -1;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.BlockRenderIDs.SLOPE.id();
	}
}
