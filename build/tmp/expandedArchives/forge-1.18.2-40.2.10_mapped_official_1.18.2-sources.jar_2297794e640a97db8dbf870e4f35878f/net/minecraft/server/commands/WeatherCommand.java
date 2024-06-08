package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class WeatherCommand {
   private static final int DEFAULT_TIME = 6000;

   public static void register(CommandDispatcher<CommandSourceStack> p_139167_) {
      p_139167_.register(Commands.literal("weather").requires((p_139171_) -> {
         return p_139171_.hasPermission(2);
      }).then(Commands.literal("clear").executes((p_139190_) -> {
         return setClear(p_139190_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_139188_) -> {
         return setClear(p_139188_.getSource(), IntegerArgumentType.getInteger(p_139188_, "duration") * 20);
      }))).then(Commands.literal("rain").executes((p_139186_) -> {
         return setRain(p_139186_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_139181_) -> {
         return setRain(p_139181_.getSource(), IntegerArgumentType.getInteger(p_139181_, "duration") * 20);
      }))).then(Commands.literal("thunder").executes((p_139176_) -> {
         return setThunder(p_139176_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_139169_) -> {
         return setThunder(p_139169_.getSource(), IntegerArgumentType.getInteger(p_139169_, "duration") * 20);
      }))));
   }

   private static int setClear(CommandSourceStack p_139173_, int p_139174_) {
      p_139173_.getLevel().setWeatherParameters(p_139174_, 0, false, false);
      p_139173_.sendSuccess(new TranslatableComponent("commands.weather.set.clear"), true);
      return p_139174_;
   }

   private static int setRain(CommandSourceStack p_139178_, int p_139179_) {
      p_139178_.getLevel().setWeatherParameters(0, p_139179_, true, false);
      p_139178_.sendSuccess(new TranslatableComponent("commands.weather.set.rain"), true);
      return p_139179_;
   }

   private static int setThunder(CommandSourceStack p_139183_, int p_139184_) {
      p_139183_.getLevel().setWeatherParameters(0, p_139184_, true, true);
      p_139183_.sendSuccess(new TranslatableComponent("commands.weather.set.thunder"), true);
      return p_139184_;
   }
}