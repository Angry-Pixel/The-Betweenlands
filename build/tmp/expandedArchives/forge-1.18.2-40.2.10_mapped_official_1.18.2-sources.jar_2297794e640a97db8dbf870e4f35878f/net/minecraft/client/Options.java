package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Options {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new Gson();
   private static final TypeToken<List<String>> RESOURCE_PACK_TYPE = new TypeToken<List<String>>() {
   };
   public static final int RENDER_DISTANCE_TINY = 2;
   public static final int RENDER_DISTANCE_SHORT = 4;
   public static final int RENDER_DISTANCE_NORMAL = 8;
   public static final int RENDER_DISTANCE_FAR = 12;
   public static final int RENDER_DISTANCE_REALLY_FAR = 16;
   public static final int RENDER_DISTANCE_EXTREME = 32;
   private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
   private static final float DEFAULT_VOLUME = 1.0F;
   public static final String DEFAULT_SOUND_DEVICE = "";
   public boolean darkMojangStudiosBackground;
   public boolean hideLightningFlashes;
   public double sensitivity = 0.5D;
   public int renderDistance;
   public int simulationDistance;
   private int serverRenderDistance = 0;
   public float entityDistanceScaling = 1.0F;
   public int framerateLimit = 120;
   public CloudStatus renderClouds = CloudStatus.FANCY;
   public GraphicsStatus graphicsMode = GraphicsStatus.FANCY;
   public AmbientOcclusionStatus ambientOcclusion = AmbientOcclusionStatus.MAX;
   public PrioritizeChunkUpdates prioritizeChunkUpdates = PrioritizeChunkUpdates.NONE;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public ChatVisiblity chatVisibility = ChatVisiblity.FULL;
   public double chatOpacity = 1.0D;
   public double chatLineSpacing;
   public double textBackgroundOpacity = 0.5D;
   @Nullable
   public String fullscreenVideoModeString;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<PlayerModelPart> modelParts = EnumSet.allOf(PlayerModelPart.class);
   public HumanoidArm mainHand = HumanoidArm.RIGHT;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public double chatScale = 1.0D;
   public double chatWidth = 1.0D;
   public double chatHeightUnfocused = (double)0.44366196F;
   public double chatHeightFocused = 1.0D;
   public double chatDelay;
   public int mipmapLevels = 4;
   private final Object2FloatMap<SoundSource> sourceVolumes = Util.make(new Object2FloatOpenHashMap<>(), (p_168434_) -> {
      p_168434_.defaultReturnValue(1.0F);
   });
   public boolean useNativeTransport = true;
   public AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.CROSSHAIR;
   public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
   public boolean joinedFirstServer = false;
   public boolean hideBundleTutorial = false;
   public int biomeBlendRadius = 2;
   public double mouseWheelSensitivity = 1.0D;
   public boolean rawMouseInput = true;
   public int glDebugVerbosity = 1;
   public boolean autoJump = true;
   public boolean autoSuggestions = true;
   public boolean chatColors = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public boolean enableVsync = true;
   public boolean entityShadows = true;
   public boolean forceUnicodeFont;
   public boolean invertYMouse;
   public boolean discreteMouseScroll;
   public boolean realmsNotifications = true;
   public boolean allowServerListing = true;
   public boolean reducedDebugInfo;
   public boolean showSubtitles;
   public boolean backgroundForChatOnly = true;
   public boolean touchscreen;
   public boolean fullscreen;
   public boolean bobView = true;
   public boolean toggleCrouch;
   public boolean toggleSprint;
   public boolean skipMultiplayerWarning;
   public boolean skipRealms32bitWarning;
   public boolean hideMatchedNames = true;
   public boolean showAutosaveIndicator = true;
   public final KeyMapping keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
   public final KeyMapping keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
   public final KeyMapping keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
   public final KeyMapping keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
   public final KeyMapping keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
   public final KeyMapping keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", () -> {
      return this.toggleCrouch;
   });
   public final KeyMapping keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", () -> {
      return this.toggleSprint;
   });
   public final KeyMapping keyInventory = new KeyMapping("key.inventory", 69, "key.categories.inventory");
   public final KeyMapping keySwapOffhand = new KeyMapping("key.swapOffhand", 70, "key.categories.inventory");
   public final KeyMapping keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
   public final KeyMapping keyUse = new KeyMapping("key.use", InputConstants.Type.MOUSE, 1, "key.categories.gameplay");
   public final KeyMapping keyAttack = new KeyMapping("key.attack", InputConstants.Type.MOUSE, 0, "key.categories.gameplay");
   public final KeyMapping keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, "key.categories.gameplay");
   public final KeyMapping keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
   public final KeyMapping keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
   public final KeyMapping keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
   public final KeyMapping keySocialInteractions = new KeyMapping("key.socialInteractions", 80, "key.categories.multiplayer");
   public final KeyMapping keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
   public final KeyMapping keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
   public final KeyMapping keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
   public final KeyMapping keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
   public final KeyMapping keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
   public final KeyMapping keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
   public final KeyMapping[] keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"), new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"), new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"), new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"), new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"), new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"), new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"), new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"), new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")};
   public final KeyMapping keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
   public final KeyMapping keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
   public KeyMapping[] keyMappings = ArrayUtils.addAll((KeyMapping[])(new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements}), (KeyMapping[])this.keyHotbarSlots);
   protected Minecraft minecraft;
   private final File optionsFile;
   public Difficulty difficulty = Difficulty.NORMAL;
   public boolean hideGui;
   private CameraType cameraType = CameraType.FIRST_PERSON;
   public boolean renderDebug;
   public boolean renderDebugCharts;
   public boolean renderFpsChart;
   public String lastMpIp = "";
   public boolean smoothCamera;
   public double fov = 70.0D;
   public float screenEffectScale = 1.0F;
   public float fovEffectScale = 1.0F;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles = ParticleStatus.ALL;
   public NarratorStatus narratorStatus = NarratorStatus.OFF;
   public String languageCode = "en_us";
   public String soundDevice = "";
   public boolean syncWrites;

   public Options(Minecraft p_92138_, File p_92139_) {
      setForgeKeybindProperties();
      this.minecraft = p_92138_;
      this.optionsFile = new File(p_92139_, "options.txt");
      if (p_92138_.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         Option.RENDER_DISTANCE.setMaxValue(32.0F);
         Option.SIMULATION_DISTANCE.setMaxValue(32.0F);
      } else {
         Option.RENDER_DISTANCE.setMaxValue(16.0F);
         Option.SIMULATION_DISTANCE.setMaxValue(16.0F);
      }

      this.renderDistance = p_92138_.is64Bit() ? 12 : 8;
      this.simulationDistance = p_92138_.is64Bit() ? 12 : 8;
      this.gamma = 0.5D;
      this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
      this.load();
   }

   public float getBackgroundOpacity(float p_92142_) {
      return this.backgroundForChatOnly ? p_92142_ : (float)this.textBackgroundOpacity;
   }

   public int getBackgroundColor(float p_92171_) {
      return (int)(this.getBackgroundOpacity(p_92171_) * 255.0F) << 24 & -16777216;
   }

   public int getBackgroundColor(int p_92144_) {
      return this.backgroundForChatOnly ? p_92144_ : (int)(this.textBackgroundOpacity * 255.0D) << 24 & -16777216;
   }

   public void setKey(KeyMapping p_92160_, InputConstants.Key p_92161_) {
      p_92160_.setKey(p_92161_);
      this.save();
   }

   private void processOptions(Options.FieldAccess p_168428_) {
      this.autoJump = p_168428_.process("autoJump", this.autoJump);
      this.autoSuggestions = p_168428_.process("autoSuggestions", this.autoSuggestions);
      this.chatColors = p_168428_.process("chatColors", this.chatColors);
      this.chatLinks = p_168428_.process("chatLinks", this.chatLinks);
      this.chatLinksPrompt = p_168428_.process("chatLinksPrompt", this.chatLinksPrompt);
      this.enableVsync = p_168428_.process("enableVsync", this.enableVsync);
      this.entityShadows = p_168428_.process("entityShadows", this.entityShadows);
      this.forceUnicodeFont = p_168428_.process("forceUnicodeFont", this.forceUnicodeFont);
      this.discreteMouseScroll = p_168428_.process("discrete_mouse_scroll", this.discreteMouseScroll);
      this.invertYMouse = p_168428_.process("invertYMouse", this.invertYMouse);
      this.realmsNotifications = p_168428_.process("realmsNotifications", this.realmsNotifications);
      this.reducedDebugInfo = p_168428_.process("reducedDebugInfo", this.reducedDebugInfo);
      this.showSubtitles = p_168428_.process("showSubtitles", this.showSubtitles);
      this.touchscreen = p_168428_.process("touchscreen", this.touchscreen);
      this.fullscreen = p_168428_.process("fullscreen", this.fullscreen);
      this.bobView = p_168428_.process("bobView", this.bobView);
      this.toggleCrouch = p_168428_.process("toggleCrouch", this.toggleCrouch);
      this.toggleSprint = p_168428_.process("toggleSprint", this.toggleSprint);
      this.darkMojangStudiosBackground = p_168428_.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
      this.hideLightningFlashes = p_168428_.process("hideLightningFlashes", this.hideLightningFlashes);
      this.sensitivity = p_168428_.process("mouseSensitivity", this.sensitivity);
      this.fov = p_168428_.process("fov", (this.fov - 70.0D) / 40.0D) * 40.0D + 70.0D;
      this.screenEffectScale = p_168428_.process("screenEffectScale", this.screenEffectScale);
      this.fovEffectScale = p_168428_.process("fovEffectScale", this.fovEffectScale);
      this.gamma = p_168428_.process("gamma", this.gamma);
      this.renderDistance = (int)Mth.clamp((double)p_168428_.process("renderDistance", this.renderDistance), Option.RENDER_DISTANCE.getMinValue(), Option.RENDER_DISTANCE.getMaxValue());
      this.simulationDistance = (int)Mth.clamp((double)p_168428_.process("simulationDistance", this.simulationDistance), Option.SIMULATION_DISTANCE.getMinValue(), Option.SIMULATION_DISTANCE.getMaxValue());
      this.entityDistanceScaling = p_168428_.process("entityDistanceScaling", this.entityDistanceScaling);
      this.guiScale = p_168428_.process("guiScale", this.guiScale);
      this.particles = p_168428_.process("particles", this.particles, ParticleStatus::byId, ParticleStatus::getId);
      this.framerateLimit = p_168428_.process("maxFps", this.framerateLimit);
      this.difficulty = p_168428_.process("difficulty", this.difficulty, Difficulty::byId, Difficulty::getId);
      this.graphicsMode = p_168428_.process("graphicsMode", this.graphicsMode, GraphicsStatus::byId, GraphicsStatus::getId);
      this.ambientOcclusion = p_168428_.process("ao", this.ambientOcclusion, Options::readAmbientOcclusion, (p_168424_) -> {
         return Integer.toString(p_168424_.getId());
      });
      this.prioritizeChunkUpdates = p_168428_.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates, PrioritizeChunkUpdates::byId, PrioritizeChunkUpdates::getId);
      this.biomeBlendRadius = p_168428_.process("biomeBlendRadius", this.biomeBlendRadius);
      this.renderClouds = p_168428_.process("renderClouds", this.renderClouds, Options::readCloudStatus, Options::writeCloudStatus);
      this.resourcePacks = p_168428_.process("resourcePacks", this.resourcePacks, Options::readPackList, GSON::toJson);
      this.incompatibleResourcePacks = p_168428_.process("incompatibleResourcePacks", this.incompatibleResourcePacks, Options::readPackList, GSON::toJson);
      this.lastMpIp = p_168428_.process("lastServer", this.lastMpIp);
      this.languageCode = p_168428_.process("lang", this.languageCode);
      this.soundDevice = p_168428_.process("soundDevice", this.soundDevice);
      this.chatVisibility = p_168428_.process("chatVisibility", this.chatVisibility, ChatVisiblity::byId, ChatVisiblity::getId);
      this.chatOpacity = p_168428_.process("chatOpacity", this.chatOpacity);
      this.chatLineSpacing = p_168428_.process("chatLineSpacing", this.chatLineSpacing);
      this.textBackgroundOpacity = p_168428_.process("textBackgroundOpacity", this.textBackgroundOpacity);
      this.backgroundForChatOnly = p_168428_.process("backgroundForChatOnly", this.backgroundForChatOnly);
      this.hideServerAddress = p_168428_.process("hideServerAddress", this.hideServerAddress);
      this.advancedItemTooltips = p_168428_.process("advancedItemTooltips", this.advancedItemTooltips);
      this.pauseOnLostFocus = p_168428_.process("pauseOnLostFocus", this.pauseOnLostFocus);
      this.overrideWidth = p_168428_.process("overrideWidth", this.overrideWidth);
      this.overrideHeight = p_168428_.process("overrideHeight", this.overrideHeight);
      this.heldItemTooltips = p_168428_.process("heldItemTooltips", this.heldItemTooltips);
      this.chatHeightFocused = p_168428_.process("chatHeightFocused", this.chatHeightFocused);
      this.chatDelay = p_168428_.process("chatDelay", this.chatDelay);
      this.chatHeightUnfocused = p_168428_.process("chatHeightUnfocused", this.chatHeightUnfocused);
      this.chatScale = p_168428_.process("chatScale", this.chatScale);
      this.chatWidth = p_168428_.process("chatWidth", this.chatWidth);
      this.mipmapLevels = p_168428_.process("mipmapLevels", this.mipmapLevels);
      this.useNativeTransport = p_168428_.process("useNativeTransport", this.useNativeTransport);
      this.mainHand = p_168428_.process("mainHand", this.mainHand, Options::readMainHand, Options::writeMainHand);
      this.attackIndicator = p_168428_.process("attackIndicator", this.attackIndicator, AttackIndicatorStatus::byId, AttackIndicatorStatus::getId);
      this.narratorStatus = p_168428_.process("narrator", this.narratorStatus, NarratorStatus::byId, NarratorStatus::getId);
      this.tutorialStep = p_168428_.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
      this.mouseWheelSensitivity = p_168428_.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
      this.rawMouseInput = p_168428_.process("rawMouseInput", this.rawMouseInput);
      this.glDebugVerbosity = p_168428_.process("glDebugVerbosity", this.glDebugVerbosity);
      this.skipMultiplayerWarning = p_168428_.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
      this.skipRealms32bitWarning = p_168428_.process("skipRealms32bitWarning", this.skipRealms32bitWarning);
      this.hideMatchedNames = p_168428_.process("hideMatchedNames", this.hideMatchedNames);
      this.joinedFirstServer = p_168428_.process("joinedFirstServer", this.joinedFirstServer);
      this.hideBundleTutorial = p_168428_.process("hideBundleTutorial", this.hideBundleTutorial);
      this.syncWrites = p_168428_.process("syncChunkWrites", this.syncWrites);
      this.showAutosaveIndicator = p_168428_.process("showAutosaveIndicator", this.showAutosaveIndicator);
      this.allowServerListing = p_168428_.process("allowServerListing", this.allowServerListing);

      for(KeyMapping keymapping : this.keyMappings) {
         String s = keymapping.saveString() + (keymapping.getKeyModifier() != net.minecraftforge.client.settings.KeyModifier.NONE ? ":" + keymapping.getKeyModifier() : "");
         String s1 = p_168428_.process("key_" + keymapping.getName(), s);
         if (!s.equals(s1)) {
            if (s1.indexOf(':') != -1) {
               String[] pts = s1.split(":");
               keymapping.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.valueFromString(pts[1]), InputConstants.getKey(pts[0]));
            } else
               keymapping.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, InputConstants.getKey(s1));
         }
      }

      for(SoundSource soundsource : SoundSource.values()) {
         this.sourceVolumes.computeFloat(soundsource, (p_168431_, p_168432_) -> {
            return p_168428_.process("soundCategory_" + p_168431_.getName(), p_168432_ != null ? p_168432_ : 1.0F);
         });
      }

      for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
         boolean flag = this.modelParts.contains(playermodelpart);
         boolean flag1 = p_168428_.process("modelPart_" + playermodelpart.getId(), flag);
         if (flag1 != flag) {
            this.setModelPart(playermodelpart, flag1);
         }
      }

   }

   public void load() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.sourceVolumes.clear();
         CompoundTag compoundtag = new CompoundTag();
         BufferedReader bufferedreader = Files.newReader(this.optionsFile, Charsets.UTF_8);

         try {
            bufferedreader.lines().forEach((p_168439_) -> {
               try {
                  Iterator<String> iterator = OPTION_SPLITTER.split(p_168439_).iterator();
                  compoundtag.putString(iterator.next(), iterator.next());
               } catch (Exception exception1) {
                  LOGGER.warn("Skipping bad option: {}", (Object)p_168439_);
               }

            });
         } catch (Throwable throwable1) {
            if (bufferedreader != null) {
               try {
                  bufferedreader.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (bufferedreader != null) {
            bufferedreader.close();
         }

         final CompoundTag compoundtag1 = this.dataFix(compoundtag);
         if (!compoundtag1.contains("graphicsMode") && compoundtag1.contains("fancyGraphics")) {
            if (isTrue(compoundtag1.getString("fancyGraphics"))) {
               this.graphicsMode = GraphicsStatus.FANCY;
            } else {
               this.graphicsMode = GraphicsStatus.FAST;
            }
         }

         this.processOptions(new Options.FieldAccess() {
            @Nullable
            private String getValueOrNull(String p_168459_) {
               return compoundtag1.contains(p_168459_) ? compoundtag1.getString(p_168459_) : null;
            }

            public int process(String p_168467_, int p_168468_) {
               String s = this.getValueOrNull(p_168467_);
               if (s != null) {
                  try {
                     return Integer.parseInt(s);
                  } catch (NumberFormatException numberformatexception) {
                     Options.LOGGER.warn("Invalid integer value for option {} = {}", p_168467_, s, numberformatexception);
                  }
               }

               return p_168468_;
            }

            public boolean process(String p_168483_, boolean p_168484_) {
               String s = this.getValueOrNull(p_168483_);
               return s != null ? Options.isTrue(s) : p_168484_;
            }

            public String process(String p_168480_, String p_168481_) {
               return MoreObjects.firstNonNull(this.getValueOrNull(p_168480_), p_168481_);
            }

            public double process(String p_168461_, double p_168462_) {
               String s = this.getValueOrNull(p_168461_);
               if (s != null) {
                  if (Options.isTrue(s)) {
                     return 1.0D;
                  }

                  if (Options.isFalse(s)) {
                     return 0.0D;
                  }

                  try {
                     return Double.parseDouble(s);
                  } catch (NumberFormatException numberformatexception) {
                     Options.LOGGER.warn("Invalid floating point value for option {} = {}", p_168461_, s, numberformatexception);
                  }
               }

               return p_168462_;
            }

            public float process(String p_168464_, float p_168465_) {
               String s = this.getValueOrNull(p_168464_);
               if (s != null) {
                  if (Options.isTrue(s)) {
                     return 1.0F;
                  }

                  if (Options.isFalse(s)) {
                     return 0.0F;
                  }

                  try {
                     return Float.parseFloat(s);
                  } catch (NumberFormatException numberformatexception) {
                     Options.LOGGER.warn("Invalid floating point value for option {} = {}", p_168464_, s, numberformatexception);
                  }
               }

               return p_168465_;
            }

            public <T> T process(String p_168470_, T p_168471_, Function<String, T> p_168472_, Function<T, String> p_168473_) {
               String s = this.getValueOrNull(p_168470_);
               return (T)(s == null ? p_168471_ : p_168472_.apply(s));
            }

            public <T> T process(String p_168475_, T p_168476_, IntFunction<T> p_168477_, ToIntFunction<T> p_168478_) {
               String s = this.getValueOrNull(p_168475_);
               if (s != null) {
                  try {
                     return p_168477_.apply(Integer.parseInt(s));
                  } catch (Exception exception1) {
                     Options.LOGGER.warn("Invalid integer value for option {} = {}", p_168475_, s, exception1);
                  }
               }

               return p_168476_;
            }
         });
         if (compoundtag1.contains("fullscreenResolution")) {
            this.fullscreenVideoModeString = compoundtag1.getString("fullscreenResolution");
         }

         if (this.minecraft.getWindow() != null) {
            this.minecraft.getWindow().setFramerateLimit(this.framerateLimit);
         }

         KeyMapping.resetMapping();
      } catch (Exception exception) {
         LOGGER.error("Failed to load options", (Throwable)exception);
      }

   }

   static boolean isTrue(String p_168436_) {
      return "true".equals(p_168436_);
   }

   static boolean isFalse(String p_168441_) {
      return "false".equals(p_168441_);
   }

   private CompoundTag dataFix(CompoundTag p_92165_) {
      int i = 0;

      try {
         i = Integer.parseInt(p_92165_.getString("version"));
      } catch (RuntimeException runtimeexception) {
      }

      return NbtUtils.update(this.minecraft.getFixerUpper(), DataFixTypes.OPTIONS, p_92165_, i);
   }

   public void save() {
      if (net.minecraftforge.client.loading.ClientModLoader.isLoading()) return; //Don't save settings before mods add keybindigns and the like to prevent them from being deleted.
      try {
         final PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

         try {
            printwriter.println("version:" + SharedConstants.getCurrentVersion().getWorldVersion());
            this.processOptions(new Options.FieldAccess() {
               public void writePrefix(String p_168491_) {
                  printwriter.print(p_168491_);
                  printwriter.print(':');
               }

               public int process(String p_168499_, int p_168500_) {
                  this.writePrefix(p_168499_);
                  printwriter.println(p_168500_);
                  return p_168500_;
               }

               public boolean process(String p_168515_, boolean p_168516_) {
                  this.writePrefix(p_168515_);
                  printwriter.println(p_168516_);
                  return p_168516_;
               }

               public String process(String p_168512_, String p_168513_) {
                  this.writePrefix(p_168512_);
                  printwriter.println(p_168513_);
                  return p_168513_;
               }

               public double process(String p_168493_, double p_168494_) {
                  this.writePrefix(p_168493_);
                  printwriter.println(p_168494_);
                  return p_168494_;
               }

               public float process(String p_168496_, float p_168497_) {
                  this.writePrefix(p_168496_);
                  printwriter.println(p_168497_);
                  return p_168497_;
               }

               public <T> T process(String p_168502_, T p_168503_, Function<String, T> p_168504_, Function<T, String> p_168505_) {
                  this.writePrefix(p_168502_);
                  printwriter.println(p_168505_.apply(p_168503_));
                  return p_168503_;
               }

               public <T> T process(String p_168507_, T p_168508_, IntFunction<T> p_168509_, ToIntFunction<T> p_168510_) {
                  this.writePrefix(p_168507_);
                  printwriter.println(p_168510_.applyAsInt(p_168508_));
                  return p_168508_;
               }
            });
            if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
               printwriter.println("fullscreenResolution:" + this.minecraft.getWindow().getPreferredFullscreenVideoMode().get().write());
            }
         } catch (Throwable throwable1) {
            try {
               printwriter.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }

            throw throwable1;
         }

         printwriter.close();
      } catch (Exception exception) {
         LOGGER.error("Failed to save options", (Throwable)exception);
      }

      this.broadcastOptions();
   }

   public float getSoundSourceVolume(SoundSource p_92148_) {
      return this.sourceVolumes.getFloat(p_92148_);
   }

   public void setSoundCategoryVolume(SoundSource p_92150_, float p_92151_) {
      this.sourceVolumes.put(p_92150_, p_92151_);
      this.minecraft.getSoundManager().updateSourceVolume(p_92150_, p_92151_);
   }

   public void broadcastOptions() {
      if (this.minecraft.player != null) {
         int i = 0;

         for(PlayerModelPart playermodelpart : this.modelParts) {
            i |= playermodelpart.getMask();
         }

         this.minecraft.player.connection.send(new ServerboundClientInformationPacket(this.languageCode, this.renderDistance, this.chatVisibility, this.chatColors, i, this.mainHand, this.minecraft.isTextFilteringEnabled(), this.allowServerListing));
      }

   }

   private void setModelPart(PlayerModelPart p_92155_, boolean p_92156_) {
      if (p_92156_) {
         this.modelParts.add(p_92155_);
      } else {
         this.modelParts.remove(p_92155_);
      }

   }

   public boolean isModelPartEnabled(PlayerModelPart p_168417_) {
      return this.modelParts.contains(p_168417_);
   }

   public void toggleModelPart(PlayerModelPart p_168419_, boolean p_168420_) {
      this.setModelPart(p_168419_, p_168420_);
      this.broadcastOptions();
   }

   public CloudStatus getCloudsType() {
      return this.getEffectiveRenderDistance() >= 4 ? this.renderClouds : CloudStatus.OFF;
   }

   public boolean useNativeTransport() {
      return this.useNativeTransport;
   }

   public void loadSelectedResourcePacks(PackRepository p_92146_) {
      Set<String> set = Sets.newLinkedHashSet();
      Iterator<String> iterator = this.resourcePacks.iterator();

      while(iterator.hasNext()) {
         String s = iterator.next();
         Pack pack = p_92146_.getPack(s);
         if (pack == null && !s.startsWith("file/")) {
            pack = p_92146_.getPack("file/" + s);
         }

         if (pack == null) {
            LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)s);
            iterator.remove();
         } else if (!pack.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(s)) {
            LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)s);
            iterator.remove();
         } else if (pack.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(s)) {
            LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)s);
            this.incompatibleResourcePacks.remove(s);
         } else {
            set.add(pack.getId());
         }
      }

      p_92146_.setSelected(set);
   }

   private void setForgeKeybindProperties() {
      net.minecraftforge.client.settings.KeyConflictContext inGame = net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
      keyUp.setKeyConflictContext(inGame);
      keyLeft.setKeyConflictContext(inGame);
      keyDown.setKeyConflictContext(inGame);
      keyRight.setKeyConflictContext(inGame);
      keyJump.setKeyConflictContext(inGame);
      keyShift.setKeyConflictContext(inGame);
      keySprint.setKeyConflictContext(inGame);
      keyAttack.setKeyConflictContext(inGame);
      keyChat.setKeyConflictContext(inGame);
      keyPlayerList.setKeyConflictContext(inGame);
      keyCommand.setKeyConflictContext(inGame);
      keyTogglePerspective.setKeyConflictContext(inGame);
      keySmoothCamera.setKeyConflictContext(inGame);
   }

   public CameraType getCameraType() {
      return this.cameraType;
   }

   public void setCameraType(CameraType p_92158_) {
      this.cameraType = p_92158_;
   }

   private static List<String> readPackList(String p_168443_) {
      List<String> list = GsonHelper.fromJson(GSON, p_168443_, RESOURCE_PACK_TYPE);
      return (List<String>)(list != null ? list : Lists.newArrayList());
   }

   private static CloudStatus readCloudStatus(String p_168445_) {
      switch(p_168445_) {
      case "true":
         return CloudStatus.FANCY;
      case "fast":
         return CloudStatus.FAST;
      case "false":
      default:
         return CloudStatus.OFF;
      }
   }

   private static String writeCloudStatus(CloudStatus p_168426_) {
      switch(p_168426_) {
      case FANCY:
         return "true";
      case FAST:
         return "fast";
      case OFF:
      default:
         return "false";
      }
   }

   private static AmbientOcclusionStatus readAmbientOcclusion(String p_168447_) {
      if (isTrue(p_168447_)) {
         return AmbientOcclusionStatus.MAX;
      } else {
         return isFalse(p_168447_) ? AmbientOcclusionStatus.OFF : AmbientOcclusionStatus.byId(Integer.parseInt(p_168447_));
      }
   }

   private static HumanoidArm readMainHand(String p_168449_) {
      return "left".equals(p_168449_) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
   }

   private static String writeMainHand(HumanoidArm p_168415_) {
      return p_168415_ == HumanoidArm.LEFT ? "left" : "right";
   }

   public File getFile() {
      return this.optionsFile;
   }

   public String dumpOptionsForReport() {
      ImmutableList<Pair<String, String>> immutablelist = ImmutableList.<Pair<String, String>>builder().add(Pair.of("ao", String.valueOf((Object)this.ambientOcclusion))).add(Pair.of("biomeBlendRadius", String.valueOf(this.biomeBlendRadius))).add(Pair.of("enableVsync", String.valueOf(this.enableVsync))).add(Pair.of("entityDistanceScaling", String.valueOf(this.entityDistanceScaling))).add(Pair.of("entityShadows", String.valueOf(this.entityShadows))).add(Pair.of("forceUnicodeFont", String.valueOf(this.forceUnicodeFont))).add(Pair.of("fov", String.valueOf(this.fov))).add(Pair.of("fovEffectScale", String.valueOf(this.fovEffectScale))).add(Pair.of("prioritizeChunkUpdates", String.valueOf((Object)this.prioritizeChunkUpdates))).add(Pair.of("fullscreen", String.valueOf(this.fullscreen))).add(Pair.of("fullscreenResolution", String.valueOf((Object)this.fullscreenVideoModeString))).add(Pair.of("gamma", String.valueOf(this.gamma))).add(Pair.of("glDebugVerbosity", String.valueOf(this.glDebugVerbosity))).add(Pair.of("graphicsMode", String.valueOf((Object)this.graphicsMode))).add(Pair.of("guiScale", String.valueOf(this.guiScale))).add(Pair.of("maxFps", String.valueOf(this.framerateLimit))).add(Pair.of("mipmapLevels", String.valueOf(this.mipmapLevels))).add(Pair.of("narrator", String.valueOf((Object)this.narratorStatus))).add(Pair.of("overrideHeight", String.valueOf(this.overrideHeight))).add(Pair.of("overrideWidth", String.valueOf(this.overrideWidth))).add(Pair.of("particles", String.valueOf((Object)this.particles))).add(Pair.of("reducedDebugInfo", String.valueOf(this.reducedDebugInfo))).add(Pair.of("renderClouds", String.valueOf((Object)this.renderClouds))).add(Pair.of("renderDistance", String.valueOf(this.renderDistance))).add(Pair.of("simulationDistance", String.valueOf(this.simulationDistance))).add(Pair.of("resourcePacks", String.valueOf((Object)this.resourcePacks))).add(Pair.of("screenEffectScale", String.valueOf(this.screenEffectScale))).add(Pair.of("syncChunkWrites", String.valueOf(this.syncWrites))).add(Pair.of("useNativeTransport", String.valueOf(this.useNativeTransport))).add(Pair.of("soundDevice", String.valueOf((Object)this.soundDevice))).build();
      return immutablelist.stream().map((p_168422_) -> {
         return (String)p_168422_.getFirst() + ": " + (String)p_168422_.getSecond();
      }).collect(Collectors.joining(System.lineSeparator()));
   }

   public void setServerRenderDistance(int p_193771_) {
      this.serverRenderDistance = p_193771_;
   }

   public int getEffectiveRenderDistance() {
      return this.serverRenderDistance > 0 ? Math.min(this.renderDistance, this.serverRenderDistance) : this.renderDistance;
   }

   @OnlyIn(Dist.CLIENT)
   interface FieldAccess {
      int process(String p_168523_, int p_168524_);

      boolean process(String p_168535_, boolean p_168536_);

      String process(String p_168533_, String p_168534_);

      double process(String p_168519_, double p_168520_);

      float process(String p_168521_, float p_168522_);

      <T> T process(String p_168525_, T p_168526_, Function<String, T> p_168527_, Function<T, String> p_168528_);

      <T> T process(String p_168529_, T p_168530_, IntFunction<T> p_168531_, ToIntFunction<T> p_168532_);
   }
}
