package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.resources.ResourceKey;

public abstract class WritableRegistry<T> extends Registry<T> {
   public WritableRegistry(ResourceKey<? extends Registry<T>> p_123346_, Lifecycle p_123347_) {
      super(p_123346_, p_123347_);
   }

   public abstract Holder<T> registerMapping(int p_206368_, ResourceKey<T> p_206369_, T p_206370_, Lifecycle p_206371_);

   public abstract Holder<T> register(ResourceKey<T> p_206376_, T p_206377_, Lifecycle p_206378_);

   public abstract Holder<T> registerOrOverride(OptionalInt p_206372_, ResourceKey<T> p_206373_, T p_206374_, Lifecycle p_206375_);

   public abstract boolean isEmpty();
}