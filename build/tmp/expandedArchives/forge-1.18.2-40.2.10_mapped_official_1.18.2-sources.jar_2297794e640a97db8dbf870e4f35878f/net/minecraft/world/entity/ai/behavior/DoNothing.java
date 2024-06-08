package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class DoNothing extends Behavior<LivingEntity> {
   public DoNothing(int p_22840_, int p_22841_) {
      super(ImmutableMap.of(), p_22840_, p_22841_);
   }

   protected boolean canStillUse(ServerLevel p_22843_, LivingEntity p_22844_, long p_22845_) {
      return true;
   }
}