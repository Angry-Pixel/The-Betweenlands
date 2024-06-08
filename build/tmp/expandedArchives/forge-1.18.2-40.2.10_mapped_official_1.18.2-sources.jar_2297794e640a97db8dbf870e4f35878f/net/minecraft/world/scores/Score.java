package net.minecraft.world.scores;

import java.util.Comparator;
import javax.annotation.Nullable;

public class Score {
   public static final Comparator<Score> SCORE_COMPARATOR = (p_83396_, p_83397_) -> {
      if (p_83396_.getScore() > p_83397_.getScore()) {
         return 1;
      } else {
         return p_83396_.getScore() < p_83397_.getScore() ? -1 : p_83397_.getOwner().compareToIgnoreCase(p_83396_.getOwner());
      }
   };
   private final Scoreboard scoreboard;
   @Nullable
   private final Objective objective;
   private final String owner;
   private int count;
   private boolean locked;
   private boolean forceUpdate;

   public Score(Scoreboard p_83389_, Objective p_83390_, String p_83391_) {
      this.scoreboard = p_83389_;
      this.objective = p_83390_;
      this.owner = p_83391_;
      this.locked = true;
      this.forceUpdate = true;
   }

   public void add(int p_83394_) {
      if (this.objective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.setScore(this.getScore() + p_83394_);
      }
   }

   public void increment() {
      this.add(1);
   }

   public int getScore() {
      return this.count;
   }

   public void reset() {
      this.setScore(0);
   }

   public void setScore(int p_83403_) {
      int i = this.count;
      this.count = p_83403_;
      if (i != p_83403_ || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreboard().onScoreChanged(this);
      }

   }

   @Nullable
   public Objective getObjective() {
      return this.objective;
   }

   public String getOwner() {
      return this.owner;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean p_83399_) {
      this.locked = p_83399_;
   }
}