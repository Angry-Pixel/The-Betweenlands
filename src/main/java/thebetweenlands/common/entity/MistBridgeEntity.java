package thebetweenlands.common.entity;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.misc.MistBridgeBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class MistBridgeEntity extends Entity implements BLEntity {
	private static final EntityDataAccessor<Boolean> START_EXTENTION = SynchedEntityData.defineId(MistBridgeEntity.class, EntityDataSerializers.BOOLEAN);
	public boolean startRetraction;
	private int matchDistance = 0;

	public MistBridgeEntity (EntityType<? extends Entity> type, Level level) {
		super(type, level);
		setNoGravity(true);
		this.noPhysics = true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(START_EXTENTION, true);
	}

	public boolean getStartExtention() {
		return getEntityData().get(START_EXTENTION);
	}

	public void setStartExtention(boolean extending) {
		getEntityData().set(START_EXTENTION, extending);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide()) {
			if (getStartExtention() && level().getGameTime() % 1 == 0) {
				List<BlockPos> pos = matchDistance(level(), matchDistance);
				if (!pos.isEmpty()) {
					for (int index = 0; index < pos.size(); index++)
						level().setBlock(pos.get(index), getTempBlock(), 2);
				}

				if (matchDistance < 16)
					matchDistance++;

				if (matchDistance >= 16) {
					setStartExtention(false);
					matchDistance = 0;
				}
			}

			if(startRetraction) {
				List<BlockPos> pos = matchDistance(level(), matchDistance);
				if (!pos.isEmpty()) {
					for (int index = 0; index < pos.size(); index++)
						restoreBlocks(pos.get(index));
				}

				if (matchDistance < 16)
					matchDistance++;

				if (matchDistance >= 16)
					remove(RemovalReason.DISCARDED);
			}	

			if (tickCount > 1) {
				if(isMist()) {
					BlockState state = level().getBlockState(blockPosition());
					if(state.is(BlockRegistry.MIST_BRIDGE) || state.is(BlockRegistry.MIST_BRIDGE) && !state.getValue(MistBridgeBlock.SOLID) || tickCount >= 200) {
						if(!startRetraction)
							level().playSound(null, blockPosition(), SoundRegistry.MIST_STAFF_VANISH.get(), SoundSource.BLOCKS, 1F, 1.0F);
						startRetraction = true;
					}
				}
				if(!isMist()) {
					if(tickCount >= 200) {
						if(!startRetraction)
							level().playSound(null, blockPosition(), SoundRegistry.MIST_STAFF_VANISH.get(), SoundSource.BLOCKS, 1F, 1.0F);
						startRetraction = true;
					}
				}
			}
		}
	}

	@Override
	public void updateInWaterStateAndDoWaterCurrentPushing() {
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public void push(Entity entity) {
	}

	@Override
	public void push(double x, double y, double z) {
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isRemoved();
	}

	@Override
	 public boolean isInvulnerable() {
		return true;
	}

    public boolean isMist() {
    	CompoundTag entityNbt = getPersistentData();
    	if(!entityNbt.getBoolean("isMist"))
    		return false;
    	return true;
    }

    public BlockState getTempBlock() {
    	if (!isMist())
    		 return BlockRegistry.SHADOW_WALKER.get().defaultBlockState();
        return BlockRegistry.MIST_BRIDGE.get().defaultBlockState();
    }

	public void setBlockList(List<Integer> blockDistance, List<BlockPos> convertPos, boolean isMist) {
		ListTag distanceList = new ListTag();
		ListTag posList = new ListTag();
		ListTag stateList = new ListTag();

		CompoundTag entityNbt = getPersistentData();
		for (int blockCount = 0; blockCount < convertPos.size(); blockCount++) {
			CompoundTag posContainer = new CompoundTag();
			posContainer.put("pos", NbtUtils.writeBlockPos(convertPos.get(blockCount)));
			posList.add(posContainer);
			
			BlockState state = level().getBlockState(convertPos.get(blockCount));
			stateList.add(NbtUtils.writeBlockState(state));

			int distance = blockDistance.get(blockCount);
			CompoundTag tagDistance = new CompoundTag();
			tagDistance.putInt("distance", distance);
			distanceList.add(tagDistance);

		}

		if (!posList.isEmpty() && !stateList.isEmpty()) {
			entityNbt.put("originPos", posList);
			entityNbt.put("tempBlockTypes", stateList);
			entityNbt.put("distance", distanceList);
			entityNbt.putBoolean("isMist", isMist);
		}
	}

	public List<BlockPos> matchDistance(Level level, int distanceIn) {
		CompoundTag entityNbt = getPersistentData();
		
		List<BlockPos> posList = new ArrayList<BlockPos>(); 
		ListTag posTagList = entityNbt.getList("originPos", Tag.TAG_COMPOUND);
		ListTag distanceTagList = entityNbt.getList("distance", Tag.TAG_COMPOUND);
		for (int indexCount = 0; indexCount < distanceTagList.size(); ++indexCount) {
			if(distanceTagList.getCompound(indexCount).getInt("distance") == distanceIn) {
				CompoundTag posContainer = posTagList.getCompound(indexCount);
				posList.add(NbtUtils.readBlockPos(posContainer, "pos").orElse(BlockPos.ZERO));
			}
		}
		return posList;
	}

	private void restoreBlocks(BlockPos blockPos) {
		CompoundTag entityNbt = getPersistentData();
		ListTag  posTagList = entityNbt.getList("originPos", Tag.TAG_COMPOUND);
		ListTag  stateTagList = entityNbt.getList("tempBlockTypes", Tag.TAG_COMPOUND);
		for (int indexCount = 0; indexCount < posTagList.size(); ++indexCount) {
			CompoundTag posContainer = posTagList.getCompound(indexCount);
			BlockPos origin = NbtUtils.readBlockPos(posContainer, "pos").orElse(BlockPos.ZERO);
			if (origin.getX() == blockPos.getX() && origin.getY() == blockPos.getY() && origin.getZ() == blockPos.getZ()) {
				BlockState state = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), stateTagList.getCompound(indexCount));
				level().setBlock(origin, state, 3);
			}
		}
	}
/*
	@Override
	public void onKillCommand() {
		this.setDead();
	}
*/
	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {	
	}
}
