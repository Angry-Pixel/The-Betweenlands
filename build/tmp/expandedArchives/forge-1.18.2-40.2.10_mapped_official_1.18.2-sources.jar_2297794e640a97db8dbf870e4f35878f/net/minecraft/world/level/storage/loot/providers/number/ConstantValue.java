package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;

public final class ConstantValue implements NumberProvider {
   final float value;

   ConstantValue(float p_165690_) {
      this.value = p_165690_;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.CONSTANT;
   }

   public float getFloat(LootContext p_165695_) {
      return this.value;
   }

   public static ConstantValue exactly(float p_165693_) {
      return new ConstantValue(p_165693_);
   }

   public boolean equals(Object p_165697_) {
      if (this == p_165697_) {
         return true;
      } else if (p_165697_ != null && this.getClass() == p_165697_.getClass()) {
         return Float.compare(((ConstantValue)p_165697_).value, this.value) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
   }

   public static class InlineSerializer implements GsonAdapterFactory.InlineSerializer<ConstantValue> {
      public JsonElement serialize(ConstantValue p_165704_, JsonSerializationContext p_165705_) {
         return new JsonPrimitive(p_165704_.value);
      }

      public ConstantValue deserialize(JsonElement p_165710_, JsonDeserializationContext p_165711_) {
         return new ConstantValue(GsonHelper.convertToFloat(p_165710_, "value"));
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ConstantValue> {
      public void serialize(JsonObject p_165717_, ConstantValue p_165718_, JsonSerializationContext p_165719_) {
         p_165717_.addProperty("value", p_165718_.value);
      }

      public ConstantValue deserialize(JsonObject p_165725_, JsonDeserializationContext p_165726_) {
         float f = GsonHelper.getAsFloat(p_165725_, "value");
         return new ConstantValue(f);
      }
   }
}