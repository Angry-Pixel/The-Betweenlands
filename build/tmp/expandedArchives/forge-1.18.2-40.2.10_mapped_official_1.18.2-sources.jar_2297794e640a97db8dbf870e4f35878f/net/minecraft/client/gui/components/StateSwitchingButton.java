package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StateSwitchingButton extends AbstractWidget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public StateSwitchingButton(int p_94615_, int p_94616_, int p_94617_, int p_94618_, boolean p_94619_) {
      super(p_94615_, p_94616_, p_94617_, p_94618_, TextComponent.EMPTY);
      this.isStateTriggered = p_94619_;
   }

   public void initTextureValues(int p_94625_, int p_94626_, int p_94627_, int p_94628_, ResourceLocation p_94629_) {
      this.xTexStart = p_94625_;
      this.yTexStart = p_94626_;
      this.xDiffTex = p_94627_;
      this.yDiffTex = p_94628_;
      this.resourceLocation = p_94629_;
   }

   public void setStateTriggered(boolean p_94636_) {
      this.isStateTriggered = p_94636_;
   }

   public boolean isStateTriggered() {
      return this.isStateTriggered;
   }

   public void setPosition(int p_94622_, int p_94623_) {
      this.x = p_94622_;
      this.y = p_94623_;
   }

   public void updateNarration(NarrationElementOutput p_169069_) {
      this.defaultButtonNarrationText(p_169069_);
   }

   public void renderButton(PoseStack p_94631_, int p_94632_, int p_94633_, float p_94634_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, this.resourceLocation);
      RenderSystem.disableDepthTest();
      int i = this.xTexStart;
      int j = this.yTexStart;
      if (this.isStateTriggered) {
         i += this.xDiffTex;
      }

      if (this.isHoveredOrFocused()) {
         j += this.yDiffTex;
      }

      this.blit(p_94631_, this.x, this.y, i, j, this.width, this.height);
      RenderSystem.enableDepthTest();
   }
}