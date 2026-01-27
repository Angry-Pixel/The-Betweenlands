package thebetweenlands.common.block.entity;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class DungeonDoorCombinationBlockEntity extends SyncedBlockEntity {

	public int topCode = 0;
	public int midCode = 0;
	public int bottomCode = 0;
	public int renderTicks = 0; // Only used for the rune glow animation

	public DungeonDoorCombinationBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DUNGEON_DOOR_COMBINATION.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DungeonDoorCombinationBlockEntity entity) {
		entity.renderTicks++;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		
		// Random render offset for the rune glow
		this.renderTicks = new Random(this.getBlockPos().asLong()).nextInt(12000);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("top_code", this.topCode);
		tag.putInt("mid_code", this.midCode);
		tag.putInt("bottom_code", this.bottomCode);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.topCode = tag.getInt("top_code");
		this.midCode = tag.getInt("mid_code");
		this.bottomCode = tag.getInt("bottom_code");
	}

	public void cycleTopState() {
		this.topCode++;
		if (this.topCode > 7)
			this.topCode = 0;
		this.setChanged();
	}

	public void cycleMidState() {
		this.midCode++;
		if (this.midCode > 7)
			this.midCode = 0;
		this.setChanged();
	}

	public void cycleBottomState() {
		this.bottomCode++;
		if (this.bottomCode > 7)
			this.bottomCode = 0;
		this.setChanged();
	}
}
