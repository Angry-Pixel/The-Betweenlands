package net.minecraft.client.gui.components;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerTabOverlay extends GuiComponent {
   private static final Ordering<PlayerInfo> PLAYER_ORDERING = Ordering.from(new PlayerTabOverlay.PlayerInfoComparator());
   public static final int MAX_ROWS_PER_COL = 20;
   public static final int HEART_EMPTY_CONTAINER = 16;
   public static final int HEART_EMPTY_CONTAINER_BLINKING = 25;
   public static final int HEART_FULL = 52;
   public static final int HEART_HALF_FULL = 61;
   public static final int HEART_GOLDEN_FULL = 160;
   public static final int HEART_GOLDEN_HALF_FULL = 169;
   public static final int HEART_GHOST_FULL = 70;
   public static final int HEART_GHOST_HALF_FULL = 79;
   private final Minecraft minecraft;
   private final Gui gui;
   @Nullable
   private Component footer;
   @Nullable
   private Component header;
   private long visibilityId;
   private boolean visible;

   public PlayerTabOverlay(Minecraft p_94527_, Gui p_94528_) {
      this.minecraft = p_94527_;
      this.gui = p_94528_;
   }

   public Component getNameForDisplay(PlayerInfo p_94550_) {
      return p_94550_.getTabListDisplayName() != null ? this.decorateName(p_94550_, p_94550_.getTabListDisplayName().copy()) : this.decorateName(p_94550_, PlayerTeam.formatNameForTeam(p_94550_.getTeam(), new TextComponent(p_94550_.getProfile().getName())));
   }

   private Component decorateName(PlayerInfo p_94552_, MutableComponent p_94553_) {
      return p_94552_.getGameMode() == GameType.SPECTATOR ? p_94553_.withStyle(ChatFormatting.ITALIC) : p_94553_;
   }

   public void setVisible(boolean p_94557_) {
      if (p_94557_ && !this.visible) {
         this.visibilityId = Util.getMillis();
      }

      this.visible = p_94557_;
   }

   public void render(PoseStack p_94545_, int p_94546_, Scoreboard p_94547_, @Nullable Objective p_94548_) {
      ClientPacketListener clientpacketlistener = this.minecraft.player.connection;
      List<PlayerInfo> list = PLAYER_ORDERING.sortedCopy(clientpacketlistener.getOnlinePlayers());
      int i = 0;
      int j = 0;

      for(PlayerInfo playerinfo : list) {
         int k = this.minecraft.font.width(this.getNameForDisplay(playerinfo));
         i = Math.max(i, k);
         if (p_94548_ != null && p_94548_.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
            k = this.minecraft.font.width(" " + p_94547_.getOrCreatePlayerScore(playerinfo.getProfile().getName(), p_94548_).getScore());
            j = Math.max(j, k);
         }
      }

      list = list.subList(0, Math.min(list.size(), 80));
      int i4 = list.size();
      int j4 = i4;

      int k4;
      for(k4 = 1; j4 > 20; j4 = (i4 + k4 - 1) / k4) {
         ++k4;
      }

      boolean flag = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int l;
      if (p_94548_ != null) {
         if (p_94548_.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            l = 90;
         } else {
            l = j;
         }
      } else {
         l = 0;
      }

      int i1 = Math.min(k4 * ((flag ? 9 : 0) + i + l + 13), p_94546_ - 50) / k4;
      int j1 = p_94546_ / 2 - (i1 * k4 + (k4 - 1) * 5) / 2;
      int k1 = 10;
      int l1 = i1 * k4 + (k4 - 1) * 5;
      List<FormattedCharSequence> list1 = null;
      if (this.header != null) {
         list1 = this.minecraft.font.split(this.header, p_94546_ - 50);

         for(FormattedCharSequence formattedcharsequence : list1) {
            l1 = Math.max(l1, this.minecraft.font.width(formattedcharsequence));
         }
      }

      List<FormattedCharSequence> list2 = null;
      if (this.footer != null) {
         list2 = this.minecraft.font.split(this.footer, p_94546_ - 50);

         for(FormattedCharSequence formattedcharsequence1 : list2) {
            l1 = Math.max(l1, this.minecraft.font.width(formattedcharsequence1));
         }
      }

      if (list1 != null) {
         fill(p_94545_, p_94546_ / 2 - l1 / 2 - 1, k1 - 1, p_94546_ / 2 + l1 / 2 + 1, k1 + list1.size() * 9, Integer.MIN_VALUE);

         for(FormattedCharSequence formattedcharsequence2 : list1) {
            int i2 = this.minecraft.font.width(formattedcharsequence2);
            this.minecraft.font.drawShadow(p_94545_, formattedcharsequence2, (float)(p_94546_ / 2 - i2 / 2), (float)k1, -1);
            k1 += 9;
         }

         ++k1;
      }

      fill(p_94545_, p_94546_ / 2 - l1 / 2 - 1, k1 - 1, p_94546_ / 2 + l1 / 2 + 1, k1 + j4 * 9, Integer.MIN_VALUE);
      int l4 = this.minecraft.options.getBackgroundColor(553648127);

      for(int i5 = 0; i5 < i4; ++i5) {
         int j5 = i5 / j4;
         int j2 = i5 % j4;
         int k2 = j1 + j5 * i1 + j5 * 5;
         int l2 = k1 + j2 * 9;
         fill(p_94545_, k2, l2, k2 + i1, l2 + 8, l4);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         if (i5 < list.size()) {
            PlayerInfo playerinfo1 = list.get(i5);
            GameProfile gameprofile = playerinfo1.getProfile();
            if (flag) {
               Player player = this.minecraft.level.getPlayerByUUID(gameprofile.getId());
               boolean flag1 = player != null && LivingEntityRenderer.isEntityUpsideDown(player);
               RenderSystem.setShaderTexture(0, playerinfo1.getSkinLocation());
               int i3 = 8 + (flag1 ? 8 : 0);
               int j3 = 8 * (flag1 ? -1 : 1);
               GuiComponent.blit(p_94545_, k2, l2, 8, 8, 8.0F, (float)i3, 8, j3, 64, 64);
               if (player != null && player.isModelPartShown(PlayerModelPart.HAT)) {
                  int k3 = 8 + (flag1 ? 8 : 0);
                  int l3 = 8 * (flag1 ? -1 : 1);
                  GuiComponent.blit(p_94545_, k2, l2, 8, 8, 40.0F, (float)k3, 8, l3, 64, 64);
               }

               k2 += 9;
            }

            this.minecraft.font.drawShadow(p_94545_, this.getNameForDisplay(playerinfo1), (float)k2, (float)l2, playerinfo1.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if (p_94548_ != null && playerinfo1.getGameMode() != GameType.SPECTATOR) {
               int l5 = k2 + i + 1;
               int i6 = l5 + l;
               if (i6 - l5 > 5) {
                  this.renderTablistScore(p_94548_, l2, gameprofile.getName(), l5, i6, playerinfo1, p_94545_);
               }
            }

            this.renderPingIcon(p_94545_, i1, k2 - (flag ? 9 : 0), l2, playerinfo1);
         }
      }

      if (list2 != null) {
         k1 += j4 * 9 + 1;
         fill(p_94545_, p_94546_ / 2 - l1 / 2 - 1, k1 - 1, p_94546_ / 2 + l1 / 2 + 1, k1 + list2.size() * 9, Integer.MIN_VALUE);

         for(FormattedCharSequence formattedcharsequence3 : list2) {
            int k5 = this.minecraft.font.width(formattedcharsequence3);
            this.minecraft.font.drawShadow(p_94545_, formattedcharsequence3, (float)(p_94546_ / 2 - k5 / 2), (float)k1, -1);
            k1 += 9;
         }
      }

   }

   protected void renderPingIcon(PoseStack p_94539_, int p_94540_, int p_94541_, int p_94542_, PlayerInfo p_94543_) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
      int i = 0;
      int j;
      if (p_94543_.getLatency() < 0) {
         j = 5;
      } else if (p_94543_.getLatency() < 150) {
         j = 0;
      } else if (p_94543_.getLatency() < 300) {
         j = 1;
      } else if (p_94543_.getLatency() < 600) {
         j = 2;
      } else if (p_94543_.getLatency() < 1000) {
         j = 3;
      } else {
         j = 4;
      }

      this.setBlitOffset(this.getBlitOffset() + 100);
      this.blit(p_94539_, p_94541_ + p_94540_ - 11, p_94542_, 0, 176 + j * 8, 10, 8);
      this.setBlitOffset(this.getBlitOffset() - 100);
   }

   private void renderTablistScore(Objective p_94531_, int p_94532_, String p_94533_, int p_94534_, int p_94535_, PlayerInfo p_94536_, PoseStack p_94537_) {
      int i = p_94531_.getScoreboard().getOrCreatePlayerScore(p_94533_, p_94531_).getScore();
      if (p_94531_.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
         RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
         long j = Util.getMillis();
         if (this.visibilityId == p_94536_.getRenderVisibilityId()) {
            if (i < p_94536_.getLastHealth()) {
               p_94536_.setLastHealthTime(j);
               p_94536_.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 20));
            } else if (i > p_94536_.getLastHealth()) {
               p_94536_.setLastHealthTime(j);
               p_94536_.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 10));
            }
         }

         if (j - p_94536_.getLastHealthTime() > 1000L || this.visibilityId != p_94536_.getRenderVisibilityId()) {
            p_94536_.setLastHealth(i);
            p_94536_.setDisplayHealth(i);
            p_94536_.setLastHealthTime(j);
         }

         p_94536_.setRenderVisibilityId(this.visibilityId);
         p_94536_.setLastHealth(i);
         int k = Mth.ceil((float)Math.max(i, p_94536_.getDisplayHealth()) / 2.0F);
         int l = Math.max(Mth.ceil((float)(i / 2)), Math.max(Mth.ceil((float)(p_94536_.getDisplayHealth() / 2)), 10));
         boolean flag = p_94536_.getHealthBlinkTime() > (long)this.gui.getGuiTicks() && (p_94536_.getHealthBlinkTime() - (long)this.gui.getGuiTicks()) / 3L % 2L == 1L;
         if (k > 0) {
            int i1 = Mth.floor(Math.min((float)(p_94535_ - p_94534_ - 4) / (float)l, 9.0F));
            if (i1 > 3) {
               for(int j1 = k; j1 < l; ++j1) {
                  this.blit(p_94537_, p_94534_ + j1 * i1, p_94532_, flag ? 25 : 16, 0, 9, 9);
               }

               for(int l1 = 0; l1 < k; ++l1) {
                  this.blit(p_94537_, p_94534_ + l1 * i1, p_94532_, flag ? 25 : 16, 0, 9, 9);
                  if (flag) {
                     if (l1 * 2 + 1 < p_94536_.getDisplayHealth()) {
                        this.blit(p_94537_, p_94534_ + l1 * i1, p_94532_, 70, 0, 9, 9);
                     }

                     if (l1 * 2 + 1 == p_94536_.getDisplayHealth()) {
                        this.blit(p_94537_, p_94534_ + l1 * i1, p_94532_, 79, 0, 9, 9);
                     }
                  }

                  if (l1 * 2 + 1 < i) {
                     this.blit(p_94537_, p_94534_ + l1 * i1, p_94532_, l1 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (l1 * 2 + 1 == i) {
                     this.blit(p_94537_, p_94534_ + l1 * i1, p_94532_, l1 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float f = Mth.clamp((float)i / 20.0F, 0.0F, 1.0F);
               int k1 = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
               String s = "" + (float)i / 2.0F;
               if (p_94535_ - this.minecraft.font.width(s + "hp") >= p_94534_) {
                  s = s + "hp";
               }

               this.minecraft.font.drawShadow(p_94537_, s, (float)((p_94535_ + p_94534_) / 2 - this.minecraft.font.width(s) / 2), (float)p_94532_, k1);
            }
         }
      } else {
         String s1 = "" + ChatFormatting.YELLOW + i;
         this.minecraft.font.drawShadow(p_94537_, s1, (float)(p_94535_ - this.minecraft.font.width(s1)), (float)p_94532_, 16777215);
      }

   }

   public void setFooter(@Nullable Component p_94555_) {
      this.footer = p_94555_;
   }

   public void setHeader(@Nullable Component p_94559_) {
      this.header = p_94559_;
   }

   public void reset() {
      this.header = null;
      this.footer = null;
   }

   @OnlyIn(Dist.CLIENT)
   static class PlayerInfoComparator implements Comparator<PlayerInfo> {
      public int compare(PlayerInfo p_94564_, PlayerInfo p_94565_) {
         PlayerTeam playerteam = p_94564_.getTeam();
         PlayerTeam playerteam1 = p_94565_.getTeam();
         return ComparisonChain.start().compareTrueFirst(p_94564_.getGameMode() != GameType.SPECTATOR, p_94565_.getGameMode() != GameType.SPECTATOR).compare(playerteam != null ? playerteam.getName() : "", playerteam1 != null ? playerteam1.getName() : "").compare(p_94564_.getProfile().getName(), p_94565_.getProfile().getName(), String::compareToIgnoreCase).result();
      }
   }
}