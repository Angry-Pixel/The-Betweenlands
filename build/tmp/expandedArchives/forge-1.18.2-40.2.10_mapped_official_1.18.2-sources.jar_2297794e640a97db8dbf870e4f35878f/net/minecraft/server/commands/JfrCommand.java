package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;

public class JfrCommand {
   private static final SimpleCommandExceptionType START_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.jfr.start.failed"));
   private static final DynamicCommandExceptionType DUMP_FAILED = new DynamicCommandExceptionType((p_183652_) -> {
      return new TranslatableComponent("commands.jfr.dump.failed", p_183652_);
   });

   private JfrCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> p_183646_) {
      p_183646_.register(Commands.literal("jfr").requires((p_183661_) -> {
         return p_183661_.hasPermission(4);
      }).then(Commands.literal("start").executes((p_183657_) -> {
         return startJfr(p_183657_.getSource());
      })).then(Commands.literal("stop").executes((p_183648_) -> {
         return stopJfr(p_183648_.getSource());
      })));
   }

   private static int startJfr(CommandSourceStack p_183650_) throws CommandSyntaxException {
      Environment environment = Environment.from(p_183650_.getServer());
      if (!JvmProfiler.INSTANCE.start(environment)) {
         throw START_FAILED.create();
      } else {
         p_183650_.sendSuccess(new TranslatableComponent("commands.jfr.started"), false);
         return 1;
      }
   }

   private static int stopJfr(CommandSourceStack p_183659_) throws CommandSyntaxException {
      try {
         Path path = Paths.get(".").relativize(JvmProfiler.INSTANCE.stop().normalize());
         Path path1 = p_183659_.getServer().isPublished() && !SharedConstants.IS_RUNNING_IN_IDE ? path : path.toAbsolutePath();
         Component component = (new TextComponent(path.toString())).withStyle(ChatFormatting.UNDERLINE).withStyle((p_183655_) -> {
            return p_183655_.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, path1.toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.copy.click")));
         });
         p_183659_.sendSuccess(new TranslatableComponent("commands.jfr.stopped", component), false);
         return 1;
      } catch (Throwable throwable) {
         throw DUMP_FAILED.create(throwable.getMessage());
      }
   }
}