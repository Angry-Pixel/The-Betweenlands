package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette<T> implements Palette<T> {
   private final IdMap<T> registry;

   public GlobalPalette(IdMap<T> p_187897_) {
      this.registry = p_187897_;
   }

   public static <A> Palette<A> create(int p_187899_, IdMap<A> p_187900_, PaletteResize<A> p_187901_, List<A> p_187902_) {
      return new GlobalPalette<>(p_187900_);
   }

   public int idFor(T p_62648_) {
      int i = this.registry.getId(p_62648_);
      return i == -1 ? 0 : i;
   }

   public boolean maybeHas(Predicate<T> p_62650_) {
      return true;
   }

   public T valueFor(int p_62646_) {
      T t = this.registry.byId(p_62646_);
      if (t == null) {
         throw new MissingPaletteEntryException(p_62646_);
      } else {
         return t;
      }
   }

   public void read(FriendlyByteBuf p_62654_) {
   }

   public void write(FriendlyByteBuf p_62656_) {
   }

   public int getSerializedSize() {
      return FriendlyByteBuf.getVarIntSize(0);
   }

   public int getSize() {
      return this.registry.size();
   }

   public Palette<T> copy() {
      return this;
   }
}