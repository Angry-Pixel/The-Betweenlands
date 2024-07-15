package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class AspectVialBlockEntity extends BlockEntity {

	public static final float MAX_AMOUNT = Amounts.VIAL;

	@Nullable
	private Aspect aspect = null;

	public AspectVialBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.ASPECT_VIAL.get(), pos, blockState);
	}

	/**
	 * Tries to add an amount and returns the added amount
	 * @param amount
	 * @return
	 */
	public int addAmount(int amount) {
		int canAdd = (int) (MAX_AMOUNT - this.aspect.amount);
		int added = 0;
		if(canAdd > 0) {
			added = Math.min(canAdd, amount);
			this.aspect = new Aspect(this.aspect.type, this.aspect.amount + added);
		}
		markDirty();
		return added;
	}

	/**
	 * Tries to remove an amount and returns the removed amount
	 * @param amount
	 * @return
	 */
	public int removeAmount(int amount) {
		int removed = Math.min(this.aspect.amount, amount);
		if(removed < this.aspect.amount) {
			this.aspect = new Aspect(this.aspect.type, this.aspect.amount - removed);
		} else {
			this.aspect = null;
		}
		markDirty();
		return removed;
	}

	@Override
	public void setChanged() {
		final BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 2);
		super.setChanged();
	}

	@Nullable
	public Aspect getAspect() {
		return this.aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
		this.setChanged();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if(this.aspect != null)
			this.aspect.writeToNBT(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("aspect")) {
			this.aspect = Aspect.readFromNBT(nbt);
		} else {
			this.aspect = null;
		}
	}
}
