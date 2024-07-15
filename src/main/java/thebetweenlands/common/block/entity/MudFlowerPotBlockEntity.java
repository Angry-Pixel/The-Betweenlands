package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class MudFlowerPotBlockEntity extends BlockEntity {

	private Block flowerBlock = Blocks.AIR;

	public MudFlowerPotBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.MUD_FLOWER_POT.get(), pos, state);
	}

	public void setFlowerBlock(Block flowerBlock) {
		if (flowerBlock != this.flowerBlock) {
			this.flowerBlock = flowerBlock;

			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
		}
	}

	public boolean hasFlowerBlock() {
		return this.flowerBlock != Blocks.AIR;
	}

	public Block getFlowerBlock() {
		return this.flowerBlock;
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("flower", BuiltInRegistries.BLOCK.getKey(this.flowerBlock).toString());
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.flowerBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(tag.getString("flower")));
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
