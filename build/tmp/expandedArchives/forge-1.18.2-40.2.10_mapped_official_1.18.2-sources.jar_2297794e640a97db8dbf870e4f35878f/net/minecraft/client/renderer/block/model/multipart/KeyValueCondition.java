package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyValueCondition implements Condition {
   private static final Splitter PIPE_SPLITTER = Splitter.on('|').omitEmptyStrings();
   private final String key;
   private final String value;

   public KeyValueCondition(String p_111939_, String p_111940_) {
      this.key = p_111939_;
      this.value = p_111940_;
   }

   public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> p_111960_) {
      Property<?> property = p_111960_.getProperty(this.key);
      if (property == null) {
         throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, p_111960_.getOwner()));
      } else {
         String s = this.value;
         boolean flag = !s.isEmpty() && s.charAt(0) == '!';
         if (flag) {
            s = s.substring(1);
         }

         List<String> list = PIPE_SPLITTER.splitToList(s);
         if (list.isEmpty()) {
            throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.value, this.key, p_111960_.getOwner()));
         } else {
            Predicate<BlockState> predicate;
            if (list.size() == 1) {
               predicate = this.getBlockStatePredicate(p_111960_, property, s);
            } else {
               List<Predicate<BlockState>> list1 = list.stream().map((p_111958_) -> {
                  return this.getBlockStatePredicate(p_111960_, property, p_111958_);
               }).collect(Collectors.toList());
               predicate = (p_111954_) -> {
                  return list1.stream().anyMatch((p_173509_) -> {
                     return p_173509_.test(p_111954_);
                  });
               };
            }

            return flag ? predicate.negate() : predicate;
         }
      }
   }

   private Predicate<BlockState> getBlockStatePredicate(StateDefinition<Block, BlockState> p_111945_, Property<?> p_111946_, String p_111947_) {
      Optional<?> optional = p_111946_.getValue(p_111947_);
      if (!optional.isPresent()) {
         throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", p_111947_, this.key, p_111945_.getOwner(), this.value));
      } else {
         return (p_111951_) -> {
            return p_111951_.getValue(p_111946_).equals(optional.get());
         };
      }
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
   }
}