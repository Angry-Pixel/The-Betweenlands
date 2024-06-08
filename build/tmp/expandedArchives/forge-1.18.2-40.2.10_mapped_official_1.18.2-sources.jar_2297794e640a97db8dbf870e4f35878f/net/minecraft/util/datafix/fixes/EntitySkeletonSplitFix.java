package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntitySkeletonSplitFix extends SimpleEntityRenameFix {
   public EntitySkeletonSplitFix(Schema p_15688_, boolean p_15689_) {
      super("EntitySkeletonSplitFix", p_15688_, p_15689_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_15691_, Dynamic<?> p_15692_) {
      if (Objects.equals(p_15691_, "Skeleton")) {
         int i = p_15692_.get("SkeletonType").asInt(0);
         if (i == 1) {
            p_15691_ = "WitherSkeleton";
         } else if (i == 2) {
            p_15691_ = "Stray";
         }
      }

      return Pair.of(p_15691_, p_15692_);
   }
}