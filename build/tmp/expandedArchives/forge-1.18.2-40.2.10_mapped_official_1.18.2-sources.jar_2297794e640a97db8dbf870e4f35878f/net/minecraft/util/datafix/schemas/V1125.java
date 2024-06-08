package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1125 extends NamespacedSchema {
   public V1125(int p_17391_, Schema p_17392_) {
      super(p_17391_, p_17392_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17398_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17398_);
      p_17398_.registerSimple(map, "minecraft:bed");
      return map;
   }

   public void registerTypes(Schema p_17400_, Map<String, Supplier<TypeTemplate>> p_17401_, Map<String, Supplier<TypeTemplate>> p_17402_) {
      super.registerTypes(p_17400_, p_17401_, p_17402_);
      p_17400_.registerType(false, References.ADVANCEMENTS, () -> {
         return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(References.BIOME.in(p_17400_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(p_17400_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(p_17400_), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(p_17400_), DSL.constType(DSL.string()))));
      });
      p_17400_.registerType(false, References.BIOME, () -> {
         return DSL.constType(namespacedString());
      });
      p_17400_.registerType(false, References.ENTITY_NAME, () -> {
         return DSL.constType(namespacedString());
      });
   }
}