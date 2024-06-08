package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Giant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantZombieModel extends AbstractZombieModel<Giant> {
   public GiantZombieModel(ModelPart p_170576_) {
      super(p_170576_);
   }

   public boolean isAggressive(Giant p_102692_) {
      return false;
   }
}