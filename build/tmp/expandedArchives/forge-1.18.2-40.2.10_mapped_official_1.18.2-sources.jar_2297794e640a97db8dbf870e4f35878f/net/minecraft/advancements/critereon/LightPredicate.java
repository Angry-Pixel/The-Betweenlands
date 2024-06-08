package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

public class LightPredicate {
   public static final LightPredicate ANY = new LightPredicate(MinMaxBounds.Ints.ANY);
   private final MinMaxBounds.Ints composite;

   LightPredicate(MinMaxBounds.Ints p_51339_) {
      this.composite = p_51339_;
   }

   public boolean matches(ServerLevel p_51342_, BlockPos p_51343_) {
      if (this == ANY) {
         return true;
      } else if (!p_51342_.isLoaded(p_51343_)) {
         return false;
      } else {
         return this.composite.matches(p_51342_.getMaxLocalRawBrightness(p_51343_));
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("light", this.composite.serializeToJson());
         return jsonobject;
      }
   }

   public static LightPredicate fromJson(@Nullable JsonElement p_51345_) {
      if (p_51345_ != null && !p_51345_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_51345_, "light");
         MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(jsonobject.get("light"));
         return new LightPredicate(minmaxbounds$ints);
      } else {
         return ANY;
      }
   }

   public static class Builder {
      private MinMaxBounds.Ints composite = MinMaxBounds.Ints.ANY;

      public static LightPredicate.Builder light() {
         return new LightPredicate.Builder();
      }

      public LightPredicate.Builder setComposite(MinMaxBounds.Ints p_153105_) {
         this.composite = p_153105_;
         return this;
      }

      public LightPredicate build() {
         return new LightPredicate(this.composite);
      }
   }
}