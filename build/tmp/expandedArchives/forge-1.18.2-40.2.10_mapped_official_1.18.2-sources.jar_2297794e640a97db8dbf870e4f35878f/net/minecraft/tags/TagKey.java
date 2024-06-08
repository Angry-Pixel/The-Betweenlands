package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record TagKey<T>(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
   private static final Interner<TagKey<?>> VALUES = Interners.newStrongInterner();

   public static <T> Codec<TagKey<T>> codec(ResourceKey<? extends Registry<T>> p_203878_) {
      return ResourceLocation.CODEC.xmap((p_203893_) -> {
         return create(p_203878_, p_203893_);
      }, TagKey::location);
   }

   public static <T> Codec<TagKey<T>> hashedCodec(ResourceKey<? extends Registry<T>> p_203887_) {
      return Codec.STRING.comapFlatMap((p_203881_) -> {
         return p_203881_.startsWith("#") ? ResourceLocation.read(p_203881_.substring(1)).map((p_203890_) -> {
            return create(p_203887_, p_203890_);
         }) : DataResult.error("Not a tag id");
      }, (p_203876_) -> {
         return "#" + p_203876_.location;
      });
   }

   public static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> p_203883_, ResourceLocation p_203884_) {
      return (TagKey<T>)VALUES.intern(new TagKey<>(p_203883_, p_203884_));
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> p_207646_) {
      return this.registry == p_207646_;
   }

   public <E> Optional<TagKey<E>> cast(ResourceKey<? extends Registry<E>> p_207648_) {
      return this.isFor(p_207648_) ? Optional.of((TagKey<E>)this) : Optional.empty();
   }

   public String toString() {
      return "TagKey[" + this.registry.location() + " / " + this.location + "]";
   }
}