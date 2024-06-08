package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;

public class PotentialCalculator {
   private final List<PotentialCalculator.PointCharge> charges = Lists.newArrayList();

   public void addCharge(BlockPos p_47193_, double p_47194_) {
      if (p_47194_ != 0.0D) {
         this.charges.add(new PotentialCalculator.PointCharge(p_47193_, p_47194_));
      }

   }

   public double getPotentialEnergyChange(BlockPos p_47196_, double p_47197_) {
      if (p_47197_ == 0.0D) {
         return 0.0D;
      } else {
         double d0 = 0.0D;

         for(PotentialCalculator.PointCharge potentialcalculator$pointcharge : this.charges) {
            d0 += potentialcalculator$pointcharge.getPotentialChange(p_47196_);
         }

         return d0 * p_47197_;
      }
   }

   static class PointCharge {
      private final BlockPos pos;
      private final double charge;

      public PointCharge(BlockPos p_47201_, double p_47202_) {
         this.pos = p_47201_;
         this.charge = p_47202_;
      }

      public double getPotentialChange(BlockPos p_47204_) {
         double d0 = this.pos.distSqr(p_47204_);
         return d0 == 0.0D ? Double.POSITIVE_INFINITY : this.charge / Math.sqrt(d0);
      }
   }
}