package thebetweenlands.common.entity.boss.malevolence;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.audio.TeleporterSoundInstance;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

import java.util.List;

public class PrimordialMalevolenceTeleporter extends Entity implements BLEntity, ScreenShaker {
	protected static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(PrimordialMalevolenceTeleporter.class, EntityDataSerializers.INT);

	private Vec3 teleportDestination = Vec3.ZERO;
	private BlockPos bossSpawnPosition = BlockPos.ZERO;

	private Player target = null;

	private int teleportTicks = 0;
	private final int maxTeleportTicks = 75;

	public boolean isLookingAtPlayer = false;

	private boolean spawnedBoss = false;

	public PrimordialMalevolenceTeleporter(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(TARGET_ID, -1);
	}

	@Override
	public void push(Entity entity) {

	}

	@Override
	public void tick() {
		super.tick();

		double radius = 6.0D;
		double lookRadius = 8.0D;

		if (!this.level().isClientSide()) {
			if (this.level().getDifficulty() != Difficulty.PEACEFUL) {
				if (this.target == null) {
					AABB checkAABB = new AABB(this.blockPosition()).inflate(radius);
					List<Player> players = this.level().getEntitiesOfClass(Player.class, checkAABB);
					Player closestPlayer = null;
					for (Player player : players) {
						if ((closestPlayer == null || player.distanceTo(this) < closestPlayer.distanceTo(this)) && player.distanceTo(this) < radius && player.hasLineOfSight(this)) {
							Vec3 playerLook = player.getViewVector(1.0F).normalize();
							Vec3 vecDiff = new Vec3(this.getX() - player.getX(), this.getBoundingBox().minY + (double) (this.getBbHeight() / 2.0F) - (player.getEyeY()), this.getZ() - player.getZ());
							double dist = vecDiff.length();
							vecDiff = vecDiff.normalize();
							double angle = playerLook.dot(vecDiff);
							if (angle > 1.0D - 0.01D / dist)
								closestPlayer = player;
						}
					}
					if (closestPlayer != null)
						this.target = closestPlayer;
				} else {
					if (this.target.distanceTo(this) > radius) {
						this.target = null;
					} else {
						Vec3 playerLook = this.target.getViewVector(1.0F).normalize();
						Vec3 vecDiff = new Vec3(this.getX() - this.target.getX(), this.getBoundingBox().minY + (double) (this.getBbHeight() / 2.0F) - (this.target.getEyeY()), this.getZ() - this.target.getZ());
						double dist = vecDiff.length();
						vecDiff = vecDiff.normalize();
						double angle = playerLook.dot(vecDiff);
						if (angle <= 1.0D - (0.01D + Math.pow(this.getTeleportProgress(), 3) / 10.0D) / dist)
							this.target = null;
					}
				}
			} else {
				this.target = null;
			}

			if (this.target == null) {
				this.getEntityData().set(TARGET_ID, -1);
			} else {
				this.getEntityData().set(TARGET_ID, this.target.getId());
			}
		} else {
			Entity prevTarget = this.target;
			Entity target = this.level().getEntity(this.getEntityData().get(TARGET_ID));
			if (target instanceof Player player) {
				if (this.target == null) {
					for (int i = 0; i < 60; i++) {
						this.spawnSmokeParticle(this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), (this.level().getRandom().nextFloat() - 0.5F) / 2.5F, (this.level().getRandom().nextFloat() - 0.5F) / 2.5F, (this.level().getRandom().nextFloat() - 0.5F) / 2.5F);
					}
				}
				this.target = player;
			} else {
				this.target = null;
			}
			if (this.target != null && prevTarget != this.target && this.level().isClientSide()) {
				this.playTeleportSound();
			}
		}

