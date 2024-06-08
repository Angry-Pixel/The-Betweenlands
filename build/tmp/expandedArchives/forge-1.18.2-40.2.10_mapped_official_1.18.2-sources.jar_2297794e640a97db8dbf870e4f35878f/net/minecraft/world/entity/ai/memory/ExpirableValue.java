package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.VisibleForDebug;

public class ExpirableValue<T> {
   private final T value;
   private long timeToLive;

   public ExpirableValue(T p_26299_, long p_26300_) {
      this.value = p_26299_;
      this.timeToLive = p_26300_;
   }

   public void tick() {
      if (this.canExpire()) {
         --this.timeToLive;
      }

   }

   public static <T> ExpirableValue<T> of(T p_26310_) {
      return new ExpirableValue<>(p_26310_, Long.MAX_VALUE);
   }

   public static <T> ExpirableValue<T> of(T p_26312_, long p_26313_) {
      return new ExpirableValue<>(p_26312_, p_26313_);
   }

   public long getTimeToLive() {
      return this.timeToLive;
   }

   public T getValue() {
      return this.value;
   }

   public boolean hasExpired() {
      return this.timeToLive <= 0L;
   }

   public String toString() {
      return this.value + (this.canExpire() ? " (ttl: " + this.timeToLive + ")" : "");
   }

   @VisibleForDebug
   public boolean canExpire() {
      return this.timeToLive != Long.MAX_VALUE;
   }

   public static <T> Codec<ExpirableValue<T>> codec(Codec<T> p_26305_) {
      return RecordCodecBuilder.create((p_26308_) -> {
         return p_26308_.group(p_26305_.fieldOf("value").forGetter((p_148193_) -> {
            return p_148193_.value;
         }), Codec.LONG.optionalFieldOf("ttl").forGetter((p_148187_) -> {
            return p_148187_.canExpire() ? Optional.of(p_148187_.timeToLive) : Optional.empty();
         })).apply(p_26308_, (p_148189_, p_148190_) -> {
            return new ExpirableValue<>(p_148189_, p_148190_.orElse(Long.MAX_VALUE));
         });
      });
   }
}