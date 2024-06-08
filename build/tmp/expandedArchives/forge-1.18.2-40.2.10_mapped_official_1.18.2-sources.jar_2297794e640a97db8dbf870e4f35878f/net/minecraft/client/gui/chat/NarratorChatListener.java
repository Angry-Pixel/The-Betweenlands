package net.minecraft.client.gui.chat;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NarratorChatListener implements ChatListener {
   public static final Component NO_TITLE = TextComponent.EMPTY;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   public void handle(ChatType p_93322_, Component p_93323_, UUID p_93324_) {
      NarratorStatus narratorstatus = getStatus();
      if (narratorstatus != NarratorStatus.OFF) {
         if (!this.narrator.active()) {
            this.logNarratedMessage(p_93323_.getString());
         } else {
            if (narratorstatus == NarratorStatus.ALL || narratorstatus == NarratorStatus.CHAT && p_93322_ == ChatType.CHAT || narratorstatus == NarratorStatus.SYSTEM && p_93322_ == ChatType.SYSTEM) {
               Component component;
               if (p_93323_ instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent)p_93323_).getKey())) {
                  component = new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent)p_93323_).getArgs());
               } else {
                  component = p_93323_;
               }

               String s = component.getString();
               this.logNarratedMessage(s);
               this.narrator.say(s, p_93322_.shouldInterrupt());
            }

         }
      }
   }

   public void sayNow(Component p_168786_) {
      this.sayNow(p_168786_.getString());
   }

   public void sayNow(String p_93320_) {
      NarratorStatus narratorstatus = getStatus();
      if (narratorstatus != NarratorStatus.OFF && narratorstatus != NarratorStatus.CHAT && !p_93320_.isEmpty()) {
         this.logNarratedMessage(p_93320_);
         if (this.narrator.active()) {
            this.narrator.clear();
            this.narrator.say(p_93320_, true);
         }
      }

   }

   private static NarratorStatus getStatus() {
      return Minecraft.getInstance().options.narratorStatus;
   }

   private void logNarratedMessage(String p_168788_) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.debug("Narrating: {}", (Object)p_168788_.replaceAll("\n", "\\\\n"));
      }

   }

   public void updateNarratorStatus(NarratorStatus p_93318_) {
      this.clear();
      this.narrator.say((new TranslatableComponent("options.narrator")).append(" : ").append(p_93318_.getName()).getString(), true);
      ToastComponent toastcomponent = Minecraft.getInstance().getToasts();
      if (this.narrator.active()) {
         if (p_93318_ == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(toastcomponent, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled"), (Component)null);
         } else {
            SystemToast.addOrUpdate(toastcomponent, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.enabled"), p_93318_.getName());
         }
      } else {
         SystemToast.addOrUpdate(toastcomponent, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled"), new TranslatableComponent("options.narrator.notavailable"));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      if (getStatus() != NarratorStatus.OFF && this.narrator.active()) {
         this.narrator.clear();
      }
   }

   public void destroy() {
      this.narrator.destroy();
   }
}