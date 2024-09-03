package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.FishingTackleBoxBlock;
import thebetweenlands.common.entities.Seat;
import thebetweenlands.common.inventory.FishingTackleBoxMenu;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class FishingTackleBoxBlockEntity extends BaseContainerBlockEntity {

	public static final float MAX_OPEN = 120.0F;
	public static final float MIN_OPEN = 0.0F;
	public static final float OPEN_SPEED = 10.0F;
	public static final float CLOSE_SPEED = 10.0F;

	private float lidAngle = 0.0F;
	private NonNullList<ItemStack> items = NonNullList.withSize(16, ItemStack.EMPTY);

	public FishingTackleBoxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.FISHING_TACKLE_BOX.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, FishingTackleBoxBlockEntity entity) {
		entity.lidAngle = state.getValue(FishingTackleBoxBlock.OPEN) ? Math.min(entity.lidAngle + OPEN_SPEED, MAX_OPEN) : Math.max(entity.lidAngle - CLOSE_SPEED, MIN_OPEN);
	}

	public float getLidAngle(BlockState state, float partialTicks) {
		return state.getValue(FishingTackleBoxBlock.OPEN) ? Math.min(this.lidAngle + OPEN_SPEED * partialTicks, MAX_OPEN) : Math.max(this.lidAngle - CLOSE_SPEED * partialTicks, MIN_OPEN);
	}

	public void seatPlayer(Player player, Level level, BlockPos pos) {
		Seat seat = new Seat(level, false);
		seat.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ()  + 0.5D);
		seat.setSeatOffset(0.1F);
		level.addFreshEntity(seat);
		player.startRiding(seat, true);
		if (player instanceof ServerPlayer sp)
			AdvancementCriteriaRegistry.SIT_ON_TACKLE_BOX.get().trigger(sp);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.fishing_tackle_box");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new FishingTackleBoxMenu(containerId, inventory, this);
	}

	@Override
	public int getContainerSize() {
		return 16;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putFloat("lid_angle", this.lidAngle);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.lidAngle = tag.getFloat("lid_angle");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveCustomOnly(registries);
	}
}
