package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
   public static final EnchantmentPredicate[] NONE = new EnchantmentPredicate[0];
   @Nullable
   private final Enchantment enchantment;
   private final MinMaxBounds.Ints level;

   public EnchantmentPredicate() {
      this.enchantment = null;
      this.level = MinMaxBounds.Ints.ANY;
   }

   public EnchantmentPredicate(@Nullable Enchantment p_30471_, MinMaxBounds.Ints p_30472_) {
      this.enchantment = p_30471_;
      this.level = p_30472_;
   }

   public boolean containedIn(Map<Enchantment, Integer> p_30477_) {
      if (this.enchantment != null) {
         if (!p_30477_.containsKey(this.enchantment)) {
            return false;
         }

         int i = p_30477_.get(this.enchantment);
         if (this.level != MinMaxBounds.Ints.ANY && !this.level.matches(i)) {
            return false;
         }
      } else if (this.level != MinMaxBounds.Ints.ANY) {
         for(Integer integer : p_30477_.values()) {
            if (this.level.matches(integer)) {
               return true;
            }
         }

         return false;
      }

      return true;
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.enchantment != null) {
            jsonobject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(this.enchantment).toString());
         }

         jsonobject.add("levels", this.level.serializeToJson());
         return jsonobject;
      }
   }

   public static EnchantmentPredicate fromJson(@Nullable JsonElement p_30475_) {
      if (p_30475_ != null && !p_30475_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_30475_, "enchantment");
         Enchantment enchantment = null;
         if (jsonobject.has("enchantment")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonobject, "enchantment"));
            enchantment = Registry.ENCHANTMENT.getOptional(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown enchantment '" + resourcelocation + "'");
            });
         }

         MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(jsonobject.get("levels"));
         return new EnchantmentPredicate(enchantment, minmaxbounds$ints);
      } else {
         return ANY;
      }
   }

   public static EnchantmentPredicate[] fromJsonArray(@Nullable JsonElement p_30481_) {
      if (p_30481_ != null && !p_30481_.isJsonNull()) {
         JsonArray jsonarray = GsonHelper.convertToJsonArray(p_30481_, "enchantments");
         EnchantmentPredicate[] aenchantmentpredicate = new EnchantmentPredicate[jsonarray.size()];

         for(int i = 0; i < aenchantmentpredicate.length; ++i) {
            aenchantmentpredicate[i] = fromJson(jsonarray.get(i));
         }

         return aenchantmentpredicate;
      } else {
         return NONE;
      }
   }
}