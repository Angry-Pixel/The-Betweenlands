package thebetweenlands.common.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityFishBait extends EntityItem {
//	private static final DataParameter<Integer> SATURATION = EntityDataManager.createKey(EntityFishBait.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SINK_SPEED = EntityDataManager.createKey(EntityFishBait.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DISSOLVE_TIME = EntityDataManager.createKey(EntityFishBait.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RANGE = EntityDataManager.createKey(EntityFishBait.class, DataSerializers.VARINT);
	private static final byte EVENT_DEAD = 110;

	public EntityFishBait(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityFishBait(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
	}

	public EntityFishBait(World world) {
		super(world);
	}

    @Override
    protected void entityInit() {
        super.entityInit();
 //       dataManager.register(SATURATION, 200); // ticks before fish are hungry again (lower will be better) 
        dataManager.register(SINK_SPEED, 3); // 0 for surface floating ;)
        dataManager.register(DISSOLVE_TIME, 200); //ticks it should exist for in water
        dataManager.register(RANGE, 1); // will increase range somehow probably with an aabb :P
    }

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(isInWater()) {
			motionX *= 0.9D;
			motionY = -(float) getBaitSinkSPeed() * 0.02D;
			motionZ *= 0.9D;
			if(!getEntityWorld().isRemote) {
				if(!hasNoGravity())
					setNoGravity(true);
				if(ticksExisted >= getBaitDissolveTime())
					setDead();
			
			if(getBaitRange() >= 1)
				lureCloseFish();
			}
			
		} else {
			if(!getEntityWorld().isRemote)
				if(hasNoGravity())
					setNoGravity(false);
		}
	}

	public AxisAlignedBB extendRangeBox() {
		return  new AxisAlignedBB(posX - 0.125D, posY - 0.125D, posZ - 0.125D, posX + 0.125D, posY + 0.125D, posZ + 0.125D).grow(getBaitRange()* 0.5D, getBaitRange()* 0.5D, getBaitRange()* 0.5D);
	}

	@SuppressWarnings("unchecked")
	public void lureCloseFish() {
		List<EntityAnadia> list = getEntityWorld().getEntitiesWithinAABB(EntityAnadia.class, extendRangeBox());
		for (Iterator<EntityAnadia> iterator = list.iterator(); iterator.hasNext();) {
			EntityAnadia fish = iterator.next();
			if (!fish.isInWater() || !fish.canEntityBeSeen(this) )// || fish.getHungerCooldown() > 0)
				iterator.remove();
		}
		if (list.isEmpty())
			return;
		if (!list.isEmpty()) {
			EntityAnadia foundFish = list.get(0);
			if(foundFish.aiFindBait != null) {
				foundFish.aiFindBait.bait = this;
				foundFish.aiFindBait.updateTask();
			}
		}
	}

	@Override
    public void setDead() {
        super.setDead();
		if(!getEntityWorld().isRemote)
			getEntityWorld().setEntityState(this, EVENT_DEAD);
    }

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_DEAD) {
			for (int count = 0; count <= 10; ++count) {
				BLParticles.ITEM_BREAKING.spawn(getEntityWorld(), posX + (getEntityWorld().rand.nextDouble() - 0.5D), posY + (getEntityWorld().rand.nextDouble() - 0.5D), posZ + (getEntityWorld().rand.nextDouble() - 0.5D), ParticleArgs.get().withData(new ItemStack(ItemRegistry.FISH_BAIT)));
			}
		}
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}
/*
    public int getBaitSaturation() {
        return dataManager.get(SATURATION);
    }

    public void setBaitSaturation(int count) {
        dataManager.set(SATURATION, count);
    }
*/    
    public int getBaitSinkSPeed() {
        return dataManager.get(SINK_SPEED);
    }

    public void setBaitSinkSpeed(int weight) {
        dataManager.set(SINK_SPEED, weight);
    }
    
    public int getBaitDissolveTime() {
        return dataManager.get(DISSOLVE_TIME);
    }

    public void setBaitDissolveTime(int life) {
        dataManager.set(DISSOLVE_TIME, life);
    }
    
    public int getBaitRange() {
        return dataManager.get(RANGE);
    }

    public void setBaitRange(int range) {
        dataManager.set(RANGE, range);
    }
    
	public void shoot(Entity shooter, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy) {
		float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		float f1 = -MathHelper.sin(pitch * 0.017453292F);
		float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		shoot((double) f, (double) f1, (double) f2, velocity, inaccuracy);
		motionX += shooter.motionX;
		motionZ += shooter.motionZ;

		if (!shooter.onGround) {
			motionY += shooter.motionY;
		}
	}

	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		x = x / (double) f;
		y = y / (double) f;
		z = z / (double) f;
		x = x + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		y = y + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		z = z + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		x = x * (double) velocity;
		y = y * (double) velocity;
		z = z * (double) velocity;
		motionX = x;
		motionY = y;
		motionZ = z;
		float f1 = MathHelper.sqrt(x * x + z * z);
		rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
		rotationPitch = (float) (MathHelper.atan2(y, (double) f1) * (180D / Math.PI));
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
	}
	
}