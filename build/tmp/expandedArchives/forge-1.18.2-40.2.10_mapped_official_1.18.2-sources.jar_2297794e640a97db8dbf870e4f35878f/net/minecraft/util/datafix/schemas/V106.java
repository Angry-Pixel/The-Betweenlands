package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V106 extends Schema {
   public V106(int p_17377_, Schema p_17378_) {
      super(p_17377_, p_17378_);
   }

   public void registerTypes(Schema p_17382_, Map<String, Supplier<TypeTemplate>> p_17383_, Map<String, Supplier<TypeTemplate>> p_17384_) {
      super.registerTypes(p_17382_, p_17383_, p_17384_);
      p_17382_.registerType(true, References.UNTAGGED_SPAWNER, () -> {
         return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", References.ENTITY_TREE.in(p_17382_))), "SpawnData", References.ENTITY_TREE.in(p_17382_));
      });
   }
}