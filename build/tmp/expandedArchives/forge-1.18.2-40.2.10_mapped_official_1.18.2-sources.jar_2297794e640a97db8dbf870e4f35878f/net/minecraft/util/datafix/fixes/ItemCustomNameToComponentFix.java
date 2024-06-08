package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ItemCustomNameToComponentFix extends DataFix {
   public ItemCustomNameToComponentFix(Schema p_15927_, boolean p_15928_) {
      super(p_15927_, p_15928_);
   }

   private Dynamic<?> fixTag(Dynamic<?> p_15935_) {
      Optional<? extends Dynamic<?>> optional = p_15935_.get("display").result();
      if (optional.isPresent()) {
         Dynamic<?> dynamic = optional.get();
         Optional<String> optional1 = dynamic.get("Name").asString().result();
         if (optional1.isPresent()) {
            dynamic = dynamic.set("Name", dynamic.createString(Component.Serializer.toJson(new TextComponent(optional1.get()))));
         } else {
            Optional<String> optional2 = dynamic.get("LocName").asString().result();
            if (optional2.isPresent()) {
               dynamic = dynamic.set("Name", dynamic.createString(Component.Serializer.toJson(new TranslatableComponent(optional2.get()))));
               dynamic = dynamic.remove("LocName");
            }
         }

         return p_15935_.set("display", dynamic);
      } else {
         return p_15935_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> opticfinder = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", type, (p_15931_) -> {
         return p_15931_.updateTyped(opticfinder, (p_145384_) -> {
            return p_145384_.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}