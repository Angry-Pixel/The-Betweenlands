package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class GossipUUIDFix extends NamedEntityFix {
   public GossipUUIDFix(Schema p_15878_, String p_15879_) {
      super(p_15878_, false, "Gossip for for " + p_15879_, References.ENTITY, p_15879_);
   }

   protected Typed<?> fix(Typed<?> p_15881_) {
      return p_15881_.update(DSL.remainderFinder(), (p_15883_) -> {
         return p_15883_.update("Gossips", (p_145376_) -> {
            return DataFixUtils.orElse(p_145376_.asStreamOpt().result().map((p_145374_) -> {
               return p_145374_.map((p_145378_) -> {
                  return AbstractUUIDFix.replaceUUIDLeastMost(p_145378_, "Target", "Target").orElse(p_145378_);
               });
            }).map(p_145376_::createList), p_145376_);
         });
      });
   }
}