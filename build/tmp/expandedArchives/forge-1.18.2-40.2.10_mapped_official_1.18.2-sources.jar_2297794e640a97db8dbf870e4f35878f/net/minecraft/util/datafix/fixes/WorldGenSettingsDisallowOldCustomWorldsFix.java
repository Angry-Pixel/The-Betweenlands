package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class WorldGenSettingsDisallowOldCustomWorldsFix extends DataFix {
   public WorldGenSettingsDisallowOldCustomWorldsFix(Schema p_185157_) {
      super(p_185157_, false);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder<?> opticfinder = type.findField("dimensions");
      return this.fixTypeEverywhereTyped("WorldGenSettingsDisallowOldCustomWorldsFix_" + this.getOutputSchema().getVersionKey(), type, (p_185160_) -> {
         return p_185160_.updateTyped(opticfinder, (p_185162_) -> {
            p_185162_.write().map((p_185164_) -> {
               return p_185164_.getMapValues().map((p_185169_) -> {
                  p_185169_.forEach((p_185166_, p_185167_) -> {
                     if (p_185167_.get("type").asString().result().isEmpty()) {
                        throw new IllegalStateException("Unable load old custom worlds.");
                     }
                  });
                  return p_185169_;
               });
            });
            return p_185162_;
         });
      });
   }
}