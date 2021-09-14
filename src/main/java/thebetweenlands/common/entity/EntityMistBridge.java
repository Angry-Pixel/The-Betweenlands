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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityMistBridge extends EntityCreature implements IEntityBL {

	public EntityMistBridge (World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
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

		if(!world.isRemote)
			if (world.isAirBlock(getPosition()) || ticksExisted >= 200)
				setDead();
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
			IBlockState state = world.getBlockState(getPosition());
			world.setBlockState(getPosition(), BlockRegistry.MIST_BRIDGE.getDefaultState(), 3);

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

}
