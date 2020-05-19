package thebetweenlands.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityFishBait extends EntityItem {
	private static final DataParameter<Integer> HUNGER_VALUE = EntityDataManager.createKey(EntityFishBait.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DENSITY_VALUE = EntityDataManager.createKey(EntityFishBait.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LIFESPAN = EntityDataManager.createKey(EntityFishBait.class, DataSerializers.VARINT);
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
        dataManager.register(HUNGER_VALUE, 200); // ticks before fish are hungry again (lower will be better) 
        dataManager.register(DENSITY_VALUE, 1); // 0 for surface floating ;)
        dataManager.register(LIFESPAN, 2000); //ticks it should exist for
    }

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(isInWater()) {
			motionX *= 0.9D;
			motionY = -(float) getBaitDensityValue() * 0.02D;
			motionZ *= 0.9D;
			if(!getEntityWorld().isRemote) {
				if(!hasNoGravity())
					setNoGravity(true);
				if(ticksExisted >= getBaitLifespanValue())
					setDead();
			}
		} else {
			if(!getEntityWorld().isRemote)
				if(hasNoGravity())
					setNoGravity(false);
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

    public int getBaitHungerValue() {
        return dataManager.get(HUNGER_VALUE);
    }

    private void setBaitHungerValue(int count) {
        dataManager.set(HUNGER_VALUE, count);
    }
    
    public int getBaitDensityValue() {
        return dataManager.get(DENSITY_VALUE);
    }

    private void setBaitDensityValue(int weight) {
        dataManager.set(DENSITY_VALUE, weight);
    }
    
    public int getBaitLifespanValue() {
        return dataManager.get(LIFESPAN);
    }

    private void setBaitLifespanValue(int life) {
        dataManager.set(LIFESPAN, life);
    }
}