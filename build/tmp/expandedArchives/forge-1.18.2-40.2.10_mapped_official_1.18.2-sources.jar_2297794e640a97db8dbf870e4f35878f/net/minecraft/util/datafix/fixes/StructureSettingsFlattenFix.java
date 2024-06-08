package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public class StructureSettingsFlattenFix extends DataFix {
   public StructureSettingsFlattenFix(Schema p_204000_) {
      super(p_204000_, false);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder<?> opticfinder = type.findField("dimensions");
      return this.fixTypeEverywhereTyped("StructureSettingsFlatten", type, (p_204003_) -> {
         return p_204003_.updateTyped(opticfinder, (p_204016_) -> {
            Dynamic<?> dynamic = p_204016_.write().result().orElseThrow();
            Dynamic<?> dynamic1 = dynamic.updateMapValues(StructureSettingsFlattenFix::fixDimension);
            return opticfinder.type().readTyped(dynamic1).result().orElseThrow().getFirst();
         });
      });
   }

   private static Pair<Dynamic<?>, Dynamic<?>> fixDimension(Pair<Dynamic<?>, Dynamic<?>> p_204005_) {
      Dynamic<?> dynamic = p_204005_.getSecond();
      return Pair.of(p_204005_.getFirst(), dynamic.update("generator", (p_204018_) -> {
         return p_204018_.update("settings", (p_204020_) -> {
            return p_204020_.update("structures", StructureSettingsFlattenFix::fixStructures);
         });
      }));
   }

   private static Dynamic<?> fixStructures(Dynamic<?> p_204007_) {
      Dynamic<?> dynamic = p_204007_.get("structures").orElseEmptyMap().updateMapValues((p_204010_) -> {
         return p_204010_.mapSecond((p_204013_) -> {
            return p_204013_.set("type", p_204007_.createString("minecraft:random_spread"));
         });
      });
      return DataFixUtils.orElse(p_204007_.get("stronghold").result().map((p_207675_) -> {
         return dynamic.set("minecraft:stronghold", p_207675_.set("type", p_204007_.createString("minecraft:concentric_rings")));
      }), dynamic);
   }
}