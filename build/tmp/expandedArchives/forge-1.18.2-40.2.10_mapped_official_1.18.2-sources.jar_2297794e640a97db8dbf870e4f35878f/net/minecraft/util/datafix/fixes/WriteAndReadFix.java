package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public class WriteAndReadFix extends DataFix {
   private final String name;
   private final TypeReference type;

   public WriteAndReadFix(Schema p_17293_, String p_17294_, TypeReference p_17295_) {
      super(p_17293_, true);
      this.name = p_17294_;
      this.type = p_17295_;
   }

   protected TypeRewriteRule makeRule() {
      return this.writeAndRead(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type));
   }
}