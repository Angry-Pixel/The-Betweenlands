package thebetweenlands.common.world.event;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.client.sky.BLWeatherRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class HeavyRainEvent extends TimedEnvironmentEvent {

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[]{TheBetweenlands.prefix("textures/events/heavy_rain.png")};

	@Override
	public int getOffTime(RandomSource rnd) {
		return rnd.nextInt(50000) + 60000;
	}

	@Override
	public int getOnTime(RandomSource rnd) {
		return rnd.nextInt(5000) + 8000;
	}

	@Override
	public void setActive(Level level, boolean active) {
		if (!active || !BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.WINTER)) {
			super.setActive(level, active);
		}
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (!level.isClientSide() && BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.WINTER)) {
			this.setActive(level, false);
		}

		if (this.isActive() && level.getRandom().nextInt(20) == 0) {
			if (level instanceof ServerLevel server) {
				for (ChunkHolder chunkholder : server.getChunkSource().chunkMap.getChunks()) {
					LevelChunk levelchunk = chunkholder.getTickingChunk();
					if (levelchunk != null && level.getRandom().nextInt(4) == 0) {
						int cbx = level.getRandom().nextInt(16);
						int cbz = level.getRandom().nextInt(16);
						BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, levelchunk.getPos().getWorldPosition().offset(cbx, 0, cbz));
						if (!level.getBlockState(pos.below()).is(BlockRegistry.PUDDLE.get()) && BlockRegistry.PUDDLE.get().defaultBlockState().canSurvive(level, pos)) {
							level.setBlock(pos, BlockRegistry.PUDDLE.get().defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
						}
					}
				}
			}
		}

		if (level.isClientSide()) {
			BLWeatherRenderer.INSTANCE.tickRain(level);
		}
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_HEAVY_RAIN.get();
	}
}
