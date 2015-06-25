package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.ModelConverter;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockSlopeRenderer implements ISimpleBlockRenderingHandler {
	private Block currentBlock = null;
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
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
		
		//TODO: Implement multiple icons
		IIcon icon = block.getIcon(world, x, y, z, 0);
	
		int meta = world.getBlockMetadata(x, y, z);
		int metaFront = -1;
		int metaBack = -1;
		int metaLeft = -1;
		int metaRight = -1;
		
		//TODO: Implement upside down renderer
		
		//x, z
		//0, 0
		float cornerHeight1 = 0.2F;
		//1, 0
		float cornerHeight2 = 0.2F;
		//1, 1
		float cornerHeight3 = 0.2F;
		//0, 1
		float cornerHeight4 = 0.2F;
		
		switch(meta) {
		case 0:
		case 4:
			//b +x f -x
			cornerHeight2 = 1.0F;
			cornerHeight3 = 1.0F;
			metaFront = this.getBlockMeta(world, x-1, y, z);
			metaBack = this.getBlockMeta(world, x+1, y, z);
			metaLeft = this.getBlockMeta(world, x, y, z-1);
			metaRight = this.getBlockMeta(world, x, y, z+1);
			if((metaBack == 2 || metaBack == 6) && metaLeft != 0 && metaLeft != 4) {
				cornerHeight2 = 0.2F;
			} else if((metaBack == 3 || metaBack == 7) && metaRight != 0 && metaLeft != 4) {
				cornerHeight3 = 0.2F;
			}
			if((metaFront == 2 || metaFront == 6) && metaRight != 0 && metaLeft != 4) {
				cornerHeight4 = 1.0F;
			} else if((metaFront == 3 || metaFront == 7) && metaLeft != 0 && metaLeft != 4) {
				cornerHeight1 = 1.0F;
			}
			break;
		case 1:
		case 5:
			//b -x f +x
			cornerHeight1 = 1.0F;
			cornerHeight4 = 1.0F;
			metaFront = this.getBlockMeta(world, x+1, y, z);
			metaBack = this.getBlockMeta(world, x-1, y, z);
			metaLeft = this.getBlockMeta(world, x, y, z+1);
			metaRight = this.getBlockMeta(world, x, y, z-1);
			if((metaBack == 3 || metaBack == 7) && metaLeft != 1 && metaRight != 5) {
				cornerHeight4 = 0.2F;
			} else if((metaBack == 2 || metaBack == 6) && metaRight != 1 && metaRight != 5) {
				cornerHeight1 = 0.2F;
			}
			if((metaFront == 3 || metaFront == 7) && metaRight != 1 && metaRight != 5) {
				cornerHeight2 = 1.0F;
			} else if((metaFront == 2 || metaFront == 6) && metaLeft != 1 && metaRight != 5) {
				cornerHeight3 = 1.0F;
			}
			break;
		case 2:
		case 6:
			//b +z f -z
			cornerHeight3 = 1.0F;
			cornerHeight4 = 1.0F;
			metaFront = this.getBlockMeta(world, x, y, z-1);
			metaBack = this.getBlockMeta(world, x, y, z+1);
			metaLeft = this.getBlockMeta(world, x+1, y, z);
			metaRight = this.getBlockMeta(world, x-1, y, z);
			if((metaBack == 0 || metaBack == 4) && metaRight != 2 && metaRight != 6) {
				cornerHeight4 = 0.2F;
			} else if((metaBack == 1 || metaBack == 5) && metaLeft != 2 && metaRight != 6) {
				cornerHeight3 = 0.2F;
			}
			if((metaFront == 0 || metaFront == 4) && metaLeft != 2 && metaRight != 6) {
				cornerHeight2 = 1.0F;
			} else if((metaFront == 1 || metaFront == 5) && metaRight != 2 && metaRight != 6) {
				cornerHeight1 = 1.0F;
			}
			break;
		case 3:
		case 7:
			//b -z f +z
			cornerHeight1 = 1.0F;
			cornerHeight2 = 1.0F;
			metaFront = this.getBlockMeta(world, x, y, z+1);
			metaBack = this.getBlockMeta(world, x, y, z-1);
			metaLeft = this.getBlockMeta(world, x-1, y, z);
			metaRight = this.getBlockMeta(world, x+1, y, z);
			if((metaBack == 0 || metaBack == 4) && metaLeft != 3 && metaRight != 7) {
				cornerHeight1 = 0.2F;
			} else if((metaBack == 1 || metaBack == 5) && metaRight != 3 && metaRight != 7) {
				cornerHeight2 = 0.2F;
			}
			if((metaFront == 0 || metaFront == 4) && metaRight != 3 && metaRight != 7) {
				cornerHeight3 = 1.0F;
			} else if((metaFront == 1 || metaFront == 5) && metaLeft != 3 && metaRight != 7) {
				cornerHeight4 = 1.0F;
			}
			break;
		}
		
		//z- face
		tessellator.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
		tessellator.addVertexWithUV(0, cornerHeight1, 0, icon.getMinU(), icon.getInterpolatedV(cornerHeight1*16.0F));
		tessellator.addVertexWithUV(1, cornerHeight2, 0, icon.getMaxU(), icon.getInterpolatedV(cornerHeight2*16.0F));
		tessellator.addVertexWithUV(1, 0, 0, icon.getMaxU(), icon.getMinV());
		
		//z+ face
		tessellator.addVertexWithUV(0, 0, 1, icon.getMinU(), icon.getMinV());
		tessellator.addVertexWithUV(1, 0, 1, icon.getMaxU(), icon.getMinV());
		tessellator.addVertexWithUV(1, cornerHeight3, 1, icon.getMaxU(), icon.getInterpolatedV(cornerHeight3*16.0F));
		tessellator.addVertexWithUV(0, cornerHeight4, 1, icon.getMinU(), icon.getInterpolatedV(cornerHeight4*16.0F));
		
		//x- face
		tessellator.addVertexWithUV(1, 0, 0, icon.getMinU(), icon.getMinV());
		tessellator.addVertexWithUV(1, cornerHeight2, 0, icon.getMinU(), icon.getInterpolatedV(cornerHeight2*16.0F));
		tessellator.addVertexWithUV(1, cornerHeight3, 1, icon.getMaxU(), icon.getInterpolatedV(cornerHeight3*16.0F));
		tessellator.addVertexWithUV(1, 0, 1, icon.getMaxU(), icon.getMinV());
		
		//x+ face
		tessellator.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
		tessellator.addVertexWithUV(0, 0, 1, icon.getMaxU(), icon.getMinV());
		tessellator.addVertexWithUV(0, cornerHeight4, 1, icon.getMaxU(), icon.getInterpolatedV(cornerHeight4*16.0F));
		tessellator.addVertexWithUV(0, cornerHeight1, 0, icon.getMinU(), icon.getInterpolatedV(cornerHeight1*16.0F));
		
		//top face
		if((cornerHeight2 != 0.2F || cornerHeight4 != 0.2F) && (cornerHeight1 == 0.2F && cornerHeight3 == 0.2F) || (cornerHeight1 == cornerHeight3)) {
			tessellator.addVertexWithUV(0, cornerHeight4, 1, icon.getMaxU(), icon.getMinV());
			tessellator.addVertexWithUV(1, cornerHeight3, 1, icon.getMaxU(), icon.getMaxV());
			tessellator.addVertexWithUV(1, cornerHeight2, 0, icon.getMinU(), icon.getMaxV());
			tessellator.addVertexWithUV(0, cornerHeight1, 0, icon.getMinU(), icon.getMinV());
		} else {
			tessellator.addVertexWithUV(0, cornerHeight1, 0, icon.getMinU(), icon.getMinV());
			tessellator.addVertexWithUV(0, cornerHeight4, 1, icon.getMaxU(), icon.getMinV());
			tessellator.addVertexWithUV(1, cornerHeight3, 1, icon.getMaxU(), icon.getMaxV());
			tessellator.addVertexWithUV(1, cornerHeight2, 0, icon.getMinU(), icon.getMaxV());
		}
		
		//bottom face
		tessellator.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
		tessellator.addVertexWithUV(1, 0, 0, icon.getMinU(), icon.getMaxV());
		tessellator.addVertexWithUV(1, 0, 1, icon.getMaxU(), icon.getMaxV());
		tessellator.addVertexWithUV(0, 0, 1, icon.getMaxU(), icon.getMinV());
		
		tessellator.addTranslation(-x, -y, -z);
		
		return true;
	}

	public int getBlockMeta(IBlockAccess world, int x, int y, int z) {
		if(world.getBlock(x, y, z) == this.currentBlock) {
			return world.getBlockMetadata(x, y, z);
		}
		return -1;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.BlockRenderIDs.SLOPE.id();
	}
}
