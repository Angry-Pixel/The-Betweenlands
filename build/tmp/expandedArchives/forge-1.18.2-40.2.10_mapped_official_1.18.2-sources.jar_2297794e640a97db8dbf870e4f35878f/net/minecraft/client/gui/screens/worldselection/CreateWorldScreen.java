package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreateWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TEMP_WORLD_PREFIX = "mcworld-";
   private static final Component GAME_MODEL_LABEL = new TranslatableComponent("selectWorld.gameMode");
   private static final Component SEED_LABEL = new TranslatableComponent("selectWorld.enterSeed");
   private static final Component SEED_INFO = new TranslatableComponent("selectWorld.seedInfo");
   private static final Component NAME_LABEL = new TranslatableComponent("selectWorld.enterName");
   private static final Component OUTPUT_DIR_INFO = new TranslatableComponent("selectWorld.resultFolder");
   private static final Component COMMANDS_INFO = new TranslatableComponent("selectWorld.allowCommands.info");
   @Nullable
   private final Screen lastScreen;
   private EditBox nameEdit;
   String resultFolder;
   private CreateWorldScreen.SelectedGameMode gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
   @Nullable
   private CreateWorldScreen.SelectedGameMode oldGameMode;
   private Difficulty difficulty = Difficulty.NORMAL;
   private boolean commands;
   private boolean commandsChanged;
   public boolean hardCore;
   protected DataPackConfig dataPacks;
   @Nullable
   private Path tempDataPackDir;
   @Nullable
   private PackRepository tempDataPackRepository;
   private boolean worldGenSettingsVisible;
   private Button createButton;
   private CycleButton<CreateWorldScreen.SelectedGameMode> modeButton;
   private CycleButton<Difficulty> difficultyButton;
   private Button moreOptionsButton;
   private Button gameRulesButton;
   private Button dataPacksButton;
   private CycleButton<Boolean> commandsButton;
   private Component gameModeHelp1;
   private Component gameModeHelp2;
   private String initName;
   private GameRules gameRules = new GameRules();
   public final WorldGenSettingsComponent worldGenSettingsComponent;

   public static CreateWorldScreen createFresh(@Nullable Screen p_205425_) {
      RegistryAccess.Frozen registryaccess$frozen = RegistryAccess.BUILTIN.get();
      return new CreateWorldScreen(p_205425_, DataPackConfig.DEFAULT, new WorldGenSettingsComponent(registryaccess$frozen, net.minecraftforge.client.ForgeHooksClient.getDefaultWorldPreset().map(type -> type.create(registryaccess$frozen, new java.util.Random().nextLong(), true, false)).orElseGet(() -> WorldGenSettings.makeDefault(registryaccess$frozen)), net.minecraftforge.client.ForgeHooksClient.getDefaultWorldPreset(), OptionalLong.empty()));
   }

   public static CreateWorldScreen createFromExisting(@Nullable Screen p_205427_, WorldStem p_205428_, @Nullable Path p_205429_) {
      WorldData worlddata = p_205428_.worldData();
      LevelSettings levelsettings = worlddata.getLevelSettings();
      WorldGenSettings worldgensettings = worlddata.worldGenSettings();
      RegistryAccess.Frozen registryaccess$frozen = p_205428_.registryAccess();
      DataPackConfig datapackconfig = levelsettings.getDataPackConfig();
      CreateWorldScreen createworldscreen = new CreateWorldScreen(p_205427_, datapackconfig, new WorldGenSettingsComponent(registryaccess$frozen, worldgensettings, WorldPreset.of(worldgensettings), OptionalLong.of(worldgensettings.seed())));
      createworldscreen.initName = levelsettings.levelName();
      createworldscreen.commands = levelsettings.allowCommands();
      createworldscreen.commandsChanged = true;
      createworldscreen.difficulty = levelsettings.difficulty();
      createworldscreen.gameRules.assignFrom(levelsettings.gameRules(), (MinecraftServer)null);
      if (levelsettings.hardcore()) {
         createworldscreen.gameMode = CreateWorldScreen.SelectedGameMode.HARDCORE;
      } else if (levelsettings.gameType().isSurvival()) {
         createworldscreen.gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
      } else if (levelsettings.gameType().isCreative()) {
         createworldscreen.gameMode = CreateWorldScreen.SelectedGameMode.CREATIVE;
      }

      createworldscreen.tempDataPackDir = p_205429_;
      return createworldscreen;
   }

   private CreateWorldScreen(@Nullable Screen p_100861_, DataPackConfig p_100862_, WorldGenSettingsComponent p_100863_) {
      super(new TranslatableComponent("selectWorld.create"));
      this.lastScreen = p_100861_;
      this.initName = I18n.get("selectWorld.newWorld");
      this.dataPacks = p_100862_;
      this.worldGenSettingsComponent = p_100863_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.worldGenSettingsComponent.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, new TranslatableComponent("selectWorld.enterName")) {
         protected MutableComponent createNarrationMessage() {
            return CommonComponents.joinForNarration(super.createNarrationMessage(), new TranslatableComponent("selectWorld.resultFolder")).append(" ").append(CreateWorldScreen.this.resultFolder);
         }
      };
      this.nameEdit.setValue(this.initName);
      this.nameEdit.setResponder((p_100932_) -> {
         this.initName = p_100932_;
         this.createButton.active = !this.nameEdit.getValue().isEmpty();
         this.updateResultFolder();
      });
      this.addWidget(this.nameEdit);
      int i = this.width / 2 - 155;
      int j = this.width / 2 + 5;
      this.modeButton = this.addRenderableWidget(CycleButton.builder(CreateWorldScreen.SelectedGameMode::getDisplayName).withValues(CreateWorldScreen.SelectedGameMode.SURVIVAL, CreateWorldScreen.SelectedGameMode.HARDCORE, CreateWorldScreen.SelectedGameMode.CREATIVE).withInitialValue(this.gameMode).withCustomNarration((p_170190_) -> {
         return AbstractWidget.wrapDefaultNarrationMessage(p_170190_.getMessage()).append(CommonComponents.NARRATION_SEPARATOR).append(this.gameModeHelp1).append(" ").append(this.gameModeHelp2);
      }).create(i, 100, 150, 20, GAME_MODEL_LABEL, (p_170165_, p_170166_) -> {
         this.setGameMode(p_170166_);
      }));
      this.difficultyButton = this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).withInitialValue(this.getEffectiveDifficulty()).create(j, 100, 150, 20, new TranslatableComponent("options.difficulty"), (p_170162_, p_170163_) -> {
         this.difficulty = p_170163_;
      }));
      this.commandsButton = this.addRenderableWidget(CycleButton.onOffBuilder(this.commands && !this.hardCore).withCustomNarration((p_170160_) -> {
         return CommonComponents.joinForNarration(p_170160_.createDefaultNarrationMessage(), new TranslatableComponent("selectWorld.allowCommands.info"));
      }).create(i, 151, 150, 20, new TranslatableComponent("selectWorld.allowCommands"), (p_170168_, p_170169_) -> {
         this.commandsChanged = true;
         this.commands = p_170169_;
      }));
      this.dataPacksButton = this.addRenderableWidget(new Button(j, 151, 150, 20, new TranslatableComponent("selectWorld.dataPacks"), (p_170201_) -> {
         this.openDataPackSelectionScreen();
      }));
      this.gameRulesButton = this.addRenderableWidget(new Button(i, 185, 150, 20, new TranslatableComponent("selectWorld.gameRules"), (p_100928_) -> {
         this.minecraft.setScreen(new EditGameRulesScreen(this.gameRules.copy(), (p_170182_) -> {
            this.minecraft.setScreen(this);
            p_170182_.ifPresent((p_170156_) -> {
               this.gameRules = p_170156_;
            });
         }));
      }));
      this.worldGenSettingsComponent.init(this, this.minecraft, this.font);
      this.moreOptionsButton = this.addRenderableWidget(new Button(j, 185, 150, 20, new TranslatableComponent("selectWorld.moreWorldOptions"), (p_100897_) -> {
         this.toggleWorldGenSettingsVisibility();
      }));
      this.createButton = this.addRenderableWidget(new Button(i, this.height - 28, 150, 20, new TranslatableComponent("selectWorld.create"), (p_170188_) -> {
         this.onCreate();
      }));
      this.createButton.active = !this.initName.isEmpty();
      this.addRenderableWidget(new Button(j, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (p_170158_) -> {
         this.popScreen();
      }));
      this.refreshWorldGenSettingsVisibility();
      this.setInitialFocus(this.nameEdit);
      this.setGameMode(this.gameMode);
      this.updateResultFolder();
   }

   private Difficulty getEffectiveDifficulty() {
      return this.gameMode == CreateWorldScreen.SelectedGameMode.HARDCORE ? Difficulty.HARD : this.difficulty;
   }

   private void updateGameModeHelp() {
      this.gameModeHelp1 = new TranslatableComponent("selectWorld.gameMode." + this.gameMode.name + ".line1");
      this.gameModeHelp2 = new TranslatableComponent("selectWorld.gameMode." + this.gameMode.name + ".line2");
   }

   private void updateResultFolder() {
      this.resultFolder = this.nameEdit.getValue().trim();
      if (this.resultFolder.isEmpty()) {
         this.resultFolder = "World";
      }

      try {
         this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
      } catch (Exception exception1) {
         this.resultFolder = "World";

         try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
         } catch (Exception exception) {
            throw new RuntimeException("Could not create save folder", exception);
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onCreate() {
      this.minecraft.forceSetScreen(new GenericDirtMessageScreen(new TranslatableComponent("createWorld.preparing")));
      if (this.copyTempDataPackDirToNewWorld()) {
         this.cleanupTempResources();
         WorldGenSettings worldgensettings = this.worldGenSettingsComponent.makeSettings(this.hardCore);
         LevelSettings levelsettings = this.createLevelSettings(worldgensettings.isDebug());
         this.minecraft.createLevel(this.resultFolder, levelsettings, this.worldGenSettingsComponent.registryHolder(), worldgensettings);
      }
   }

   private LevelSettings createLevelSettings(boolean p_205448_) {
      String s = this.nameEdit.getValue().trim();
      if (p_205448_) {
         GameRules gamerules = new GameRules();
         gamerules.getRule(GameRules.RULE_DAYLIGHT).set(false, (MinecraftServer)null);
         return new LevelSettings(s, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, gamerules, DataPackConfig.DEFAULT);
      } else {
         return new LevelSettings(s, this.gameMode.gameType, this.hardCore, this.getEffectiveDifficulty(), this.commands && !this.hardCore, this.gameRules, this.dataPacks);
      }
   }

   private void toggleWorldGenSettingsVisibility() {
      this.setWorldGenSettingsVisible(!this.worldGenSettingsVisible);
   }

   private void setGameMode(CreateWorldScreen.SelectedGameMode p_100901_) {
      if (!this.commandsChanged) {
         this.commands = p_100901_ == CreateWorldScreen.SelectedGameMode.CREATIVE;
         this.commandsButton.setValue(this.commands);
      }

      if (p_100901_ == CreateWorldScreen.SelectedGameMode.HARDCORE) {
         this.hardCore = true;
         this.commandsButton.active = false;
         this.commandsButton.setValue(false);
         this.worldGenSettingsComponent.switchToHardcore();
         this.difficultyButton.setValue(Difficulty.HARD);
         this.difficultyButton.active = false;
      } else {
         this.hardCore = false;
         this.commandsButton.active = true;
         this.commandsButton.setValue(this.commands);
         this.worldGenSettingsComponent.switchOutOfHardcode();
         this.difficultyButton.setValue(this.difficulty);
         this.difficultyButton.active = true;
      }

      this.gameMode = p_100901_;
      this.updateGameModeHelp();
   }

   public void refreshWorldGenSettingsVisibility() {
      this.setWorldGenSettingsVisible(this.worldGenSettingsVisible);
   }

   private void setWorldGenSettingsVisible(boolean p_170197_) {
      this.worldGenSettingsVisible = p_170197_;
      this.modeButton.visible = !p_170197_;
      this.difficultyButton.visible = !p_170197_;
      if (this.worldGenSettingsComponent.isDebug()) {
         this.dataPacksButton.visible = false;
         this.modeButton.active = false;
         if (this.oldGameMode == null) {
            this.oldGameMode = this.gameMode;
         }

         this.setGameMode(CreateWorldScreen.SelectedGameMode.DEBUG);
         this.commandsButton.visible = false;
      } else {
         this.modeButton.active = true;
         if (this.oldGameMode != null) {
            this.setGameMode(this.oldGameMode);
         }

         this.commandsButton.visible = !p_170197_;
         this.dataPacksButton.visible = !p_170197_;
      }

      this.worldGenSettingsComponent.setVisibility(p_170197_);
      this.nameEdit.setVisible(!p_170197_);
      if (p_170197_) {
         this.moreOptionsButton.setMessage(CommonComponents.GUI_DONE);
      } else {
         this.moreOptionsButton.setMessage(new TranslatableComponent("selectWorld.moreWorldOptions"));
      }

      this.gameRulesButton.visible = !p_170197_;
   }

   public boolean keyPressed(int p_100875_, int p_100876_, int p_100877_) {
      if (super.keyPressed(p_100875_, p_100876_, p_100877_)) {
         return true;
      } else if (p_100875_ != 257 && p_100875_ != 335) {
         return false;
      } else {
         this.onCreate();
         return true;
      }
   }

   public void onClose() {
      if (this.worldGenSettingsVisible) {
         this.setWorldGenSettingsVisible(false);
      } else {
         this.popScreen();
      }

   }

   public void popScreen() {
      this.minecraft.setScreen(this.lastScreen);
      this.cleanupTempResources();
   }

   private void cleanupTempResources() {
      if (this.tempDataPackRepository != null) {
         this.tempDataPackRepository.close();
      }

      this.removeTempDataPackDir();
   }

   public void render(PoseStack p_100890_, int p_100891_, int p_100892_, float p_100893_) {
      this.renderBackground(p_100890_);
      drawCenteredString(p_100890_, this.font, this.title, this.width / 2, 20, -1);
      if (this.worldGenSettingsVisible) {
         drawString(p_100890_, this.font, SEED_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(p_100890_, this.font, SEED_INFO, this.width / 2 - 100, 85, -6250336);
         this.worldGenSettingsComponent.render(p_100890_, p_100891_, p_100892_, p_100893_);
      } else {
         drawString(p_100890_, this.font, NAME_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(p_100890_, this.font, (new TextComponent("")).append(OUTPUT_DIR_INFO).append(" ").append(this.resultFolder), this.width / 2 - 100, 85, -6250336);
         this.nameEdit.render(p_100890_, p_100891_, p_100892_, p_100893_);
         drawString(p_100890_, this.font, this.gameModeHelp1, this.width / 2 - 150, 122, -6250336);
         drawString(p_100890_, this.font, this.gameModeHelp2, this.width / 2 - 150, 134, -6250336);
         if (this.commandsButton.visible) {
            drawString(p_100890_, this.font, COMMANDS_INFO, this.width / 2 - 150, 172, -6250336);
         }
      }

      super.render(p_100890_, p_100891_, p_100892_, p_100893_);
   }

   protected <T extends GuiEventListener & NarratableEntry> T addWidget(T p_100948_) {
      return super.addWidget(p_100948_);
   }

   protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T p_170199_) {
      return super.addRenderableWidget(p_170199_);
   }

   @Nullable
   protected Path getTempDataPackDir() {
      if (this.tempDataPackDir == null) {
         try {
            this.tempDataPackDir = Files.createTempDirectory("mcworld-");
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to create temporary dir", (Throwable)ioexception);
            SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
            this.popScreen();
         }
      }

      return this.tempDataPackDir;
   }

   private void openDataPackSelectionScreen() {
      Pair<File, PackRepository> pair = this.getDataPackSelectionSettings();
      if (pair != null) {
         this.minecraft.setScreen(new PackSelectionScreen(this, pair.getSecond(), this::tryApplyNewDataPacks, pair.getFirst(), new TranslatableComponent("dataPack.title")));
      }

   }

   private void tryApplyNewDataPacks(PackRepository p_100879_) {
      List<String> list = ImmutableList.copyOf(p_100879_.getSelectedIds());
      List<String> list1 = p_100879_.getAvailableIds().stream().filter((p_170180_) -> {
         return !list.contains(p_170180_);
      }).collect(ImmutableList.toImmutableList());
      DataPackConfig datapackconfig = new DataPackConfig(list, list1);
      if (list.equals(this.dataPacks.getEnabled())) {
         this.dataPacks = datapackconfig;
      } else {
         this.minecraft.tell(() -> {
            this.minecraft.setScreen(new GenericDirtMessageScreen(new TranslatableComponent("dataPack.validation.working")));
         });
         WorldStem.load(new WorldStem.InitConfig(p_100879_, Commands.CommandSelection.INTEGRATED, 2, false), () -> {
            return datapackconfig;
         }, (p_205414_, p_205415_) -> {
            RegistryAccess registryaccess = this.worldGenSettingsComponent.registryHolder();
            RegistryAccess.Writable registryaccess$writable = RegistryAccess.builtinCopy();
            DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, registryaccess);
            DynamicOps<JsonElement> dynamicops1 = RegistryOps.createAndLoad(JsonOps.INSTANCE, registryaccess$writable, p_205414_);
            DataResult<WorldGenSettings> dataresult = WorldGenSettings.CODEC.encodeStart(dynamicops, this.worldGenSettingsComponent.makeSettings(this.hardCore)).flatMap((p_205423_) -> {
               return WorldGenSettings.CODEC.parse(dynamicops1, p_205423_);
            });
            WorldGenSettings worldgensettings = dataresult.getOrThrow(false, Util.prefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error));
            LevelSettings levelsettings = this.createLevelSettings(worldgensettings.isDebug());
            return Pair.of(new PrimaryLevelData(levelsettings, worldgensettings, dataresult.lifecycle()), registryaccess$writable.freeze());
         }, Util.backgroundExecutor(), this.minecraft).thenAcceptAsync((p_205420_) -> {
            this.dataPacks = datapackconfig;
            this.worldGenSettingsComponent.updateDataPacks(p_205420_);
            p_205420_.close();
         }, this.minecraft).handle((p_205431_, p_205432_) -> {
            if (p_205432_ != null) {
               LOGGER.warn("Failed to validate datapack", p_205432_);
               this.minecraft.tell(() -> {
                  this.minecraft.setScreen(new ConfirmScreen((p_205450_) -> {
                     if (p_205450_) {
                        this.openDataPackSelectionScreen();
                     } else {
                        this.dataPacks = DataPackConfig.DEFAULT;
                        this.minecraft.setScreen(this);
                     }

                  }, new TranslatableComponent("dataPack.validation.failed"), TextComponent.EMPTY, new TranslatableComponent("dataPack.validation.back"), new TranslatableComponent("dataPack.validation.reset")));
               });
            } else {
               this.minecraft.tell(() -> {
                  this.minecraft.setScreen(this);
               });
            }

            return null;
         });
      }
   }

   private void removeTempDataPackDir() {
      if (this.tempDataPackDir != null) {
         try {
            Stream<Path> stream = Files.walk(this.tempDataPackDir);

            try {
               stream.sorted(Comparator.reverseOrder()).forEach((p_205443_) -> {
                  try {
                     Files.delete(p_205443_);
                  } catch (IOException ioexception1) {
                     LOGGER.warn("Failed to remove temporary file {}", p_205443_, ioexception1);
                  }

               });
            } catch (Throwable throwable1) {
               if (stream != null) {
                  try {
                     stream.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (stream != null) {
               stream.close();
            }
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to list temporary dir {}", (Object)this.tempDataPackDir);
         }

         this.tempDataPackDir = null;
      }

   }

   private static void copyBetweenDirs(Path p_100913_, Path p_100914_, Path p_100915_) {
      try {
         Util.copyBetweenDirs(p_100913_, p_100914_, p_100915_);
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to copy datapack file from {} to {}", p_100915_, p_100914_);
         throw new CreateWorldScreen.OperationFailedException(ioexception);
      }
   }

   private boolean copyTempDataPackDirToNewWorld() {
      if (this.tempDataPackDir != null) {
         try {
            LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess(this.resultFolder);

            try {
               Stream<Path> stream = Files.walk(this.tempDataPackDir);

               try {
                  Path path = levelstoragesource$levelstorageaccess.getLevelPath(LevelResource.DATAPACK_DIR);
                  Files.createDirectories(path);
                  stream.filter((p_205434_) -> {
                     return !p_205434_.equals(this.tempDataPackDir);
                  }).forEach((p_205446_) -> {
                     copyBetweenDirs(this.tempDataPackDir, path, p_205446_);
                  });
               } catch (Throwable throwable2) {
                  if (stream != null) {
                     try {
                        stream.close();
                     } catch (Throwable throwable1) {
                        throwable2.addSuppressed(throwable1);
                     }
                  }

                  throw throwable2;
               }

               if (stream != null) {
                  stream.close();
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
         } catch (CreateWorldScreen.OperationFailedException | IOException ioexception) {
            LOGGER.warn("Failed to copy datapacks to world {}", this.resultFolder, ioexception);
            SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
            this.popScreen();
            return false;
         }
      }

      return true;
   }

   @Nullable
   public static Path createTempDataPackDirFromExistingWorld(Path p_100907_, Minecraft p_100908_) {
      MutableObject<Path> mutableobject = new MutableObject<>();

      try {
         Stream<Path> stream = Files.walk(p_100907_);

         try {
            stream.filter((p_205437_) -> {
               return !p_205437_.equals(p_100907_);
            }).forEach((p_205441_) -> {
               Path path = mutableobject.getValue();
               if (path == null) {
                  try {
                     path = Files.createTempDirectory("mcworld-");
                  } catch (IOException ioexception1) {
                     LOGGER.warn("Failed to create temporary dir");
                     throw new CreateWorldScreen.OperationFailedException(ioexception1);
                  }

                  mutableobject.setValue(path);
               }

               copyBetweenDirs(p_100907_, path, p_205441_);
            });
         } catch (Throwable throwable1) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (CreateWorldScreen.OperationFailedException | IOException ioexception) {
         LOGGER.warn("Failed to copy datapacks from world {}", p_100907_, ioexception);
         SystemToast.onPackCopyFailure(p_100908_, p_100907_.toString());
         return null;
      }

      return mutableobject.getValue();
   }

   @Nullable
   private Pair<File, PackRepository> getDataPackSelectionSettings() {
      Path path = this.getTempDataPackDir();
      if (path != null) {
         File file1 = path.toFile();
         if (this.tempDataPackRepository == null) {
            this.tempDataPackRepository = new PackRepository(PackType.SERVER_DATA, new ServerPacksSource(), new FolderRepositorySource(file1, PackSource.DEFAULT));
            net.minecraftforge.resource.ResourcePackLoader.loadResourcePacks(this.tempDataPackRepository, net.minecraftforge.server.ServerLifecycleHooks::buildPackFinder);
            this.tempDataPackRepository.reload();
         }

         this.tempDataPackRepository.setSelected(this.dataPacks.getEnabled());
         return Pair.of(file1, this.tempDataPackRepository);
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class OperationFailedException extends RuntimeException {
      public OperationFailedException(Throwable p_101023_) {
         super(p_101023_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum SelectedGameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      final String name;
      final GameType gameType;
      private final Component displayName;

      private SelectedGameMode(String p_101035_, GameType p_101036_) {
         this.name = p_101035_;
         this.gameType = p_101036_;
         this.displayName = new TranslatableComponent("selectWorld.gameMode." + p_101035_);
      }

      public Component getDisplayName() {
         return this.displayName;
      }
   }
}
