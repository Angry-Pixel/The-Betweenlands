package net.minecraft.client.model.geom.builders;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MaterialDefinition {
   final int xTexSize;
   final int yTexSize;

   public MaterialDefinition(int p_171572_, int p_171573_) {
      this.xTexSize = p_171572_;
      this.yTexSize = p_171573_;
   }
}