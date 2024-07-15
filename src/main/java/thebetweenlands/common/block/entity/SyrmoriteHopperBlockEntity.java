package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class SyrmoriteHopperBlockEntity extends HopperBlockEntity {
	public SyrmoriteHopperBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return BlockEntityRegistry.SYRMORITE_HOPPER.get();
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.syrmorite_hopper");
	}
}
