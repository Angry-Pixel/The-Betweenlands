package thebetweenlands.common.world.gen.biome.decorator.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.features.BetweenlandsDecoratorConfiguration;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

// For biome features that need a server level instance to operate (trees, plants, structures)
public abstract class DecoratorFeature extends Feature<BetweenlandsDecoratorConfiguration> {

    private final boolean doBlockNotify;

    public DecoratorFeature(Codec<BetweenlandsDecoratorConfiguration> p_65786_, boolean notify) {
        super(p_65786_);
        this.doBlockNotify = notify;
    }

    public DecoratorFeature(Codec<BetweenlandsDecoratorConfiguration> p_65786) {
        this(p_65786, false);
    }

    /**
     * Enums to get the metadata of a stair with when a structure is rotated
     */
    public enum EnumRotationSequence {
        //TODO: This ought to be removed and replaced with the proper block states at some point
        // - sure thing!
        //E.g. enum.getRotatedBlockState(IProperty propertyThatContainsTheRotation, IBlockState theBlockStateToRotate)

        STAIR(0, 3, 1, 2),
        UPSIDE_DOWN_STAIR(4, 7, 5, 6),
        CHEST(3, 5, 2, 4),
        LOG_SIDEWAYS(4, 8),
        PILLAR_SIDEWAYS(8, 7);

        private int[] sequence;

        EnumRotationSequence(int... sequence) {
            this.sequence = sequence;
        }
    }


    protected int width;
    protected int height;
    protected int depth;
    protected List<Predicate<BlockState>> replaceable = new ArrayList<>();

    private BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

    protected BlockPos.MutableBlockPos getCheckPos(int x, int y, int z) {
        this.checkPos.set(x, y, z);
        return this.checkPos;
    }

    public boolean generate(Level world, Random rand, BlockPos pos) {
        return true;
    }

    /**
     * A constructor that takes the width, height, depth every time
     *
     * @param width  the width of the structure (x axis)
     * @param height the height of the structure (not always necessary)
     * @param depth  the depth of the structure (z axis)
     */
    /*
    public WorldGenHelper(int width, int height, int depth, BlockState... replaceable) {
        this(false);
        this.width = width;
        this.height = height;
        this.depth = depth;
        for(BlockState state : replaceable) {
            this.replaceable.add(s -> s == state);
        }
    }

    public WorldGenHelper(BlockState... replaceable) {
        this(false);
        for(BlockState state : replaceable) {
            this.replaceable.add(s -> s == state);
        }
    }*/



    /**
     * @see #rotatedCubeVolume(Level, int, int, int, int, int, int, BlockState, int, int, int, int, Predicate, Consumer...)
     * @param world
     * @param x
     * @param y
     * @param z
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param blockState
     * @param sizeWidth
     * @param sizeHeight
     * @param sizeDepth
     * @param rotation
     * @param callbacks
     */
    @SafeVarargs
    public final void rotatedCubeVolume(Level world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState blockState, int sizeWidth, int sizeHeight, int sizeDepth, int rotation, Consumer<BlockPos>... callbacks) {
        this.rotatedCubeVolume(world, null, x, y, z, offsetX, offsetY, offsetZ, blockState, sizeWidth, sizeHeight, sizeDepth, rotation, callbacks);
    }

