package net.minecraft.world.level.timers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface TimerCallback<T> {
   void handle(T p_82213_, TimerQueue<T> p_82214_, long p_82215_);

   public abstract static class Serializer<T, C extends TimerCallback<T>> {
      private final ResourceLocation id;
      private final Class<?> cls;

      public Serializer(ResourceLocation p_82219_, Class<?> p_82220_) {
         this.id = p_82219_;
         this.cls = p_82220_;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public Class<?> getCls() {
         return this.cls;
      }

      public abstract void serialize(CompoundTag p_82222_, C p_82223_);

      public abstract C deserialize(CompoundTag p_82225_);
   }
}