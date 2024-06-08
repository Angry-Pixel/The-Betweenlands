package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityCatSplitFix extends SimpleEntityRenameFix {
   public EntityCatSplitFix(Schema p_15384_, boolean p_15385_) {
      super("EntityCatSplitFix", p_15384_, p_15385_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_15387_, Dynamic<?> p_15388_) {
      if (Objects.equals("minecraft:ocelot", p_15387_)) {
         int i = p_15388_.get("CatType").asInt(0);
         if (i == 0) {
            String s = p_15388_.get("Owner").asString("");
            String s1 = p_15388_.get("OwnerUUID").asString("");
            if (s.length() > 0 || s1.length() > 0) {
               p_15388_.set("Trusting", p_15388_.createBoolean(true));
            }
         } else if (i > 0 && i < 4) {
            p_15388_ = p_15388_.set("CatType", p_15388_.createInt(i));
            p_15388_ = p_15388_.set("OwnerUUID", p_15388_.createString(p_15388_.get("OwnerUUID").asString("")));
            return Pair.of("minecraft:cat", p_15388_);
         }
      }

      return Pair.of(p_15387_, p_15388_);
   }
}