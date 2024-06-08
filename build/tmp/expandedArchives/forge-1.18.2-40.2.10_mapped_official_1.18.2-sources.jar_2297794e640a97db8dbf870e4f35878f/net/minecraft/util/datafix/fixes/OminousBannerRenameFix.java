package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class OminousBannerRenameFix extends DataFix {
   public OminousBannerRenameFix(Schema p_16597_, boolean p_16598_) {
      super(p_16597_, p_16598_);
   }

   private Dynamic<?> fixTag(Dynamic<?> p_16604_) {
      Optional<? extends Dynamic<?>> optional = p_16604_.get("display").result();
      if (optional.isPresent()) {
         Dynamic<?> dynamic = optional.get();
         Optional<String> optional1 = dynamic.get("Name").asString().result();
         if (optional1.isPresent()) {
            String s = optional1.get();
            s = s.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            dynamic = dynamic.set("Name", dynamic.createString(s));
         }

         return p_16604_.set("display", dynamic);
      } else {
         return p_16604_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("OminousBannerRenameFix", type, (p_16602_) -> {
         Optional<Pair<String, String>> optional = p_16602_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:white_banner")) {
            Optional<? extends Typed<?>> optional1 = p_16602_.getOptionalTyped(opticfinder1);
            if (optional1.isPresent()) {
               Typed<?> typed = optional1.get();
               Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
               return p_16602_.set(opticfinder1, typed.set(DSL.remainderFinder(), this.fixTag(dynamic)));
            }
         }

         return p_16602_;
      });
   }
}