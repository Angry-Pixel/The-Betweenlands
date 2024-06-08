package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyboardHandler {
   public static final int DEBUG_CRASH_TIME = 10000;
   private final Minecraft minecraft;
   private boolean sendRepeatsToGui;
   private final ClipboardManager clipboardManager = new ClipboardManager();
   private long debugCrashKeyTime = -1L;
   private long debugCrashKeyReportedTime = -1L;
   private long debugCrashKeyReportedCount = -1L;
   private boolean handledDebugKey;

   public KeyboardHandler(Minecraft p_90875_) {
      this.minecraft = p_90875_;
   }

   private boolean handleChunkDebugKeys(int p_167814_) {
      switch(p_167814_) {
      case 69:
         this.minecraft.chunkPath = !this.minecraft.chunkPath;
         this.debugFeedback("ChunkPath: {0}", this.minecraft.chunkPath ? "shown" : "hidden");
         return true;
      case 76:
         this.minecraft.smartCull = !this.minecraft.smartCull;
         this.debugFeedback("SmartCull: {0}", this.minecraft.smartCull ? "enabled" : "disabled");
         return true;
      case 85:
         if (Screen.hasShiftDown()) {
            this.minecraft.levelRenderer.killFrustum();
            this.debugFeedback("Killed frustum");
         } else {
            this.minecraft.levelRenderer.captureFrustum();
            this.debugFeedback("Captured frustum");
         }

         return true;
      case 86:
         this.minecraft.chunkVisibility = !this.minecraft.chunkVisibility;
         this.debugFeedback("ChunkVisibility: {0}", this.minecraft.chunkVisibility ? "enabled" : "disabled");
         return true;
      case 87:
         this.minecraft.wireframe = !this.minecraft.wireframe;
         this.debugFeedback("WireFrame: {0}", this.minecraft.wireframe ? "enabled" : "disabled");
         return true;
      default:
         return false;
      }
   }

   private void debugComponent(ChatFormatting p_167825_, Component p_167826_) {
      this.minecraft.gui.getChat().addMessage((new TextComponent("")).append((new TranslatableComponent("debug.prefix")).withStyle(new ChatFormatting[]{p_167825_, ChatFormatting.BOLD})).append(" ").append(p_167826_));
   }

   private void debugFeedbackComponent(Component p_167823_) {
      this.debugComponent(ChatFormatting.YELLOW, p_167823_);
   }

   private void debugFeedbackTranslated(String p_90914_, Object... p_90915_) {
      this.debugFeedbackComponent(new TranslatableComponent(p_90914_, p_90915_));
   }

   private void debugWarningTranslated(String p_90949_, Object... p_90950_) {
      this.debugComponent(ChatFormatting.RED, new TranslatableComponent(p_90949_, p_90950_));
   }

   private void debugFeedback(String p_167838_, Object... p_167839_) {
      this.debugFeedbackComponent(new TextComponent(MessageFormat.format(p_167838_, p_167839_)));
   }

   private boolean handleDebugKeys(int p_90933_) {
      if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
         return true;
      } else {
         switch(p_90933_) {
         case 65:
            this.minecraft.levelRenderer.allChanged();
            this.debugFeedbackTranslated("debug.reload_chunks.message");
            return true;
         case 66:
            boolean flag = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
            this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(flag);
            this.debugFeedbackTranslated(flag ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            return true;
         case 67:
            if (this.minecraft.player.isReducedDebugInfo()) {
               return false;
            } else {
               ClientPacketListener clientpacketlistener = this.minecraft.player.connection;
               if (clientpacketlistener == null) {
                  return false;
               }

               this.debugFeedbackTranslated("debug.copy_location.message");
               this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.minecraft.player.level.dimension().location(), this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), this.minecraft.player.getYRot(), this.minecraft.player.getXRot()));
               return true;
            }
         case 68:
            if (this.minecraft.gui != null) {
               this.minecraft.gui.getChat().clearMessages(false);
            }

            return true;
         case 70:
            Option.RENDER_DISTANCE.set(this.minecraft.options, Mth.clamp((double)(this.minecraft.options.renderDistance + (Screen.hasShiftDown() ? -1 : 1)), Option.RENDER_DISTANCE.getMinValue(), Option.RENDER_DISTANCE.getMaxValue()));
            this.debugFeedbackTranslated("debug.cycle_renderdistance.message", this.minecraft.options.renderDistance);
            return true;
         case 71:
            boolean flag1 = this.minecraft.debugRenderer.switchRenderChunkborder();
            this.debugFeedbackTranslated(flag1 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return true;
         case 72:
            this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
            this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            this.minecraft.options.save();
            return true;
         case 73:
            if (!this.minecraft.player.isReducedDebugInfo()) {
               this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
            }

            return true;
         case 76:
            if (this.minecraft.debugClientMetricsStart(this::debugFeedbackComponent)) {
               this.debugFeedbackTranslated("debug.profiling.start", 10);
            }

            return true;
         case 78:
            if (!this.minecraft.player.hasPermissions(2)) {
               this.debugFeedbackTranslated("debug.creative_spectator.error");
            } else if (!this.minecraft.player.isSpectator()) {
               this.minecraft.player.chat("/gamemode spectator");
            } else {
               this.minecraft.player.chat("/gamemode " + MoreObjects.firstNonNull(this.minecraft.gameMode.getPreviousPlayerMode(), GameType.CREATIVE).getName());
            }

            return true;
         case 80:
            this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
            this.minecraft.options.save();
            this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
            return true;
         case 81:
            this.debugFeedbackTranslated("debug.help.message");
            ChatComponent chatcomponent = this.minecraft.gui.getChat();
            chatcomponent.addMessage(new TranslatableComponent("debug.reload_chunks.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.show_hitboxes.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.copy_location.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.clear_chat.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.cycle_renderdistance.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.chunk_boundaries.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.advanced_tooltips.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.inspect.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.profiling.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.creative_spectator.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.pause_focus.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.help.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.reload_resourcepacks.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.pause.help"));
            chatcomponent.addMessage(new TranslatableComponent("debug.gamemodes.help"));
            return true;
         case 84:
            this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
            this.minecraft.reloadResourcePacks();
            return true;
         case 293:
            if (!this.minecraft.player.hasPermissions(2)) {
               this.debugFeedbackTranslated("debug.gamemodes.error");
            } else {
               this.minecraft.setScreen(new GameModeSwitcherScreen());
            }

            return true;
         default:
            return false;
         }
      }
   }

   private void copyRecreateCommand(boolean p_90929_, boolean p_90930_) {
      HitResult hitresult = this.minecraft.hitResult;
      if (hitresult != null) {
         switch(hitresult.getType()) {
         case BLOCK:
            BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
            BlockState blockstate = this.minecraft.player.level.getBlockState(blockpos);
            if (p_90929_) {
               if (p_90930_) {
                  this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(blockpos, (p_90947_) -> {
                     this.copyCreateBlockCommand(blockstate, blockpos, p_90947_);
                     this.debugFeedbackTranslated("debug.inspect.server.block");
                  });
               } else {
                  BlockEntity blockentity = this.minecraft.player.level.getBlockEntity(blockpos);
                  CompoundTag compoundtag1 = blockentity != null ? blockentity.saveWithoutMetadata() : null;
                  this.copyCreateBlockCommand(blockstate, blockpos, compoundtag1);
                  this.debugFeedbackTranslated("debug.inspect.client.block");
               }
            } else {
               this.copyCreateBlockCommand(blockstate, blockpos, (CompoundTag)null);
               this.debugFeedbackTranslated("debug.inspect.client.block");
            }
            break;
         case ENTITY:
            Entity entity = ((EntityHitResult)hitresult).getEntity();
            ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(entity.getType());
            if (p_90929_) {
               if (p_90930_) {
                  this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag(entity.getId(), (p_90921_) -> {
                     this.copyCreateEntityCommand(resourcelocation, entity.position(), p_90921_);
                     this.debugFeedbackTranslated("debug.inspect.server.entity");
                  });
               } else {
                  CompoundTag compoundtag = entity.saveWithoutId(new CompoundTag());
                  this.copyCreateEntityCommand(resourcelocation, entity.position(), compoundtag);
                  this.debugFeedbackTranslated("debug.inspect.client.entity");
               }
            } else {
               this.copyCreateEntityCommand(resourcelocation, entity.position(), (CompoundTag)null);
               this.debugFeedbackTranslated("debug.inspect.client.entity");
            }
         }

      }
   }

   private void copyCreateBlockCommand(BlockState p_90900_, BlockPos p_90901_, @Nullable CompoundTag p_90902_) {
      StringBuilder stringbuilder = new StringBuilder(BlockStateParser.serialize(p_90900_));
      if (p_90902_ != null) {
         stringbuilder.append((Object)p_90902_);
      }

      String s = String.format(Locale.ROOT, "/setblock %d %d %d %s", p_90901_.getX(), p_90901_.getY(), p_90901_.getZ(), stringbuilder);
      this.setClipboard(s);
   }

   private void copyCreateEntityCommand(ResourceLocation p_90923_, Vec3 p_90924_, @Nullable CompoundTag p_90925_) {
      String s;
      if (p_90925_ != null) {
         p_90925_.remove("UUID");
         p_90925_.remove("Pos");
         p_90925_.remove("Dimension");
         String s1 = NbtUtils.toPrettyComponent(p_90925_).getString();
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", p_90923_.toString(), p_90924_.x, p_90924_.y, p_90924_.z, s1);
      } else {
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", p_90923_.toString(), p_90924_.x, p_90924_.y, p_90924_.z);
      }

      this.setClipboard(s);
   }

   public void keyPress(long p_90894_, int p_90895_, int p_90896_, int p_90897_, int p_90898_) {
      if (p_90894_ == this.minecraft.getWindow().getWindow()) {
         if (this.debugCrashKeyTime > 0L) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) || !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
               this.debugCrashKeyTime = -1L;
            }
         } else if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
         }

         Screen screen = this.minecraft.screen;

         if ((!(this.minecraft.screen instanceof KeyBindsScreen) || ((KeyBindsScreen)screen).lastKeySelection <= Util.getMillis() - 20L)) {
            if (p_90897_ == 1) {
            if (this.minecraft.options.keyFullscreen.matches(p_90895_, p_90896_)) {
               this.minecraft.getWindow().toggleFullScreen();
               this.minecraft.options.fullscreen = this.minecraft.getWindow().isFullscreen();
               this.minecraft.options.save();
               return;
            }

            if (this.minecraft.options.keyScreenshot.matches(p_90895_, p_90896_)) {
               if (Screen.hasControlDown()) {
               }

               Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.getMainRenderTarget(), (p_90917_) -> {
                  this.minecraft.execute(() -> {
                     this.minecraft.gui.getChat().addMessage(p_90917_);
                  });
               });
               return;
            }
            } else if (p_90897_ == 0 /*GLFW_RELEASE*/ && this.minecraft.screen instanceof KeyBindsScreen)
               ((KeyBindsScreen)this.minecraft.screen).selectedKey = null; //Forge: Unset pure modifiers.
         }

         if (NarratorChatListener.INSTANCE.isActive()) {
            boolean flag = screen == null || !(screen.getFocused() instanceof EditBox) || !((EditBox)screen.getFocused()).canConsumeInput();
            if (p_90897_ != 0 && p_90895_ == 66 && Screen.hasControlDown() && flag) {
               boolean flag1 = this.minecraft.options.narratorStatus == NarratorStatus.OFF;
               this.minecraft.options.narratorStatus = NarratorStatus.byId(this.minecraft.options.narratorStatus.getId() + 1);
               NarratorChatListener.INSTANCE.updateNarratorStatus(this.minecraft.options.narratorStatus);
               if (screen instanceof SimpleOptionsSubScreen) {
                  ((SimpleOptionsSubScreen)screen).updateNarratorButton();
               }

               if (flag1 && screen != null) {
                  screen.narrationEnabled();
               }
            }
         }

         if (screen != null) {
            boolean[] aboolean = new boolean[]{false};
            Screen.wrapScreenError(() -> {
               if (p_90897_ != 1 && (p_90897_ != 2 || !this.sendRepeatsToGui)) {
                  if (p_90897_ == 0) {
                     aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onScreenKeyReleasedPre(screen, p_90895_, p_90896_, p_90898_);
                     if (!aboolean[0]) aboolean[0] = screen.keyReleased(p_90895_, p_90896_, p_90898_);
                     if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onScreenKeyReleasedPost(screen, p_90895_, p_90896_, p_90898_);
                  }
               } else {
                  screen.afterKeyboardAction();
                  aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onScreenKeyPressedPre(screen, p_90895_, p_90896_, p_90898_);
                  if (!aboolean[0]) aboolean[0] = screen.keyPressed(p_90895_, p_90896_, p_90898_);
                  if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onScreenKeyPressedPost(screen, p_90895_, p_90896_, p_90898_);
               }

            }, "keyPressed event handler", screen.getClass().getCanonicalName());
            if (aboolean[0]) {
               return;
            }
         }

         if (this.minecraft.screen == null || this.minecraft.screen.passEvents) {
            InputConstants.Key inputconstants$key = InputConstants.getKey(p_90895_, p_90896_);
            if (p_90897_ == 0) {
               KeyMapping.set(inputconstants$key, false);
               if (p_90895_ == 292) {
                  if (this.handledDebugKey) {
                     this.handledDebugKey = false;
                  } else {
                     this.minecraft.options.renderDebug = !this.minecraft.options.renderDebug;
                     this.minecraft.options.renderDebugCharts = this.minecraft.options.renderDebug && Screen.hasShiftDown();
                     this.minecraft.options.renderFpsChart = this.minecraft.options.renderDebug && Screen.hasAltDown();
                  }
               }
            } else {
               if (p_90895_ == 293 && this.minecraft.gameRenderer != null) {
                  this.minecraft.gameRenderer.togglePostEffect();
               }

               boolean flag3 = false;
               if (this.minecraft.screen == null) {
                  if (p_90895_ == 256) {
                     boolean flag2 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
                     this.minecraft.pauseGame(flag2);
                  }

                  flag3 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292) && this.handleDebugKeys(p_90895_);
                  this.handledDebugKey |= flag3;
                  if (p_90895_ == 290) {
                     this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
                  }
               }

               if (flag3) {
                  KeyMapping.set(inputconstants$key, false);
               } else {
                  KeyMapping.set(inputconstants$key, true);
                  KeyMapping.click(inputconstants$key);
               }

               if (this.minecraft.options.renderDebugCharts && p_90895_ >= 48 && p_90895_ <= 57) {
                  this.minecraft.debugFpsMeterKeyPress(p_90895_ - 48);
               }
            }
         }
         net.minecraftforge.client.ForgeHooksClient.fireKeyInput(p_90895_, p_90896_, p_90897_, p_90898_);
      }
   }

   private void charTyped(long p_90890_, int p_90891_, int p_90892_) {
      if (p_90890_ == this.minecraft.getWindow().getWindow()) {
         Screen guieventlistener = this.minecraft.screen;
         if (guieventlistener != null && this.minecraft.getOverlay() == null) {
            if (Character.charCount(p_90891_) == 1) {
               Screen.wrapScreenError(() -> {
                  if (net.minecraftforge.client.ForgeHooksClient.onScreenCharTypedPre(guieventlistener, (char)p_90891_, p_90892_)) return;
                  if (guieventlistener.charTyped((char)p_90891_, p_90892_)) return;
                  net.minecraftforge.client.ForgeHooksClient.onScreenCharTypedPost(guieventlistener, (char)p_90891_, p_90892_);
               }, "charTyped event handler", guieventlistener.getClass().getCanonicalName());
            } else {
               for(char c0 : Character.toChars(p_90891_)) {
                  Screen.wrapScreenError(() -> {
                     if (net.minecraftforge.client.ForgeHooksClient.onScreenCharTypedPre(guieventlistener, c0, p_90892_)) return;
                     if (guieventlistener.charTyped(c0, p_90892_)) return;
                     net.minecraftforge.client.ForgeHooksClient.onScreenCharTypedPost(guieventlistener, c0, p_90892_);
                  }, "charTyped event handler", guieventlistener.getClass().getCanonicalName());
               }
            }

         }
      }
   }

   public void setSendRepeatsToGui(boolean p_90927_) {
      this.sendRepeatsToGui = p_90927_;
   }

   public void setup(long p_90888_) {
      InputConstants.setupKeyboardCallbacks(p_90888_, (p_90939_, p_90940_, p_90941_, p_90942_, p_90943_) -> {
         this.minecraft.execute(() -> {
            this.keyPress(p_90939_, p_90940_, p_90941_, p_90942_, p_90943_);
         });
      }, (p_90935_, p_90936_, p_90937_) -> {
         this.minecraft.execute(() -> {
            this.charTyped(p_90935_, p_90936_, p_90937_);
         });
      });
   }

   public String getClipboard() {
      return this.clipboardManager.getClipboard(this.minecraft.getWindow().getWindow(), (p_90878_, p_90879_) -> {
         if (p_90878_ != 65545) {
            this.minecraft.getWindow().defaultErrorCallback(p_90878_, p_90879_);
         }

      });
   }

   public void setClipboard(String p_90912_) {
      if (!p_90912_.isEmpty()) {
         this.clipboardManager.setClipboard(this.minecraft.getWindow().getWindow(), p_90912_);
      }

   }

   public void tick() {
      if (this.debugCrashKeyTime > 0L) {
         long i = Util.getMillis();
         long j = 10000L - (i - this.debugCrashKeyTime);
         long k = i - this.debugCrashKeyReportedTime;
         if (j < 0L) {
            if (Screen.hasControlDown()) {
               Blaze3D.youJustLostTheGame();
            }

            String s = "Manually triggered debug crash";
            CrashReport crashreport = new CrashReport("Manually triggered debug crash", new Throwable("Manually triggered debug crash"));
            CrashReportCategory crashreportcategory = crashreport.addCategory("Manual crash details");
            NativeModuleLister.addCrashSection(crashreportcategory);
            throw new ReportedException(crashreport);
         }

         if (k >= 1000L) {
            if (this.debugCrashKeyReportedCount == 0L) {
               this.debugFeedbackTranslated("debug.crash.message");
            } else {
               this.debugWarningTranslated("debug.crash.warning", Mth.ceil((float)j / 1000.0F));
            }

            this.debugCrashKeyReportedTime = i;
            ++this.debugCrashKeyReportedCount;
         }
      }

   }
}
