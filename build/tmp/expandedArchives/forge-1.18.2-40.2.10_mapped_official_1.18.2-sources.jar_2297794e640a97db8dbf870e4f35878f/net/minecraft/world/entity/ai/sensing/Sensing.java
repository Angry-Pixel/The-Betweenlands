package net.minecraft.world.entity.ai.sensing;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class Sensing {
   private final Mob mob;
   private final IntSet seen = new IntOpenHashSet();
   private final IntSet unseen = new IntOpenHashSet();

   public Sensing(Mob p_26788_) {
      this.mob = p_26788_;
   }

   public void tick() {
      this.seen.clear();
      this.unseen.clear();
   }

   public boolean hasLineOfSight(Entity p_148307_) {
      int i = p_148307_.getId();
      if (this.seen.contains(i)) {
         return true;
      } else if (this.unseen.contains(i)) {
         return false;
      } else {
         this.mob.level.getProfiler().push("hasLineOfSight");
         boolean flag = this.mob.hasLineOfSight(p_148307_);
         this.mob.level.getProfiler().pop();
         if (flag) {
            this.seen.add(i);
         } else {
            this.unseen.add(i);
         }

         return flag;
      }
   }
}