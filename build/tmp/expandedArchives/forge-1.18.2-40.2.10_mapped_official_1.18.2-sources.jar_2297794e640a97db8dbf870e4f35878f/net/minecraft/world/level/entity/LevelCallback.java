package net.minecraft.world.level.entity;

public interface LevelCallback<T> {
   void onCreated(T p_156930_);

   void onDestroyed(T p_156929_);

   void onTickingStart(T p_156928_);

   void onTickingEnd(T p_156927_);

   void onTrackingStart(T p_156926_);

   void onTrackingEnd(T p_156925_);
}