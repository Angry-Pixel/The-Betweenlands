package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.Validate;

public class LinearPalette<T> implements Palette<T> {
   private final IdMap<T> registry;
   private final T[] values;
   private final PaletteResize<T> resizeHandler;
   private final int bits;
   private int size;

   private LinearPalette(IdMap<T> p_188015_, int p_188016_, PaletteResize<T> p_188017_, List<T> p_188018_) {
      this.registry = p_188015_;
      this.values = (T[])(new Object[1 << p_188016_]);
      this.bits = p_188016_;
      this.resizeHandler = p_188017_;
      Validate.isTrue(p_188018_.size() <= this.values.length, "Can't initialize LinearPalette of size %d with %d entries", this.values.length, p_188018_.size());

      for(int i = 0; i < p_188018_.size(); ++i) {
         this.values[i] = p_188018_.get(i);
      }

      this.size = p_188018_.size();
   }

   private LinearPalette(IdMap<T> p_199921_, T[] p_199922_, PaletteResize<T> p_199923_, int p_199924_, int p_199925_) {
      this.registry = p_199921_;
      this.values = p_199922_;
      this.resizeHandler = p_199923_;
      this.bits = p_199924_;
      this.size = p_199925_;
   }

   public static <A> Palette<A> create(int p_188020_, IdMap<A> p_188021_, PaletteResize<A> p_188022_, List<A> p_188023_) {
      return new LinearPalette<>(p_188021_, p_188020_, p_188022_, p_188023_);
   }

   public int idFor(T p_63040_) {
      for(int i = 0; i < this.size; ++i) {
         if (this.values[i] == p_63040_) {
            return i;
         }
      }

      int j = this.size;
      if (j < this.values.length) {
         this.values[j] = p_63040_;
         ++this.size;
         return j;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, p_63040_);
      }
   }

   public boolean maybeHas(Predicate<T> p_63042_) {
      for(int i = 0; i < this.size; ++i) {
         if (p_63042_.test(this.values[i])) {
            return true;
         }
      }

      return false;
   }

   public T valueFor(int p_63038_) {
      if (p_63038_ >= 0 && p_63038_ < this.size) {
         return this.values[p_63038_];
      } else {
         throw new MissingPaletteEntryException(p_63038_);
      }
   }

   public void read(FriendlyByteBuf p_63046_) {
      this.size = p_63046_.readVarInt();

      for(int i = 0; i < this.size; ++i) {
         this.values[i] = this.registry.byIdOrThrow(p_63046_.readVarInt());
      }

   }

   public void write(FriendlyByteBuf p_63049_) {
      p_63049_.writeVarInt(this.size);

      for(int i = 0; i < this.size; ++i) {
         p_63049_.writeVarInt(this.registry.getId(this.values[i]));
      }

   }

   public int getSerializedSize() {
      int i = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int j = 0; j < this.getSize(); ++j) {
         i += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values[j]));
      }

      return i;
   }

   public int getSize() {
      return this.size;
   }

   public Palette<T> copy() {
      return new LinearPalette<>(this.registry, (T[])((Object[])this.values.clone()), this.resizeHandler, this.bits, this.size);
   }
}