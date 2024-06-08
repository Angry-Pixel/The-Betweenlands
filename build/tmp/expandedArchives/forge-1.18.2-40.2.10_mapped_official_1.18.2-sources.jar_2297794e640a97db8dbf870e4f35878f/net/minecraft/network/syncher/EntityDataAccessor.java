package net.minecraft.network.syncher;

public class EntityDataAccessor<T> {
   private final int id;
   private final EntityDataSerializer<T> serializer;

   public EntityDataAccessor(int p_135013_, EntityDataSerializer<T> p_135014_) {
      this.id = p_135013_;
      this.serializer = p_135014_;
   }

   public int getId() {
      return this.id;
   }

   public EntityDataSerializer<T> getSerializer() {
      return this.serializer;
   }

   public boolean equals(Object p_135018_) {
      if (this == p_135018_) {
         return true;
      } else if (p_135018_ != null && this.getClass() == p_135018_.getClass()) {
         EntityDataAccessor<?> entitydataaccessor = (EntityDataAccessor)p_135018_;
         return this.id == entitydataaccessor.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   public String toString() {
      return "<entity data: " + this.id + ">";
   }
}