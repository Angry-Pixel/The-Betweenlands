package net.minecraft.world.level.block.state.predicate;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStatePredicate implements Predicate<BlockState> {
   public static final Predicate<BlockState> ANY = (p_61299_) -> {
      return true;
   };
   private final StateDefinition<Block, BlockState> definition;
   private final Map<Property<?>, Predicate<Object>> properties = Maps.newHashMap();

   private BlockStatePredicate(StateDefinition<Block, BlockState> p_61286_) {
      this.definition = p_61286_;
   }

   public static BlockStatePredicate forBlock(Block p_61288_) {
      return new BlockStatePredicate(p_61288_.getStateDefinition());
   }

   public boolean test(@Nullable BlockState p_61290_) {
      if (p_61290_ != null && p_61290_.getBlock().equals(this.definition.getOwner())) {
         if (this.properties.isEmpty()) {
            return true;
         } else {
            for(Entry<Property<?>, Predicate<Object>> entry : this.properties.entrySet()) {
               if (!this.applies(p_61290_, entry.getKey(), entry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected <T extends Comparable<T>> boolean applies(BlockState p_61292_, Property<T> p_61293_, Predicate<Object> p_61294_) {
      T t = p_61292_.getValue(p_61293_);
      return p_61294_.test(t);
   }

   public <V extends Comparable<V>> BlockStatePredicate where(Property<V> p_61296_, Predicate<Object> p_61297_) {
      if (!this.definition.getProperties().contains(p_61296_)) {
         throw new IllegalArgumentException(this.definition + " cannot support property " + p_61296_);
      } else {
         this.properties.put(p_61296_, p_61297_);
         return this;
      }
   }
}