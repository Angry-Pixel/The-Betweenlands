package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class StatType<T> extends net.minecraftforge.registries.ForgeRegistryEntry<StatType<?>> implements Iterable<Stat<T>> {
   private final Registry<T> registry;
   private final Map<T, Stat<T>> map = new IdentityHashMap<>();
   @Nullable
   private Component displayName;

   public StatType(Registry<T> p_12892_) {
      this.registry = p_12892_;
   }

   public boolean contains(T p_12898_) {
      return this.map.containsKey(p_12898_);
   }

   public Stat<T> get(T p_12900_, StatFormatter p_12901_) {
      return this.map.computeIfAbsent(p_12900_, (p_12896_) -> {
         return new Stat<>(this, p_12896_, p_12901_);
      });
   }

   public Registry<T> getRegistry() {
      return this.registry;
   }

   public Iterator<Stat<T>> iterator() {
      return this.map.values().iterator();
   }

   public Stat<T> get(T p_12903_) {
      return this.get(p_12903_, StatFormatter.DEFAULT);
   }

   public String getTranslationKey() {
      return "stat_type." + Registry.STAT_TYPE.getKey(this).toString().replace(':', '.');
   }

   public Component getDisplayName() {
      if (this.displayName == null) {
         this.displayName = new TranslatableComponent(this.getTranslationKey());
      }

      return this.displayName;
   }
}
