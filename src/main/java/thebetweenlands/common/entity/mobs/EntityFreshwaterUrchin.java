package thebetweenlands.common.entity.mobs;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.registries.LootTableRegistry;

public class EntityFreshwaterUrchin extends EntityProximitySpawner {
	private static final DataParameter<Integer> SPIKE_COOLDOWN = EntityDataManager.createKey(EntityFreshwaterUrchin.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SPIKE_BOX_SIZE = EntityDataManager.createKey(EntityFreshwaterUrchin.class, DataSerializers.VARINT);
	private boolean shootSpikes;
	public int MAX_SPIKE_TIMER = 10;

	public EntityFreshwaterUrchin(World world) {
		super(world);
		setSize(0.6875F, 0.4375F);
		setPathPriority(PathNodeType.WATER, 4.0F);
		stepHeight = 1F;
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, 10.0F, 1D, 1D));
		tasks.addTask(1, new EntityAIWander(this, 1.0D, 40));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SPIKE_COOLDOWN, 80);
		dataManager.register(SPIKE_BOX_SIZE, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05D);
	}

	public int getSpikeGrowTimer() {
		return dataManager.get(SPIKE_COOLDOWN);
	}

	public void setSpikeGrowTimer(int count) {
		dataManager.set(SPIKE_COOLDOWN, count);
	}

	public int getSpikeBoxTimer() {
		return dataManager.get(SPIKE_BOX_SIZE);
	}

	public void setSpikeBoxTimer(int count) {
		dataManager.set(SPIKE_BOX_SIZE, count);
	}

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.FRESHWATER_URCHIN;
    }

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
				checkAOEDamage();

			if (getSpikeGrowTimer() < 80)
				setSpikeGrowTimer(getSpikeGrowTimer() + 1);

			if (getSpikeGrowTimer() >= 80)
				if (getEntityWorld().getTotalWorldTime() % 5 == 0)
					checkAreaHere();
			
			if(shootSpikes) {
				if (getSpikeBoxTimer() < MAX_SPIKE_TIMER)
					setSpikeBoxTimer(getSpikeBoxTimer() + 1);
				if (getSpikeBoxTimer() >= MAX_SPIKE_TIMER) {
					shootSpikes = false;
					setSpikeBoxTimer(0);
				}
			}
		}
	}

	private void checkAOEDamage() {
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, spikesBox());
			for (Iterator<EntityLivingBase> iterator = list.iterator(); iterator.hasNext();) {
				EntityLivingBase entity  = iterator.next();
				if (entity != null && (entity instanceof EntityFreshwaterUrchin) || entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator() || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
					iterator.remove();
			}
			if (list.isEmpty()) {
				return;
			}
			if (!list.isEmpty()) {
				EntityLivingBase entity = list.get(0);
				if(entity.hurtResistantTime <= 0) {
					entity.attackEntityFrom(new EntityDamageSource("cactus", this), 2F);
					shootSpikes = false;
					setSpikeBoxTimer(0);
				}
			}
		}
		
	}

	public void checkAreaHere() {
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL && isInWater()) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, proximityBox());
			for (Iterator<EntityLivingBase> iterator = list.iterator(); iterator.hasNext();) {
				EntityLivingBase entity  = iterator.next();
				if (entity != null && (entity instanceof EntityFreshwaterUrchin) || entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator() || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
					iterator.remove();
			}
			if (list.isEmpty()) {
				return;
			}
			if (!list.isEmpty()) {
				EntityLivingBase entity = list.get(0);

					if (canSneakPast() && entity.isSneaking())
						return;
					else if (checkSight() && !canEntityBeSeen(entity))
						return;
					else 
						shootSpikes();
					if (!isDead && isSingleUse())
						setDead();
			}
		}
	}

	private void shootSpikes() {
		//TODO add particles
		setSpikeGrowTimer(0);
		shootSpikes = true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source == DamageSource.DROWN)
			return false;
		return super.attackEntityFrom(source, damage);
	}

    @Override
    public void travel(float strafe, float up, float forward) {
        if (isServerWorld()) {
            if (isInWater()) {
                moveRelative(strafe, up, forward, 0.1F);
                move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.75D;
				motionY *= 0.75D;
				motionZ *= 0.75D;
				motionY -= 0.006D;
            } else {
                move(MoverType.SELF, motionX, motionY, motionZ);
				motionX = 0D;
				motionY = 0D;
				motionZ = 0D;
				motionY -= 0.2D;
            }
        } else {
            super.travel(strafe, up, forward);
        }
    }

	@Override
    public float getEyeHeight(){
        return this.height;
    }

	@Override
	protected float getProximityHorizontal() {
		return 2;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	@Override
	public AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(posX -0.5D, posY, posZ - 0.5D, posX + 0.5D, posY + 1D, posZ + 0.5D).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).offset(0D, getProximityVertical() + height , 0D);
	}

	public AxisAlignedBB spikesBox() {
		float x = (getProximityHorizontal() / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		float y = (getProximityVertical() / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		float z = (getProximityHorizontal() / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		return new AxisAlignedBB(posX -0.5D, posY, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D).grow(x, y, z).offset(0D, y , 0D);
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
		return null;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 0;
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
