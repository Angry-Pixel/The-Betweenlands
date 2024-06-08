package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;

public class EmptyArgumentSerializer<T extends ArgumentType<?>> implements ArgumentSerializer<T> {
   private final Supplier<T> constructor;

   public EmptyArgumentSerializer(Supplier<T> p_121632_) {
      this.constructor = p_121632_;
   }

   public void serializeToNetwork(T p_121637_, FriendlyByteBuf p_121638_) {
   }

   public T deserializeFromNetwork(FriendlyByteBuf p_121640_) {
      return this.constructor.get();
   }

   public void serializeToJson(T p_121634_, JsonObject p_121635_) {
   }
}