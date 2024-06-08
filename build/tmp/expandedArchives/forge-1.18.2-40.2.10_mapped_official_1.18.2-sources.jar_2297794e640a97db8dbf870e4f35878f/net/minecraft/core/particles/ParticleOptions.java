package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.FriendlyByteBuf;

public interface ParticleOptions {
   ParticleType<?> getType();

   void writeToNetwork(FriendlyByteBuf p_123732_);

   String writeToString();

   /** @deprecated */
   @Deprecated
   public interface Deserializer<T extends ParticleOptions> {
      T fromCommand(ParticleType<T> p_123733_, StringReader p_123734_) throws CommandSyntaxException;

      T fromNetwork(ParticleType<T> p_123735_, FriendlyByteBuf p_123736_);
   }
}