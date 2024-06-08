package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentSerializer<T extends ArgumentType<?>> {
   void serializeToNetwork(T p_121579_, FriendlyByteBuf p_121580_);

   T deserializeFromNetwork(FriendlyByteBuf p_121581_);

   void serializeToJson(T p_121577_, JsonObject p_121578_);
}