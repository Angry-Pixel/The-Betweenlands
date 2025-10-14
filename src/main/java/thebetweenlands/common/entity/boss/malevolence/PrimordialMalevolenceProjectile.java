package thebetweenlands.common.entity.boss.malevolence;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class PrimordialMalevolenceProjectile extends Projectile implements BLEntity {

	protected static final EntityDataAccessor<Boolean> DEFLECTION_STATE = SynchedEntityData.defineId(PrimordialMalevolenceProjectile.class, EntityDataSerializers.BOOLEAN);

	private int ticksInAir = 0;
	private boolean canDismount = false;

	public PrimordialMalevolenceProjectile(EntityType<? extends Projectile> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	public PrimordialMalevolenceProjectile(Level level, Entity source) {
		this(EntityRegistry.PRIMORDIAL_MALEVOLENCE_PROJECTILE.get(), level);
		this.setOwner(source);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DEFLECTION_STATE, false);
	}

	public void setDeflectable(boolean deflectable) {
		this.getEntityData().set(DEFLECTION_STATE, deflectable);
	}

	public boolean isDeflectable() {
		return this.getEntityData().get(DEFLECTION_STATE);
	}

	@Override
	protected boolean canHitEntity(Entity target) {
		return target.getType() != this.getType() && super.canHitEntity(target);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		if (result.getEntity() instanceof LivingEntity living) {
			if (living instanceof PrimordialMalevolence malevolence) {
				Vec3 ray = this.getDeltaMovement();
				ray = ray.normalize().scale(64.0D);
				int shieldHit = PrimordialMalevolence.rayTraceShield(malevolence.shield, new Vec3(malevolence.getX() + PrimordialMalevolence.SHIELD_OFFSET_X, malevolence.getY() + PrimordialMalevolence.SHIELD_OFFSET_Y, malevolence.getZ() + PrimordialMalevolence.SHIELD_OFFSET_Z), malevolence.getShieldRotationYaw(1), malevolence.getShieldRotationPitch(1), malevolence.getShieldRotationYaw(1), malevolence.getShieldExplosion(1), this.position(), ray, false);
				if (shieldHit >= 0) {
					if (!this.level().isClientSide()) {
						malevolence.shield.setActive(shieldHit, false);

						this.level().playSound(null, this.blockPosition(), SoundRegistry.FORTRESS_BOSS_SHIELD_DOWN.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

						double angle = Math.PI * 2.0D / 18;
						for (int i = 0; i < 18; i++) {
							Vec3 dir = new Vec3(Math.sin(angle * i), 0, Math.cos(angle * i));
							dir = dir.normalize();
							float speed = 0.8F;
							PrimordialMalevolenceProjectile bullet = new PrimordialMalevolenceProjectile(this.level(), this.getOwner());
							bullet.moveTo(malevolence.getX(), malevolence.getY(), malevolence.getZ(), 0, 0);
							bullet.shoot(dir.x, dir.y, dir.z, speed, 0.0F);
							this.level().addFreshEntity(bullet);
						}
					}
				} else {
					malevolence.hurt(this.damageSources().generic(), 10);
				}

				if (!this.level().isClientSide()) {
					malevolence.setFloating(false);
				}
			} else {
				living.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 2);
			}

			if (!this.level().isClientSide()) {
				this.discard();
			}
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		this.discard();
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.isInvulnerableTo(source)) {
			if (this.isDeflectable()) {
				this.markHurt();
				if (source.getEntity() instanceof Player player) {
					ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
					if (heldItem.is(ItemTags.SWORDS)) {
						if (!this.level().isClientSide() && source.getEntity().getPassengers().isEmpty()) {
							this.startRiding(source.getEntity(), true);
							this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(player));
							return false;
						}
					}
				}
			} else {
				if (!this.level().isClientSide()) {
					this.discard();
				}
			}
		}
		return false;
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide() && (this.level().getDifficulty() == Difficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isAlive()))) {
			this.discard();
			return;
		}

		if (this.isAlive()) {
			if (this.getVehicle() == null) {
				this.ticksInAir++;

				if (this.ticksInAir > 200) {
					this.discard();
				}

				HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
				if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult))
					this.hitTargetOrDeflectSelf(hitresult);
				this.move(MoverType.SELF, this.getDeltaMovement());
			} else {
				if (this.getVehicle() instanceof Player player) {
					ItemStack heldItem = player.getMainHandItem();
					if (!this.isDeflectable() || heldItem.isEmpty() || !heldItem.is(ItemTags.SWORDS)) {
						if (!this.level().isClientSide()) {
							this.discard();
						}
					} else {
						player.makeStuckInBlock(null, new Vec3(0.25F, 0.05F, 0.25F));
						player.setDeltaMovement(player.getDeltaMovement().subtract(0.0D, 1.5D, 0.0D));
						if (player.swinging) {
							if (this.canDismount) {
								Vec3 look = this.getVehicle().getViewVector(0.0F);
								look.normalize();
								this.shoot(look.x, look.y, look.z, 0.5F, 0.0F);
								this.stopRiding();
								this.setOwner(player);
							}
						} else {
							this.canDismount = true;
						}
					}
				}
			}
		}

		super.tick();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.ticksInAir = compound.getInt("ticks_in_air");
		this.canDismount = compound.getBoolean("can_dismount");
		this.setDeflectable(compound.getBoolean("deflectable"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("ticks_in_air", this.ticksInAir);
		compound.putBoolean("can_dismount", this.canDismount);
		compound.putBoolean("deflectable", this.isDeflectable());
	}
}