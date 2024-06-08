package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.slf4j.Logger;

public class LevelUUIDFix extends AbstractUUIDFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public LevelUUIDFix(Schema p_16360_) {
      super(p_16360_, References.LEVEL);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LevelUUIDFix", this.getInputSchema().getType(this.typeReference), (p_16362_) -> {
         return p_16362_.updateTyped(DSL.remainderFinder(), (p_145496_) -> {
            return p_145496_.update(DSL.remainderFinder(), (p_145510_) -> {
               p_145510_ = this.updateCustomBossEvents(p_145510_);
               p_145510_ = this.updateDragonFight(p_145510_);
               return this.updateWanderingTrader(p_145510_);
            });
         });
      });
   }

   private Dynamic<?> updateWanderingTrader(Dynamic<?> p_16373_) {
      return replaceUUIDString(p_16373_, "WanderingTraderId", "WanderingTraderId").orElse(p_16373_);
   }

   private Dynamic<?> updateDragonFight(Dynamic<?> p_16375_) {
      return p_16375_.update("DimensionData", (p_16387_) -> {
         return p_16387_.updateMapValues((p_145498_) -> {
            return p_145498_.mapSecond((p_145506_) -> {
               return p_145506_.update("DragonFight", (p_145508_) -> {
                  return replaceUUIDLeastMost(p_145508_, "DragonUUID", "Dragon").orElse(p_145508_);
               });
            });
         });
      });
   }

   private Dynamic<?> updateCustomBossEvents(Dynamic<?> p_16377_) {
      return p_16377_.update("CustomBossEvents", (p_16379_) -> {
         return p_16379_.updateMapValues((p_145491_) -> {
            return p_145491_.mapSecond((p_145500_) -> {
               return p_145500_.update("Players", (p_145494_) -> {
                  return p_145500_.createList(p_145494_.asStream().map((p_145502_) -> {
                     return createUUIDFromML(p_145502_).orElseGet(() -> {
                        LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
                        return p_145502_;
                     });
                  }));
               });
            });
         });
      });
   }
}