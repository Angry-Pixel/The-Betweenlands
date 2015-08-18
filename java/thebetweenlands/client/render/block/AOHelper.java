package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class AOHelper {
	///// FFS /////
	public static enum Face {
		TOP(0, 1, 0), BOTTOM(0, -1, 0), NORTH(0, 0, -1), SOUTH(0, 0, 1), WEST(-1, 0, 0), EAST(1, 0, 0),
		TOP_NORTH(0, 1, -1), TOP_SOUTH(0, 1, 1), TOP_WEST(-1, 1, 0), TOP_EAST(1, 1, 0),
		BOTTOM_NORTH(0, 1, -1), BOTTOM_SOUTH(0, 1, 1), BOTTOM_WEST(-1, 1, 0), BOTTOM_EAST(1, 1, 0),
		NORTH_EAST(1, 0, -1), NORTH_WEST(-1, 0, -1),
		SOUTH_EAST(1, 0, 1), SOUTH_WEST(-1, 0, 1);

		Face(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		int x; int y; int z;

		public int getX(int x) {
			return x + this.x;
		}

		public int getY(int x) {
			return y + this.y;
		}

		public int getZ(int x) {
			return z + this.z;
		}
	}

	///// FUCK THIS SHIT /////
	
	public static float getBrightness(IBlockAccess blockAccess, int x, int y, int z, double dx, double dy, double dz) {
		Block centerBlock = blockAccess.getBlock(x, y, z);
		int centerBlockBrightness = centerBlock.getMixedBrightnessForBlock(blockAccess, x, y, z);
		float br000 = getCornerLight(blockAccess, x, y, z);
		float br100 = getCornerLight(blockAccess, x+1, y, z);
		float br010 = getCornerLight(blockAccess, x, y, z+1);
		float br110 = getCornerLight(blockAccess, x+1, y, z);
		float br001 = getCornerLight(blockAccess, x, y+1, z);
		float br101 = getCornerLight(blockAccess, x+1, y+1, z);
		float br011 = getCornerLight(blockAccess, x, y+1, z+1);
		float br111 = getCornerLight(blockAccess, x+1, y+1, z+1);
		return triLerp((float)dx, (float)dy, (float)dz, br000, br001, br010, br011, br100, br101, br110, br111, 0, 1, 0, 1, 0, 1) * 1.5F;
	}

	private static float lerp(float x, float x1, float x2, float q00, float q01) {
		return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
	}

	private static float biLerp(float x, float y, float q11, float q12, float q21, float q22, float x1, float x2, float y1, float y2) {
		float r1 = lerp(x, x1, x2, q11, q21);
		float r2 = lerp(x, x1, x2, q12, q22);

		return lerp(y, y1, y2, r1, r2);
	}

	private static float triLerp(float x, float y, float z, float q000, float q001, float q010, float q011, float q100, float q101, float q110, float q111, float x1, float x2, float y1, float y2, float z1, float z2) {
		float x00 = lerp(x, x1, x2, q000, q100);
		float x10 = lerp(x, x1, x2, q010, q110);
		float x01 = lerp(x, x1, x2, q001, q101);
		float x11 = lerp(x, x1, x2, q011, q111);
		float r0 = lerp(y, y1, y2, x00, x01);
		float r1 = lerp(y, y1, y2, x10, x11);

		return lerp(z, z1, z2, r0, r1);
	}

	///// FUCK THAT SHIT /////
	 
	private static float getCornerLight(IBlockAccess blockAccess, int x, int y, int z) {
		float lightValue = 0.0F;
		float values = 1.0F;
		for(int xo = -1; xo < 1; xo++) {
			for(int zo = -1; zo < 1; zo++) {
				for(int yo = -1; yo < 1; yo++) {
					Block block = blockAccess.getBlock(x+xo, y+yo, z+zo);
					int brightness = block.getMixedBrightnessForBlock(blockAccess, x+xo, y+yo, z+zo);
					lightValue += brightness;
					if(!block.isOpaqueCube() || brightness > 0) {
						values += 1.0F;
					}
				}
			}
		}
		return lightValue / values / 15.0F;
	}

	///// AND FUCK THAT SHIT TOO /////
	
	private static float getCornerAO(IBlockAccess blockAccess, int x, int y, int z) {
		float aoVal = 0.0F;
		int values = 0;
		for(int xo = 0; xo > -1; xo--) {
			for(int zo = 0; zo > -1; zo--) {
				for(int yo = 0; yo < 1; yo++) {
					Block block = blockAccess.getBlock(x+xo, y+yo, z+zo);
					aoVal += block.getAmbientOcclusionLightValue();
					values++;
				}
			}
		}
		return aoVal / values;
	}
}
