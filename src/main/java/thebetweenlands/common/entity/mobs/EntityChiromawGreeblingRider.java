package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawGreeblingRider extends EntityChiromaw {
	private static final DataParameter<Boolean> IS_SHOOTING = EntityDataManager.createKey(EntityChiromawGreeblingRider.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> RELOAD_TIMER = EntityDataManager.createKey(EntityChiromawGreeblingRider.class, DataSerializers.VARINT);
	public EntityChiromawGreeblingRider(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		setIsHanging(false);

		this.moveHelper = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		//this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
		//this.tasks.addTask(2, new EntityAIFlyingWander(this, 0.5D));
		//this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true).setUnseenMemoryTicks(160));
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(IS_SHOOTING, false);
		this.dataManager.register(RELOAD_TIMER, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		//WIP Temp 
		if (getReloadTimer() <= 100 && !getIsShooting())
			setReloadTimer(getReloadTimer() +1);
		if(getReloadTimer() >= 100 && !getIsShooting()) {
			setIsShooting(true);
			setReloadTimer(0);
		}

		if(getIsShooting())
			System.out.println("Shooting: " + getIsShooting());

		if(getIsShooting() && getReloadTimer() < 100)
			setIsShooting(false);
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
	}

	public boolean getIsShooting() {
		return dataManager.get(IS_SHOOTING);
	}

	public void setIsShooting(boolean shooting) {
		dataManager.set(IS_SHOOTING, shooting);
	}
	
	public int getReloadTimer() {
		return dataManager.get(RELOAD_TIMER);
	}

	public void setReloadTimer(int timer) {
		dataManager.set(RELOAD_TIMER, timer);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FLYING_FIEND_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundRegistry.FLYING_FIEND_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FLYING_FIEND_DEATH;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.095D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
}
