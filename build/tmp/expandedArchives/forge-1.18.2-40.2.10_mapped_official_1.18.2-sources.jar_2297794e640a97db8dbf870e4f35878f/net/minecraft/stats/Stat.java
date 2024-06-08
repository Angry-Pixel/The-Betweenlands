package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Stat<T> extends ObjectiveCriteria {
   private final StatFormatter formatter;
   private final T value;
   private final StatType<T> type;

   protected Stat(StatType<T> p_12856_, T p_12857_, StatFormatter p_12858_) {
      super(buildName(p_12856_, p_12857_));
      this.type = p_12856_;
      this.formatter = p_12858_;
      this.value = p_12857_;
   }

   public static <T> String buildName(StatType<T> p_12863_, T p_12864_) {
      return locationToKey(Registry.STAT_TYPE.getKey(p_12863_)) + ":" + locationToKey(p_12863_.getRegistry().getKey(p_12864_));
   }

   private static <T> String locationToKey(@Nullable ResourceLocation p_12866_) {
      return p_12866_.toString().replace(':', '.');
   }

   public StatType<T> getType() {
      return this.type;
   }

   public T getValue() {
      return this.value;
   }

   public String format(int p_12861_) {
      return this.formatter.format(p_12861_);
   }

   public boolean equals(Object p_12869_) {
      return this == p_12869_ || p_12869_ instanceof Stat && Objects.equals(this.getName(), ((Stat)p_12869_).getName());
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public String toString() {
      return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + "}";
   }
}