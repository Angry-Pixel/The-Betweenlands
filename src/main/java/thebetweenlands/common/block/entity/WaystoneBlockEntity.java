package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class WaystoneBlockEntity extends SyncedBlockEntity {

	private float rotation;

	public WaystoneBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.WAYSTONE.get(), pos, state);
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.setChanged();
	}

	public float getRotation() {
		return this.rotation;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putFloat("rotation", this.rotation);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.rotation = tag.getFloat("rotation");
	}
}
