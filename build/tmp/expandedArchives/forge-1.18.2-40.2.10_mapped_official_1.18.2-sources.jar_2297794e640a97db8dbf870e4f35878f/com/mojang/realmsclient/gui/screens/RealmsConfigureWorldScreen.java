package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.task.CloseServerTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchMinigameTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConfigureWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
   private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
   private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
   private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
   private static final Component WORLD_LIST_TITLE = new TranslatableComponent("mco.configure.worlds.title");
   private static final Component TITLE = new TranslatableComponent("mco.configure.world.title");
   private static final Component MINIGAME_PREFIX = (new TranslatableComponent("mco.configure.current.minigame")).append(": ");
   private static final Component SERVER_EXPIRED_TOOLTIP = new TranslatableComponent("mco.selectServer.expired");
   private static final Component SERVER_EXPIRING_SOON_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.soon");
   private static final Component SERVER_EXPIRING_IN_DAY_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.day");
   private static final Component SERVER_OPEN_TOOLTIP = new TranslatableComponent("mco.selectServer.open");
   private static final Component SERVER_CLOSED_TOOLTIP = new TranslatableComponent("mco.selectServer.closed");
   private static final int DEFAULT_BUTTON_WIDTH = 80;
   private static final int DEFAULT_BUTTON_OFFSET = 5;
   @Nullable
   private Component toolTip;
   private final RealmsMainScreen lastScreen;
   @Nullable
   private RealmsServer serverData;
   private final long serverId;
   private int leftX;
   private int rightX;
   private Button playersButton;
   private Button settingsButton;
   private Button subscriptionButton;
   private Button optionsButton;
   private Button backupButton;
   private Button resetWorldButton;
   private Button switchMinigameButton;
   private boolean stateChanged;
   private int animTick;
   private int clicks;
   private final List<RealmsWorldSlotButton> slotButtonList = Lists.newArrayList();

   public RealmsConfigureWorldScreen(RealmsMainScreen p_88411_, long p_88412_) {
      super(TITLE);
      this.lastScreen = p_88411_;
      this.serverId = p_88412_;
   }

   public void init() {
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      this.leftX = this.width / 2 - 187;
      this.rightX = this.width / 2 + 190;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.playersButton = this.addRenderableWidget(new Button(this.centerButton(0, 3), row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.players"), (p_88532_) -> {
         this.minecraft.setScreen(new RealmsPlayerScreen(this, this.serverData));
      }));
      this.settingsButton = this.addRenderableWidget(new Button(this.centerButton(1, 3), row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.settings"), (p_88530_) -> {
         this.minecraft.setScreen(new RealmsSettingsScreen(this, this.serverData.clone()));
      }));
      this.subscriptionButton = this.addRenderableWidget(new Button(this.centerButton(2, 3), row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.subscription"), (p_88527_) -> {
         this.minecraft.setScreen(new RealmsSubscriptionInfoScreen(this, this.serverData.clone(), this.lastScreen));
      }));
      this.slotButtonList.clear();

      for(int i = 1; i < 5; ++i) {
         this.slotButtonList.add(this.addSlotButton(i));
      }

      this.switchMinigameButton = this.addRenderableWidget(new Button(this.leftButton(0), row(13) - 5, 100, 20, new TranslatableComponent("mco.configure.world.buttons.switchminigame"), (p_88524_) -> {
         this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME));
      }));
      this.optionsButton = this.addRenderableWidget(new Button(this.leftButton(0), row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.buttons.options"), (p_88522_) -> {
         this.minecraft.setScreen(new RealmsSlotOptionsScreen(this, this.serverData.slots.get(this.serverData.activeSlot).clone(), this.serverData.worldType, this.serverData.activeSlot));
      }));
      this.backupButton = this.addRenderableWidget(new Button(this.leftButton(1), row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.backup"), (p_88514_) -> {
         this.minecraft.setScreen(new RealmsBackupScreen(this, this.serverData.clone(), this.serverData.activeSlot));
      }));
      this.resetWorldButton = this.addRenderableWidget(new Button(this.leftButton(2), row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.buttons.resetworld"), (p_88496_) -> {
         this.minecraft.setScreen(new RealmsResetWorldScreen(this, this.serverData.clone(), () -> {
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(this.getNewScreen());
            });
         }, () -> {
            this.minecraft.setScreen(this.getNewScreen());
         }));
      }));
      this.addRenderableWidget(new Button(this.rightX - 80 + 8, row(13) - 5, 70, 20, CommonComponents.GUI_BACK, (p_167407_) -> {
         this.backButtonClicked();
      }));
      this.backupButton.active = true;
      if (this.serverData == null) {
         this.hideMinigameButtons();
         this.hideRegularButtons();
         this.playersButton.active = false;
         this.settingsButton.active = false;
         this.subscriptionButton.active = false;
      } else {
         this.disableButtons();
         if (this.isMinigame()) {
            this.hideRegularButtons();
         } else {
            this.hideMinigameButtons();
         }
      }

   }

   private RealmsWorldSlotButton addSlotButton(int p_167386_) {
      int i = this.frame(p_167386_);
      int j = row(5) + 5;
      RealmsWorldSlotButton realmsworldslotbutton = new RealmsWorldSlotButton(i, j, 80, 80, () -> {
         return this.serverData;
      }, (p_167399_) -> {
         this.toolTip = p_167399_;
      }, p_167386_, (p_167389_) -> {
         RealmsWorldSlotButton.State realmsworldslotbutton$state = ((RealmsWorldSlotButton)p_167389_).getState();
         if (realmsworldslotbutton$state != null) {
            switch(realmsworldslotbutton$state.action) {
            case NOTHING:
               break;
            case JOIN:
               this.joinRealm(this.serverData);
               break;
            case SWITCH_SLOT:
               if (realmsworldslotbutton$state.minigame) {
                  this.switchToMinigame();
               } else if (realmsworldslotbutton$state.empty) {
                  this.switchToEmptySlot(p_167386_, this.serverData);
               } else {
                  this.switchToFullSlot(p_167386_, this.serverData);
               }
               break;
            default:
               throw new IllegalStateException("Unknown action " + realmsworldslotbutton$state.action);
            }
         }

      });
      return this.addRenderableWidget(realmsworldslotbutton);
   }

   private int leftButton(int p_88464_) {
      return this.leftX + p_88464_ * 95;
   }

   private int centerButton(int p_88466_, int p_88467_) {
      return this.width / 2 - (p_88467_ * 105 - 5) / 2 + p_88466_ * 105;
   }

   public void tick() {
      super.tick();
      ++this.animTick;
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

      this.slotButtonList.forEach(RealmsWorldSlotButton::tick);
   }

   public void render(PoseStack p_88429_, int p_88430_, int p_88431_, float p_88432_) {
      this.toolTip = null;
      this.renderBackground(p_88429_);
      drawCenteredString(p_88429_, this.font, WORLD_LIST_TITLE, this.width / 2, row(4), 16777215);
      super.render(p_88429_, p_88430_, p_88431_, p_88432_);
      if (this.serverData == null) {
         drawCenteredString(p_88429_, this.font, this.title, this.width / 2, 17, 16777215);
      } else {
         String s = this.serverData.getName();
         int i = this.font.width(s);
         int j = this.serverData.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
         int k = this.font.width(this.title);
         drawCenteredString(p_88429_, this.font, this.title, this.width / 2, 12, 16777215);
         drawCenteredString(p_88429_, this.font, s, this.width / 2, 24, j);
         int l = Math.min(this.centerButton(2, 3) + 80 - 11, this.width / 2 + i / 2 + k / 2 + 10);
         this.drawServerStatus(p_88429_, l, 7, p_88430_, p_88431_);
         if (this.isMinigame()) {
            this.font.draw(p_88429_, MINIGAME_PREFIX.copy().append(this.serverData.getMinigameName()), (float)(this.leftX + 80 + 20 + 10), (float)row(13), 16777215);
         }

         if (this.toolTip != null) {
            this.renderMousehoverTooltip(p_88429_, this.toolTip, p_88430_, p_88431_);
         }

      }
   }

   private int frame(int p_88488_) {
      return this.leftX + (p_88488_ - 1) * 98;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_88417_, int p_88418_, int p_88419_) {
      if (p_88417_ == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_88417_, p_88418_, p_88419_);
      }
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.lastScreen.resetScreen();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   private void fetchServerData(long p_88427_) {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.create();

         try {
            RealmsServer realmsserver = realmsclient.getOwnWorld(p_88427_);
            this.minecraft.execute(() -> {
               this.serverData = realmsserver;
               this.disableButtons();
               if (this.isMinigame()) {
                  this.show(this.switchMinigameButton);
               } else {
                  this.show(this.optionsButton);
                  this.show(this.backupButton);
                  this.show(this.resetWorldButton);
               }

            });
         } catch (RealmsServiceException realmsserviceexception) {
            LOGGER.error("Couldn't get own world");
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty(realmsserviceexception.getMessage()), this.lastScreen));
            });
         }

      })).start();
   }

   private void disableButtons() {
      this.playersButton.active = !this.serverData.expired;
      this.settingsButton.active = !this.serverData.expired;
      this.subscriptionButton.active = true;
      this.switchMinigameButton.active = !this.serverData.expired;
      this.optionsButton.active = !this.serverData.expired;
      this.resetWorldButton.active = !this.serverData.expired;
   }

   private void joinRealm(RealmsServer p_88439_) {
      if (this.serverData.state == RealmsServer.State.OPEN) {
         this.lastScreen.play(p_88439_, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      } else {
         this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      }

   }

   private void switchToMinigame() {
      RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME);
      realmsselectworldtemplatescreen.setWarning(new TranslatableComponent("mco.minigame.world.info.line1"), new TranslatableComponent("mco.minigame.world.info.line2"));
      this.minecraft.setScreen(realmsselectworldtemplatescreen);
   }

   private void switchToFullSlot(int p_88421_, RealmsServer p_88422_) {
      Component component = new TranslatableComponent("mco.configure.world.slot.switch.question.line1");
      Component component1 = new TranslatableComponent("mco.configure.world.slot.switch.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_167405_) -> {
         if (p_167405_) {
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(p_88422_.id, p_88421_, () -> {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(this.getNewScreen());
               });
            })));
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, component, component1, true));
   }

   private void switchToEmptySlot(int p_88469_, RealmsServer p_88470_) {
      Component component = new TranslatableComponent("mco.configure.world.slot.switch.question.line1");
      Component component1 = new TranslatableComponent("mco.configure.world.slot.switch.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_167393_) -> {
         if (p_167393_) {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, p_88470_, new TranslatableComponent("mco.configure.world.switch.slot"), new TranslatableComponent("mco.configure.world.switch.slot.subtitle"), 10526880, CommonComponents.GUI_CANCEL, () -> {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(this.getNewScreen());
               });
            }, () -> {
               this.minecraft.setScreen(this.getNewScreen());
            });
            realmsresetworldscreen.setSlot(p_88469_);
            realmsresetworldscreen.setResetTitle(new TranslatableComponent("mco.create.world.reset.title"));
            this.minecraft.setScreen(realmsresetworldscreen);
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, component, component1, true));
   }

   protected void renderMousehoverTooltip(PoseStack p_88434_, @Nullable Component p_88435_, int p_88436_, int p_88437_) {
      int i = p_88436_ + 12;
      int j = p_88437_ - 12;
      int k = this.font.width(p_88435_);
      if (i + k + 3 > this.rightX) {
         i = i - k - 20;
      }

      this.fillGradient(p_88434_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
      this.font.drawShadow(p_88434_, p_88435_, (float)i, (float)j, 16777215);
   }

   private void drawServerStatus(PoseStack p_88490_, int p_88491_, int p_88492_, int p_88493_, int p_88494_) {
      if (this.serverData.expired) {
         this.drawExpired(p_88490_, p_88491_, p_88492_, p_88493_, p_88494_);
      } else if (this.serverData.state == RealmsServer.State.CLOSED) {
         this.drawClose(p_88490_, p_88491_, p_88492_, p_88493_, p_88494_);
      } else if (this.serverData.state == RealmsServer.State.OPEN) {
         if (this.serverData.daysLeft < 7) {
            this.drawExpiring(p_88490_, p_88491_, p_88492_, p_88493_, p_88494_, this.serverData.daysLeft);
         } else {
            this.drawOpen(p_88490_, p_88491_, p_88492_, p_88493_, p_88494_);
         }
      }

   }

   private void drawExpired(PoseStack p_88499_, int p_88500_, int p_88501_, int p_88502_, int p_88503_) {
      RenderSystem.setShaderTexture(0, EXPIRED_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(p_88499_, p_88500_, p_88501_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_88502_ >= p_88500_ && p_88502_ <= p_88500_ + 9 && p_88503_ >= p_88501_ && p_88503_ <= p_88501_ + 27) {
         this.toolTip = SERVER_EXPIRED_TOOLTIP;
      }

   }

   private void drawExpiring(PoseStack p_88474_, int p_88475_, int p_88476_, int p_88477_, int p_88478_, int p_88479_) {
      RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.animTick % 20 < 10) {
         GuiComponent.blit(p_88474_, p_88475_, p_88476_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         GuiComponent.blit(p_88474_, p_88475_, p_88476_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      if (p_88477_ >= p_88475_ && p_88477_ <= p_88475_ + 9 && p_88478_ >= p_88476_ && p_88478_ <= p_88476_ + 27) {
         if (p_88479_ <= 0) {
            this.toolTip = SERVER_EXPIRING_SOON_TOOLTIP;
         } else if (p_88479_ == 1) {
            this.toolTip = SERVER_EXPIRING_IN_DAY_TOOLTIP;
         } else {
            this.toolTip = new TranslatableComponent("mco.selectServer.expires.days", p_88479_);
         }
      }

   }

   private void drawOpen(PoseStack p_88508_, int p_88509_, int p_88510_, int p_88511_, int p_88512_) {
      RenderSystem.setShaderTexture(0, ON_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(p_88508_, p_88509_, p_88510_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_88511_ >= p_88509_ && p_88511_ <= p_88509_ + 9 && p_88512_ >= p_88510_ && p_88512_ <= p_88510_ + 27) {
         this.toolTip = SERVER_OPEN_TOOLTIP;
      }

   }

   private void drawClose(PoseStack p_88516_, int p_88517_, int p_88518_, int p_88519_, int p_88520_) {
      RenderSystem.setShaderTexture(0, OFF_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(p_88516_, p_88517_, p_88518_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_88519_ >= p_88517_ && p_88519_ <= p_88517_ + 9 && p_88520_ >= p_88518_ && p_88520_ <= p_88518_ + 27) {
         this.toolTip = SERVER_CLOSED_TOOLTIP;
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
   }

   private void hideRegularButtons() {
      this.hide(this.optionsButton);
      this.hide(this.backupButton);
      this.hide(this.resetWorldButton);
   }

   private void hide(Button p_88451_) {
      p_88451_.visible = false;
      this.removeWidget(p_88451_);
   }

   private void show(Button p_88485_) {
      p_88485_.visible = true;
      this.addRenderableWidget(p_88485_);
   }

   private void hideMinigameButtons() {
      this.hide(this.switchMinigameButton);
   }

   public void saveSlotSettings(RealmsWorldOptions p_88445_) {
      RealmsWorldOptions realmsworldoptions = this.serverData.slots.get(this.serverData.activeSlot);
      p_88445_.templateId = realmsworldoptions.templateId;
      p_88445_.templateImage = realmsworldoptions.templateImage;
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.updateSlot(this.serverData.id, this.serverData.activeSlot, p_88445_);
         this.serverData.slots.put(this.serverData.activeSlot, p_88445_);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't save slot settings");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void saveSettings(String p_88455_, String p_88456_) {
      String s = p_88456_.trim().isEmpty() ? null : p_88456_;
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.update(this.serverData.id, p_88455_, s);
         this.serverData.setName(p_88455_);
         this.serverData.setDescription(s);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't save settings");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void openTheWorld(boolean p_88460_, Screen p_88461_) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(p_88461_, new OpenServerTask(this.serverData, this, this.lastScreen, p_88460_, this.minecraft)));
   }

   public void closeTheWorld(Screen p_88453_) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(p_88453_, new CloseServerTask(this.serverData, this)));
   }

   public void stateChanged() {
      this.stateChanged = true;
   }

   private void templateSelectionCallback(@Nullable WorldTemplate p_167395_) {
      if (p_167395_ != null && WorldTemplate.WorldTemplateType.MINIGAME == p_167395_.type) {
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameTask(this.serverData.id, p_167395_, this.getNewScreen())));
      } else {
         this.minecraft.setScreen(this);
      }

   }

   public RealmsConfigureWorldScreen getNewScreen() {
      return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
   }
}