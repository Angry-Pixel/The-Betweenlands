package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStateProvider {
   public static final Codec<BlockStateProvider> CODEC = Registry.BLOCKSTATE_PROVIDER_TYPES.byNameCodec().dispatch(BlockStateProvider::type, BlockStateProviderType::codec);

   public static SimpleStateProvider simple(BlockState p_191385_) {
      return new SimpleStateProvider(p_191385_);
   }

   public static SimpleStateProvider simple(Block p_191383_) {
      return new SimpleStateProvider(p_191383_.defaultBlockState());
   }

   protected abstract BlockStateProviderType<?> type();

   public abstract BlockState getState(Random p_68750_, BlockPos p_68751_);
}