package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class TreeNodePosition {
   private final Advancement advancement;
   @Nullable
   private final TreeNodePosition parent;
   @Nullable
   private final TreeNodePosition previousSibling;
   private final int childIndex;
   private final List<TreeNodePosition> children = Lists.newArrayList();
   private TreeNodePosition ancestor;
   @Nullable
   private TreeNodePosition thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public TreeNodePosition(Advancement p_16567_, @Nullable TreeNodePosition p_16568_, @Nullable TreeNodePosition p_16569_, int p_16570_, int p_16571_) {
      if (p_16567_.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.advancement = p_16567_;
         this.parent = p_16568_;
         this.previousSibling = p_16569_;
         this.childIndex = p_16570_;
         this.ancestor = this;
         this.x = p_16571_;
         this.y = -1.0F;
         TreeNodePosition treenodeposition = null;

         for(Advancement advancement : p_16567_.getChildren()) {
            treenodeposition = this.addChild(advancement, treenodeposition);
         }

      }
   }

   @Nullable
   private TreeNodePosition addChild(Advancement p_16590_, @Nullable TreeNodePosition p_16591_) {
      if (p_16590_.getDisplay() != null) {
         p_16591_ = new TreeNodePosition(p_16590_, this, p_16591_, this.children.size() + 1, this.x + 1);
         this.children.add(p_16591_);
      } else {
         for(Advancement advancement : p_16590_.getChildren()) {
            p_16591_ = this.addChild(advancement, p_16591_);
         }
      }

      return p_16591_;
   }

   private void firstWalk() {
      if (this.children.isEmpty()) {
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }

      } else {
         TreeNodePosition treenodeposition = null;

         for(TreeNodePosition treenodeposition1 : this.children) {
            treenodeposition1.firstWalk();
            treenodeposition = treenodeposition1.apportion(treenodeposition == null ? treenodeposition1 : treenodeposition);
         }

         this.executeShifts();
         float f = ((this.children.get(0)).y + (this.children.get(this.children.size() - 1)).y) / 2.0F;
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
            this.mod = this.y - f;
         } else {
            this.y = f;
         }

      }
   }

   private float secondWalk(float p_16576_, int p_16577_, float p_16578_) {
      this.y += p_16576_;
      this.x = p_16577_;
      if (this.y < p_16578_) {
         p_16578_ = this.y;
      }

      for(TreeNodePosition treenodeposition : this.children) {
         p_16578_ = treenodeposition.secondWalk(p_16576_ + this.mod, p_16577_ + 1, p_16578_);
      }

      return p_16578_;
   }

   private void thirdWalk(float p_16574_) {
      this.y += p_16574_;

      for(TreeNodePosition treenodeposition : this.children) {
         treenodeposition.thirdWalk(p_16574_);
      }

   }

   private void executeShifts() {
      float f = 0.0F;
      float f1 = 0.0F;

      for(int i = this.children.size() - 1; i >= 0; --i) {
         TreeNodePosition treenodeposition = this.children.get(i);
         treenodeposition.y += f;
         treenodeposition.mod += f;
         f1 += treenodeposition.change;
         f += treenodeposition.shift + f1;
      }

   }

   @Nullable
   private TreeNodePosition previousOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? this.children.get(0) : null;
      }
   }

   @Nullable
   private TreeNodePosition nextOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? this.children.get(this.children.size() - 1) : null;
      }
   }

   private TreeNodePosition apportion(TreeNodePosition p_16580_) {
      if (this.previousSibling == null) {
         return p_16580_;
      } else {
         TreeNodePosition treenodeposition = this;
         TreeNodePosition treenodeposition1 = this;
         TreeNodePosition treenodeposition2 = this.previousSibling;
         TreeNodePosition treenodeposition3 = this.parent.children.get(0);
         float f = this.mod;
         float f1 = this.mod;
         float f2 = treenodeposition2.mod;

         float f3;
         for(f3 = treenodeposition3.mod; treenodeposition2.nextOrThread() != null && treenodeposition.previousOrThread() != null; f1 += treenodeposition1.mod) {
            treenodeposition2 = treenodeposition2.nextOrThread();
            treenodeposition = treenodeposition.previousOrThread();
            treenodeposition3 = treenodeposition3.previousOrThread();
            treenodeposition1 = treenodeposition1.nextOrThread();
            treenodeposition1.ancestor = this;
            float f4 = treenodeposition2.y + f2 - (treenodeposition.y + f) + 1.0F;
            if (f4 > 0.0F) {
               treenodeposition2.getAncestor(this, p_16580_).moveSubtree(this, f4);
               f += f4;
               f1 += f4;
            }

            f2 += treenodeposition2.mod;
            f += treenodeposition.mod;
            f3 += treenodeposition3.mod;
         }

         if (treenodeposition2.nextOrThread() != null && treenodeposition1.nextOrThread() == null) {
            treenodeposition1.thread = treenodeposition2.nextOrThread();
            treenodeposition1.mod += f2 - f1;
         } else {
            if (treenodeposition.previousOrThread() != null && treenodeposition3.previousOrThread() == null) {
               treenodeposition3.thread = treenodeposition.previousOrThread();
               treenodeposition3.mod += f - f3;
            }

            p_16580_ = this;
         }

         return p_16580_;
      }
   }

   private void moveSubtree(TreeNodePosition p_16582_, float p_16583_) {
      float f = (float)(p_16582_.childIndex - this.childIndex);
      if (f != 0.0F) {
         p_16582_.change -= p_16583_ / f;
         this.change += p_16583_ / f;
      }

      p_16582_.shift += p_16583_;
      p_16582_.y += p_16583_;
      p_16582_.mod += p_16583_;
   }

   private TreeNodePosition getAncestor(TreeNodePosition p_16585_, TreeNodePosition p_16586_) {
      return this.ancestor != null && p_16585_.parent.children.contains(this.ancestor) ? this.ancestor : p_16586_;
   }

   private void finalizePosition() {
      if (this.advancement.getDisplay() != null) {
         this.advancement.getDisplay().setLocation((float)this.x, this.y);
      }

      if (!this.children.isEmpty()) {
         for(TreeNodePosition treenodeposition : this.children) {
            treenodeposition.finalizePosition();
         }
      }

   }

   public static void run(Advancement p_16588_) {
      if (p_16588_.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         TreeNodePosition treenodeposition = new TreeNodePosition(p_16588_, (TreeNodePosition)null, (TreeNodePosition)null, 1, 0);
         treenodeposition.firstWalk();
         float f = treenodeposition.secondWalk(0.0F, 0, treenodeposition.y);
         if (f < 0.0F) {
            treenodeposition.thirdWalk(-f);
         }

         treenodeposition.finalizePosition();
      }
   }
}