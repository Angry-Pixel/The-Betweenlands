package thebetweenlands.common.world.event;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.registries.SoundRegistry;

public class SnowfallEvent extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = TheBetweenlands.prefix("snowfall");

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { TheBetweenlands.prefix("textures/events/snowfall.png") };

	private float snowingStrength = 0.0F;
	protected static final EntityDataAccessor<Float> TARGET_SNOWING_STRENGTH = GenericDataAccessor.defineId(SnowfallEvent.class, EntityDataSerializers.FLOAT);

	public SnowfallEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(TARGET_SNOWING_STRENGTH, 0.0F);
	}

	public static float getSnowingStrength(Level level) {
		if (level != null) {
//			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(level);
//			if (provider != null) {
//				return provider.getEnvironmentEventRegistry().snowfall.getSnowingStrength();
//			}
		}
		return 0;
	}

	public float getSnowingStrength() {
		return this.snowingStrength;
	}

	public boolean isSnowing() {
		return this.snowingStrength > 0;
	}

	@Override
	protected boolean canActivate() {
		return this.getRegistry().winter.isActive();
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public void setActive(boolean active) {
		if(!this.getLevel().isClientSide()) {
			if (active) {
				this.dataManager.set(TARGET_SNOWING_STRENGTH, 0.5F + this.getLevel().getRandom().nextFloat() * 7.5F);
			} else {
				this.dataManager.set(TARGET_SNOWING_STRENGTH, 0.0F);
			}
		}
		super.setActive(active);
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (!level.isClientSide()) {
			if(this.isActive() && !this.getRegistry().winter.isActive()) {
				this.setActive(false);
			}

			if (this.isActive() && level instanceof ServerLevel server && level.getRandom().nextInt(5) == 0) {
				for (ChunkHolder chunkholder : server.getChunkSource().chunkMap.getChunks()) {
					LevelChunk levelchunk = chunkholder.getTickingChunk();
					if (levelchunk != null) {
						int cbx = level.getRandom().nextInt(16);
						int cbz = level.getRandom().nextInt(16);
						BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, levelchunk.getPos().getWorldPosition().offset(cbx, 0, cbz)).below();
						if (level.getRandom().nextInt(Math.max(20 - (int) (this.getSnowingStrength() / 8.0F * 18.0F), 2)) == 0) {
							BlockState stateAbove = level.getBlockState(pos.above());
//							if (stateAbove.isAir() && BlockRegistry.SNOW.canPlaceBlockAt(level, pos.above())) {
//								levelchunk.setBlockState(pos.above(), BlockRegistry.SNOW.getDefaultState());
//							} else if (stateAbove.getBlock() instanceof BlockSnowBetweenlands) {
//								int layers = stateAbove.getValue(BlockSnowBetweenlands.LAYERS);
//								if (layers < 5) {
//									boolean hasEnoughSnowAround = true;
//									BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
//									for (Direction dir : Direction.Plane.HORIZONTAL) {
//										checkPos.set(pos.getX() + dir.getStepX(), pos.getY() + 1, pos.getZ() + dir.getStepZ());
//										if (level.isLoaded(checkPos)) {
//											BlockState neighourState = level.getBlockState(checkPos);
//											if (BlockRegistry.SNOW.canPlaceBlockAt(level, checkPos)
//												&& (!neighourState.is(BlockRegistry.SNOW) || neighourState.getValue(BlockSnowBetweenlands.LAYERS) < layers)) {
//												hasEnoughSnowAround = false;
//											}
//										} else {
//											hasEnoughSnowAround = false;
//											break;
//										}
//									}
//									if (hasEnoughSnowAround) {
//										level.setBlockAndUpdate(pos.above(), stateAbove.setValue(BlockSnowBetweenlands.LAYERS, layers + 1));
//									}
//								}
//							}
						}
					}
				}
			}
		} else {
			this.updateSnowRenderer(level);
		}

		if (!this.isActive()) {
			this.dataManager.set(TARGET_SNOWING_STRENGTH, 0.0F);
		}

		float targetSnowingStrength = this.dataManager.get(TARGET_SNOWING_STRENGTH);

		if (this.snowingStrength < targetSnowingStrength) {
			this.snowingStrength = this.snowingStrength + 0.01F;
			if (this.snowingStrength > targetSnowingStrength) {
				this.snowingStrength = targetSnowingStrength;
			}
		} else if (this.snowingStrength > targetSnowingStrength) {
			this.snowingStrength = this.snowingStrength - 0.01F;
			if (this.snowingStrength < targetSnowingStrength) {
				this.snowingStrength = targetSnowingStrength;
			}
		}
	}

	protected void updateSnowRenderer(Level level) {
		//BLSnowRenderer.INSTANCE.update(level);
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().putFloat("targetSnowingStrength", this.dataManager.get(TARGET_SNOWING_STRENGTH));
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.dataManager.set(TARGET_SNOWING_STRENGTH, this.getData().getFloat("targetSnowingStrength"));
	}

	@Override
	public int getOffTime(RandomSource rnd) {
		return 18000 + rnd.nextInt(18000);
	}

	@Override
	public int getOnTime(RandomSource rnd) {
		return 4800 + rnd.nextInt(6000);
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_SNOWFALL.get();
	}
}
