package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class WorldGenHelper<T extends FeatureConfiguration> extends Feature<T> {

	protected int width;
	protected int height;
	protected int depth;

	public WorldGenHelper(Codec<T> codec) {
		super(codec);
	}

	/**
	 * @see #rotatedCubeVolume(WorldGenLevel, Predicate, int, int, int, int, int, int, BlockState, int, int, int, Direction, Consumer...)
	 * @param level
	 * @param x
	 * @param y
	 * @param z
	 * @param offsetX
	 * @param offsetY
	 * @param offsetZ
	 * @param state
	 * @param sizeWidth
	 * @param sizeHeight
	 * @param sizeDepth
	 * @param direction
	 * @param callbacks
	 */
	@SafeVarargs
	public final void rotatedCubeVolume(WorldGenLevel level, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState state, int sizeWidth, int sizeHeight, int sizeDepth, Direction direction, Consumer<BlockPos>... callbacks) {
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
	 * @param rotation   The rotation for the cube volume (0 to 3)
	 * @param callbacks  All callbacks are called once a block is placed
	 */
	@SafeVarargs
	public final void rotatedCubeVolume(WorldGenLevel level, Predicate<BlockPos> pred, int x, int y, int z, int offsetX, int offsetY, int offsetZ, BlockState state, int sizeWidth, int sizeHeight, int sizeDepth, Direction rotation, Consumer<BlockPos>... callbacks) {
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
}
