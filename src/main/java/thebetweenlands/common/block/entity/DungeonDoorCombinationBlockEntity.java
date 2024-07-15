package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class DungeonDoorCombinationBlockEntity extends BlockEntity {

	private int topCode = 0;
	private int midCode = 0;
	private int bottomCode = 0;
	public int renderTicks = 0;

	public DungeonDoorCombinationBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DUNGEON_DOOR_COMBINATION.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DungeonDoorCombinationBlockEntity entity) {
		entity.renderTicks++;
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

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		this.loadAdditional(packet.getTag(), registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		this.saveAdditional(tag, registries);
		return tag;
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
