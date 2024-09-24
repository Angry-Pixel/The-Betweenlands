package thebetweenlands.common.entity.projectile;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.armor.RubberBootsItem;
import thebetweenlands.common.registries.EntityRegistry;

import javax.annotation.Nullable;
import java.util.*;

public class ElectricShock extends Entity {
	@Nullable
	private final Entity source;
	private final float damage;

	private final Set<LivingEntity> targets = new HashSet<>();

	private int maxJumps, jumps;

	public ElectricShock(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.source = null;
		this.damage = 0.0F;
	}

	public ElectricShock(Level level, Entity source, LivingEntity hit, float damage, boolean isWet) {
		super(EntityRegistry.ELECTRIC_SHOCK.get(), level);

		this.moveTo(source.getX(), source.getY(), source.getZ(), 0, 0);

		this.source = source;
		if (hit != null) {
			this.targets.add(hit);
		}
		this.damage = damage;

		this.maxJumps = 2 + this.level().getRandom().nextInt(3);

		if (isWet) {
			this.maxJumps = this.maxJumps * 2;
		}
	}


	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	public void move(MoverType type, Vec3 pos) {
		//no-op
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide()) {
			if (this.source == null) {
				this.discard();
			} else {
				Entity shootingEntity;
				DamageSource damageSource;

				if (this.source instanceof AbstractArrow arrow) {
					shootingEntity = arrow.getOwner();

					damageSource = this.damageSources().arrow(arrow, Objects.requireNonNullElse(shootingEntity, arrow));
				} else if (this.source instanceof LivingEntity living) {
					shootingEntity = null;
					damageSource = this.damageSources().mobAttack(living);
				} else {
					shootingEntity = null;
					damageSource = this.damageSources().generic();
				}

				List<Pair<Entity, Entity>> chain = new ArrayList<>();

				if (this.jumps < this.maxJumps) {
					if (this.tickCount != 0 && this.tickCount % 3 == 0) {
						Set<LivingEntity> newTargets = new HashSet<>();

						entityLoop:
						for (Entity entity : this.targets) {
							boolean isWet = entity.isInWaterOrRain();

							List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(isWet ? 6 : 4), e -> {
								Entity riding = e.getFirstPassenger();

								//Passengers are handled further down
								return riding == e || !(riding instanceof LivingEntity);
							});

							if (entities.size() > 1) {
								entities.sort(Comparator.comparingDouble(e -> e.distanceToSqr(entity)));

								for (int j = 1; j < entities.size(); j++) {
									LivingEntity newTarget = entities.get(j);

									if (!this.targets.contains(newTarget) && !newTargets.contains(newTarget)) {
										newTargets.add(newTarget);

										chain.add(Pair.of(entity, newTarget));

										float f = Mth.sqrt((float) this.source.getDeltaMovement().dot(this.source.getDeltaMovement()));

										float damage = this.damage;
										if (this.source instanceof AbstractArrow arrow) {
											damage = Mth.ceil((double) f * arrow.getBaseDamage());

											if (arrow.isCritArrow()) {
												damage += this.getRandom().nextInt((int) damage / 2 + 2);
											}
										}

										boolean blocked = false;

										for (ItemStack stack : newTarget.getArmorAndBodyArmorSlots()) {
											if (!stack.isEmpty() && stack.getItem() instanceof RubberBootsItem) {
												stack.hurtAndBreak(2, newTarget, newTarget.getEquipmentSlotForItem(stack));
												blocked = true;
											}
										}

										boolean wasShocked = false;

										if (!blocked) {
											wasShocked = newTarget.hurt(damageSource, isWet ? 2 * damage : damage);

											//Also zap all passengers >:)
											for (Entity passenger : newTarget.getPassengers()) {
												if (passenger instanceof LivingEntity living && !this.targets.contains(passenger) && !newTargets.contains(passenger)) {
													passenger.hurt(damageSource, isWet ? 2 * damage : damage);
													newTargets.add(living);
												}
											}
										}

										if (!wasShocked) {
											newTarget.addEffect(ElixirEffectRegistry.EFFECT_SHOCKED.get().createEffect(newTarget instanceof Player ? 30 : 80, 0, true, true));
										}

										continue entityLoop;
									}
								}
							}
						}

						this.targets.addAll(newTargets);

//						PacketDistributor.sendToPlayersTrackingEntity(this, new ShockArrowHitPacket(chain));

						this.jumps++;
					}
				} else {
					this.discard();
				}
			}
		}
	}
}
