package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedStateProvider extends BlockStateProvider {
   public static final Codec<WeightedStateProvider> CODEC = SimpleWeightedRandomList.wrappedCodec(BlockState.CODEC).comapFlatMap(WeightedStateProvider::create, (p_161600_) -> {
      return p_161600_.weightedList;
   }).fieldOf("entries").codec();
   private final SimpleWeightedRandomList<BlockState> weightedList;

   private static DataResult<WeightedStateProvider> create(SimpleWeightedRandomList<BlockState> p_161598_) {
      return p_161598_.isEmpty() ? DataResult.error("WeightedStateProvider with no states") : DataResult.success(new WeightedStateProvider(p_161598_));
   }

   public WeightedStateProvider(SimpleWeightedRandomList<BlockState> p_161596_) {
      this.weightedList = p_161596_;
   }

   public WeightedStateProvider(SimpleWeightedRandomList.Builder<BlockState> p_161594_) {
      this(p_161594_.build());
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
   }

   public BlockState getState(Random p_68823_, BlockPos p_68824_) {
      return this.weightedList.getRandomValue(p_68823_).orElseThrow(IllegalStateException::new);
   }
}