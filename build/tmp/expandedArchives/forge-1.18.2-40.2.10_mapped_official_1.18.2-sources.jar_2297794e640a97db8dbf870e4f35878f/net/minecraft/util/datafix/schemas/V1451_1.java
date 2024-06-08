package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_1 extends NamespacedSchema {
   public V1451_1(int p_17427_, Schema p_17428_) {
      super(p_17427_, p_17428_);
   }

   public void registerTypes(Schema p_17432_, Map<String, Supplier<TypeTemplate>> p_17433_, Map<String, Supplier<TypeTemplate>> p_17434_) {
      super.registerTypes(p_17432_, p_17433_, p_17434_);
      p_17432_.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(p_17432_)), "TileEntities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(p_17432_), DSL.remainder())), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(p_17432_))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(References.BLOCK_STATE.in(p_17432_))))));
      });
   }
}