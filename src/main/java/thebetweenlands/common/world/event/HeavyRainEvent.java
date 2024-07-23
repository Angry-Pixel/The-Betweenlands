package thebetweenlands.common.world.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.TheBetweenlands;
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
//						if (!level.getBlockState(pos.below()).is(BlockRegistry.PUDDLE.get()) && BlockRegistry.PUDDLE.get().canPlaceBlockAt(level, pos)) {
//							levelchunk.setBlockState(pos, BlockRegistry.PUDDLE.get().getDefaultState());
//						}
					}
				}
			}
		}

		if (level.isClientSide()) {
			this.updateWeather(level);
		}
	}

	private int rainSoundCounter = 0;

	private void updateWeather(Level level) {
//		BLRainRenderer.INSTANCE.update(level);

		Minecraft mc = Minecraft.getInstance();

		float particleStrength = level.getRainLevel(1.0F);

		if (!Minecraft.useFancyGraphics()) {
			particleStrength /= 2.0F;
		}

		if (particleStrength > 0.0F) {
			Entity entity = mc.getCameraEntity();

			if (entity != null) {
				BlockPos center = entity.blockPosition();
				int numParticles = (int) (50.0F * particleStrength * particleStrength);

				if (mc.options.particles().get() == ParticleStatus.DECREASED) {
					numParticles >>= 1;
				} else if (mc.options.particles().get() == ParticleStatus.MINIMAL) {
					numParticles = 0;
				}

				double soundX = 0.0D;
				double soundY = 0.0D;
				double soundZ = 0.0D;

				int spawnedParticles = 0;

				for (int l = 0; l < numParticles; ++l) {
					BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, center.offset(level.getRandom().nextInt(10) - level.getRandom().nextInt(10), 0, level.getRandom().nextInt(10) - level.getRandom().nextInt(10)));

					BlockPos below = pos.below();
					BlockState stateBelow = level.getBlockState(below);
					FluidState fluidstate = level.getFluidState(below);
					Holder<Biome> biome = level.getBiome(pos);

					if (pos.getY() <= center.getY() + 10 && pos.getY() >= center.getY() - 10 && biome.value().hasPrecipitation() && biome.value().warmEnoughToRain(pos)) {
						AABB blockAABB = stateBelow.getCollisionShape(level, below).bounds();

						float rangeX = (float) (blockAABB.maxX - blockAABB.minX);
						float rangeZ = (float) (blockAABB.maxZ - blockAABB.minZ);

						float size = (0.025f + level.getRandom().nextFloat() * 0.4f);

						if (size * 2 < rangeX && size * 2 < rangeZ) {
							double rx = level.getRandom().nextDouble() * (rangeX - size * 2) + size + blockAABB.minX;
							double rz = level.getRandom().nextDouble() * (rangeZ - size * 2) + size + blockAABB.minZ;

							if (!fluidstate.is(FluidTags.LAVA) && !stateBelow.is(Blocks.MAGMA_BLOCK) && !stateBelow.isAir()) {
								int waterColor = BiomeColors.getAverageWaterColor(level, pos);

								float r = (waterColor >> 16 & 255) / 255.0f;
								float g = (waterColor >> 8 & 255) / 255.0f;
								float b = (waterColor & 255) / 255.0f;

								//TODO
								for (int i = 0; i < 4 + level.getRandom().nextInt(6); i++) {
//									level.addParticle(ParticleRegistry.RAIN.get(), (double) below.getX() + rx, (double) ((float) below.getY() + 0.1F) + blockAABB.maxY, (double) below.getZ() + rz, r, g, b + 0.075f);
								}

//								BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR,
//									BLParticles.WATER_RIPPLE.create(level, (double) below.getX() + rx, (double) ((float) below.getY() + 0.1F) + blockAABB.maxY, (double) below.getZ() + rz,
//										ParticleArgs.get()
//											.withScale(size * 10)
//											.withColor(r * 1.15f, g * 1.15f, b * 1.15f, 1)
//									));

								spawnedParticles++;

								if (level.getRandom().nextInt(spawnedParticles) == 0) {
									soundX = (double) pos.getX() + rx;
									soundY = (double) ((float) pos.getY() + 0.1F) + blockAABB.maxY - 1.0D;
									soundZ = (double) pos.getZ() + rz;
								}
							}
						}
					}
				}

				if (spawnedParticles > 0 && level.getRandom().nextInt(3) < this.rainSoundCounter++) {
					this.rainSoundCounter = 0;

					if (soundY > center.getY() + 1 && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, center).getY() > Mth.floor((float)center.getY())) {
						level.playLocalSound(soundX, soundY, soundZ, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
					} else {
						level.playLocalSound(soundX, soundY, soundZ, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
					}
				}
			}
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
