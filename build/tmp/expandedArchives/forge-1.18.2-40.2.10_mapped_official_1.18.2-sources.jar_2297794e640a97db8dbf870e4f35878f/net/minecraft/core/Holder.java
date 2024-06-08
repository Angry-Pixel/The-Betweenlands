package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface Holder<T> extends net.minecraftforge.registries.tags.IReverseTag<T> {
   @Override
   default boolean containsTag(TagKey<T> key) {
      return this.is(key);
   }

   @Override
   default Stream<TagKey<T>> getTagKeys() {
      return this.tags();
   }

   T value();

   boolean isBound();

   boolean is(ResourceLocation p_205713_);

   boolean is(ResourceKey<T> p_205712_);

   boolean is(Predicate<ResourceKey<T>> p_205711_);

   boolean is(TagKey<T> p_205705_);

   Stream<TagKey<T>> tags();

   Either<ResourceKey<T>, T> unwrap();

   Optional<ResourceKey<T>> unwrapKey();

   Holder.Kind kind();

   boolean isValidInRegistry(Registry<T> p_205708_);

   static <T> Holder<T> direct(T p_205710_) {
      return new Holder.Direct<>(p_205710_);
   }

   static <T> Holder<T> hackyErase(Holder<? extends T> p_205707_) {
      return (Holder<T>)p_205707_;
   }

   public static record Direct<T>(T value) implements Holder<T> {
      public boolean isBound() {
         return true;
      }

      public boolean is(ResourceLocation p_205727_) {
         return false;
      }

      public boolean is(ResourceKey<T> p_205725_) {
         return false;
      }

      public boolean is(TagKey<T> p_205719_) {
         return false;
      }

      public boolean is(Predicate<ResourceKey<T>> p_205723_) {
         return false;
      }

      public Either<ResourceKey<T>, T> unwrap() {
         return Either.right(this.value);
      }

      public Optional<ResourceKey<T>> unwrapKey() {
         return Optional.empty();
      }

      public Holder.Kind kind() {
         return Holder.Kind.DIRECT;
      }

      public String toString() {
         return "Direct{" + this.value + "}";
      }

      public boolean isValidInRegistry(Registry<T> p_205721_) {
         return true;
      }

      public Stream<TagKey<T>> tags() {
         return Stream.of();
      }

      public T value() {
         return this.value;
      }
   }

   public static enum Kind {
      REFERENCE,
      DIRECT;
   }

   public static class Reference<T> implements Holder<T> {
      private final Registry<T> registry;
      private Set<TagKey<T>> tags = Set.of();
      private final Holder.Reference.Type type;
      @Nullable
      private ResourceKey<T> key;
      @Nullable
      private T value;

      private Reference(Holder.Reference.Type p_205754_, Registry<T> p_205755_, @Nullable ResourceKey<T> p_205756_, @Nullable T p_205757_) {
         this.registry = p_205755_;
         this.type = p_205754_;
         this.key = p_205756_;
         this.value = p_205757_;
      }

      public static <T> Holder.Reference<T> createStandAlone(Registry<T> p_205767_, ResourceKey<T> p_205768_) {
         return new Holder.Reference<>(Holder.Reference.Type.STAND_ALONE, p_205767_, p_205768_, (T)null);
      }

      /** @deprecated */
      @Deprecated
      public static <T> Holder.Reference<T> createIntrusive(Registry<T> p_205764_, @Nullable T p_205765_) {
         return new Holder.Reference<>(Holder.Reference.Type.INTRUSIVE, p_205764_, (ResourceKey<T>)null, p_205765_);
      }

      public ResourceKey<T> key() {
         if (this.key == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.registry);
         } else {
            return this.key;
         }
      }

      public T value() {
         if (this.value == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.key + "' from registry " + this.registry);
         } else {
            return this.value;
         }
      }

      public boolean is(ResourceLocation p_205779_) {
         return this.key().location().equals(p_205779_);
      }

      public boolean is(ResourceKey<T> p_205774_) {
         return this.key() == p_205774_;
      }

      public boolean is(TagKey<T> p_205760_) {
         return this.tags.contains(p_205760_);
      }

      public boolean is(Predicate<ResourceKey<T>> p_205772_) {
         return p_205772_.test(this.key());
      }

      public boolean isValidInRegistry(Registry<T> p_205762_) {
         return this.registry == p_205762_;
      }

      public Either<ResourceKey<T>, T> unwrap() {
         return Either.left(this.key());
      }

      public Optional<ResourceKey<T>> unwrapKey() {
         return Optional.of(this.key());
      }

      public Holder.Kind kind() {
         return Holder.Kind.REFERENCE;
      }

      public boolean isBound() {
         return this.key != null && this.value != null;
      }

      public void bind(ResourceKey<T> p_205776_, T p_205777_) {
         if (this.key != null && p_205776_ != this.key) {
            throw new IllegalStateException("Can't change holder key: existing=" + this.key + ", new=" + p_205776_);
         } else if (this.type == Holder.Reference.Type.INTRUSIVE && this.value != p_205777_) {
            throw new IllegalStateException("Can't change holder " + p_205776_ + " value: existing=" + this.value + ", new=" + p_205777_);
         } else {
            this.key = p_205776_;
            this.value = p_205777_;
         }
      }

      public void bindTags(Collection<TagKey<T>> p_205770_) {
         this.tags = Set.copyOf(p_205770_);
      }

      public Stream<TagKey<T>> tags() {
         return this.tags.stream();
      }

      public Type getType() {
         return this.type;
      }

      public String toString() {
         return "Reference{" + this.key + "=" + this.value + "}";
      }

      public static enum Type {
         STAND_ALONE,
         INTRUSIVE;
      }
   }
}