		if (this.target != null) {
			this.lookAt(this.target, 360.0F, 360.0F);

			this.teleportTicks++;

			if (!this.level().isClientSide() && this.teleportTicks > this.maxTeleportTicks) {
				//Teleport
				if (this.target instanceof ServerPlayer player) {
					player.stopRiding();
					player.connection.teleport(this.teleportDestination.x, this.teleportDestination.y, this.teleportDestination.z, player.getYRot(), player.getXRot());
				} else {
					this.target.stopRiding();
					this.target.moveTo(this.teleportDestination.x, this.teleportDestination.y, this.teleportDestination.z, this.target.getYRot(), this.target.getXRot());
				}
				if (!this.spawnedBoss) {
					this.spawnBoss();
					this.spawnedBoss = true;
				}
				this.target = null;
				this.teleportTicks = 0;
			} else if (this.level().isClientSide() && this.level().getRandom().nextInt(2) == 0) {
				double rx = this.level().getRandom().nextFloat();
				double ry = this.level().getRandom().nextFloat();
				double rz = this.level().getRandom().nextFloat();
				double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
				this.spawnSmokeParticle((float) this.getX() - this.getBbWidth() / 2.0F + rx, (float) this.getY() + ry, (float) this.getZ() - this.getBbWidth() / 2.0F + rz,
					(rx - 0.5D) / len * 0.2D, (ry - 0.5D) / len * 0.2D, (rz - 0.5D) / len * 0.2D);
			}
		} else {
			this.teleportTicks = 0;
			Player closestPlayer = this.level().getNearestPlayer(this, lookRadius);
			if (closestPlayer != null) {
				this.lookAt(closestPlayer, 360.0F, 360.0F);
				if (this.level().isClientSide()) {
					if (!this.isLookingAtPlayer) {
						for (int i = 0; i < 10; i++) {
							this.spawnSmokeParticle(this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), (this.level().getRandom().nextFloat() - 0.5F) / 2.5F, (this.level().getRandom().nextFloat() - 0.5F) / 2.5F, (this.level().getRandom().nextFloat() - 0.5F) / 2.5F);
						}
					}
				}
				this.isLookingAtPlayer = true;
			} else {
				this.isLookingAtPlayer = false;
			}
		}
	}

	private void playTeleportSound() {
		BetweenlandsClient.playLocalSound(new TeleporterSoundInstance(this, this.getTarget()));
	}

	public void lookAt(Entity target, float maxYRotIncrease, float maxXRotIncrease) {
		double d0 = target.getX() - this.getX();
		double d2 = target.getZ() - this.getZ();
		double d1;
		if (target instanceof LivingEntity livingentity) {
			d1 = livingentity.getEyeY() - this.getEyeY();
		} else {
			d1 = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0 - this.getEyeY();
		}

		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		float f = (float)(Mth.atan2(d2, d0) * 180.0F / Mth.PI) - 90.0F;
		float f1 = (float)(-(Mth.atan2(d1, d3) * 180.0F / Mth.PI));
		this.setXRot(this.rotlerp(this.getXRot(), f1, maxXRotIncrease));
		this.setYRot(this.rotlerp(this.getYRot(), f, maxYRotIncrease));
	}

	private float rotlerp(float angle, float targetAngle, float maxIncrease) {
		float f = Mth.wrapDegrees(targetAngle - angle);
		if (f > maxIncrease) {
			f = maxIncrease;
		}

		if (f < -maxIncrease) {
			f = -maxIncrease;
		}

		return angle + f;
	}

	public float getTeleportProgress() {
		return this.teleportTicks / (float) this.maxTeleportTicks;
	}

	public Player getTarget() {
		return this.target;
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		double dx = compound.getDouble("destinationX");
		double dy = compound.getDouble("destinationY");
		double dz = compound.getDouble("destinationZ");
		this.teleportDestination = new Vec3(dx, dy, dz);
		double sx = compound.getDouble("bossSpawnX");
		double sy = compound.getDouble("bossSpawnY");
		double sz = compound.getDouble("bossSpawnZ");
		this.bossSpawnPosition = BlockPos.containing(sx, sy, sz);
		this.spawnedBoss = compound.getBoolean("spawnedBoss");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		if (this.teleportDestination != null) {
			compound.putDouble("destinationX", this.teleportDestination.x);
			compound.putDouble("destinationY", this.teleportDestination.y);
			compound.putDouble("destinationZ", this.teleportDestination.z);
		}
		if (this.bossSpawnPosition != null) {
			compound.putDouble("bossSpawnX", this.bossSpawnPosition.getX());
			compound.putDouble("bossSpawnY", this.bossSpawnPosition.getY());
			compound.putDouble("bossSpawnZ", this.bossSpawnPosition.getZ());
		}
		compound.putBoolean("spawnedBoss", this.spawnedBoss);
	}

	public void setTeleportDestination(Vec3 destination) {
		this.teleportDestination = destination;
	}

	public Vec3 getTeleportDestination() {
		return this.teleportDestination;
	}

	public void setBossSpawnPosition(BlockPos position) {
		this.bossSpawnPosition = position;
	}

	public BlockPos getBossSpawnPosition() {
		return this.bossSpawnPosition;
	}

	protected void spawnBoss() {
		PrimordialMalevolence boss = new PrimordialMalevolence(EntityRegistry.PRIMORDIAL_MALEVOLENCE.get(), this.level());
		boss.setPos(this.bossSpawnPosition.getX() + 0.5D, this.bossSpawnPosition.getY() + 0.5D, this.bossSpawnPosition.getZ() + 0.5D);
		boss.setAnchor(this.bossSpawnPosition, 6.0D);
		this.level().addFreshEntity(boss);
	}

	@Override
	public float getShakeIntensity(Entity viewer) {
		if (this.getTarget() == viewer)
			return (float) Math.pow(this.getTeleportProgress(), 3) / 2.0F;
		return 0.0F;
	}

	protected void spawnSmokeParticle(double x, double y, double z, double mx, double my, double mz) {
		this.level().addParticle(ParticleRegistry.PORTAL_EFFECT.get(), x, y, z, mx, my, mz);
	}
}

