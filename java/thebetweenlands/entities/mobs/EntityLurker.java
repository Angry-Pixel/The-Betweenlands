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
	private ChunkCoordinates currentSwimTarget;

	private Class<?>[] prey = { EntityAngler.class, EntityDragonFly.class };

	private AnimationMathHelper animation = new AnimationMathHelper();

	public float moveProgress;

	public EntityLurker(World world) {
		super(world);
		setSize(1.5F, 0.9F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (byte) 0);
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

	public boolean isGrounded() {
		return !isInWater() && getRelativeBlock(1) == Blocks.air && getRelativeBlock(-1).isCollidable();
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
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
//		prevRotationPitch = rotationPitch;
//		prevRotationYaw = rotationYaw;
//		prevRotationYawHead = rotationYawHead;
//		limbSwingAmount = 0;
//		limbSwing = 0;
//		renderYawOffset = 0;
//		prevRenderYawOffset = 0;
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
			super.attackEntityAsMob(entity);
			if (isLeaping() && entity instanceof EntityDragonFly)
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), ((EntityLivingBase) entity).getMaxHealth());
		}
		if (isInWater() && entity instanceof EntityDragonFly) {
			if (distance > 0 && distance < 10 && entity.boundingBox.maxY >= boundingBox.minY && entity.boundingBox.minY <= boundingBox.maxY) {
				setIsLeaping(true);
				double distanceX = entity.posX - posX;
				double distanceZ = entity.posZ - posZ;
				float magnitude = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
				motionX = distanceX / magnitude * 0.5 * 0.9 + motionX * 1.7;
				motionZ = distanceZ / magnitude * 0.5 * 0.9 + motionZ * 1.7;
				motionY = 1;
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
}