package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1920 extends NamespacedSchema {
   public V1920(int p_17787_, Schema p_17788_) {
      super(p_17787_, p_17788_);
   }

   protected static void registerInventory(Schema p_17792_, Map<String, Supplier<TypeTemplate>> p_17793_, String p_17794_) {
      p_17792_.register(p_17793_, p_17794_, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_17792_)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17796_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17796_);
      registerInventory(p_17796_, map, "minecraft:campfire");
      return map;
   }
}