package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_7 extends NamespacedSchema {
   public V1451_7(int p_17544_, Schema p_17545_) {
      super(p_17544_, p_17545_);
   }

   public void registerTypes(Schema p_17549_, Map<String, Supplier<TypeTemplate>> p_17550_, Map<String, Supplier<TypeTemplate>> p_17551_) {
      super.registerTypes(p_17549_, p_17550_, p_17551_);
      p_17549_.registerType(false, References.STRUCTURE_FEATURE, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", References.BLOCK_STATE.in(p_17549_), "CB", References.BLOCK_STATE.in(p_17549_), "CC", References.BLOCK_STATE.in(p_17549_), "CD", References.BLOCK_STATE.in(p_17549_))));
      });
   }
}