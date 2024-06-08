package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2831 extends NamespacedSchema {
   public V2831(int p_185208_, Schema p_185209_) {
      super(p_185208_, p_185209_);
   }

   public void registerTypes(Schema p_185213_, Map<String, Supplier<TypeTemplate>> p_185214_, Map<String, Supplier<TypeTemplate>> p_185215_) {
      super.registerTypes(p_185213_, p_185214_, p_185215_);
      p_185213_.registerType(true, References.UNTAGGED_SPAWNER, () -> {
         return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("data", DSL.fields("entity", References.ENTITY_TREE.in(p_185213_)))), "SpawnData", DSL.fields("entity", References.ENTITY_TREE.in(p_185213_)));
      });
   }
}