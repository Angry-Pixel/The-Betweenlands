package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Main {
   static final Logger LOGGER = LogUtils.getLogger();

   @DontObfuscate
   public static void main(String[] p_129642_) {
      SharedConstants.tryDetectVersion();
      OptionParser optionparser = new OptionParser();
      optionparser.allowsUnrecognizedOptions();
      optionparser.accepts("demo");
      optionparser.accepts("disableMultiplayer");
      optionparser.accepts("disableChat");
      optionparser.accepts("fullscreen");
      optionparser.accepts("checkGlErrors");
      OptionSpec<Void> optionspec = optionparser.accepts("jfrProfile");
      OptionSpec<String> optionspec1 = optionparser.accepts("server").withRequiredArg();
      OptionSpec<Integer> optionspec2 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
      OptionSpec<File> optionspec3 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
      OptionSpec<File> optionspec4 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec<File> optionspec5 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec<String> optionspec6 = optionparser.accepts("proxyHost").withRequiredArg();
      OptionSpec<Integer> optionspec7 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
      OptionSpec<String> optionspec8 = optionparser.accepts("proxyUser").withRequiredArg();
      OptionSpec<String> optionspec9 = optionparser.accepts("proxyPass").withRequiredArg();
      OptionSpec<String> optionspec10 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMillis() % 1000L);
      OptionSpec<String> optionspec11 = optionparser.accepts("uuid").withRequiredArg();
      OptionSpec<String> optionspec12 = optionparser.accepts("xuid").withOptionalArg().defaultsTo("");
      OptionSpec<String> optionspec13 = optionparser.accepts("clientId").withOptionalArg().defaultsTo("");
      OptionSpec<String> optionspec14 = optionparser.accepts("accessToken").withRequiredArg().required();
      OptionSpec<String> optionspec15 = optionparser.accepts("version").withRequiredArg().required();
      OptionSpec<Integer> optionspec16 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
      OptionSpec<Integer> optionspec17 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
      OptionSpec<Integer> optionspec18 = optionparser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      OptionSpec<Integer> optionspec19 = optionparser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      OptionSpec<String> optionspec20 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec21 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec22 = optionparser.accepts("assetIndex").withRequiredArg();
      OptionSpec<String> optionspec23 = optionparser.accepts("userType").withRequiredArg().defaultsTo(User.Type.LEGACY.getName());
      OptionSpec<String> optionspec24 = optionparser.accepts("versionType").withRequiredArg().defaultsTo("release");
      OptionSpec<String> optionspec25 = optionparser.nonOptions();
      OptionSet optionset = optionparser.parse(p_129642_);
      List<String> list = optionset.valuesOf(optionspec25);
      if (!list.isEmpty()) {
         System.out.println("Completely ignored arguments: " + list);
      }

      String s = parseArgument(optionset, optionspec6);
      Proxy proxy = Proxy.NO_PROXY;
      if (s != null) {
         try {
            proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, parseArgument(optionset, optionspec7)));
         } catch (Exception exception) {
         }
      }

      final String s1 = parseArgument(optionset, optionspec8);
      final String s2 = parseArgument(optionset, optionspec9);
      if (!proxy.equals(Proxy.NO_PROXY) && stringHasValue(s1) && stringHasValue(s2)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(s1, s2.toCharArray());
            }
         });
      }

      int i = parseArgument(optionset, optionspec16);
      int j = parseArgument(optionset, optionspec17);
      OptionalInt optionalint = ofNullable(parseArgument(optionset, optionspec18));
      OptionalInt optionalint1 = ofNullable(parseArgument(optionset, optionspec19));
      boolean flag = optionset.has("fullscreen");
      boolean flag1 = optionset.has("demo");
      boolean flag2 = optionset.has("disableMultiplayer");
      boolean flag3 = optionset.has("disableChat");
      String s3 = parseArgument(optionset, optionspec15);
      Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap propertymap = GsonHelper.fromJson(gson, parseArgument(optionset, optionspec20), PropertyMap.class);
      PropertyMap propertymap1 = GsonHelper.fromJson(gson, parseArgument(optionset, optionspec21), PropertyMap.class);
      String s4 = parseArgument(optionset, optionspec24);
      File file1 = parseArgument(optionset, optionspec3);
      File file2 = optionset.has(optionspec4) ? parseArgument(optionset, optionspec4) : new File(file1, "assets/");
      File file3 = optionset.has(optionspec5) ? parseArgument(optionset, optionspec5) : new File(file1, "resourcepacks/");
      String s5 = optionset.has(optionspec11) ? optionspec11.value(optionset) : Player.createPlayerUUID(optionspec10.value(optionset)).toString();
      String s6 = optionset.has(optionspec22) ? optionspec22.value(optionset) : null;
      String s7 = optionset.valueOf(optionspec12);
      String s8 = optionset.valueOf(optionspec13);
      String s9 = parseArgument(optionset, optionspec1);
      Integer integer = parseArgument(optionset, optionspec2);
      if (optionset.has(optionspec)) {
         JvmProfiler.INSTANCE.start(Environment.CLIENT);
      }

      CrashReport.preload();
      net.minecraftforge.fml.loading.BackgroundWaiter.runAndTick(()->Bootstrap.bootStrap(), net.minecraftforge.fml.loading.FMLLoader.progressWindowTick);
      Bootstrap.validate();
      Util.startTimerHackThread();
      String s10 = optionspec23.value(optionset);
      User.Type user$type = User.Type.byName(s10);
      if (user$type == null) {
         LOGGER.warn("Unrecognized user type: {}", (Object)s10);
      }

      User user = new User(optionspec10.value(optionset), s5, optionspec14.value(optionset), emptyStringToEmptyOptional(s7), emptyStringToEmptyOptional(s8), user$type);
      GameConfig gameconfig = new GameConfig(new GameConfig.UserData(user, propertymap, propertymap1, proxy), new DisplayData(i, j, optionalint, optionalint1, flag), new GameConfig.FolderData(file1, file3, file2, s6), new GameConfig.GameData(flag1, s3, s4, flag2, flag3), new GameConfig.ServerData(s9, integer));
      Thread thread = new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft minecraft1 = Minecraft.getInstance();
            if (minecraft1 != null) {
               IntegratedServer integratedserver = minecraft1.getSingleplayerServer();
               if (integratedserver != null) {
                  integratedserver.halt(true);
               }

            }
         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(thread);

      final Minecraft minecraft;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         minecraft = new Minecraft(gameconfig);
         RenderSystem.finishInitialization();
      } catch (SilentInitException silentinitexception) {
         LOGGER.warn("Failed to create window: ", (Throwable)silentinitexception);
         return;
      } catch (Throwable throwable1) {
         CrashReport crashreport = CrashReport.forThrowable(throwable1, "Initializing game");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Initialization");
         NativeModuleLister.addCrashSection(crashreportcategory);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, gameconfig.game.launchVersion, (Options)null, crashreport);
         Minecraft.crash(crashreport);
         return;
      }

      Thread thread1;
      if (minecraft.renderOnThread()) {
         thread1 = new Thread("Game thread") {
            public void run() {
               try {
                  RenderSystem.initGameThread(true);
                  minecraft.run();
               } catch (Throwable throwable2) {
                  Main.LOGGER.error("Exception in client thread", throwable2);
               }

            }
         };
         thread1.start();

         while(minecraft.isRunning()) {
         }
      } else {
         thread1 = null;

         try {
            RenderSystem.initGameThread(false);
            minecraft.run();
         } catch (Throwable throwable) {
            LOGGER.error("Unhandled game exception", throwable);
         }
      }

      BufferUploader.reset();

      try {
         minecraft.stop();
         if (thread1 != null) {
            thread1.join();
         }
      } catch (InterruptedException interruptedexception) {
         LOGGER.error("Exception during client thread shutdown", (Throwable)interruptedexception);
      } finally {
         minecraft.destroy();
      }

   }

   private static Optional<String> emptyStringToEmptyOptional(String p_195487_) {
      return p_195487_.isEmpty() ? Optional.empty() : Optional.of(p_195487_);
   }

   private static OptionalInt ofNullable(@Nullable Integer p_129635_) {
      return p_129635_ != null ? OptionalInt.of(p_129635_) : OptionalInt.empty();
   }

   @Nullable
   private static <T> T parseArgument(OptionSet p_129639_, OptionSpec<T> p_129640_) {
      try {
         return p_129639_.valueOf(p_129640_);
      } catch (Throwable throwable) {
         if (p_129640_ instanceof ArgumentAcceptingOptionSpec) {
            ArgumentAcceptingOptionSpec<T> argumentacceptingoptionspec = (ArgumentAcceptingOptionSpec)p_129640_;
            List<T> list = argumentacceptingoptionspec.defaultValues();
            if (!list.isEmpty()) {
               return list.get(0);
            }
         }

         throw throwable;
      }
   }

   private static boolean stringHasValue(@Nullable String p_129637_) {
      return p_129637_ != null && !p_129637_.isEmpty();
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
