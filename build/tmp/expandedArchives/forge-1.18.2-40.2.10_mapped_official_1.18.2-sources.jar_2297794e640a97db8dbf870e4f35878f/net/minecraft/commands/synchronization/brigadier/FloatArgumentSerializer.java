package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class FloatArgumentSerializer implements ArgumentSerializer<FloatArgumentType> {
   public void serializeToNetwork(FloatArgumentType p_121721_, FriendlyByteBuf p_121722_) {
      boolean flag = p_121721_.getMinimum() != -Float.MAX_VALUE;
      boolean flag1 = p_121721_.getMaximum() != Float.MAX_VALUE;
      p_121722_.writeByte(BrigadierArgumentSerializers.createNumberFlags(flag, flag1));
      if (flag) {
         p_121722_.writeFloat(p_121721_.getMinimum());
      }

      if (flag1) {
         p_121722_.writeFloat(p_121721_.getMaximum());
      }

   }

   public FloatArgumentType deserializeFromNetwork(FriendlyByteBuf p_121724_) {
      byte b0 = p_121724_.readByte();
      float f = BrigadierArgumentSerializers.numberHasMin(b0) ? p_121724_.readFloat() : -Float.MAX_VALUE;
      float f1 = BrigadierArgumentSerializers.numberHasMax(b0) ? p_121724_.readFloat() : Float.MAX_VALUE;
      return FloatArgumentType.floatArg(f, f1);
   }

   public void serializeToJson(FloatArgumentType p_121718_, JsonObject p_121719_) {
      if (p_121718_.getMinimum() != -Float.MAX_VALUE) {
         p_121719_.addProperty("min", p_121718_.getMinimum());
      }

      if (p_121718_.getMaximum() != Float.MAX_VALUE) {
         p_121719_.addProperty("max", p_121718_.getMaximum());
      }

   }
}