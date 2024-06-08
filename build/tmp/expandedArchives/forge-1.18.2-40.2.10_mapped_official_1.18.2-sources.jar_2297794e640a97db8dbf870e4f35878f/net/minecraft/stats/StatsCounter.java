package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.player.Player;

public class StatsCounter {
   protected final Object2IntMap<Stat<?>> stats = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

   public StatsCounter() {
      this.stats.defaultReturnValue(0);
   }

   public void increment(Player p_13024_, Stat<?> p_13025_, int p_13026_) {
      int i = (int)Math.min((long)this.getValue(p_13025_) + (long)p_13026_, 2147483647L);
      this.setValue(p_13024_, p_13025_, i);
   }

   public void setValue(Player p_13020_, Stat<?> p_13021_, int p_13022_) {
      this.stats.put(p_13021_, p_13022_);
   }

   public <T> int getValue(StatType<T> p_13018_, T p_13019_) {
      return p_13018_.contains(p_13019_) ? this.getValue(p_13018_.get(p_13019_)) : 0;
   }

   public int getValue(Stat<?> p_13016_) {
      return this.stats.getInt(p_13016_);
   }
}