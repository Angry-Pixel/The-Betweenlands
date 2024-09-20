package thebetweenlands.common.entities;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class SapSpit extends ThrowableItemProjectile {

	protected float damage;

	public SapSpit(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
	}

	public SapSpit(Level level, LivingEntity thrower, float damage) {
		super(EntityRegistry.SAP_SPIT.get(), thrower, level);
		this.damage = damage;
	}

	@Override
	public void tick() {
		super.tick();

		if(this.level().isClientSide()) {
			this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if(id == 3) {
			for(int i = 0; i < 16; ++i) {
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		this.level().broadcastEntityEvent(this, (byte)3);
		this.discard();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), this.damage);
	}

	@Override
	protected boolean canHitEntity(Entity target) {
		return super.canHitEntity(target); // && !(target instanceof SpiritTreeFace) && !(target instanceof RootGrabber);
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

	@Override
	protected Item getDefaultItem() {
		return ItemRegistry.SAP_SPIT.get();
	}
}
