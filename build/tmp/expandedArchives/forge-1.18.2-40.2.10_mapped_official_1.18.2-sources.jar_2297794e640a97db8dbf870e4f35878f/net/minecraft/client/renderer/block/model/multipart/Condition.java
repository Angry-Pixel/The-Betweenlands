package net.minecraft.client.renderer.block.model.multipart;

import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface Condition {
   Condition TRUE = (p_111932_) -> {
      return (p_173506_) -> {
         return true;
      };
   };
   Condition FALSE = (p_111928_) -> {
      return (p_173504_) -> {
         return false;
      };
   };

   Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> p_111933_);
}