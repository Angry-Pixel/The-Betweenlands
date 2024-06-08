package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DefaultedRegistry<T> extends MappedRegistry<T> {
   private final ResourceLocation defaultKey;
   private Holder<T> defaultValue;

   public DefaultedRegistry(String p_205693_, ResourceKey<? extends Registry<T>> p_205694_, Lifecycle p_205695_, @Nullable Function<T, Holder.Reference<T>> p_205696_) {
      super(p_205694_, p_205695_, p_205696_);
      this.defaultKey = new ResourceLocation(p_205693_);
   }

   public Holder<T> registerMapping(int p_205698_, ResourceKey<T> p_205699_, T p_205700_, Lifecycle p_205701_) {
      Holder<T> holder = super.registerMapping(p_205698_, p_205699_, p_205700_, p_205701_);
      if (this.defaultKey.equals(p_205699_.location())) {
         this.defaultValue = holder;
      }

      return holder;
   }

   public int getId(@Nullable T p_122324_) {
      int i = super.getId(p_122324_);
      return i == -1 ? super.getId(this.defaultValue.value()) : i;
   }

   @Nonnull
   public ResourceLocation getKey(T p_122330_) {
      ResourceLocation resourcelocation = super.getKey(p_122330_);
      return resourcelocation == null ? this.defaultKey : resourcelocation;
   }

   @Nonnull
   public T get(@Nullable ResourceLocation p_122328_) {
      T t = super.get(p_122328_);
      return (T)(t == null ? this.defaultValue.value() : t);
   }

   public Optional<T> getOptional(@Nullable ResourceLocation p_122332_) {
      return Optional.ofNullable(super.get(p_122332_));
   }

   @Nonnull
   public T byId(int p_122317_) {
      T t = super.byId(p_122317_);
      return (T)(t == null ? this.defaultValue.value() : t);
   }

   public Optional<Holder<T>> getRandom(Random p_205703_) {
      return super.getRandom(p_205703_).or(() -> {
         return Optional.of(this.defaultValue);
      });
   }

   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }
}