package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class VillagerTradeFix extends NamedEntityFix {
   public VillagerTradeFix(Schema p_17116_, boolean p_17117_) {
      super(p_17116_, p_17117_, "Villager trade fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> p_17143_) {
      OpticFinder<?> opticfinder = p_17143_.getType().findField("Offers");
      OpticFinder<?> opticfinder1 = opticfinder.type().findField("Recipes");
      Type<?> type = opticfinder1.type();
      if (!(type instanceof ListType)) {
         throw new IllegalStateException("Recipes are expected to be a list.");
      } else {
         ListType<?> listtype = (ListType)type;
         Type<?> type1 = listtype.getElement();
         OpticFinder<?> opticfinder2 = DSL.typeFinder(type1);
         OpticFinder<?> opticfinder3 = type1.findField("buy");
         OpticFinder<?> opticfinder4 = type1.findField("buyB");
         OpticFinder<?> opticfinder5 = type1.findField("sell");
         OpticFinder<Pair<String, String>> opticfinder6 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
         Function<Typed<?>, Typed<?>> function = (p_17150_) -> {
            return this.updateItemStack(opticfinder6, p_17150_);
         };
         return p_17143_.updateTyped(opticfinder, (p_17125_) -> {
            return p_17125_.updateTyped(opticfinder1, (p_145782_) -> {
               return p_145782_.updateTyped(opticfinder2, (p_145788_) -> {
                  return p_145788_.updateTyped(opticfinder3, function).updateTyped(opticfinder4, function).updateTyped(opticfinder5, function);
               });
            });
         });
      }
   }

   private Typed<?> updateItemStack(OpticFinder<Pair<String, String>> p_17134_, Typed<?> p_17135_) {
      return p_17135_.update(p_17134_, (p_17145_) -> {
         return p_17145_.mapSecond((p_145790_) -> {
            return Objects.equals(p_145790_, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : p_145790_;
         });
      });
   }
}