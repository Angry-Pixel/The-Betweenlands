package thebetweenlands.common.entity;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.misc.BlockMistBridge;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityMistBridge extends EntityCreature implements IEntityBL {
	public boolean startExtention;
	public boolean startRetraction;
	private int matchDistance = 0;

	public EntityMistBridge (World world) {
		super(world);
		setSize(0.0F, 0.0F);
		setNoGravity(true);
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

		if (!world.isRemote) {
			if (startExtention && world.getTotalWorldTime() % 1 == 0) {
				List<BlockPos> pos = matchDistance(world, matchDistance);
				if (!pos.isEmpty()) {
					for (int index = 0; index < pos.size(); index++)
						world.setBlockState(pos.get(index), getTempBlock(), 2);
				}

				if (matchDistance < 16)
					matchDistance++;

				if (matchDistance >= 16) {
					startExtention = false;
					matchDistance = 0;
				}
	
			}

			if(startRetraction) {
				List<BlockPos> pos = matchDistance(world, matchDistance);
				if (!pos.isEmpty()) {
					for (int index = 0; index < pos.size(); index++)
						restoreBlocks(pos.get(index));
				}

				if (matchDistance < 16)
					matchDistance++;

				if (matchDistance >= 16)
					setDead();
			}	

			if (ticksExisted > 1) {
				if(isMist()) {
					if(world.getBlockState(getPosition()).getBlock() != BlockRegistry.MIST_BRIDGE || world.getBlockState(getPosition()).getBlock() == BlockRegistry.MIST_BRIDGE && !world.getBlockState(getPosition()).getValue(BlockMistBridge.SOLID) || ticksExisted >= 200) {
						if(!startRetraction)
							world.playSound((EntityPlayer)null, getPosition(), SoundRegistry.MIST_STAFF_VANISH, SoundCategory.BLOCKS, 1F, 1.0F);
						startRetraction = true;
					}
				}
				if(!isMist()) {
					if(world.getBlockState(getPosition()).getBlock() != BlockRegistry.SHADOW_WALKER || ticksExisted >= 200) {
						if(!startRetraction)
							world.playSound((EntityPlayer)null, getPosition(), SoundRegistry.MIST_STAFF_VANISH, SoundCategory.BLOCKS, 1F, 1.0F);
						startRetraction = true;
					}
				}
			}
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

    public boolean isMist() {
    	NBTTagCompound entityNbt = getEntityData();
    	if(!entityNbt.getBoolean("isMist"))
    		return false;
    	return true;
    }

    public IBlockState getTempBlock() {
    	if (!isMist())
    		 return BlockRegistry.SHADOW_WALKER.getDefaultState();
        return BlockRegistry.MIST_BRIDGE.getDefaultState();
    }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
			startExtention = true;
		}
		return livingdata;
	}

	public void setBlockList(List<Integer> blockDistance, List<BlockPos> convertPos, boolean isMist) {
		NBTTagList distanceList = new NBTTagList();
		NBTTagList posList = new NBTTagList();
		NBTTagList stateList = new NBTTagList();
		NBTTagCompound entityNbt = getEntityData();
		for (int blockCount = 0; blockCount < convertPos.size(); blockCount++) {
			
			NBTTagCompound tagPos = NBTUtil.createPosTag(convertPos.get(blockCount));
			posList.appendTag(tagPos);
			
			IBlockState state = world.getBlockState(convertPos.get(blockCount));
			NBTTagCompound tagState = new NBTTagCompound();
			NBTUtil.writeBlockState(tagState, state);
			stateList.appendTag(tagState);
			
			int distance = blockDistance.get(blockCount);
			NBTTagCompound tagDistance = new NBTTagCompound();
			tagDistance.setInteger("distance", distance);
			distanceList.appendTag(tagDistance);
		}

		if (!posList.isEmpty() && !stateList.isEmpty()) {
			entityNbt.setTag("originPos", posList);
			entityNbt.setTag("tempBlockTypes", stateList);
			entityNbt.setTag("distance", distanceList);
			entityNbt.setBoolean("isMist", isMist);
		}
		writeEntityToNBT(entityNbt);
	}

	public List<BlockPos> matchDistance(World world, int distanceIn) {
		NBTTagCompound entityNbt = getEntityData();
		List<BlockPos> posList = new ArrayList<BlockPos>(); 
		NBTTagList posTagList = entityNbt.getTagList("originPos", Constants.NBT.TAG_COMPOUND);
		NBTTagList distanceTagList = entityNbt.getTagList("distance", Constants.NBT.TAG_COMPOUND);
		for (int indexCount = 0; indexCount < distanceTagList.tagCount(); ++indexCount) {
			if(distanceTagList.getCompoundTagAt(indexCount).getInteger("distance") == distanceIn)
				posList.add(NBTUtil.getPosFromTag(posTagList.getCompoundTagAt(indexCount)));
		}
		return posList;
	}

	private void restoreBlocks(BlockPos blockPos) {
		NBTTagCompound entityNbt = getEntityData();
		NBTTagList posTagList = entityNbt.getTagList("originPos", Constants.NBT.TAG_COMPOUND);
		NBTTagList stateTagList = entityNbt.getTagList("tempBlockTypes", Constants.NBT.TAG_COMPOUND);
		for (int indexCount = 0; indexCount < posTagList.tagCount(); ++indexCount) {
			BlockPos origin = NBTUtil.getPosFromTag(posTagList.getCompoundTagAt(indexCount));
			if (origin.getX() == blockPos.getX() && origin.getY() == blockPos.getY() && origin.getZ() == blockPos.getZ()) {
				IBlockState state = NBTUtil.readBlockState(stateTagList.getCompoundTagAt(indexCount));
				world.setBlockState(origin, state, 3);
			}
		}
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
