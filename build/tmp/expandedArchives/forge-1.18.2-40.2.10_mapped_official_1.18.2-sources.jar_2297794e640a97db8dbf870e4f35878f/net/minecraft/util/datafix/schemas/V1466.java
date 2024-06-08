package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1466 extends NamespacedSchema {
   public V1466(int p_17685_, Schema p_17686_) {
      super(p_17685_, p_17686_);
   }

   public void registerTypes(Schema p_17694_, Map<String, Supplier<TypeTemplate>> p_17695_, Map<String, Supplier<TypeTemplate>> p_17696_) {
      super.registerTypes(p_17694_, p_17695_, p_17696_);
      p_17694_.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(p_17694_)), "TileEntities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(p_17694_), DSL.remainder())), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(p_17694_))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(References.BLOCK_STATE.in(p_17694_)))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(References.STRUCTURE_FEATURE.in(p_17694_)))));
      });
      p_17694_.registerType(false, References.STRUCTURE_FEATURE, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", References.BLOCK_STATE.in(p_17694_), "CB", References.BLOCK_STATE.in(p_17694_), "CC", References.BLOCK_STATE.in(p_17694_), "CD", References.BLOCK_STATE.in(p_17694_))), "biome", References.BIOME.in(p_17694_));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17692_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17692_);
      map.put("DUMMY", DSL::remainder);
      return map;
   }
}