package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class DistancePredicate {
   public static final DistancePredicate ANY = new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   private final MinMaxBounds.Doubles x;
   private final MinMaxBounds.Doubles y;
   private final MinMaxBounds.Doubles z;
   private final MinMaxBounds.Doubles horizontal;
   private final MinMaxBounds.Doubles absolute;

   public DistancePredicate(MinMaxBounds.Doubles p_26249_, MinMaxBounds.Doubles p_26250_, MinMaxBounds.Doubles p_26251_, MinMaxBounds.Doubles p_26252_, MinMaxBounds.Doubles p_26253_) {
      this.x = p_26249_;
      this.y = p_26250_;
      this.z = p_26251_;
      this.horizontal = p_26252_;
      this.absolute = p_26253_;
   }

   public static DistancePredicate horizontal(MinMaxBounds.Doubles p_148837_) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, p_148837_, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate vertical(MinMaxBounds.Doubles p_148839_) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, p_148839_, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate absolute(MinMaxBounds.Doubles p_148841_) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, p_148841_);
   }

   public boolean matches(double p_26256_, double p_26257_, double p_26258_, double p_26259_, double p_26260_, double p_26261_) {
      float f = (float)(p_26256_ - p_26259_);
      float f1 = (float)(p_26257_ - p_26260_);
      float f2 = (float)(p_26258_ - p_26261_);
      if (this.x.matches((double)Mth.abs(f)) && this.y.matches((double)Mth.abs(f1)) && this.z.matches((double)Mth.abs(f2))) {
         if (!this.horizontal.matchesSqr((double)(f * f + f2 * f2))) {
            return false;
         } else {
            return this.absolute.matchesSqr((double)(f * f + f1 * f1 + f2 * f2));
         }
      } else {
         return false;
      }
   }

   public static DistancePredicate fromJson(@Nullable JsonElement p_26265_) {
      if (p_26265_ != null && !p_26265_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_26265_, "distance");
         MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromJson(jsonobject.get("x"));
         MinMaxBounds.Doubles minmaxbounds$doubles1 = MinMaxBounds.Doubles.fromJson(jsonobject.get("y"));
         MinMaxBounds.Doubles minmaxbounds$doubles2 = MinMaxBounds.Doubles.fromJson(jsonobject.get("z"));
         MinMaxBounds.Doubles minmaxbounds$doubles3 = MinMaxBounds.Doubles.fromJson(jsonobject.get("horizontal"));
         MinMaxBounds.Doubles minmaxbounds$doubles4 = MinMaxBounds.Doubles.fromJson(jsonobject.get("absolute"));
         return new DistancePredicate(minmaxbounds$doubles, minmaxbounds$doubles1, minmaxbounds$doubles2, minmaxbounds$doubles3, minmaxbounds$doubles4);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("x", this.x.serializeToJson());
         jsonobject.add("y", this.y.serializeToJson());
         jsonobject.add("z", this.z.serializeToJson());
         jsonobject.add("horizontal", this.horizontal.serializeToJson());
         jsonobject.add("absolute", this.absolute.serializeToJson());
         return jsonobject;
      }
   }
}