package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;

public class DirectionProperty extends EnumProperty<Direction> {
   protected DirectionProperty(String p_61541_, Collection<Direction> p_61542_) {
      super(p_61541_, Direction.class, p_61542_);
   }

   public static DirectionProperty create(String p_156004_) {
      return create(p_156004_, (p_187558_) -> {
         return true;
      });
   }

   public static DirectionProperty create(String p_61547_, Predicate<Direction> p_61548_) {
      return create(p_61547_, Arrays.stream(Direction.values()).filter(p_61548_).collect(Collectors.toList()));
   }

   public static DirectionProperty create(String p_61550_, Direction... p_61551_) {
      return create(p_61550_, Lists.newArrayList(p_61551_));
   }

   public static DirectionProperty create(String p_61544_, Collection<Direction> p_61545_) {
      return new DirectionProperty(p_61544_, p_61545_);
   }
}