package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;

public class ChunkStatusFix2 extends DataFix {
   private static final Map<String, String> RENAMES_AND_DOWNGRADES = ImmutableMap.<String, String>builder().put("structure_references", "empty").put("biomes", "empty").put("base", "surface").put("carved", "carvers").put("liquid_carved", "liquid_carvers").put("decorated", "features").put("lighted", "light").put("mobs_spawned", "spawn").put("finalized", "heightmaps").put("fullchunk", "full").build();

   public ChunkStatusFix2(Schema p_15258_, boolean p_15259_) {
      super(p_15258_, p_15259_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);
      return this.fixTypeEverywhereTyped("ChunkStatusFix2", type, this.getOutputSchema().getType(References.CHUNK), (p_15262_) -> {
         return p_15262_.updateTyped(opticfinder, (p_145232_) -> {
            Dynamic<?> dynamic = p_145232_.get(DSL.remainderFinder());
            String s = dynamic.get("Status").asString("empty");
            String s1 = RENAMES_AND_DOWNGRADES.getOrDefault(s, "empty");
            return Objects.equals(s, s1) ? p_145232_ : p_145232_.set(DSL.remainderFinder(), dynamic.set("Status", dynamic.createString(s1)));
         });
      });
   }
}