package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ObjectiveRenderTypeFix extends DataFix {
   public ObjectiveRenderTypeFix(Schema p_16536_, boolean p_16537_) {
      super(p_16536_, p_16537_);
   }

   private static ObjectiveCriteria.RenderType getRenderType(String p_16545_) {
      return p_16545_.equals("health") ? ObjectiveCriteria.RenderType.HEARTS : ObjectiveCriteria.RenderType.INTEGER;
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.OBJECTIVE);
      return this.fixTypeEverywhereTyped("ObjectiveRenderTypeFix", type, (p_181041_) -> {
         return p_181041_.update(DSL.remainderFinder(), (p_145565_) -> {
            Optional<String> optional = p_145565_.get("RenderType").asString().result();
            if (!optional.isPresent()) {
               String s = p_145565_.get("CriteriaName").asString("");
               ObjectiveCriteria.RenderType objectivecriteria$rendertype = getRenderType(s);
               return p_145565_.set("RenderType", p_145565_.createString(objectivecriteria$rendertype.getId()));
            } else {
               return p_145565_;
            }
         });
      });
   }
}