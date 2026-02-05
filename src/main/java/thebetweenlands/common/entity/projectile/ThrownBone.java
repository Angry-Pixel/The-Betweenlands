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
import thebetweenlands.common.entity.monster.BonePuppetBase;
import thebetweenlands.common.entity.monster.BoneShaman;
import thebetweenlands.common.registries.EntityRegistry;

public class ThrownBone extends ThrowableProjectile {
	protected float damage;
	public int animationTicks, prevAnimationTicks;

	public ThrownBone(EntityType<ThrownBone> type, Level level) {
		super(type, level);
	}

	public ThrownBone(Level level, LivingEntity owner, float damage) {
		super(EntityRegistry.THROWN_BONE.get(), owner, level);
		this.damage = damage;
	}

	@Override
	protected void defineSynchedData(Builder builder) {}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide())
			if (level().getDifficulty() == Difficulty.PEACEFUL)
				discard();

		if (level().isClientSide()) {
			prevAnimationTicks = animationTicks;
			if (animationTicks < 360)
				animationTicks += 60;
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
		if (result.getEntity() instanceof LivingEntity &&  (!(result.getEntity() instanceof BonePuppetBase) || !(result.getEntity() instanceof BoneShaman)))
			(result.getEntity()).hurt(this.damageSources().thrown(this, getOwner()), this.damage);
	}

	public void breakBone() {
		if (!level().isClientSide()) {
			discard();
			//level().levelEvent(null, 2001, getOnPos(), Block.getId(BlockRegistry.SLIMY_BONE_ORE.get().defaultBlockState()));
		}
	}

	@Override
	protected double getDefaultGravity() {
		return 0.01F;
	}

	@Override
	protected boolean canHitEntity(Entity target) {
		return super.canHitEntity(target) && (!(target instanceof BonePuppetBase)  || !(target instanceof BoneShaman));
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