package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2842 extends NamespacedSchema {
   public V2842(int p_185238_, Schema p_185239_) {
      super(p_185238_, p_185239_);
   }

   public void registerTypes(Schema p_185243_, Map<String, Supplier<TypeTemplate>> p_185244_, Map<String, Supplier<TypeTemplate>> p_185245_) {
      super.registerTypes(p_185243_, p_185244_, p_185245_);
      p_185243_.registerType(false, References.CHUNK, () -> {
         return DSL.optionalFields("entities", DSL.list(References.ENTITY_TREE.in(p_185243_)), "block_entities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(p_185243_), DSL.remainder())), "block_ticks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(p_185243_))), "sections", DSL.list(DSL.optionalFields("biomes", DSL.optionalFields("palette", DSL.list(References.BIOME.in(p_185243_))), "block_states", DSL.optionalFields("palette", DSL.list(References.BLOCK_STATE.in(p_185243_))))), "structures", DSL.optionalFields("starts", DSL.compoundList(References.STRUCTURE_FEATURE.in(p_185243_))));
      });
   }
}