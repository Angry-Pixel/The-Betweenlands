package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_4 extends NamespacedSchema {
   public V1451_4(int p_17519_, Schema p_17520_) {
      super(p_17519_, p_17520_);
   }

   public void registerTypes(Schema p_17523_, Map<String, Supplier<TypeTemplate>> p_17524_, Map<String, Supplier<TypeTemplate>> p_17525_) {
      super.registerTypes(p_17523_, p_17524_, p_17525_);
      p_17523_.registerType(false, References.BLOCK_NAME, () -> {
         return DSL.constType(namespacedString());
      });
   }
}