package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.world.gen.feature.loot.LootTables;
import thebetweenlands.common.world.gen.feature.loot.LootUtil;

public abstract class WorldGenHelper extends WorldGenerator {
	/**
	 * Enums to get the meta data of a stair with when a structure is rotated
	 */
	public enum EnumRotationSequence {
		STAIR(0, 3, 1, 2),
		UPSIDE_DOWN_STAIR(4, 7, 5, 6),
		CHEST(2, 5, 3, 4),
		LOG_SIDEWAYS(4, 8);

		private int[] sequence;

		EnumRotationSequence(int... sequence) {
			this.sequence = sequence;
		}
	}


	public int width;
	public int height;
	public int depth;
	public IBlockState[] replaceable;

	private MutableBlockPos checkPos = new MutableBlockPos();

	protected MutableBlockPos getCheckPos(int x, int y, int z) {
		this.checkPos.setPos(x, y, z);
		return this.checkPos;
	}

	/**
	 * A constructor to make you not need to put in the width, height, depth  every time
	 *
	 * @param width  the width of the structure (x axis)
	 * @param height the height of the structure (not always necessary)
	 * @param depth  the depth of the structure (z axis)
	 */
	public WorldGenHelper(int width, int height, int depth, IBlockState... replaceable) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.replaceable = replaceable;
	}


	/**
	 * Generates cube volumes and rotates them depending on the given rotation
	 *
	 * @param world      The world
	 * @param x          x to generate relative from
	 * @param y          y to generate relative from
	 * @param z          z to generate relative from
	 * @param offsetX    Where to generate relative from the x
	 * @param offsetY    Where to generate relative from the y
	 * @param offsetZ    Where to generate relative from the z
	 * @param blockState The block to generate
	 * @param sizeWidth  The width of the cube volume
	 * @param sizeHeight The height of the cube volume
	 * @param sizeDepth  The depth of the cube volume
	 * @param rotation   The rotation for the cube volume (0 to 3)
	 */
	public void rotatedCubeVolume(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, IBlockState blockState, int sizeWidth, int sizeHeight, int sizeDepth, int rotation) {
		x -= width / 2;
		z -= depth / 2;
		switch (rotation) {
		case 0:
			for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
				for (int xx = x + offsetX; xx < x + offsetX + sizeWidth; xx++)
					for (int zz = z + offsetZ; zz < z + offsetZ + sizeDepth; zz++)
						world.setBlockState(new BlockPos(xx, yy, zz), blockState, 2);
			break;
		case 1:
			for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
				for (int zz = z + depth - offsetX - 1; zz > z + depth - offsetX - sizeWidth - 1; zz--)
					for (int xx = x + offsetZ; xx < x + offsetZ + sizeDepth; xx++)
						world.setBlockState(new BlockPos(xx, yy, zz), blockState, 2);
			break;
		case 2:
			for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
				for (int xx = x + width - offsetX - 1; xx > x + width - offsetX - sizeWidth - 1; xx--)
					for (int zz = z + depth - offsetZ - 1; zz > z + depth - offsetZ - sizeDepth - 1; zz--)
						world.setBlockState(new BlockPos(xx, yy, zz), blockState, 2);
			break;
		case 3:
			for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
				for (int zz = z + offsetX; zz < z + offsetX + sizeWidth; zz++)
					for (int xx = x + width - offsetZ - 1; xx > x + width - offsetZ - sizeDepth - 1; xx--)
						world.setBlockState(new BlockPos(xx, yy, zz), blockState, 2);
			break;
		}
	}


	/**
	 * Creates a WorldGenHelper#rotatedCubeVolume that also extends down until it hits a non replaceable block
	 *
	 * @param world      The world
	 * @param x          x to generate relative from
	 * @param y          y to generate relative from
	 * @param z          z to generate relative from
	 * @param offsetX    Where to generate relative from the x
	 * @param offsetY    Where to generate relative from the y
	 * @param offsetZ    Where to generate relative from the z
	 * @param blockState The block to generate
	 * @param sizeWidth  The width of the cube volume
	 * @param sizeHeight The height of the cube volume
	 * @param sizeDepth  The depth of the cube volume
	 * @param rotation   The rotation for the cube volume (0 to 3)
	 */
	public void rotatedCubeVolumeExtendedDown(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, IBlockState blockState, int sizeWidth, int sizeHeight, int sizeDepth, int rotation) {
		while (isReplaceable(world, x, y, z, offsetX, offsetY - 1, offsetZ, rotation)) {
			offsetY--;
			sizeHeight++;
		}
		rotatedCubeVolume(world, x, y, z, offsetX, offsetY, offsetZ, blockState, sizeWidth, sizeHeight, sizeDepth, rotation);
	}

	/**
	 * checks if a block is replaceable
	 *
	 * @param world    The world
	 * @param x        x to generate relative from
	 * @param y        y to generate relative from
	 * @param z        z to generate relative from
	 * @param offsetX  Where to generate relative from the x
	 * @param offsetY  Where to generate relative from the y
	 * @param offsetZ  Where to generate relative from the z
	 * @param rotation The rotation for the cube volume (0 to 3)
	 * @return whether or not it is replaceable
	 */
	public boolean isReplaceable(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation) {
		//return isReplaceable(world, new BlockPos(x, y, z), offsetX, offsetY, offsetZ, rotation);
		x -= width / 2;
		z -= depth / 2;
		//pos = pos.add(-(width / 2), 0, -(depth / 2));
		BlockPos pos;
		switch (rotation) {
		case 0:
			//pos = pos.add(offsetX, offsetY, offsetZ);
			pos = this.getCheckPos(x + offsetX, y + offsetY, z + offsetZ);
			return world.getBlockState(pos).getBlock().isReplaceable(world, pos) || (replaceable != null && arrayContainsBlock(replaceable, world.getBlockState(pos)));
		case 1:
			//pos = pos.add(offsetZ, offsetY, depth - offsetX - 1);
			pos = this.getCheckPos(offsetZ, offsetY, depth - offsetX - 1);
			return world.getBlockState(pos).getBlock().isReplaceable(world, pos) || (replaceable != null && arrayContainsBlock(replaceable, world.getBlockState(pos)));
		case 2:
			//pos = pos.add(width - offsetX - 1, offsetY, depth - offsetZ - 1);
			pos = this.getCheckPos(width - offsetX - 1, offsetY, depth - offsetZ - 1);
			return world.getBlockState(pos).getBlock().isReplaceable(world, pos) || (replaceable != null && arrayContainsBlock(replaceable, world.getBlockState(pos)));
		case 3:
			//pos = pos.add(width - offsetZ - 1, offsetY, offsetX);
			pos = this.getCheckPos(width - offsetZ - 1, offsetY, offsetX);
			return world.getBlockState(pos).getBlock().isReplaceable(world, pos) || (replaceable != null && arrayContainsBlock(replaceable, world.getBlockState(pos)));
		}
		return false;
	}

	/**
	 * Generates a loot pot taking the rotation of a structure into a count
	 *
	 * @param world   The workd
	 * @param rand    a random
	 * @param x       x to generate relative from
	 * @param y       y to generate relative from
	 * @param z       z to generate relative from
	 * @param offsetX Where to generate relative from the x
	 * @param offsetY Where to generate relative from the y
	 * @param offsetZ Where to generate relative from the z
	 * @param min     The minimum amount of items
	 * @param max     The maximum amount of items
	 * @param chance  The chance of it actually generating
	 */
	public void rotatedLootPot(World world, Random rand, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, int min, int max, int chance) {
		x -= width / 2;
		z -= depth / 2;
		if (rand.nextInt(chance) == 0)
			return;
		switch (rotation) {
		case 0:
			generateLootPot(world, rand, new BlockPos(x + offsetX, y + offsetY, z + offsetZ), min, max);
			break;
		case 1:
			generateLootPot(world, rand, new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1), min, max);
			break;
		case 2:
			generateLootPot(world, rand, new BlockPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1), min, max);
			break;
		case 3:
			generateLootPot(world, rand, new BlockPos(x + width - offsetZ - 1, y + offsetY, z + offsetX), min, max);
			break;
		}
	}

	/**
	 * Generates a loot pot taking the rotation of a structure into a count
	 *
	 * @param world   The workd
	 * @param rand    a random
	 * @param x       x to generate relative from
	 * @param y       y to generate relative from
	 * @param z       z to generate relative from
	 * @param offsetX Where to generate relative from the x
	 * @param offsetY Where to generate relative from the y
	 * @param offsetZ Where to generate relative from the z
	 * @param min     The minimum amount of items
	 * @param max     The maximum amount of items
	 * @param chance  The chance of it actually generating
	 */
	public void rotatedLootChest(World world, Random rand, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, int min, int max, int chance, int sequenceStart) {
		x -= width / 2;
		z -= depth / 2;
		if (rand.nextInt(chance) == 0)
			return;
		switch (rotation) {
		case 0:
			generateLootChest(world, rand, new BlockPos(x + offsetX, y + offsetY, z + offsetZ), min, max, getStateFromRotation(sequenceStart, rotation, Blocks.CHEST.getDefaultState(), EnumRotationSequence.CHEST));
			break;
		case 1:
			generateLootChest(world, rand, new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1), min, max, getStateFromRotation(sequenceStart, rotation, Blocks.CHEST.getDefaultState(), EnumRotationSequence.CHEST));
			break;
		case 2:
			generateLootChest(world, rand, new BlockPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1), min, max, getStateFromRotation(sequenceStart, rotation, Blocks.CHEST.getDefaultState(), EnumRotationSequence.CHEST));
			break;
		case 3:
			generateLootChest(world, rand, new BlockPos(x + width - offsetZ - 1, y + offsetY, z + offsetX), min, max, getStateFromRotation(sequenceStart, rotation, Blocks.CHEST.getDefaultState(), EnumRotationSequence.CHEST));
			break;
		}
	}

	/**
	 * Generates a spawner taking the rotation into a count
	 *
	 * @param world    the world
	 * @param x       x to generate relative from
	 * @param y       y to generate relative from
	 * @param z       z to generate relative from
	 * @param offsetX  Where to generate relative from the x
	 * @param offsetY  Where to generate relative from the y
	 * @param offsetZ  Where to generate relative from the z
	 * @param rotation the rotation
	 * @param mob      the mob that should be in the spawner
	 */
	public MobSpawnerLogicBetweenlands rotatedSpawner(World world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, String mob) {
		BlockPos pos = (new BlockPos(x, y, z)).add(-(width / 2), 0, -(depth / 2));
		IBlockState spawner = BlockRegistry.MOB_SPAWNER.getDefaultState();
		switch (rotation) {
		case 0:
			pos = pos.add(offsetX, offsetY, offsetZ);
			world.setBlockState(pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		case 1:
			pos = pos.add(offsetZ, offsetY, depth - offsetX - 1);
			world.setBlockState(pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		case 2:
			pos = pos.add(width - offsetX - 1, offsetY, depth - offsetZ - 1);
			world.setBlockState(pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		default:
		case 3:
			pos = pos.add(width - offsetZ - 1, offsetY, offsetX);
			world.setBlockState(pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		}

	}

	/**
	 * Generates a loot pot at a location
	 *
	 * @param world  The world
	 * @param random A Random
	 * @param pos    The pos to generate at
	 * @param min    The minimum amount of items
	 * @param max    The maximum amount of items
	 */
	public void generateLootPot(World world, Random random, BlockPos pos, int min, int max) {
		world.setBlockState(pos, getRandomLootPot(random), 3);
		TileEntityLootPot lootPot = (TileEntityLootPot) world.getTileEntity(pos);
		if (lootPot != null)
			LootUtil.generateLoot(lootPot, random, LootTables.DUNGEON_POT_LOOT, min, max);
	}

	/** TODO change to weedwood chest when added
	 * Generates a loot chest at a location
	 *
	 * @param world  The world
	 * @param random A Random
	 * @param pos    The pos to generate at
	 * @param min    The minimum amount of items
	 * @param max    The maximum amount of items
	 */
	public void generateLootChest(World world, Random random, BlockPos pos, int min, int max, IBlockState state) {
		world.setBlockState(pos, state, 3);
		TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
		if (chest != null)
			LootUtil.generateLoot(chest, random, LootTables.DUNGEON_CHEST_LOOT, min, max);
	}


	/**
	 * Gets the meta from a sequence depending on the rotation
	 *
	 * @param start                where to start in the sequence
	 * @param rotation             the rotation
	 * @param enumRotationSequence which sequence to use
	 * @return the meta of corresponding to the rotation
	 */
	public IBlockState getStateFromRotation(int start, int rotation, IBlockState state, EnumRotationSequence enumRotationSequence) {
		return state.getBlock().getStateFromMeta(enumRotationSequence.sequence[(rotation + start) % enumRotationSequence.sequence.length]);
	}


	/**
	 * Gives one of 3 different styles of loot post with a random rotation
	 *
	 * @param random a random
	 * @return the blockstate of one of the loot pots
	 */
	public IBlockState getRandomLootPot(Random random) {
		int randDirection = random.nextInt(4) + 2;

		switch (random.nextInt(3)) {
		case 0:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_1).withProperty(BlockLootPot.FACING, EnumFacing.getFront(randDirection));
		case 1:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_2).withProperty(BlockLootPot.FACING, EnumFacing.getFront(randDirection));
		default:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_3).withProperty(BlockLootPot.FACING, EnumFacing.getFront(randDirection));
		}
	}

	/**
	 * Checks if the list contains the block state (usefull for blacklists)
	 *
	 * @param list  the list of blockstates
	 * @param block a blockstate
	 * @return if the list contains the block
	 */
	public boolean arrayContainsBlock(IBlockState[] list, IBlockState block) {
		for (IBlockState block1 : list)
			if (block == block1)
				return true;
		return false;
	}
}
