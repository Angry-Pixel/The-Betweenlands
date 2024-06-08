package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatScreen extends Screen {
   public static final double MOUSE_SCROLL_SPEED = 7.0D;
   private static final Component USAGE_TEXT = new TranslatableComponent("chat_screen.usage");
   private String historyBuffer = "";
   private int historyPos = -1;
   protected EditBox input;
   private final String initial;
   CommandSuggestions commandSuggestions;

   public ChatScreen(String p_95579_) {
      super(new TranslatableComponent("chat_screen.title"));
      this.initial = p_95579_;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, new TranslatableComponent("chat.editBox")) {
         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(ChatScreen.this.commandSuggestions.getNarrationMessage());
         }
      };
      this.input.setMaxLength(256);
      this.input.setBordered(false);
      this.input.setValue(this.initial);
      this.input.setResponder(this::onEdited);
      this.addWidget(this.input);
      this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.input, this.font, false, false, 1, 10, true, -805306368);
      this.commandSuggestions.updateCommandInfo();
      this.setInitialFocus(this.input);
   }

   public void resize(Minecraft p_95600_, int p_95601_, int p_95602_) {
      String s = this.input.getValue();
      this.init(p_95600_, p_95601_, p_95602_);
      this.setChatLine(s);
      this.commandSuggestions.updateCommandInfo();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.gui.getChat().resetChatScroll();
   }

   public void tick() {
      this.input.tick();
   }

   private void onEdited(String p_95611_) {
      String s = this.input.getValue();
      this.commandSuggestions.setAllowSuggestions(!s.equals(this.initial));
      this.commandSuggestions.updateCommandInfo();
   }

   public boolean keyPressed(int p_95591_, int p_95592_, int p_95593_) {
      if (this.commandSuggestions.keyPressed(p_95591_, p_95592_, p_95593_)) {
         return true;
      } else if (super.keyPressed(p_95591_, p_95592_, p_95593_)) {
         return true;
      } else if (p_95591_ == 256) {
         this.minecraft.setScreen((Screen)null);
         return true;
      } else if (p_95591_ != 257 && p_95591_ != 335) {
         if (p_95591_ == 265) {
            this.moveInHistory(-1);
            return true;
         } else if (p_95591_ == 264) {
            this.moveInHistory(1);
            return true;
         } else if (p_95591_ == 266) {
            this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
            return true;
         } else if (p_95591_ == 267) {
            this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
            return true;
         } else {
            return false;
         }
      } else {
         String s = this.input.getValue().trim();
         if (!s.isEmpty()) {
            this.sendMessage(s);
         }

         this.minecraft.setScreen((Screen)null);
         return true;
      }
   }

   public boolean mouseScrolled(double p_95581_, double p_95582_, double p_95583_) {
      p_95583_ = Mth.clamp(p_95583_, -1.0D, 1.0D);
      if (this.commandSuggestions.mouseScrolled(p_95583_)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            p_95583_ *= 7.0D;
         }

         this.minecraft.gui.getChat().scrollChat((int)p_95583_);
         return true;
      }
   }

   public boolean mouseClicked(double p_95585_, double p_95586_, int p_95587_) {
      if (this.commandSuggestions.mouseClicked((double)((int)p_95585_), (double)((int)p_95586_), p_95587_)) {
         return true;
      } else {
         if (p_95587_ == 0) {
            ChatComponent chatcomponent = this.minecraft.gui.getChat();
            if (chatcomponent.handleChatQueueClicked(p_95585_, p_95586_)) {
               return true;
            }

            Style style = chatcomponent.getClickedComponentStyleAt(p_95585_, p_95586_);
            if (style != null && this.handleComponentClicked(style)) {
               return true;
            }
         }

         return this.input.mouseClicked(p_95585_, p_95586_, p_95587_) ? true : super.mouseClicked(p_95585_, p_95586_, p_95587_);
      }
   }

   protected void insertText(String p_95606_, boolean p_95607_) {
      if (p_95607_) {
         this.input.setValue(p_95606_);
      } else {
         this.input.insertText(p_95606_);
      }

   }

   public void moveInHistory(int p_95589_) {
      int i = this.historyPos + p_95589_;
      int j = this.minecraft.gui.getChat().getRecentChat().size();
      i = Mth.clamp(i, 0, j);
      if (i != this.historyPos) {
         if (i == j) {
            this.historyPos = j;
            this.input.setValue(this.historyBuffer);
         } else {
            if (this.historyPos == j) {
               this.historyBuffer = this.input.getValue();
            }

            this.input.setValue(this.minecraft.gui.getChat().getRecentChat().get(i));
            this.commandSuggestions.setAllowSuggestions(false);
            this.historyPos = i;
         }
      }
   }

   public void render(PoseStack p_95595_, int p_95596_, int p_95597_, float p_95598_) {
      this.setFocused(this.input);
      this.input.setFocus(true);
      fill(p_95595_, 2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
      this.input.render(p_95595_, p_95596_, p_95597_, p_95598_);
      this.commandSuggestions.render(p_95595_, p_95596_, p_95597_);
      Style style = this.minecraft.gui.getChat().getClickedComponentStyleAt((double)p_95596_, (double)p_95597_);
      if (style != null && style.getHoverEvent() != null) {
         this.renderComponentHoverEffect(p_95595_, style, p_95596_, p_95597_);
      }

      super.render(p_95595_, p_95596_, p_95597_, p_95598_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void setChatLine(String p_95613_) {
      this.input.setValue(p_95613_);
   }

   protected void updateNarrationState(NarrationElementOutput p_169238_) {
      p_169238_.add(NarratedElementType.TITLE, this.getTitle());
      p_169238_.add(NarratedElementType.USAGE, USAGE_TEXT);
      String s = this.input.getValue();
      if (!s.isEmpty()) {
         p_169238_.nest().add(NarratedElementType.TITLE, new TranslatableComponent("chat_screen.message", s));
      }

   }
}