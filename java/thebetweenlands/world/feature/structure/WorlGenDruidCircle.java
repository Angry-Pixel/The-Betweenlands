package thebetweenlands.world.feature.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.common.IWorldGenerator;

public class WorlGenDruidCircle implements IWorldGenerator {
	
	private int height = -1;
	private int baseRadius = -1;
	
	public WorlGenDruidCircle() {
		height = 4;
		baseRadius = 6;
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == 0)
			generate(world, random, chunkX * 16, chunkZ * 16);
	}

	private void generate(World world, Random random, int x, int z) {
		BiomeGenBase biomeBase = world.getBiomeGenForCoords(x, z);
		int yy = 63;
		if (biomeBase == BiomeGenBase.swampland)
			for (int xx = x - baseRadius; xx <= x + baseRadius; ++xx)
				for (int zz = z - baseRadius; zz <= z + baseRadius; ++zz) {
					Block block = world.getBlock(xx, yy, zz);
					if (block != null && block == biomeBase.topBlock)
						if (random.nextInt(ConfigHandler.DRUID_CIRCLE_FREQUENCY) == 0) // this may need to change ie. give it a config option
							generateStructure(world, random, x, yy, z);
				}
	}

	public boolean generateStructure(World world, Random rand, int x, int y, int z) {
		// air check
		for (int xx = x - baseRadius; xx <= x + baseRadius; ++xx)
			for (int zz = z - baseRadius; zz <= z + baseRadius; ++zz)
				for (int yy = y + 1; yy < y + height; ++yy)
					if (!world.isAirBlock(xx, yy, zz))
						return false;

		//circle
			for (int xx = baseRadius * -1; xx <= baseRadius; ++xx)
				for (int zz = baseRadius * -1; zz <= baseRadius; ++zz) {
					double dSq = xx * xx + zz * zz;
						if (Math.round(Math.sqrt(dSq)) == baseRadius && xx % 2 == 0 && zz % 2 == 0)
							placePillar(world, x + xx, y, z + zz, rand);
						else if (Math.round(Math.sqrt(dSq)) == baseRadius && xx % 2 != 0 && zz % 2 != 0)
							placeAir(world, x + xx, y, z + zz);						
						if (Math.round(Math.sqrt(dSq)) <= baseRadius)
							world.setBlock(x + xx, y - 1, z + zz, Blocks.grass);
					}
			world.setBlock(x , y, z, BLBlockRegistry.druidAltar);
			world.setBlock(x , y - 1, z, BLBlockRegistry.druidSpawner);

		System.out.println("Added DruidCircle at: " + x + " " + z);
		return true;
	}

	private void placeAir(World world, int x, int y, int z) {
		for (int yy = y; height + y >= yy; ++yy)
			world.setBlock(x, yy, z, Blocks.air);
	}

	private void placePillar(World world, int x, int y, int z, Random rand) {
		int randHeight = rand.nextInt(3) + 3;
		for (int yy = y; randHeight + y >= yy; ++yy) {
			int randDirection = rand.nextInt(4);
			if (rand.nextBoolean())
				world.setBlock(x, yy, z, getRandomBlock(rand), randDirection, 3);
			else {
				world.setBlock(x, yy, z, Blocks.stone);
				for(int vineCount = 0; vineCount < 4; vineCount++ )
					setRandomFoliage(world, x, yy, z, rand);
			}
		}
	}

	private void setRandomFoliage(World world, int x, int y, int z, Random rand) {
		int randSide = rand.nextInt(4);
		switch (randSide) {
		case 0:
			if (world.getBlock(x + 1, y, z) == Blocks.air)
				world.setBlock(x + 1, y, z, Blocks.vine, 2, 3);
			break;
		case 1:
			if (world.getBlock(x - 1, y, z) == Blocks.air)
				world.setBlock(x - 1, y, z, Blocks.vine, 8, 3);
			break;
		case 2:
			if (world.getBlock(x, y, z + 1) == Blocks.air)
				world.setBlock(x, y, z + 1, Blocks.vine, 4, 3);
			break;
		case 3:
			if (world.getBlock(x, y, z - 1) == Blocks.air)
				world.setBlock(x, y, z - 1, Blocks.vine, 1, 3);
			break;
		}
	}

	private Block getRandomBlock(Random rand) {
		int randBlock = rand.nextInt(5);
		switch (randBlock) {
		case 0:
			return BLBlockRegistry.druidStone1;
		case 1:
			return BLBlockRegistry.druidStone2;
		case 2:
			return BLBlockRegistry.druidStone3;
		case 3:
			return BLBlockRegistry.druidStone4;
		case 4:
			return BLBlockRegistry.druidStone5;
		default:
			return Blocks.stone;
		}
	}

}
