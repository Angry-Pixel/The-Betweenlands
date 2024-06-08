package net.minecraft.realms;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
   protected RealmsObjectSelectionList(int p_120745_, int p_120746_, int p_120747_, int p_120748_, int p_120749_) {
      super(Minecraft.getInstance(), p_120745_, p_120746_, p_120747_, p_120748_, p_120749_);
   }

   public void setSelectedItem(int p_120768_) {
      if (p_120768_ == -1) {
         this.setSelected((E)null);
      } else if (super.getItemCount() != 0) {
         this.setSelected(this.getEntry(p_120768_));
      }

   }

   public void selectItem(int p_120750_) {
      this.setSelectedItem(p_120750_);
   }

   public void itemClicked(int p_120751_, int p_120752_, double p_120753_, double p_120754_, int p_120755_) {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.getRowLeft() + this.getRowWidth();
   }

   public int getRowWidth() {
      return (int)((double)this.width * 0.6D);
   }

   public void replaceEntries(Collection<E> p_120759_) {
      super.replaceEntries(p_120759_);
   }

   public int getItemCount() {
      return super.getItemCount();
   }

   public int getRowTop(int p_120766_) {
      return super.getRowTop(p_120766_);
   }

   public int getRowLeft() {
      return super.getRowLeft();
   }

   public int addEntry(E p_120757_) {
      return super.addEntry(p_120757_);
   }

   public void clear() {
      this.clearEntries();
   }
}