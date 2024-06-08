package net.minecraft.client;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FullscreenResolutionProgressOption extends ProgressOption {
   private static final double CURRENT_MODE = -1.0D;

   public FullscreenResolutionProgressOption(Window p_90714_) {
      this(p_90714_, p_90714_.findBestMonitor());
   }

   private FullscreenResolutionProgressOption(Window p_90716_, @Nullable Monitor p_90717_) {
      super("options.fullscreen.resolution", -1.0D, p_90717_ != null ? (double)(p_90717_.getModeCount() - 1) : -1.0D, 1.0F, (p_90724_) -> {
         if (p_90717_ == null) {
            return -1.0D;
         } else {
            Optional<VideoMode> optional = p_90716_.getPreferredFullscreenVideoMode();
            return optional.map((p_167802_) -> {
               return (double)p_90717_.getVideoModeIndex(p_167802_);
            }).orElse(-1.0D);
         }
      }, (p_90728_, p_90729_) -> {
         if (p_90717_ != null) {
            if (p_90729_ == -1.0D) {
               p_90716_.setPreferredFullscreenVideoMode(Optional.empty());
            } else {
               p_90716_.setPreferredFullscreenVideoMode(Optional.of(p_90717_.getMode(p_90729_.intValue())));
            }

         }
      }, (p_90732_, p_90733_) -> {
         if (p_90717_ == null) {
            return new TranslatableComponent("options.fullscreen.unavailable");
         } else {
            double d0 = p_90733_.get(p_90732_);
            return d0 == -1.0D ? p_90733_.genericValueLabel(new TranslatableComponent("options.fullscreen.current")) : p_90733_.genericValueLabel(new TextComponent(p_90717_.getMode((int)d0).toString()));
         }
      });
   }
}