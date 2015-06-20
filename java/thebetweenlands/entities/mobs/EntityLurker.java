package thebetweenlands.entities.mobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;

public class EntityLurker extends EntityMob implements IEntityBL {
	private static final int MOUTH_OPEN_TICKS = 20;

	private ChunkCoordinates currentSwimTarget;

	private Class<?>[] prey = { EntityAngler.class, EntityDragonFly.class };

	private float prevRotationPitchBody;
	private float rotationPitchBody;

	private float prevTailYaw;
	private float tailYaw;

	private float prevTailPitch;
	private float tailPitch;

	private float prevMouthOpenTicks;
	private float mouthOpenTicks;

	private int ticksUntilBiteDamage = -1;

	private Entity entityBeingBit;

	private int anger = 0;

	public EntityLurker(World world) {
		super(world);
		setSize(1.5F, 0.9F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (byte) 0);
		dataWatcher.addObject(21, (byte) 0);
		dataWatcher.addObject(22, (float) 1);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.5);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.difficultySetting != EnumDifficulty.PEACEFUL && getRelativeBlock(0) == BLBlockRegistry.swampWater;
	}

	@Override
	public boolean isInWater() {
		return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
	}

	private Block getRelativeBlock(int offsetY) {
		return worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) + offsetY, MathHelper.floor_double(posZ));
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (isInWater()) {
			if (!worldObj.isRemote) {
				Entity entityToAttack = getEntityToAttack();
				if (entityToAttack == null) {
					swimAbout();
				} else {
					currentSwimTarget = new ChunkCoordinates(MathHelper.floor_double(entityToAttack.posX), MathHelper.floor_double(entityToAttack.posY), MathHelper.floor_double(entityToAttack.posZ));
					swimToTarget();
				}
				if (motionY < 0 && isLeaping()) {
					setIsLeaping(false);
				}
			}
			renderYawOffset += (-((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI - renderYawOffset) * 0.1F;
			rotationYaw = renderYawOffset;
		} else {
			if (!worldObj.isRemote) {
				if (onGround) {
					setIsLeaping(false);
				} else {
					motionX *= 0.4;
					motionY -= 0.08;
					motionY *= 0.98;
					motionZ *= 0.4;
				}
			}
		}
		float magnitude = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ) * (onGround ? 0 : 1);
		float motionPitch = (float) Math.atan2(magnitude, motionY) / (float) Math.PI * 180 - 90;
		if (magnitude > 1) {
			magnitude = 1;
		}
		float newRotationPitch = (rotationPitchBody - motionPitch) * magnitude * 4 * (getRelativeBlock(2).getMaterial() == Material.water || !inWater ? 1 : 0);
		tailPitch += (rotationPitchBody - newRotationPitch);
		rotationPitchBody = newRotationPitch;
		if (Math.abs(rotationPitchBody) < 0.05F) {
			rotationPitchBody = 0;
		}
	}

	@Override
	public void onUpdate() {
		// debug walk animation
		if (false) {
			prevRotationYaw = rotationYaw = 0;
			prevRotationYawHead = rotationYawHead = 0;
			prevRenderYawOffset = renderYawOffset = 0;
			prevRotationPitch = rotationPitch = 0;
			prevRotationPitchBody = rotationPitchBody = 0;

			prevTailPitch = tailPitch = 0;
			prevTailYaw = tailYaw = 0;

			prevLimbSwingAmount = limbSwingAmount;
			float speed = Math.min(0.08F * 4, 1);
			limbSwingAmount += (speed - limbSwingAmount) * 0.4F;
			limbSwing += limbSwingAmount;
			return;
		}
		prevRotationPitchBody = rotationPitchBody;
		prevTailPitch = tailPitch;
		prevTailYaw = tailYaw;
		while (rotationPitchBody - prevRotationPitchBody < -180) {
			prevRotationPitchBody -= 360;
		}
		while (rotationPitchBody - prevRotationPitchBody >= 180) {
			prevRotationPitchBody += 360;
		}
		while (tailPitch - prevTailPitch < -180) {
			prevTailPitch -= 360;
		}
		while (tailPitch - prevTailPitch >= 180) {
			prevTailPitch += 360;
		}
		while (tailYaw - prevTailYaw < -180) {
			prevTailYaw -= 360;
		}
		while (tailYaw - prevTailYaw >= 180) {
			prevTailYaw += 360;
		}
		prevMouthOpenTicks = mouthOpenTicks;
		super.onUpdate();
		if (shouldMouthBeOpen()) {
			if (mouthOpenTicks < MOUTH_OPEN_TICKS) {
				mouthOpenTicks += getMouthMoveSpeed();
			}
			if (mouthOpenTicks > MOUTH_OPEN_TICKS) {
				mouthOpenTicks = MOUTH_OPEN_TICKS;
			}
		} else {
			if (mouthOpenTicks > 0) {
				mouthOpenTicks -= getMouthMoveSpeed();
			}
			if (mouthOpenTicks < 0) {
				mouthOpenTicks = 0;
			}
		}
		if (ticksUntilBiteDamage > -1) {
			ticksUntilBiteDamage--;
			if (ticksUntilBiteDamage == -1) {
				setShouldMouthBeOpen(false);
				if (entityBeingBit != null) {
					if (!entityBeingBit.isDead) {
						super.attackEntityAsMob(entityBeingBit);
						if (isLeaping() && entityBeingBit instanceof EntityDragonFly) {
							entityBeingBit.attackEntityFrom(DamageSource.causeMobDamage(this), ((EntityLivingBase) entityBeingBit).getMaxHealth());
						}
					}
					entityBeingBit = null;
				}
			}
		}
		float movementSpeed = MathHelper.sqrt_double((prevPosX - posX) * (prevPosX - posX) + (prevPosY - posY) * (prevPosY - posY) + (prevPosZ - posZ) * (prevPosZ - posZ));
		if (movementSpeed > 1) {
			movementSpeed = 1;
		} else if (movementSpeed < 0.08) {
			movementSpeed = 0;
		}
		if (Math.abs(tailYaw) < 90) {
			tailYaw += (prevRenderYawOffset - renderYawOffset);
		}
		if (Math.abs(tailPitch) < 90) {
			tailPitch += (prevRotationPitchBody - rotationPitchBody);
		}
		tailPitch *= (1 - movementSpeed);
		tailYaw *= (1 - movementSpeed);
	}

	@Override
	protected void updateEntityActionState() {
		super.updateEntityActionState();
		if (entityToAttack == null) {
			entityToAttack = findEnemyToAttack();
		} else {
			if (entityToAttack.getDistanceSqToEntity(this) > 256) {
				entityToAttack = null;
			}
		}
		if (anger > 0) {
			anger--;
			if (anger == 0) {
				entityToAttack = null;
			}
		}
	}

	@Override
	protected Entity findPlayerToAttack() {
		return anger == 0 ? null : super.findPlayerToAttack();
	}

	public void swimAbout() {
		if (currentSwimTarget != null && (worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ).getMaterial() != Material.water || currentSwimTarget.posY < 1)) {
			currentSwimTarget = null;
		}
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(boundingBox.minY);
		int z = MathHelper.floor_double(posZ);
		if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistanceSquared(x, y, z) < 4) {
			currentSwimTarget = new ChunkCoordinates(x + rand.nextInt(10) - rand.nextInt(10), y - rand.nextInt(4) + 1, z + rand.nextInt(10) - rand.nextInt(10));
		}
		swimToTarget();
	}

	private void swimToTarget() {
		double targetX = currentSwimTarget.posX + 0.5 - posX;
		double targetY = currentSwimTarget.posY - posY;
		double targetZ = currentSwimTarget.posZ + 0.5 - posZ;
		motionX += (Math.signum(targetX) * 0.2 - motionX) * 0.2;
		motionY += (Math.signum(targetY) * 0.4 - motionY) * 0.01;
		motionY -= 0.01;
		motionZ += (Math.signum(targetZ) * 0.2 - motionZ) * 0.2;
		moveForward = 0.5F;
	}

	private Entity findEnemyToAttack() {
		List<Entity> nearEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(8, 10, 8));
		for (int i = 0; i < nearEntities.size(); i++) {
			Entity entity = nearEntities.get(i);
			for (int n = 0; n < prey.length; n++) {
				if (entity.getClass() == prey[n]) {
					return entity;
				}
			}
		}
		return null;
	}

	@Override
	protected void attackEntity(Entity entity, float distance) {
		if (distance < 3 && entity.boundingBox.maxY >= boundingBox.minY && entity.boundingBox.minY <= boundingBox.maxY && ticksUntilBiteDamage == -1) {
			setShouldMouthBeOpen(true);
			setMouthMoveSpeed(10);
			ticksUntilBiteDamage = 10;
			entityBeingBit = entity;
		}
		if (inWater && entity instanceof EntityDragonFly && !isLeaping() && distance < 5) {
			setIsLeaping(true);
			double distanceX = entity.posX - posX;
			double distanceZ = entity.posZ - posZ;
			float magnitude = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
			motionX += distanceX / magnitude * 0.8;
			motionZ += distanceZ / magnitude * 0.8;
			motionY += 0.9;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (isEntityInvulnerable() || source.equals(DamageSource.inWall) || source.equals(DamageSource.drown)) {
			return false;
		}
		Entity attacker = source.getEntity();
		if (attacker instanceof EntityPlayer) {
			List<EntityLurker> nearLurkers = worldObj.getEntitiesWithinAABB(EntityLurker.class, boundingBox.expand(16, 16, 16));
			for (EntityLurker fellowLurker : nearLurkers) {
				// Thou shouldst joineth me! F'r thither is a great foe comest!
				fellowLurker.showDeadlyAffectionTowards(attacker);
			}
		}
		return super.attackEntityFrom(source, damage);
	}

	private void showDeadlyAffectionTowards(Entity entity) {
		entityToAttack = entity;
		anger = 200 + rand.nextInt(100);
	}

	public boolean isLeaping() {
		return dataWatcher.getWatchableObjectByte(20) == 1;
	}

	public void setIsLeaping(boolean isLeaping) {
		dataWatcher.updateObject(20, (byte) (isLeaping ? 1 : 0));
	}

	public boolean shouldMouthBeOpen() {
		return dataWatcher.getWatchableObjectByte(21) == 1;
	}

	public void setShouldMouthBeOpen(boolean shouldMouthBeOpen) {
		dataWatcher.updateObject(21, (byte) (shouldMouthBeOpen ? 1 : 0));
	}

	public float getMouthMoveSpeed() {
		return dataWatcher.getWatchableObjectFloat(22);
	}

	public void setMouthMoveSpeed(float mouthMoveSpeed) {
		dataWatcher.updateObject(22, mouthMoveSpeed);
	}

	public float getRotationPitch(float partialRenderTicks) {
		return rotationPitchBody * partialRenderTicks + prevRotationPitchBody * (1 - partialRenderTicks);
	}

	public float getMouthOpen(float partialRenderTicks) {
		return (mouthOpenTicks * partialRenderTicks + prevMouthOpenTicks * (1 - partialRenderTicks)) / MOUTH_OPEN_TICKS;
	}

	public float getTailYaw(float partialRenderTicks) {
		return tailYaw * partialRenderTicks + prevTailYaw * (1 - partialRenderTicks);
	}

	public float getTailPitch(float partialRenderTicks) {
		return tailPitch * partialRenderTicks + prevTailPitch * (1 - partialRenderTicks);
	}

	public boolean isSimplyInWater() {
		return inWater;
	}

	@Override
	protected String getLivingSound() {
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:lurkerLiving" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		int randomSound = rand.nextInt(3) + 1;
		setShouldMouthBeOpen(true);
		ticksUntilBiteDamage = 10;
		return "thebetweenlands:lurkerHurt" + randomSound;
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:lurkerDeath";
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.LURKER_SKIN, 3), 0F);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		tagCompound.setShort("Anger", (short) anger);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tagCompound) {
		super.readEntityFromNBT(tagCompound);
		anger = tagCompound.getShort("Anger");
	}
}