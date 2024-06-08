package net.minecraft.data.models.blockstates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class VariantProperty<T> {
   final String key;
   final Function<T, JsonElement> serializer;

   public VariantProperty(String p_125549_, Function<T, JsonElement> p_125550_) {
      this.key = p_125549_;
      this.serializer = p_125550_;
   }

   public VariantProperty<T>.Value withValue(T p_125554_) {
      return new VariantProperty.Value(p_125554_);
   }

   public String toString() {
      return this.key;
   }

   public class Value {
      private final T value;

      public Value(T p_125562_) {
         this.value = p_125562_;
      }

      public VariantProperty<T> getKey() {
         return VariantProperty.this;
      }

      public void addToVariant(JsonObject p_125564_) {
         p_125564_.add(VariantProperty.this.key, VariantProperty.this.serializer.apply(this.value));
      }

      public String toString() {
         return VariantProperty.this.key + "=" + this.value;
      }
   }
}