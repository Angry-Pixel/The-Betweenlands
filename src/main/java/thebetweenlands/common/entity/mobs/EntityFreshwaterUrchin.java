package thebetweenlands.common.entity.mobs;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleUrchinSpike;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityFreshwaterUrchin extends EntityProximitySpawner {
	private static final DataParameter<Integer> SPIKE_COOLDOWN = EntityDataManager.createKey(EntityFreshwaterUrchin.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SPIKE_BOX_SIZE = EntityDataManager.createKey(EntityFreshwaterUrchin.class, DataSerializers.VARINT);
	private boolean shootSpikes;
	public int MAX_SPIKE_TIMER = 10;
	public static final byte EVENT_ATTACK = 66;

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

    @Override
    public boolean getCanSpawnHere() {
        return world.getDifficulty() != EnumDifficulty.PEACEFUL && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }
 
    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

	@Override
    public boolean isNotColliding() {
		 return getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
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
				if (entity != null && (entity instanceof EntityFreshwaterUrchin) || entity instanceof EntityAnadia || entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator() || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
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
		getEntityWorld().playSound(null, getPosition(), SoundRegistry.URCHIN_SHOOT, SoundCategory.NEUTRAL, 1F, 1.5F + (getEntityWorld().rand.nextFloat() - getEntityWorld().rand.nextFloat()) * 0.5F);
		setSpikeGrowTimer(0);
		shootSpikes = true;
		getEntityWorld().setEntityState(this, EVENT_ATTACK);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);               
		if (id == EVENT_ATTACK) {
			Vec3d frontCenter = new Vec3d(posX, posY, posZ);
			for (int i = 0; i < 64; i++) {
				Random rnd = world.rand;
				float rx = rnd.nextFloat() * 4.0F - 2.0F;
				float ry = rnd.nextFloat() * 4.0F - 2.0F;
				float rz = rnd.nextFloat() * 4.0F - 2.0F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				ParticleUrchinSpike particle = (ParticleUrchinSpike) BLParticles.URCHIN_SPIKE.spawn(world, frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleArgs.get().withMotion(vec.x * 0.175F, vec.y * 0.15F + 0.35F, vec.z * 0.175F).withScale(0.2F));
				particle.setUseSound(this.rand.nextInt(15) == 0);
			}
		}
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
