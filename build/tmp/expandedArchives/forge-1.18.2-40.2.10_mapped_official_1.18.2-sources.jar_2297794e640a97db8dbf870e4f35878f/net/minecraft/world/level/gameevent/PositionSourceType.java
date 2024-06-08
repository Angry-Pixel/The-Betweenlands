package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface PositionSourceType<T extends PositionSource> {
   PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
   PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

   T read(FriendlyByteBuf p_157884_);

   void write(FriendlyByteBuf p_157880_, T p_157881_);

   Codec<T> codec();

   static <S extends PositionSourceType<T>, T extends PositionSource> S register(String p_157878_, S p_157879_) {
      return Registry.register(Registry.POSITION_SOURCE_TYPE, p_157878_, p_157879_);
   }

   static PositionSource fromNetwork(FriendlyByteBuf p_157886_) {
      ResourceLocation resourcelocation = p_157886_.readResourceLocation();
      return Registry.POSITION_SOURCE_TYPE.getOptional(resourcelocation).orElseThrow(() -> {
         return new IllegalArgumentException("Unknown position source type " + resourcelocation);
      }).read(p_157886_);
   }

   static <T extends PositionSource> void toNetwork(T p_157875_, FriendlyByteBuf p_157876_) {
      p_157876_.writeResourceLocation(Registry.POSITION_SOURCE_TYPE.getKey(p_157875_.getType()));
      ((PositionSourceType<T>)p_157875_.getType()).write(p_157876_, p_157875_);
   }
}