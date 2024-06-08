package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType(new TranslatableComponent("commands.schedule.same_tick"));
   private static final DynamicCommandExceptionType ERROR_CANT_REMOVE = new DynamicCommandExceptionType((p_138437_) -> {
      return new TranslatableComponent("commands.schedule.cleared.failure", p_138437_);
   });
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_SCHEDULE = (p_138424_, p_138425_) -> {
      return SharedSuggestionProvider.suggest(p_138424_.getSource().getServer().getWorldData().overworldData().getScheduledEvents().getEventsIds(), p_138425_);
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_138420_) {
      p_138420_.register(Commands.literal("schedule").requires((p_138427_) -> {
         return p_138427_.hasPermission(2);
      }).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(Commands.argument("time", TimeArgument.time()).executes((p_138459_) -> {
         return schedule(p_138459_.getSource(), FunctionArgument.getFunctionOrTag(p_138459_, "function"), IntegerArgumentType.getInteger(p_138459_, "time"), true);
      }).then(Commands.literal("append").executes((p_138457_) -> {
         return schedule(p_138457_.getSource(), FunctionArgument.getFunctionOrTag(p_138457_, "function"), IntegerArgumentType.getInteger(p_138457_, "time"), false);
      })).then(Commands.literal("replace").executes((p_138455_) -> {
         return schedule(p_138455_.getSource(), FunctionArgument.getFunctionOrTag(p_138455_, "function"), IntegerArgumentType.getInteger(p_138455_, "time"), true);
      }))))).then(Commands.literal("clear").then(Commands.argument("function", StringArgumentType.greedyString()).suggests(SUGGEST_SCHEDULE).executes((p_138422_) -> {
         return remove(p_138422_.getSource(), StringArgumentType.getString(p_138422_, "function"));
      }))));
   }

   private static int schedule(CommandSourceStack p_138429_, Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> p_138430_, int p_138431_, boolean p_138432_) throws CommandSyntaxException {
      if (p_138431_ == 0) {
         throw ERROR_SAME_TICK.create();
      } else {
         long i = p_138429_.getLevel().getGameTime() + (long)p_138431_;
         ResourceLocation resourcelocation = p_138430_.getFirst();
         TimerQueue<MinecraftServer> timerqueue = p_138429_.getServer().getWorldData().overworldData().getScheduledEvents();
         p_138430_.getSecond().ifLeft((p_138453_) -> {
            String s = resourcelocation.toString();
            if (p_138432_) {
               timerqueue.remove(s);
            }

            timerqueue.schedule(s, i, new FunctionCallback(resourcelocation));
            p_138429_.sendSuccess(new TranslatableComponent("commands.schedule.created.function", resourcelocation, p_138431_, i), true);
         }).ifRight((p_138445_) -> {
            String s = "#" + resourcelocation;
            if (p_138432_) {
               timerqueue.remove(s);
            }

            timerqueue.schedule(s, i, new FunctionTagCallback(resourcelocation));
            p_138429_.sendSuccess(new TranslatableComponent("commands.schedule.created.tag", resourcelocation, p_138431_, i), true);
         });
         return Math.floorMod(i, Integer.MAX_VALUE);
      }
   }

   private static int remove(CommandSourceStack p_138434_, String p_138435_) throws CommandSyntaxException {
      int i = p_138434_.getServer().getWorldData().overworldData().getScheduledEvents().remove(p_138435_);
      if (i == 0) {
         throw ERROR_CANT_REMOVE.create(p_138435_);
      } else {
         p_138434_.sendSuccess(new TranslatableComponent("commands.schedule.cleared.success", i, p_138435_), true);
         return i;
      }
   }
}