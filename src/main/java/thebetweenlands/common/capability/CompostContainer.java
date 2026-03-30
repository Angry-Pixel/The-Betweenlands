package thebetweenlands.common.capability;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.datamap.item.CompostableItem;

public class CompostContainer extends CompostHandler implements ICompostStorageHandler {
	private final int maxCompost;
	private int availableCompost = 0;
	private int remainingCompost = 0;
	
	public CompostContainer(int size, int maxCompost) {
		super(size);
		
		this.maxCompost = maxCompost;
	}
	
	@Override
	public int tickComposting(boolean simulate) {
		final int compostProcessed = super.tickComposting(simulate);
		
		if(!simulate) {
			this.availableCompost += compostProcessed;
		}
		
		return compostProcessed;
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(Provider provider) {
		CompoundTag tag = super.serializeNBT(provider);
		tag.putInt("max_compost", this.maxCompost);
		tag.putInt("available_compost", this.availableCompost);
		return tag;
	}
	
	@Override
	public void deserializeNBT(Provider provider, CompoundTag tag) {
		this.availableCompost = tag.getInt("available_compost");
		super.deserializeNBT(provider, tag);
	}
	
	@Override
	public CompostableItem getCompostData(ItemStack stack) {
		CompostableItem compostData = super.getCompostData(stack);
		if(compostData == null) {
			return null;
		}
		
		int compostAmount = compostData.amount();
		int totalCompostAmount = this.getTotalCompost();
		
		if(totalCompostAmount + compostAmount <= this.maxCompost) {
			return compostData;
		} else {
			int clampedCompostAmount = this.maxCompost - totalCompostAmount;
			return new CompostableItem(clampedCompostAmount, compostData.time());
		}
	}

	@Override
	public void clearContent() {
		super.clearContent();
		
		this.remainingCompost = 0;
		this.availableCompost = 0;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		this.remainingCompost = ICompostHandler.calculateTotalCompost(this);
	}
	
	@Override
	protected void onCompostChanged(int slot, int flags) {
		super.onCompostChanged(slot, flags);
		
		if((flags & AMOUNT_CHANGED) != 0) {
			this.remainingCompost = ICompostHandler.calculateTotalCompost(this);
		}
	}

	@Override
	public int getRemainingCompost() {
		return this.remainingCompost;
	}

	@Override
	public int getAvailableCompost() {
		return this.availableCompost;
	}

	@Override
	public int removeCompost(int amount, boolean simulate) {
		final int extractedCompost = Math.min(amount, this.availableCompost);
		
		if(!simulate) {
			this.availableCompost -= extractedCompost;
		}
		
		return extractedCompost;
	}
}
