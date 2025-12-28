package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class WorldGenHelper<T extends FeatureConfiguration> extends Feature<T> {

	protected int width;
	protected int height;
	protected int depth;
	protected List<Predicate<BlockState>> replaceable = new ArrayList<>();

	private BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

	protected BlockPos.MutableBlockPos getCheckPos(int x, int y, int z) {
		this.checkPos.set(x, y, z);
		return this.checkPos;
	}

	public WorldGenHelper(Codec<T> codec) {
		super(codec);
	}

	/**
	 * Generates cube volumes and rotates them depending on the given rotation
	 *
	 * @param level      The world
	 * @param x          x to generate relative from
	 * @param y          y to generate relative from
	 * @param z          z to generate relative from
	 * @param offsetX    Where to generate relative from the x
	 * @param offsetY    Where to generate relative from the y
	 * @param offsetZ    Where to generate relative from the z
	 * @param state      The block to generate
	 * @param sizeWidth  The width of the cube volume
	 * @param sizeHeight The height of the cube volume
	 * @param sizeDepth  The depth of the cube volume
	 * @param direction  The direction the cube generates in
	 * @param callbacks  All callbacks are called once a block is placed
	 */
	@SafeVarargs
	public final void rotatedCubeVolume(LevelAccessor level, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState state, int sizeWidth, int sizeHeight, int sizeDepth, Direction direction, Consumer<BlockPos>... callbacks) {
		this.rotatedCubeVolume(level, null, x, y, z, offsetX, offsetY, offsetZ, state, sizeWidth, sizeHeight, sizeDepth, direction, callbacks);
	}

	/**
	 * Generates cube volumes and rotates them depending on the given rotation
	 *
	 * @param level      The world
	 * @param pred       The predicate decides whether a block should be placed or not
	 * @param x          x to generate relative from
	 * @param y          y to generate relative from
	 * @param z          z to generate relative from
	 * @param offsetX    Where to generate relative from the x
	 * @param offsetY    Where to generate relative from the y
	 * @param offsetZ    Where to generate relative from the z
	 * @param state      The block to generate
	 * @param sizeWidth  The width of the cube volume
	 * @param sizeHeight The height of the cube volume
	 * @param sizeDepth  The depth of the cube volume
	 * @param rotation   The direction the cube generates in
	 * @param callbacks  All callbacks are called once a block is placed
	 */
	@SafeVarargs
	@SuppressWarnings("fallthrough")
	public final void rotatedCubeVolume(LevelAccessor level, @Nullable Predicate<BlockPos> pred, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState state, int sizeWidth, int sizeHeight, int sizeDepth, Direction rotation, Consumer<BlockPos>... callbacks) {
		x -= width / 2;
		z -= depth / 2;

		switch (rotation) {
			case WEST: {
				for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++) {
					for (int zz = z + depth - offsetX - 1; zz > z + depth - offsetX - sizeWidth - 1; zz--) {
						for (int xx = x + offsetZ; xx < x + offsetZ + sizeDepth; xx++) {
							BlockPos pos = new BlockPos(xx, yy, zz);
							if(pred == null || pred.test(pos)) {
								this.setBlock(level, pos, state);
								for(Consumer<BlockPos> callback : callbacks) {
									callback.accept(pos);
								}
							}
						}
					}
				}
			}
			case NORTH: {
				for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++) {
					for (int xx = x + width - offsetX - 1; xx > x + width - offsetX - sizeWidth - 1; xx--) {
						for (int zz = z + depth - offsetZ - 1; zz > z + depth - offsetZ - sizeDepth - 1; zz--) {
							BlockPos pos = new BlockPos(xx, yy, zz);
							if(pred == null || pred.test(pos)) {
								this.setBlock(level, pos, state);
								for(Consumer<BlockPos> callback : callbacks) {
									callback.accept(pos);
								}
							}
						}
					}
				}
			}
			case EAST: {
				for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++) {
					for (int zz = z + offsetX; zz < z + offsetX + sizeWidth; zz++) {
						for (int xx = x + width - offsetZ - 1; xx > x + width - offsetZ - sizeDepth - 1; xx--) {
							BlockPos pos = new BlockPos(xx, yy, zz);
							if(pred == null || pred.test(pos)) {
								this.setBlock(level, pos, state);
								for(Consumer<BlockPos> callback : callbacks) {
									callback.accept(pos);
								}
							}
						}
					}
				}
			}
			default: {
				for (int yy = y + offsetY; yy < y + offsetY + sizeHeight; yy++) {
					for (int xx = x + offsetX; xx < x + offsetX + sizeWidth; xx++) {
						for (int zz = z + offsetZ; zz < z + offsetZ + sizeDepth; zz++) {
							BlockPos pos = new BlockPos(xx, yy, zz);
							if(pred == null || pred.test(pos)) {
								this.setBlock(level, pos, state);
								for(Consumer<BlockPos> callback : callbacks) {
									callback.accept(pos);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * checks if a block is replaceable
	 *
	 * @param level    The level
	 * @param x        x to generate relative from
	 * @param y        y to generate relative from
	 * @param z        z to generate relative from
	 * @param offsetX  Where to generate relative from the x
	 * @param offsetY  Where to generate relative from the y
	 * @param offsetZ  Where to generate relative from the z
	 * @param rotation The rotation for the cube volume (0 to 3)
	 * @return whether or not it is replaceable
	 */
	public boolean isReplaceable(LevelAccessor level, int x, int y, int z, int offsetX, int offsetY, int offsetZ, int rotation) {
		x -= width / 2;
		z -= depth / 2;
		BlockPos pos;
		return switch (rotation) {
			case 0 -> {
				pos = this.getCheckPos(x + offsetX, y + offsetY, z + offsetZ);
				yield level.isAreaLoaded(pos, 1) && (level.getBlockState(pos).canBeReplaced() || (replaceable != null && checkReplaceablePredicates(level.getBlockState(pos))));
			}
			case 1 -> {
				pos = this.getCheckPos(x + offsetZ, y + offsetY, z + depth - offsetX - 1);
				yield level.isAreaLoaded(pos, 1) && (level.getBlockState(pos).canBeReplaced() || (replaceable != null && checkReplaceablePredicates(level.getBlockState(pos))));
			}
			case 2 -> {
				pos = this.getCheckPos(x + width - offsetX - 1, y + offsetY, z + depth - offsetZ - 1);
				yield level.isAreaLoaded(pos, 1) && (level.getBlockState(pos).canBeReplaced() || (replaceable != null && checkReplaceablePredicates(level.getBlockState(pos))));
			}
			case 3 -> {
				pos = this.getCheckPos(x + width - offsetZ - 1, y + offsetY, z + offsetX);
				yield level.isAreaLoaded(pos, 1) && (level.getBlockState(pos).canBeReplaced() || (replaceable != null && checkReplaceablePredicates(level.getBlockState(pos))));
			}
			default -> false;
		};
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
	 *
	 * @param level
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
	public boolean rotatedCubeCantReplace(LevelAccessor level, int x, int y, int z, int offsetA, int offsetB, int offsetC, int sizeWidth, int sizeHeight, int sizeDepth, int direction) {
		x -= width / 2;
		z -= depth / 2;
		switch (direction) {
			case 0:
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
					for (int xx = x + offsetA; xx < x + offsetA + sizeWidth; xx++)
						for (int zz = z + offsetC; zz < z + offsetC + sizeDepth; zz++) {
							if (!level.isAreaLoaded(this.getCheckPos(xx, yy, zz), 1) || !isReplaceable(level, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
				break;
			case 1:
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
					for (int zz = z + depth - offsetA - 1; zz > z + depth - offsetA - sizeWidth - 1; zz--)
						for (int xx = x + offsetC; xx < x + offsetC + sizeDepth; xx++) {
							if (!level.isAreaLoaded(this.getCheckPos(xx, yy, zz), 1) || !isReplaceable(level, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
				break;
			case 2:
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
					for (int xx = x + width - offsetA - 1; xx > x + width - offsetA - sizeWidth - 1; xx--)
						for (int zz = z + depth - offsetC - 1; zz > z + depth - offsetC - sizeDepth - 1; zz--) {
							if (!level.isAreaLoaded(this.getCheckPos(xx, yy, zz), 1) || !isReplaceable(level, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
				break;
			case 3:
				for (int yy = y + offsetB; yy < y + offsetB + sizeHeight; yy++)
					for (int zz = z + offsetA; zz < z + offsetA + sizeWidth; zz++)
						for (int xx = x + width - offsetC - 1; xx > x + width - offsetC - sizeDepth - 1; xx--) {
							if (!level.isAreaLoaded(this.getCheckPos(xx, yy, zz), 1) || !isReplaceable(level, xx, yy, zz, 0, 0, 0, 0))
								return true;
						}
				break;
		}
		return false;
	}
}
