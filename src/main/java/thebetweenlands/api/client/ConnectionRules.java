package thebetweenlands.api.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ConnectionRules {
	/**
	 * Can be used to create a block access cache to speed up lookups
	 * @param world
	 * @param pos The position of the connected texture block
	 * @return same as input world or some kind of cached block access
	 */
	default BlockAndTintGetter getBlockAccessCache(BlockAndTintGetter world, BlockPos pos) {
		return world;
	}

	/**
	 * Returns whether the face can connect to the specified block pos
	 * @param world
	 * @param pos The position of this connected texture block
	 * @param face The face of this connected texture block
	 * @param to The position that it tries to connect to
	 * @return
	 */
	boolean canTextureConnectTo(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to);

	/**
	 * Returns whether the face can connect through the specified block pos.
	 * The default implementation lets the texture connect through any non solid
	 * sides
	 * @param world
	 * @param pos The position of this connected texture block
	 * @param face The face of this connected texture block
	 * @param to The position that it tries to connect through
	 * @return
	 */
	default boolean canConnectThrough(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to) {
		//The block tries to connect to one of its own other sides
		Axis axis = face.getAxis();
		BlockPos planeOffset = new BlockPos(axis == Axis.X ? 0 : (to.getX() - pos.getX()), axis == Axis.Y ? 0 : (to.getY() - pos.getY()), axis == Axis.Z ? 0 : (to.getZ() - pos.getZ()));
		boolean onSamePlane = (axis != Axis.X || (to.getX() - pos.getX()) == 0) && (axis != Axis.Y || (to.getY() - pos.getY()) == 0) && (axis != Axis.Z || (to.getZ() - pos.getZ()) == 0);
		if(Math.abs(planeOffset.getX()) + Math.abs(planeOffset.getY()) + Math.abs(planeOffset.getZ()) > 1) {
			MutableBlockPos directNeighbour = new MutableBlockPos();
			//Diagonal block connection
			for(Direction offsetFace : Direction.values()) {
				//Check if offsetFace points to a neighbour of the connected texture block
				if(Math.abs(planeOffset.getX() + offsetFace.getStepX()) + Math.abs(planeOffset.getY() + offsetFace.getStepY()) + Math.abs(planeOffset.getZ() + offsetFace.getStepZ()) == 1) {
					//Check if either sides of the diagonal block that point to a direct neighbour of the connected texture block are solid
					if(doesOccludeSide(world, pos, face, to, offsetFace)) return false;
					if(onSamePlane) {
						if(doesOccludeSide(world, pos, face, to, face)) {
							return false;
						}
					} else {
						if(doesOccludeSide(world, pos, face, to, face.getOpposite())) {
							return false;
						}
					}

					directNeighbour.set(to.getX() + offsetFace.getStepX(), to.getY() + offsetFace.getStepY(), to.getZ() + offsetFace.getStepZ());
					//Check if direct neighbour can be connected to, if not it needs to check if it lets a conncetion through
					if(!this.canTextureConnectTo(world, pos, face, directNeighbour)) {
						//Check if either sides of the direct neighbour block of the connected texture block are solid
						if(doesOccludeSide(world, pos, face, directNeighbour, offsetFace.getOpposite())) return false;
						if(onSamePlane) {
							if(doesOccludeSide(world, pos, face, directNeighbour, face)) {
								return false;
							}
						} else {
							if(doesOccludeSide(world, pos, face, directNeighbour, face.getOpposite())) {
								return false;
							}
						}
					}
				}
			}
			return true;
		} else {
			//Direct neighbour
			Direction offsetDir = Direction.getNearest(planeOffset.getX(), planeOffset.getY(), planeOffset.getZ());
			final boolean cached = !isSideSolid(world, to, offsetDir.getOpposite());
			if(onSamePlane) {
				return cached && !isSideSolid(world, to, face);
			} else {
				return cached && !isSideSolid(world, to, face.getOpposite());
			}
		}
	}

	default boolean doesOccludeSide(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to, Direction toFace) {
		return isSideSolid(world, to, toFace);
	}

	static boolean isSideSolid(BlockAndTintGetter world, BlockPos pos, Direction face) {
		return isSideSolid(world, world.getBlockState(pos), pos, face);
	}

	static boolean isSideSolid(BlockAndTintGetter world, BlockState state, BlockPos pos, Direction face) {
		return state.canOcclude() && Block.isFaceFull(state.getFaceOcclusionShape(world, pos, face), face);
	}
}