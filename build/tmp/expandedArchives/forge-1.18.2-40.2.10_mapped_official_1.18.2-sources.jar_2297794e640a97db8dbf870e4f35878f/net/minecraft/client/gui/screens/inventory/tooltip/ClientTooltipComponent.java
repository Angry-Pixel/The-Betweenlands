package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ClientTooltipComponent {
   static ClientTooltipComponent create(FormattedCharSequence p_169949_) {
      return new ClientTextTooltip(p_169949_);
   }

   static ClientTooltipComponent create(TooltipComponent p_169951_) {
      if (p_169951_ instanceof BundleTooltip) {
         return new ClientBundleTooltip((BundleTooltip)p_169951_);
      } else {
         ClientTooltipComponent result = net.minecraftforge.client.MinecraftForgeClient.getClientTooltipComponent(p_169951_);
         if (result != null) return result;
         throw new IllegalArgumentException("Unknown TooltipComponent");
      }
   }

   int getHeight();

   int getWidth(Font p_169952_);

   default void renderText(Font p_169953_, int p_169954_, int p_169955_, Matrix4f p_169956_, MultiBufferSource.BufferSource p_169957_) {
   }

   default void renderImage(Font p_194048_, int p_194049_, int p_194050_, PoseStack p_194051_, ItemRenderer p_194052_, int p_194053_) {
   }
}
