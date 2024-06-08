package net.minecraft.server.commands;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.chase.ChaseClient;
import net.minecraft.server.chase.ChaseServer;
import net.minecraft.world.level.Level;

public class ChaseCommand {
   private static final String DEFAULT_CONNECT_HOST = "localhost";
   private static final String DEFAULT_BIND_ADDRESS = "0.0.0.0";
   private static final int DEFAULT_PORT = 10000;
   private static final int BROADCAST_INTERVAL_MS = 100;
   public static BiMap<String, ResourceKey<Level>> DIMENSION_NAMES = ImmutableBiMap.of("o", Level.OVERWORLD, "n", Level.NETHER, "e", Level.END);
   @Nullable
   private static ChaseServer chaseServer;
   @Nullable
   private static ChaseClient chaseClient;

   public static void register(CommandDispatcher<CommandSourceStack> p_196078_) {
      p_196078_.register(Commands.literal("chase").then(Commands.literal("follow").then(Commands.argument("host", StringArgumentType.string()).executes((p_196104_) -> {
         return follow(p_196104_.getSource(), StringArgumentType.getString(p_196104_, "host"), 10000);
      }).then(Commands.argument("port", IntegerArgumentType.integer(1, 65535)).executes((p_196102_) -> {
         return follow(p_196102_.getSource(), StringArgumentType.getString(p_196102_, "host"), IntegerArgumentType.getInteger(p_196102_, "port"));
      }))).executes((p_196100_) -> {
         return follow(p_196100_.getSource(), "localhost", 10000);
      })).then(Commands.literal("lead").then(Commands.argument("bind_address", StringArgumentType.string()).executes((p_196098_) -> {
         return lead(p_196098_.getSource(), StringArgumentType.getString(p_196098_, "bind_address"), 10000);
      }).then(Commands.argument("port", IntegerArgumentType.integer(1024, 65535)).executes((p_196096_) -> {
         return lead(p_196096_.getSource(), StringArgumentType.getString(p_196096_, "bind_address"), IntegerArgumentType.getInteger(p_196096_, "port"));
      }))).executes((p_196088_) -> {
         return lead(p_196088_.getSource(), "0.0.0.0", 10000);
      })).then(Commands.literal("stop").executes((p_196080_) -> {
         return stop(p_196080_.getSource());
      })));
   }

   private static int stop(CommandSourceStack p_196082_) {
      if (chaseClient != null) {
         chaseClient.stop();
         p_196082_.sendSuccess(new TextComponent("You have now stopped chasing"), false);
         chaseClient = null;
      }

      if (chaseServer != null) {
         chaseServer.stop();
         p_196082_.sendSuccess(new TextComponent("You are no longer being chased"), false);
         chaseServer = null;
      }

      return 0;
   }

   private static boolean alreadyRunning(CommandSourceStack p_196090_) {
      if (chaseServer != null) {
         p_196090_.sendFailure(new TextComponent("Chase server is already running. Stop it using /chase stop"));
         return true;
      } else if (chaseClient != null) {
         p_196090_.sendFailure(new TextComponent("You are already chasing someone. Stop it using /chase stop"));
         return true;
      } else {
         return false;
      }
   }

   private static int lead(CommandSourceStack p_196084_, String p_196085_, int p_196086_) {
      if (alreadyRunning(p_196084_)) {
         return 0;
      } else {
         chaseServer = new ChaseServer(p_196085_, p_196086_, p_196084_.getServer().getPlayerList(), 100);

         try {
            chaseServer.start();
            p_196084_.sendSuccess(new TextComponent("Chase server is now running on port " + p_196086_ + ". Clients can follow you using /chase follow <ip> <port>"), false);
         } catch (IOException ioexception) {
            ioexception.printStackTrace();
            p_196084_.sendFailure(new TextComponent("Failed to start chase server on port " + p_196086_));
            chaseServer = null;
         }

         return 0;
      }
   }

   private static int follow(CommandSourceStack p_196092_, String p_196093_, int p_196094_) {
      if (alreadyRunning(p_196092_)) {
         return 0;
      } else {
         chaseClient = new ChaseClient(p_196093_, p_196094_, p_196092_.getServer());
         chaseClient.start();
         p_196092_.sendSuccess(new TextComponent("You are now chasing " + p_196093_ + ":" + p_196094_ + ". If that server does '/chase lead' then you will automatically go to the same position. Use '/chase stop' to stop chasing."), false);
         return 0;
      }
   }
}