package thebetweenlands.common.world.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.entity.monster.spirit_tree.LargeSpiritTreeFace;
import thebetweenlands.common.entity.monster.spirit_tree.SmallSpiritTreeFace;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.StructureTypeRegistry;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.common.world.gen.feature.tree.SpiritTree;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;
import thebetweenlands.util.BlockShapeUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpiritTreePiece extends ScatteredFeaturePiece {

	public static final int RADIUS_INNER_CIRCLE = 6;
	public static final int RADIUS_OUTER_CIRCLE = 14;

	public SpiritTreePiece(RandomSource random, int x, int z) {
		super(StructureTypeRegistry.SPIRIT_TREE_PIECE.get(), x, 128, z, 64, 16, 64, getRandomHorizontalDirection(random));
	}

	public SpiritTreePiece(StructurePieceSerializationContext context, CompoundTag tag) {
		super(StructureTypeRegistry.SPIRIT_TREE_PIECE.get(), tag);
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
		if (this.updateHeightPositionToLowestGroundHeight(level, -random.nextInt(3))) {
			ILocationGuard guard = null;
			LocationSpiritTree location = null;

			BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(level);
			if (storage != null) {
				location = new LocationSpiritTree(storage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos));
				location.addBounds(new AABB(new BlockPos(pos)).inflate(14 + 18, 16, 14 + 18).move(0, 6, 0));
				location.setLayer(0);
				location.setSeed(random.nextLong());
				location.setVisible(true);
				guard = location.getGuard();
			}

			SpiritTree genSpiritTree = new SpiritTree(NoneFeatureConfiguration.CODEC, guard, location);
			if (genSpiritTree.place(NoneFeatureConfiguration.INSTANCE, level, generator, random, pos)) {
				this.generateWispCircle(level, random, pos, RADIUS_INNER_CIRCLE, 1, 2, location);
				this.generateWispCircle(level, random, pos, RADIUS_OUTER_CIRCLE, 1, 1, location);

				int rootsGenerated = 0;
				for (int i = 0; i < 80; i++) {
					float dir = random.nextFloat() * Mth.PI * 2;
					float dx = Mth.cos(dir);
					float dz = Mth.sin(dir);
					int bx = (int) (dx * 16 + dx * random.nextDouble() * 16);
					int bz = (int) (dz * 16 + dz * random.nextDouble() * 16);
					BlockPos root = pos.offset(bx, 0, bz);
					root = this.findGroundPosition(level, root);
					if (root != null && level.isEmptyBlock(root) && level.isEmptyBlock(root.above())) {
						this.generateRoot(level, random, root, genSpiritTree, location);
						if (rootsGenerated++ > 12) {
							break;
						}
					}
				}

				for (int i = 0; i < 120; i++) {
					float dir = random.nextFloat() * Mth.PI * 2;
					float dx = Mth.cos(dir);
					float dz = Mth.sin(dir);
					int bx = (int) (dx * 16 + dx * random.nextFloat() * 16);
					int bz = (int) (dz * 16 + dz * random.nextFloat() * 16);
					BlockPos root = pos.offset(bx, 0, bz);
					root = this.findGroundPosition(level, root);
					if (root != null && level.isEmptyBlock(root) && level.isEmptyBlock(root.above())) {
						int height = 2 + random.nextInt(4);
						for (int yo = 0; yo < height; yo++) {
							this.setBlock(level, root.above(yo), BlockRegistry.ROOT.get().defaultBlockState(), location);
						}
					}
				}

				if (storage != null) {
					genSpiritTree.trySpawnFace(level, new LargeSpiritTreeFace(EntityRegistry.LARGE_SPIRIT_TREE_FACE.get(), level.getLevel()), location.getLargeFacePositions());

					for (int i = 0; i < 8; i++) {
						genSpiritTree.trySpawnFace(level, new SmallSpiritTreeFace(EntityRegistry.SMALL_SPIRIT_TREE_FACE.get(), level.getLevel()), location.getSmallFacePositions());
					}

					location.setDirty(true);
					storage.getLocalStorageHandler().addLocalStorage(level, location);
				}
			}
		}
	}

	@Nullable
	private BlockPos findGroundPosition(WorldGenLevel level, BlockPos pos) {
		boolean hadAir = false;
		for (int yo = 6; yo >= -6; yo--) {
			BlockPos offsetPos = pos.above(yo);
			BlockState state = level.getBlockState(offsetPos);
			if (hadAir && state.is(BlockTags.DIRT)) {
				return offsetPos.above();
			}
			if (state.isAir()) {
				hadAir = true;
			}
		}
		return null;
	}

	private void generateWispCircle(WorldGenLevel level, RandomSource random, BlockPos center, int radius, int minHeight, int heightVar, @Nullable LocationSpiritTree location) {
		List<BlockPos> circle = BlockShapeUtils.getCircle(center, radius, new ArrayList<>());
		for (int i = 0; i < circle.size(); i += 2 + random.nextInt(2)) {
			if (i == circle.size() - 1) {
				break;
			}
			BlockPos pos = circle.get(i);
			pos = this.findGroundPosition(level, pos);
			if (pos != null && (level.isEmptyBlock(pos) || level.getBlockState(pos).canBeReplaced())) {
				BlockPos wall = pos.below();
				this.setBlock(level, wall, BlockRegistry.MOSSY_BETWEENSTONE_BRICKS.get().defaultBlockState(), location);
				int height = minHeight + random.nextInt(heightVar + 1);
				for (int yo = 0; yo < height; yo++) {
					wall = pos.above(yo);
					this.setBlock(level, wall, BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL.get().defaultBlockState(), location);
				}
				BlockPos wisp = pos.above(height);
				if (random.nextInt(4) == 0) {
					this.setBlock(level, wisp, BlockRegistry.WISP.get().defaultBlockState(), location);
					if (location != null) location.addGeneratedWispPosition(wisp);
				} else {
					if (location != null) location.addNotGeneratedWispPosition(wisp);
				}
			}
		}
	}

	private void generateRoot(WorldGenLevel level, RandomSource random, BlockPos pos, SpiritTree tree, @Nullable LocationSpiritTree location) {
		List<BlockPos> potentialBlocks = tree.generateBranchPositions(random, pos, random.nextInt(7), 32, 0.4D, 0.3D, (i, remainingBlocks) -> i < 2 ? 1 : (i > 4 ? -1 : 0), (i, length) -> true);
		int length = 0;
		for (int i = 0; i < potentialBlocks.size(); i++) {
			BlockPos block = potentialBlocks.get(i);
			if (i > 2 && !level.isEmptyBlock(block)) {
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
						filler = filler.offset(choices.remove(random.nextInt(choices.size())));
						this.setBlock(level, filler, BlockRegistry.SPIRIT_TREE_BARK.get().defaultBlockState(), location);
					}
				}
			}
			this.setBlock(level, block, BlockRegistry.SPIRIT_TREE_BARK.get().defaultBlockState(), location);
			prevPos = block;
		}
	}

	protected void setBlock(WorldGenLevel level, BlockPos pos, BlockState state, @Nullable LocationSpiritTree location) {
		level.setBlock(pos, state, 3);

		if (location != null) {
			if (!state.is(BlockRegistry.WISP)) {
				location.getGuard().setGuarded(level, pos, true);
			}

			if (state.is(BlockRegistry.SPIRIT_TREE_BARK)) {
				location.addSmallFacePosition(pos);
			}
		}
	}
}
