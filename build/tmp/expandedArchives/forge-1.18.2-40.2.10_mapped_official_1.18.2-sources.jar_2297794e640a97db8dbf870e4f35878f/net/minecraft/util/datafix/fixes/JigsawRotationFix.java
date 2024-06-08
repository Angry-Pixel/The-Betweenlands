package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;

public class JigsawRotationFix extends DataFix {
   private static final Map<String, String> RENAMES = ImmutableMap.<String, String>builder().put("down", "down_south").put("up", "up_north").put("north", "north_up").put("south", "south_up").put("west", "west_up").put("east", "east_up").build();

   public JigsawRotationFix(Schema p_16191_, boolean p_16192_) {
      super(p_16191_, p_16192_);
   }

   private static Dynamic<?> fix(Dynamic<?> p_16196_) {
      Optional<String> optional = p_16196_.get("Name").asString().result();
      return optional.equals(Optional.of("minecraft:jigsaw")) ? p_16196_.update("Properties", (p_16198_) -> {
         String s = p_16198_.get("facing").asString("north");
         return p_16198_.remove("facing").set("orientation", p_16198_.createString(RENAMES.getOrDefault(s, s)));
      }) : p_16196_;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("jigsaw_rotation_fix", this.getInputSchema().getType(References.BLOCK_STATE), (p_16194_) -> {
         return p_16194_.update(DSL.remainderFinder(), JigsawRotationFix::fix);
      });
   }
}