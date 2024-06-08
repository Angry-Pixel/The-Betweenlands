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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class TeamDisplayNameFix extends DataFix {
   public TeamDisplayNameFix(Schema p_17001_, boolean p_17002_) {
      super(p_17001_, p_17002_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(References.TEAM.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(References.TEAM))) {
         throw new IllegalStateException("Team type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("TeamDisplayNameFix", type, (p_17011_) -> {
            return (p_145726_) -> {
               return p_145726_.mapSecond((p_145728_) -> {
                  return p_145728_.update("DisplayName", (p_145731_) -> {
                     return DataFixUtils.orElse(p_145731_.asString().map((p_145733_) -> {
                        return Component.Serializer.toJson(new TextComponent(p_145733_));
                     }).map(p_145728_::createString).result(), p_145731_);
                  });
               });
            };
         });
      }
   }
}