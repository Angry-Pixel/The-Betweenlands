package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class LongArgumentSerializer implements ArgumentSerializer<LongArgumentType> {
   public void serializeToNetwork(LongArgumentType p_121755_, FriendlyByteBuf p_121756_) {
      boolean flag = p_121755_.getMinimum() != Long.MIN_VALUE;
      boolean flag1 = p_121755_.getMaximum() != Long.MAX_VALUE;
      p_121756_.writeByte(BrigadierArgumentSerializers.createNumberFlags(flag, flag1));
      if (flag) {
         p_121756_.writeLong(p_121755_.getMinimum());
      }

      if (flag1) {
         p_121756_.writeLong(p_121755_.getMaximum());
      }

   }

   public LongArgumentType deserializeFromNetwork(FriendlyByteBuf p_121758_) {
      byte b0 = p_121758_.readByte();
      long i = BrigadierArgumentSerializers.numberHasMin(b0) ? p_121758_.readLong() : Long.MIN_VALUE;
      long j = BrigadierArgumentSerializers.numberHasMax(b0) ? p_121758_.readLong() : Long.MAX_VALUE;
      return LongArgumentType.longArg(i, j);
   }

   public void serializeToJson(LongArgumentType p_121752_, JsonObject p_121753_) {
      if (p_121752_.getMinimum() != Long.MIN_VALUE) {
         p_121753_.addProperty("min", p_121752_.getMinimum());
      }

      if (p_121752_.getMaximum() != Long.MAX_VALUE) {
         p_121753_.addProperty("max", p_121752_.getMaximum());
      }

   }
}