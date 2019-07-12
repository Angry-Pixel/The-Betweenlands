package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySplodeshroom extends EntityProximitySpawner {
	public int MAX_SWELL = 40;
	public int MIN_SWELL = 0;
	private static final DataParameter<Boolean> IS_SWELLING = EntityDataManager.createKey(EntityProximitySpawner.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SWELL_COUNT = EntityDataManager.createKey(EntityProximitySpawner.class, DataSerializers.VARINT);

	public EntitySplodeshroom(World world) {
		super(world);
		setSize(0.5F, 1F);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_SWELLING, false);
		dataManager.register(SWELL_COUNT, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime() % 5 == 0)
			checkArea();

		if (!getEntityWorld().isRemote) {
		if (!getSwelling() && getSwellCount() > MIN_SWELL)
			setSwellCount(getSwellCount() - 1);

		if (getSwelling() && getSwellCount() < MAX_SWELL)
			setSwellCount(getSwellCount() + 1);
		}
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		Entity entity = null;
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);

				if (entity != null) {
					if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
						if (canSneakPast() && entity.isSneaking())
							return null;
						else if (checkSight() && !canEntityBeSeen(entity))
							return null;
						else {
							if(!getSwelling())
								setSwelling(true);
						}

						if (!isDead && isSingleUse() && getSwellCount() >= MAX_SWELL) {
							explode();
							setDead();
						}
					}
				}
			}
			if (entity == null) {
				if (getSwelling())
					setSwelling(false);
			}
		}
		return entity;
	}

	private void explode() {
		EntityAreaEffectCloud entity = (EntityAreaEffectCloud) getEntitySpawned();
		performPreSpawnaction(this, entity);
		entity.setRadius(2F);
		entity.setDuration(240);
		entity.setWaitTime(10);
		entity.setRadiusPerTick(-entity.getRadius() / (float) entity.getDuration());
		entity.addEffect(new PotionEffect(MobEffects.BLINDNESS, 60));
		getEntityWorld().spawnEntity(entity);
	}

	private void setSwelling(boolean swell) {
		dataManager.set(IS_SWELLING, swell);
	}
	
    public boolean getSwelling() {
        return dataManager.get(IS_SWELLING);
    }

	private void setSwellCount(int swellCountIn) {
		dataManager.set(SWELL_COUNT, swellCountIn);
	}

	public int getSwellCount() {
		return dataManager.get(SWELL_COUNT);
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY += y;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if (sourceEntity instanceof EntityPlayer) {
				if (!getEntityWorld().isRemote) {
					explode();
					setDead();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 3F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		return new EntityAreaEffectCloud(getEntityWorld());
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
}