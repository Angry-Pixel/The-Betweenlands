package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class ForcePoiRebuild extends DataFix {
   public ForcePoiRebuild(Schema p_15821_, boolean p_15822_) {
      super(p_15821_, p_15822_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI rebuild", type, (p_15828_) -> {
            return (p_145354_) -> {
               return p_145354_.mapSecond(ForcePoiRebuild::cap);
            };
         });
      }
   }

   private static <T> Dynamic<T> cap(Dynamic<T> p_15826_) {
      return p_15826_.update("Sections", (p_15832_) -> {
         return p_15832_.updateMapValues((p_145352_) -> {
            return p_145352_.mapSecond((p_145356_) -> {
               return p_145356_.remove("Valid");
            });
         });
      });
   }
}