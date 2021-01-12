package thebetweenlands.common.entity;


import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTriggeredSludgeWallJet extends EntityProximitySpawner {
	private static final DataParameter<Integer> ANIMATION_TICKS_SYNC = EntityDataManager.createKey(EntityTriggeredSludgeWallJet.class, DataSerializers.VARINT);

	public int animationTicks = 0;
	public int animationTicksPrev = 0;

	public EntityTriggeredSludgeWallJet(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ANIMATION_TICKS_SYNC, 0);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setPosition(this.posX, this.posY + 1.0F, this.posZ);
		return super.onInitialSpawn(difficulty, livingdata);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!getEntityWorld().isRemote && this.ticksExisted % 40 == 0)
			checkArea();

		animationTicksPrev = animationTicks;
		animationTicks++;
		rotationYaw = renderYawOffset = MathHelper.wrapDegrees(animationTicks);

		if(!getEntityWorld().isRemote && this.ticksExisted % 20 == 0)
			this.dataManager.set(ANIMATION_TICKS_SYNC, this.animationTicks);

		if (animationTicks >= 360)
			animationTicks = animationTicksPrev = 0;

		if (getEntityWorld().isRemote) {
			this.spawnCloudParticles();
		}
	}
	
	@SideOnly(Side.CLIENT)
	protected void spawnCloudParticles() {
		if(this.rand.nextInt(4) == 0) {
			ParticleArgs<?> args = ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
				args.withColor(1F, 0.65F, 0.25F, 0.75F);
				args.withScale(1.5F + rand.nextFloat() * 6);
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SLUDGE_SWIRL.create(this.world, this.posX, this.posY, this.posZ, args));
		}
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if(getEntityWorld().isRemote && ANIMATION_TICKS_SYNC.equals(key))
			this.animationTicks = this.animationTicksPrev = this.dataManager.get(ANIMATION_TICKS_SYNC);
	}

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(targetEntity instanceof EntityPlayer) {
			float angle = (float) Math.toDegrees(Math.atan2(targetEntity.posX - this.posX, targetEntity.posZ - this.posZ));
			float angleDiff = Math.abs(MathHelper.wrapDegrees(MathHelper.wrapDegrees(angle) - MathHelper.wrapDegrees(-this.rotationYaw)));

			if(angleDiff < 55)
				((EntitySludgeWallJet) entitySpawned).setDead();
			else {
				((EntitySludgeWallJet) entitySpawned).setPosition(posX, posY + height * 0.5D , posZ);
				((EntitySludgeWallJet) entitySpawned).shoot(targetEntity.posX - posX, targetEntity.posY + targetEntity.height - posY, targetEntity.posZ - posZ, 0.5F, 0F);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity immediateSource = ((EntityDamageSource) source).getImmediateSource();
			if(immediateSource != null) {
				float angle = (float) Math.toDegrees(Math.atan2(immediateSource.posX - this.posX, immediateSource.posZ - this.posZ));
				float angleDiff = Math.abs(MathHelper.wrapDegrees(MathHelper.wrapDegrees(angle) - MathHelper.wrapDegrees(-this.rotationYaw)));

				if (angleDiff < 55) {
					if (!getEntityWorld().isRemote) {
						damageEntity(source, 5F);
					}
					this.playSound(SoundRegistry.SLUDGE_TURRET_DEATH, 1, 1);
					return true;
				} else {
					this.playSound(SoundRegistry.SLUDGE_TURRET_HURT, 1, 1);
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
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
    public boolean canBeCollidedWith() {
        return true;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		//motionX = 0;
		//motionY = 0;
		//motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 5F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	@Override
	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal());
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
		EntitySludgeWallJet entity = new EntitySludgeWallJet(getEntityWorld(), this);
		return entity;
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
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SLUDGE_TURRET_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundRegistry.SLUDGE_TURRET_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SLUDGE_TURRET_DEATH;
	}
}