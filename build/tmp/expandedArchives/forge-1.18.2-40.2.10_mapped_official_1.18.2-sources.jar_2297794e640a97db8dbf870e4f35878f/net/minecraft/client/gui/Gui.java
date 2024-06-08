package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.chat.ChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.chat.StandardChatListener;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.StringDecomposer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class Gui extends GuiComponent {
   protected static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   protected static final ResourceLocation PUMPKIN_BLUR_LOCATION = new ResourceLocation("textures/misc/pumpkinblur.png");
   protected static final ResourceLocation SPYGLASS_SCOPE_LOCATION = new ResourceLocation("textures/misc/spyglass_scope.png");
   protected static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = new ResourceLocation("textures/misc/powder_snow_outline.png");
   protected static final Component DEMO_EXPIRED_TEXT = new TranslatableComponent("demo.demoExpired");
   protected static final Component SAVING_TEXT = new TranslatableComponent("menu.savingLevel");
   protected static final int COLOR_WHITE = 16777215;
   protected static final float MIN_CROSSHAIR_ATTACK_SPEED = 5.0F;
   protected static final int NUM_HEARTS_PER_ROW = 10;
   protected static final int LINE_HEIGHT = 10;
   protected static final String SPACER = ": ";
   protected static final float PORTAL_OVERLAY_ALPHA_MIN = 0.2F;
   protected static final int HEART_SIZE = 9;
   protected static final int HEART_SEPARATION = 8;
   protected static final float AUTOSAVE_FADE_SPEED_FACTOR = 0.2F;
   protected final Random random = new Random();
   protected final Minecraft minecraft;
   protected final ItemRenderer itemRenderer;
   protected final ChatComponent chat;
   protected int tickCount;
   @Nullable
   protected Component overlayMessageString;
   protected int overlayMessageTime;
   protected boolean animateOverlayMessageColor;
   public float vignetteBrightness = 1.0F;
   protected int toolHighlightTimer;
   protected ItemStack lastToolHighlight = ItemStack.EMPTY;
   protected final DebugScreenOverlay debugScreen;
   protected final SubtitleOverlay subtitleOverlay;
   protected final SpectatorGui spectatorGui;
   protected final PlayerTabOverlay tabList;
   protected final BossHealthOverlay bossOverlay;
   protected int titleTime;
   @Nullable
   protected Component title;
   @Nullable
   protected Component subtitle;
   protected int titleFadeInTime;
   protected int titleStayTime;
   protected int titleFadeOutTime;
   protected int lastHealth;
   protected int displayHealth;
   protected long lastHealthTime;
   protected long healthBlinkTime;
   protected int screenWidth;
   protected int screenHeight;
   protected float autosaveIndicatorValue;
   protected float lastAutosaveIndicatorValue;
   protected final Map<ChatType, List<ChatListener>> chatListeners = Maps.newHashMap();
   protected float scopeScale;

   public Gui(Minecraft p_93005_) {
      this.minecraft = p_93005_;
      this.itemRenderer = p_93005_.getItemRenderer();
      this.debugScreen = new DebugScreenOverlay(p_93005_);
      this.spectatorGui = new SpectatorGui(p_93005_);
      this.chat = new ChatComponent(p_93005_);
      this.tabList = new PlayerTabOverlay(p_93005_, this);
      this.bossOverlay = new BossHealthOverlay(p_93005_);
      this.subtitleOverlay = new SubtitleOverlay(p_93005_);

      for(ChatType chattype : ChatType.values()) {
         this.chatListeners.put(chattype, Lists.newArrayList());
      }

      ChatListener chatlistener = NarratorChatListener.INSTANCE;
      this.chatListeners.get(ChatType.CHAT).add(new StandardChatListener(p_93005_));
      this.chatListeners.get(ChatType.CHAT).add(chatlistener);
      this.chatListeners.get(ChatType.SYSTEM).add(new StandardChatListener(p_93005_));
      this.chatListeners.get(ChatType.SYSTEM).add(chatlistener);
      this.chatListeners.get(ChatType.GAME_INFO).add(new OverlayChatListener(p_93005_));
      this.resetTitleTimes();
   }

   public void resetTitleTimes() {
      this.titleFadeInTime = 10;
      this.titleStayTime = 70;
      this.titleFadeOutTime = 20;
   }

   public void render(PoseStack p_93031_, float p_93032_) {
      this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
      this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
      Font font = this.getFont();
      RenderSystem.enableBlend();
      if (Minecraft.useFancyGraphics()) {
         this.renderVignette(this.minecraft.getCameraEntity());
      } else {
         RenderSystem.enableDepthTest();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.defaultBlendFunc();
      }

      float f = this.minecraft.getDeltaFrameTime();
      this.scopeScale = Mth.lerp(0.5F * f, this.scopeScale, 1.125F);
      if (this.minecraft.options.getCameraType().isFirstPerson()) {
         if (this.minecraft.player.isScoping()) {
            this.renderSpyglassOverlay(this.scopeScale);
         } else {
            this.scopeScale = 0.5F;
            ItemStack itemstack = this.minecraft.player.getInventory().getArmor(3);
            if (itemstack.is(Blocks.CARVED_PUMPKIN.asItem())) {
               this.renderTextureOverlay(PUMPKIN_BLUR_LOCATION, 1.0F);
            }
         }
      }

      if (this.minecraft.player.getTicksFrozen() > 0) {
         this.renderTextureOverlay(POWDER_SNOW_OUTLINE_LOCATION, this.minecraft.player.getPercentFrozen());
      }

      float f2 = Mth.lerp(p_93032_, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
      if (f2 > 0.0F && !this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
         this.renderPortalOverlay(f2);
      }

      if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
         this.spectatorGui.renderHotbar(p_93031_);
      } else if (!this.minecraft.options.hideGui) {
         this.renderHotbar(p_93032_, p_93031_);
      }

      if (!this.minecraft.options.hideGui) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
         RenderSystem.enableBlend();
         this.renderCrosshair(p_93031_);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.defaultBlendFunc();
         this.minecraft.getProfiler().push("bossHealth");
         this.bossOverlay.render(p_93031_);
         this.minecraft.getProfiler().pop();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
         if (this.minecraft.gameMode.canHurtPlayer()) {
            this.renderPlayerHealth(p_93031_);
         }

         this.renderVehicleHealth(p_93031_);
         RenderSystem.disableBlend();
         int i = this.screenWidth / 2 - 91;
         if (this.minecraft.player.isRidingJumpable()) {
            this.renderJumpMeter(p_93031_, i);
         } else if (this.minecraft.gameMode.hasExperience()) {
            this.renderExperienceBar(p_93031_, i);
         }

         if (this.minecraft.options.heldItemTooltips && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.renderSelectedItemName(p_93031_);
         } else if (this.minecraft.player.isSpectator()) {
            this.spectatorGui.renderTooltip(p_93031_);
         }
      }

      if (this.minecraft.player.getSleepTimer() > 0) {
         this.minecraft.getProfiler().push("sleep");
         RenderSystem.disableDepthTest();
         float f3 = (float)this.minecraft.player.getSleepTimer();
         float f1 = f3 / 100.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F - (f3 - 100.0F) / 10.0F;
         }

         int j = (int)(220.0F * f1) << 24 | 1052704;
         fill(p_93031_, 0, 0, this.screenWidth, this.screenHeight, j);
         RenderSystem.enableDepthTest();
         this.minecraft.getProfiler().pop();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.minecraft.isDemo()) {
         this.renderDemoOverlay(p_93031_);
      }

      this.renderEffects(p_93031_);
      if (this.minecraft.options.renderDebug) {
         this.debugScreen.render(p_93031_);
      }

      if (!this.minecraft.options.hideGui) {
         if (this.overlayMessageString != null && this.overlayMessageTime > 0) {
            this.minecraft.getProfiler().push("overlayMessage");
            float f4 = (float)this.overlayMessageTime - p_93032_;
            int i1 = (int)(f4 * 255.0F / 20.0F);
            if (i1 > 255) {
               i1 = 255;
            }

            if (i1 > 8) {
               p_93031_.pushPose();
               p_93031_.translate((double)(this.screenWidth / 2), (double)(this.screenHeight - 68), 0.0D);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               int k1 = 16777215;
               if (this.animateOverlayMessageColor) {
                  k1 = Mth.hsvToRgb(f4 / 50.0F, 0.7F, 0.6F) & 16777215;
               }

               int k = i1 << 24 & -16777216;
               int l = font.width(this.overlayMessageString);
               this.drawBackdrop(p_93031_, font, -4, l, 16777215 | k);
               font.draw(p_93031_, this.overlayMessageString, (float)(-l / 2), -4.0F, k1 | k);
               RenderSystem.disableBlend();
               p_93031_.popPose();
            }

            this.minecraft.getProfiler().pop();
         }

         if (this.title != null && this.titleTime > 0) {
            this.minecraft.getProfiler().push("titleAndSubtitle");
            float f5 = (float)this.titleTime - p_93032_;
            int j1 = 255;
            if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
               float f6 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - f5;
               j1 = (int)(f6 * 255.0F / (float)this.titleFadeInTime);
            }

            if (this.titleTime <= this.titleFadeOutTime) {
               j1 = (int)(f5 * 255.0F / (float)this.titleFadeOutTime);
            }

            j1 = Mth.clamp(j1, 0, 255);
            if (j1 > 8) {
               p_93031_.pushPose();
               p_93031_.translate((double)(this.screenWidth / 2), (double)(this.screenHeight / 2), 0.0D);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               p_93031_.pushPose();
               p_93031_.scale(4.0F, 4.0F, 4.0F);
               int l1 = j1 << 24 & -16777216;
               int i2 = font.width(this.title);
               this.drawBackdrop(p_93031_, font, -10, i2, 16777215 | l1);
               font.drawShadow(p_93031_, this.title, (float)(-i2 / 2), -10.0F, 16777215 | l1);
               p_93031_.popPose();
               if (this.subtitle != null) {
                  p_93031_.pushPose();
                  p_93031_.scale(2.0F, 2.0F, 2.0F);
                  int k2 = font.width(this.subtitle);
                  this.drawBackdrop(p_93031_, font, 5, k2, 16777215 | l1);
                  font.drawShadow(p_93031_, this.subtitle, (float)(-k2 / 2), 5.0F, 16777215 | l1);
                  p_93031_.popPose();
               }

               RenderSystem.disableBlend();
               p_93031_.popPose();
            }

            this.minecraft.getProfiler().pop();
         }

         this.subtitleOverlay.render(p_93031_);
         Scoreboard scoreboard = this.minecraft.level.getScoreboard();
         Objective objective = null;
         PlayerTeam playerteam = scoreboard.getPlayersTeam(this.minecraft.player.getScoreboardName());
         if (playerteam != null) {
            int j2 = playerteam.getColor().getId();
            if (j2 >= 0) {
               objective = scoreboard.getDisplayObjective(3 + j2);
            }
         }

         Objective objective1 = objective != null ? objective : scoreboard.getDisplayObjective(1);
         if (objective1 != null) {
            this.displayScoreboardSidebar(p_93031_, objective1);
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         p_93031_.pushPose();
         p_93031_.translate(0.0D, (double)(this.screenHeight - 48), 0.0D);
         this.minecraft.getProfiler().push("chat");
         this.chat.render(p_93031_, this.tickCount);
         this.minecraft.getProfiler().pop();
         p_93031_.popPose();
         objective1 = scoreboard.getDisplayObjective(0);
         if (!this.minecraft.options.keyPlayerList.isDown() || this.minecraft.isLocalServer() && this.minecraft.player.connection.getOnlinePlayers().size() <= 1 && objective1 == null) {
            this.tabList.setVisible(false);
         } else {
            this.tabList.setVisible(true);
            this.tabList.render(p_93031_, this.screenWidth, scoreboard, objective1);
         }

         this.renderSavingIndicator(p_93031_);
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   protected void drawBackdrop(PoseStack p_93040_, Font p_93041_, int p_93042_, int p_93043_, int p_93044_) {
      int i = this.minecraft.options.getBackgroundColor(0.0F);
      if (i != 0) {
         int j = -p_93043_ / 2;
         fill(p_93040_, j - 2, p_93042_ - 2, j + p_93043_ + 2, p_93042_ + 9 + 2, FastColor.ARGB32.multiply(i, p_93044_));
      }

   }

   protected void renderCrosshair(PoseStack p_93081_) {
      Options options = this.minecraft.options;
      if (options.getCameraType().isFirstPerson()) {
         if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            if (options.renderDebug && !options.hideGui && !this.minecraft.player.isReducedDebugInfo() && !options.reducedDebugInfo) {
               Camera camera = this.minecraft.gameRenderer.getMainCamera();
               PoseStack posestack = RenderSystem.getModelViewStack();
               posestack.pushPose();
               posestack.translate((double)(this.screenWidth / 2), (double)(this.screenHeight / 2), (double)this.getBlitOffset());
               posestack.mulPose(Vector3f.XN.rotationDegrees(camera.getXRot()));
               posestack.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot()));
               posestack.scale(-1.0F, -1.0F, -1.0F);
               RenderSystem.applyModelViewMatrix();
               RenderSystem.renderCrosshair(10);
               posestack.popPose();
               RenderSystem.applyModelViewMatrix();
            } else {
               RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               int i = 15;
               this.blit(p_93081_, (this.screenWidth - 15) / 2, (this.screenHeight - 15) / 2, 0, 0, 15, 15);
               if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                  float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                  boolean flag = false;
                  if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                     flag = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                     flag &= this.minecraft.crosshairPickEntity.isAlive();
                     flag &= this.minecraft.player.canHit(this.minecraft.crosshairPickEntity, 0);
                  }

                  int j = this.screenHeight / 2 - 7 + 16;
                  int k = this.screenWidth / 2 - 8;
                  if (flag) {
                     this.blit(p_93081_, k, j, 68, 94, 16, 16);
                  } else if (f < 1.0F) {
                     int l = (int)(f * 17.0F);
                     this.blit(p_93081_, k, j, 36, 94, 16, 4);
                     this.blit(p_93081_, k, j, 52, 94, l, 4);
                  }
               }
            }

         }
      }
   }

   private boolean canRenderCrosshairForSpectator(HitResult p_93025_) {
      if (p_93025_ == null) {
         return false;
      } else if (p_93025_.getType() == HitResult.Type.ENTITY) {
         return ((EntityHitResult)p_93025_).getEntity() instanceof MenuProvider;
      } else if (p_93025_.getType() == HitResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockHitResult)p_93025_).getBlockPos();
         Level level = this.minecraft.level;
         return level.getBlockState(blockpos).getMenuProvider(level, blockpos) != null;
      } else {
         return false;
      }
   }

   protected void renderEffects(PoseStack p_93029_) {
      Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
      if (!collection.isEmpty()) {
         Screen $$4 = this.minecraft.screen;
         if ($$4 instanceof EffectRenderingInventoryScreen) {
            EffectRenderingInventoryScreen effectrenderinginventoryscreen = (EffectRenderingInventoryScreen)$$4;
            if (effectrenderinginventoryscreen.canSeeEffects()) {
               return;
            }
         }

         RenderSystem.enableBlend();
         int j1 = 0;
         int k1 = 0;
         MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
         List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
         RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);

         for(MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
            MobEffect mobeffect = mobeffectinstance.getEffect();
            net.minecraftforge.client.EffectRenderer renderer = net.minecraftforge.client.RenderProperties.getEffectRenderer(mobeffectinstance);
            if (!renderer.shouldRenderHUD(mobeffectinstance)) continue;
            // Rebind in case previous renderHUDEffect changed texture
            RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
            if (mobeffectinstance.showIcon()) {
               int i = this.screenWidth;
               int j = 1;
               if (this.minecraft.isDemo()) {
                  j += 15;
               }

               if (mobeffect.isBeneficial()) {
                  ++j1;
                  i -= 25 * j1;
               } else {
                  ++k1;
                  i -= 25 * k1;
                  j += 26;
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               float f = 1.0F;
               if (mobeffectinstance.isAmbient()) {
                  this.blit(p_93029_, i, j, 165, 166, 24, 24);
               } else {
                  this.blit(p_93029_, i, j, 141, 166, 24, 24);
                  if (mobeffectinstance.getDuration() <= 200) {
                     int k = 10 - mobeffectinstance.getDuration() / 20;
                     f = Mth.clamp((float)mobeffectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)mobeffectinstance.getDuration() * (float)Math.PI / 5.0F) * Mth.clamp((float)k / 10.0F * 0.25F, 0.0F, 0.25F);
                  }
               }

               TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);
               int l = i;
               int i1 = j;
               float f1 = f;
               list.add(() -> {
                  RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);
                  blit(p_93029_, l + 3, i1 + 3, this.getBlitOffset(), 18, 18, textureatlassprite);
               });
               renderer.renderHUDEffect(mobeffectinstance,this, p_93029_, i, j, this.getBlitOffset(), f);
            }
         }

         list.forEach(Runnable::run);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   protected void renderHotbar(float p_93010_, PoseStack p_93011_) {
      Player player = this.getCameraPlayer();
      if (player != null) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
         ItemStack itemstack = player.getOffhandItem();
         HumanoidArm humanoidarm = player.getMainArm().getOpposite();
         int i = this.screenWidth / 2;
         int j = this.getBlitOffset();
         int k = 182;
         int l = 91;
         this.setBlitOffset(-90);
         this.blit(p_93011_, i - 91, this.screenHeight - 22, 0, 0, 182, 22);
         this.blit(p_93011_, i - 91 - 1 + player.getInventory().selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);
         if (!itemstack.isEmpty()) {
            if (humanoidarm == HumanoidArm.LEFT) {
               this.blit(p_93011_, i - 91 - 29, this.screenHeight - 23, 24, 22, 29, 24);
            } else {
               this.blit(p_93011_, i + 91, this.screenHeight - 23, 53, 22, 29, 24);
            }
         }

         this.setBlitOffset(j);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         int i1 = 1;

         for(int j1 = 0; j1 < 9; ++j1) {
            int k1 = i - 90 + j1 * 20 + 2;
            int l1 = this.screenHeight - 16 - 3;
            this.renderSlot(k1, l1, p_93010_, player, player.getInventory().items.get(j1), i1++);
         }

         if (!itemstack.isEmpty()) {
            int j2 = this.screenHeight - 16 - 3;
            if (humanoidarm == HumanoidArm.LEFT) {
               this.renderSlot(i - 91 - 26, j2, p_93010_, player, itemstack, i1++);
            } else {
               this.renderSlot(i + 91 + 10, j2, p_93010_, player, itemstack, i1++);
            }
         }

         if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.HOTBAR) {
            float f = this.minecraft.player.getAttackStrengthScale(0.0F);
            if (f < 1.0F) {
               int k2 = this.screenHeight - 20;
               int l2 = i + 91 + 6;
               if (humanoidarm == HumanoidArm.RIGHT) {
                  l2 = i - 91 - 22;
               }

               RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
               int i2 = (int)(f * 19.0F);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               this.blit(p_93011_, l2, k2, 0, 94, 18, 18);
               this.blit(p_93011_, l2, k2 + 18 - i2, 18, 112 - i2, 18, i2);
            }
         }

         RenderSystem.disableBlend();
      }
   }

   public void renderJumpMeter(PoseStack p_93034_, int p_93035_) {
      this.minecraft.getProfiler().push("jumpBar");
      RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      float f = this.minecraft.player.getJumpRidingScale();
      int i = 182;
      int j = (int)(f * 183.0F);
      int k = this.screenHeight - 32 + 3;
      this.blit(p_93034_, p_93035_, k, 0, 84, 182, 5);
      if (j > 0) {
         this.blit(p_93034_, p_93035_, k, 0, 89, j, 5);
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderExperienceBar(PoseStack p_93072_, int p_93073_) {
      this.minecraft.getProfiler().push("expBar");
      RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      int i = this.minecraft.player.getXpNeededForNextLevel();
      if (i > 0) {
         int j = 182;
         int k = (int)(this.minecraft.player.experienceProgress * 183.0F);
         int l = this.screenHeight - 32 + 3;
         this.blit(p_93072_, p_93073_, l, 0, 64, 182, 5);
         if (k > 0) {
            this.blit(p_93072_, p_93073_, l, 0, 69, k, 5);
         }
      }

      this.minecraft.getProfiler().pop();
      if (this.minecraft.player.experienceLevel > 0) {
         this.minecraft.getProfiler().push("expLevel");
         String s = "" + this.minecraft.player.experienceLevel;
         int i1 = (this.screenWidth - this.getFont().width(s)) / 2;
         int j1 = this.screenHeight - 31 - 4;
         this.getFont().draw(p_93072_, s, (float)(i1 + 1), (float)j1, 0);
         this.getFont().draw(p_93072_, s, (float)(i1 - 1), (float)j1, 0);
         this.getFont().draw(p_93072_, s, (float)i1, (float)(j1 + 1), 0);
         this.getFont().draw(p_93072_, s, (float)i1, (float)(j1 - 1), 0);
         this.getFont().draw(p_93072_, s, (float)i1, (float)j1, 8453920);
         this.minecraft.getProfiler().pop();
      }

   }

   public void renderSelectedItemName(PoseStack p_93070_) {
      this.minecraft.getProfiler().push("selectedItemName");
      if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
         MutableComponent mutablecomponent = (new TextComponent("")).append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().getStyleModifier());
         if (this.lastToolHighlight.hasCustomHoverName()) {
            mutablecomponent.withStyle(ChatFormatting.ITALIC);
         }

         Component highlightTip = this.lastToolHighlight.getHighlightTip(mutablecomponent);
         int i = this.getFont().width(highlightTip);
         int j = (this.screenWidth - i) / 2;
         int k = this.screenHeight - 59;
         if (!this.minecraft.gameMode.canHurtPlayer()) {
            k += 14;
         }

         int l = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
         if (l > 255) {
            l = 255;
         }

         if (l > 0) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            fill(p_93070_, j - 2, k - 2, j + i + 2, k + 9 + 2, this.minecraft.options.getBackgroundColor(0));
            Font font = net.minecraftforge.client.RenderProperties.get(lastToolHighlight).getFont(lastToolHighlight);
            if (font == null) {
               this.getFont().drawShadow(p_93070_, highlightTip, (float)j, (float)k, 16777215 + (l << 24));
            } else {
               j = (this.screenWidth - font.width(highlightTip)) / 2;
               font.drawShadow(p_93070_, highlightTip, (float)j, (float)k, 16777215 + (l << 24));
            }
            RenderSystem.disableBlend();
         }
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderDemoOverlay(PoseStack p_93078_) {
      this.minecraft.getProfiler().push("demo");
      Component component;
      if (this.minecraft.level.getGameTime() >= 120500L) {
         component = DEMO_EXPIRED_TEXT;
      } else {
         component = new TranslatableComponent("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime())));
      }

      int i = this.getFont().width(component);
      this.getFont().drawShadow(p_93078_, component, (float)(this.screenWidth - i - 10), 5.0F, 16777215);
      this.minecraft.getProfiler().pop();
   }

   protected void displayScoreboardSidebar(PoseStack p_93037_, Objective p_93038_) {
      Scoreboard scoreboard = p_93038_.getScoreboard();
      Collection<Score> collection = scoreboard.getPlayerScores(p_93038_);
      List<Score> list = collection.stream().filter((p_93027_) -> {
         return p_93027_.getOwner() != null && !p_93027_.getOwner().startsWith("#");
      }).collect(Collectors.toList());
      if (list.size() > 15) {
         collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
      } else {
         collection = list;
      }

      List<Pair<Score, Component>> list1 = Lists.newArrayListWithCapacity(collection.size());
      Component component = p_93038_.getDisplayName();
      int i = this.getFont().width(component);
      int j = i;
      int k = this.getFont().width(": ");

      for(Score score : collection) {
         PlayerTeam playerteam = scoreboard.getPlayersTeam(score.getOwner());
         Component component1 = PlayerTeam.formatNameForTeam(playerteam, new TextComponent(score.getOwner()));
         list1.add(Pair.of(score, component1));
         j = Math.max(j, this.getFont().width(component1) + k + this.getFont().width(Integer.toString(score.getScore())));
      }

      int i2 = collection.size() * 9;
      int j2 = this.screenHeight / 2 + i2 / 3;
      int k2 = 3;
      int l2 = this.screenWidth - j - 3;
      int l = 0;
      int i1 = this.minecraft.options.getBackgroundColor(0.3F);
      int j1 = this.minecraft.options.getBackgroundColor(0.4F);

      for(Pair<Score, Component> pair : list1) {
         ++l;
         Score score1 = pair.getFirst();
         Component component2 = pair.getSecond();
         String s = "" + ChatFormatting.RED + score1.getScore();
         int k1 = j2 - l * 9;
         int l1 = this.screenWidth - 3 + 2;
         fill(p_93037_, l2 - 2, k1, l1, k1 + 9, i1);
         this.getFont().draw(p_93037_, component2, (float)l2, (float)k1, -1);
         this.getFont().draw(p_93037_, s, (float)(l1 - this.getFont().width(s)), (float)k1, -1);
         if (l == collection.size()) {
            fill(p_93037_, l2 - 2, k1 - 9 - 1, l1, k1 - 1, j1);
            fill(p_93037_, l2 - 2, k1 - 1, l1, k1, i1);
            this.getFont().draw(p_93037_, component, (float)(l2 + j / 2 - i / 2), (float)(k1 - 9), -1);
         }
      }

   }

   private Player getCameraPlayer() {
      return !(this.minecraft.getCameraEntity() instanceof Player) ? null : (Player)this.minecraft.getCameraEntity();
   }

   private LivingEntity getPlayerVehicleWithHealth() {
      Player player = this.getCameraPlayer();
      if (player != null) {
         Entity entity = player.getVehicle();
         if (entity == null) {
            return null;
         }

         if (entity instanceof LivingEntity) {
            return (LivingEntity)entity;
         }
      }

      return null;
   }

   private int getVehicleMaxHearts(LivingEntity p_93023_) {
      if (p_93023_ != null && p_93023_.showVehicleHealth()) {
         float f = p_93023_.getMaxHealth();
         int i = (int)(f + 0.5F) / 2;
         if (i > 30) {
            i = 30;
         }

         return i;
      } else {
         return 0;
      }
   }

   private int getVisibleVehicleHeartRows(int p_93013_) {
      return (int)Math.ceil((double)p_93013_ / 10.0D);
   }

   private void renderPlayerHealth(PoseStack p_93084_) {
      Player player = this.getCameraPlayer();
      if (player != null) {
         int i = Mth.ceil(player.getHealth());
         boolean flag = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
         long j = Util.getMillis();
         if (i < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = j;
            this.healthBlinkTime = (long)(this.tickCount + 20);
         } else if (i > this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = j;
            this.healthBlinkTime = (long)(this.tickCount + 10);
         }

         if (j - this.lastHealthTime > 1000L) {
            this.lastHealth = i;
            this.displayHealth = i;
            this.lastHealthTime = j;
         }

         this.lastHealth = i;
         int k = this.displayHealth;
         this.random.setSeed((long)(this.tickCount * 312871));
         FoodData fooddata = player.getFoodData();
         int l = fooddata.getFoodLevel();
         int i1 = this.screenWidth / 2 - 91;
         int j1 = this.screenWidth / 2 + 91;
         int k1 = this.screenHeight - 39;
         float f = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(k, i));
         int l1 = Mth.ceil(player.getAbsorptionAmount());
         int i2 = Mth.ceil((f + (float)l1) / 2.0F / 10.0F);
         int j2 = Math.max(10 - (i2 - 2), 3);
         int k2 = k1 - (i2 - 1) * j2 - 10;
         int l2 = k1 - 10;
         int i3 = player.getArmorValue();
         int j3 = -1;
         if (player.hasEffect(MobEffects.REGENERATION)) {
            j3 = this.tickCount % Mth.ceil(f + 5.0F);
         }

         this.minecraft.getProfiler().push("armor");

         for(int k3 = 0; k3 < 10; ++k3) {
            if (i3 > 0) {
               int l3 = i1 + k3 * 8;
               if (k3 * 2 + 1 < i3) {
                  this.blit(p_93084_, l3, k2, 34, 9, 9, 9);
               }

               if (k3 * 2 + 1 == i3) {
                  this.blit(p_93084_, l3, k2, 25, 9, 9, 9);
               }

               if (k3 * 2 + 1 > i3) {
                  this.blit(p_93084_, l3, k2, 16, 9, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().popPush("health");
         this.renderHearts(p_93084_, player, i1, k1, j2, j3, f, i, k, l1, flag);
         LivingEntity livingentity = this.getPlayerVehicleWithHealth();
         int k5 = this.getVehicleMaxHearts(livingentity);
         if (k5 == 0) {
            this.minecraft.getProfiler().popPush("food");

            for(int i4 = 0; i4 < 10; ++i4) {
               int j4 = k1;
               int k4 = 16;
               int l4 = 0;
               if (player.hasEffect(MobEffects.HUNGER)) {
                  k4 += 36;
                  l4 = 13;
               }

               if (player.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (l * 3 + 1) == 0) {
                  j4 = k1 + (this.random.nextInt(3) - 1);
               }

               int i5 = j1 - i4 * 8 - 9;
               this.blit(p_93084_, i5, j4, 16 + l4 * 9, 27, 9, 9);
               if (i4 * 2 + 1 < l) {
                  this.blit(p_93084_, i5, j4, k4 + 36, 27, 9, 9);
               }

               if (i4 * 2 + 1 == l) {
                  this.blit(p_93084_, i5, j4, k4 + 45, 27, 9, 9);
               }
            }

            l2 -= 10;
         }

         this.minecraft.getProfiler().popPush("air");
         int l5 = player.getMaxAirSupply();
         int i6 = Math.min(player.getAirSupply(), l5);
         if (player.isEyeInFluid(FluidTags.WATER) || i6 < l5) {
            int j6 = this.getVisibleVehicleHeartRows(k5) - 1;
            l2 -= j6 * 10;
            int k6 = Mth.ceil((double)(i6 - 2) * 10.0D / (double)l5);
            int l6 = Mth.ceil((double)i6 * 10.0D / (double)l5) - k6;

            for(int j5 = 0; j5 < k6 + l6; ++j5) {
               if (j5 < k6) {
                  this.blit(p_93084_, j1 - j5 * 8 - 9, l2, 16, 18, 9, 9);
               } else {
                  this.blit(p_93084_, j1 - j5 * 8 - 9, l2, 25, 18, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   protected void renderHearts(PoseStack p_168689_, Player p_168690_, int p_168691_, int p_168692_, int p_168693_, int p_168694_, float p_168695_, int p_168696_, int p_168697_, int p_168698_, boolean p_168699_) {
      Gui.HeartType gui$hearttype = Gui.HeartType.forPlayer(p_168690_);
      int i = 9 * (p_168690_.level.getLevelData().isHardcore() ? 5 : 0);
      int j = Mth.ceil((double)p_168695_ / 2.0D);
      int k = Mth.ceil((double)p_168698_ / 2.0D);
      int l = j * 2;

      for(int i1 = j + k - 1; i1 >= 0; --i1) {
         int j1 = i1 / 10;
         int k1 = i1 % 10;
         int l1 = p_168691_ + k1 * 8;
         int i2 = p_168692_ - j1 * p_168693_;
         if (p_168696_ + p_168698_ <= 4) {
            i2 += this.random.nextInt(2);
         }

         if (i1 < j && i1 == p_168694_) {
            i2 -= 2;
         }

         this.renderHeart(p_168689_, Gui.HeartType.CONTAINER, l1, i2, i, p_168699_, false);
         int j2 = i1 * 2;
         boolean flag = i1 >= j;
         if (flag) {
            int k2 = j2 - l;
            if (k2 < p_168698_) {
               boolean flag1 = k2 + 1 == p_168698_;
               this.renderHeart(p_168689_, gui$hearttype == Gui.HeartType.WITHERED ? gui$hearttype : Gui.HeartType.ABSORBING, l1, i2, i, false, flag1);
            }
         }

         if (p_168699_ && j2 < p_168697_) {
            boolean flag2 = j2 + 1 == p_168697_;
            this.renderHeart(p_168689_, gui$hearttype, l1, i2, i, true, flag2);
         }

         if (j2 < p_168696_) {
            boolean flag3 = j2 + 1 == p_168696_;
            this.renderHeart(p_168689_, gui$hearttype, l1, i2, i, false, flag3);
         }
      }

   }

   private void renderHeart(PoseStack p_168701_, Gui.HeartType p_168702_, int p_168703_, int p_168704_, int p_168705_, boolean p_168706_, boolean p_168707_) {
      this.blit(p_168701_, p_168703_, p_168704_, p_168702_.getX(p_168707_, p_168706_), p_168705_, 9, 9);
   }

   private void renderVehicleHealth(PoseStack p_93087_) {
      LivingEntity livingentity = this.getPlayerVehicleWithHealth();
      if (livingentity != null) {
         int i = this.getVehicleMaxHearts(livingentity);
         if (i != 0) {
            int j = (int)Math.ceil((double)livingentity.getHealth());
            this.minecraft.getProfiler().popPush("mountHealth");
            int k = this.screenHeight - 39;
            int l = this.screenWidth / 2 + 91;
            int i1 = k;
            int j1 = 0;

            for(boolean flag = false; i > 0; j1 += 20) {
               int k1 = Math.min(i, 10);
               i -= k1;

               for(int l1 = 0; l1 < k1; ++l1) {
                  int i2 = 52;
                  int j2 = 0;
                  int k2 = l - l1 * 8 - 9;
                  this.blit(p_93087_, k2, i1, 52 + j2 * 9, 9, 9, 9);
                  if (l1 * 2 + 1 + j1 < j) {
                     this.blit(p_93087_, k2, i1, 88, 9, 9, 9);
                  }

                  if (l1 * 2 + 1 + j1 == j) {
                     this.blit(p_93087_, k2, i1, 97, 9, 9, 9);
                  }
               }

               i1 -= 10;
            }

         }
      }
   }

   protected void renderTextureOverlay(ResourceLocation p_168709_, float p_168710_) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, p_168710_);
      RenderSystem.setShaderTexture(0, p_168709_);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
      tesselator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   protected void renderSpyglassOverlay(float p_168676_) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, SPYGLASS_SCOPE_LOCATION);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      float f = (float)Math.min(this.screenWidth, this.screenHeight);
      float f1 = Math.min((float)this.screenWidth / f, (float)this.screenHeight / f) * p_168676_;
      float f2 = f * f1;
      float f3 = f * f1;
      float f4 = ((float)this.screenWidth - f2) / 2.0F;
      float f5 = ((float)this.screenHeight - f3) / 2.0F;
      float f6 = f4 + f2;
      float f7 = f5 + f3;
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex((double)f4, (double)f7, -90.0D).uv(0.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)f6, (double)f7, -90.0D).uv(1.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)f6, (double)f5, -90.0D).uv(1.0F, 0.0F).endVertex();
      bufferbuilder.vertex((double)f4, (double)f5, -90.0D).uv(0.0F, 0.0F).endVertex();
      tesselator.end();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.disableTexture();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      bufferbuilder.vertex(0.0D, (double)this.screenHeight, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)f7, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(0.0D, (double)f7, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(0.0D, (double)f5, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)f5, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, 0.0D, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(0.0D, (double)f7, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)f4, (double)f7, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)f4, (double)f5, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(0.0D, (double)f5, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)f6, (double)f7, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)f7, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)f5, -90.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex((double)f6, (double)f5, -90.0D).color(0, 0, 0, 255).endVertex();
      tesselator.end();
      RenderSystem.enableTexture();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void updateVignetteBrightness(Entity p_93021_) {
      if (p_93021_ != null) {
         float f = Mth.clamp(1.0F - p_93021_.getBrightness(), 0.0F, 1.0F);
         this.vignetteBrightness += (f - this.vignetteBrightness) * 0.01F;
      }
   }

   protected void renderVignette(Entity p_93068_) {
      WorldBorder worldborder = this.minecraft.level.getWorldBorder();
      float f = (float)worldborder.getDistanceToBorder(p_93068_);
      double d0 = Math.min(worldborder.getLerpSpeed() * (double)worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getLerpTarget() - worldborder.getSize()));
      double d1 = Math.max((double)worldborder.getWarningBlocks(), d0);
      if ((double)f < d1) {
         f = 1.0F - (float)((double)f / d1);
      } else {
         f = 0.0F;
      }

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      if (f > 0.0F) {
         f = Mth.clamp(f, 0.0F, 1.0F);
         RenderSystem.setShaderColor(0.0F, f, f, 1.0F);
      } else {
         float f1 = this.vignetteBrightness;
         f1 = Mth.clamp(f1, 0.0F, 1.0F);
         RenderSystem.setShaderColor(f1, f1, f1, 1.0F);
      }

      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
      tesselator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
   }

   protected void renderPortalOverlay(float p_93008_) {
      if (p_93008_ < 1.0F) {
         p_93008_ *= p_93008_;
         p_93008_ *= p_93008_;
         p_93008_ = p_93008_ * 0.8F + 0.2F;
      }

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, p_93008_);
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      TextureAtlasSprite textureatlassprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
      float f = textureatlassprite.getU0();
      float f1 = textureatlassprite.getV0();
      float f2 = textureatlassprite.getU1();
      float f3 = textureatlassprite.getV1();
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(f, f3).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(f2, f3).endVertex();
      bufferbuilder.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(f2, f1).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(f, f1).endVertex();
      tesselator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderSlot(int p_168678_, int p_168679_, float p_168680_, Player p_168681_, ItemStack p_168682_, int p_168683_) {
      if (!p_168682_.isEmpty()) {
         PoseStack posestack = RenderSystem.getModelViewStack();
         float f = (float)p_168682_.getPopTime() - p_168680_;
         if (f > 0.0F) {
            float f1 = 1.0F + f / 5.0F;
            posestack.pushPose();
            posestack.translate((double)(p_168678_ + 8), (double)(p_168679_ + 12), 0.0D);
            posestack.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            posestack.translate((double)(-(p_168678_ + 8)), (double)(-(p_168679_ + 12)), 0.0D);
            RenderSystem.applyModelViewMatrix();
         }

         this.itemRenderer.renderAndDecorateItem(p_168681_, p_168682_, p_168678_, p_168679_, p_168683_);
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         if (f > 0.0F) {
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
         }

         this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, p_168682_, p_168678_, p_168679_);
      }
   }

   public void tick(boolean p_193833_) {
      this.tickAutosaveIndicator();
      if (!p_193833_) {
         this.tick();
      }

   }

   private void tick() {
      if (this.overlayMessageTime > 0) {
         --this.overlayMessageTime;
      }

      if (this.titleTime > 0) {
         --this.titleTime;
         if (this.titleTime <= 0) {
            this.title = null;
            this.subtitle = null;
         }
      }

      ++this.tickCount;
      Entity entity = this.minecraft.getCameraEntity();
      if (entity != null) {
         this.updateVignetteBrightness(entity);
      }

      if (this.minecraft.player != null) {
         ItemStack itemstack = this.minecraft.player.getInventory().getSelected();
         if (itemstack.isEmpty()) {
            this.toolHighlightTimer = 0;
         } else if (!this.lastToolHighlight.isEmpty() && itemstack.getItem() == this.lastToolHighlight.getItem() && (itemstack.getHoverName().equals(this.lastToolHighlight.getHoverName()) && itemstack.getHighlightTip(itemstack.getHoverName()).equals(lastToolHighlight.getHighlightTip(lastToolHighlight.getHoverName())))) {
            if (this.toolHighlightTimer > 0) {
               --this.toolHighlightTimer;
            }
         } else {
            this.toolHighlightTimer = 40;
         }

         this.lastToolHighlight = itemstack;
      }

   }

   private void tickAutosaveIndicator() {
      MinecraftServer minecraftserver = this.minecraft.getSingleplayerServer();
      boolean flag = minecraftserver != null && minecraftserver.isCurrentlySaving();
      this.lastAutosaveIndicatorValue = this.autosaveIndicatorValue;
      this.autosaveIndicatorValue = Mth.lerp(0.2F, this.autosaveIndicatorValue, flag ? 1.0F : 0.0F);
   }

   public void setNowPlaying(Component p_93056_) {
      this.setOverlayMessage(new TranslatableComponent("record.nowPlaying", p_93056_), true);
   }

   public void setOverlayMessage(Component p_93064_, boolean p_93065_) {
      this.overlayMessageString = p_93064_;
      this.overlayMessageTime = 60;
      this.animateOverlayMessageColor = p_93065_;
   }

   public void setTimes(int p_168685_, int p_168686_, int p_168687_) {
      if (p_168685_ >= 0) {
         this.titleFadeInTime = p_168685_;
      }

      if (p_168686_ >= 0) {
         this.titleStayTime = p_168686_;
      }

      if (p_168687_ >= 0) {
         this.titleFadeOutTime = p_168687_;
      }

      if (this.titleTime > 0) {
         this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
      }

   }

   public void setSubtitle(Component p_168712_) {
      this.subtitle = p_168712_;
   }

   public void setTitle(Component p_168715_) {
      this.title = p_168715_;
      this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
   }

   public void clear() {
      this.title = null;
      this.subtitle = null;
      this.titleTime = 0;
   }

   public UUID guessChatUUID(Component p_93075_) {
      String s = StringDecomposer.getPlainText(p_93075_);
      String s1 = StringUtils.substringBetween(s, "<", ">");
      return s1 == null ? Util.NIL_UUID : this.minecraft.getPlayerSocialManager().getDiscoveredUUID(s1);
   }

   public void handleChat(ChatType p_93052_, Component p_93053_, UUID p_93054_) {
      if (!this.minecraft.isBlocked(p_93054_)) {
         if (!this.minecraft.options.hideMatchedNames || !this.minecraft.isBlocked(this.guessChatUUID(p_93053_))) {
            for(ChatListener chatlistener : this.chatListeners.get(p_93052_)) {
               chatlistener.handle(p_93052_, p_93053_, p_93054_);
            }

         }
      }
   }

   public ChatComponent getChat() {
      return this.chat;
   }

   public int getGuiTicks() {
      return this.tickCount;
   }

   public Font getFont() {
      return this.minecraft.font;
   }

   public SpectatorGui getSpectatorGui() {
      return this.spectatorGui;
   }

   public PlayerTabOverlay getTabList() {
      return this.tabList;
   }

   public void onDisconnected() {
      this.tabList.reset();
      this.bossOverlay.reset();
      this.minecraft.getToasts().clear();
      this.minecraft.options.renderDebug = false;
      this.chat.clearMessages(true);
   }

   public BossHealthOverlay getBossOverlay() {
      return this.bossOverlay;
   }

   public void clearCache() {
      this.debugScreen.clearChunkCache();
   }

   private void renderSavingIndicator(PoseStack p_193835_) {
      if (this.minecraft.options.showAutosaveIndicator && (this.autosaveIndicatorValue > 0.0F || this.lastAutosaveIndicatorValue > 0.0F)) {
         int i = Mth.floor(255.0F * Mth.clamp(Mth.lerp(this.minecraft.getFrameTime(), this.lastAutosaveIndicatorValue, this.autosaveIndicatorValue), 0.0F, 1.0F));
         if (i > 8) {
            Font font = this.getFont();
            int j = font.width(SAVING_TEXT);
            int k = 16777215 | i << 24 & -16777216;
            font.drawShadow(p_193835_, SAVING_TEXT, (float)(this.screenWidth - j - 10), (float)(this.screenHeight - 15), k);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   static enum HeartType {
      CONTAINER(0, false),
      NORMAL(2, true),
      POISIONED(4, true),
      WITHERED(6, true),
      ABSORBING(8, false),
      FROZEN(9, false);

      private final int index;
      private final boolean canBlink;

      private HeartType(int p_168729_, boolean p_168730_) {
         this.index = p_168729_;
         this.canBlink = p_168730_;
      }

      public int getX(boolean p_168735_, boolean p_168736_) {
         int i;
         if (this == CONTAINER) {
            i = p_168736_ ? 1 : 0;
         } else {
            int j = p_168735_ ? 1 : 0;
            int k = this.canBlink && p_168736_ ? 2 : 0;
            i = j + k;
         }

         return 16 + (this.index * 2 + i) * 9;
      }

      static Gui.HeartType forPlayer(Player p_168733_) {
         Gui.HeartType gui$hearttype;
         if (p_168733_.hasEffect(MobEffects.POISON)) {
            gui$hearttype = POISIONED;
         } else if (p_168733_.hasEffect(MobEffects.WITHER)) {
            gui$hearttype = WITHERED;
         } else if (p_168733_.isFullyFrozen()) {
            gui$hearttype = FROZEN;
         } else {
            gui$hearttype = NORMAL;
         }

         return gui$hearttype;
      }
   }
}
