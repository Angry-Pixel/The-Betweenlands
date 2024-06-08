package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InBedChatScreen extends ChatScreen {
   public InBedChatScreen() {
      super("");
   }

   protected void init() {
      super.init();
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableComponent("multiplayer.stopSleeping"), (p_96074_) -> {
         this.sendWakeUp();
      }));
   }

   public void onClose() {
      this.sendWakeUp();
   }

   public boolean keyPressed(int p_96070_, int p_96071_, int p_96072_) {
      if (p_96070_ == 256) {
         this.sendWakeUp();
      } else if (p_96070_ == 257 || p_96070_ == 335) {
         String s = this.input.getValue().trim();
         if (!s.isEmpty()) {
            this.sendMessage(s);
         }

         this.input.setValue("");
         this.minecraft.gui.getChat().resetChatScroll();
         return true;
      }

      return super.keyPressed(p_96070_, p_96071_, p_96072_);
   }

   private void sendWakeUp() {
      ClientPacketListener clientpacketlistener = this.minecraft.player.connection;
      clientpacketlistener.send(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING));
   }

   public void onPlayerWokeUp() {
      if (this.input.getValue().isEmpty()) {
         this.minecraft.setScreen((Screen)null);
      } else {
         this.minecraft.setScreen(new ChatScreen(this.input.getValue()));
      }

   }
}