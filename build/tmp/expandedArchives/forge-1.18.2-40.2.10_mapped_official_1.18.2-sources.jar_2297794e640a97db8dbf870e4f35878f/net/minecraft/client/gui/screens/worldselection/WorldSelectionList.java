package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldStem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionList extends ObjectSelectionList<WorldSelectionList.WorldListEntry> {
   static final Logger LOGGER = LogUtils.getLogger();
   static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
   private static final ResourceLocation FORGE_EXPERIMENTAL_WARNING_ICON = new ResourceLocation("forge","textures/gui/experimental_warning.png");
   static final Component FROM_NEWER_TOOLTIP_1 = (new TranslatableComponent("selectWorld.tooltip.fromNewerVersion1")).withStyle(ChatFormatting.RED);
   static final Component FROM_NEWER_TOOLTIP_2 = (new TranslatableComponent("selectWorld.tooltip.fromNewerVersion2")).withStyle(ChatFormatting.RED);
   static final Component SNAPSHOT_TOOLTIP_1 = (new TranslatableComponent("selectWorld.tooltip.snapshot1")).withStyle(ChatFormatting.GOLD);
   static final Component SNAPSHOT_TOOLTIP_2 = (new TranslatableComponent("selectWorld.tooltip.snapshot2")).withStyle(ChatFormatting.GOLD);
   static final Component WORLD_LOCKED_TOOLTIP = (new TranslatableComponent("selectWorld.locked")).withStyle(ChatFormatting.RED);
   static final Component WORLD_REQUIRES_CONVERSION = (new TranslatableComponent("selectWorld.conversion.tooltip")).withStyle(ChatFormatting.RED);
   private final SelectWorldScreen screen;
   @Nullable
   private List<LevelSummary> cachedList;

   public WorldSelectionList(SelectWorldScreen p_101658_, Minecraft p_101659_, int p_101660_, int p_101661_, int p_101662_, int p_101663_, int p_101664_, Supplier<String> p_101665_, @Nullable WorldSelectionList p_101666_) {
      super(p_101659_, p_101660_, p_101661_, p_101662_, p_101663_, p_101664_);
      this.screen = p_101658_;
      if (p_101666_ != null) {
         this.cachedList = p_101666_.cachedList;
      }

      this.refreshList(p_101665_, false);
   }

   public void refreshList(Supplier<String> p_101677_, boolean p_101678_) {
      this.clearEntries();
      LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();
      if (this.cachedList == null || p_101678_) {
         try {
            this.cachedList = levelstoragesource.getLevelList();
         } catch (LevelStorageException levelstorageexception) {
            LOGGER.error("Couldn't load level list", (Throwable)levelstorageexception);
            this.minecraft.setScreen(new ErrorScreen(new TranslatableComponent("selectWorld.unable_to_load"), new TextComponent(levelstorageexception.getMessage())));
            return;
         }

         Collections.sort(this.cachedList);
      }

      if (this.cachedList.isEmpty()) {
         this.minecraft.setScreen(CreateWorldScreen.createFresh((Screen)null));
      } else {
         String s = p_101677_.get().toLowerCase(Locale.ROOT);

         for(LevelSummary levelsummary : this.cachedList) {
            if (levelsummary.getLevelName().toLowerCase(Locale.ROOT).contains(s) || levelsummary.getLevelId().toLowerCase(Locale.ROOT).contains(s)) {
               this.addEntry(new WorldSelectionList.WorldListEntry(this, levelsummary));
            }
         }

      }
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   public void setSelected(@Nullable WorldSelectionList.WorldListEntry p_101675_) {
      super.setSelected(p_101675_);
      this.screen.updateButtonStatus(p_101675_ != null && !p_101675_.summary.isDisabled());
   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection p_101673_) {
      this.moveSelection(p_101673_, (p_101681_) -> {
         return !p_101681_.summary.isDisabled();
      });
   }

   public Optional<WorldSelectionList.WorldListEntry> getSelectedOpt() {
      return Optional.ofNullable(this.getSelected());
   }

   public SelectWorldScreen getScreen() {
      return this.screen;
   }

   @OnlyIn(Dist.CLIENT)
   public final class WorldListEntry extends ObjectSelectionList.Entry<WorldSelectionList.WorldListEntry> implements AutoCloseable {
      private static final int ICON_WIDTH = 32;
      private static final int ICON_HEIGHT = 32;
      private static final int ICON_OVERLAY_X_JOIN = 0;
      private static final int ICON_OVERLAY_X_JOIN_WITH_NOTIFY = 32;
      private static final int ICON_OVERLAY_X_WARNING = 64;
      private static final int ICON_OVERLAY_X_ERROR = 96;
      private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
      private static final int ICON_OVERLAY_Y_SELECTED = 32;
      private final Minecraft minecraft;
      private final SelectWorldScreen screen;
      final LevelSummary summary;
      private final ResourceLocation iconLocation;
      @Nullable
      private File iconFile;
      @Nullable
      private final DynamicTexture icon;
      private long lastClickTime;

      public WorldListEntry(WorldSelectionList p_101702_, LevelSummary p_101703_) {
         this.screen = p_101702_.getScreen();
         this.summary = p_101703_;
         this.minecraft = Minecraft.getInstance();
         String s = p_101703_.getLevelId();
         this.iconLocation = new ResourceLocation("minecraft", "worlds/" + Util.sanitizeName(s, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(s) + "/icon");
         this.iconFile = p_101703_.getIcon();
         if (!this.iconFile.isFile()) {
            this.iconFile = null;
         }

         this.icon = this.loadServerIcon();
      }

      public Component getNarration() {
         TranslatableComponent translatablecomponent = new TranslatableComponent("narrator.select.world", this.summary.getLevelName(), new Date(this.summary.getLastPlayed()), this.summary.isHardcore() ? new TranslatableComponent("gameMode.hardcore") : new TranslatableComponent("gameMode." + this.summary.getGameMode().getName()), this.summary.hasCheats() ? new TranslatableComponent("selectWorld.cheats") : TextComponent.EMPTY, this.summary.getWorldVersionName());
         Component component;
         if (this.summary.isLocked()) {
            component = CommonComponents.joinForNarration(translatablecomponent, WorldSelectionList.WORLD_LOCKED_TOOLTIP);
         } else {
            component = translatablecomponent;
         }

         return new TranslatableComponent("narrator.select", component);
      }

      public void render(PoseStack p_101721_, int p_101722_, int p_101723_, int p_101724_, int p_101725_, int p_101726_, int p_101727_, int p_101728_, boolean p_101729_, float p_101730_) {
         String s = this.summary.getLevelName();
         String s1 = this.summary.getLevelId() + " (" + WorldSelectionList.DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
         if (StringUtils.isEmpty(s)) {
            s = I18n.get("selectWorld.world") + " " + (p_101722_ + 1);
         }

         Component component = this.summary.getInfo();
         this.minecraft.font.draw(p_101721_, s, (float)(p_101724_ + 32 + 3), (float)(p_101723_ + 1), 16777215);
         this.minecraft.font.draw(p_101721_, s1, (float)(p_101724_ + 32 + 3), (float)(p_101723_ + 9 + 3), 8421504);
         this.minecraft.font.draw(p_101721_, component, (float)(p_101724_ + 32 + 3), (float)(p_101723_ + 9 + 9 + 3), 8421504);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : WorldSelectionList.ICON_MISSING);
         RenderSystem.enableBlend();
         GuiComponent.blit(p_101721_, p_101724_, p_101723_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
         renderExperimentalWarning(p_101721_, p_101727_, p_101728_, p_101723_, p_101724_);
         if (this.minecraft.options.touchscreen || p_101729_) {
            RenderSystem.setShaderTexture(0, WorldSelectionList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(p_101721_, p_101724_, p_101723_, p_101724_ + 32, p_101723_ + 32, -1601138544);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = p_101727_ - p_101724_;
            boolean flag = i < 32;
            int j = flag ? 32 : 0;
            if (this.summary.isLocked()) {
               GuiComponent.blit(p_101721_, p_101724_, p_101723_, 96.0F, (float)j, 32, 32, 256, 256);
               if (flag) {
                  this.screen.setToolTip(this.minecraft.font.split(WorldSelectionList.WORLD_LOCKED_TOOLTIP, 175));
               }
            } else if (this.summary.requiresManualConversion()) {
               GuiComponent.blit(p_101721_, p_101724_, p_101723_, 96.0F, (float)j, 32, 32, 256, 256);
               if (flag) {
                  this.screen.setToolTip(this.minecraft.font.split(WorldSelectionList.WORLD_REQUIRES_CONVERSION, 175));
               }
            } else if (this.summary.markVersionInList()) {
               GuiComponent.blit(p_101721_, p_101724_, p_101723_, 32.0F, (float)j, 32, 32, 256, 256);
               if (this.summary.askToOpenWorld()) {
                  GuiComponent.blit(p_101721_, p_101724_, p_101723_, 96.0F, (float)j, 32, 32, 256, 256);
                  if (flag) {
                     this.screen.setToolTip(ImmutableList.of(WorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()));
                  }
               } else if (!SharedConstants.getCurrentVersion().isStable()) {
                  GuiComponent.blit(p_101721_, p_101724_, p_101723_, 64.0F, (float)j, 32, 32, 256, 256);
                  if (flag) {
                     this.screen.setToolTip(ImmutableList.of(WorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()));
                  }
               }
            } else {
               GuiComponent.blit(p_101721_, p_101724_, p_101723_, 0.0F, (float)j, 32, 32, 256, 256);
            }
         }

      }

      public boolean mouseClicked(double p_101706_, double p_101707_, int p_101708_) {
         if (this.summary.isDisabled()) {
            return true;
         } else {
            WorldSelectionList.this.setSelected(this);
            this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
            if (p_101706_ - (double)WorldSelectionList.this.getRowLeft() <= 32.0D) {
               this.joinWorld();
               return true;
            } else if (Util.getMillis() - this.lastClickTime < 250L) {
               this.joinWorld();
               return true;
            } else {
               this.lastClickTime = Util.getMillis();
               return false;
            }
         }
      }

      public void joinWorld() {
         if (!this.summary.isDisabled()) {
            LevelSummary.BackupStatus levelsummary$backupstatus = this.summary.backupStatus();
            if (levelsummary$backupstatus.shouldBackup()) {
               String s = "selectWorld.backupQuestion." + levelsummary$backupstatus.getTranslationKey();
               String s1 = "selectWorld.backupWarning." + levelsummary$backupstatus.getTranslationKey();
               MutableComponent mutablecomponent = new TranslatableComponent(s);
               if (levelsummary$backupstatus.isSevere()) {
                  mutablecomponent.withStyle(ChatFormatting.BOLD, ChatFormatting.RED);
               }

               Component component = new TranslatableComponent(s1, this.summary.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
               this.minecraft.setScreen(new BackupConfirmScreen(this.screen, (p_101736_, p_101737_) -> {
                  if (p_101736_) {
                     String s2 = this.summary.getLevelId();

                     try {
                        LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess(s2);

                        try {
                           EditWorldScreen.makeBackupAndShowToast(levelstoragesource$levelstorageaccess);
                        } catch (Throwable throwable1) {
                           if (levelstoragesource$levelstorageaccess != null) {
                              try {
                                 levelstoragesource$levelstorageaccess.close();
                              } catch (Throwable throwable) {
                                 throwable1.addSuppressed(throwable);
                              }
                           }

                           throw throwable1;
                        }

                        if (levelstoragesource$levelstorageaccess != null) {
                           levelstoragesource$levelstorageaccess.close();
                        }
                     } catch (IOException ioexception) {
                        SystemToast.onWorldAccessFailure(this.minecraft, s2);
                        WorldSelectionList.LOGGER.error("Failed to backup level {}", s2, ioexception);
                     }
                  }

                  this.loadWorld();
               }, mutablecomponent, component, false));
            } else if (this.summary.askToOpenWorld()) {
               this.minecraft.setScreen(new ConfirmScreen((p_101741_) -> {
                  if (p_101741_) {
                     try {
                        this.loadWorld();
                     } catch (Exception exception) {
                        WorldSelectionList.LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                        this.minecraft.setScreen(new AlertScreen(() -> {
                           this.minecraft.setScreen(this.screen);
                        }, new TranslatableComponent("selectWorld.futureworld.error.title"), new TranslatableComponent("selectWorld.futureworld.error.text")));
                     }
                  } else {
                     this.minecraft.setScreen(this.screen);
                  }

               }, new TranslatableComponent("selectWorld.versionQuestion"), new TranslatableComponent("selectWorld.versionWarning", this.summary.getWorldVersionName()), new TranslatableComponent("selectWorld.versionJoinButton"), CommonComponents.GUI_CANCEL));
            } else {
               this.loadWorld();
            }

         }
      }

      public void deleteWorld() {
         this.minecraft.setScreen(new ConfirmScreen((p_170322_) -> {
            if (p_170322_) {
               this.minecraft.setScreen(new ProgressScreen(true));
               this.doDeleteWorld();
            }

            this.minecraft.setScreen(this.screen);
         }, new TranslatableComponent("selectWorld.deleteQuestion"), new TranslatableComponent("selectWorld.deleteWarning", this.summary.getLevelName()), new TranslatableComponent("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
      }

      public void doDeleteWorld() {
         LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();
         String s = this.summary.getLevelId();

         try {
            LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = levelstoragesource.createAccess(s);

            try {
               levelstoragesource$levelstorageaccess.deleteLevel();
            } catch (Throwable throwable1) {
               if (levelstoragesource$levelstorageaccess != null) {
                  try {
                     levelstoragesource$levelstorageaccess.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (levelstoragesource$levelstorageaccess != null) {
               levelstoragesource$levelstorageaccess.close();
            }
         } catch (IOException ioexception) {
            SystemToast.onWorldDeleteFailure(this.minecraft, s);
            WorldSelectionList.LOGGER.error("Failed to delete world {}", s, ioexception);
         }

         WorldSelectionList.this.refreshList(() -> {
            return this.screen.searchBox.getValue();
         }, true);
      }

      public void editWorld() {
         String s = this.summary.getLevelId();

         try {
            LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess(s);
            this.minecraft.setScreen(new EditWorldScreen((p_101719_) -> {
               try {
                  levelstoragesource$levelstorageaccess.close();
               } catch (IOException ioexception1) {
                  WorldSelectionList.LOGGER.error("Failed to unlock level {}", s, ioexception1);
               }

               if (p_101719_) {
                  WorldSelectionList.this.refreshList(() -> {
                     return this.screen.searchBox.getValue();
                  }, true);
               }

               this.minecraft.setScreen(this.screen);
            }, levelstoragesource$levelstorageaccess));
         } catch (IOException ioexception) {
            SystemToast.onWorldAccessFailure(this.minecraft, s);
            WorldSelectionList.LOGGER.error("Failed to access level {}", s, ioexception);
            WorldSelectionList.this.refreshList(() -> {
               return this.screen.searchBox.getValue();
            }, true);
         }

      }

      public void recreateWorld() {
         this.queueLoadScreen();

         try {
            LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess(this.summary.getLevelId());

            try {
               WorldStem worldstem = this.minecraft.makeWorldStem(levelstoragesource$levelstorageaccess, false);

               try {
                  WorldGenSettings worldgensettings = worldstem.worldData().worldGenSettings();
                  Path path = CreateWorldScreen.createTempDataPackDirFromExistingWorld(levelstoragesource$levelstorageaccess.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
                  if (worldgensettings.isOldCustomizedWorld()) {
                     this.minecraft.setScreen(new ConfirmScreen((p_205503_) -> {
                        this.minecraft.setScreen((Screen)(p_205503_ ? CreateWorldScreen.createFromExisting(this.screen, worldstem, path) : this.screen));
                     }, new TranslatableComponent("selectWorld.recreate.customized.title"), new TranslatableComponent("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
                  } else {
                     this.minecraft.setScreen(CreateWorldScreen.createFromExisting(this.screen, worldstem, path));
                  }
               } catch (Throwable throwable2) {
                  if (worldstem != null) {
                     try {
                        worldstem.close();
                     } catch (Throwable throwable1) {
                        throwable2.addSuppressed(throwable1);
                     }
                  }

                  throw throwable2;
               }

               if (worldstem != null) {
                  worldstem.close();
               }
            } catch (Throwable throwable3) {
               if (levelstoragesource$levelstorageaccess != null) {
                  try {
                     levelstoragesource$levelstorageaccess.close();
                  } catch (Throwable throwable) {
                     throwable3.addSuppressed(throwable);
                  }
               }

               throw throwable3;
            }

            if (levelstoragesource$levelstorageaccess != null) {
               levelstoragesource$levelstorageaccess.close();
            }
         } catch (Exception exception) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", (Throwable)exception);
            this.minecraft.setScreen(new AlertScreen(() -> {
               this.minecraft.setScreen(this.screen);
            }, new TranslatableComponent("selectWorld.recreate.error.title"), new TranslatableComponent("selectWorld.recreate.error.text")));
         }

      }

      private void loadWorld() {
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
            this.queueLoadScreen();
            this.minecraft.loadLevel(this.summary.getLevelId());
         }

      }

      private void queueLoadScreen() {
         this.minecraft.forceSetScreen(new GenericDirtMessageScreen(new TranslatableComponent("selectWorld.data_read")));
      }

      @Nullable
      private DynamicTexture loadServerIcon() {
         boolean flag = this.iconFile != null && this.iconFile.isFile();
         if (flag) {
            try {
               InputStream inputstream = new FileInputStream(this.iconFile);

               DynamicTexture dynamictexture1;
               try {
                  NativeImage nativeimage = NativeImage.read(inputstream);
                  Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
                  Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
                  DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
                  this.minecraft.getTextureManager().register(this.iconLocation, dynamictexture);
                  dynamictexture1 = dynamictexture;
               } catch (Throwable throwable1) {
                  try {
                     inputstream.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }

                  throw throwable1;
               }

               inputstream.close();
               return dynamictexture1;
            } catch (Throwable throwable2) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), throwable2);
               this.iconFile = null;
               return null;
            }
         } else {
            this.minecraft.getTextureManager().release(this.iconLocation);
            return null;
         }
      }

      public void close() {
         if (this.icon != null) {
            this.icon.close();
         }

      }

      public String getLevelName() {
         return this.summary.getLevelName();
      }
      private void renderExperimentalWarning(PoseStack stack, int mouseX, int mouseY, int top, int left) {
         if (this.summary.isExperimental()) {
            int leftStart = left + WorldSelectionList.this.getRowWidth();
            RenderSystem.setShaderTexture(0, WorldSelectionList.FORGE_EXPERIMENTAL_WARNING_ICON);
            GuiComponent.blit(stack, leftStart - 36, top, 0.0F, 0.0F, 32, 32, 32, 32);
            //Reset texture to what it was before
            RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : WorldSelectionList.ICON_MISSING);
            if (WorldSelectionList.this.getEntryAtPosition(mouseX, mouseY) == this && mouseX > leftStart - 36 && mouseX < leftStart) {
               List<net.minecraft.util.FormattedCharSequence> tooltip = Minecraft.getInstance().font.split(new TranslatableComponent("forge.experimentalsettings.tooltip"), 200);
               WorldSelectionList.this.screen.renderTooltip(stack, tooltip, mouseX, mouseY);
            }
         }
      }
   }
}
