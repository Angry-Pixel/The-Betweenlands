package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;

public class EnumProperty<T extends Enum<T> & StringRepresentable> extends Property<T> {
   private final ImmutableSet<T> values;
   private final Map<String, T> names = Maps.newHashMap();

   protected EnumProperty(String p_61579_, Class<T> p_61580_, Collection<T> p_61581_) {
      super(p_61579_, p_61580_);
      this.values = ImmutableSet.copyOf(p_61581_);

      for(T t : p_61581_) {
         String s = t.getSerializedName();
         if (this.names.containsKey(s)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
         }

         this.names.put(s, t);
      }

   }

   public Collection<T> getPossibleValues() {
      return this.values;
   }

   public Optional<T> getValue(String p_61604_) {
      return Optional.ofNullable(this.names.get(p_61604_));
   }

   public String getName(T p_61586_) {
      return p_61586_.getSerializedName();
   }

   public boolean equals(Object p_61606_) {
      if (this == p_61606_) {
         return true;
      } else if (p_61606_ instanceof EnumProperty && super.equals(p_61606_)) {
         EnumProperty<?> enumproperty = (EnumProperty)p_61606_;
         return this.values.equals(enumproperty.values) && this.names.equals(enumproperty.names);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      int i = super.generateHashCode();
      i = 31 * i + this.values.hashCode();
      return 31 * i + this.names.hashCode();
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61588_, Class<T> p_61589_) {
      return create(p_61588_, p_61589_, (p_187560_) -> {
         return true;
      });
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61595_, Class<T> p_61596_, Predicate<T> p_61597_) {
      return create(p_61595_, p_61596_, Arrays.<T>stream(p_61596_.getEnumConstants()).filter(p_61597_).collect(Collectors.toList()));
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61599_, Class<T> p_61600_, T... p_61601_) {
      return create(p_61599_, p_61600_, Lists.newArrayList(p_61601_));
   }

   public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61591_, Class<T> p_61592_, Collection<T> p_61593_) {
      return new EnumProperty<>(p_61591_, p_61592_, p_61593_);
   }
}