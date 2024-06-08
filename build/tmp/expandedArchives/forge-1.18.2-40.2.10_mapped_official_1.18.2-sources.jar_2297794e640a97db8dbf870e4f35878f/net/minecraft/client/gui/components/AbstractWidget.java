package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractWidget extends GuiComponent implements Widget, GuiEventListener, NarratableEntry {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   protected int width;
   protected int height;
   public int x;
   public int y;
   private Component message;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   private boolean focused;

   public AbstractWidget(int p_93629_, int p_93630_, int p_93631_, int p_93632_, Component p_93633_) {
      this.x = p_93629_;
      this.y = p_93630_;
      this.width = p_93631_;
      this.height = p_93632_;
      this.message = p_93633_;
   }

   public int getHeight() {
      return this.height;
   }

   protected int getYImage(boolean p_93668_) {
      int i = 1;
      if (!this.active) {
         i = 0;
      } else if (p_93668_) {
         i = 2;
      }

      return i;
   }

   public void render(PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
      if (this.visible) {
         this.isHovered = p_93658_ >= this.x && p_93659_ >= this.y && p_93658_ < this.x + this.width && p_93659_ < this.y + this.height;
         this.renderButton(p_93657_, p_93658_, p_93659_, p_93660_);
      }
   }

   protected MutableComponent createNarrationMessage() {
      return wrapDefaultNarrationMessage(this.getMessage());
   }

   public static MutableComponent wrapDefaultNarrationMessage(Component p_168800_) {
      return new TranslatableComponent("gui.narrate.button", p_168800_);
   }

   public void renderButton(PoseStack p_93676_, int p_93677_, int p_93678_, float p_93679_) {
      Minecraft minecraft = Minecraft.getInstance();
      Font font = minecraft.font;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
      int i = this.getYImage(this.isHoveredOrFocused());
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      this.blit(p_93676_, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
      this.blit(p_93676_, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
      this.renderBg(p_93676_, minecraft, p_93677_, p_93678_);
      int j = getFGColor();
      drawCenteredString(p_93676_, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   protected void renderBg(PoseStack p_93661_, Minecraft p_93662_, int p_93663_, int p_93664_) {
   }

   public void onClick(double p_93634_, double p_93635_) {
   }

   public void onRelease(double p_93669_, double p_93670_) {
   }

   protected void onDrag(double p_93636_, double p_93637_, double p_93638_, double p_93639_) {
   }

   public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(p_93643_)) {
            boolean flag = this.clicked(p_93641_, p_93642_);
            if (flag) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(p_93641_, p_93642_);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_93684_, double p_93685_, int p_93686_) {
      if (this.isValidClickButton(p_93686_)) {
         this.onRelease(p_93684_, p_93685_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(int p_93652_) {
      return p_93652_ == 0;
   }

   public boolean mouseDragged(double p_93645_, double p_93646_, int p_93647_, double p_93648_, double p_93649_) {
      if (this.isValidClickButton(p_93647_)) {
         this.onDrag(p_93645_, p_93646_, p_93648_, p_93649_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double p_93681_, double p_93682_) {
      return this.active && this.visible && p_93681_ >= (double)this.x && p_93682_ >= (double)this.y && p_93681_ < (double)(this.x + this.width) && p_93682_ < (double)(this.y + this.height);
   }

   public boolean isHoveredOrFocused() {
      return this.isHovered || this.focused;
   }

   public boolean changeFocus(boolean p_93691_) {
      if (this.active && this.visible) {
         this.focused = !this.focused;
         this.onFocusedChanged(this.focused);
         return this.focused;
      } else {
         return false;
      }
   }

   protected void onFocusedChanged(boolean p_93689_) {
   }

   public boolean isMouseOver(double p_93672_, double p_93673_) {
      return this.active && this.visible && p_93672_ >= (double)this.x && p_93673_ >= (double)this.y && p_93672_ < (double)(this.x + this.width) && p_93673_ < (double)(this.y + this.height);
   }

   public void renderToolTip(PoseStack p_93653_, int p_93654_, int p_93655_) {
   }

   public void playDownSound(SoundManager p_93665_) {
      p_93665_.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int p_93675_) {
      this.width = p_93675_;
   }

   public void setHeight(int value) {
      this.height = value;
   }

   public void setAlpha(float p_93651_) {
      this.alpha = p_93651_;
   }

   public void setMessage(Component p_93667_) {
      this.message = p_93667_;
   }

   public Component getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public boolean isActive() {
      return this.visible && this.active;
   }

   protected void setFocused(boolean p_93693_) {
      this.focused = p_93693_;
   }

   public static final int UNSET_FG_COLOR = -1;
   protected int packedFGColor = UNSET_FG_COLOR;
   public int getFGColor() {
      if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
      return this.active ? 16777215 : 10526880; // White : Light Grey
   }
   public void setFGColor(int color) {
      this.packedFGColor = color;
   }
   public void clearFGColor() {
      this.packedFGColor = UNSET_FG_COLOR;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.focused) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   protected void defaultButtonNarrationText(NarrationElementOutput p_168803_) {
      p_168803_.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            p_168803_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.button.usage.focused"));
         } else {
            p_168803_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.button.usage.hovered"));
         }
      }

   }
}
