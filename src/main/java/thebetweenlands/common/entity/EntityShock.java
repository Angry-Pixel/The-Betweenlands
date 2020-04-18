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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.network.clientbound.MessageShockArrowHit;

public class EntityShock extends Entity {
	private final EntityBLArrow arrow;

	private final Set<EntityLivingBase> targets = new HashSet<>();

	private int maxJumps, jumps;
	private boolean isWet;

	public EntityShock(World worldIn) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);
		this.arrow = null;
	}

	public EntityShock(World worldIn, EntityBLArrow arrow, EntityLivingBase hit, boolean isWet) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);

		this.setLocationAndAngles(arrow.posX, arrow.posY, arrow.posZ, 0, 0);
		
		this.arrow = arrow;	
		this.targets.add(hit);
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
			if(this.arrow == null) {
				this.setDead();
			} else {
				Entity shootingEntity = this.arrow.getThrower();
				DamageSource damagesource;
				if (shootingEntity == null) {
					damagesource = DamageSource.causeArrowDamage(this.arrow, this.arrow);
				} else {
					damagesource = DamageSource.causeArrowDamage(this.arrow, shootingEntity);
				}

				List<Pair<Entity, Entity>> chain = new ArrayList<>();

				if(this.jumps < this.maxJumps) {
					if(this.ticksExisted != 0 && this.ticksExisted % 3 == 0) {
						Set<EntityLivingBase> newTargets = new HashSet<>();

						entityLoop: for(Entity entity : this.targets) {
							boolean isWet = entity.isWet() || entity.isInWater() || this.world.isRainingAt(entity.getPosition().up());

							List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(isWet ? 6 : 4));

							if(entities.size() > 1) {
								Collections.sort(entities, (e1, e2) -> Double.compare(e1.getDistanceSq(entity), e2.getDistanceSq(entity)));

								for(int j = 1; j < entities.size(); j++) {
									EntityLivingBase newTarget = entities.get(j);

									if(!this.targets.contains(newTarget) && !newTargets.contains(newTarget)) {
										newTargets.add(newTarget);

										chain.add(Pair.of(entity, newTarget));

										float f = MathHelper.sqrt(this.arrow.motionX * this.arrow.motionX + this.arrow.motionY * this.arrow.motionY + this.arrow.motionZ * this.arrow.motionZ);
										float damage = MathHelper.ceil((double)f * this.arrow.getDamage());
										if (this.arrow.getIsCritical()) {
											damage += this.rand.nextInt((int)damage / 2 + 2);
										}

										newTarget.attackEntityFrom(damagesource, isWet ? 2 * damage : damage);

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
