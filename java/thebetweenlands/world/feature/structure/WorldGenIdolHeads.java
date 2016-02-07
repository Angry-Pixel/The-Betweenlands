package thebetweenlands.world.feature.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;

public class WorldGenIdolHeads extends WorldGenerator {

    private int length = -1;
    private int width = -1;
    private int height = -1;
    private int direction = -1;
    private Block solid = BLBlockRegistry.smoothCragrock;
    private Block slab = BLBlockRegistry.smoothCragrockSlab;
    private Block stairs = BLBlockRegistry.smoothCragrockStairs;
    private Block octine = BLBlockRegistry.octineBlock;

    public WorldGenIdolHeads() {
        length = 8;
        width = 8;
        height = 8;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return generateStructure(world, random, x, y, z);
    }

    public boolean generateStructure(World world, Random rand, int x, int y, int z) {
        // air check
		for (int xx = x - length / 2; xx <= x + length / 2; ++xx) {
            for(int zz = z - width / 2; zz <= z + width / 2; ++zz) {
                for(int yy = y + 1; yy < y + height; ++yy ) {
                    if(!world.isAirBlock(xx, yy, zz)) {
                        return false;
                    }
                }
            }
        }
		direction = rand.nextInt(4);
        int headType = rand.nextInt(3);
        int xx = x - length / 2;
        int zz = z - width / 2;

        switch (headType) {
	        case 0:// Gold Head
	        	rotatedCubeVolume(world, rand, xx, y, zz, 1, 0, 2, solid, 0, 6, 4, 5, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 0, 3, 4, solid, 0, 1, 2, 2, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 7, 3, 4, solid, 0, 1, 2, 2, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 1, 5, 1, solid, 0, 6, 2, 6, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 3, solid, 0, 6, 1, 4, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 3, 3, 0, solid, 0, 2, 4, 3, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 2, 3, 1, solid, 0, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 5, 3, 1, solid, 0, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 1, solid, 0, 2, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 3, solid, 0, 2, 1, 2, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 6, solid, 0, 2, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 3, 6, 7, solid, 0, 2, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 3, 4, 7, solid, 0, 2, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 2, 4, 3, octine, 0, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 5, 4, 3, octine, 0, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 2, 1, 1, slab, 0, 4, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 2, 2, 1, slab, 8, 4, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, xx, y, zz, 1, 1, 1, stairs, direction == 0 ? 1 : direction == 2 ? 0 : direction == 1 ? 3 : 2, 1, 1, 1, direction); //bottom right
	        	rotatedCubeVolume(world, rand, xx, y, zz, 6, 1, 1, stairs, direction == 0 ? 0 : direction == 2 ? 1 : direction == 1 ? 2 : 3, 1, 1, 1, direction); //bottom left
	        	rotatedCubeVolume(world, rand, xx, y, zz, 1, 2, 1, stairs, direction == 0 ? 5 : direction == 2 ? 4 : direction == 1 ? 7 : 6, 1, 1, 1, direction); //top right
	        	rotatedCubeVolume(world, rand, xx, y, zz, 6, 2, 1, stairs, direction == 0 ? 4 : direction == 2 ? 5 : direction == 1 ? 6 : 7, 1, 1, 1, direction); //top left
	        	break;

	        case 1:// Silver Head
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 0, 2, solid, 0, 6, 4, 5, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 0, 1, 4, solid, 0, 1, 5, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 7, 1, 4, solid, 0, 1, 5, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 1, 7, solid, 0, 2, 5, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 3, solid, 0, 6, 3, 4, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 7, 1, solid, 0, 2, 1, 6, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 6, 1, slab, 8, 2, 1, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 7, 1, slab, 0, 2, 1, 6, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 7, 1, solid, 0, 2, 1, 6, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 6, 1, slab, 8, 2, 1, 2, direction); 
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 3, 0, solid, 0, 2, 3, 3, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 6, 1, solid, 0, 2, 1, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 2, slab, 0, 2, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 4, 2, slab, 0, 2, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 3, 1, solid, 0, 4, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 5, 3, octine, 0, 1, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 5, 3, octine, 0, 1, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 1, 1, stairs, direction == 0 ? 1 : direction == 2 ? 0 : direction == 1 ? 3 : 2, 1, 1, 1, direction); //bottom right
		        rotatedCubeVolume(world, rand, xx, y, zz, 4, 1, 1, stairs, direction == 0 ? 0 : direction == 2 ? 1 : direction == 1 ? 2 : 3, 1, 1, 1, direction); //bottom left
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 2, 1, stairs, direction == 0 ? 5 : direction == 2 ? 4 : direction == 1 ? 7 : 6, 1, 1, 1, direction); //top right
		        rotatedCubeVolume(world, rand, xx, y, zz, 4, 2, 1, stairs, direction == 0 ? 4 : direction == 2 ? 5 : direction == 1 ? 6 : 7, 1, 1, 1, direction); //top left
		        break;

	        case 2://Bronze Head
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 0, 2, solid, 0, 6, 4, 5, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 0, 1, 4, solid, 0, 1, 5, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 7, 1, 4, solid, 0, 1, 5, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 2, 7, solid, 0, 4, 4, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 6, 1, solid, 0, 6, 1, 6, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 7, 1, solid, 0, 4, 1, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 7, 4, solid, 0, 4, 1, 2, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 3, solid, 0, 6, 2, 4, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 3, 0, solid, 0, 2, 4, 3, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 1, 4, 2, slab, 0, 2, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 4, 2, slab, 0, 2, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 3, 1, solid, 0, 4, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 5, 3, octine, 0, 1, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 5, 3, octine, 0, 1, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 1, 1, slab, 0, 2, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 3, 2, 1, slab, 8, 2, 1, 1, direction);
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 1, 1, stairs, direction == 0 ? 1 : direction == 2 ? 0 : direction == 1 ? 3 : 2, 1, 1, 1, direction); //bottom right
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 1, 1, stairs, direction == 0 ? 0 : direction == 2 ? 1 : direction == 1 ? 2 : 3, 1, 1, 1, direction); //bottom left
		        rotatedCubeVolume(world, rand, xx, y, zz, 2, 2, 1, stairs, direction == 0 ? 5 : direction == 2 ? 4 : direction == 1 ? 7 : 6, 1, 1, 1, direction); //top right
		        rotatedCubeVolume(world, rand, xx, y, zz, 5, 2, 1, stairs, direction == 0 ? 4 : direction == 2 ? 5 : direction == 1 ? 6 : 7, 1, 1, 1, direction); //top left
		        break;
        }
        return true;
    }

    public void rotatedCubeVolume(World world, Random rand, int x, int y, int z, int offsetA, int offsetB, int offsetC, Block blockType, int blockMeta, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
        switch (direction) {
            case 0:
            	for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
            		for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
            			for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++)
            				world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 1:
            	for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
            		for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
            			for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++)
            				world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 2:
            	for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
            		for (int xx = x + length - offsetA - 1; xx > x + length - offsetA - sizeWidth - 1; xx--)
            			for (int zz = z + length - offsetC - 1; zz > z + length - offsetC - sizeDepth - 1; zz--)
            				world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
            case 3:
            	for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
            		for (int zz = z + length - offsetA - 1; zz > z + length - offsetA - sizeWidth - 1; zz--)
            			for (int xx = x + length - offsetC - 1; xx > x + length - offsetC - sizeDepth - 1; xx--)
            				world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
        }
    }

}
