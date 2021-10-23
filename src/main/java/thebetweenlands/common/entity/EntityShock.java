package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.network.clientbound.MessageShockArrowHit;

public class EntityShock extends Entity {
	private final Entity source;
	private final float damage;

	private final Set<EntityLivingBase> targets = new HashSet<>();

	private int maxJumps, jumps;
	private boolean isWet;

	public EntityShock(World worldIn) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);
		this.source = null;
		this.damage = 0.0f;
	}

	public EntityShock(World worldIn, Entity source, EntityLivingBase hit, float damage, boolean isWet) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);

		this.setLocationAndAngles(source.posX, source.posY, source.posZ, 0, 0);

		this.source = source;
		if(hit != null) {
			this.targets.add(hit);
		}
		this.damage = damage;
		this.isWet = isWet;

		this.maxJumps = 2 + this.world.rand.nextInt(3);

		if(isWet) {
			this.maxJumps = this.maxJumps * 2;
		}
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {

	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound) {
		//don't save
		return false;
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		//no moving
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			if(this.source == null) {
				this.setDead();
			} else {
				Entity shootingEntity;
				DamageSource damageSource;

				if(this.source instanceof EntityBLArrow) {
					shootingEntity = ((EntityBLArrow) this.source).getThrower();

					if(shootingEntity != null) {
						damageSource = DamageSource.causeArrowDamage((EntityBLArrow) this.source, shootingEntity);
					} else {
						damageSource = DamageSource.causeArrowDamage((EntityBLArrow) this.source, (EntityBLArrow) this.source);
					}
				} else if(this.source instanceof EntityLivingBase) {
					shootingEntity = null;
					damageSource = DamageSource.causeMobDamage((EntityLivingBase) this.source);
				} else {
					shootingEntity = null;
					damageSource = DamageSource.GENERIC;
				}

				List<Pair<Entity, Entity>> chain = new ArrayList<>();

				if(this.jumps < this.maxJumps) {
					if(this.ticksExisted != 0 && this.ticksExisted % 3 == 0) {
						Set<EntityLivingBase> newTargets = new HashSet<>();

						entityLoop: for(Entity entity : this.targets) {
							boolean isWet = entity.isWet() || entity.isInWater() || this.world.isRainingAt(entity.getPosition().up());

							List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(isWet ? 6 : 4), e -> {
								Entity riding = e.getLowestRidingEntity();

								//Passengers are handled further down
								if(riding != e && riding instanceof EntityLivingBase) {
									return false;
								}

								return true;	
							});

							if(entities.size() > 1) {
								Collections.sort(entities, (e1, e2) -> Double.compare(e1.getDistanceSq(entity), e2.getDistanceSq(entity)));

								for(int j = 1; j < entities.size(); j++) {
									EntityLivingBase newTarget = entities.get(j);

									if(!this.targets.contains(newTarget) && !newTargets.contains(newTarget)) {
										newTargets.add(newTarget);

										chain.add(Pair.of(entity, newTarget));

										float f = MathHelper.sqrt(this.source.motionX * this.source.motionX + this.source.motionY * this.source.motionY + this.source.motionZ * this.source.motionZ);

										float damage = this.damage;
										if(this.source instanceof EntityBLArrow) {
											damage = MathHelper.ceil((double)f * ((EntityBLArrow) this.source).getDamage());

											if (((EntityBLArrow) this.source).getIsCritical()) {
												damage += this.rand.nextInt((int)damage / 2 + 2);
											}
										}

										boolean blocked = false;

										for(ItemStack stack : newTarget.getEquipmentAndArmor()) {
											if(!stack.isEmpty() && stack.getItem() instanceof ItemRubberBoots) {
												stack.damageItem(2, newTarget);
												blocked = true;
											}
										}

										boolean wasShocked = false;
										
										if(!blocked) {
											wasShocked = newTarget.attackEntityFrom(damageSource, isWet ? 2 * damage : damage);

											//Also zap all passengers >:)
											for(Entity passenger : newTarget.getRecursivePassengers()) {
												if(passenger instanceof EntityLivingBase && !this.targets.contains(passenger) && !newTargets.contains(passenger)) {
													passenger.attackEntityFrom(damageSource, isWet ? 2 * damage : damage);
													newTargets.add((EntityLivingBase) passenger);
												}
											}
										}
										
										if(!wasShocked) {
											newTarget.addPotionEffect(new PotionEffect(ElixirEffectRegistry.SHOCKED, newTarget instanceof EntityPlayer ? 30 : 80));
										}

										continue entityLoop;
									}
								}
							}
						}

						this.targets.addAll(newTargets);

						TheBetweenlands.networkWrapper.sendToAllTracking(new MessageShockArrowHit(chain), this);

						this.jumps++;
					}
				} else {
					this.setDead();
				}
			}
		}
	}
}