    /**
     * Generates cube volumes and rotates them depending on the given rotation
     *
     * @param world      The world
     * @param predicate  The predicate decides whether a block should be placed or not
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
     * @param callbacks  All callbacks are called once a block is placed
     */
    @SafeVarargs
    public final void rotatedCubeVolume(Level world, @Nullable Predicate<BlockPos> pred, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState blockState, int sizeWidth, int sizeHeight, int sizeDepth, int rotation, Consumer<BlockPos>... callbacks) {
        x -= width / 2;
        z -= depth / 2;
        switch (rotation) {
            case 0:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int xx = x + offsetX; xx < x + offsetX + sizeWidth; xx++)
                        for (int zz = z + offsetZ; zz < z + offsetZ + sizeDepth; zz++) {
                            BlockPos pos = new BlockPos(xx, yy, zz);
                            if(pred == null || pred.test(pos)) {
                                this.setBlockAndNotifyAdequately(world, pos, blockState);
                                for(Consumer<BlockPos> callback : callbacks) {
                                    callback.accept(pos);
                                }
                            }
                        }
                break;
            case 1:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int zz = z + depth - offsetX - 1; zz > z + depth - offsetX - sizeWidth - 1; zz--)
                        for (int xx = x + offsetZ; xx < x + offsetZ + sizeDepth; xx++) {
                            BlockPos pos = new BlockPos(xx, yy, zz);
                            if(pred == null || pred.test(pos)) {
                                this.setBlockAndNotifyAdequately(world, pos, blockState);
                                for(Consumer<BlockPos> callback : callbacks) {
                                    callback.accept(pos);
                                }
                            }
                        }
                break;
            case 2:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int xx = x + width - offsetX - 1; xx > x + width - offsetX - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetZ - 1; zz > z + depth - offsetZ - sizeDepth - 1; zz--) {
                            BlockPos pos = new BlockPos(xx, yy, zz);
                            if(pred == null || pred.test(pos)) {
                                this.setBlockAndNotifyAdequately(world, pos, blockState);
                                for(Consumer<BlockPos> callback : callbacks) {
                                    callback.accept(pos);
                                }
                            }
                        }
                break;
            case 3:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int zz = z + offsetX; zz < z + offsetX + sizeWidth; zz++)
                        for (int xx = x + width - offsetZ - 1; xx > x + width - offsetZ - sizeDepth - 1; xx--) {
                            BlockPos pos = new BlockPos(xx, yy, zz);
                            if(pred == null || pred.test(pos)) {
                                this.setBlockAndNotifyAdequately(world, pos, blockState);
                                for(Consumer<BlockPos> callback : callbacks) {
                                    callback.accept(pos);
                                }
                            }
                        }
                break;
        }
    }

    /**
     * Returns a rotated AABB
     * @param world
     * @param x
     * @param y
     * @param z
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param sizeWidth
     * @param sizeHeight
     * @param sizeDepth
     * @param rotation
     * @return
     */
    public final AABB rotatedAABB(Level world, double x, double y, double z, double offsetX, double offsetY, double offsetZ, double sizeWidth, double sizeHeight, double sizeDepth, int rotation) {
        x -= width / 2;
        z -= depth / 2;
        switch (rotation) {
            default:
            case 0:
                return new AABB(
                        x + offsetX, y + offsetY, z + offsetZ,
                        x + offsetX + sizeWidth, y + offsetY + sizeHeight, z + offsetZ + sizeDepth
                );
            case 1:
                return new AABB(
                        x + offsetZ, y + offsetY, z + depth - offsetX - sizeWidth - 1,
                        x + offsetZ + sizeDepth, y + offsetY + sizeHeight, z + depth - offsetX - 1
                );
            case 2:
                return new AABB(
                        x + width - offsetX - sizeWidth - 1, y + offsetY, z + depth - offsetZ - sizeDepth - 1,
                        x + width - offsetX - 1, y + offsetY + sizeHeight, z + depth - offsetZ - 1
                );
            case 3:
                return new AABB(
                        x + width - offsetZ - sizeDepth - 1, y + offsetY, z + offsetX,
                        x + width - offsetZ - 1, y + offsetY + sizeHeight, z + offsetX + sizeWidth
                );
        }
    }

    /**
     * Rotates the specified position
     * @param world
     * @param x
     * @param y
     * @param z
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param rotation
     * @return
     */
    public final BlockPos rotatePos(Level world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation) {
        x -= width / 2;
        z -= depth / 2;
        switch (rotation) {
            default:
            case 0:
                return new BlockPos(x + offsetX, y + offsetY, z + offsetZ);
            case 1:
                return new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1);
            case 2:
                return new BlockPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1);
            case 3:
                return new BlockPos(x + width - offsetZ - 1, y + offsetY, z + offsetX);
        }
    }

    /**
     * Rotates the specified position
     * @param world
     * @param x
     * @param y
     * @param z
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param rotation
     * @return
     */
    public final BlockPos rotatePosNoOffsets(Level world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation) {
        x -= width / 2;
        z -= depth / 2;
        switch (rotation) {
            default:
            case 0:
                return new BlockPos(x + offsetX, y + offsetY, z + offsetZ);
            case 1:
                return new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX);
            case 2:
                return new BlockPos(x + width - offsetX, y + offsetY, z + depth - offsetZ);
            case 3:
                return new BlockPos(x + width - offsetZ, y + offsetY, z + offsetX);
        }
    }

    /**
     * Rotates the specified position
     * @param world
     * @param pos
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @param rotation
     * @return
     */
    public final BlockPos rotatePos(Level world, BlockPos pos, int offsetX, int offsetY, int offsetZ, int rotation) {
        return this.rotatePos(world, pos.getX(), pos.getY(), pos.getZ(), offsetX, offsetY, offsetZ, rotation);
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
    @SafeVarargs
    public final void rotatedCubeVolumeExtendedDown(Level world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState blockState, int sizeWidth, int sizeHeight, int sizeDepth, int rotation, Consumer<BlockPos>... callbacks) {
        for(int w = 0; w < sizeWidth; w++) {
            for(int d = 0; d < sizeDepth; d++) {
                while (y + offsetY > 0 && isReplaceable(world, x, y, z, offsetX + w, offsetY - 1, offsetZ + d, rotation)) {
                    offsetY--;
                    sizeHeight++;
                }
                rotatedCubeVolume(world, x, y, z, offsetX + w, offsetY, offsetZ + d, blockState, 1, sizeHeight, 1, rotation, callbacks);
            }
        }
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
    public boolean isReplaceable(Level world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation) {
        x -= width / 2;
        z -= depth / 2;
        BlockPos pos;
        switch (rotation) {
            case 0:
                pos = this.getCheckPos(x + offsetX, y + offsetY, z + offsetZ);
                return world.isLoaded(pos) && (world.getBlockState(pos).getBlock() == Blocks.AIR || (replaceable != null && checkReplaceablePredicates(world.getBlockState(pos))));
            case 1:
                pos = this.getCheckPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1);
                return world.isLoaded(pos) && (world.getBlockState(pos).getBlock() == Blocks.AIR || (replaceable != null && checkReplaceablePredicates(world.getBlockState(pos))));
            case 2:
                pos = this.getCheckPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1);
                return world.isLoaded(pos) && (world.getBlockState(pos).getBlock() == Blocks.AIR || (replaceable != null && checkReplaceablePredicates(world.getBlockState(pos))));
            case 3:
                pos = this.getCheckPos(x + width - offsetZ - 1, y + offsetY, z + offsetX);
                return world.isLoaded(pos) && (world.getBlockState(pos).getBlock() == Blocks.AIR || (replaceable != null && checkReplaceablePredicates(world.getBlockState(pos))));
        }
        return false;
    }

    private boolean checkReplaceablePredicates(BlockState state) {
        for(Predicate<BlockState> replaceable : this.replaceable) {
            if(replaceable.test(state)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an area matches a surface type
     * @param world    The world
     * @param x        x to generate relative from
     * @param y        y to generate relative from
     * @param z        z to generate relative from
     * @param offsetX  Where to generate relative from the x
     * @param offsetY  Where to generate relative from the y
     * @param offsetZ  Where to generate relative from the z
     * @param rotation The rotation for the cube volume (0 to 3)
     * @param type the surface type
     * @return
     */
    public boolean rotatedCubeMatches(Level world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int sizeWidth, int sizeHeight, int sizeDepth,  int rotation, SurfaceType type) {
        x -= width / 2;
        z -= depth / 2;
        switch (rotation) {
            case 0:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int xx = x + offsetX; xx < x + offsetX + sizeWidth; xx++)
                        for (int zz = z + offsetZ; zz < z + offsetZ + sizeDepth; zz++) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !type.matches(world.getBlockState(this.getCheckPos(xx, yy, zz))))
                                return false;
                        }
                break;
            case 1:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int zz = z + sizeDepth - offsetX - 1; zz > z + sizeDepth - offsetX - sizeWidth - 1; zz--)
                        for (int xx = x + offsetZ; xx < x + offsetZ + sizeDepth; xx++) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !type.matches(world.getBlockState(this.getCheckPos(xx, yy, zz))))
                                return false;
                        }
                break;
            case 2:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int xx = x + sizeWidth - offsetX - 1; xx > x + sizeWidth - offsetX - sizeWidth - 1; xx--)
                        for (int zz = z + sizeDepth - offsetZ - 1; zz > z + sizeDepth - offsetZ - sizeDepth - 1; zz--) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !type.matches(world.getBlockState(this.getCheckPos(xx, yy, zz))))
                                return false;
                        }
                break;
            case 3:
                for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
                    for (int zz = z + offsetX; zz < z + offsetX + sizeWidth; zz++)
                        for (int xx = x + width - offsetZ - 1; xx > x + width - offsetZ - sizeDepth - 1; xx--) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !type.matches(world.getBlockState(this.getCheckPos(xx, yy, zz))))
                                return false;
                        }
                break;
        }
        return true;
    }

    /**
     * Generates a loot pot taking the rotation of a structure into a count
     *
     * @param world   The world
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
	/*
	public void rotatedLootPot(Level world, Random rand, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, int min, int max, int chance, @Nullable ResourceLocation lootTable) {
		x -= width / 2;
		z -= depth / 2;
		if (rand.nextInt(chance) == 0)
			return;
		switch (rotation) {
		case 0:
			generateLootPot(world, rand, new BlockPos(x + offsetX, y + offsetY, z + offsetZ), min, max, lootTable);
			break;
		case 1:
			generateLootPot(world, rand, new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1), min, max, lootTable);
			break;
		case 2:
			generateLootPot(world, rand, new BlockPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1), min, max, lootTable);
			break;
		case 3:
			generateLootPot(world, rand, new BlockPos(x + width - offsetZ - 1, y + offsetY, z + offsetX), min, max, lootTable);
			break;
		}
	}

	/**
	 * Generates a loot urn taking the rotation of a structure into a count
	 *
	 * @param world   The world
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
	/*
	public void rotatedLootUrn(Level world, Random rand, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, int min, int max, int chance, @Nullable ResourceLocation lootTable) {
		x -= width / 2;
		z -= depth / 2;
		if (rand.nextInt(chance) == 0)
			return;
		switch (rotation) {
		case 0:
			generateLootUrn(world, rand, new BlockPos(x + offsetX, y + offsetY, z + offsetZ), min, max, lootTable);
			break;
		case 1:
			generateLootUrn(world, rand, new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1), min, max, lootTable);
			break;
		case 2:
			generateLootUrn(world, rand, new BlockPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1), min, max, lootTable);
			break;
		case 3:
			generateLootUrn(world, rand, new BlockPos(x + width - offsetZ - 1, y + offsetY, z + offsetX), min, max, lootTable);
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
	/*
	public void rotatedLootChest(Level world, Random rand, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, int min, int max, int chance, int sequenceStart, @Nullable ResourceLocation lootTable) {
		x -= width / 2;
		z -= depth / 2;
		if (rand.nextInt(chance) == 0)
			return;
		switch (rotation) {
		case 0:
			generateLootChest(world, rand, new BlockPos(x + offsetX, y + offsetY, z + offsetZ), min, max, getStateFromRotation(sequenceStart, rotation, BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), EnumRotationSequence.CHEST), lootTable);
			break;
		case 1:
			generateLootChest(world, rand, new BlockPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1), min, max, getStateFromRotation(sequenceStart, rotation, BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), EnumRotationSequence.CHEST), lootTable);
			break;
		case 2:
			generateLootChest(world, rand, new BlockPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1), min, max, getStateFromRotation(sequenceStart, rotation, BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), EnumRotationSequence.CHEST), lootTable);
			break;
		case 3:
			generateLootChest(world, rand, new BlockPos(x + width - offsetZ - 1, y + offsetY, z + offsetX), min, max, getStateFromRotation(sequenceStart, rotation, BlockRegistry.WEEDWOOD_CHEST.getDefaultState(), EnumRotationSequence.CHEST), lootTable);
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
	/*
	public MobSpawnerLogicBetweenlands rotatedSpawner(Level world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation, String mob) {
		BlockPos pos = (new BlockPos(x, y, z)).add(-(width / 2), 0, -(depth / 2));
		BlockState spawner = BlockRegistry.MOB_SPAWNER.getDefaultState();
		switch (rotation) {
		case 0:
			pos = pos.add(offsetX, offsetY, offsetZ);
			this.setBlockAndNotifyAdequately(world, pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		case 1:
			pos = pos.add(offsetZ, offsetY, depth - offsetX - 1);
			this.setBlockAndNotifyAdequately(world, pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		case 2:
			pos = pos.add(width - offsetX - 1, offsetY, depth - offsetZ - 1);
			this.setBlockAndNotifyAdequately(world, pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		default:
		case 3:
			pos = pos.add(width - offsetZ - 1, offsetY, offsetX);
			this.setBlockAndNotifyAdequately(world, pos, spawner);
			BlockMobSpawnerBetweenlands.setMob(world, pos, mob);
			return BlockMobSpawnerBetweenlands.getLogic(world, pos);
		}

	}

	/**
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param offsetA
	 * @param offsetB
	 * @param offsetC
	 * @param sizeWidth
	 * @param sizeHeight
	 * @param sizeDepth
	 * @param direction
	 * @return
	 */
    public boolean rotatedCubeCantReplace(Level world, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {

        x -= width / 2;
        z -= depth / 2;
        switch (direction) {
            case 0 -> {
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++) {
                    for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++) {
                        for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, xx, yy, zz, 0, 0, 0, 0))
                                return true;
                        }
                    }
                }
            }
            case 1 -> {
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
                        for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, xx, yy, zz, 0, 0, 0, 0))
                                return true;
                        }
            }
            case 2 -> {
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
                        for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, xx, yy, zz, 0, 0, 0, 0))
                                return true;
                        }
            }
            case 3 -> {
                for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
                    for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
                        for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
                            if (!world.isLoaded(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, xx, yy, zz, 0, 0, 0, 0))
                                return true;
                        }
            }
        }

        return false;
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
	/*
	public void generateLootPot(Level world, Random random, BlockPos pos, int min, int max, @Nullable ResourceLocation lootTable) {
		this.setBlockAndNotifyAdequately(world, pos, getRandomLootPot(random));
		if(lootTable != null) {
			TileEntityLootPot lootPot = BlockLootPot.getTileEntity(world, pos);
			if(lootPot != null) {
				lootPot.setLootTable(lootTable, random.nextLong());
			}
		}
	}

	/**
	 * Generates a loot urn at a location
	 *
	 * @param world  The world
	 * @param random A Random
	 * @param pos    The pos to generate at
	 * @param min    The minimum amount of items
	 * @param max    The maximum amount of items
	 */
	/*
	public void generateLootUrn(Level world, Random random, BlockPos pos, int min, int max, @Nullable ResourceLocation lootTable) {
		this.setBlockAndNotifyAdequately(world, pos, getRandomLootUrn(random));
		if(lootTable != null) {
			TileEntityLootUrn lootPot = BlockLootUrn.getTileEntity(world, pos);
			if(lootPot != null) {
				lootPot.setLootTable(lootTable, random.nextLong());
			}
		}
	}

	/**
	 * Generates a loot chest at a location
	 *
	 * @param world  The world
	 * @param random A Random
	 * @param pos    The pos to generate at
	 * @param min    The minimum amount of items
	 * @param max    The maximum amount of items
	 */
	/*
	public void generateLootChest(Level world, Random random, BlockPos pos, int min, int max, BlockState state, @Nullable ResourceLocation lootTable) {
		this.setBlockAndNotifyAdequately(world, pos, state);
		if(lootTable != null) {
			TileEntity chest = world.getTileEntity(pos);
			if (chest instanceof TileEntityChest) {
				((TileEntityChest)chest).setLootTable(lootTable, random.nextLong());
			}
		}
	}


	/**
	 * Gets the meta from a sequence depending on the rotation
	 *
	 * @param start                where to start in the sequence
	 * @param rotation             the rotation
	 * @param enumRotationSequence which sequence to use
	 * @return the meta of corresponding to the rotation
	 */
    @SuppressWarnings("deprecation")
    public BlockState getStateFromRotation(int start, int rotation, BlockState state, EnumRotationSequence enumRotationSequence) {
        return state.getBlock().defaultBlockState(); //.getStateFromMeta(enumRotationSequence.sequence[(rotation + start) % enumRotationSequence.sequence.length]);
    }


    /**
     * Gives one of 3 different styles of loot post with a random rotation
     *
     * @param random a random
     * @return the blockstate of one of the loot pots
     */
	/*
	public BlockState getRandomLootPot(Random random) {
		int randDirection = random.nextInt(4) + 2;

		switch (random.nextInt(3)) {
		case 0:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_1).withProperty(BlockLootPot.FACING, EnumFacing.byIndex(randDirection));
		case 1:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_2).withProperty(BlockLootPot.FACING, EnumFacing.byIndex(randDirection));
		default:
			return BlockRegistry.LOOT_POT.getDefaultState().withProperty(BlockLootPot.VARIANT, BlockLootPot.EnumLootPot.POT_3).withProperty(BlockLootPot.FACING, EnumFacing.byIndex(randDirection));
		}
	}*/

    /**
     * Gives one of 3 different styles of loot urns with a random rotation
     *
     * @param random a random
     * @return the blockstate of one of the loot urns
     */
	/*
	public BlockState getRandomLootUrn(Random random) {
		int randDirection = random.nextInt(4) + 2;

		switch (random.nextInt(3)) {
		case 0:
			return BlockRegistry.LOOT_URN.getDefaultState().withProperty(BlockLootUrn.VARIANT, BlockLootUrn.EnumLootUrn.URN_1).withProperty(BlockLootUrn.FACING, EnumFacing.byIndex(randDirection));
		case 1:
			return BlockRegistry.LOOT_URN.getDefaultState().withProperty(BlockLootUrn.VARIANT, BlockLootUrn.EnumLootUrn.URN_2).withProperty(BlockLootUrn.FACING, EnumFacing.byIndex(randDirection));
		default:
			return BlockRegistry.LOOT_URN.getDefaultState().withProperty(BlockLootUrn.VARIANT, BlockLootUrn.EnumLootUrn.URN_3).withProperty(BlockLootUrn.FACING, EnumFacing.byIndex(randDirection));
		}
	}
	*/

    protected void setBlockAndNotifyAdequately(Level worldIn, BlockPos pos, BlockState state) {
        if (this.doBlockNotify) {
            worldIn.setBlock(pos, state, 3 | 16);
        } else {
            worldIn.setBlock(pos, state, 2 | 16);
        }
    }

    public static boolean canShortThingsGenerateHere(ChunkGeneratorBetweenlands gen, BlockPos pos) {
        return gen.evalTreeNoise(pos.getX() * 0.01, pos.getZ() * 0.01) > -0.25;
    }
}
