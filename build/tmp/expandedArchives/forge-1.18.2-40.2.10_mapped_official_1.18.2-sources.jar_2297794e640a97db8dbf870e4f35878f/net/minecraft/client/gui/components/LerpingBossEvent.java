package net.minecraft.client.gui.components;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LerpingBossEvent extends BossEvent {
   private static final long LERP_MILLISECONDS = 100L;
   protected float targetPercent;
   protected long setTime;

   public LerpingBossEvent(UUID p_169021_, Component p_169022_, float p_169023_, BossEvent.BossBarColor p_169024_, BossEvent.BossBarOverlay p_169025_, boolean p_169026_, boolean p_169027_, boolean p_169028_) {
      super(p_169021_, p_169022_, p_169024_, p_169025_);
      this.targetPercent = p_169023_;
      this.progress = p_169023_;
      this.setTime = Util.getMillis();
      this.setDarkenScreen(p_169026_);
      this.setPlayBossMusic(p_169027_);
      this.setCreateWorldFog(p_169028_);
   }

   public void setProgress(float p_169030_) {
      this.progress = this.getProgress();
      this.targetPercent = p_169030_;
      this.setTime = Util.getMillis();
   }

   public float getProgress() {
      long i = Util.getMillis() - this.setTime;
      float f = Mth.clamp((float)i / 100.0F, 0.0F, 1.0F);
      return Mth.lerp(f, this.progress, this.targetPercent);
   }
}