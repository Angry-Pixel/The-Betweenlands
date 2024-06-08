package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   private boolean hasFocus;

   public ContainerObjectSelectionList(Minecraft p_94010_, int p_94011_, int p_94012_, int p_94013_, int p_94014_, int p_94015_) {
      super(p_94010_, p_94011_, p_94012_, p_94013_, p_94014_, p_94015_);
   }

   public boolean changeFocus(boolean p_94017_) {
      this.hasFocus = super.changeFocus(p_94017_);
      if (this.hasFocus) {
         this.ensureVisible(this.getFocused());
      }

      return this.hasFocus;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.hasFocus ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
   }

   protected boolean isSelectedItem(int p_94019_) {
      return false;
   }

   public void updateNarration(NarrationElementOutput p_168851_) {
      E e = this.getHovered();
      if (e != null) {
         e.updateNarration(p_168851_.nest());
         this.narrateListElementPosition(p_168851_, e);
      } else {
         E e1 = this.getFocused();
         if (e1 != null) {
            e1.updateNarration(p_168851_.nest());
            this.narrateListElementPosition(p_168851_, e1);
         }
      }

      p_168851_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.component_list.usage"));
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList.Entry<E> implements ContainerEventHandler {
      @Nullable
      private GuiEventListener focused;
      @Nullable
      private NarratableEntry lastNarratable;
      private boolean dragging;

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(boolean p_94028_) {
         this.dragging = p_94028_;
      }

      public void setFocused(@Nullable GuiEventListener p_94024_) {
         this.focused = p_94024_;
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.focused;
      }

      public abstract List<? extends NarratableEntry> narratables();

      void updateNarration(NarrationElementOutput p_168855_) {
         List<? extends NarratableEntry> list = this.narratables();
         Screen.NarratableSearchResult screen$narratablesearchresult = Screen.findNarratableWidget(list, this.lastNarratable);
         if (screen$narratablesearchresult != null) {
            if (screen$narratablesearchresult.priority.isTerminal()) {
               this.lastNarratable = screen$narratablesearchresult.entry;
            }

            if (list.size() > 1) {
               p_168855_.add(NarratedElementType.POSITION, new TranslatableComponent("narrator.position.object_list", screen$narratablesearchresult.index + 1, list.size()));
               if (screen$narratablesearchresult.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                  p_168855_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.component_list.usage"));
               }
            }

            screen$narratablesearchresult.entry.updateNarration(p_168855_.nest());
         }

      }
   }
}