package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;


public class LanternBlockEntity extends BlockEntity {
    public LanternBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LANTERN.get(), pos, state);
    }
}