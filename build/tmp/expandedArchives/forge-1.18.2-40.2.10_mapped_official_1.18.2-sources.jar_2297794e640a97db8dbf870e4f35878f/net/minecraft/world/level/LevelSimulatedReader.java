package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

public interface LevelSimulatedReader {
   boolean isStateAtPosition(BlockPos p_46938_, Predicate<BlockState> p_46939_);

   boolean isFluidAtPosition(BlockPos p_151584_, Predicate<FluidState> p_151585_);

   <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos p_151582_, BlockEntityType<T> p_151583_);

   BlockPos getHeightmapPos(Heightmap.Types p_46936_, BlockPos p_46937_);
}