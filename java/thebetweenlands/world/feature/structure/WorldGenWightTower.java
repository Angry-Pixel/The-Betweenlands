package thebetweenlands.world.feature.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLSpawner;

public class WorldGenWightTower extends WorldGenerator {

    private int length = -1;
    private int width = -1;
    private int height = -1;
    private int direction = -1;

    private Block limestonePolished = BLBlockRegistry.polishedLimestone;
    private Block limestoneChiselled = BLBlockRegistry.chiseledLimestone;
    private Block betweenstone = BLBlockRegistry.betweenstone;
    private Block betweenstoneSmooth = BLBlockRegistry.smoothBetweenstone;
    private Block betweenstoneTiles = BLBlockRegistry.betweenstoneTiles;
    private Block betweenstoneBrickStairs = BLBlockRegistry.betweenstoneBrickStairs;
    private Block betweenstoneBrickSlab = BLBlockRegistry.betweenstoneBrickSlab;
    private Block betweenstoneBrickWall = BLBlockRegistry.betweenstoneBrickWall;
    private Block betweenstoneBricks = BLBlockRegistry.betweenstoneBricks;
    private Block betweenstonePillar = BLBlockRegistry.betweenstonePillar;
    private Block betweenstoneStairsSmooth = BLBlockRegistry.smoothBetweenstoneStairs;
    private Block silverCircleBrick = BLBlockRegistry.silverCircleBrick;
    private Block peat = BLBlockRegistry.peat;

    private BlockBLSpawner spawner = BLBlockRegistry.blSpawner;
    
    public WorldGenWightTower() {
    	//these sizes are subject to change
        length = 13;
        width = 13;
        height = 19;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return generateStructure(world, random, x, y, z);
    }

    public boolean generateStructure(World world, Random rand, int xx, int yy, int zz) {
      // air just to erase old one :P
		for (int xa = xx; xa <= xx + 32; ++xa) {
            for(int za = zz; za <= zz + 32; ++za) {
                for(int ya = yy; ya < yy + 32; ++ya ) {
                	world.setBlockToAir(xa, ya, za);
                }
            }
        }
		
		//ground floors
		length = 32;
        width = 32;

		for (direction = 0; direction < 4; direction++) {
			rotatedCubeVolume(world, rand, xx, yy, zz, 0, -1, 0, betweenstoneSmooth, 0, 13, 1, 13, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, -1, 3, betweenstoneTiles, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, -1, 4, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, -1, 4, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 7, -1, 4, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, -1, 5, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, -1, 5, limestonePolished, 0, 3, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 8, -1, 5, betweenstoneTiles, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, -1, 6, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, -1, 6, limestonePolished, 0, 1, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 7, -1, 6, betweenstoneTiles, 0, 2, 1, 1, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, -1, 7, betweenstoneTiles, 0, 3, 1, 1, direction);
	//		if(rand.nextBoolean())
	//			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 2, 6, spawner, 0, 1, 1, 1, direction);
	//		spawner.setMob(world, xx + 6, yy + 2, zz + 6, "thebetweenlands.swampHag");
	//		spawner.setMob(world, xx + 25, yy + 2, zz + 6, "thebetweenlands.swampHag");
	//		spawner.setMob(world, xx + 25, yy + 2, zz + 25, "thebetweenlands.swampHag");
	//		spawner.setMob(world, xx + 6, yy + 2, zz + 25, "thebetweenlands.swampHag");

		//1st floors
			rotatedCubeVolume(world, rand, xx, yy, zz, 3, 5, 3, limestonePolished, 0, 7, 1, 7, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 5, 5, limestoneChiselled, 0, 1, 1, 3, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 5, 5, 6, limestoneChiselled, 0, 3, 1, 1, direction);
	//		if(rand.nextBoolean())
	//			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 8, 6, spawner, 0, 1, 1, 1, direction);
	//		spawner.setMob(world, xx + 6, yy + 8, zz + 6, "thebetweenlands.wight");
	//		spawner.setMob(world, xx + 25, yy + 8, zz + 6, "thebetweenlands.wight");
	//		spawner.setMob(world, xx + 25, yy + 8, zz + 25, "thebetweenlands.wight");
	//		spawner.setMob(world, xx + 6, yy + 8, zz + 25, "thebetweenlands.wight");

		//2nd floors
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, 11, 4, limestonePolished, 0, 5, 1, 5, direction);
			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 11, 6, limestoneChiselled, 0, 1, 1, 1, direction);
	//		if(rand.nextBoolean())
	//			rotatedCubeVolume(world, rand, xx, yy, zz, 6, 14, 6, spawner, 0, 1, 1, 1, direction);
	//		spawner.setMob(world, xx + 6, yy + 14, zz + 6, "thebetweenlands.termite");
	//		spawner.setMob(world, xx + 25, yy + 14, zz + 6, "thebetweenlands.termite");
	//		spawner.setMob(world, xx + 25, yy + 14, zz + 25, "thebetweenlands.termite");
	//		spawner.setMob(world, xx + 6, yy + 14, zz + 25, "thebetweenlands.termite");

