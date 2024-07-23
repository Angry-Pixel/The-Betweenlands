package thebetweenlands.common.world.event;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.simulacrum.SimulacrumBlockEntity;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.registries.SimulacrumEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import javax.annotation.Nullable;
import java.util.List;

public class ThunderstormEvent extends TimedEnvironmentEvent {
	protected int updateLCG = RandomSource.create().nextInt();

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[]{TheBetweenlands.prefix("textures/events/thunderstorm.png")};

	public ThunderstormEvent() {
		this.getActiveStateEstimator().dependsOnEvent(EnvironmentEventRegistry.HEAVY_RAIN::get);
	}

	@Override
	protected boolean canActivate(Level level) {
		return BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.HEAVY_RAIN);
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (!level.isClientSide()) {
			if (this.isActive() && !this.canActivate(level)) {
				this.setActive(level, false);
			}

			if (this.isActive() && level instanceof ServerLevel server) {
				for (ChunkHolder chunkholder : server.getChunkSource().chunkMap.getChunks()) {
					LevelChunk levelchunk = chunkholder.getTickingChunk();
					if (levelchunk != null && level.dimensionType().hasSkyLight() && !level.dimensionType().hasCeiling() && level.getRandom().nextInt(2500) == 0) {
						this.updateLCG = this.updateLCG * 3 + 1013904223;
						int l = this.updateLCG >> 2;

						BlockPos seedPos = new BlockPos(levelchunk.getPos().x * 16 + (l & 15), 0, levelchunk.getPos().z * 16 + (l >> 8 & 15));

						SimulacrumBlockEntity simulacrum = SimulacrumBlockEntity.getClosestActiveTile(SimulacrumBlockEntity.class, null, server, seedPos.getX() + 0.5D, level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, seedPos).getY(), seedPos.getZ() + 0.5D, 64.0D, SimulacrumEffectRegistry.ATTRACTION.get(), null);

						BlockPos pos;
						boolean isFlyingPlayerTarget = false;

						if (simulacrum != null) {
							pos = simulacrum.getBlockPos().above();
						} else {
							pos = this.getNearbyFlyingPlayer(server, seedPos);
							if (pos == null) {
								pos = this.adjustPosToNearbyEntity(server, seedPos);
							} else {
								isFlyingPlayerTarget = true;
							}
						}

						if ((pos.getY() > 150 || level.getRandom().nextInt(8) == 0) && level.isRainingAt(pos)) {
//							level.addFreshEntity(new EntityBLLightningBolt(level, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, isFlyingPlayerTarget ? 50 : 400, isFlyingPlayerTarget, false));
						}
					}
				}
			}
		}
	}

	@Nullable
	protected BlockPos getNearbyFlyingPlayer(ServerLevel world, BlockPos blockpos) {
		Player closestPlayer = null;
		double closestDistSq = Double.MAX_VALUE;
		for (Player player : world.players()) {
			if (player.getY() > 130 && (!player.onGround() || player.isPassenger()) && (player.getY() - world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, player.blockPosition()).getY()) > 8) {
				double dstSq = (blockpos.getX() - player.getX()) * (blockpos.getX() - player.getX()) + (blockpos.getZ() - player.getZ()) * (blockpos.getZ() - player.getZ());
				if (dstSq < closestDistSq) {
					closestPlayer = player;
					closestDistSq = dstSq;
				}
			}
		}

		if (closestPlayer != null && closestDistSq < 50 * 50) {
			double motionX;
			double motionY;
			double motionZ;
			if (closestPlayer.getVehicle() != null) {
				motionX = closestPlayer.getVehicle().getDeltaMovement().x();
				motionY = closestPlayer.getVehicle().getDeltaMovement().y();
				motionZ = closestPlayer.getVehicle().getDeltaMovement().z();
			} else {
				motionX = closestPlayer.getDeltaMovement().x();
				motionY = closestPlayer.getDeltaMovement().y();
				motionZ = closestPlayer.getDeltaMovement().z();
			}

			return closestPlayer.blockPosition().offset((int) (motionX * 60 + world.getRandom().nextInt(5) - 2), (int) (motionY * 60 + world.getRandom().nextInt(5) - 2), (int) (motionZ * 60 + world.getRandom().nextInt(5) - 2));
		}

		return null;
	}

	protected BlockPos adjustPosToNearbyEntity(ServerLevel world, BlockPos pos) {
		BlockPos blockpos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
		AABB aabb = (new AABB(blockpos.getCenter(), new BlockPos(blockpos.getX(), world.getHeight(), blockpos.getZ()).getCenter())).inflate(3.0D);
		List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, aabb, entity -> entity != null && entity.isAlive() && world.canSeeSky(entity.blockPosition()));

		if (!list.isEmpty()) {
			return list.get(world.getRandom().nextInt(list.size())).blockPosition();
		} else {
			if (blockpos.getY() == -1) {
				blockpos = blockpos.above(2);
			}

			return blockpos;
		}
	}

	@Override
	public int getOffTime(RandomSource rnd) {
		return 5000 + rnd.nextInt(4000);
	}

	@Override
	public int getOnTime(RandomSource rnd) {
		return 4000 + rnd.nextInt(4000);
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_THUNDERSTORM.get();
	}
}
