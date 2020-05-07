package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;
import thebetweenlands.common.entity.mobs.EntitySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EntityWormGroundSpawner extends EntityCCGroundSpawner {

	public EntityWormGroundSpawner(World world) {
		super(world);
		setSize(3F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public boolean isSpawnEventActive(World world) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
        if(worldStorage.getEnvironmentEventRegistry().heavyRain.isActive())
            return true;
        return false;
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		if (!getEntityWorld().isRemote) {
			if(getCanBeRemovedSafely() && canBeRemovedNow())
				setDead();
			if (getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
				if(isWorldSpawned() && !isSpawnEventActive(getEntityWorld()))
					return null;
				List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, proximityBox());
				if(list.stream().filter(e -> e instanceof EntitySludgeWorm).count() >= 4)
					return null;
				for (int entityCount = 0; entityCount < list.size(); entityCount++) {
					Entity entity = list.get(entityCount);
					if (entity != null)
						if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
							if (canSneakPast() && entity.isSneaking())
								return null;
							else if (checkSight() && !canEntityBeSeen(entity) || getCanBeRemovedSafely())
								return null;
							else {
								for (int count = 0; count < getEntitySpawnCount(); count++) {
									Entity spawn = getEntitySpawned();
									if (spawn != null) {
										performPreSpawnaction(entity, spawn);
										if (!spawn.isDead) // just in case of pre-emptive removal
											getEntityWorld().spawnEntity(spawn);
										performPostSpawnaction(entity, spawn);
									}
								}
							}
						}
				}
			}
		}
		return null;
	}

	@Override
    public boolean canBeRemovedNow() {
    	AxisAlignedBB dead_zone = getEntityBoundingBox().grow(0D, 1D, 0D).offset(0D, -0.5D, 0D);
		List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, dead_zone);
		if(list.stream().filter(e -> e instanceof EntitySludgeWorm).count() >= 1)
			return false;
        return true;
    }

	@Override
	protected Entity getEntitySpawned() {
		EntityLiving worm = null;
		int rand = getEntityWorld().rand.nextInt(5);

		switch (rand) {
		case 0:
			worm = new EntityLargeSludgeWorm(getEntityWorld());
			break;
		case 1:
		case 2:
			worm  = new EntitySludgeWorm(getEntityWorld());
			break;
		case 3:
		case 4:
			worm  = new EntityTinySludgeWorm(getEntityWorld());
			break;
		}

		if(worm != null)
			((EntityLiving) worm).onInitialSpawn(getEntityWorld().getDifficultyForLocation(getPosition()), null);
		return worm;
	}
	
	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(isWorldSpawned())
			setSpawnCount(getSpawnCount() + 1);
		getEntityWorld().playSound((EntityPlayer)null, getPosition(), getDigSound(), SoundCategory.HOSTILE, 0.5F, 1.0F);
		entitySpawned.setPosition(getPosition().getX() + 0.5F, getPosition().getY() - 0.0F, getPosition().getZ() + 0.5F);
	}

	@Override
	protected int maxUseCount() {
		return 8;
	}
}