package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.farming.DecayableCropBlock;
import thebetweenlands.common.block.farming.DugSoilBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class DugSoilBlockEntity extends SyncedBlockEntity {

	private int compost = 0;
	private int decay = 0;
	private int purifiedHarvests = 0;

	public DugSoilBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DUG_SOIL.get(), pos, state);
	}

	public void copy(Level level, BlockPos pos, DugSoilBlockEntity other) {
		this.setDecay(level, pos, other.getDecay());
		this.setCompost(level, pos, other.getCompost());
	}

	public void setPurifiedHarvests(Level level, BlockPos pos, int harvests) {
		BlockState blockState = level.getBlockState(pos);
		DugSoilBlock soil = ((DugSoilBlock) blockState.getBlock());
		if (soil.isPurified(level, pos, blockState)) {
			if (harvests < 0) {
				harvests = 0;
			}
			int maxHarvests = soil.getPurifiedHarvests(level, pos, blockState);
			this.purifiedHarvests = harvests;
			if (this.purifiedHarvests >= maxHarvests) {
				level.setBlockAndUpdate(pos, soil.getUnpurifiedDugSoil(level, pos, blockState));
				DugSoilBlock.copy(level, pos, this);
			}
		} else {
			this.purifiedHarvests = 0;
		}
		this.setChanged();
	}

	public int getPurifiedHarvests() {
		return this.purifiedHarvests;
	}

	public void setCompost(Level level, BlockPos pos, int compost) {
		if (compost < 0) {
			compost = 0;
		}
		boolean wasComposted = this.isComposted();
		this.compost = compost;
		if (wasComposted != this.isComposted()) {
			BlockState blockState = level.getBlockState(pos);
			if (!this.isFullyDecayed()) {
				level.setBlockAndUpdate(pos, blockState.setValue(DugSoilBlock.DECAYED, this.isFullyDecayed()).setValue(DugSoilBlock.COMPOSTED, this.isComposted()));
			} else {
				level.setBlockAndUpdate(pos, blockState.setValue(DugSoilBlock.DECAYED, this.isFullyDecayed()).setValue(DugSoilBlock.COMPOSTED, false));
			}
		} else {
			BlockState state = level.getBlockState(pos);
			level.sendBlockUpdated(pos, state, state, 3);
		}
		this.setChanged();
	}

	public int getCompost() {
		return this.compost;
	}

	public boolean isComposted() {
		return this.compost > 0;
	}

	public void setDecay(Level level, BlockPos pos, int decay) {
		if (decay < 0) {
			decay = 0;
		}
		boolean wasDecayed = this.isFullyDecayed();
		this.decay = Math.min(20, decay);
		if (wasDecayed != this.isFullyDecayed()) {
			BlockState blockState = level.getBlockState(pos);
			if (this.isFullyDecayed()) {
				blockState = blockState.setValue(DugSoilBlock.DECAYED, this.isFullyDecayed()).setValue(DugSoilBlock.COMPOSTED, false);
			} else {
				blockState = blockState.setValue(DugSoilBlock.DECAYED, false).setValue(DugSoilBlock.COMPOSTED, this.isComposted());
			}
			level.setBlockAndUpdate(pos, blockState);

			BlockState blockUp = level.getBlockState(pos.above());
			if (blockUp.getBlock() instanceof DecayableCropBlock crop) {
				for (int i = 0; i < crop.getMaxHeight(); i++) {
					BlockPos checkPos = pos.above(i + 1);
					BlockState cropBlockState = level.getBlockState(checkPos);
					if (cropBlockState.getBlock() instanceof DecayableCropBlock) {
						if (this.isFullyDecayed()) {
							level.setBlockAndUpdate(checkPos, cropBlockState.setValue(DecayableCropBlock.DECAYED, true));
						} else {
							level.setBlockAndUpdate(checkPos, cropBlockState.setValue(DecayableCropBlock.DECAYED, false));
						}
					} else {
						break;
					}
				}
			}
		} else {
			BlockState state = level.getBlockState(pos);
			level.sendBlockUpdated(pos, state, state, 3);
		}
		this.setChanged();
	}

	public int getDecay() {
		return this.decay;
	}

	public boolean isFullyDecayed() {
		return this.decay >= 20;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("compost", this.compost);
		tag.putInt("decay", this.decay);
		tag.putInt("purified_harvests", this.purifiedHarvests);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.decay = tag.getInt("decay");
		this.compost = tag.getInt("compost");
		this.purifiedHarvests = tag.getInt("purified_harvests");
	}
}
