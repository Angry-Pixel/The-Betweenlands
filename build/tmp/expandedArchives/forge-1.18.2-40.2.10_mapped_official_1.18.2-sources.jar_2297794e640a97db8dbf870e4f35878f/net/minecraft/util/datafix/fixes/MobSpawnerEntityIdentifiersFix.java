package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;

public class MobSpawnerEntityIdentifiersFix extends DataFix {
   public MobSpawnerEntityIdentifiersFix(Schema p_16451_, boolean p_16452_) {
      super(p_16451_, p_16452_);
   }

   private Dynamic<?> fix(Dynamic<?> p_16457_) {
      if (!"MobSpawner".equals(p_16457_.get("id").asString(""))) {
         return p_16457_;
      } else {
         Optional<String> optional = p_16457_.get("EntityId").asString().result();
         if (optional.isPresent()) {
            Dynamic<?> dynamic = DataFixUtils.orElse(p_16457_.get("SpawnData").result(), p_16457_.emptyMap());
            dynamic = dynamic.set("id", dynamic.createString(optional.get().isEmpty() ? "Pig" : optional.get()));
            p_16457_ = p_16457_.set("SpawnData", dynamic);
            p_16457_ = p_16457_.remove("EntityId");
         }

         Optional<? extends Stream<? extends Dynamic<?>>> optional1 = p_16457_.get("SpawnPotentials").asStreamOpt().result();
         if (optional1.isPresent()) {
            p_16457_ = p_16457_.set("SpawnPotentials", p_16457_.createList(optional1.get().map((p_16459_) -> {
               Optional<String> optional2 = p_16459_.get("Type").asString().result();
               if (optional2.isPresent()) {
                  Dynamic<?> dynamic1 = DataFixUtils.orElse(p_16459_.get("Properties").result(), p_16459_.emptyMap()).set("id", p_16459_.createString(optional2.get()));
                  return p_16459_.set("Entity", dynamic1).remove("Type").remove("Properties");
               } else {
                  return p_16459_;
               }
            })));
         }

         return p_16457_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(References.UNTAGGED_SPAWNER);
      return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(References.UNTAGGED_SPAWNER), type, (p_16455_) -> {
         Dynamic<?> dynamic = p_16455_.get(DSL.remainderFinder());
         dynamic = dynamic.set("id", dynamic.createString("MobSpawner"));
         DataResult<? extends Pair<? extends Typed<?>, ?>> dataresult = type.readTyped(this.fix(dynamic));
         return !dataresult.result().isPresent() ? p_16455_ : dataresult.result().get().getFirst();
      });
   }
}