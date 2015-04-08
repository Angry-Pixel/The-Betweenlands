package thebetweenlands.entities.mobs;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Items;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.AnimationMathHelper;

public class EntityAngler extends EntityWaterMob implements IEntityBL {
	

    private float randomMotionSpeed;
    private float randomMotionVecX;
    private float randomMotionVecY;
    private float randomMotionVecZ;
    AnimationMathHelper animation = new AnimationMathHelper();
    public float moveProgress;
    
	public EntityAngler(World world) {
		super(world);
		setSize(0.9F, 0.5F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:anglerAttack1";
		else
			return "thebetweenlands:anglerAttack2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:anglerDeath";
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		int amount = rand.nextInt(3) + rand.nextInt(1 + looting);
		int count;

		for (count = 0; count < amount; ++count) {
			dropItem(Items.leather, 1);
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.getBlock((int) posX, (int) posY, (int) posZ) == BLBlockRegistry.swampWater;
	}
	
    public boolean isInWater() {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        moveProgress = animation.swing(1.2F, 0.4F, false);
        if (isInWater()) {
        	randomMotionSpeed = 0.5F + rand.nextFloat();

            if (!worldObj.isRemote){
                motionX = (double)(randomMotionVecX * randomMotionSpeed);
                motionZ = (double)(randomMotionVecZ * randomMotionSpeed);
                motionY = (double)(randomMotionVecY * randomMotionSpeed);
            }
            renderYawOffset += (-((float)Math.atan2(motionX, motionZ)) * 180.0F / (float)Math.PI - renderYawOffset) * 0.1F;
            rotationYaw = renderYawOffset;
        }
        else {
            if (!worldObj.isRemote) {
                motionX = 0.0D;
                motionY -= 0.08D;
                motionY *= 0.9800000190734863D;
                motionZ = 0.0D;
            }
        }
    }

    public void moveEntityWithHeading(float strafe, float forwards) {
        moveEntity(motionX, motionY, motionZ);
    }

    protected void updateEntityActionState() {
       if (rand.nextInt(50) == 0 || !inWater || randomMotionVecX == 0.0F && randomMotionVecY == 0.0F && randomMotionVecZ == 0.0F) {
            float randomAngle = rand.nextFloat() * (float)Math.PI * 2.0F;
            randomMotionVecX = MathHelper.cos(randomAngle) * 0.2F;
            randomMotionVecY = -0.1F + rand.nextFloat() * 0.2F;
            randomMotionVecZ = MathHelper.sin(randomAngle) * 0.2F;
        }
    }

}
