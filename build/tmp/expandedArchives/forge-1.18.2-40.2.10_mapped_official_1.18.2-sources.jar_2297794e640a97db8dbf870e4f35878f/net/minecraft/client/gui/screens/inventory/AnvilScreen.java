package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnvilScreen extends ItemCombinerScreen<AnvilMenu> {
   private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
   private static final Component TOO_EXPENSIVE_TEXT = new TranslatableComponent("container.repair.expensive");
   private EditBox name;
   private final Player player;

   public AnvilScreen(AnvilMenu p_97874_, Inventory p_97875_, Component p_97876_) {
      super(p_97874_, p_97875_, p_97876_, ANVIL_LOCATION);
      this.player = p_97875_.player;
      this.titleLabelX = 60;
   }

   public void containerTick() {
      super.containerTick();
      this.name.tick();
   }

   protected void subInit() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.name = new EditBox(this.font, i + 62, j + 24, 103, 12, new TranslatableComponent("container.repair"));
      this.name.setCanLoseFocus(false);
      this.name.setTextColor(-1);
      this.name.setTextColorUneditable(-1);
      this.name.setBordered(false);
      this.name.setMaxLength(50);
      this.name.setResponder(this::onNameChanged);
      this.name.setValue("");
      this.addWidget(this.name);
      this.setInitialFocus(this.name);
      this.name.setEditable(false);
   }

   public void resize(Minecraft p_97886_, int p_97887_, int p_97888_) {
      String s = this.name.getValue();
      this.init(p_97886_, p_97887_, p_97888_);
      this.name.setValue(s);
   }

   public void removed() {
      super.removed();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_97878_, int p_97879_, int p_97880_) {
      if (p_97878_ == 256) {
         this.minecraft.player.closeContainer();
      }

      return !this.name.keyPressed(p_97878_, p_97879_, p_97880_) && !this.name.canConsumeInput() ? super.keyPressed(p_97878_, p_97879_, p_97880_) : true;
   }

   private void onNameChanged(String p_97899_) {
      if (!p_97899_.isEmpty()) {
         String s = p_97899_;
         Slot slot = this.menu.getSlot(0);
         if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && p_97899_.equals(slot.getItem().getHoverName().getString())) {
            s = "";
         }

         this.menu.setItemName(s);
         this.minecraft.player.connection.send(new ServerboundRenameItemPacket(s));
      }
   }

   protected void renderLabels(PoseStack p_97890_, int p_97891_, int p_97892_) {
      RenderSystem.disableBlend();
      super.renderLabels(p_97890_, p_97891_, p_97892_);
      int i = this.menu.getCost();
      if (i > 0) {
         int j = 8453920;
         Component component;
         if (i >= 40 && !this.minecraft.player.getAbilities().instabuild) {
            component = TOO_EXPENSIVE_TEXT;
            j = 16736352;
         } else if (!this.menu.getSlot(2).hasItem()) {
            component = null;
         } else {
            component = new TranslatableComponent("container.repair.cost", i);
            if (!this.menu.getSlot(2).mayPickup(this.player)) {
               j = 16736352;
            }
         }

         if (component != null) {
            int k = this.imageWidth - 8 - this.font.width(component) - 2;
            int l = 69;
            fill(p_97890_, k - 2, 67, this.imageWidth - 8, 79, 1325400064);
            this.font.drawShadow(p_97890_, component, (float)k, 69.0F, j);
         }
      }

   }

   public void renderFg(PoseStack p_97894_, int p_97895_, int p_97896_, float p_97897_) {
      this.name.render(p_97894_, p_97895_, p_97896_, p_97897_);
   }

   public void slotChanged(AbstractContainerMenu p_97882_, int p_97883_, ItemStack p_97884_) {
      if (p_97883_ == 0) {
         this.name.setValue(p_97884_.isEmpty() ? "" : p_97884_.getHoverName().getString());
         this.name.setEditable(!p_97884_.isEmpty());
         this.setFocused(this.name);
      }

   }
}