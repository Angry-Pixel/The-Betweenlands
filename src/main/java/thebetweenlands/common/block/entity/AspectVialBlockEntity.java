package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.herblore.aspect.IAspectVial;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class AspectVialBlockEntity extends SyncedBlockEntity implements IAspectBlockEntity, IAspectVial {

	private final IAspectVial.VialType type;

	public static final float MAX_AMOUNT = Amounts.VIAL;

	@Nullable
	private Aspect aspect = null;

	public AspectVialBlockEntity(BlockPos pos, BlockState blockState, IAspectVial.VialType type) {
		super(BlockEntityRegistry.ASPECT_VIAL.get(), pos, blockState);
		this.type = type;
	}

	public AspectVialBlockEntity(BlockPos pos, BlockState blockState) {
		this(pos, blockState, IAspectVial.VialType.GREEN);
	}

	/**
	 * Tries to add an amount and returns the added amount
	 *
	 * @param amount
	 * @return
	 */
	public int addAmount(int amount) {
		int canAdd = (int) (MAX_AMOUNT - this.aspect.amount());
		int added = 0;
		if (canAdd > 0) {
			added = Math.min(canAdd, amount);
			this.aspect = new Aspect(this.aspect.type(), this.aspect.amount() + added);
		}
		this.setChanged();
		return added;
	}

	/**
	 * Tries to remove an amount and returns the removed amount
	 *
	 * @param amount
	 * @return
	 */
	public int removeAmount(int amount) {
		int removed = Math.min(this.aspect.amount(), amount);
		if (removed < this.aspect.amount()) {
			this.aspect = new Aspect(this.aspect.type(), this.aspect.amount() - removed);
		} else {
			this.aspect = null;
		}
		this.setChanged();
		return removed;
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
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (this.aspect != null)
			this.aspect.writeToNBT(tag, registries);
		else 
			tag.putInt("dummy", 0); 
		/* 
		ok so like. this is very, and i mean VERY scuffed. however, im like 99% sure that when aspect is null and nothing is written to
		the NBT tag, a packet isnt sent to update the client, which i guess makes sense to not send as many packets to the client
		but this raises a big issue, that being the renderer, which depends on being updated (even if its updating it to null), so that it
		doesnt render a very tiny sliver of the aspect. adding this dummy key fixes it, as it forces a packet send. 
		ok rant over
		 */
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("aspect")) {
			this.aspect = Aspect.readFromNBT(tag, registries);
		} else {
			this.aspect = null;
		}
	}

	@Override
	public VialType type() {
		return type;
	}
}
