package thebetweenlands.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.entity.monster.BonePuppetMelee;
import thebetweenlands.common.entity.monster.BonePuppetRanged;
import thebetweenlands.common.entity.monster.BoneShaman;
import thebetweenlands.common.registries.EntityRegistry;

public class BoneShamanProjectile extends ThrowableProjectile {
	protected float damage;
	public int animationTicks, prevAnimationTicks;
	private int ticksInAir = 0;

	public BoneShamanProjectile(EntityType<BoneShamanProjectile> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	public BoneShamanProjectile(Level level, LivingEntity owner, float damage) {
		super(EntityRegistry.BONE_SHAMAN_PROJECTILE.get(), owner, level);
		this.damage = damage;
	}

	@Override
	protected void defineSynchedData(Builder builder) {}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide()) {
			if (level().getDifficulty() == Difficulty.PEACEFUL || (getOwner() != null && !getOwner().isAlive()))
				discard();
			if (isAlive()) {
				ticksInAir++;
				if (ticksInAir > 200)
					discard();
			}
		}

		if (level().isClientSide()) {
			prevAnimationTicks = animationTicks;
			if (animationTicks < 360)
				animationTicks += 15;
			if (animationTicks >= 360) {
				animationTicks -= 360;
				prevAnimationTicks -= 360;
			}
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		breakBone();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (result.getEntity() instanceof LivingEntity && (!(result.getEntity() instanceof BonePuppetMelee) && !(result.getEntity() instanceof BoneShaman) && !(result.getEntity() instanceof BonePuppetRanged)))
			(result.getEntity()).hurt(this.damageSources().thrown(this, getOwner()), this.damage);
	}

	public void breakBone() {
		if (!level().isClientSide()) {
			discard();
		}
	}

	@Override
	protected double getDefaultGravity() {
		return 0.0002F;
	}

	@Override
	protected boolean canHitEntity(Entity target) {
		return super.canHitEntity(target) && (!(target instanceof BonePuppetMelee) && !(target instanceof BoneShaman) && !(target instanceof BonePuppetRanged));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("damage", this.damage);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.damage = compound.getFloat("damage");
	}
}