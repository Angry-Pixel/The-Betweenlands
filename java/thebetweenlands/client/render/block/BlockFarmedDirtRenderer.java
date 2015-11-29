package thebetweenlands.client.render.block;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.proxy.ClientProxy;

public class BlockFarmedDirtRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		renderer.setRenderBounds(0D, -0.1D, 0D, 1D, 0.9D, 1D);

		Tessellator.instance.setColorOpaque(255, 255, 255);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			Tessellator.instance.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
		Tessellator.instance.startDrawingQuads();
		BlockRenderHelper.renderSimpleBlock(block, metadata, renderer);
		if(metadata != BlockFarmedDirt.PURE_SWAMP_DIRT) {
			Tessellator.instance.setNormal(0, 1, 0);
			renderer.renderFaceYPos(block, 0D, 0D, 0D, BLBlockRegistry.farmedDirt.getOverlayIcon(0, metadata));
		}
		Tessellator.instance.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);

		Tessellator tessellator = Tessellator.instance;

		renderer.renderStandardBlock(block, x, y, z);

		renderer.renderMinY = 0.9999D;

		for(int xo = -1; xo <= 1; xo++) {
			for(int zo = -1; zo <= 1; zo++) {
				renderer.renderMaxY = 1.002D;
				renderer.uvRotateTop = 0;
				int piece = 0;
				boolean render = false;
				if((xo != 0 && zo == 0) || (xo == 0 && zo != 0)) {
					//Adjacent neighbour
					piece = 1;
					if(xo == -1) {
						renderer.uvRotateTop = 0;
					} else if (xo == 1){
						renderer.uvRotateTop = 3;
					} else if(zo == -1) {
						renderer.uvRotateTop = 1;
					} else if (zo == 1){
						renderer.uvRotateTop = 2;
					}
					renderer.renderMaxY = 1.004D;
					if(world.getBlock(x+xo, y, z+zo) == BLBlockRegistry.farmedDirt && world.getBlockMetadata(x+xo, y, z+zo) == meta) render = true;
				} else if(xo != 0 && zo != 0) {
					//Diagonal neighbour
					if(world.getBlock(x+xo, y, z) == BLBlockRegistry.farmedDirt && world.getBlock(x, y, z+zo) == BLBlockRegistry.farmedDirt
							&& world.getBlockMetadata(x+xo, y, z) == meta && world.getBlockMetadata(x, y, z+zo) == meta) {
						if(xo == 1 && zo == -1) {
							renderer.uvRotateTop = 1;
						} else if(xo == 1 && zo == 1) {
							renderer.uvRotateTop = 3;
						} else if(xo == -1 && zo == 1) {
							renderer.uvRotateTop = 2;
						}
						renderer.renderMaxY = 1.006D;
						if(world.getBlock(x+xo, y, z+zo) == BLBlockRegistry.farmedDirt && world.getBlockMetadata(x+xo, y, z+zo) == meta) {
							//Full sharp corner
							piece = 2;
							render = true;
						} else {
							//Smooth half corner
							piece = 3;
							render = true;
						}

					}
				} else if(xo == 0 && zo == 0 && world.getBlockMetadata(x, y, z) != BlockFarmedDirt.PURE_SWAMP_DIRT) {
					//Center hole
					render = true;
				}
				if(render) {
					renderer.overrideBlockTexture = BLBlockRegistry.farmedDirt.getOverlayIcon(piece, meta);
					renderer.renderStandardBlock(block, x, y, z);
					renderer.overrideBlockTexture = null;
				}
			}
			renderer.uvRotateTop = 0;
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.BlockRenderIDs.FARMED_DIRT.id();
	}
}
