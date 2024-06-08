package net.minecraft.world.level.storage.loot;

public class SerializerType<T> {
   private final Serializer<? extends T> serializer;

   public SerializerType(Serializer<? extends T> p_79330_) {
      this.serializer = p_79330_;
   }

   public Serializer<? extends T> getSerializer() {
      return this.serializer;
   }
}