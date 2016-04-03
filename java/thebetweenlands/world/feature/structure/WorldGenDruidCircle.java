package thebetweenlands.world.feature.structure;

import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class WorldGenDruidCircle
implements IWorldGenerator
{
	private int height = -1;
	private int baseRadius = -1;

	public WorldGenDruidCircle() {
		height = 4;
		baseRadius = 6;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.dimensionId == 0) {
			generate(world, random, chunkX * 16 + 8 + world.rand.nextInt(16), chunkZ * 16 + 8 + world.rand.nextInt(16));
		}
	}

	private void generate(World world, Random random, int x, int z) {
		if(world.checkChunksExist(x - baseRadius - 1, 64, z - baseRadius - 1, x + baseRadius + 1, 64, z + baseRadius + 1)) {
			BiomeGenBase biomeBase = world.getBiomeGenForCoords(x, z);
			int newY = 63 + random.nextInt(20) - 10;
			if(BiomeDictionary.isBiomeOfType(biomeBase, Type.SWAMP)) {
				for(int newX = x - baseRadius; newX <= x + baseRadius; ++newX) {
					for(int newZ = z - baseRadius; newZ <= z + baseRadius; ++newZ) {
						if(world.isAirBlock(x, newY + 1, z)) {
							Block block = world.getBlock(newX, newY, newZ);
							if(block != null && block == biomeBase.topBlock) {
								if(random.nextInt(ConfigHandler.DRUID_CIRCLE_FREQUENCY) == 0) {
									generateStructure(world, random, x, newY + 1, z);
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean generateStructure(World world, Random rand, int x, int y, int z) {
		// air check
		for(int newX = x - baseRadius; newX <= x + baseRadius; ++newX) {
			for(int newZ = z - baseRadius; newZ <= z + baseRadius; ++newZ) {
				for(int newY = y + 1; newY < y + height; ++newY) {
					if(!world.isAirBlock(newX, newY, newZ) && !world.getBlock(newX, newY, newZ).isReplaceable(world, newX, newY, newZ)) {
						return false;
					}
				}
			}
		}

		//circle
		for( int newX = baseRadius * -1; newX <= baseRadius; ++newX ) {
			for( int newZ = baseRadius * -1; newZ <= baseRadius; ++newZ ) {
				double dSq = newX * newX + newZ * newZ;
				if( Math.round(Math.sqrt(dSq)) == baseRadius && newX % 2 == 0 && newZ % 2 == 0 ) {
					placePillar(world, x + newX, y, z + newZ, rand);
				} else if( Math.round(Math.sqrt(dSq)) == baseRadius && newX % 2 != 0 && newZ % 2 != 0 ) {
					placeAir(world, x + newX, y, z + newZ);
				}

				if( Math.round(Math.sqrt(dSq)) <= baseRadius ) {
					world.setBlock(x + newX, y - 1, z + newZ, Blocks.grass);
				}
			}
		}

		world.setBlock(x, y, z, BLBlockRegistry.druidAltar);
		world.setBlock(x, y - 1, z, BLBlockRegistry.druidSpawner);

		return true;
	}

	private void placeAir(World world, int x, int y, int z) {
		for( int yNew = y; height + y >= yNew; ++yNew ) {
			world.setBlock(x, yNew, z, Blocks.air);
		}
	}

	private void placePillar(World world, int x, int y, int z, Random rand) {
		int randHeight = rand.nextInt(3) + 3;
		for( int yy = y; randHeight + y >= yy; ++yy ) {
			int randDirection = rand.nextInt(4);
			if( rand.nextBoolean() ) {
				world.setBlock(x, yy, z, getRandomBlock(rand), randDirection, 3);
			} else {
				world.setBlock(x, yy, z, Blocks.stone);
				for( int vineCount = 0; vineCount < 4; vineCount++ ) {
					setRandomFoliage(world, x, yy, z, rand);
				}
			}
		}
	}

	private void setRandomFoliage(World world, int x, int y, int z, Random rand) {
		switch( rand.nextInt(4) ) { // get random side
		case 0:
			if( world.getBlock(x + 1, y, z) == Blocks.air ) {
				world.setBlock(x + 1, y, z, Blocks.vine, 2, 3);
			}
			break;
		case 1:
			if( world.getBlock(x - 1, y, z) == Blocks.air ) {
				world.setBlock(x - 1, y, z, Blocks.vine, 8, 3);
			}
			break;
		case 2:
			if( world.getBlock(x, y, z + 1) == Blocks.air ) {
				world.setBlock(x, y, z + 1, Blocks.vine, 4, 3);
			}
			break;
		case 3:
			if( world.getBlock(x, y, z - 1) == Blocks.air ) {
				world.setBlock(x, y, z - 1, Blocks.vine, 1, 3);
			}
			break;
		}
	}

	private Block getRandomBlock(Random rand) {
		switch( rand.nextInt(5) ) {
		case 0: return BLBlockRegistry.druidStone1;
		case 1: return BLBlockRegistry.druidStone2;
		case 2: return BLBlockRegistry.druidStone3;
		case 3: return BLBlockRegistry.druidStone4;
		case 4: return BLBlockRegistry.druidStone5;
		default: return Blocks.stone;
		}
	}
}
