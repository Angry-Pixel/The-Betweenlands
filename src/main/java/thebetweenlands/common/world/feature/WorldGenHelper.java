package thebetweenlands.common.world.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.world.feature.loot.LootTables;
import thebetweenlands.common.world.feature.loot.LootUtil;

import java.util.Random;

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

    /**
     * A constructor to make you not need to put in the width, height, depth  every time
     *
     * @param width  the width of the structure (x axis)
     * @param height the height of the structure (not always necessary)
     * @param depth  the depth of the structure (z axis)
     */
    public WorldGenHelper(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }


    /**
     * Generates cube volumes and rotates them depending on the given rotatiopn
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
    public void rotatedLoot(World world, Random rand, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, int min, int max, int chance) {
        x -= width / 2;
        z -= depth / 2;
        if (rand.nextInt(chance) == 0)
            return;
        switch (rotation) {
            case 0:
                generateLoot(world, rand, new BlockPos(x + offsetX, y + offsetY, z + offsetZ), min, max);
                break;
            case 1:
                generateLoot(world, rand, new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1), min, max);
                break;
            case 2:
                generateLoot(world, rand, new BlockPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1), min, max);
                break;
            case 3:
                generateLoot(world, rand, new BlockPos(x + width - offsetZ - 1, y + offsetY, z + offsetX), min, max);
                break;
        }
    }

    /**
     * TODO fix when spawners are added
     * Generates a spawner taking the rotation into a count
     *
     * @param world    the world
     * @param pos      the position to generate relative from
     * @param offsetX  Where to generate relative from the x
     * @param offsetY  Where to generate relative from the y
     * @param offsetZ  Where to generate relative from the z
     * @param rotation the rotation
     * @param mob      the mob that should be in the spawner
     */
    public void rotatedSpawner(World world, BlockPos pos, int offsetX, int offsetY, int offsetZ, int rotation, String mob) {
        pos.add(-(width / 2), 0, -(depth / 2));
        IBlockState spawner = BlockRegistry.MOB_SPAWNER.getDefaultState();
        switch (rotation) {
            case 0:
                world.setBlockState(pos.add(offsetX, offsetY, offsetZ), spawner);
                BlockMobSpawnerBetweenlands.setMob(world, pos.add(offsetX, offsetY, offsetZ), mob);
                break;
            case 1:
                world.setBlockState(pos.add(offsetZ, offsetY, depth - offsetX - 1), spawner);
                BlockMobSpawnerBetweenlands.setMob(world, pos.add(offsetZ, offsetY, depth - offsetX - 1), mob);
                break;
            case 2:
                world.setBlockState(pos.add(width - offsetX - 1, offsetY, depth - offsetZ - 1), spawner);
                BlockMobSpawnerBetweenlands.setMob(world, pos.add(width - offsetX - 1, offsetY, depth - offsetZ - 1), mob);
                break;
            case 3:
                world.setBlockState(pos.add(width - offsetZ - 1, offsetY, offsetX), spawner);
                BlockMobSpawnerBetweenlands.setMob(world, pos.add(width - offsetZ - 1, offsetY, offsetX), mob);
                break;
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
    public void generateLoot(World world, Random random, BlockPos pos, int min, int max) {
        world.setBlockState(pos, getRandomLootPot(random), 3);
        TileEntityLootPot lootPot = (TileEntityLootPot) world.getTileEntity(pos);
        if (lootPot != null)
            LootUtil.generateLoot(lootPot, random, LootTables.DUNGEON_POT_LOOT, min, max);
    }


    /**
     * Gets the meta from a sequence depending on the rotation
     *
     * @param start                where to start in the sequence
     * @param rotation             the rotation
     * @param enumRotationSequence which sequence to use
     * @return the meta of corresponding to the rotation
     */
    public int getMetaFromRotation(int start, int rotation, EnumRotationSequence enumRotationSequence) {
        return enumRotationSequence.sequence[(rotation + start) % enumRotationSequence.sequence.length];
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
