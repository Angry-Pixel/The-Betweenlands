package thebetweenlands.common.world.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;
import thebetweenlands.common.block.entity.spawner.MobSpawnerBlockEntity;
import thebetweenlands.common.block.structure.DruidStoneBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.StructureTypeRegistry;

public class DruidCirclePiece extends ScatteredFeaturePiece {

	private static final BlockState[] RUNE_STONES = {
		BlockRegistry.DRUID_STONE_1.get().defaultBlockState(),
		BlockRegistry.DRUID_STONE_2.get().defaultBlockState(),
		BlockRegistry.DRUID_STONE_3.get().defaultBlockState(),
		BlockRegistry.DRUID_STONE_4.get().defaultBlockState(),
		BlockRegistry.DRUID_STONE_5.get().defaultBlockState()
	};

	public DruidCirclePiece(RandomSource random, int x, int z) {
		super(StructureTypeRegistry.DRUID_CIRCLE_PIECE.get(), x, 64, z, 12, 13, 13, getRandomHorizontalDirection(random));
	}

	public DruidCirclePiece(StructurePieceType piece, CompoundTag nbt) {
		super(piece, nbt);
	}

	public DruidCirclePiece(StructurePieceSerializationContext ctx, CompoundTag nbt) {
		this(StructureTypeRegistry.DRUID_CIRCLE_PIECE.get(), nbt);
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
		if (this.updateAverageGroundHeight(level, box, 0)) {
			// circle
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
			int baseRadius = 6;
			for (int x = -baseRadius; x <= baseRadius; x++) {
				for (int z = -baseRadius; z <= baseRadius; z++) {
					mutable.set(pos.getX() + x, pos.getY(), pos.getZ() + z);
					int dSq = (int) Math.round(Math.sqrt(x * x + z * z));
					if (dSq == baseRadius) {
						if (x % 2 == 0 && z % 2 == 0) {
							this.placePillar(level, mutable, random);
						}
					}
				}
			}
			level.setBlock(pos, BlockRegistry.DRUID_ALTAR.get().defaultBlockState(), 3);
			level.setBlock(pos.below(), BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
			BlockEntity te = level.getBlockEntity(pos.below());
			if (te instanceof MobSpawnerBlockEntity spawner) {
				BetweenlandsBaseSpawner logic = spawner.getSpawner();
				spawner.setEntityId(EntityRegistry.DARK_DRUID.get(), random);
				logic.setCheckRange(32.0D).setSpawnRange(6).setSpawnInAir(false).setMaxEntities(2 + random.nextInt(2));
			}
		}
	}

	private void placePillar(WorldGenLevel level, BlockPos.MutableBlockPos pos, RandomSource rand) {
		int height = rand.nextInt(3) + 3;
		for (int k = 0, y = pos.getY(); k <= height; k++, pos.setY(y + k)) {
			Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
			if (rand.nextBoolean()) {
				level.setBlock(pos.immutable(), this.getRandomRuneBlock(rand).setValue(DruidStoneBlock.FACING, facing), 3);
			} else {
				level.setBlock(pos.immutable(), BlockRegistry.DRUID_STONE_6.get().defaultBlockState().setValue(DruidStoneBlock.FACING, facing), 3);
				for (int vineCount = 0; vineCount < 4; vineCount++) {
					setRandomFoliage(level, pos, rand);
				}
			}
		}
	}

	private void setRandomFoliage(WorldGenLevel level, BlockPos.MutableBlockPos pos, RandomSource rand) {
		Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
		BlockPos side = pos.immutable().relative(facing);
		if (level.isEmptyBlock(side)) {
			level.setBlock(side, Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(facing.getOpposite()), true), 3);
		}
	}

	private BlockState getRandomRuneBlock(RandomSource rand) {
		return RUNE_STONES[rand.nextInt(RUNE_STONES.length)];
	}
}
