package thebetweenlands.entities.mobs;

import java.util.List;

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
    AnimationMathHelper animation = new AnimationMathHelper();
    public float moveProgress;
    Class<?>[] preys = { EntityAngler.class, EntityDragonFly.class };
    
	public EntityLurker(World world) {
		super(world);
		this.setSize(1.5F, 0.9F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, Byte.valueOf((byte) 0));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.5D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.difficultySetting != EnumDifficulty.PEACEFUL && worldObj.getBlock((int) posX, (int) posY, (int) posZ) == BLBlockRegistry.swampWater;
	}

	@Override
    public boolean isInWater() {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
    }

    public boolean isGrounded() {
        return !isInWater() && worldObj.getBlock((int) posX, (int) posY + 1, (int) posZ) == Blocks.air && worldObj.getBlock((int) posX, (int) posY - 1, (int) posZ).isCollidable();
    }

	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        //EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
      //  setTarget(target);

        if (isInWater()) {
        	moveProgress = animation.swing(1.2F, 0.4F, false);
        	if (!worldObj.isRemote) {
    			if (getEntityToAttack() != null) {
    				currentSwimTarget = new ChunkCoordinates((int) getEntityToAttack().posX, (int) ((int) getEntityToAttack().posY), (int) getEntityToAttack().posZ);
    				swimToTarget();
    			}
    			else
    				swimAbout();
        	}
            renderYawOffset += (-((float)Math.atan2(motionX, motionZ)) * 180.0F / (float)Math.PI - renderYawOffset) * 0.1F;
            rotationYaw = renderYawOffset;
        }
        else {
        	moveProgress = animation.swing(2F, 0.4F, false);
           if (!worldObj.isRemote) {
            	if(!onGround)
            	{
                motionX *= 0.4D;
                motionY -= 0.08D;
                motionY *= 0.9800000190734863D;
                motionZ *= 0.4D;
                }
            	else if(onGround) {
            		setIsLeaping((byte) 0);
            		motionX *= 3.0D;
                    motionY = 0.01D;
                    motionZ *= 3.0D;
				}
            }
        }
    }

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
			setDead();
		if (findEnemyToAttack() != null)
			entityToAttack = findEnemyToAttack();
		else
			entityToAttack = null;
	}

	public void swimAbout() {
		if (currentSwimTarget != null && (worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ) != BLBlockRegistry.swampWater && worldObj.getBlock(currentSwimTarget.posX, currentSwimTarget.posY, currentSwimTarget.posZ) != Blocks.water || currentSwimTarget.posY < 1))
			currentSwimTarget = null;

		if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistanceSquared((int) posX, (int) posY, (int) posZ) < 10.0F)
			currentSwimTarget = new ChunkCoordinates((int) posX + rand.nextInt(10) - rand.nextInt(10), (int) posY - rand.nextInt(4) + 1, (int) posZ + rand.nextInt(10) - rand.nextInt(10));

		swimToTarget();
	}

	protected void swimToTarget() {
		double targetX = currentSwimTarget.posX + 0.5D - posX;
		double targetY = currentSwimTarget.posY - posY;
		double targetZ = currentSwimTarget.posZ + 0.5D - posZ;
		motionX += (Math.signum(targetX) * 0.3D - motionX) * 0.20000000149011612D;
		motionY += (Math.signum(targetY) * 0.599999988079071D - motionY) * 0.010000000149011612D;
		motionY -= 0.01D;
		motionZ += (Math.signum(targetZ) * 0.3D - motionZ) * 0.20000000149011612D;
		moveForward = 0.5F;
	}

	@SuppressWarnings("unchecked")
	protected Entity findEnemyToAttack() {
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(8D, 10D, 8D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null) {
				if (!(entity instanceof EntityLivingBase))
					continue;
				for (int j = 0; j < preys.length; j++)
					if (entity.getClass() == preys[j])
						return entity;
			}
		}
		return null;
	}

	@Override
	protected void attackEntity(Entity entity, float distance) {
		if (distance < 2.0F && entity.boundingBox.maxY >= boundingBox.minY && entity.boundingBox.minY <= boundingBox.maxY) {
			super.attackEntityAsMob(entity);
			if(getIsLeaping() == 1 && entity instanceof EntityDragonFly)
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), ((EntityLivingBase) entity).getMaxHealth());
			}
			if (isInWater() && entity instanceof EntityDragonFly) {
				if (distance > 0.0F && distance < 10F  && entity.boundingBox.maxY >= boundingBox.minY && entity.boundingBox.minY <= boundingBox.maxY && rand.nextInt(1) == 0) {
					setIsLeaping((byte) 1);
					double distanceX = entity.posX - posX;
					double distanceZ = entity.posZ - posZ;
					float distanceSqrRoot = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
					motionX = distanceX / distanceSqrRoot * 0.5D * 0.900000011920929D + motionX * 1.70000000298023224D;
					motionZ = distanceZ / distanceSqrRoot * 0.5D * 0.900000011920929D + motionZ * 1.70000000298023224D;
					motionY = 1.0D;
				}
			}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.inWall) || source.equals(DamageSource.drown))
			return false;
		return super.attackEntityFrom(source, damage);
	}

	public int getIsLeaping() {
		return dataWatcher.getWatchableObjectByte(20);
	}
	
	public void setIsLeaping(byte leaping) {
		dataWatcher.updateObject(20, Byte.valueOf((byte) leaping));
	}
}