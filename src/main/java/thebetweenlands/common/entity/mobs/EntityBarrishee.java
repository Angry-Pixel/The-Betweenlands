package thebetweenlands.common.entity.mobs;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSoundRipple;
import thebetweenlands.common.registries.LootTableRegistry;

//TODO Loot tables
public class EntityBarrishee extends EntityMob implements IEntityScreenShake, IEntityBL {

	private static final DataParameter<Boolean> AMBUSH_SPAWNED = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SCREAM = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SCREAM_TIMER = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.VARINT);
	public float standingAngle, prevStandingAngle;

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	public int screamTimer;
	private boolean screaming;

	//Adjust to length of screaming sound
	private static final int SCREAMING_TIMER_MAX = 50;

	public EntityBarrishee(World world) {
		super(world);
		setSize(2.25F, 1.8F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(AMBUSH_SPAWNED, false);
		dataManager.register(SCREAM, false);
		dataManager.register(SCREAM_TIMER, 50);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.BARRISHEE;
	}

	public boolean isAmbushSpawn() {
		return dataManager.get(AMBUSH_SPAWNED);
	}

	public void setIsAmbushSpawn(boolean is_ambush) {
		dataManager.set(AMBUSH_SPAWNED, is_ambush);
	}

	public void setIsScreaming(boolean scream) {
		dataManager.set(SCREAM, scream);
	}

	public boolean isScreaming() {
		return dataManager.get(SCREAM);
	}

	public void setScreamTimer(int scream_timer) {
		dataManager.set(SCREAM_TIMER, scream_timer);
	}

	public int getScreamTimer() {
		return dataManager.get(SCREAM_TIMER);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityBarrishee.AIBarrisheeAttack(this));
		tasks.addTask(3, new EntityAIWander(this, 0.4D, 20));
		//tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		//tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, 0, true, true, null));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
    public boolean isNotColliding() {
        return !getEntityWorld().containsAnyLiquid(getEntityBoundingBox()) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this);
    }

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevStandingAngle + (standingAngle - prevStandingAngle) * partialTicks;
    }

	@Override
	public void onLivingUpdate() {
		//Test Scream remove once testing is over
		if(getEntityWorld().getTotalWorldTime()%200 == 0)
			setScreamTimer(0);

		if (getEntityWorld().isRemote) {
			prevStandingAngle = standingAngle;

			if (isAmbushSpawn() && standingAngle <= 0.1F)
				standingAngle += 0.01F;
			if (isAmbushSpawn() && standingAngle > 0.1F && standingAngle <= 1F)
				standingAngle += 0.1F;

			if (isAmbushSpawn() && standingAngle > 1F)
				standingAngle = 1F;
		}

		prevScreamTimer = getScreamTimer();
		if (!getEntityWorld().isRemote) {
			if (getScreamTimer() == 0) {
				setIsScreaming(true);
				setScreamTimer(1);
			//	this.spawnEffect(getPosition().up(), 5); // TODO Particles and stuffs go here
			}

			if (getScreamTimer() > 0 && getScreamTimer() <= SCREAMING_TIMER_MAX) {
				setScreamTimer(getScreamTimer() + 1);
			}

			if (getScreamTimer() >= SCREAMING_TIMER_MAX)
				setIsScreaming(false);
			else
				setIsScreaming(true);
		}
		
		if(this.world.isRemote && this.isScreaming()) {
			this.spawnScreamParticles();
		}
		
		super.onLivingUpdate();
	}
	
	@SideOnly(Side.CLIENT)
	protected void spawnScreamParticles() {
		Vec3d look = this.getLookVec();
		float speed = 0.6f;
		Particle particle = BLParticles.SONIC_SCREAM.create(this.world, this.posX, this.posY + 1, this.posZ, 
				ParticleArgs.get().withMotion(look.x * speed, look.y * speed, look.z * speed).withScale(10).withData(30, MathHelper.floor(this.ticksExisted * 3.3f))
				.withColor(1.0f, 0.9f, 0.8f, 1.0f));
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING, particle);
	}
	
	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || isScreaming() || true;
	}

	protected void spawnEffect(BlockPos target, int delay) {
		TheBetweenlands.networkWrapper.sendToAll(new MessageSoundRipple(target, delay));
	}

	public float getScreamingProgress(float delta) {
		return 1.0F / SCREAMING_TIMER_MAX * (prevScreamTimer + (screamTimer - prevScreamTimer) * delta);
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(isScreaming()) {
			double dist = getDistance(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getScreamingProgress(partialTicks) * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}

	static class AIBarrisheeAttack extends EntityAIAttackMelee {

		public AIBarrisheeAttack(EntityBarrishee barrishee) {
			super(barrishee, 0.4D, false);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}


}