package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternScreen extends BookViewScreen implements MenuAccess<LecternMenu> {
   private final LecternMenu menu;
   private final ContainerListener listener = new ContainerListener() {
      public void slotChanged(AbstractContainerMenu p_99054_, int p_99055_, ItemStack p_99056_) {
         LecternScreen.this.bookChanged();
      }

      public void dataChanged(AbstractContainerMenu p_169772_, int p_169773_, int p_169774_) {
         if (p_169773_ == 0) {
            LecternScreen.this.pageChanged();
         }

      }
   };

   public LecternScreen(LecternMenu p_99020_, Inventory p_99021_, Component p_99022_) {
      this.menu = p_99020_;
   }

   public LecternMenu getMenu() {
      return this.menu;
   }

   protected void init() {
      super.init();
      this.menu.addSlotListener(this.listener);
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      super.onClose();
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this.listener);
   }

   protected void createMenuControls() {
      if (this.minecraft.player.mayBuild()) {
         this.addRenderableWidget(new Button(this.width / 2 - 100, 196, 98, 20, CommonComponents.GUI_DONE, (p_99033_) -> {
            this.onClose();
         }));
         this.addRenderableWidget(new Button(this.width / 2 + 2, 196, 98, 20, new TranslatableComponent("lectern.take_book"), (p_99024_) -> {
            this.sendButtonClick(3);
         }));
      } else {
         super.createMenuControls();
      }

   }

   protected void pageBack() {
      this.sendButtonClick(1);
   }

   protected void pageForward() {
      this.sendButtonClick(2);
   }

   protected boolean forcePage(int p_99031_) {
      if (p_99031_ != this.menu.getPage()) {
         this.sendButtonClick(100 + p_99031_);
         return true;
      } else {
         return false;
      }
   }

   private void sendButtonClick(int p_99037_) {
      this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, p_99037_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   void bookChanged() {
      ItemStack itemstack = this.menu.getBook();
      this.setBookAccess(BookViewScreen.BookAccess.fromItem(itemstack));
   }

   void pageChanged() {
      this.setPage(this.menu.getPage());
   }

   protected void closeScreen() {
      this.minecraft.player.closeContainer();
   }
}