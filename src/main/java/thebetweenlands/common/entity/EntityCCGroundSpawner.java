package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;

public class EntityCCGroundSpawner extends EntityProximitySpawner {

	public EntityCCGroundSpawner(World world) {
		super(world);
		setSize(1F, 0.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (getEntityWorld().getTotalWorldTime() % 60 == 0)
				checkArea();
			List<EntityFallingBlock> listPlug = getEntityWorld().getEntitiesWithinAABB(EntityFallingBlock.class, getEntityBoundingBox());
			if (!listPlug.isEmpty()) {
				getEntityWorld().setBlockToAir(getPosition().down());
				setDead();
			}
		}
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				Entity entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
						if (canSneakPast() && entity.isSneaking())
							return null;
						else if (checkSight() && !canEntityBeSeen(entity))
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
		return null;
	}

	@Override
    public float getEyeHeight() {
        return height + 0.5F; // sort of needed so it can see a bit further
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
	public void onKillCommand() {
		this.setDead();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntityPlayer && ((EntityPlayer) sourceEntity).isCreative()) {
				setDead();
			}
		}
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity instanceof EntityFallingBlock)
			if (!getEntityWorld().isRemote)
				setDead();
	}

	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		entitySpawned.setPosition(getPosition().getX() + 0.5F, getPosition().getY() - 1.5F, getPosition().getZ() + 0.5F);
	}

	@Override
	protected void performPostSpawnaction(Entity targetEntity, @Nullable Entity entitySpawned) {
		if(!entitySpawned.getEntityWorld().isRemote) {
			TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.GOOP_SPLAT, (float) posX, (float)posY + 0.25F, (float)posZ, 0F));
			entitySpawned.motionY += 0.5D;
		}
	}

	@Override
	protected float getProximityHorizontal() {
		return 8F;
	}

	@Override
	protected float getProximityVertical() {
		return 2F;
	}

	@Override
	protected boolean canSneakPast() {
		return false;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		EntityCryptCrawler crawler = new EntityCryptCrawler(getEntityWorld());
		crawler.onInitialSpawn(getEntityWorld().getDifficultyForLocation(getPosition()), null);
		return crawler;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return false;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
}