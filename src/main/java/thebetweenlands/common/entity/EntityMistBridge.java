package thebetweenlands.common.entity;


import javax.annotation.Nullable;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityMistBridge extends EntityCreature implements IEntityBL {
	private static final DataParameter<Integer> DELAY = EntityDataManager.<Integer>createKey(EntityMistBridge.class, DataSerializers.VARINT);

	@SideOnly(Side.CLIENT)
	private ParticleBatch particleBatch;

	public EntityMistBridge (World world) {
		super(world);
		setSize(0.9F, 0.9F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		getDataManager().register(DELAY, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	@Override
	 public void onUpdate() {
		super.onUpdate();

		if (this.world.isRemote) {
			spawnCloudParticle();
		}

		if(!world.isRemote) {
			if(ticksExisted >= getDelay() && world.getBlockState(getPosition()).getBlock() != BlockRegistry.MIST_BRIDGE)
				world.setBlockState(getPosition(), BlockRegistry.MIST_BRIDGE.getDefaultState(), 3);
			if (world.isAirBlock(getPosition()) || ticksExisted - getDelay() >= 200)
				setDead();
		}
	}

	@Override
	public boolean isNotColliding() {
		return true;
	}

	@Override
    public EnumPushReaction getPushReaction() {
        return EnumPushReaction.IGNORE;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

    @Override
    public boolean getCanSpawnHere() {
        return true;
    }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
			getOriginBlocks(getEntityWorld(), getPosition());
		}
		return livingdata;
	}

	@Override
    public void setDead() {
		if(!getEntityWorld().isRemote) {
			if(getEntityData().hasKey("tempBlockTypes"))
				loadOriginBlocks(getEntityWorld(), getEntityData());
		}
        super.setDead();
    }

	private void getOriginBlocks(World world, BlockPos pos) {
		NBTTagCompound entityNbt = getEntityData();
		IBlockState state = world.getBlockState(pos);
		NBTTagCompound tag = new NBTTagCompound();
		NBTUtil.writeBlockState(tag, state);
		entityNbt.setTag("tempBlockTypes", tag);
		entityNbt.setTag("originPos", NBTUtil.createPosTag(pos));
		writeEntityToNBT(entityNbt);
	}

	public void loadOriginBlocks(World world, NBTTagCompound tag) {
		NBTTagCompound entityNbt = getEntityData();
		BlockPos origin = NBTUtil.getPosFromTag(entityNbt.getCompoundTag("originPos"));
		IBlockState state = NBTUtil.readBlockState((NBTTagCompound) entityNbt.getTag("tempBlockTypes"));
		world.setBlockState(origin, state, 3);
		getEntityWorld().playSound((EntityPlayer)null, origin, SoundRegistry.GAS_CLOUD_DEATH, SoundCategory.BLOCKS, 1F, 1.0F);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
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
				this.setDead();
			}
		}
		return false;
	}

	public void setDelay(int distance) {
		dataManager.set(DELAY, distance);
	}

	public int getDelay() {
		return dataManager.get(DELAY);
	}

	@SideOnly(Side.CLIENT)
	private void spawnCloudParticle() {
		double x = this.posX + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double y = this.posY + 0.5D;
		double z = this.posZ + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double mx = (this.world.rand.nextFloat() - 0.5F) / 12.0F;
		double my = (this.world.rand.nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (this.world.rand.nextFloat() - 0.5F) / 12.0F;
		int[] color = {255, 255, 255, 255};

		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(8f));
		
	//	BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
		
		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(5f));

		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
		
}
