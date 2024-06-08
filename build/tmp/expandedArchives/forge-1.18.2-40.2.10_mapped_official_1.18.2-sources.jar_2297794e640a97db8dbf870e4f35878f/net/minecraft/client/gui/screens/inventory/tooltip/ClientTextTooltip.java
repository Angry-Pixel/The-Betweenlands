package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTextTooltip implements ClientTooltipComponent {
   private final FormattedCharSequence text;

   public ClientTextTooltip(FormattedCharSequence p_169938_) {
      this.text = p_169938_;
   }

   public int getWidth(Font p_169941_) {
      return p_169941_.width(this.text);
   }

   public int getHeight() {
      return 10;
   }

   public void renderText(Font p_169943_, int p_169944_, int p_169945_, Matrix4f p_169946_, MultiBufferSource.BufferSource p_169947_) {
      p_169943_.drawInBatch(this.text, (float)p_169944_, (float)p_169945_, -1, true, p_169946_, p_169947_, false, 0, 15728880);
   }
}