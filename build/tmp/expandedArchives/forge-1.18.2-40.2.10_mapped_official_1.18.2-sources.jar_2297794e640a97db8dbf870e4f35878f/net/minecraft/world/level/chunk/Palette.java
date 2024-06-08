package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public interface Palette<T> {
   int idFor(T p_63061_);

   boolean maybeHas(Predicate<T> p_63062_);

   T valueFor(int p_63060_);

   void read(FriendlyByteBuf p_63064_);

   void write(FriendlyByteBuf p_63065_);

   int getSerializedSize();

   int getSize();

   Palette<T> copy();

   public interface Factory {
      <A> Palette<A> create(int p_188027_, IdMap<A> p_188028_, PaletteResize<A> p_188029_, List<A> p_188030_);
   }
}