package thebetweenlands.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;
import thebetweenlands.common.entity.monster.SwampHag;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class VolatileSoul extends Projectile implements BLEntity {

	@Nullable
	private Entity target = null;
	private int strikes = 0;
	private int ticksInAir;

	protected final Deque<Vec3> trail = new LinkedList<>();

	public VolatileSoul(EntityType<? extends Projectile> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	public VolatileSoul(Level level, Entity shooter) {
		this(EntityRegistry.VOLATILE_SOUL.get(), level);
		this.setOwner(shooter);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	public void tick() {
		if (!this.level().isClientSide() && (this.level().getDifficulty() == Difficulty.PEACEFUL || this.getOwner() == null || !this.getOwner().isAlive())) {
			this.discard();
			return;
		}

		if (!this.level().isClientSide()) {
			if ((this.getOwner() == null || !this.getOwner().isAlive() || !(this.getOwner() instanceof Wight wight) || !(wight.isVolatile())) || this.target instanceof PrimordialMalevolence)
				this.discard();
		}

		if (this.isAlive()) {
			this.ticksInAir++;
			if (this.level().isClientSide()) {
				this.trail.push(this.position());
				while (this.trail.size() > 4) {
					this.trail.removeLast();
				}
			}
			if (this.target == null || !this.target.isAlive()) {
				List<LivingEntity> targetList = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(16.0D));
				List<Entity> eligibleTargets = new ArrayList<>();
				if (this.level().getRandom().nextInt(4) > 0) {
					for (Entity e : targetList) {
						if (e instanceof Player player) {
							eligibleTargets.add(player);
						}
					}
				}
				if (eligibleTargets.isEmpty()) {
					for (Entity e : targetList) {
						if (!(e instanceof Wight)) {
							eligibleTargets.add(e);
						}
					}
				}
				if (!eligibleTargets.isEmpty()) {
					this.target = eligibleTargets.get(this.level().getRandom().nextInt(eligibleTargets.size()));
				}
			}
			if (this.target != null && this.ticksInAir >= 10) {
				double dx = this.target.getBoundingBox().minX + (this.target.getBoundingBox().maxX - this.target.getBoundingBox().minX) / 2.0D - this.getX();
				double dy = this.target.getBoundingBox().minY + (this.target.getBoundingBox().maxY - this.target.getBoundingBox().minY) / 2.0D - this.getY();
				double dz = this.target.getBoundingBox().minZ + (this.target.getBoundingBox().maxZ - this.target.getBoundingBox().minZ) / 2.0D - this.getZ();
				double dist = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));
				double speed = 0.075D;
				double maxSpeed = 0.8D;
				this.setDeltaMovement(this.getDeltaMovement().add(dx / dist * speed, dy / dist * speed, dz / dist * speed));
				Vec3 motion = this.getDeltaMovement();
				if (motion.length() > maxSpeed) {
					motion = motion.normalize();
					this.setDeltaMovement(motion.multiply(maxSpeed, maxSpeed, maxSpeed));
				}
				hurtMarked = true;
			}
			HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
			if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
				this.hitTargetOrDeflectSelf(hitresult);
			}
			this.move(MoverType.SELF, this.getDeltaMovement());
		}

		super.tick();
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	//TODO tag?
	@Override
	protected boolean canHitEntity(Entity target) {
		return !(target instanceof Wight) && !(target instanceof SwampHag) && super.canHitEntity(target);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		if (!this.level().isClientSide()) {
			if (result.getEntity() instanceof Player player && player.isBlocking() && player.getUseItemRemainingTicks() <= 15) {
				this.setDeltaMovement(this.getDeltaMovement().multiply(-6.0F, -6.0F, -6.0F));
				this.strikes++;
				return;
			}
			result.getEntity().hurt(this.damageSources().indirectMagic(this, this.getOwner()), 3);
			this.discard();
			this.setDeltaMovement(Vec3.ZERO);
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.strikes++;
			if (this.strikes >= 3) {
				this.discard();
				return true;
			}
			this.markHurt();
			if (source.getEntity() != null) {
				if (!this.level().isClientSide()) {
					this.setDeltaMovement(source.getEntity().getLookAngle().multiply(1.5F, 1.5F, 1.5F));
				}
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("strikes", this.strikes);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.strikes = compound.getInt("strikes");
	}

	public Deque<Vec3> getTrail() {
		return this.trail;
	}
}
