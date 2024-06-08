package net.minecraft.network.syncher;

import net.minecraft.network.FriendlyByteBuf;

public interface EntityDataSerializer<T> {
   void write(FriendlyByteBuf p_135025_, T p_135026_);

   T read(FriendlyByteBuf p_135024_);

   default EntityDataAccessor<T> createAccessor(int p_135022_) {
      return new EntityDataAccessor<>(p_135022_, this);
   }

   T copy(T p_135023_);
}