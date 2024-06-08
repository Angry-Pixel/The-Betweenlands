package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class PlayerUUIDFix extends AbstractUUIDFix {
   public PlayerUUIDFix(Schema p_16684_) {
      super(p_16684_, References.PLAYER);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.typeReference), (p_16686_) -> {
         OpticFinder<?> opticfinder = p_16686_.getType().findField("RootVehicle");
         return p_16686_.updateTyped(opticfinder, opticfinder.type(), (p_145597_) -> {
            return p_145597_.update(DSL.remainderFinder(), (p_145601_) -> {
               return replaceUUIDLeastMost(p_145601_, "Attach", "Attach").orElse(p_145601_);
            });
         }).update(DSL.remainderFinder(), (p_145599_) -> {
            return EntityUUIDFix.updateEntityUUID(EntityUUIDFix.updateLivingEntity(p_145599_));
         });
      });
   }
}