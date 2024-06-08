package net.minecraft.client.gui.chat;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StandardChatListener implements ChatListener {
   private final Minecraft minecraft;

   public StandardChatListener(Minecraft p_93359_) {
      this.minecraft = p_93359_;
   }

   public void handle(ChatType p_93361_, Component p_93362_, UUID p_93363_) {
      if (p_93361_ != ChatType.CHAT) {
         this.minecraft.gui.getChat().addMessage(p_93362_);
      } else {
         this.minecraft.gui.getChat().enqueueMessage(p_93362_);
      }

   }
}