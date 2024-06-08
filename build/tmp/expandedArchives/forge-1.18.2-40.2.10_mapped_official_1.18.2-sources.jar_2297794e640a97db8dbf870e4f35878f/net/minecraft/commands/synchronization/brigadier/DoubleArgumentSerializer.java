package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleArgumentSerializer implements ArgumentSerializer<DoubleArgumentType> {
   public void serializeToNetwork(DoubleArgumentType p_121704_, FriendlyByteBuf p_121705_) {
      boolean flag = p_121704_.getMinimum() != -Double.MAX_VALUE;
      boolean flag1 = p_121704_.getMaximum() != Double.MAX_VALUE;
      p_121705_.writeByte(BrigadierArgumentSerializers.createNumberFlags(flag, flag1));
      if (flag) {
         p_121705_.writeDouble(p_121704_.getMinimum());
      }

      if (flag1) {
         p_121705_.writeDouble(p_121704_.getMaximum());
      }

   }

   public DoubleArgumentType deserializeFromNetwork(FriendlyByteBuf p_121707_) {
      byte b0 = p_121707_.readByte();
      double d0 = BrigadierArgumentSerializers.numberHasMin(b0) ? p_121707_.readDouble() : -Double.MAX_VALUE;
      double d1 = BrigadierArgumentSerializers.numberHasMax(b0) ? p_121707_.readDouble() : Double.MAX_VALUE;
      return DoubleArgumentType.doubleArg(d0, d1);
   }

   public void serializeToJson(DoubleArgumentType p_121701_, JsonObject p_121702_) {
      if (p_121701_.getMinimum() != -Double.MAX_VALUE) {
         p_121702_.addProperty("min", p_121701_.getMinimum());
      }

      if (p_121701_.getMaximum() != Double.MAX_VALUE) {
         p_121702_.addProperty("max", p_121701_.getMaximum());
      }

   }
}