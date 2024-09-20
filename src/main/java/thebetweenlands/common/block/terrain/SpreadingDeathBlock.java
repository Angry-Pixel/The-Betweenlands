package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SpreadingDeathBlock extends Block {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public SpreadingDeathBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, true));
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		super.destroy(level, pos, state);

		if (level instanceof ServerLevel server) {
			this.checkAndRevertBiome(server, pos);
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock())) {
			int spreadTime = this.getScheduledSpreadTime(level, pos, state);
			if (spreadTime > 0) {
				level.scheduleTick(pos, this, spreadTime);
			}
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide()) {
			if (state.getValue(ACTIVE) && this.shouldSpread(level, pos, state)) {
				boolean spread = false;
				for (int i = 0; i < 16; ++i) {
					BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);

					if (level.isLoaded(target)) {
						BlockState offsetState = level.getBlockState(target);

						if (offsetState.getBlock() != this && this.canSpreadInto(level, pos, state, target, offsetState)) {
							this.spreadInto(level, pos, state, target, offsetState);
							if (this.getSpreadingBiome() != null) {
								this.convertBiome(level, target, this.getSpreadingBiome());
							}
							spread = true;
						}
					}
				}
				if (!spread) {
					for (int i = 0; i < 16; ++i) {
						BlockPos target = pos.offset(random.nextInt(5) - 2, random.nextInt(5) - 2, random.nextInt(5) - 2);

						if (level.isLoaded(target)) {
							BlockState offsetState = level.getBlockState(target);

							if (offsetState.getBlock() != this && this.canSpreadInto(level, pos, state, target, offsetState)) {
								this.spreadInto(level, pos, state, target, offsetState);
								if (this.getSpreadingBiome() != null) {
									this.convertBiome(level, target, this.getSpreadingBiome());
								}
								spread = true;
							}
						}
					}
				}

				int spreadTime = this.getScheduledSpreadTime(level, pos, state);
				if (spreadTime > 0) {
					level.scheduleTick(pos, this, spreadTime);
				}
			}

			if (level.getRandom().nextInt(6) == 0) {
				level.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
			}

			if (this.getSpreadingBiome() != null && random.nextInt(3) == 0 && !level.getBiome(pos).is(this.getSpreadingBiome())) {
				this.convertBiome(level, pos, this.getSpreadingBiome());
			}
		}
	}

	protected boolean shouldSpread(Level level, BlockPos pos, BlockState state) {
		return true;
	}

	public boolean canSpreadInto(Level level, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		BlockState offsetStateUp = level.getBlockState(offsetPos.above());
		return offsetStateUp.getBlock() != this && !offsetStateUp.isSolid() && (this.getPreviousBiome() == null || level.getBiome(offsetPos).is(this.getPreviousBiome()));
	}

	public abstract void spreadInto(Level level, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState);

	protected int getScheduledSpreadTime(Level level, BlockPos pos, BlockState state) {
		return -1;
	}

	@Nullable
	public ResourceKey<Biome> getSpreadingBiome() {
		return null;
	}

	@Nullable
	public ResourceKey<Biome> getPreviousBiome() {
		return null;
	}

	protected void checkAndRevertBiome(ServerLevel level, BlockPos pos) {
		if (this.getPreviousBiome() != null && this.getSpreadingBiome() != null && level.getBiome(pos).is(this.getSpreadingBiome())) {
			this.convertBiome(level, pos, this.getPreviousBiome());
		}
	}

	protected void convertBiome(ServerLevel level, BlockPos pos, ResourceKey<Biome> biomeKey) {
		Holder<Biome> biome = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(biomeKey);
		ChunkAccess chunk = level.getChunk(pos);
		int minY = QuartPos.fromBlock(level.getMinBuildHeight());
		int maxY = minY + QuartPos.fromBlock(level.getHeight()) - 1;

		int x = QuartPos.fromBlock(pos.getX());
		int z = QuartPos.fromBlock(pos.getZ());
		for (LevelChunkSection section : chunk.getSections()) {
			for (int sy = 0; sy < 16; sy += 4) {
				int y = Mth.clamp(QuartPos.fromBlock(chunk.getMinSection() + sy), minY, maxY);
				if (section.getBiomes().get(x & 3, y & 3, z & 3).is(biomeKey))
					continue;
				if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container)
					container.set(x & 3, y & 3, z & 3, biome);
			}
		}

		if (!chunk.isUnsaved()) chunk.setUnsaved(true);
		level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(chunk));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}
}
