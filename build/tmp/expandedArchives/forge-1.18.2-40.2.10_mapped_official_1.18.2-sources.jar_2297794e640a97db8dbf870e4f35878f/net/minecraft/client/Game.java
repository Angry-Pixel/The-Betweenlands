package net.minecraft.client;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.FrameTimer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Game implements RunningGame {
   private final Minecraft minecraft;
   @Nullable
   private final Launcher launcher;
   private SessionEventListener listener = SessionEventListener.NONE;

   public Game(Minecraft p_90738_) {
      this.minecraft = p_90738_;
      this.launcher = Bridge.getLauncher();
      if (this.launcher != null) {
         this.launcher.registerGame(this);
      }

   }

   public GameVersion getVersion() {
      return SharedConstants.getCurrentVersion();
   }

   public Language getSelectedLanguage() {
      return this.minecraft.getLanguageManager().getSelected();
   }

   @Nullable
   public GameSession getCurrentSession() {
      ClientLevel clientlevel = this.minecraft.level;
      return clientlevel == null ? null : new Session(clientlevel, this.minecraft.player, this.minecraft.player.connection);
   }

   public PerformanceMetrics getPerformanceMetrics() {
      FrameTimer frametimer = this.minecraft.getFrameTimer();
      long i = 2147483647L;
      long j = -2147483648L;
      long k = 0L;

      for(long l : frametimer.getLog()) {
         i = Math.min(i, l);
         j = Math.max(j, l);
         k += l;
      }

      return new Game.Metrics((int)i, (int)j, (int)(k / (long)frametimer.getLog().length), frametimer.getLog().length);
   }

   public void setSessionEventListener(SessionEventListener p_90746_) {
      this.listener = p_90746_;
   }

   public void onStartGameSession() {
      this.listener.onStartGameSession(this.getCurrentSession());
   }

   public void onLeaveGameSession() {
      this.listener.onLeaveGameSession(this.getCurrentSession());
   }

   @OnlyIn(Dist.CLIENT)
   static class Metrics implements PerformanceMetrics {
      private final int min;
      private final int max;
      private final int average;
      private final int samples;

      public Metrics(int p_90752_, int p_90753_, int p_90754_, int p_90755_) {
         this.min = p_90752_;
         this.max = p_90753_;
         this.average = p_90754_;
         this.samples = p_90755_;
      }

      public int getMinTime() {
         return this.min;
      }

      public int getMaxTime() {
         return this.max;
      }

      public int getAverageTime() {
         return this.average;
      }

      public int getSampleCount() {
         return this.samples;
      }
   }
}