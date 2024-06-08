package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.state.properties.Property;

public final class Selector {
   private static final Selector EMPTY = new Selector(ImmutableList.of());
   private static final Comparator<Property.Value<?>> COMPARE_BY_NAME = Comparator.comparing((p_125494_) -> {
      return p_125494_.property().getName();
   });
   private final List<Property.Value<?>> values;

   public Selector extend(Property.Value<?> p_125487_) {
      return new Selector(ImmutableList.<Property.Value<?>>builder().addAll(this.values).add(p_125487_).build());
   }

   public Selector extend(Selector p_125489_) {
      return new Selector(ImmutableList.<Property.Value<?>>builder().addAll(this.values).addAll(p_125489_.values).build());
   }

   private Selector(List<Property.Value<?>> p_125484_) {
      this.values = p_125484_;
   }

   public static Selector empty() {
      return EMPTY;
   }

   public static Selector of(Property.Value<?>... p_125491_) {
      return new Selector(ImmutableList.copyOf(p_125491_));
   }

   public boolean equals(Object p_125496_) {
      return this == p_125496_ || p_125496_ instanceof Selector && this.values.equals(((Selector)p_125496_).values);
   }

   public int hashCode() {
      return this.values.hashCode();
   }

   public String getKey() {
      return this.values.stream().sorted(COMPARE_BY_NAME).map(Property.Value::toString).collect(Collectors.joining(","));
   }

   public String toString() {
      return this.getKey();
   }
}