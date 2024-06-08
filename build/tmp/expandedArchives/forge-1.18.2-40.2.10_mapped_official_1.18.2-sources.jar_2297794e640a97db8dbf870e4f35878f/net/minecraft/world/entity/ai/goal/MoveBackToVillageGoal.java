package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class MoveBackToVillageGoal extends RandomStrollGoal {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;

   public MoveBackToVillageGoal(PathfinderMob p_25568_, double p_25569_, boolean p_25570_) {
      super(p_25568_, p_25569_, 10, p_25570_);
   }

   public boolean canUse() {
      ServerLevel serverlevel = (ServerLevel)this.mob.level;
      BlockPos blockpos = this.mob.blockPosition();
      return serverlevel.isVillage(blockpos) ? false : super.canUse();
   }

   @Nullable
   protected Vec3 getPosition() {
      ServerLevel serverlevel = (ServerLevel)this.mob.level;
      BlockPos blockpos = this.mob.blockPosition();
      SectionPos sectionpos = SectionPos.of(blockpos);
      SectionPos sectionpos1 = BehaviorUtils.findSectionClosestToVillage(serverlevel, sectionpos, 2);
      return sectionpos1 != sectionpos ? DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(sectionpos1.center()), (double)((float)Math.PI / 2F)) : null;
   }
}