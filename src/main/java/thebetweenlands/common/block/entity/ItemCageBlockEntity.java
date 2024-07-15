package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCageBlockEntity extends BlockEntity {

	public byte type; // type will be used for each sword part rendering
	public boolean canBreak;

	public ItemCageBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.ITEM_CAGE.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, ItemCageBlockEntity entity) {
		if (!level.isClientSide()) {
			if (entity.isBlockOccupied(level, pos, state)) {
				if (!entity.canBreak)
					entity.setCanBeBroken(level, pos, state, true);
			} else {
				if (entity.canBreak)
					entity.setCanBeBroken(level, pos, state, false);
			}
		}
	}

	public void setCanBeBroken(Level level, BlockPos pos, BlockState state, boolean isBreakable) {
		this.canBreak = isBreakable;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public void setType(Level level, BlockPos pos, BlockState state, byte blockType) {
		this.type = blockType;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	protected boolean isBlockOccupied(Level level, BlockPos pos, BlockState state) {
		List<Player> list = level.getEntitiesOfClass(Player.class, new AABB(pos.getX() + 0.25D, pos.getY() - 3D, pos.getZ() + 0.25D, pos.getX() + 0.75D, pos.getY(), pos.getZ() + 0.75D));
		return !list.isEmpty();
	}

//	@Nullable
//	public EnergySword isSwordEnergyBelow(LevelAccessor level, BlockPos pos, BlockState state) {
//		List<EnergySword> list = level.getEntitiesOfClass(EnergySword.class, new AABB(pos.getX() - 9D, pos.getY() - 2D, pos.getZ() - 9D, pos.getX() + 10D, pos.getY() + 3D, pos.getZ() + 10D));
//		if (!list.isEmpty()) {
//			return list.getFirst();
//		}
//		return null;
//	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putByte("type", this.type);
		tag.putBoolean("canBreak", this.canBreak);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.type = tag.getByte("type");
		this.canBreak = tag.getBoolean("canBreak");
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
}
