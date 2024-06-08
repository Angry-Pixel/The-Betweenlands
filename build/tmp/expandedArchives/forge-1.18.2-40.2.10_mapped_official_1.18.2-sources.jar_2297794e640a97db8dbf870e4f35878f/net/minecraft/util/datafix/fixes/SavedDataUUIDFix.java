package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class SavedDataUUIDFix extends AbstractUUIDFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public SavedDataUUIDFix(Schema p_16863_) {
      super(p_16863_, References.SAVED_DATA);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.typeReference), (p_16865_) -> {
         return p_16865_.updateTyped(p_16865_.getType().findField("data"), (p_145672_) -> {
            return p_145672_.update(DSL.remainderFinder(), (p_145674_) -> {
               return p_145674_.update("Raids", (p_145676_) -> {
                  return p_145676_.createList(p_145676_.asStream().map((p_145678_) -> {
                     return p_145678_.update("HeroesOfTheVillage", (p_145680_) -> {
                        return p_145680_.createList(p_145680_.asStream().map((p_145682_) -> {
                           return createUUIDFromLongs(p_145682_, "UUIDMost", "UUIDLeast").orElseGet(() -> {
                              LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
                              return p_145682_;
                           });
                        }));
                     });
                  }));
               });
            });
         });
      });
   }
}