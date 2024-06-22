package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class FeatureHelper<FHC extends FeatureHelperConfiguration> extends Feature<FHC> {
	// Set in configuration
	public boolean doBlockNotify;

	/**
	 * Enums to get the metadata of a stair with when a structure is rotated
	 */
	public enum EnumRotationSequence {
		//TODO: This ought to be removed and replaced with the proper block states at some point
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

	public BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

	public BlockPos.MutableBlockPos getCheckPos(int x, int y, int z) {
		this.checkPos.set(x, y, z);
		return this.checkPos;
	}

	public FeatureHelper(Codec<FHC> p_65786_) {
		super(p_65786_);
	}

	/**
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
	public final void rotatedCubeVolume(WorldGenLevel world, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState blockState, int sizeWidth, int sizeHeight, int sizeDepth, int rotation, Consumer<BlockPos>... callbacks) {
		this.rotatedCubeVolume(world, null, x, y, z, offsetX, offsetY, offsetZ, blockState, sizeWidth, sizeHeight, sizeDepth, rotation, callbacks);
	}

	/**
	 * Generates cube volumes and rotates them depending on the given rotation
	 *
	 * @param world      The world
	 * @param pred       The predicate decides whether a block should be placed or not
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
	public final void rotatedCubeVolume(WorldGenLevel world, @Nullable Predicate<BlockPos> pred, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState blockState, int sizeWidth, int sizeHeight, int sizeDepth, int rotation, Consumer<BlockPos>... callbacks) {
		x -= width / 2;
		z -= depth / 2;
		switch (rotation) {
			case 0:
				for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++)
					for (int xx = x + offsetX; xx < x + offsetX + sizeWidth; xx++)
						for (int zz = z + offsetZ; zz < z + offsetZ + sizeDepth; zz++) {
							BlockPos pos = new BlockPos(xx, yy, zz);
							if (pred == null || pred.test(pos)) {
								this.setBlockAndNotifyAdequately(world, pos, blockState);
								for (Consumer<BlockPos> callback : callbacks) {
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
							if (pred == null || pred.test(pos)) {
								this.setBlockAndNotifyAdequately(world, pos, blockState);
								for (Consumer<BlockPos> callback : callbacks) {
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
							if (pred == null || pred.test(pos)) {
								this.setBlockAndNotifyAdequately(world, pos, blockState);
								for (Consumer<BlockPos> callback : callbacks) {
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
							if (pred == null || pred.test(pos)) {
								this.setBlockAndNotifyAdequately(world, pos, blockState);
								for (Consumer<BlockPos> callback : callbacks) {
									callback.accept(pos);
								}
							}
						}
				break;
		}
	}

	/**
	 * Returns a rotated AABB
	 *
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
	public final AABB rotatedAABB(WorldGenLevel world, double x, double y, double z, double offsetX, double offsetY, double offsetZ, double sizeWidth, double sizeHeight, double sizeDepth, int rotation) {
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
	public boolean rotatedCubeCantReplace(WorldGenLevel world, FeaturePlaceContext<FeatureHelperConfiguration> context, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
		x -= width / 2;
		z -= depth / 2;
		switch (direction) {
			case 0 -> {
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++) {
					for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++) {
						for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
							if (!world.ensureCanWrite(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, context, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
					}
				}
			}
			case 1 -> {
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
					for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
						for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
							if (!world.ensureCanWrite(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, context, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
			}
			case 2 -> {
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
					for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
						for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
							if (!world.ensureCanWrite(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, context, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
			}
			case 3 -> {
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
					for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
						for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
							if (!world.ensureCanWrite(this.getCheckPos(xx, yy, zz)) || !isReplaceable(world, context, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
			}
		}
		return false;
	}

	protected void setBlockAndNotifyAdequately(WorldGenLevel worldIn, BlockPos pos, BlockState state) {
		if (this.doBlockNotify) {
			worldIn.setBlock(pos, state, 3 | 16);
		} else {
			worldIn.setBlock(pos, state, 2 | 16);
		}
	}

	// Stand alone isReplaceable check
	public boolean isReplaceable(FeaturePlaceContext<FeatureHelperConfiguration> context, BlockState in) {
		return context.config().replaceable.stream().anyMatch(predicate -> predicate.target.test(in, context.random()));
	}

	public boolean isReplaceable(WorldGenLevel world, FeaturePlaceContext<FeatureHelperConfiguration> context, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation) {
		x -= width / 2;
		z -= depth / 2;
		BlockPos pos;
		switch (rotation) {
			case 0:
				pos = this.getCheckPos(x + offsetX, y + offsetY, z + offsetZ);
				return world.ensureCanWrite(pos) && (world.getBlockState(pos).canBeReplaced(Fluids.EMPTY) || (context.config().replaceable != null && isReplaceable(context, world.getBlockState(pos))));
			case 1:
				pos = this.getCheckPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1);
				return world.ensureCanWrite(pos) && (world.getBlockState(pos).canBeReplaced(Fluids.EMPTY) || (context.config().replaceable != null && isReplaceable(context, world.getBlockState(pos))));
			case 2:
				pos = this.getCheckPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1);
				return world.ensureCanWrite(pos) && (world.getBlockState(pos).canBeReplaced(Fluids.EMPTY) || (context.config().replaceable != null && isReplaceable(context, world.getBlockState(pos))));
			case 3:
				pos = this.getCheckPos(x + width - offsetZ - 1, y + offsetY, z + offsetX);
				return world.ensureCanWrite(pos) && (world.getBlockState(pos).canBeReplaced(Fluids.EMPTY) || (context.config().replaceable != null && isReplaceable(context, world.getBlockState(pos))));
		}
		return false;
	}
}
