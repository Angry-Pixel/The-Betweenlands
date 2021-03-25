package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenUnderwaterRuins extends WorldGenHelper {

	private final IBlockState cragrockBrick = BlockRegistry.CRAGROCK_BRICKS.getDefaultState();
	private final IBlockState cragrockBrickCracked = BlockRegistry.CRAGROCK_BRICKS_CRACKED.getDefaultState();
	private final IBlockState cragrockBrickMossy = BlockRegistry.CRAGROCK_BRICKS_MOSSY.getDefaultState();
	private final IBlockState cragrockTile = BlockRegistry.CRAGROCK_TILES.getDefaultState();
	private final IBlockState cragrockTileCracked = BlockRegistry.CRAGROCK_TILES_CRACKED.getDefaultState();
	private final IBlockState cragrockTileMossy = BlockRegistry.CRAGROCK_TILES_MOSSY.getDefaultState();
	private final IBlockState cragrockChisel = BlockRegistry.CRAGROCK_CHISELED.getDefaultState();
	private final IBlockState cragrockBrickStairs = BlockRegistry.CRAGROCK_BRICK_STAIRS.getDefaultState();

	public WorldGenUnderwaterRuins() {
		super(); //TODO: Replace the dirt underwater and the water
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position), "underwater_ruins", EnumLocationType.RUINS);

		boolean generated;

		switch (rand.nextInt(4)) {
			case 1:
				generated = structureArch(world, rand, position);
				break;
			case 2:
				generated = structurePillars(world, rand, position);
				break;
			case 3:
				generated = structureRing(world, rand, position);
				break;
			case 0:
			default:
				generated = structureShelter(world, rand, position);
				break;
		}

		return generated;
	}

	//generate a ring with 4 corner pillars
	private boolean structureRing(World world, Random rand, BlockPos position) {
		//NOTE: Ring size is 3, 5, 7. Generate 2-7, add 1 if even number, ring size 7 has a middle pillar
		int ringsize = rand.nextInt(6) + 2; // 0, 1, 2, 3, 4, 5 -> 2, 3, 4, 5, 6, 7
		ringsize = ringsize % 2 == 0 ? ringsize + 1 : ringsize;
		int center = (ringsize - 1) / 2;

		//floor
		for (int fx = -center; fx <= center; fx++) {
			for (int fz = -center; fz <= center; fz++) {
				//floor
				world.setBlockState(new BlockPos(position.getX() + fx, position.getY(), position.getZ() + fz), getTileGrade(rand), 2 | 16);

				//put a pot in the middle of the ring
				if (fx > -center + 1 && fx < center - 1) {
					//do not put a pot in the middle, especially for ringsize = 7
					if (fx != 0 && fz != 0) {
						if (rand.nextInt(7) == 0) {
							setLootPot(world, rand, new BlockPos(position.getX() + fx, position.getY() + 1, position.getZ() + fz));
						}
					}
				}

				//ring
				if (!(Math.abs(fx) == center && Math.abs(fz) == center)) {
					if (Math.abs(fx) == center || Math.abs(fz) == center) {
						world.setBlockState(new BlockPos(position.getX() + fx, position.getY() + 1, position.getZ() + fz), getBrickGrade(rand), 2 | 16);

						//put a pot on the ring
						if (rand.nextInt(5) == 0) {
							setLootPot(world, rand, new BlockPos(position.getX() + fx, position.getY() + 2, position.getZ() + fz));
						}
					}
				} else {
					//pillar
					int pillarbase = ringsize;
					int pillarheight = pillarbase + rand.nextInt(3); // + 0, 1, 2
					buildPillar(world, rand, new BlockPos(position.getX() + fx, position.getY() + 1, position.getZ() + fz), pillarheight);
				}

				//middle pillar
				if (ringsize >= 7 && (fx == 0 && fz == 0)) {
					buildPillar(world, rand, new BlockPos(position.getX() + fx, position.getY() + 1, position.getZ() + fz), ringsize);
				}
			}
		}

		return true;
	}

	private void buildPillar(World world, Random rand, BlockPos pos, int height) {
		for (int y = 0; y <= height; y++) {
			world.setBlockState(new BlockPos(pos.getX(), pos.getY() + y, pos.getZ()), getTileGrade(rand), 2 | 16);
		}
		//put a pot on top
		if (rand.nextInt(4) == 0) {
			setLootPot(world, rand, new BlockPos(pos.getX(), pos.getY() + height + 1, pos.getZ()));
		}
	}

	//generate a pillar ring
	//FIXME: Radius too small. Investigate.
	private boolean structurePillars(World world, Random rand, BlockPos position) {
		//7x7 grid, 5x5 grid of tiles
		int basepillar = 3; //max height 8

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (rand.nextInt(3) == 0) {
					world.setBlockState(new BlockPos(position.getX() + x, position.getY(), position.getZ() + z), getTileGrade(rand), 2 | 16);
				}
				if (rand.nextInt(8) == 0) {
					setLootPot(world, rand, new BlockPos(position.getX() + x, position.getY() + 1, position.getZ() + z));
				}
			}
		}
		world.setBlockState(position.add(-1, 0, 0), getBrickGrade(rand), 2 | 16);
		world.setBlockState(position.add(1, 0, 0), getBrickGrade(rand), 2 | 16);
		world.setBlockState(position.add(0, 0, -1), getBrickGrade(rand), 2 | 16);
		world.setBlockState(position.add(0, 0, 1), getBrickGrade(rand), 2 | 16);

		position.add(0, 1, 0);

		//north
		buildPillar2(world, rand, position.add(-1, 0, -3), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(0, 0, -3), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(1, 0, -3), rand.nextInt(6) + basepillar);
		//ne
		buildPillar2(world, rand, position.add(2, 0, -2), rand.nextInt(6) + basepillar);
		//east
		buildPillar2(world, rand, position.add(3, 0, -1), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(3, 0, 0), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(3, 0, 1), rand.nextInt(6) + basepillar);
		//se
		buildPillar2(world, rand, position.add(2, 0, 2), rand.nextInt(6) + basepillar);
		//south
		buildPillar2(world, rand, position.add(1, 0, 3), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(0, 0, 3), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(-1, 0, 3), rand.nextInt(6) + basepillar);
		//sw
		buildPillar2(world, rand, position.add(-2, 0, 2), rand.nextInt(6) + basepillar);
		//west
		buildPillar2(world, rand, position.add(-3, 0, 1), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(-3, 0, 0), rand.nextInt(6) + basepillar);
		buildPillar2(world, rand, position.add(-3, 0, -1), rand.nextInt(6) + basepillar);
		//nw
		buildPillar2(world, rand, position.add(-2, 0, -2), rand.nextInt(6) + basepillar);

		return true;
	}

	private void buildPillar2(World world, Random rand, BlockPos pos, int height) {
		for (int y = 0; y <= height; y++) {
			//if we hit the top, make it chiselled
			world.setBlockState(new BlockPos(pos.getX(), pos.getY() + y, pos.getZ()), y == height ? cragrockChisel : getTileGrade(rand), 2 | 16);
		}
		//put a pot on top
		if (rand.nextInt(4) == 0) {
			setLootPot(world, rand, new BlockPos(pos.getX(), pos.getY() + height + 2, pos.getZ()));
		}
	}

	//generate some arches
	//FIXME: NOT GENERATING
	private boolean structureArch(World world, Random rand, BlockPos pos) {
		if (rand.nextBoolean()) buildArch(world, rand, pos.add(1, 0, 1), EnumFacing.NORTH); //north
		if (rand.nextBoolean()) buildArch(world, rand, pos.add(1, 0, 0), EnumFacing.EAST); //east
		if (rand.nextBoolean()) buildArch(world, rand, pos, EnumFacing.SOUTH); //south
		if (rand.nextBoolean()) buildArch(world, rand, pos.add(0, 0, 1), EnumFacing.WEST); //west

		return true;
	}

	private void buildArch(World world, Random rand, BlockPos pos, EnumFacing directionIn) {
		int direction = directionIn.getHorizontalIndex();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		rotatedCubeVolume(world, x, y, z, 0, 0, -3, getBrickGrade(rand), 1, 4, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 4, -3, getStateFromRotation(3, direction, cragrockBrickStairs, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 4, -2, getStateFromRotation(1, direction, cragrockBrickStairs, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, -2, getStateFromRotation(3, direction, cragrockBrickStairs, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 5, -1, getStateFromRotation(1, direction, cragrockBrickStairs, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 1, direction);
	}

	//generate a shelter
	//TODO: Maths seems to be not ok. Spice it up, too.
	private boolean structureShelter(World world, Random random, BlockPos pos) {
		int length = (random.nextInt(3) + 7) / 2;
		int width = (random.nextInt(3) + 7) / 2;

		for (int x = -length; x <= length; x++) {
			for (int z = -width; z <= width; z++) {
				world.setBlockState(pos.add(x, 0, z), getTileGrade(random), 2 | 16);

				//build wall
				if (Math.abs(x) == 3 || Math.abs(z) == 3) {
					int height = random.nextInt(3) + 2;
					for (int y = 0; y <= height; y++) {
						world.setBlockState(pos.add(x, y + 1, z), getBrickGrade(random), 2 | 16);
					}
				} else {
					//fill with pots
					if (random.nextInt(10) == 0) {
						setLootPot(world, random, pos.add(0, 1, 0));
					}
				}
			}
		}

		return true;
	}

	//degrades Cragrock Bricks
	private IBlockState getBrickGrade(Random rand) {
		switch (rand.nextInt(3)) {
			case 0:
				return cragrockBrick;
			case 1:
				return cragrockBrickCracked;
			case 2:
				return cragrockBrickMossy;
		}
		return cragrockBrick;
	}

	//degrades Cragrock Tiles
	private IBlockState getTileGrade(Random rand) {
		switch (rand.nextInt(3)) {
			case 0:
				return cragrockTile;
			case 1:
				return cragrockTileCracked;
			case 2:
				return cragrockTileMossy;
		}
		return cragrockTile;
	}

	//creates a pot of loot
	private void setLootPot(World world, Random random, BlockPos pos) {
		this.setBlockAndNotifyAdequately(world, pos, this.getRandomPot(random));
		TileEntityLootPot lootPot = BlockLootPot.getTileEntity(world, pos);
		if(lootPot != null) {
			lootPot.setLootTable(LootTableRegistry.CAVE_POT, random.nextLong()); //TODO: replace
		}
	}

	//get a random pot variant, TODO: Underwater exclusive pots?
	private IBlockState getRandomPot(Random rand) {
		switch (rand.nextInt(3)) {
			case 1:
				return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_2);
			case 2:
				return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_3);
			case 0:
			default:
				return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_1);
		}
	}
}
