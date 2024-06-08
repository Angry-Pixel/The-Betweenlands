package net.minecraft.server.chase;

import com.google.common.base.Charsets;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ChaseClient {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int RECONNECT_INTERVAL_SECONDS = 5;
   private final String serverHost;
   private final int serverPort;
   private final MinecraftServer server;
   private volatile boolean wantsToRun;
   @Nullable
   private Socket socket;
   @Nullable
   private Thread thread;

   public ChaseClient(String p_195990_, int p_195991_, MinecraftServer p_195992_) {
      this.serverHost = p_195990_;
      this.serverPort = p_195991_;
      this.server = p_195992_;
   }

   public void start() {
      if (this.thread != null && this.thread.isAlive()) {
         LOGGER.warn("Remote control client was asked to start, but it is already running. Will ignore.");
      }

      this.wantsToRun = true;
      this.thread = new Thread(this::run, "chase-client");
      this.thread.setDaemon(true);
      this.thread.start();
   }

   public void stop() {
      this.wantsToRun = false;
      IOUtils.closeQuietly(this.socket);
      this.socket = null;
      this.thread = null;
   }

   public void run() {
      String s = this.serverHost + ":" + this.serverPort;

      while(this.wantsToRun) {
         try {
            LOGGER.info("Connecting to remote control server {}", (Object)s);
            this.socket = new Socket(this.serverHost, this.serverPort);
            LOGGER.info("Connected to remote control server! Will continuously execute the command broadcasted by that server.");

            try {
               BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), Charsets.US_ASCII));

               try {
                  while(this.wantsToRun) {
                     String s1 = bufferedreader.readLine();
                     if (s1 == null) {
                        LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", s, 5);
                        break;
                     }

                     this.handleMessage(s1);
                  }
               } catch (Throwable throwable1) {
                  try {
                     bufferedreader.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }

                  throw throwable1;
               }

               bufferedreader.close();
            } catch (IOException ioexception) {
               LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", s, 5);
            }
         } catch (IOException ioexception1) {
            LOGGER.warn("Failed to connect to remote control server {}. Will retry in {}s.", s, 5);
         }

         if (this.wantsToRun) {
            try {
               Thread.sleep(5000L);
            } catch (InterruptedException interruptedexception) {
            }
         }
      }

   }

   private void handleMessage(String p_195995_) {
      try {
         Scanner scanner = new Scanner(new StringReader(p_195995_));

         try {
            scanner.useLocale(Locale.ROOT);
            String s = scanner.next();
            if ("t".equals(s)) {
               this.handleTeleport(scanner);
            } else {
               LOGGER.warn("Unknown message type '{}'", (Object)s);
            }
         } catch (Throwable throwable1) {
            try {
               scanner.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }

            throw throwable1;
         }

         scanner.close();
      } catch (NoSuchElementException nosuchelementexception) {
         LOGGER.warn("Could not parse message '{}', ignoring", (Object)p_195995_);
      }

   }

   private void handleTeleport(Scanner p_195997_) {
      this.parseTarget(p_195997_).ifPresent((p_195999_) -> {
         this.executeCommand(String.format(Locale.ROOT, "/execute in %s run tp @s %.3f %.3f %.3f %.3f %.3f", p_195999_.level.location(), p_195999_.pos.x, p_195999_.pos.y, p_195999_.pos.z, p_195999_.rot.y, p_195999_.rot.x));
      });
   }

   private Optional<ChaseClient.TeleportTarget> parseTarget(Scanner p_196004_) {
      ResourceKey<Level> resourcekey = ChaseCommand.DIMENSION_NAMES.get(p_196004_.next());
      if (resourcekey == null) {
         return Optional.empty();
      } else {
         float f = p_196004_.nextFloat();
         float f1 = p_196004_.nextFloat();
         float f2 = p_196004_.nextFloat();
         float f3 = p_196004_.nextFloat();
         float f4 = p_196004_.nextFloat();
         return Optional.of(new ChaseClient.TeleportTarget(resourcekey, new Vec3((double)f, (double)f1, (double)f2), new Vec2(f4, f3)));
      }
   }

   private void executeCommand(String p_196002_) {
      this.server.execute(() -> {
         List<ServerPlayer> list = this.server.getPlayerList().getPlayers();
         if (!list.isEmpty()) {
            ServerPlayer serverplayer = list.get(0);
            ServerLevel serverlevel = this.server.overworld();
            CommandSourceStack commandsourcestack = new CommandSourceStack(serverplayer, Vec3.atLowerCornerOf(serverlevel.getSharedSpawnPos()), Vec2.ZERO, serverlevel, 4, "", TextComponent.EMPTY, this.server, serverplayer);
            Commands commands = this.server.getCommands();
            commands.performCommand(commandsourcestack, p_196002_);
         }
      });
   }

   static record TeleportTarget(ResourceKey<Level> level, Vec3 pos, Vec2 rot) {
   }
}