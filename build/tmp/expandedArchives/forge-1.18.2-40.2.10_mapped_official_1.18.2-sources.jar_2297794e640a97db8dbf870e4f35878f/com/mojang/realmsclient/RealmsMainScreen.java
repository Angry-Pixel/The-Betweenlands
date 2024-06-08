package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
   private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
   private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
   private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
   private static final ResourceLocation LEAVE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/leave_icon.png");
   private static final ResourceLocation INVITATION_ICONS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invitation_icons.png");
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   static final ResourceLocation WORLDICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/world_icon.png");
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("realms", "textures/gui/title/realms.png");
   private static final ResourceLocation CONFIGURE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/configure_icon.png");
   private static final ResourceLocation QUESTIONMARK_LOCATION = new ResourceLocation("realms", "textures/gui/realms/questionmark.png");
   private static final ResourceLocation NEWS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_icon.png");
   private static final ResourceLocation POPUP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/popup.png");
   private static final ResourceLocation DARKEN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/darken.png");
   static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   static final ResourceLocation BUTTON_LOCATION = new ResourceLocation("minecraft", "textures/gui/widgets.png");
   static final Component NO_PENDING_INVITES_TEXT = new TranslatableComponent("mco.invites.nopending");
   static final Component PENDING_INVITES_TEXT = new TranslatableComponent("mco.invites.pending");
   static final List<Component> TRIAL_MESSAGE_LINES = ImmutableList.of(new TranslatableComponent("mco.trial.message.line1"), new TranslatableComponent("mco.trial.message.line2"));
   static final Component SERVER_UNITIALIZED_TEXT = new TranslatableComponent("mco.selectServer.uninitialized");
   static final Component SUBSCRIPTION_EXPIRED_TEXT = new TranslatableComponent("mco.selectServer.expiredList");
   static final Component SUBSCRIPTION_RENEW_TEXT = new TranslatableComponent("mco.selectServer.expiredRenew");
   static final Component TRIAL_EXPIRED_TEXT = new TranslatableComponent("mco.selectServer.expiredTrial");
   static final Component SUBSCRIPTION_CREATE_TEXT = new TranslatableComponent("mco.selectServer.expiredSubscribe");
   static final Component SELECT_MINIGAME_PREFIX = (new TranslatableComponent("mco.selectServer.minigame")).append(" ");
   private static final Component POPUP_TEXT = new TranslatableComponent("mco.selectServer.popup");
   private static final Component SERVER_EXPIRED_TOOLTIP = new TranslatableComponent("mco.selectServer.expired");
   private static final Component SERVER_EXPIRES_SOON_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.soon");
   private static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.day");
   private static final Component SERVER_OPEN_TOOLTIP = new TranslatableComponent("mco.selectServer.open");
   private static final Component SERVER_CLOSED_TOOLTIP = new TranslatableComponent("mco.selectServer.closed");
   private static final Component LEAVE_SERVER_TOOLTIP = new TranslatableComponent("mco.selectServer.leave");
   private static final Component CONFIGURE_SERVER_TOOLTIP = new TranslatableComponent("mco.selectServer.configure");
   private static final Component SERVER_INFO_TOOLTIP = new TranslatableComponent("mco.selectServer.info");
   private static final Component NEWS_TOOLTIP = new TranslatableComponent("mco.news");
   static final Component UNITIALIZED_WORLD_NARRATION = new TranslatableComponent("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
   static final Component TRIAL_TEXT = CommonComponents.joinLines(TRIAL_MESSAGE_LINES);
   private static List<ResourceLocation> teaserImages = ImmutableList.of();
   static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher(Minecraft.getInstance(), RealmsClient.create());
   static boolean overrideConfigure;
   private static int lastScrollYPosition = -1;
   static volatile boolean hasParentalConsent;
   static volatile boolean checkedParentalConsent;
   static volatile boolean checkedClientCompatability;
   @Nullable
   static Screen realmsGenericErrorScreen;
   private static boolean regionsPinged;
   private final RateLimiter inviteNarrationLimiter;
   private boolean dontSetConnectedToRealms;
   final Screen lastScreen;
   RealmsMainScreen.RealmSelectionList realmSelectionList;
   private boolean realmsSelectionListAdded;
   private Button playButton;
   private Button backButton;
   private Button renewButton;
   private Button configureButton;
   private Button leaveButton;
   @Nullable
   private List<Component> toolTip;
   private List<RealmsServer> realmsServers = ImmutableList.of();
   volatile int numberOfPendingInvites;
   int animTick;
   private boolean hasFetchedServers;
   boolean popupOpenedByUser;
   private boolean justClosedPopup;
   private volatile boolean trialsAvailable;
   private volatile boolean createdTrial;
   private volatile boolean showingPopup;
   volatile boolean hasUnreadNews;
   @Nullable
   volatile String newsLink;
   private int carouselIndex;
   private int carouselTick;
   private boolean hasSwitchedCarouselImage;
   private List<KeyCombo> keyCombos;
   long lastClickTime;
   private ReentrantLock connectLock = new ReentrantLock();
   private MultiLineLabel formattedPopup = MultiLineLabel.EMPTY;
   RealmsMainScreen.HoveredElement hoveredElement;
   private Button showPopupButton;
   private RealmsMainScreen.PendingInvitesButton pendingInvitesButton;
   private Button newsButton;
   private Button createTrialButton;
   private Button buyARealmButton;
   private Button closeButton;

   public RealmsMainScreen(Screen p_86315_) {
      super(NarratorChatListener.NO_TITLE);
      this.lastScreen = p_86315_;
      this.inviteNarrationLimiter = RateLimiter.create((double)0.016666668F);
   }

   private boolean shouldShowMessageInList() {
      if (hasParentalConsent() && this.hasFetchedServers) {
         if (this.trialsAvailable && !this.createdTrial) {
            return true;
         } else {
            for(RealmsServer realmsserver : this.realmsServers) {
               if (realmsserver.ownerUUID.equals(this.minecraft.getUser().getUuid())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldShowPopup() {
      if (hasParentalConsent() && this.hasFetchedServers) {
         return this.popupOpenedByUser ? true : this.realmsServers.isEmpty();
      } else {
         return false;
      }
   }

   public void init() {
      this.keyCombos = Lists.newArrayList(new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
         overrideConfigure = !overrideConfigure;
      }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
         if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
            this.switchToProd();
         } else {
            this.switchToStage();
         }

      }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
         if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
            this.switchToProd();
         } else {
            this.switchToLocal();
         }

      }));
      if (realmsGenericErrorScreen != null) {
         this.minecraft.setScreen(realmsGenericErrorScreen);
      } else {
         this.connectLock = new ReentrantLock();
         if (checkedClientCompatability && !hasParentalConsent()) {
            this.checkParentalConsent();
         }

         this.checkClientCompatability();
         if (!this.dontSetConnectedToRealms) {
            this.minecraft.setConnectedToRealms(false);
         }

         this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
         if (hasParentalConsent()) {
            REALMS_DATA_FETCHER.forceUpdate();
         }

         this.showingPopup = false;
         this.addButtons();
         this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
         if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount((double)lastScrollYPosition);
         }

         this.addWidget(this.realmSelectionList);
         this.realmsSelectionListAdded = true;
         this.magicalSpecialHackyFocus(this.realmSelectionList);
         this.formattedPopup = MultiLineLabel.create(this.font, POPUP_TEXT, 100);
      }
   }

   private static boolean hasParentalConsent() {
      return checkedParentalConsent && hasParentalConsent;
   }

   public void addButtons() {
      this.leaveButton = this.addRenderableWidget(new Button(this.width / 2 - 202, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.leave"), (p_86679_) -> {
         this.leaveClicked(this.getSelectedServer());
      }));
      this.configureButton = this.addRenderableWidget(new Button(this.width / 2 - 190, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.configure"), (p_86672_) -> {
         this.configureClicked(this.getSelectedServer());
      }));
      this.playButton = this.addRenderableWidget(new Button(this.width / 2 - 93, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.play"), (p_86659_) -> {
         this.play(this.getSelectedServer(), this);
      }));
      this.backButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 32, 90, 20, CommonComponents.GUI_BACK, (p_86647_) -> {
         if (!this.justClosedPopup) {
            this.minecraft.setScreen(this.lastScreen);
         }

      }));
      this.renewButton = this.addRenderableWidget(new Button(this.width / 2 + 100, this.height - 32, 90, 20, new TranslatableComponent("mco.selectServer.expiredRenew"), (p_86622_) -> {
         this.onRenew(this.getSelectedServer());
      }));
      this.pendingInvitesButton = this.addRenderableWidget(new RealmsMainScreen.PendingInvitesButton());
      this.newsButton = this.addRenderableWidget(new RealmsMainScreen.NewsButton());
      this.showPopupButton = this.addRenderableWidget(new RealmsMainScreen.ShowPopupButton());
      this.closeButton = this.addRenderableWidget(new RealmsMainScreen.CloseButton());
      this.createTrialButton = this.addRenderableWidget(new Button(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20, new TranslatableComponent("mco.selectServer.trial"), (p_86597_) -> {
         if (this.trialsAvailable && !this.createdTrial) {
            Util.getPlatform().openUri("https://aka.ms/startjavarealmstrial");
            this.minecraft.setScreen(this.lastScreen);
         }
      }));
      this.buyARealmButton = this.addRenderableWidget(new Button(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20, new TranslatableComponent("mco.selectServer.buy"), (p_86565_) -> {
         Util.getPlatform().openUri("https://aka.ms/BuyJavaRealms");
      }));
      this.updateButtonStates((RealmsServer)null);
   }

   void updateButtonStates(@Nullable RealmsServer p_86514_) {
      this.backButton.active = true;
      if (hasParentalConsent() && this.hasFetchedServers) {
         this.playButton.visible = true;
         this.playButton.active = this.shouldPlayButtonBeActive(p_86514_) && !this.shouldShowPopup();
         this.renewButton.visible = this.shouldRenewButtonBeActive(p_86514_);
         this.configureButton.visible = this.shouldConfigureButtonBeVisible(p_86514_);
         this.leaveButton.visible = this.shouldLeaveButtonBeVisible(p_86514_);
         boolean flag = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
         this.createTrialButton.visible = flag;
         this.createTrialButton.active = flag;
         this.buyARealmButton.visible = this.shouldShowPopup();
         this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
         this.renewButton.active = !this.shouldShowPopup();
         this.configureButton.active = !this.shouldShowPopup();
         this.leaveButton.active = !this.shouldShowPopup();
         this.newsButton.active = true;
         this.pendingInvitesButton.active = true;
         this.showPopupButton.active = !this.shouldShowPopup();
      } else {
         hideWidgets(new AbstractWidget[]{this.playButton, this.renewButton, this.configureButton, this.createTrialButton, this.buyARealmButton, this.closeButton, this.newsButton, this.pendingInvitesButton, this.showPopupButton, this.leaveButton});
      }
   }

   private boolean shouldShowPopupButton() {
      return (!this.shouldShowPopup() || this.popupOpenedByUser) && hasParentalConsent() && this.hasFetchedServers;
   }

   boolean shouldPlayButtonBeActive(@Nullable RealmsServer p_86563_) {
      return p_86563_ != null && !p_86563_.expired && p_86563_.state == RealmsServer.State.OPEN;
   }

   private boolean shouldRenewButtonBeActive(@Nullable RealmsServer p_86595_) {
      return p_86595_ != null && p_86595_.expired && this.isSelfOwnedServer(p_86595_);
   }

   private boolean shouldConfigureButtonBeVisible(@Nullable RealmsServer p_86620_) {
      return p_86620_ != null && this.isSelfOwnedServer(p_86620_);
   }

   private boolean shouldLeaveButtonBeVisible(@Nullable RealmsServer p_86645_) {
      return p_86645_ != null && !this.isSelfOwnedServer(p_86645_);
   }

   public void tick() {
      super.tick();
      if (this.pendingInvitesButton != null) {
         this.pendingInvitesButton.tick();
      }

      this.justClosedPopup = false;
      ++this.animTick;
      if (hasParentalConsent()) {
         REALMS_DATA_FETCHER.init();
         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> list = REALMS_DATA_FETCHER.getServers();
            RealmsServer realmsserver = this.getSelectedServer();
            RealmsMainScreen.Entry realmsmainscreen$entry = null;
            this.realmSelectionList.clear();
            boolean flag = !this.hasFetchedServers;
            if (flag) {
               this.hasFetchedServers = true;
            }

            if (list != null) {
               boolean flag1 = false;

               for(RealmsServer realmsserver1 : list) {
                  if (this.isSelfOwnedNonExpiredServer(realmsserver1)) {
                     flag1 = true;
                  }
               }

               this.realmsServers = list;
               if (this.shouldShowMessageInList()) {
                  this.realmSelectionList.addEntry(new RealmsMainScreen.TrialEntry());
               }

               for(RealmsServer realmsserver3 : this.realmsServers) {
                  RealmsMainScreen.ServerEntry realmsmainscreen$serverentry = new RealmsMainScreen.ServerEntry(realmsserver3);
                  this.realmSelectionList.addEntry(realmsmainscreen$serverentry);
                  if (realmsserver != null && realmsserver.id == realmsserver3.id) {
                     realmsmainscreen$entry = realmsmainscreen$serverentry;
                  }
               }

               if (!regionsPinged && flag1) {
                  regionsPinged = true;
                  this.pingRegions();
               }
            }

            if (flag) {
               this.updateButtonStates((RealmsServer)null);
            } else {
               this.realmSelectionList.setSelected(realmsmainscreen$entry);
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
            if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
               NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("mco.configure.world.invite.narration", this.numberOfPendingInvites));
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean flag2 = REALMS_DATA_FETCHER.isTrialAvailable();
            if (flag2 != this.trialsAvailable && this.shouldShowPopup()) {
               this.trialsAvailable = flag2;
               this.showingPopup = false;
            } else {
               this.trialsAvailable = flag2;
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists realmsserverplayerlists = REALMS_DATA_FETCHER.getLivestats();

            for(RealmsServerPlayerList realmsserverplayerlist : realmsserverplayerlists.servers) {
               for(RealmsServer realmsserver2 : this.realmsServers) {
                  if (realmsserver2.id == realmsserverplayerlist.serverId) {
                     realmsserver2.updateServerPing(realmsserverplayerlist);
                     break;
                  }
               }
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
            this.newsLink = REALMS_DATA_FETCHER.newsLink();
         }

         REALMS_DATA_FETCHER.markClean();
         if (this.shouldShowPopup()) {
            ++this.carouselTick;
         }

         if (this.showPopupButton != null) {
            this.showPopupButton.visible = this.shouldShowPopupButton();
            this.showPopupButton.active = this.showPopupButton.visible;
         }

      }
   }

   private void pingRegions() {
      (new Thread(() -> {
         List<RegionPingResult> list = Ping.pingAllRegions();
         RealmsClient realmsclient = RealmsClient.create();
         PingResult pingresult = new PingResult();
         pingresult.pingResults = list;
         pingresult.worldIds = this.getOwnedNonExpiredWorldIds();

         try {
            realmsclient.sendPingResults(pingresult);
         } catch (Throwable throwable) {
            LOGGER.warn("Could not send ping result to Realms: ", throwable);
         }

      })).start();
   }

   private List<Long> getOwnedNonExpiredWorldIds() {
      List<Long> list = Lists.newArrayList();

      for(RealmsServer realmsserver : this.realmsServers) {
         if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
            list.add(realmsserver.id);
         }
      }

      return list;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.stopRealmsFetcher();
   }

   public void setCreatedTrial(boolean p_167191_) {
      this.createdTrial = p_167191_;
   }

   void onRenew(@Nullable RealmsServer p_193500_) {
      if (p_193500_ != null) {
         String s = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + p_193500_.remoteSubscriptionId + "&profileId=" + this.minecraft.getUser().getUuid() + "&ref=" + (p_193500_.expiredTrial ? "expiredTrial" : "expiredRealm");
         this.minecraft.keyboardHandler.setClipboard(s);
         Util.getPlatform().openUri(s);
      }

   }

   private void checkClientCompatability() {
      if (!checkedClientCompatability) {
         checkedClientCompatability = true;
         (new Thread("MCO Compatability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.clientCompatible();
                  if (realmsclient$compatibleversionresponse == RealmsClient.CompatibleVersionResponse.OUTDATED) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                     return;
                  }

                  if (realmsclient$compatibleversionresponse == RealmsClient.CompatibleVersionResponse.OTHER) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                     return;
                  }

                  RealmsMainScreen.this.checkParentalConsent();
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.checkedClientCompatability = false;
                  RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)realmsserviceexception);
                  if (realmsserviceexception.httpResultCode == 401) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(new TranslatableComponent("mco.error.invalid.session.title"), new TranslatableComponent("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                  } else {
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
                     });
                  }
               }

            }
         }).start();
      }

   }

   void checkParentalConsent() {
      (new Thread("MCO Compatability Checker #1") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               Boolean obool = realmsclient.mcoEnabled();
               if (obool) {
                  RealmsMainScreen.LOGGER.info("Realms is available for this user");
                  RealmsMainScreen.hasParentalConsent = true;
               } else {
                  RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                  RealmsMainScreen.hasParentalConsent = false;
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.minecraft.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen));
                  });
               }

               RealmsMainScreen.checkedParentalConsent = true;
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)realmsserviceexception);
               RealmsMainScreen.this.minecraft.execute(() -> {
                  RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
               });
            }

         }
      }).start();
   }

   private void switchToStage() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToStage();
                     RealmsMainScreen.LOGGER.info("Switched to stage");
                     RealmsMainScreen.REALMS_DATA_FETCHER.forceUpdate();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", (Object)realmsserviceexception.toString());
               }

            }
         }).start();
      }

   }

   private void switchToLocal() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
         (new Thread("MCO Local Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToLocal();
                     RealmsMainScreen.LOGGER.info("Switched to local");
                     RealmsMainScreen.REALMS_DATA_FETCHER.forceUpdate();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", (Object)realmsserviceexception.toString());
               }

            }
         }).start();
      }

   }

   private void switchToProd() {
      RealmsClient.switchToProd();
      REALMS_DATA_FETCHER.forceUpdate();
   }

   private void stopRealmsFetcher() {
      REALMS_DATA_FETCHER.stop();
   }

   void configureClicked(@Nullable RealmsServer p_86657_) {
      if (p_86657_ != null && (this.minecraft.getUser().getUuid().equals(p_86657_.ownerUUID) || overrideConfigure)) {
         this.saveListScrollPosition();
         this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, p_86657_.id));
      }

   }

   void leaveClicked(@Nullable RealmsServer p_86670_) {
      if (p_86670_ != null && !this.minecraft.getUser().getUuid().equals(p_86670_.ownerUUID)) {
         this.saveListScrollPosition();
         Component component = new TranslatableComponent("mco.configure.world.leave.question.line1");
         Component component1 = new TranslatableComponent("mco.configure.world.leave.question.line2");
         this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_193490_) -> {
            this.leaveServer(p_193490_, p_86670_);
         }, RealmsLongConfirmationScreen.Type.Info, component, component1, true));
      }

   }

   private void saveListScrollPosition() {
      lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
   }

   @Nullable
   private RealmsServer getSelectedServer() {
      if (this.realmSelectionList == null) {
         return null;
      } else {
         RealmsMainScreen.Entry realmsmainscreen$entry = this.realmSelectionList.getSelected();
         return realmsmainscreen$entry != null ? realmsmainscreen$entry.getServer() : null;
      }
   }

   private void leaveServer(boolean p_193494_, final RealmsServer p_193495_) {
      if (p_193494_) {
         (new Thread("Realms-leave-server") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.uninviteMyselfFrom(p_193495_.id);
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.removeServer(p_193495_);
                  });
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't configure world");
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this));
                  });
               }

            }
         }).start();
      }

      this.minecraft.setScreen(this);
   }

   void removeServer(RealmsServer p_86677_) {
      this.realmsServers = REALMS_DATA_FETCHER.removeItem(p_86677_);
      this.realmSelectionList.children().removeIf((p_193487_) -> {
         RealmsServer realmsserver = p_193487_.getServer();
         return realmsserver != null && realmsserver.id == p_86677_.id;
      });
      this.realmSelectionList.setSelected((RealmsMainScreen.Entry)null);
      this.updateButtonStates((RealmsServer)null);
      this.playButton.active = false;
   }

   public void resetScreen() {
      if (this.realmSelectionList != null) {
         this.realmSelectionList.setSelected((RealmsMainScreen.Entry)null);
      }

   }

   public boolean keyPressed(int p_86401_, int p_86402_, int p_86403_) {
      if (p_86401_ == 256) {
         this.keyCombos.forEach(KeyCombo::reset);
         this.onClosePopup();
         return true;
      } else {
         return super.keyPressed(p_86401_, p_86402_, p_86403_);
      }
   }

   void onClosePopup() {
      if (this.shouldShowPopup() && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public boolean charTyped(char p_86388_, int p_86389_) {
      this.keyCombos.forEach((p_193484_) -> {
         p_193484_.keyPressed(p_86388_);
      });
      return true;
   }

   public void render(PoseStack p_86413_, int p_86414_, int p_86415_, float p_86416_) {
      this.hoveredElement = RealmsMainScreen.HoveredElement.NONE;
      this.toolTip = null;
      this.renderBackground(p_86413_);
      this.realmSelectionList.render(p_86413_, p_86414_, p_86415_, p_86416_);
      this.drawRealmsLogo(p_86413_, this.width / 2 - 50, 7);
      if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
         this.renderStage(p_86413_);
      }

      if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
         this.renderLocal(p_86413_);
      }

      if (this.shouldShowPopup()) {
         this.drawPopup(p_86413_);
      } else {
         if (this.showingPopup) {
            this.updateButtonStates((RealmsServer)null);
            if (!this.realmsSelectionListAdded) {
               this.addWidget(this.realmSelectionList);
               this.realmsSelectionListAdded = true;
            }

            this.playButton.active = this.shouldPlayButtonBeActive(this.getSelectedServer());
         }

         this.showingPopup = false;
      }

      super.render(p_86413_, p_86414_, p_86415_, p_86416_);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(p_86413_, this.toolTip, p_86414_, p_86415_);
      }

      if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
         RenderSystem.setShaderTexture(0, TRIAL_ICON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int i = 8;
         int j = 8;
         int k = 0;
         if ((Util.getMillis() / 800L & 1L) == 1L) {
            k = 8;
         }

         GuiComponent.blit(p_86413_, this.createTrialButton.x + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.y + this.createTrialButton.getHeight() / 2 - 4, 0.0F, (float)k, 8, 8, 8, 16);
      }

   }

   private void drawRealmsLogo(PoseStack p_86409_, int p_86410_, int p_86411_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, LOGO_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      p_86409_.pushPose();
      p_86409_.scale(0.5F, 0.5F, 0.5F);
      GuiComponent.blit(p_86409_, p_86410_ * 2, p_86411_ * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
      p_86409_.popPose();
   }

   public boolean mouseClicked(double p_86397_, double p_86398_, int p_86399_) {
      if (this.isOutsidePopup(p_86397_, p_86398_) && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
         this.justClosedPopup = true;
         return true;
      } else {
         return super.mouseClicked(p_86397_, p_86398_, p_86399_);
      }
   }

   private boolean isOutsidePopup(double p_86394_, double p_86395_) {
      int i = this.popupX0();
      int j = this.popupY0();
      return p_86394_ < (double)(i - 5) || p_86394_ > (double)(i + 315) || p_86395_ < (double)(j - 5) || p_86395_ > (double)(j + 171);
   }

   private void drawPopup(PoseStack p_202330_) {
      int i = this.popupX0();
      int j = this.popupY0();
      if (!this.showingPopup) {
         this.carouselIndex = 0;
         this.carouselTick = 0;
         this.hasSwitchedCarouselImage = true;
         this.updateButtonStates((RealmsServer)null);
         if (this.realmsSelectionListAdded) {
            this.removeWidget(this.realmSelectionList);
            this.realmsSelectionListAdded = false;
         }

         NarratorChatListener.INSTANCE.sayNow(POPUP_TEXT);
      }

      if (this.hasFetchedServers) {
         this.showingPopup = true;
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.7F);
      RenderSystem.enableBlend();
      RenderSystem.setShaderTexture(0, DARKEN_LOCATION);
      int k = 0;
      int l = 32;
      GuiComponent.blit(p_202330_, 0, 32, 0.0F, 0.0F, this.width, this.height - 40 - 32, 310, 166);
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, POPUP_LOCATION);
      GuiComponent.blit(p_202330_, i, j, 0.0F, 0.0F, 310, 166, 310, 166);
      if (!teaserImages.isEmpty()) {
         RenderSystem.setShaderTexture(0, teaserImages.get(this.carouselIndex));
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         GuiComponent.blit(p_202330_, i + 7, j + 7, 0.0F, 0.0F, 195, 152, 195, 152);
         if (this.carouselTick % 95 < 5) {
            if (!this.hasSwitchedCarouselImage) {
               this.carouselIndex = (this.carouselIndex + 1) % teaserImages.size();
               this.hasSwitchedCarouselImage = true;
            }
         } else {
            this.hasSwitchedCarouselImage = false;
         }
      }

      this.formattedPopup.renderLeftAlignedNoShadow(p_202330_, this.width / 2 + 52, j + 7, 10, 5000268);
   }

   int popupX0() {
      return (this.width - 310) / 2;
   }

   int popupY0() {
      return this.height / 2 - 80;
   }

   void drawInvitationPendingIcon(PoseStack p_86425_, int p_86426_, int p_86427_, int p_86428_, int p_86429_, boolean p_86430_, boolean p_86431_) {
      int i = this.numberOfPendingInvites;
      boolean flag = this.inPendingInvitationArea((double)p_86426_, (double)p_86427_);
      boolean flag1 = p_86431_ && p_86430_;
      if (flag1) {
         float f = 0.25F + (1.0F + Mth.sin((float)this.animTick * 0.5F)) * 0.25F;
         int j = -16777216 | (int)(f * 64.0F) << 16 | (int)(f * 64.0F) << 8 | (int)(f * 64.0F) << 0;
         this.fillGradient(p_86425_, p_86428_ - 2, p_86429_ - 2, p_86428_ + 18, p_86429_ + 18, j, j);
         j = -16777216 | (int)(f * 255.0F) << 16 | (int)(f * 255.0F) << 8 | (int)(f * 255.0F) << 0;
         this.fillGradient(p_86425_, p_86428_ - 2, p_86429_ - 2, p_86428_ + 18, p_86429_ - 1, j, j);
         this.fillGradient(p_86425_, p_86428_ - 2, p_86429_ - 2, p_86428_ - 1, p_86429_ + 18, j, j);
         this.fillGradient(p_86425_, p_86428_ + 17, p_86429_ - 2, p_86428_ + 18, p_86429_ + 18, j, j);
         this.fillGradient(p_86425_, p_86428_ - 2, p_86429_ + 17, p_86428_ + 18, p_86429_ + 18, j, j);
      }

      RenderSystem.setShaderTexture(0, INVITE_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      boolean flag3 = p_86431_ && p_86430_;
      float f2 = flag3 ? 16.0F : 0.0F;
      GuiComponent.blit(p_86425_, p_86428_, p_86429_ - 6, f2, 0.0F, 15, 25, 31, 25);
      boolean flag2 = p_86431_ && i != 0;
      if (flag2) {
         int k = (Math.min(i, 6) - 1) * 8;
         int l = (int)(Math.max(0.0F, Math.max(Mth.sin((float)(10 + this.animTick) * 0.57F), Mth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RenderSystem.setShaderTexture(0, INVITATION_ICONS_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         float f1 = flag ? 8.0F : 0.0F;
         GuiComponent.blit(p_86425_, p_86428_ + 4, p_86429_ + 4 + l, (float)k, f1, 8, 8, 48, 16);
      }

      int j1 = p_86426_ + 12;
      boolean flag4 = p_86431_ && flag;
      if (flag4) {
         Component component = i == 0 ? NO_PENDING_INVITES_TEXT : PENDING_INVITES_TEXT;
         int i1 = this.font.width(component);
         this.fillGradient(p_86425_, j1 - 3, p_86427_ - 3, j1 + i1 + 3, p_86427_ + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_86425_, component, (float)j1, (float)p_86427_, -1);
      }

   }

   private boolean inPendingInvitationArea(double p_86572_, double p_86573_) {
      int i = this.width / 2 + 50;
      int j = this.width / 2 + 66;
      int k = 11;
      int l = 23;
      if (this.numberOfPendingInvites != 0) {
         i -= 3;
         j += 3;
         k -= 5;
         l += 5;
      }

      return (double)i <= p_86572_ && p_86572_ <= (double)j && (double)k <= p_86573_ && p_86573_ <= (double)l;
   }

   public void play(@Nullable RealmsServer p_86516_, Screen p_86517_) {
      if (p_86516_ != null) {
         try {
            if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if (this.connectLock.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException interruptedexception) {
            return;
         }

         this.dontSetConnectedToRealms = true;
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(p_86517_, new GetServerDetailsTask(this, p_86517_, p_86516_, this.connectLock)));
      }

   }

   boolean isSelfOwnedServer(RealmsServer p_86684_) {
      return p_86684_.ownerUUID != null && p_86684_.ownerUUID.equals(this.minecraft.getUser().getUuid());
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer p_86689_) {
      return this.isSelfOwnedServer(p_86689_) && !p_86689_.expired;
   }

   void drawExpired(PoseStack p_86577_, int p_86578_, int p_86579_, int p_86580_, int p_86581_) {
      RenderSystem.setShaderTexture(0, EXPIRED_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(p_86577_, p_86578_, p_86579_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_86580_ >= p_86578_ && p_86580_ <= p_86578_ + 9 && p_86581_ >= p_86579_ && p_86581_ <= p_86579_ + 27 && p_86581_ < this.height - 40 && p_86581_ > 32 && !this.shouldShowPopup()) {
         this.setTooltip(SERVER_EXPIRED_TOOLTIP);
      }

   }

   void drawExpiring(PoseStack p_86538_, int p_86539_, int p_86540_, int p_86541_, int p_86542_, int p_86543_) {
      RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.animTick % 20 < 10) {
         GuiComponent.blit(p_86538_, p_86539_, p_86540_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         GuiComponent.blit(p_86538_, p_86539_, p_86540_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      if (p_86541_ >= p_86539_ && p_86541_ <= p_86539_ + 9 && p_86542_ >= p_86540_ && p_86542_ <= p_86540_ + 27 && p_86542_ < this.height - 40 && p_86542_ > 32 && !this.shouldShowPopup()) {
         if (p_86543_ <= 0) {
            this.setTooltip(SERVER_EXPIRES_SOON_TOOLTIP);
         } else if (p_86543_ == 1) {
            this.setTooltip(SERVER_EXPIRES_IN_DAY_TOOLTIP);
         } else {
            this.setTooltip(new TranslatableComponent("mco.selectServer.expires.days", p_86543_));
         }
      }

   }

   void drawOpen(PoseStack p_86602_, int p_86603_, int p_86604_, int p_86605_, int p_86606_) {
      RenderSystem.setShaderTexture(0, ON_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(p_86602_, p_86603_, p_86604_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_86605_ >= p_86603_ && p_86605_ <= p_86603_ + 9 && p_86606_ >= p_86604_ && p_86606_ <= p_86604_ + 27 && p_86606_ < this.height - 40 && p_86606_ > 32 && !this.shouldShowPopup()) {
         this.setTooltip(SERVER_OPEN_TOOLTIP);
      }

   }

   void drawClose(PoseStack p_86627_, int p_86628_, int p_86629_, int p_86630_, int p_86631_) {
      RenderSystem.setShaderTexture(0, OFF_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(p_86627_, p_86628_, p_86629_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_86630_ >= p_86628_ && p_86630_ <= p_86628_ + 9 && p_86631_ >= p_86629_ && p_86631_ <= p_86629_ + 27 && p_86631_ < this.height - 40 && p_86631_ > 32 && !this.shouldShowPopup()) {
         this.setTooltip(SERVER_CLOSED_TOOLTIP);
      }

   }

   void drawLeave(PoseStack p_86649_, int p_86650_, int p_86651_, int p_86652_, int p_86653_) {
      boolean flag = false;
      if (p_86652_ >= p_86650_ && p_86652_ <= p_86650_ + 28 && p_86653_ >= p_86651_ && p_86653_ <= p_86651_ + 28 && p_86653_ < this.height - 40 && p_86653_ > 32 && !this.shouldShowPopup()) {
         flag = true;
      }

      RenderSystem.setShaderTexture(0, LEAVE_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 28.0F : 0.0F;
      GuiComponent.blit(p_86649_, p_86650_, p_86651_, f, 0.0F, 28, 28, 56, 28);
      if (flag) {
         this.setTooltip(LEAVE_SERVER_TOOLTIP);
         this.hoveredElement = RealmsMainScreen.HoveredElement.LEAVE;
      }

   }

   void drawConfigure(PoseStack p_86662_, int p_86663_, int p_86664_, int p_86665_, int p_86666_) {
      boolean flag = false;
      if (p_86665_ >= p_86663_ && p_86665_ <= p_86663_ + 28 && p_86666_ >= p_86664_ && p_86666_ <= p_86664_ + 28 && p_86666_ < this.height - 40 && p_86666_ > 32 && !this.shouldShowPopup()) {
         flag = true;
      }

      RenderSystem.setShaderTexture(0, CONFIGURE_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 28.0F : 0.0F;
      GuiComponent.blit(p_86662_, p_86663_, p_86664_, f, 0.0F, 28, 28, 56, 28);
      if (flag) {
         this.setTooltip(CONFIGURE_SERVER_TOOLTIP);
         this.hoveredElement = RealmsMainScreen.HoveredElement.CONFIGURE;
      }

   }

   protected void renderMousehoverTooltip(PoseStack p_86442_, List<Component> p_86443_, int p_86444_, int p_86445_) {
      if (!p_86443_.isEmpty()) {
         int i = 0;
         int j = 0;

         for(Component component : p_86443_) {
            int k = this.font.width(component);
            if (k > j) {
               j = k;
            }
         }

         int i1 = p_86444_ - j - 5;
         int j1 = p_86445_;
         if (i1 < 0) {
            i1 = p_86444_ + 12;
         }

         for(Component component1 : p_86443_) {
            int l = j1 - (i == 0 ? 3 : 0) + i;
            this.fillGradient(p_86442_, i1 - 3, l, i1 + j + 3, j1 + 8 + 3 + i, -1073741824, -1073741824);
            this.font.drawShadow(p_86442_, component1, (float)i1, (float)(j1 + i), 16777215);
            i += 10;
         }

      }
   }

   void renderMoreInfo(PoseStack p_86418_, int p_86419_, int p_86420_, int p_86421_, int p_86422_, boolean p_86423_) {
      boolean flag = false;
      if (p_86419_ >= p_86421_ && p_86419_ <= p_86421_ + 20 && p_86420_ >= p_86422_ && p_86420_ <= p_86422_ + 20) {
         flag = true;
      }

      RenderSystem.setShaderTexture(0, QUESTIONMARK_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = p_86423_ ? 20.0F : 0.0F;
      GuiComponent.blit(p_86418_, p_86421_, p_86422_, f, 0.0F, 20, 20, 40, 20);
      if (flag) {
         this.setTooltip(SERVER_INFO_TOOLTIP);
      }

   }

   void renderNews(PoseStack p_86433_, int p_86434_, int p_86435_, boolean p_86436_, int p_86437_, int p_86438_, boolean p_86439_, boolean p_86440_) {
      boolean flag = false;
      if (p_86434_ >= p_86437_ && p_86434_ <= p_86437_ + 20 && p_86435_ >= p_86438_ && p_86435_ <= p_86438_ + 20) {
         flag = true;
      }

      RenderSystem.setShaderTexture(0, NEWS_LOCATION);
      if (p_86440_) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
      }

      boolean flag1 = p_86440_ && p_86439_;
      float f = flag1 ? 20.0F : 0.0F;
      GuiComponent.blit(p_86433_, p_86437_, p_86438_, f, 0.0F, 20, 20, 40, 20);
      if (flag && p_86440_) {
         this.setTooltip(NEWS_TOOLTIP);
      }

      if (p_86436_ && p_86440_) {
         int i = flag ? 0 : (int)(Math.max(0.0F, Math.max(Mth.sin((float)(10 + this.animTick) * 0.57F), Mth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RenderSystem.setShaderTexture(0, INVITATION_ICONS_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         GuiComponent.blit(p_86433_, p_86437_ + 10, p_86438_ + 2 + i, 40.0F, 0.0F, 8, 8, 48, 16);
      }

   }

   private void renderLocal(PoseStack p_86532_) {
      String s = "LOCAL!";
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      p_86532_.pushPose();
      p_86532_.translate((double)(this.width / 2 - 25), 20.0D, 0.0D);
      p_86532_.mulPose(Vector3f.ZP.rotationDegrees(-20.0F));
      p_86532_.scale(1.5F, 1.5F, 1.5F);
      this.font.draw(p_86532_, "LOCAL!", 0.0F, 0.0F, 8388479);
      p_86532_.popPose();
   }

   private void renderStage(PoseStack p_86575_) {
      String s = "STAGE!";
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      p_86575_.pushPose();
      p_86575_.translate((double)(this.width / 2 - 25), 20.0D, 0.0D);
      p_86575_.mulPose(Vector3f.ZP.rotationDegrees(-20.0F));
      p_86575_.scale(1.5F, 1.5F, 1.5F);
      this.font.draw(p_86575_, "STAGE!", 0.0F, 0.0F, -256);
      p_86575_.popPose();
   }

   public RealmsMainScreen newScreen() {
      RealmsMainScreen realmsmainscreen = new RealmsMainScreen(this.lastScreen);
      realmsmainscreen.init(this.minecraft, this.width, this.height);
      return realmsmainscreen;
   }

   public static void updateTeaserImages(ResourceManager p_86407_) {
      Collection<ResourceLocation> collection = p_86407_.listResources("textures/gui/images", (p_193497_) -> {
         return p_193497_.endsWith(".png");
      });
      teaserImages = collection.stream().filter((p_193492_) -> {
         return p_193492_.getNamespace().equals("realms");
      }).toList();
   }

   void setTooltip(Component... p_86527_) {
      this.toolTip = Arrays.asList(p_86527_);
   }

   private void pendingButtonPress(Button p_86519_) {
      this.minecraft.setScreen(new RealmsPendingInvitesScreen(this.lastScreen));
   }

   @OnlyIn(Dist.CLIENT)
   class CloseButton extends Button {
      public CloseButton() {
         super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, new TranslatableComponent("mco.selectServer.close"), null);
      }

      @Override
      public void onPress() {
            RealmsMainScreen.this.onClosePopup();
      }

      public void renderButton(PoseStack p_86777_, int p_86778_, int p_86779_, float p_86780_) {
         RenderSystem.setShaderTexture(0, RealmsMainScreen.CROSS_ICON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         float f = this.isHoveredOrFocused() ? 12.0F : 0.0F;
         blit(p_86777_, this.x, this.y, 0.0F, f, 12, 12, 12, 24);
         if (this.isMouseOver((double)p_86778_, (double)p_86779_)) {
            RealmsMainScreen.this.setTooltip(this.getMessage());
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract class Entry extends ObjectSelectionList.Entry<RealmsMainScreen.Entry> {
      @Nullable
      public abstract RealmsServer getServer();
   }

   @OnlyIn(Dist.CLIENT)
   static enum HoveredElement {
      NONE,
      EXPIRED,
      LEAVE,
      CONFIGURE;
   }

   @OnlyIn(Dist.CLIENT)
   class NewsButton extends Button {
      public NewsButton() {
         super(RealmsMainScreen.this.width - 62, 6, 20, 20, new TranslatableComponent("mco.news"), null);
      }

      @Override
      public void onPress() {
            if (RealmsMainScreen.this.newsLink != null) {
               Util.getPlatform().openUri(RealmsMainScreen.this.newsLink);
               if (RealmsMainScreen.this.hasUnreadNews) {
                  RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = RealmsPersistence.readFile();
                  realmspersistence$realmspersistencedata.hasUnreadNews = false;
                  RealmsMainScreen.this.hasUnreadNews = false;
                  RealmsPersistence.writeFile(realmspersistence$realmspersistencedata);
               }

            }
      }

      public void renderButton(PoseStack p_86806_, int p_86807_, int p_86808_, float p_86809_) {
         RealmsMainScreen.this.renderNews(p_86806_, p_86807_, p_86808_, RealmsMainScreen.this.hasUnreadNews, this.x, this.y, this.isHoveredOrFocused(), this.active);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInvitesButton extends Button {
      public PendingInvitesButton() {
         super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, TextComponent.EMPTY, RealmsMainScreen.this::pendingButtonPress);
      }

      public void tick() {
         this.setMessage(RealmsMainScreen.this.numberOfPendingInvites == 0 ? RealmsMainScreen.NO_PENDING_INVITES_TEXT : RealmsMainScreen.PENDING_INVITES_TEXT);
      }

      public void renderButton(PoseStack p_86817_, int p_86818_, int p_86819_, float p_86820_) {
         RealmsMainScreen.this.drawInvitationPendingIcon(p_86817_, p_86818_, p_86819_, this.x, this.y, this.isHoveredOrFocused(), this.active);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
      public RealmSelectionList() {
         super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
      }

      public boolean isFocused() {
         return RealmsMainScreen.this.getFocused() == this;
      }

      public boolean keyPressed(int p_86840_, int p_86841_, int p_86842_) {
         if (p_86840_ != 257 && p_86840_ != 32 && p_86840_ != 335) {
            return super.keyPressed(p_86840_, p_86841_, p_86842_);
         } else {
            RealmsMainScreen.Entry realmsmainscreen$entry = this.getSelected();
            return realmsmainscreen$entry == null ? super.keyPressed(p_86840_, p_86841_, p_86842_) : realmsmainscreen$entry.mouseClicked(0.0D, 0.0D, 0);
         }
      }

      public boolean mouseClicked(double p_86828_, double p_86829_, int p_86830_) {
         if (p_86830_ == 0 && p_86828_ < (double)this.getScrollbarPosition() && p_86829_ >= (double)this.y0 && p_86829_ <= (double)this.y1) {
            int i = RealmsMainScreen.this.realmSelectionList.getRowLeft();
            int j = this.getScrollbarPosition();
            int k = (int)Math.floor(p_86829_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int l = k / this.itemHeight;
            if (p_86828_ >= (double)i && p_86828_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.itemClicked(k, l, p_86828_, p_86829_, this.width);
               this.selectItem(l);
            }

            return true;
         } else {
            return super.mouseClicked(p_86828_, p_86829_, p_86830_);
         }
      }

      public void setSelected(@Nullable RealmsMainScreen.Entry p_86849_) {
         super.setSelected(p_86849_);
         if (p_86849_ != null) {
            RealmsMainScreen.this.updateButtonStates(p_86849_.getServer());
         } else {
            RealmsMainScreen.this.updateButtonStates((RealmsServer)null);
         }

      }

      public void itemClicked(int p_86834_, int p_86835_, double p_86836_, double p_86837_, int p_86838_) {
         RealmsMainScreen.Entry realmsmainscreen$entry = this.getEntry(p_86835_);
         if (realmsmainscreen$entry instanceof RealmsMainScreen.TrialEntry) {
            RealmsMainScreen.this.popupOpenedByUser = true;
         } else {
            RealmsServer realmsserver = realmsmainscreen$entry.getServer();
            if (realmsserver != null) {
               if (realmsserver.state == RealmsServer.State.UNINITIALIZED) {
                  Minecraft.getInstance().setScreen(new RealmsCreateRealmScreen(realmsserver, RealmsMainScreen.this));
               } else {
                  if (RealmsMainScreen.this.hoveredElement == RealmsMainScreen.HoveredElement.CONFIGURE) {
                     RealmsMainScreen.this.configureClicked(realmsserver);
                  } else if (RealmsMainScreen.this.hoveredElement == RealmsMainScreen.HoveredElement.LEAVE) {
                     RealmsMainScreen.this.leaveClicked(realmsserver);
                  } else if (RealmsMainScreen.this.hoveredElement == RealmsMainScreen.HoveredElement.EXPIRED) {
                     RealmsMainScreen.this.onRenew(realmsserver);
                  } else if (RealmsMainScreen.this.shouldPlayButtonBeActive(realmsserver)) {
                     if (Util.getMillis() - RealmsMainScreen.this.lastClickTime < 250L && this.isSelectedItem(p_86835_)) {
                        RealmsMainScreen.this.play(realmsserver, RealmsMainScreen.this);
                     }

                     RealmsMainScreen.this.lastClickTime = Util.getMillis();
                  }

               }
            }
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerEntry extends RealmsMainScreen.Entry {
      private static final int SKIN_HEAD_LARGE_WIDTH = 36;
      private final RealmsServer serverData;

      public ServerEntry(RealmsServer p_86856_) {
         this.serverData = p_86856_;
      }

      public void render(PoseStack p_86866_, int p_86867_, int p_86868_, int p_86869_, int p_86870_, int p_86871_, int p_86872_, int p_86873_, boolean p_86874_, float p_86875_) {
         this.renderMcoServerItem(this.serverData, p_86866_, p_86869_, p_86868_, p_86872_, p_86873_);
      }

      public boolean mouseClicked(double p_86858_, double p_86859_, int p_86860_) {
         if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
            RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this));
         }

         return true;
      }

      private void renderMcoServerItem(RealmsServer p_86879_, PoseStack p_86880_, int p_86881_, int p_86882_, int p_86883_, int p_86884_) {
         this.renderLegacy(p_86879_, p_86880_, p_86881_ + 36, p_86882_, p_86883_, p_86884_);
      }

      private void renderLegacy(RealmsServer p_86886_, PoseStack p_86887_, int p_86888_, int p_86889_, int p_86890_, int p_86891_) {
         if (p_86886_.state == RealmsServer.State.UNINITIALIZED) {
            RenderSystem.setShaderTexture(0, RealmsMainScreen.WORLDICON_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            GuiComponent.blit(p_86887_, p_86888_ + 10, p_86889_ + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            float f = 0.5F + (1.0F + Mth.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
            int k2 = -16777216 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
            GuiComponent.drawCenteredString(p_86887_, RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, p_86888_ + 10 + 40 + 75, p_86889_ + 12, k2);
         } else {
            int i = 225;
            int j = 2;
            if (p_86886_.expired) {
               RealmsMainScreen.this.drawExpired(p_86887_, p_86888_ + 225 - 14, p_86889_ + 2, p_86890_, p_86891_);
            } else if (p_86886_.state == RealmsServer.State.CLOSED) {
               RealmsMainScreen.this.drawClose(p_86887_, p_86888_ + 225 - 14, p_86889_ + 2, p_86890_, p_86891_);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(p_86886_) && p_86886_.daysLeft < 7) {
               RealmsMainScreen.this.drawExpiring(p_86887_, p_86888_ + 225 - 14, p_86889_ + 2, p_86890_, p_86891_, p_86886_.daysLeft);
            } else if (p_86886_.state == RealmsServer.State.OPEN) {
               RealmsMainScreen.this.drawOpen(p_86887_, p_86888_ + 225 - 14, p_86889_ + 2, p_86890_, p_86891_);
            }

            if (!RealmsMainScreen.this.isSelfOwnedServer(p_86886_) && !RealmsMainScreen.overrideConfigure) {
               RealmsMainScreen.this.drawLeave(p_86887_, p_86888_ + 225, p_86889_ + 2, p_86890_, p_86891_);
            } else {
               RealmsMainScreen.this.drawConfigure(p_86887_, p_86888_ + 225, p_86889_ + 2, p_86890_, p_86891_);
            }

            if (!"0".equals(p_86886_.serverPing.nrOfPlayers)) {
               String s = ChatFormatting.GRAY + p_86886_.serverPing.nrOfPlayers;
               RealmsMainScreen.this.font.draw(p_86887_, s, (float)(p_86888_ + 207 - RealmsMainScreen.this.font.width(s)), (float)(p_86889_ + 3), 8421504);
               if (p_86890_ >= p_86888_ + 207 - RealmsMainScreen.this.font.width(s) && p_86890_ <= p_86888_ + 207 && p_86891_ >= p_86889_ + 1 && p_86891_ <= p_86889_ + 10 && p_86891_ < RealmsMainScreen.this.height - 40 && p_86891_ > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  RealmsMainScreen.this.setTooltip(new TextComponent(p_86886_.serverPing.playerList));
               }
            }

            if (RealmsMainScreen.this.isSelfOwnedServer(p_86886_) && p_86886_.expired) {
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.enableBlend();
               RenderSystem.setShaderTexture(0, RealmsMainScreen.BUTTON_LOCATION);
               RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
               Component component;
               Component component1;
               if (p_86886_.expiredTrial) {
                  component = RealmsMainScreen.TRIAL_EXPIRED_TEXT;
                  component1 = RealmsMainScreen.SUBSCRIPTION_CREATE_TEXT;
               } else {
                  component = RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
                  component1 = RealmsMainScreen.SUBSCRIPTION_RENEW_TEXT;
               }

               int l = RealmsMainScreen.this.font.width(component1) + 17;
               int i1 = 16;
               int j1 = p_86888_ + RealmsMainScreen.this.font.width(component) + 8;
               int k1 = p_86889_ + 13;
               boolean flag = false;
               if (p_86890_ >= j1 && p_86890_ < j1 + l && p_86891_ > k1 && p_86891_ <= k1 + 16 && p_86891_ < RealmsMainScreen.this.height - 40 && p_86891_ > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  flag = true;
                  RealmsMainScreen.this.hoveredElement = RealmsMainScreen.HoveredElement.EXPIRED;
               }

               int l1 = flag ? 2 : 1;
               GuiComponent.blit(p_86887_, j1, k1, 0.0F, (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               GuiComponent.blit(p_86887_, j1 + l / 2, k1, (float)(200 - l / 2), (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               GuiComponent.blit(p_86887_, j1, k1 + 8, 0.0F, (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               GuiComponent.blit(p_86887_, j1 + l / 2, k1 + 8, (float)(200 - l / 2), (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               RenderSystem.disableBlend();
               int i2 = p_86889_ + 11 + 5;
               int j2 = flag ? 16777120 : 16777215;
               RealmsMainScreen.this.font.draw(p_86887_, component, (float)(p_86888_ + 2), (float)(i2 + 1), 15553363);
               GuiComponent.drawCenteredString(p_86887_, RealmsMainScreen.this.font, component1, j1 + l / 2, i2 + 1, j2);
            } else {
               if (p_86886_.worldType == RealmsServer.WorldType.MINIGAME) {
                  int l2 = 13413468;
                  int k = RealmsMainScreen.this.font.width(RealmsMainScreen.SELECT_MINIGAME_PREFIX);
                  RealmsMainScreen.this.font.draw(p_86887_, RealmsMainScreen.SELECT_MINIGAME_PREFIX, (float)(p_86888_ + 2), (float)(p_86889_ + 12), 13413468);
                  RealmsMainScreen.this.font.draw(p_86887_, p_86886_.getMinigameName(), (float)(p_86888_ + 2 + k), (float)(p_86889_ + 12), 7105644);
               } else {
                  RealmsMainScreen.this.font.draw(p_86887_, p_86886_.getDescription(), (float)(p_86888_ + 2), (float)(p_86889_ + 12), 7105644);
               }

               if (!RealmsMainScreen.this.isSelfOwnedServer(p_86886_)) {
                  RealmsMainScreen.this.font.draw(p_86887_, p_86886_.owner, (float)(p_86888_ + 2), (float)(p_86889_ + 12 + 11), 5000268);
               }
            }

            RealmsMainScreen.this.font.draw(p_86887_, p_86886_.getName(), (float)(p_86888_ + 2), (float)(p_86889_ + 1), 16777215);
            RealmsTextureManager.withBoundFace(p_86886_.ownerUUID, () -> {
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               GuiComponent.blit(p_86887_, p_86888_ - 36, p_86889_, 32, 32, 8.0F, 8.0F, 8, 8, 64, 64);
               GuiComponent.blit(p_86887_, p_86888_ - 36, p_86889_, 32, 32, 40.0F, 8.0F, 8, 8, 64, 64);
            });
         }
      }

      public Component getNarration() {
         return (Component)(this.serverData.state == RealmsServer.State.UNINITIALIZED ? RealmsMainScreen.UNITIALIZED_WORLD_NARRATION : new TranslatableComponent("narrator.select", this.serverData.name));
      }

      @Nullable
      public RealmsServer getServer() {
         return this.serverData;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ShowPopupButton extends Button {
      public ShowPopupButton() {
         super(RealmsMainScreen.this.width - 37, 6, 20, 20, new TranslatableComponent("mco.selectServer.info"), null);
      }

      @Override
      public void onPress() {
            RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser;
      }

      public void renderButton(PoseStack p_86899_, int p_86900_, int p_86901_, float p_86902_) {
         RealmsMainScreen.this.renderMoreInfo(p_86899_, p_86900_, p_86901_, this.x, this.y, this.isHoveredOrFocused());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TrialEntry extends RealmsMainScreen.Entry {
      public void render(PoseStack p_86921_, int p_86922_, int p_86923_, int p_86924_, int p_86925_, int p_86926_, int p_86927_, int p_86928_, boolean p_86929_, float p_86930_) {
         this.renderTrialItem(p_86921_, p_86922_, p_86924_, p_86923_, p_86927_, p_86928_);
      }

      public boolean mouseClicked(double p_86910_, double p_86911_, int p_86912_) {
         RealmsMainScreen.this.popupOpenedByUser = true;
         return true;
      }

      private void renderTrialItem(PoseStack p_86914_, int p_86915_, int p_86916_, int p_86917_, int p_86918_, int p_86919_) {
         int i = p_86917_ + 8;
         int j = 0;
         boolean flag = false;
         if (p_86916_ <= p_86918_ && p_86918_ <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && p_86917_ <= p_86919_ && p_86919_ <= p_86917_ + 32) {
            flag = true;
         }

         int k = 8388479;
         if (flag && !RealmsMainScreen.this.shouldShowPopup()) {
            k = 6077788;
         }

         for(Component component : RealmsMainScreen.TRIAL_MESSAGE_LINES) {
            GuiComponent.drawCenteredString(p_86914_, RealmsMainScreen.this.font, component, RealmsMainScreen.this.width / 2, i + j, k);
            j += 10;
         }

      }

      public Component getNarration() {
         return RealmsMainScreen.TRIAL_TEXT;
      }

      @Nullable
      public RealmsServer getServer() {
         return null;
      }
   }
}