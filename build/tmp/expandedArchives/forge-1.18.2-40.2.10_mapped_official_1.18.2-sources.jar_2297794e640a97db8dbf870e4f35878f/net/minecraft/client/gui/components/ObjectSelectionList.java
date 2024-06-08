package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   private static final Component USAGE_NARRATION = new TranslatableComponent("narration.selection.usage");
   private boolean inFocus;

   public ObjectSelectionList(Minecraft p_94442_, int p_94443_, int p_94444_, int p_94445_, int p_94446_, int p_94447_) {
      super(p_94442_, p_94443_, p_94444_, p_94445_, p_94446_, p_94447_);
   }

   public boolean changeFocus(boolean p_94449_) {
      if (!this.inFocus && this.getItemCount() == 0) {
         return false;
      } else {
         this.inFocus = !this.inFocus;
         if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(AbstractSelectionList.SelectionDirection.DOWN);
         } else if (this.inFocus && this.getSelected() != null) {
            this.refreshSelection();
         }

         return this.inFocus;
      }
   }

   public void updateNarration(NarrationElementOutput p_169042_) {
      E e = this.getHovered();
      if (e != null) {
         this.narrateListElementPosition(p_169042_.nest(), e);
         e.updateNarration(p_169042_);
      } else {
         E e1 = this.getSelected();
         if (e1 != null) {
            this.narrateListElementPosition(p_169042_.nest(), e1);
            e1.updateNarration(p_169042_);
         }
      }

      if (this.isFocused()) {
         p_169042_.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry<E extends ObjectSelectionList.Entry<E>> extends AbstractSelectionList.Entry<E> implements NarrationSupplier {
      public boolean changeFocus(boolean p_94452_) {
         return false;
      }

      public abstract Component getNarration();

      public void updateNarration(NarrationElementOutput p_169044_) {
         p_169044_.add(NarratedElementType.TITLE, this.getNarration());
      }
   }
}