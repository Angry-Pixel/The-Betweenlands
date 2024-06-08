package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.Validate;

public class SingleValuePalette<T> implements Palette<T> {
   private final IdMap<T> registry;
   @Nullable
   private T value;
   private final PaletteResize<T> resizeHandler;

   public SingleValuePalette(IdMap<T> p_188207_, PaletteResize<T> p_188208_, List<T> p_188209_) {
      this.registry = p_188207_;
      this.resizeHandler = p_188208_;
      if (p_188209_.size() > 0) {
         Validate.isTrue(p_188209_.size() <= 1, "Can't initialize SingleValuePalette with %d values.", (long)p_188209_.size());
         this.value = p_188209_.get(0);
      }

   }

   public static <A> Palette<A> create(int p_188214_, IdMap<A> p_188215_, PaletteResize<A> p_188216_, List<A> p_188217_) {
      return new SingleValuePalette<>(p_188215_, p_188216_, p_188217_);
   }

   public int idFor(T p_188219_) {
      if (this.value != null && this.value != p_188219_) {
         return this.resizeHandler.onResize(1, p_188219_);
      } else {
         this.value = p_188219_;
         return 0;
      }
   }

   public boolean maybeHas(Predicate<T> p_188221_) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return p_188221_.test(this.value);
      }
   }

   public T valueFor(int p_188212_) {
      if (this.value != null && p_188212_ == 0) {
         return this.value;
      } else {
         throw new IllegalStateException("Missing Palette entry for id " + p_188212_ + ".");
      }
   }

   public void read(FriendlyByteBuf p_188223_) {
      this.value = this.registry.byIdOrThrow(p_188223_.readVarInt());
   }

   public void write(FriendlyByteBuf p_188226_) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         p_188226_.writeVarInt(this.registry.getId(this.value));
      }
   }

   public int getSerializedSize() {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return FriendlyByteBuf.getVarIntSize(this.registry.getId(this.value));
      }
   }

   public int getSize() {
      return 1;
   }

   public Palette<T> copy() {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return this;
      }
   }
}