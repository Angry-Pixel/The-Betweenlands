package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1906 extends NamespacedSchema {
   public V1906(int p_17768_, Schema p_17769_) {
      super(p_17768_, p_17769_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17780_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17780_);
      registerInventory(p_17780_, map, "minecraft:barrel");
      registerInventory(p_17780_, map, "minecraft:smoker");
      registerInventory(p_17780_, map, "minecraft:blast_furnace");
      p_17780_.register(map, "minecraft:lectern", (p_17774_) -> {
         return DSL.optionalFields("Book", References.ITEM_STACK.in(p_17780_));
      });
      p_17780_.registerSimple(map, "minecraft:bell");
      return map;
   }

   protected static void registerInventory(Schema p_17776_, Map<String, Supplier<TypeTemplate>> p_17777_, String p_17778_) {
      p_17776_.register(p_17777_, p_17778_, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_17776_)));
      });
   }
}