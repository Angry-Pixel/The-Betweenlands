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

public class ItemShulkerBoxColorFix extends DataFix {
   public static final String[] NAMES_BY_COLOR = new String[]{"minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

   public ItemShulkerBoxColorFix(Schema p_16023_, boolean p_16024_) {
      super(p_16023_, p_16024_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      OpticFinder<?> opticfinder2 = opticfinder1.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemShulkerBoxColorFix", type, (p_16029_) -> {
         Optional<Pair<String, String>> optional = p_16029_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:shulker_box")) {
            Optional<? extends Typed<?>> optional1 = p_16029_.getOptionalTyped(opticfinder1);
            if (optional1.isPresent()) {
               Typed<?> typed = optional1.get();
               Optional<? extends Typed<?>> optional2 = typed.getOptionalTyped(opticfinder2);
               if (optional2.isPresent()) {
                  Typed<?> typed1 = optional2.get();
                  Dynamic<?> dynamic = typed1.get(DSL.remainderFinder());
                  int i = dynamic.get("Color").asInt(0);
                  dynamic.remove("Color");
                  return p_16029_.set(opticfinder1, typed.set(opticfinder2, typed1.set(DSL.remainderFinder(), dynamic))).set(opticfinder, Pair.of(References.ITEM_NAME.typeName(), NAMES_BY_COLOR[i % 16]));
               }
            }
         }

         return p_16029_;
      });
   }
}