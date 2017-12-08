package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySporeling extends EntityCreature implements IEntityBL {
	private static final DataParameter<Boolean> IS_FALLING = EntityDataManager.<Boolean>createKey(EntitySporeling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> FLOATING_ROTATION = EntityDataManager.<Integer>createKey(EntitySporeling.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PREV_FLOATING_ROTATION = EntityDataManager.<Integer>createKey(EntitySporeling.class, DataSerializers.VARINT);

	public EntitySporeling(World world) {
		super(world);
		setSize(0.3F, 0.6F);
		stepHeight = 1.0F;

		this.setPathPriority(PathNodeType.WATER, -1.0F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_FALLING, false);
		dataManager.register(FLOATING_ROTATION, 0);
		dataManager.register(PREV_FLOATING_ROTATION, 0);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAvoidEntity<EntityLivingBase>(this, EntityLivingBase.class, entity -> entity instanceof EntityMob || entity instanceof IMob || (entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()), 10.0F, 1.0D, 0.5D));
		tasks.addTask(2, new EntityAIPanic(this, 1.0D));
		tasks.addTask(3, new EntityAIWander(this, 0.6D));
		tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(5, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.49D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public void onLivingUpdate() {
		if(this.world.isRemote) {
			BLParticles.REDSTONE_DUST.spawn(this.world, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height - 0.25D, posZ + (rand.nextDouble() - 0.5D) * width, 
					ParticleArgs.get().withColor(0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 1.0F));
		}
		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		if (!getEntityWorld().isRemote) {
			if (!this.isInWater()) {
				if (!onGround && motionY < 0D && world.isAirBlock(getPosition().down())) {
					if (!getIsFalling())
						setIsFalling(true);
					motionY *= 0.7D;
					if (getFloatingRotation() < 360)
						setFloatingRotation(getFloatingRotation() + 10);
					if (getFloatingRotation() >= 360)
						setFloatingRotation(0);
					setPrevFloatingRotation(getFloatingRotation());
				} else {
					if (onGround && getIsFalling()) {
						setFloatingRotation(0);
						setPrevFloatingRotation(getFloatingRotation());
						setIsFalling(false);
					}
				}
			}
		}
		super.onUpdate();
	}

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return getPrevFloatingRotation() + (getFloatingRotation() - getPrevFloatingRotation()) * partialTicks;
    }

	private void setIsFalling(boolean state) {
		dataManager.set(IS_FALLING, state);
	}

	public boolean getIsFalling() {
		return dataManager.get(IS_FALLING);
	}

	private void setFloatingRotation(int rotation) {
		dataManager.set(FLOATING_ROTATION, rotation);
	}

	public int getFloatingRotation() {
		return dataManager.get(FLOATING_ROTATION);
	}

	private void setPrevFloatingRotation(int rotation) {
		dataManager.set(PREV_FLOATING_ROTATION, rotation);
	}

	public int getPrevFloatingRotation() {
		return dataManager.get(PREV_FLOATING_ROTATION);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPORELING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SPORELING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SPORELING_DEATH;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPORELING;
	}
}
