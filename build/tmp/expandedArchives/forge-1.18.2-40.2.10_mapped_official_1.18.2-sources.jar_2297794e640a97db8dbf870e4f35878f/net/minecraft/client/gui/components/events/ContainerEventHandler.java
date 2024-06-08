package net.minecraft.client.gui.components.events;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ContainerEventHandler extends GuiEventListener {
   List<? extends GuiEventListener> children();

   default Optional<GuiEventListener> getChildAt(double p_94730_, double p_94731_) {
      for(GuiEventListener guieventlistener : this.children()) {
         if (guieventlistener.isMouseOver(p_94730_, p_94731_)) {
            return Optional.of(guieventlistener);
         }
      }

      return Optional.empty();
   }

   default boolean mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
      for(GuiEventListener guieventlistener : this.children()) {
         if (guieventlistener.mouseClicked(p_94695_, p_94696_, p_94697_)) {
            this.setFocused(guieventlistener);
            if (p_94697_ == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   default boolean mouseReleased(double p_94722_, double p_94723_, int p_94724_) {
      this.setDragging(false);
      return this.getChildAt(p_94722_, p_94723_).filter((p_94708_) -> {
         return p_94708_.mouseReleased(p_94722_, p_94723_, p_94724_);
      }).isPresent();
   }

   default boolean mouseDragged(double p_94699_, double p_94700_, int p_94701_, double p_94702_, double p_94703_) {
      return this.getFocused() != null && this.isDragging() && p_94701_ == 0 ? this.getFocused().mouseDragged(p_94699_, p_94700_, p_94701_, p_94702_, p_94703_) : false;
   }

   boolean isDragging();

   void setDragging(boolean p_94720_);

   default boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_) {
      return this.getChildAt(p_94686_, p_94687_).filter((p_94693_) -> {
         return p_94693_.mouseScrolled(p_94686_, p_94687_, p_94688_);
      }).isPresent();
   }

   default boolean keyPressed(int p_94710_, int p_94711_, int p_94712_) {
      return this.getFocused() != null && this.getFocused().keyPressed(p_94710_, p_94711_, p_94712_);
   }

   default boolean keyReleased(int p_94715_, int p_94716_, int p_94717_) {
      return this.getFocused() != null && this.getFocused().keyReleased(p_94715_, p_94716_, p_94717_);
   }

   default boolean charTyped(char p_94683_, int p_94684_) {
      return this.getFocused() != null && this.getFocused().charTyped(p_94683_, p_94684_);
   }

   @Nullable
   GuiEventListener getFocused();

   void setFocused(@Nullable GuiEventListener p_94713_);

   default void setInitialFocus(@Nullable GuiEventListener p_94719_) {
      this.setFocused(p_94719_);
      p_94719_.changeFocus(true);
   }

   default void magicalSpecialHackyFocus(@Nullable GuiEventListener p_94726_) {
      this.setFocused(p_94726_);
   }

   default boolean changeFocus(boolean p_94728_) {
      GuiEventListener guieventlistener = this.getFocused();
      boolean flag = guieventlistener != null;
      if (flag && guieventlistener.changeFocus(p_94728_)) {
         return true;
      } else {
         List<? extends GuiEventListener> list = this.children();
         int j = list.indexOf(guieventlistener);
         int i;
         if (flag && j >= 0) {
            i = j + (p_94728_ ? 1 : 0);
         } else if (p_94728_) {
            i = 0;
         } else {
            i = list.size();
         }

         ListIterator<? extends GuiEventListener> listiterator = list.listIterator(i);
         BooleanSupplier booleansupplier = p_94728_ ? listiterator::hasNext : listiterator::hasPrevious;
         Supplier<? extends GuiEventListener> supplier = p_94728_ ? listiterator::next : listiterator::previous;

         while(booleansupplier.getAsBoolean()) {
            GuiEventListener guieventlistener1 = supplier.get();
            if (guieventlistener1.changeFocus(p_94728_)) {
               this.setFocused(guieventlistener1);
               return true;
            }
         }

         this.setFocused((GuiEventListener)null);
         return false;
      }
   }
}