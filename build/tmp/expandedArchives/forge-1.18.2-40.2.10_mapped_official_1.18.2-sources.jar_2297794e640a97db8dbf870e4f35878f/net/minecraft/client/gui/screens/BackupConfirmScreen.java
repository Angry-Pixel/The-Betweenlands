package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BackupConfirmScreen extends Screen {
   @Nullable
   private final Screen lastScreen;
   protected final BackupConfirmScreen.Listener listener;
   private final Component description;
   private final boolean promptForCacheErase;
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   protected int id;
   private Checkbox eraseCache;

   public BackupConfirmScreen(@Nullable Screen p_95543_, BackupConfirmScreen.Listener p_95544_, Component p_95545_, Component p_95546_, boolean p_95547_) {
      super(p_95545_);
      this.lastScreen = p_95543_;
      this.listener = p_95544_;
      this.description = p_95546_;
      this.promptForCacheErase = p_95547_;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.description, this.width - 50);
      int i = (this.message.getLineCount() + 1) * 9;
      this.addRenderableWidget(new Button(this.width / 2 - 155, 100 + i, 150, 20, new TranslatableComponent("selectWorld.backupJoinConfirmButton"), (p_95564_) -> {
         this.listener.proceed(true, this.eraseCache.selected());
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, new TranslatableComponent("selectWorld.backupJoinSkipButton"), (p_95562_) -> {
         this.listener.proceed(false, this.eraseCache.selected());
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 80, 124 + i, 150, 20, CommonComponents.GUI_CANCEL, (p_95558_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + i, 150, 20, new TranslatableComponent("selectWorld.backupEraseCache"), false);
      if (this.promptForCacheErase) {
         this.addRenderableWidget(this.eraseCache);
      }

   }

   public void render(PoseStack p_95553_, int p_95554_, int p_95555_, float p_95556_) {
      this.renderBackground(p_95553_);
      drawCenteredString(p_95553_, this.font, this.title, this.width / 2, 50, 16777215);
      this.message.renderCentered(p_95553_, this.width / 2, 70);
      super.render(p_95553_, p_95554_, p_95555_, p_95556_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_95549_, int p_95550_, int p_95551_) {
      if (p_95549_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_95549_, p_95550_, p_95551_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface Listener {
      void proceed(boolean p_95566_, boolean p_95567_);
   }
}