		//3rd floors
			rotatedCubeVolume(world, rand, xx, yy, zz, 4, 16, 4, limestonePolished, 0, 5, 1, 5, direction);
	//		rotatedCubeVolume(world, rand, xx, yy, zz, 6, 19, 6, spawner, 0, 1, 1, 1, direction);
	//		spawner.setMob(world, xx + 6, yy + 19, zz + 6, "thebetweenlands.wight");
	//		spawner.setMob(world, xx + 25, yy + 19, zz + 6, "thebetweenlands.wight");
	//		spawner.setMob(world, xx + 25, yy + 19, zz + 25, "thebetweenlands.wight");
	//		spawner.setMob(world, xx + 6, yy + 19, zz + 25, "thebetweenlands.wight");
		}

		length = 13;
		width = 13;
		for (int tower = 0; tower  < 5; tower ++) {
			int x = xx, y = yy, z = zz;

    		if (tower == 1)
    			x = xx + 19;

    		if (tower == 2) {
    		    x = xx + 19;
    		    z = zz + 19;
    		}

    		if (tower == 3)
    		    z = zz + 19;

    		for (direction = 0; direction < 4; direction++) {
    			if(tower < 4) {
    			
    			rotatedCubeVolume(world, rand, x, y, z, 3, 2, 1, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 7, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 4, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 8, 1, 1, direction);
    			
    			
    			rotatedCubeVolume(world, rand, x, y, z, 0, 0, 1, betweenstone, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 0, 0, 3, betweenstone, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 1, 0, 0, betweenstone, 0, 1, 3, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 3, 0, 0, betweenstone, 0, 1, 3, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 5, 0, 0, betweenstone, 0, 1, 3, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 7, 0, 0, betweenstone, 0, 1, 3, 1, direction);
	        	
	        	rotatedCubeVolume(world, rand, x, y, z, 1, 0, 1, betweenstone, 0, 1, 3, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 1, 3, 1, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 0, 1, betweenstone, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 4, 0, 1, betweenstone, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 0, 1, betweenstone, 0, 1, 3, 1, direction);
    			
    			rotatedCubeVolume(world, rand, x, y, z, 2, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 4, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 0, 0, 0, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 0, 0, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 4, 4, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 4, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
    			
    			//deco walls
    			rotatedCubeVolume(world, rand, x, y, z, 0, 4, 0, betweenstoneBrickWall, 0, 9, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 0, 4, 1, betweenstoneBrickWall, 0, 1, 1, 3, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 1, 4, 1, betweenstoneBrickWall, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 3, 4, 1, betweenstoneBrickWall, 0, 1, 6, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 1, 4, 3, betweenstoneBrickWall, 0, 1, 6, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 6, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);//
    			rotatedCubeVolume(world, rand, x, y, z, 1, 6, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 7, 2, betweenstoneBrickWall, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 3, 9, 2, betweenstoneBrickWall, 0, 1, 6, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 9, 3, betweenstoneBrickWall, 0, 1, 6, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 3, 14, 3, betweenstoneBrickWall, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 3, 16, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 16, 3, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 17, 2, betweenstoneBrickWall, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 3, 17, 1, betweenstoneBrickWall, 0, 4, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 1, 17, 3, betweenstoneBrickWall, 0, 1, 1, 3, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 4, 16, 1, betweenstoneBrickWall, 0, 5, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 16, 0, betweenstoneBrickWall, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 15, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 18, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 18, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 7, 16, 0, betweenstoneBrickWall, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 7, 15, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 7, 18, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 7, 18, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 10, 2, betweenstoneBrickWall, 0, 1, 7, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 6, 14, 2, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 7, 10, 2, betweenstoneBrickWall, 0, 1, 7, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 10, 1, betweenstoneBrickWall, 0, 3, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 5, 1, betweenstoneBrickWall, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 6, 8, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 7, 5, 1, betweenstoneBrickWall, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 5, 0, betweenstoneBrickWall, 0, 3, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 4, 9, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 9, 1, betweenstoneBrickWall, 0, 1, 1, 1, direction);
    			//
    			rotatedCubeVolume(world, rand, x, y, z, 4, 3, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 4, 4, 3, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 3, 2, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 4, 3, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			
    			rotatedCubeVolume(world, rand, x, y, z, 4, 4, 4, betweenstoneBrickSlab, 8, 5, 1, 1, direction);

    			rotatedCubeVolume(world, rand, x, y, z, 1, 3, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 3, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 1, 0, 2, betweenstone, 0, 1, 3, 1, direction);
    			
    			rotatedCubeVolume(world, rand, x, y, z, 4, 4, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 4, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
    			
    			rotatedCubeVolume(world, rand, x, y, z, 2, 5, 2, betweenstoneBricks, 0, 1, 2, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 2, 5, 3, betweenstoneBricks, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 3, 5, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
    			
    			rotatedCubeVolume(world, rand, x, y, z, 2, 3, 1, betweenstoneBricks, 0, 7, 1, 1, direction);//
    			rotatedCubeVolume(world, rand, x, y, z, 1, 3, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 3, 8, 3, betweenstoneBricks, 0, 1, 6, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 4, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 8, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 6, 16, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, x, y, z, 5, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
				rotatedCubeVolume(world, rand, x, y, z, 7, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
				rotatedCubeVolume(world, rand, x, y, z, 6, 16, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
				rotatedCubeVolume(world, rand, x, y, z, 4, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
				rotatedCubeVolume(world, rand, x, y, z, 8, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
				rotatedCubeVolume(world, rand, x, y, z, 5, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
				rotatedCubeVolume(world, rand, x, y, z, 7, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
				
	        	rotatedCubeVolume(world, rand, x, y, z, 4, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 7, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 5, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 7, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);

	        	rotatedCubeVolume(world, rand, x, y, z, 6, 15, 3, silverCircleBrick, 0, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 6, 14, 3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 6, 9, 2, silverCircleBrick, 0, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 6, 8, 2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 6, 3, 0, betweenstoneTiles, 0, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);

	        	rotatedCubeVolume(world, rand, x, y, z, 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 7, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 5, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 7, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 6, 5, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 0, 3, 1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 0, 3, 3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 2, 8, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 3, 8, 2, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 4, 8, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 8, 8, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        	rotatedCubeVolume(world, rand, x, y, z, 6, 11, 4, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    		}
    		}
		}
    		for (int tower = 0; tower  < 5; tower ++) {
    			int x = xx, y = yy, z = zz;

        		if (tower == 1)
        			x = xx + 19;

        		if (tower == 2) {
        		    x = xx + 19;
        		    z = zz + 19;
        		}

        		if (tower == 3)
        		    z = zz + 19;

        		for (direction = 0; direction < 4; direction++) {
        			if(tower < 4) {
	        	
	        	//walkways
	        	if (tower == 0 && direction == 0 || tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 1 && direction == 3|| tower == 2 && direction == 2 || tower == 2 && direction == 3|| tower == 3 && direction == 1 || tower == 3 && direction == 2) {
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 4, 11, betweenstoneBricks, 0, 3, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 5, 12, betweenstoneBricks, 0, 1, 1, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 5, 12, betweenstoneBricks, 0, 1, 1, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 4, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 3, 1, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 6, 10, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 5, 11, Blocks.air, 0, 3, 5, 2, direction);
	        		
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 6, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 6, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 7, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 7, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 9, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 9, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 10, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 10, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 10, 9, betweenstoneBricks, 0, 3, 1, 7, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 11, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 11, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 12, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 12, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 13, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 13, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 15, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 15, 11, betweenstoneBricks, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 16, 10, betweenstoneBricks, 0, 3, 1, 6, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 11, 10, Blocks.air, 0, 3, 5, 2, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 17, 10, Blocks.air, 0, 3, 4, 3, direction);
	        		
	        		rotatedCubeVolume(world, rand, x, y, z, 3, 5, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 9, 5, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
	        		
	        		rotatedCubeVolume(world, rand, x, y, z, 3, 9, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 9, 9, 12, betweenstoneBrickWall, 0, 1, 1, 4, direction);
	        		
	        		rotatedCubeVolume(world, rand, x, y, z, 3, 11, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 9, 11, 11, betweenstoneBrickWall, 0, 1, 1, 5, direction);
	        		
				}

	        	//top Floor
	        	if (tower == 0 && direction == 0 || tower == 1 && direction == 3 || tower == 2 && direction == 2 || tower == 3 && direction == 1) {
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 16, 11, betweenstoneBricks, 0, 1, 3, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 7, 17, 15, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 17, 15, betweenstoneBricks, 0, 3, 1, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 9, 17, 14, betweenstoneBricks, 0, 3, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 19, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 19, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 19, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 21, 10, betweenstoneBrickSlab, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 16, 12, betweenstoneBrickWall, 0, 1, 2, 4, direction);
	        	}
	        	if (tower == 0 && direction == 1 || tower == 1 && direction == 0 || tower == 2 && direction == 3 || tower == 3 && direction == 2) {
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 16, 11, betweenstoneBricks, 0, 1, 3, 4, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 5, 17, 15, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 2, 17, 15, betweenstoneBricks, 0, 3, 1, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 1, 17, 14, betweenstoneBricks, 0, 3, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 19, 10, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 19, 12, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 19, 14, betweenstoneBrickWall, 0, 1, 2, 1, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 4, 21, 10, betweenstoneBrickSlab, 0, 1, 1, 5, direction);
	        		rotatedCubeVolume(world, rand, x, y, z, 8, 16, 12, betweenstoneBrickWall, 0, 1, 2, 4, direction);
	        	}
    		}
    			//top tower
	        	if(tower == 4) {
	        		length = 14;
	                width = 14;
	      			x = xx + 9;
	    		    z = zz + 9;
	    		    y = yy + 18;
	        		generateTopTowerRight(world, rand, x, y, z, direction);
	        		generateTopTowerLeft(world, rand, x, y, z, direction);
	        	}
    		
    	}
    		}
    		
    		length = 32;
            width = 32;
    		for (direction = 0; direction < 4; direction++) {
    			// Len's Middle stuff
    			//pillars
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 9, betweenstonePillar, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 3, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 4, 9, betweenstonePillar, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 9, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 10, 9, betweenstonePillar, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 15, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 9, betweenstonePillar, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 0, 9, betweenstonePillar, 0, 1, 3, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 3, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 4, 9, betweenstonePillar, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 9, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 10, 9, betweenstonePillar, 0, 1, 5, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 15, 9, betweenstoneTiles, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 9, betweenstonePillar, 0, 1, 1, 1, direction);
    			
    			//arches
    			// lower
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 3, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 3, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 4, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 4, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 4, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 11, 4, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 4, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 3, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 4, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 4, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
    			
    			
    			// mid
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 9, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 9, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 10, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 10, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 9, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 10, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 9, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 10, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 10, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
    			
    			// top
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 15, 9, betweenstoneBrickStairs, direction == 0 ? 5 : direction== 1 ? 6 : direction == 2 ? 4 : 7, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 15, 9, betweenstoneBrickStairs, direction == 0 ? 4 : direction== 1 ? 7 : direction == 2 ? 5 : 6, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 16, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, 16, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, 16, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 15, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 10, 16, 9, betweenstoneBricks, 0, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 12, 16, 9, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 15, 9, betweenstoneBricks, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 20, 16, 9, betweenstoneBricks, 0, 3, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 22, 15, 9, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 19, 16, 9, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 15, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 10, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 11, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, 16, 12, betweenstoneBrickSlab, 8, 1, 1, 2, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, 16, 12, betweenstoneBrickSlab, 8, 4, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 15, 10, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 10, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 11, betweenstoneBrickStairs, direction == 0 ? 7 : direction== 1 ? 5 : direction == 2 ? 6 : 4, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, 16, 12, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
    			
    			//floor
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -1, 5, betweenstoneTiles, 0, 6, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -1, 6, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -1, 7, betweenstoneStairsSmooth, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 6, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 7, betweenstoneSmooth, 0, 2, 1, 2, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 17, -1, 7, betweenstoneStairsSmooth, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 6, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 9, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 10, betweenstoneSmooth, 0, 2, 1, 3, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 13, betweenstoneStairsSmooth, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 13, -1, 6, betweenstoneTiles, 0, 1, 1, 7, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 18, -1, 6, betweenstoneTiles, 0, 1, 1, 7, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 12, -1, 12, betweenstoneTiles, 0, 1, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 14, -1, 14, betweenstoneSmooth, 0, 3, 1, 1, direction);
    			rotatedCubeVolume(world, rand, xx, yy, zz, 15, -1, 15, peat, 0, 1, 1, 1, direction);
    		
    		}
		//retro-gen betweenstoneBrickStairs
		direction = rand.nextInt(4);

        //main betweenstoneBrickStairs
		rotatedCubeVolume(world, rand, xx, yy, zz, 12, 0, 4, betweenstoneBricks, 0, 8, 4, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 3, betweenstoneBricks, 0, 6, 3, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 2, betweenstoneBricks, 0, 6, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 1, betweenstoneBricks, 0, 6, 1, 1, direction);
		
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 3, 3, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 2, 2, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 1, 1, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 0, 0, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 3, 3, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 2, 2, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 1, 1, betweenstoneBricks, 0, 1, 2, 2, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 0, 0, betweenstoneBricks, 0, 1, 2, 2, direction);

		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 5, 3, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 4, 2, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 3, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 2, 0, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 5, 3, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 4, 2, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 3, 1, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 2, 0, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 7, 4, betweenstoneBrickWall, 0, 1, 2, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 7, 4, betweenstoneBrickWall, 0, 1, 2, 1, direction);

		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 0, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 1, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 2, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 3, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 4, 4, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 14, 5, 3, Blocks.air, 0, 4, 4, 2, direction);

		//2nd betweenstoneBrickStairs
		for(int count = 0;count < 6;count ++)
			rotatedCubeVolume(world, rand, xx, yy, zz, 16 + count, 5 + count, 24, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 17, 5, 24, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 6, 24, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 19, 7, 24, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 20, 8, 24, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 18, 10, 24, Blocks.air, 0, 3, 1, 1, direction);

		//3rd betweenstoneBrickStairs
		for(int count = 0;count < 6 ;count ++)
			rotatedCubeVolume(world, rand, xx, yy, zz, 16 - count, 11 + count, 7, betweenstoneBrickStairs, direction == 0 ? 1 : direction== 1 ? 2 : direction == 2 ? 0 : 3, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 11, 7, betweenstoneBricks, 0, 6, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 12, 7, betweenstoneBricks, 0, 5, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 13, 7, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 14, 7, betweenstoneBricks, 0, 3, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 15, 7, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 10, 16, 7, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, xx, yy, zz, 12, 16, 7, Blocks.air, 0, 3, 1, 1, direction);
		
			//top tower floors
		
		//underneath
		rotatedCubeVolume(world, rand, xx, yy, zz, 9, 17, 9, limestonePolished, 0, 14, 1, 14, 0);
		//floor 1
		rotatedCubeVolume(world, rand, xx, yy, zz, 12, 23, 12, limestonePolished, 0, 8, 1, 8, 0);
		//floor2
		rotatedCubeVolume(world, rand, xx, yy, zz, 13, 28, 13, limestonePolished, 0, 6, 1, 6, 0);
		
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
            		for (int zz = z + length - offsetA - 1; zz > z + length - offsetA - sizeWidth - 1; zz--)
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
            		for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
            			for (int xx = x + length - offsetC - 1; xx > x + length - offsetC - sizeDepth - 1; xx--)
            				world.setBlock(xx, yy, zz, blockType, blockMeta, 2);
                break;
        }
    }

    public void generateTopTowerRight(World world, Random rand, int x, int y, int z, int direction) {		   
    	rotatedCubeVolume(world, rand, x, y, z, 0, 0, 1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 0, 3, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 1, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 2, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 0, 1, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 0, 1, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, 3, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 4, 2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 3, 1, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 3, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 8, 3, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 8, 2, betweenstoneBricks, 0, 1, 11, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 17, 2, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, 3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 10, 3, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 10, 3, betweenstoneBricks, 0, 1, 7, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 3, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 5, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 4, 5, 2, betweenstoneBricks, 0, 2, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 5, 6, 2, betweenstoneBricks, 0, 1, 3, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 6, 15, 3, silverCircleBrick, 0, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 6, 14, 3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 6, 9, 2, silverCircleBrick, 0, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 6, 8, 2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 6, 3, 0, silverCircleBrick, 0, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 5, 9, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 6, 5, 2, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 0, 3, 1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
    	rotatedCubeVolume(world, rand, x, y, z, 0, 3, 3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);    	
    	rotatedCubeVolume(world, rand, x, y, z, 5, 4, 1, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 10, 3, betweenstoneBrickStairs, direction == 0 ? 2 : direction== 1 ? 0 : direction == 2 ? 3 : 1, 2, 1, 1, direction);
    }
    
    public void generateTopTowerLeft(World world, Random rand, int x, int y, int z, int direction) {
        if(direction == 2)
        	z -= 13;
        if(direction == 0)
        	z += 13;
        if(direction == 3)
        	x -= 13;
        if(direction == 1)
        	x += 13;

        rotatedCubeVolume(world, rand, x, y, z, 0, 0, -1, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 0, -3, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, -1, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, -2, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 0, -1, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 0, -1, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 2, 4, -3, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 4, -2, betweenstoneBricks, 0, 1, 4, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 3, -1, betweenstoneBricks, 0, 4, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 3, -3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 8, -3, betweenstoneBricks, 0, 1, 6, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 8, -2, betweenstoneBricks, 0, 1, 11, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 17, -2, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 16, -3, betweenstoneBricks, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 10, -3, betweenstoneBricks, 0, 1, 8, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 10, -3, betweenstoneBricks, 0, 1, 7, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 0, 0, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 4, 5, -2, betweenstoneBricks, 0, 2, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 6, -2, betweenstoneBricks, 0, 1, 3, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 15, -3, silverCircleBrick, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 14, -3, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 9, -2, silverCircleBrick, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 8, -2, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 3, 0, silverCircleBrick, 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 2, 0, betweenstoneBrickSlab, 8, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 1, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 3, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 3, 0, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 5, 9, -2, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 6, 5, -2, betweenstoneBrickStairs, direction == 0 ? 3 : direction== 1 ? 1 : direction == 2 ? 2 : 0, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 3, -1, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
		rotatedCubeVolume(world, rand, x, y, z, 0, 3, -3, betweenstoneBrickStairs, direction == 0 ? 0 : direction== 1 ? 3 : direction == 2 ? 1 : 2, 1, 1, 1, direction);
	}

}
