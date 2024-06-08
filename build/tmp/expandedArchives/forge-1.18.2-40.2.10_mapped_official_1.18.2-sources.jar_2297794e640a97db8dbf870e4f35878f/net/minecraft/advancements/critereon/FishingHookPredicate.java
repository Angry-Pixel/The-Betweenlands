package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;

public class FishingHookPredicate {
   public static final FishingHookPredicate ANY = new FishingHookPredicate(false);
   private static final String IN_OPEN_WATER_KEY = "in_open_water";
   private final boolean inOpenWater;

   private FishingHookPredicate(boolean p_39760_) {
      this.inOpenWater = p_39760_;
   }

   public static FishingHookPredicate inOpenWater(boolean p_39767_) {
      return new FishingHookPredicate(p_39767_);
   }

   public static FishingHookPredicate fromJson(@Nullable JsonElement p_39765_) {
      if (p_39765_ != null && !p_39765_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_39765_, "fishing_hook");
         JsonElement jsonelement = jsonobject.get("in_open_water");
         return jsonelement != null ? new FishingHookPredicate(GsonHelper.convertToBoolean(jsonelement, "in_open_water")) : ANY;
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("in_open_water", new JsonPrimitive(this.inOpenWater));
         return jsonobject;
      }
   }

   public boolean matches(Entity p_39763_) {
      if (this == ANY) {
         return true;
      } else if (!(p_39763_ instanceof FishingHook)) {
         return false;
      } else {
         FishingHook fishinghook = (FishingHook)p_39763_;
         return this.inOpenWater == fishinghook.isOpenWaterFishing();
      }
   }
}