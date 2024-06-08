package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.WorldStem;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson WORLD_GEN_SETTINGS_GSON = (new GsonBuilder()).setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
   private static final Component NAME_LABEL = new TranslatableComponent("selectWorld.enterName");
   private Button renameButton;
   private final BooleanConsumer callback;
   private EditBox nameEdit;
   private final LevelStorageSource.LevelStorageAccess levelAccess;

   public EditWorldScreen(BooleanConsumer p_101252_, LevelStorageSource.LevelStorageAccess p_101253_) {
      super(new TranslatableComponent("selectWorld.edit.title"));
      this.callback = p_101252_;
      this.levelAccess = p_101253_;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      Button button = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.resetIcon"), (p_101297_) -> {
         this.levelAccess.getIconFile().ifPresent((p_182594_) -> {
            FileUtils.deleteQuietly(p_182594_.toFile());
         });
         p_101297_.active = false;
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.openFolder"), (p_101294_) -> {
         Util.getPlatform().openFile(this.levelAccess.getLevelPath(LevelResource.ROOT).toFile());
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.backup"), (p_101292_) -> {
         boolean flag = makeBackupAndShowToast(this.levelAccess);
         this.callback.accept(!flag);
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.backupFolder"), (p_101290_) -> {
         LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();
         Path path = levelstoragesource.getBackupPath();

         try {
            Files.createDirectories(Files.exists(path) ? path.toRealPath() : path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Util.getPlatform().openFile(path.toFile());
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.optimize"), (p_101287_) -> {
         this.minecraft.setScreen(new BackupConfirmScreen(this, (p_170235_, p_170236_) -> {
            if (p_170235_) {
               makeBackupAndShowToast(this.levelAccess);
            }

            this.minecraft.setScreen(OptimizeWorldScreen.create(this.minecraft, this.callback, this.minecraft.getFixerUpper(), this.levelAccess, p_170236_));
         }, new TranslatableComponent("optimizeWorld.confirm.title"), new TranslatableComponent("optimizeWorld.confirm.description"), true));
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.export_worldgen_settings"), (p_101284_) -> {
         DataResult<String> dataresult;
         try {
            WorldStem worldstem = this.minecraft.makeWorldStem(this.levelAccess, false);

            try {
               DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, worldstem.registryAccess());
               DataResult<JsonElement> dataresult1 = WorldGenSettings.CODEC.encodeStart(dynamicops, worldstem.worldData().worldGenSettings());
               dataresult = dataresult1.flatMap((p_170231_) -> {
                  Path path = this.levelAccess.getLevelPath(LevelResource.ROOT).resolve("worldgen_settings_export.json");

                  try {
                     JsonWriter jsonwriter = WORLD_GEN_SETTINGS_GSON.newJsonWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8));

                     try {
                        WORLD_GEN_SETTINGS_GSON.toJson(p_170231_, jsonwriter);
                     } catch (Throwable throwable3) {
                        if (jsonwriter != null) {
                           try {
                              jsonwriter.close();
                           } catch (Throwable throwable2) {
                              throwable3.addSuppressed(throwable2);
                           }
                        }

                        throw throwable3;
                     }

                     if (jsonwriter != null) {
                        jsonwriter.close();
                     }
                  } catch (JsonIOException | IOException ioexception) {
                     return DataResult.error("Error writing file: " + ioexception.getMessage());
                  }

                  return DataResult.success(path.toString());
               });
            } catch (Throwable throwable1) {
               if (worldstem != null) {
                  try {
                     worldstem.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (worldstem != null) {
               worldstem.close();
            }
         } catch (Exception exception) {
            LOGGER.warn("Could not parse level data", (Throwable)exception);
            dataresult = DataResult.error("Could not parse level data: " + exception.getMessage());
         }

         Component component = new TextComponent(dataresult.get().map(Function.identity(), PartialResult::message));
         Component component1 = new TranslatableComponent(dataresult.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure");
         dataresult.error().ifPresent((p_170233_) -> {
            LOGGER.error("Error exporting world settings: {}", (Object)p_170233_);
         });
         this.minecraft.getToasts().addToast(SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, component1, component));
      }));
      this.renameButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslatableComponent("selectWorld.edit.save"), (p_101280_) -> {
         this.onRename();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, CommonComponents.GUI_CANCEL, (p_101273_) -> {
         this.callback.accept(false);
      }));
      button.active = this.levelAccess.getIconFile().filter((p_182587_) -> {
         return Files.isRegularFile(p_182587_);
      }).isPresent();
      LevelSummary levelsummary = this.levelAccess.getSummary();
      String s = levelsummary == null ? "" : levelsummary.getLevelName();
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 38, 200, 20, new TranslatableComponent("selectWorld.enterName"));
      this.nameEdit.setValue(s);
      this.nameEdit.setResponder((p_101282_) -> {
         this.renameButton.active = !p_101282_.trim().isEmpty();
      });
      this.addWidget(this.nameEdit);
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft p_101269_, int p_101270_, int p_101271_) {
      String s = this.nameEdit.getValue();
      this.init(p_101269_, p_101270_, p_101271_);
      this.nameEdit.setValue(s);
   }

   public void onClose() {
      this.callback.accept(false);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onRename() {
      try {
         this.levelAccess.renameLevel(this.nameEdit.getValue().trim());
         this.callback.accept(true);
      } catch (IOException ioexception) {
         LOGGER.error("Failed to access world '{}'", this.levelAccess.getLevelId(), ioexception);
         SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
         this.callback.accept(true);
      }

   }

   public static void makeBackupAndShowToast(LevelStorageSource p_101261_, String p_101262_) {
      boolean flag = false;

      try {
         LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = p_101261_.createAccess(p_101262_);

         try {
            flag = true;
            makeBackupAndShowToast(levelstoragesource$levelstorageaccess);
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
         if (!flag) {
            SystemToast.onWorldAccessFailure(Minecraft.getInstance(), p_101262_);
         }

         LOGGER.warn("Failed to create backup of level {}", p_101262_, ioexception);
      }

   }

   public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess p_101259_) {
      long i = 0L;
      IOException ioexception = null;

      try {
         i = p_101259_.makeWorldBackup();
      } catch (IOException ioexception1) {
         ioexception = ioexception1;
      }

      if (ioexception != null) {
         Component component2 = new TranslatableComponent("selectWorld.edit.backupFailed");
         Component component3 = new TextComponent(ioexception.getMessage());
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, component2, component3));
         return false;
      } else {
         Component component = new TranslatableComponent("selectWorld.edit.backupCreated", p_101259_.getLevelId());
         Component component1 = new TranslatableComponent("selectWorld.edit.backupSize", Mth.ceil((double)i / 1048576.0D));
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, component, component1));
         return true;
      }
   }

   public void render(PoseStack p_101264_, int p_101265_, int p_101266_, float p_101267_) {
      this.renderBackground(p_101264_);
      drawCenteredString(p_101264_, this.font, this.title, this.width / 2, 15, 16777215);
      drawString(p_101264_, this.font, NAME_LABEL, this.width / 2 - 100, 24, 10526880);
      this.nameEdit.render(p_101264_, p_101265_, p_101266_, p_101267_);
      super.render(p_101264_, p_101265_, p_101266_, p_101267_);
   }
}