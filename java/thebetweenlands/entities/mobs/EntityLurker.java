package thebetweenlands.entities.mobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.AnimationMathHelper;

public class EntityLurker extends EntityMob implements IEntityBL {
	private static final int MOUTH_OPEN_TICKS = 20;

	private ChunkCoordinates currentSwimTarget;

	private Class<?>[] prey = { EntityAngler.class, EntityDragonFly.class };

	private AnimationMathHelper animation = new AnimationMathHelper();

	public float moveProgress;

	private float prevRotationPitch;
	private float rotationPitch;

	private float prevTailYaw;
	private float tailYaw;

	private float prevTailPitch;
	private float tailPitch;

	private float prevMouthOpenTicks;
	private float mouthOpenTicks;

	private int ticksUntilBiteDamage = -1;

	private Entity entityBeingBit;

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
			moveProgress = animation.swing(1.2F, 0.4F, false);
			if (!worldObj.isRemote) {
				Entity entityToAttack = getEntityToAttack();
				if (entityToAttack == null) {
					swimAbout();
				} else {
					currentSwimTarget = new ChunkCoordinates(MathHelper.floor_double(entityToAttack.posX), MathHelper.floor_double(entityToAttack.posY), MathHelper.floor_double(entityToAttack.posZ));
					swimToTarget();
				}
			}
			renderYawOffset += (-((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI - renderYawOffset) * 0.1F;
			rotationYaw = renderYawOffset;
		} else {
			moveProgress = animation.swing(2F, 0.4F, false);
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
		float newRotationPitch = (rotationPitch - motionPitch) * magnitude * 4;
		tailPitch += (rotationPitch - newRotationPitch);
		rotationPitch = newRotationPitch;
		if (Math.abs(rotationPitch) < 0.05F) {
			rotationPitch = 0;
		}
	}

	@Override
	public void onUpdate() {
		prevRotationPitch = rotationPitch;
		prevTailPitch = tailPitch;
		prevTailYaw = tailYaw;
		while (rotationPitch - prevRotationPitch < -180) {
			prevRotationPitch -= 360;
		}
		while (rotationPitch - prevRotationPitch >= 180) {
			prevRotationPitch += 360;
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
				super.attackEntityAsMob(entityBeingBit);
				if (isLeaping() && entityBeingBit instanceof EntityDragonFly) {
					entityBeingBit.attackEntityFrom(DamageSource.causeMobDamage(this), ((EntityLivingBase) entityBeingBit).getMaxHealth());
				}
				entityBeingBit = null;
			}
		}
		float movementSpeed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
		movementSpeed *= 1.02F;
		if (movementSpeed > 1) {
			movementSpeed = 1;
		} else if (movementSpeed < 0.08) {
			movementSpeed = 0;
		}
		if (Math.abs(tailYaw) < 90) {
			tailYaw += (prevRenderYawOffset - renderYawOffset);
		}
		tailPitch *= (1 - movementSpeed);
		tailYaw *= (1 - movementSpeed);
	}

	@Override
	protected void updateEntityActionState() {
		super.updateEntityActionState();
		entityToAttack = findEnemyToAttack();
	}

	public void swimAbout() {
		if (currentSwimTarget != null && (worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ).getMaterial() != Material.water || currentSwimTarget.posY < 1)) {
			currentSwimTarget = null;
		}
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(boundingBox.minY);
		int z = MathHelper.floor_double(posZ);
		if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistanceSquared(x, y, z) < 10) {
			currentSwimTarget = new ChunkCoordinates(x + rand.nextInt(10) - rand.nextInt(10), y - rand.nextInt(4) + 1, z + rand.nextInt(10) - rand.nextInt(10));
		}
		swimToTarget();
	}

	private void swimToTarget() {
		double targetX = currentSwimTarget.posX + 0.5 - posX;
		double targetY = currentSwimTarget.posY - posY;
		double targetZ = currentSwimTarget.posZ + 0.5 - posZ;
		motionX += (Math.signum(targetX) * 0.3 - motionX) * 0.2;
		motionY += (Math.signum(targetY) * 0.6 - motionY) * 0.01;
		motionY -= 0.01;
		motionZ += (Math.signum(targetZ) * 0.3 - motionZ) * 0.2;
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
		if (distance < 2 && entity.boundingBox.maxY >= boundingBox.minY && entity.boundingBox.minY <= boundingBox.maxY) {
			setShouldMouthBeOpen(true);
			setMouthMoveSpeed(10);
			ticksUntilBiteDamage = 10;
			entityBeingBit = entity;
		}
		if (isNoHandleInWater() && entity instanceof EntityDragonFly && !isLeaping()) {
			if (distance > 0 && distance < 10 && entity.boundingBox.maxY >= boundingBox.minY && entity.boundingBox.minY <= boundingBox.maxY) {
				setIsLeaping(true);
				double distanceX = entity.posX - posX;
				double distanceZ = entity.posZ - posZ;
				float magnitude = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
				motionX += distanceX / magnitude * 0.6;
				motionZ += distanceZ / magnitude * 0.6;
				motionY += 0.7;
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.inWall) || source.equals(DamageSource.drown)) {
			return false;
		}
		return super.attackEntityFrom(source, damage);
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
		return rotationPitch * partialRenderTicks + prevRotationPitch * (1 - partialRenderTicks);
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

	public boolean isNoHandleInWater() {
		return inWater;
	}
}