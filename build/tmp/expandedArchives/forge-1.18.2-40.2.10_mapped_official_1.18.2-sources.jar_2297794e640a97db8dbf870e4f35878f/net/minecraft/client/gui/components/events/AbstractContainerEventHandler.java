package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractContainerEventHandler extends GuiComponent implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean isDragging;

   public final boolean isDragging() {
      return this.isDragging;
   }

   public final void setDragging(boolean p_94681_) {
      this.isDragging = p_94681_;
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable GuiEventListener p_94677_) {
      this.focused = p_94677_;
   }
}