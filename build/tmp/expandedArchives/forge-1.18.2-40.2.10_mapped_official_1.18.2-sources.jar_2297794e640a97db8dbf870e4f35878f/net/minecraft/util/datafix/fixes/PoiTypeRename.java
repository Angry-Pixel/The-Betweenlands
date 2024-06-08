package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;

public abstract class PoiTypeRename extends DataFix {
   public PoiTypeRename(Schema p_16695_, boolean p_16696_) {
      super(p_16695_, p_16696_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI rename", type, (p_16705_) -> {
            return (p_145605_) -> {
               return p_145605_.mapSecond(this::cap);
            };
         });
      }
   }

   private <T> Dynamic<T> cap(Dynamic<T> p_16700_) {
      return p_16700_.update("Sections", (p_16716_) -> {
         return p_16716_.updateMapValues((p_145603_) -> {
            return p_145603_.mapSecond((p_145611_) -> {
               return p_145611_.update("Records", (p_145613_) -> {
                  return DataFixUtils.orElse(this.renameRecords(p_145613_), p_145613_);
               });
            });
         });
      });
   }

   private <T> Optional<Dynamic<T>> renameRecords(Dynamic<T> p_16710_) {
      return p_16710_.asStreamOpt().map((p_16703_) -> {
         return p_16710_.createList(p_16703_.map((p_145607_) -> {
            return p_145607_.update("type", (p_145609_) -> {
               return DataFixUtils.orElse(p_145609_.asString().map(this::rename).map(p_145609_::createString).result(), p_145609_);
            });
         }));
      }).result();
   }

   protected abstract String rename(String p_16706_);
}