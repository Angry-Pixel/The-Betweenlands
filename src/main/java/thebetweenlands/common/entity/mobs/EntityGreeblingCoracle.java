package thebetweenlands.common.entity.mobs;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.entity.movement.PathNavigateAboveWater;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EntityGreeblingCoracle extends EntityCreature implements IEntityBL {
	protected static final byte EVENT_DISAPPEAR = 41;
	protected static final byte EVENT_SPOUT = 42;
	private static final DataParameter<Integer> SINKING_TICKS = EntityDataManager.createKey(EntityGreeblingCoracle.class, DataSerializers.VARINT);
	AIWaterWander waterWander;
	EntityAILookIdle lookIdle;
	boolean hasSetAIForEmptyBoat = false;

    public EntityGreeblingCoracle(World worldIn) {
        super(worldIn);
        setSize(1.0F, 1.0F);
		setPathPriority(PathNodeType.WALKABLE, -100.0F);
		setPathPriority(PathNodeType.BLOCKED, -100.0F);
		setPathPriority(PathNodeType.LAVA, -100.0F);
		setPathPriority(PathNodeType.WATER, 16.0F);
		stepHeight = 0;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SINKING_TICKS, 0);
    }

    @Override
    protected void initEntityAI() {
    	waterWander = new EntityGreeblingCoracle.AIWaterWander(this, 0.5D, 10);
    	lookIdle = new EntityAILookIdle(this);
    	tasks.addTask(1, waterWander);
        tasks.addTask(3, lookIdle);
    }

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}

	@Override
    protected PathNavigate createNavigator(World world) {
		return new PathNavigateAboveWater(this, world); 
	}

	@Override
	public boolean getCanSpawnHere() {
		int y = MathHelper.floor(getEntityBoundingBox().minY);
		if(y <= WorldProviderBetweenlands.LAYER_HEIGHT)
			return getEntityWorld().checkNoEntityCollision(getEntityBoundingBox()) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().isMaterialInBB(getEntityBoundingBox(), Material.WATER);
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (getEntityWorld().containsAnyLiquid(getEntityBoundingBox()) && getSinkingTicks() <= 200)
			motionY += 0.06D;
		
		if (getSinkingTicks() > 0) {
			motionX *= 0.975D;
			motionZ *= 0.975D;
		}

		if (isGreeblingAboveWater() && getSinkingTicks() <= 200) {
			if (motionY < 0.0D)
				motionY = 0.0D;
			fallDistance = 0.0F;
		}
		else {
			motionY = -0.0075D;
		}

		limbSwing += 0.5D;
		if (posX != lastTickPosX && posZ != lastTickPosZ)
			limbSwingAmount += 0.5D;

		if(!this.world.isRemote) {
			if (getSinkingTicks() > 0 && getSinkingTicks() < 400)
				setSinkingTicks(getSinkingTicks() + 1);

			if (getSinkingTicks() == 5)
				this.world.setEntityState(this, EVENT_DISAPPEAR);
			
			if (getSinkingTicks() >= 200 && getSinkingTicks() <= 400)
				this.world.setEntityState(this, EVENT_SPOUT);

			if (getSinkingTicks() > 0 && !hasSetAIForEmptyBoat) {
				tasks.removeTask(waterWander);
				tasks.removeTask(lookIdle);
				if(getNavigator().getPath() != null) {
					getNavigator().clearPath();
				}
				hasSetAIForEmptyBoat = true;
			}

			if (getSinkingTicks() >= 400)
				setDead();

			List<EntityPlayer> nearPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(2.5, 2.5, 2.5), e -> !e.capabilities.isCreativeMode && !e.isInvisible());
			if (getSinkingTicks() == 0 && !nearPlayers.isEmpty()) {
				setSinkingTicks(getSinkingTicks() + 1);
				this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.GREEBLING_VANISH, SoundCategory.NEUTRAL, 1, 1);
			}
		}
	}

	public boolean isGreeblingAboveWater() {
		AxisAlignedBB floatingBox = new AxisAlignedBB(getEntityBoundingBox().minX + 0.25D, getEntityBoundingBox().minY, getEntityBoundingBox().minZ + 0.25D, getEntityBoundingBox().maxX - 0.25D, getEntityBoundingBox().minY + 0.0625D, getEntityBoundingBox().maxZ - 0.25D);
		return getEntityWorld().containsAnyLiquid(floatingBox);
	}

    public void setSinkingTicks(int count) {
        dataManager.set(SINKING_TICKS, count);
    }

    public int getSinkingTicks() {
        return dataManager.get(SINKING_TICKS);
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("sinkingTicks", getSinkingTicks());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setSinkingTicks(nbt.getInteger("sinkingTicks"));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_DISAPPEAR)
			doLeafEffects();
		if(id == EVENT_SPOUT)
			doSpoutEffects();
	}

	private void doSpoutEffects() {
		if(world.isRemote) {
			int count = getSinkingTicks() <= 240 ? 40 : 10;
			float x = (float) (posX);
			float y = (float) (posY + 0.25F);
			float z = (float) (posZ);
			while (count-- > 0) {
				float dx = world.rand.nextFloat() * 0.25F - 0.1255f;
				float dy = world.rand.nextFloat() * 0.25F - 0.1255f;
				float dz = world.rand.nextFloat() * 0.25F - 0.1255f;
				float mag = 0.08F + world.rand.nextFloat() * 0.07F;
				if(getSinkingTicks() <= 240)
					BLParticles.SPLASH.spawn(world, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
				else if(getSinkingTicks() > 240 && getSinkingTicks() <= 400 && getSinkingTicks()%5 == 0)
					BLParticles.BUBBLE_PURIFIER.spawn(world, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	private void doLeafEffects() {
		if(world.isRemote) {
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY + 1.3F);
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = world.rand.nextFloat() * 1 - 0.5f;
				float dy = world.rand.nextFloat() * 1f - 0.1F;
				float dz = world.rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + world.rand.nextFloat() * 0.07F;
				BLParticles.WEEDWOOD_LEAF.spawn(world, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	public class AIWaterWander extends EntityAIWander {

		private final EntityGreeblingCoracle coracle;

		public AIWaterWander(EntityGreeblingCoracle coracleIn, double speedIn, int chance) {
			super(coracleIn, speedIn, chance);
			setMutexBits(1);
			this.coracle = coracleIn;
		}

		@Nullable
		@Override
		protected Vec3d getPosition() {
			return RandomPositionGenerator.findRandomTarget(coracle, 16, 0);
		}
	}
}
