package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerSelectionList extends ObjectSelectionList<ServerSelectionList.Entry> {
   static final Logger LOGGER = LogUtils.getLogger();
   static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
   static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
   static final Component SCANNING_LABEL = new TranslatableComponent("lanServer.scanning");
   static final Component CANT_RESOLVE_TEXT = (new TranslatableComponent("multiplayer.status.cannot_resolve")).withStyle(ChatFormatting.DARK_RED);
   static final Component CANT_CONNECT_TEXT = (new TranslatableComponent("multiplayer.status.cannot_connect")).withStyle(ChatFormatting.DARK_RED);
   static final Component INCOMPATIBLE_TOOLTIP = new TranslatableComponent("multiplayer.status.incompatible");
   static final Component NO_CONNECTION_TOOLTIP = new TranslatableComponent("multiplayer.status.no_connection");
   static final Component PINGING_TOOLTIP = new TranslatableComponent("multiplayer.status.pinging");
   private final JoinMultiplayerScreen screen;
   private final List<ServerSelectionList.OnlineServerEntry> onlineServers = Lists.newArrayList();
   private final ServerSelectionList.Entry lanHeader = new ServerSelectionList.LANHeader();
   private final List<ServerSelectionList.NetworkServerEntry> networkServers = Lists.newArrayList();

   public ServerSelectionList(JoinMultiplayerScreen p_99771_, Minecraft p_99772_, int p_99773_, int p_99774_, int p_99775_, int p_99776_, int p_99777_) {
      super(p_99772_, p_99773_, p_99774_, p_99775_, p_99776_, p_99777_);
      this.screen = p_99771_;
   }

   private void refreshEntries() {
      this.clearEntries();
      this.onlineServers.forEach((p_169979_) -> {
         this.addEntry(p_169979_);
      });
      this.addEntry(this.lanHeader);
      this.networkServers.forEach((p_169976_) -> {
         this.addEntry(p_169976_);
      });
   }

   public void setSelected(@Nullable ServerSelectionList.Entry p_99790_) {
      super.setSelected(p_99790_);
      this.screen.onSelectedChange();
   }

   public boolean keyPressed(int p_99782_, int p_99783_, int p_99784_) {
      ServerSelectionList.Entry serverselectionlist$entry = this.getSelected();
      return serverselectionlist$entry != null && serverselectionlist$entry.keyPressed(p_99782_, p_99783_, p_99784_) || super.keyPressed(p_99782_, p_99783_, p_99784_);
   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection p_99788_) {
      this.moveSelection(p_99788_, (p_169973_) -> {
         return !(p_169973_ instanceof ServerSelectionList.LANHeader);
      });
   }

   public void updateOnlineServers(ServerList p_99798_) {
      this.onlineServers.clear();

      for(int i = 0; i < p_99798_.size(); ++i) {
         this.onlineServers.add(new ServerSelectionList.OnlineServerEntry(this.screen, p_99798_.get(i)));
      }

      this.refreshEntries();
   }

   public void updateNetworkServers(List<LanServer> p_99800_) {
      this.networkServers.clear();

      for(LanServer lanserver : p_99800_) {
         this.networkServers.add(new ServerSelectionList.NetworkServerEntry(this.screen, lanserver));
      }

      this.refreshEntries();
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 30;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 85;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends ObjectSelectionList.Entry<ServerSelectionList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public static class LANHeader extends ServerSelectionList.Entry {
      private final Minecraft minecraft = Minecraft.getInstance();

      public void render(PoseStack p_99818_, int p_99819_, int p_99820_, int p_99821_, int p_99822_, int p_99823_, int p_99824_, int p_99825_, boolean p_99826_, float p_99827_) {
         int i = p_99820_ + p_99823_ / 2 - 9 / 2;
         this.minecraft.font.draw(p_99818_, ServerSelectionList.SCANNING_LABEL, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(ServerSelectionList.SCANNING_LABEL) / 2), (float)i, 16777215);
         String s;
         switch((int)(Util.getMillis() / 300L % 4L)) {
         case 0:
         default:
            s = "O o o";
            break;
         case 1:
         case 3:
            s = "o O o";
            break;
         case 2:
            s = "o o O";
         }

         this.minecraft.font.draw(p_99818_, s, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(s) / 2), (float)(i + 9), 8421504);
      }

      public Component getNarration() {
         return TextComponent.EMPTY;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NetworkServerEntry extends ServerSelectionList.Entry {
      private static final int ICON_WIDTH = 32;
      private static final Component LAN_SERVER_HEADER = new TranslatableComponent("lanServer.title");
      private static final Component HIDDEN_ADDRESS_TEXT = new TranslatableComponent("selectServer.hiddenAddress");
      private final JoinMultiplayerScreen screen;
      protected final Minecraft minecraft;
      protected final LanServer serverData;
      private long lastClickTime;

      protected NetworkServerEntry(JoinMultiplayerScreen p_99836_, LanServer p_99837_) {
         this.screen = p_99836_;
         this.serverData = p_99837_;
         this.minecraft = Minecraft.getInstance();
      }

      public void render(PoseStack p_99844_, int p_99845_, int p_99846_, int p_99847_, int p_99848_, int p_99849_, int p_99850_, int p_99851_, boolean p_99852_, float p_99853_) {
         this.minecraft.font.draw(p_99844_, LAN_SERVER_HEADER, (float)(p_99847_ + 32 + 3), (float)(p_99846_ + 1), 16777215);
         this.minecraft.font.draw(p_99844_, this.serverData.getMotd(), (float)(p_99847_ + 32 + 3), (float)(p_99846_ + 12), 8421504);
         if (this.minecraft.options.hideServerAddress) {
            this.minecraft.font.draw(p_99844_, HIDDEN_ADDRESS_TEXT, (float)(p_99847_ + 32 + 3), (float)(p_99846_ + 12 + 11), 3158064);
         } else {
            this.minecraft.font.draw(p_99844_, this.serverData.getAddress(), (float)(p_99847_ + 32 + 3), (float)(p_99846_ + 12 + 11), 3158064);
         }

      }

      public boolean mouseClicked(double p_99840_, double p_99841_, int p_99842_) {
         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public LanServer getServerData() {
         return this.serverData;
      }

      public Component getNarration() {
         return new TranslatableComponent("narrator.select", (new TextComponent("")).append(LAN_SERVER_HEADER).append(" ").append(this.serverData.getMotd()));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class OnlineServerEntry extends ServerSelectionList.Entry {
      private static final int ICON_WIDTH = 32;
      private static final int ICON_HEIGHT = 32;
      private static final int ICON_OVERLAY_X_MOVE_RIGHT = 0;
      private static final int ICON_OVERLAY_X_MOVE_LEFT = 32;
      private static final int ICON_OVERLAY_X_MOVE_DOWN = 64;
      private static final int ICON_OVERLAY_X_MOVE_UP = 96;
      private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
      private static final int ICON_OVERLAY_Y_SELECTED = 32;
      private final JoinMultiplayerScreen screen;
      private final Minecraft minecraft;
      private final ServerData serverData;
      private final ResourceLocation iconLocation;
      @Nullable
      private String lastIconB64;
      @Nullable
      private DynamicTexture icon;
      private long lastClickTime;

      protected OnlineServerEntry(JoinMultiplayerScreen p_99864_, ServerData p_99865_) {
         this.screen = p_99864_;
         this.serverData = p_99865_;
         this.minecraft = Minecraft.getInstance();
         this.iconLocation = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(p_99865_.ip) + "/icon");
         AbstractTexture abstracttexture = this.minecraft.getTextureManager().getTexture(this.iconLocation, MissingTextureAtlasSprite.getTexture());
         if (abstracttexture != MissingTextureAtlasSprite.getTexture() && abstracttexture instanceof DynamicTexture) {
            this.icon = (DynamicTexture)abstracttexture;
         }

      }

      public void render(PoseStack p_99879_, int p_99880_, int p_99881_, int p_99882_, int p_99883_, int p_99884_, int p_99885_, int p_99886_, boolean p_99887_, float p_99888_) {
         if (!this.serverData.pinged) {
            this.serverData.pinged = true;
            this.serverData.ping = -2L;
            this.serverData.motd = TextComponent.EMPTY;
            this.serverData.status = TextComponent.EMPTY;
            ServerSelectionList.THREAD_POOL.submit(() -> {
               try {
                  this.screen.getPinger().pingServer(this.serverData, () -> {
                     this.minecraft.execute(this::updateServerList);
                  });
               } catch (UnknownHostException unknownhostexception) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_RESOLVE_TEXT;
               } catch (Exception exception) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_CONNECT_TEXT;
               }

            });
         }

         boolean flag = this.serverData.protocol != SharedConstants.getCurrentVersion().getProtocolVersion();
         this.minecraft.font.draw(p_99879_, this.serverData.name, (float)(p_99882_ + 32 + 3), (float)(p_99881_ + 1), 16777215);
         List<FormattedCharSequence> list = this.minecraft.font.split(this.serverData.motd, p_99883_ - 32 - 2);

         for(int i = 0; i < Math.min(list.size(), 2); ++i) {
            this.minecraft.font.draw(p_99879_, list.get(i), (float)(p_99882_ + 32 + 3), (float)(p_99881_ + 12 + 9 * i), 8421504);
         }

         Component component1 = (Component)(flag ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status);
         int j = this.minecraft.font.width(component1);
         this.minecraft.font.draw(p_99879_, component1, (float)(p_99882_ + p_99883_ - j - 15 - 2), (float)(p_99881_ + 1), 8421504);
         int k = 0;
         int l;
         List<Component> list1;
         Component component;
         if (flag) {
            l = 5;
            component = ServerSelectionList.INCOMPATIBLE_TOOLTIP;
            list1 = this.serverData.playerList;
         } else if (this.serverData.pinged && this.serverData.ping != -2L) {
            if (this.serverData.ping < 0L) {
               l = 5;
            } else if (this.serverData.ping < 150L) {
               l = 0;
            } else if (this.serverData.ping < 300L) {
               l = 1;
            } else if (this.serverData.ping < 600L) {
               l = 2;
            } else if (this.serverData.ping < 1000L) {
               l = 3;
            } else {
               l = 4;
            }

            if (this.serverData.ping < 0L) {
               component = ServerSelectionList.NO_CONNECTION_TOOLTIP;
               list1 = Collections.emptyList();
            } else {
               component = new TranslatableComponent("multiplayer.status.ping", this.serverData.ping);
               list1 = this.serverData.playerList;
            }
         } else {
            k = 1;
            l = (int)(Util.getMillis() / 100L + (long)(p_99880_ * 2) & 7L);
            if (l > 4) {
               l = 8 - l;
            }

            component = ServerSelectionList.PINGING_TOOLTIP;
            list1 = Collections.emptyList();
         }

         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
         GuiComponent.blit(p_99879_, p_99882_ + p_99883_ - 15, p_99881_, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
         String s = this.serverData.getIconB64();
         if (!Objects.equals(s, this.lastIconB64)) {
            if (this.uploadServerIcon(s)) {
               this.lastIconB64 = s;
            } else {
               this.serverData.setIconB64((String)null);
               this.updateServerList();
            }
         }

         if (this.icon == null) {
            this.drawIcon(p_99879_, p_99882_, p_99881_, ServerSelectionList.ICON_MISSING);
         } else {
            this.drawIcon(p_99879_, p_99882_, p_99881_, this.iconLocation);
         }

         int i1 = p_99885_ - p_99882_;
         int j1 = p_99886_ - p_99881_;
         if (i1 >= p_99883_ - 15 && i1 <= p_99883_ - 5 && j1 >= 0 && j1 <= 8) {
            this.screen.setToolTip(Collections.singletonList(component));
         } else if (i1 >= p_99883_ - j - 15 - 2 && i1 <= p_99883_ - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.screen.setToolTip(list1);
         }

         net.minecraftforge.client.ForgeHooksClient.drawForgePingInfo(this.screen, serverData, p_99879_, p_99882_, p_99881_, p_99883_, i1, j1);

         if (this.minecraft.options.touchscreen || p_99887_) {
            RenderSystem.setShaderTexture(0, ServerSelectionList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(p_99879_, p_99882_, p_99881_, p_99882_ + 32, p_99881_ + 32, -1601138544);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = p_99885_ - p_99882_;
            int l1 = p_99886_ - p_99881_;
            if (this.canJoin()) {
               if (k1 < 32 && k1 > 16) {
                  GuiComponent.blit(p_99879_, p_99882_, p_99881_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(p_99879_, p_99882_, p_99881_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_99880_ > 0) {
               if (k1 < 16 && l1 < 16) {
                  GuiComponent.blit(p_99879_, p_99882_, p_99881_, 96.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(p_99879_, p_99882_, p_99881_, 96.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_99880_ < this.screen.getServers().size() - 1) {
               if (k1 < 16 && l1 > 16) {
                  GuiComponent.blit(p_99879_, p_99882_, p_99881_, 64.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(p_99879_, p_99882_, p_99881_, 64.0F, 0.0F, 32, 32, 256, 256);
               }
            }
         }

      }

      public void updateServerList() {
         this.screen.getServers().save();
      }

      protected void drawIcon(PoseStack p_99890_, int p_99891_, int p_99892_, ResourceLocation p_99893_) {
         RenderSystem.setShaderTexture(0, p_99893_);
         RenderSystem.enableBlend();
         GuiComponent.blit(p_99890_, p_99891_, p_99892_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
      }

      private boolean canJoin() {
         return true;
      }

      private boolean uploadServerIcon(@Nullable String p_99897_) {
         if (p_99897_ == null) {
            this.minecraft.getTextureManager().release(this.iconLocation);
            if (this.icon != null && this.icon.getPixels() != null) {
               this.icon.getPixels().close();
            }

            this.icon = null;
         } else {
            try {
               NativeImage nativeimage = NativeImage.fromBase64(p_99897_);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               if (this.icon == null) {
                  this.icon = new DynamicTexture(nativeimage);
               } else {
                  this.icon.setPixels(nativeimage);
                  this.icon.upload();
               }

               this.minecraft.getTextureManager().register(this.iconLocation, this.icon);
            } catch (Throwable throwable) {
               ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, throwable);
               return false;
            }
         }

         return true;
      }

      public boolean keyPressed(int p_99875_, int p_99876_, int p_99877_) {
         if (Screen.hasShiftDown()) {
            ServerSelectionList serverselectionlist = this.screen.serverSelectionList;
            int i = serverselectionlist.children().indexOf(this);
            if (i == -1) {
               return true;
            }

            if (p_99875_ == 264 && i < this.screen.getServers().size() - 1 || p_99875_ == 265 && i > 0) {
               this.swap(i, p_99875_ == 264 ? i + 1 : i - 1);
               return true;
            }
         }

         return super.keyPressed(p_99875_, p_99876_, p_99877_);
      }

      private void swap(int p_99872_, int p_99873_) {
         this.screen.getServers().swap(p_99872_, p_99873_);
         this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
         ServerSelectionList.Entry serverselectionlist$entry = this.screen.serverSelectionList.children().get(p_99873_);
         this.screen.serverSelectionList.setSelected(serverselectionlist$entry);
         ServerSelectionList.this.ensureVisible(serverselectionlist$entry);
      }

      public boolean mouseClicked(double p_99868_, double p_99869_, int p_99870_) {
         double d0 = p_99868_ - (double)ServerSelectionList.this.getRowLeft();
         double d1 = p_99869_ - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
         if (d0 <= 32.0D) {
            if (d0 < 32.0D && d0 > 16.0D && this.canJoin()) {
               this.screen.setSelected(this);
               this.screen.joinSelectedServer();
               return true;
            }

            int i = this.screen.serverSelectionList.children().indexOf(this);
            if (d0 < 16.0D && d1 < 16.0D && i > 0) {
               this.swap(i, i - 1);
               return true;
            }

            if (d0 < 16.0D && d1 > 16.0D && i < this.screen.getServers().size() - 1) {
               this.swap(i, i + 1);
               return true;
            }
         }

         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public ServerData getServerData() {
         return this.serverData;
      }

      public Component getNarration() {
         return new TranslatableComponent("narrator.select", this.serverData.name);
      }
   }
}
