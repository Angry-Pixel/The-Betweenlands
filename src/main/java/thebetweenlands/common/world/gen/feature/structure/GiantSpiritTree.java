package thebetweenlands.common.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.FeatureHelper;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;
import thebetweenlands.common.world.gen.feature.legacy.WorldGenSpiritTree;

import javax.annotation.Nullable;
import java.util.*;

public class GiantSpiritTree extends FeatureHelper<FeatureHelperConfiguration> {
	public static final int RADIUS_INNER_CIRLCE = 6;
	public static final int RADIUS_OUTER_CIRCLE = 14;

	private static final ThreadLocal<Boolean> CASCADING_GEN_MUTEX = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	public GiantSpiritTree(Codec<FeatureHelperConfiguration> p_65786_) {
		super(p_65786_);
	}

	@Override
	public boolean place(FeaturePlaceContext<FeatureHelperConfiguration> context) {
		WorldGenLevel Level = context.level();
		BlockPos position = context.origin();
		Random rand = context.random();

		if (CASCADING_GEN_MUTEX.get()) {
			return false;
		}

		CASCADING_GEN_MUTEX.set(true);

		try {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(Level);
			LocationSpiritTree location = new LocationSpiritTree(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position));
			location.addBounds(new AABB(new BlockPos(position)).inflate(14 + 18, 16, 14 + 18).offset(0, 6, 0));
			location.setLayer(0);
			location.setSeed(rand.nextLong());
			location.setVisible(true);

			WorldGenSpiritTree genSpiritTree = new WorldGenSpiritTree(location.getGuard(), location);
			if (genSpiritTree.generate(Level, rand, position)) {
				this.generateWispCircle(Level, rand, position, RADIUS_INNER_CIRLCE, 1, 2, location);
				this.generateWispCircle(Level, rand, position, RADIUS_OUTER_CIRCLE, 1, 1, location);

				int rootsGenerated = 0;
				for (int i = 0; i < 80; i++) {
					double dir = rand.nextDouble() * Math.PI * 2;
					double dx = Math.cos(dir);
					double dz = Math.sin(dir);
					double bx = dx * 16 + dx * rand.nextDouble() * 16;
					double bz = dz * 16 + dz * rand.nextDouble() * 16;
					BlockPos root = position.offset(bx, 0, bz);
					root = this.findGroundPosition(Level, root);
					if (root != null && Level.getBlockState(root).isAir() && Level.getBlockState(root.above()).isAir()) {
						this.generateRoot(Level, rand, root, genSpiritTree, location);
						if (rootsGenerated++ > 12) {
							break;
						}
					}
				}

				for (int i = 0; i < 120; i++) {
					double dir = rand.nextDouble() * Math.PI * 2;
					double dx = Math.cos(dir);
					double dz = Math.sin(dir);
					double bx = dx * 16 + dx * rand.nextDouble() * 16;
					double bz = dz * 16 + dz * rand.nextDouble() * 16;
					BlockPos root = position.offset(bx, 0, bz);
					root = this.findGroundPosition(Level, root);
					if (root != null && Level.getBlockState(root).isAir() && Level.getBlockState(root.above()).isAir()) {
						int height = 2 + rand.nextInt(4);
						for (int yo = 0; yo < height; yo++) {
							this.setBlock(Level, root.above(yo), BlockRegistry.ROOT.getDefaultState(), location);
						}
					}
				}

				this.trySpawnFace(Level, rand, new EntitySpiritTreeFaceLarge(level), location.getLargeFacePositions());

				for (int i = 0; i < 8; i++) {
					this.trySpawnFace(Level, rand, new EntitySpiritTreeFaceSmall(level), location.getSmallFacePositions());
				}

				location.setDirty(true);
				worldStorage.getLocalStorageHandler().addLocalStorage(location);

				return true;
			}

			return false;
		} finally {
			CASCADING_GEN_MUTEX.set(false);
		}
	}

	private void trySpawnFace(WorldGenLevel Level, Random rand, EntitySpiritTreeFace face, List<BlockPos> locations) {
		BlockPos faceAnchor = null;
		Direction faceFacing = null;

		List<BlockPos> largeFacePositions = new ArrayList<>();
		largeFacePositions.addAll(locations);
		Collections.shuffle(largeFacePositions, rand);
		largeFaceLoop:
		for (BlockPos anchor : largeFacePositions) {
			List<Direction> facings = new ArrayList<>();
			facings.addAll(Arrays.asList(EnumFacing.HORIZONTALS));
			Collections.shuffle(facings, rand);
			for (Direction facing : facings) {
				if (face.checkAnchorAt(anchor, facing, EnumFacing.UP, AnchorChecks.ALL) == 0) {
					faceAnchor = anchor;
					faceFacing = facing;
					break largeFaceLoop;
				}
			}
		}

		if (faceAnchor != null && faceFacing != null) {
			face.onInitialSpawn(world.getDifficultyForLocation(faceAnchor), null);
			face.setPositionToAnchor(faceAnchor, faceFacing, EnumFacing.UP);
			Level.spawnEntity(face);
		}
	}

	@Nullable
	private BlockPos findGroundPosition(WorldGenLevel Level, BlockPos pos) {
		boolean hadAir = false;
		for (int yo = 6; yo >= -6; yo--) {
			BlockPos offsetPos = pos.above(yo);
			BlockState state = Level.getBlockState(offsetPos);
			if (hadAir && SurfaceType.MIXED_GROUND.test(state)) {
				return offsetPos.above();
			}
			if (state.getBlock().isAir(state, Level, offsetPos)) {
				hadAir = true;
			}
		}
		return null;
	}

	private void generateWispCircle(WorldGenLevel Level, Random rand, BlockPos center, int radius, int minHeight, int heightVar, LocationSpiritTree location) {
		List<BlockPos> circle = BlockShapeUtils.getCircle(center, radius, new ArrayList<>());
		for (int i = 0; i < circle.size(); i += 2 + rand.nextInt(2)) {
			if (i == circle.size() - 1) {
				break;
			}
			BlockPos pos = circle.get(i);
			pos = this.findGroundPosition(Level, pos);
			if (pos != null && (Level.getBlockState(pos).isAir() || Level.getBlockState(pos).getBlock() instanceof IPlantable)) {
				BlockPos wall = pos.below();
				this.setBlock(Level, wall, BlockRegistry.MOSSY_BETWEENSTONE_BRICKS.getDefaultState(), location);
				int height = minHeight + rand.nextInt(heightVar + 1);
				for (int yo = 0; yo < height; yo++) {
					wall = pos.above(yo);
					this.setBlock(Level, wall, BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL.getDefaultState(), location);
				}
				BlockPos wisp = pos.above(height);
				if (rand.nextInt(4) == 0) {
					this.setBlock(Level, wisp, BlockRegistry.WISP.getDefaultState(), location);
					location.addGeneratedWispPosition(wisp);
				} else {
					location.addNotGeneratedWispPosition(wisp);
				}
			}
		}
	}

	private void generateRoot(WorldGenLevel Level, Random rand, BlockPos pos, WorldGenSpiritTree tree, LocationSpiritTree location) {
		List<BlockPos> potentialBlocks = tree.generateBranchPositions(rand, pos, rand.nextInt(7), 32, 0.4D, 0.3D, (i, remainingBlocks) -> i < 2 ? 1 : (i > 4 ? -1 : 0), (i, length) -> true);
		int length = 0;
		for (int i = 0; i < potentialBlocks.size(); i++) {
			BlockPos block = potentialBlocks.get(i);
			if (i > 2 && !Level.getBlockState(block).isAir()) {
				length = i + 1;
				break;
			}
		}
		BlockPos prevPos = null;
		for (int i = 0; i < length; i++) {
			BlockPos block = potentialBlocks.get(i);
			if (prevPos != null) {
				int xo = block.getX() - prevPos.getX();
				int yo = block.getY() - prevPos.getY();
				int zo = block.getZ() - prevPos.getZ();
				int moves = Math.abs(xo) + Math.abs(yo) + Math.abs(zo);
				if (moves > 1) {
					List<BlockPos> choices = new ArrayList<>();
					if (xo != 0) {
						choices.add(new BlockPos(xo, 0, 0));
					}
					if (yo != 0) {
						choices.add(new BlockPos(0, yo, 0));
					}
					if (zo != 0) {
						choices.add(new BlockPos(0, 0, zo));
					}
					BlockPos filler = prevPos;
					for (int j = 0; j < moves; j++) {
						filler = filler.Direction(choices.remove(rand.nextInt(choices.size())));
						this.setBlock(Level, filler, BlockRegistry.LOG_SPIRIT_TREE.getDefaultState(), location);
					}
				}
			}
			this.setBlock(Level, block, BlockRegistry.LOG_SPIRIT_TREE.getDefaultState(), location);
			prevPos = block;
		}
	}

	protected void setBlock(WorldGenLevel Level, BlockPos pos, BlockState state, LocationSpiritTree location) {
		this.setBlockAndNotifyAdequately(Level, pos, state);

		if (state.getBlock() != BlockRegistry.WISP) {
			location.getGuard().setGuarded(Level, pos, true);
		}

		if (state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE) {
			location.addSmallFacePosition(pos);
		}
	}
}
