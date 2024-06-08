package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AndCondition implements Condition {
   public static final String TOKEN = "AND";
   private final Iterable<? extends Condition> conditions;

   public AndCondition(Iterable<? extends Condition> p_111910_) {
      this.conditions = p_111910_;
   }

   public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> p_111921_) {
      List<Predicate<BlockState>> list = Streams.stream(this.conditions).map((p_111916_) -> {
         return p_111916_.getPredicate(p_111921_);
      }).collect(Collectors.toList());
      return (p_111919_) -> {
         return list.stream().allMatch((p_173502_) -> {
            return p_173502_.test(p_111919_);
         });
      };
   }
}