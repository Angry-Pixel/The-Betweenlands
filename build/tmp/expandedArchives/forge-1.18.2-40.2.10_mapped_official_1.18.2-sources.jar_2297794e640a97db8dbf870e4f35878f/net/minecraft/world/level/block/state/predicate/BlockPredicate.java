package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate implements Predicate<BlockState> {
   private final Block block;

   public BlockPredicate(Block p_61274_) {
      this.block = p_61274_;
   }

   public static BlockPredicate forBlock(Block p_61276_) {
      return new BlockPredicate(p_61276_);
   }

   public boolean test(@Nullable BlockState p_61278_) {
      return p_61278_ != null && p_61278_.is(this.block);
   }
}