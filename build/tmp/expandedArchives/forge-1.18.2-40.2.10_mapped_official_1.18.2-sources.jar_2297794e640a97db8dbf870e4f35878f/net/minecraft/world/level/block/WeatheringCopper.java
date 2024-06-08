package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringCopper extends ChangeOverTimeBlock<WeatheringCopper.WeatherState> {
   Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
      return ImmutableBiMap.<Block, Block>builder().put(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER).put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER).put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER).put(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER).put(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER).put(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER).put(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB).put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB).put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB).put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS).put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS).put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS).build();
   });
   Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> {
      return NEXT_BY_BLOCK.get().inverse();
   });

   static Optional<Block> getPrevious(Block p_154891_) {
      return Optional.ofNullable(PREVIOUS_BY_BLOCK.get().get(p_154891_));
   }

   static Block getFirst(Block p_154898_) {
      Block block = p_154898_;

      for(Block block1 = PREVIOUS_BY_BLOCK.get().get(p_154898_); block1 != null; block1 = PREVIOUS_BY_BLOCK.get().get(block1)) {
         block = block1;
      }

      return block;
   }

   static Optional<BlockState> getPrevious(BlockState p_154900_) {
      return getPrevious(p_154900_.getBlock()).map((p_154903_) -> {
         return p_154903_.withPropertiesOf(p_154900_);
      });
   }

   static Optional<Block> getNext(Block p_154905_) {
      return Optional.ofNullable(NEXT_BY_BLOCK.get().get(p_154905_));
   }

   static BlockState getFirst(BlockState p_154907_) {
      return getFirst(p_154907_.getBlock()).withPropertiesOf(p_154907_);
   }

   default Optional<BlockState> getNext(BlockState p_154893_) {
      return getNext(p_154893_.getBlock()).map((p_154896_) -> {
         return p_154896_.withPropertiesOf(p_154893_);
      });
   }

   default float getChanceModifier() {
      return this.getAge() == WeatheringCopper.WeatherState.UNAFFECTED ? 0.75F : 1.0F;
   }

   public static enum WeatherState {
      UNAFFECTED,
      EXPOSED,
      WEATHERED,
      OXIDIZED;
   }
}