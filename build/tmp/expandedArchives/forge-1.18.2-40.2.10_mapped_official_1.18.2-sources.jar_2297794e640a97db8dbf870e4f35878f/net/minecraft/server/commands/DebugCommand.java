package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import org.slf4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.alreadyRunning"));

   public static void register(CommandDispatcher<CommandSourceStack> p_136906_) {
      p_136906_.register(Commands.literal("debug").requires((p_180073_) -> {
         return p_180073_.hasPermission(3);
      }).then(Commands.literal("start").executes((p_180069_) -> {
         return start(p_180069_.getSource());
      })).then(Commands.literal("stop").executes((p_136918_) -> {
         return stop(p_136918_.getSource());
      })).then(Commands.literal("function").requires((p_180071_) -> {
         return p_180071_.hasPermission(3);
      }).then(Commands.argument("name", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).executes((p_136908_) -> {
         return traceFunction(p_136908_.getSource(), FunctionArgument.getFunctions(p_136908_, "name"));
      }))));
   }

   private static int start(CommandSourceStack p_136910_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_136910_.getServer();
      if (minecraftserver.isTimeProfilerRunning()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         minecraftserver.startTimeProfiler();
         p_136910_.sendSuccess(new TranslatableComponent("commands.debug.started"), true);
         return 0;
      }
   }

   private static int stop(CommandSourceStack p_136916_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_136916_.getServer();
      if (!minecraftserver.isTimeProfilerRunning()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         ProfileResults profileresults = minecraftserver.stopTimeProfiler();
         double d0 = (double)profileresults.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
         double d1 = (double)profileresults.getTickDuration() / d0;
         p_136916_.sendSuccess(new TranslatableComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d0), profileresults.getTickDuration(), String.format("%.2f", d1)), true);
         return (int)d1;
      }
   }

   private static int traceFunction(CommandSourceStack p_180066_, Collection<CommandFunction> p_180067_) {
      int i = 0;
      MinecraftServer minecraftserver = p_180066_.getServer();
      String s = "debug-trace-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt";

      try {
         Path path = minecraftserver.getFile("debug").toPath();
         Files.createDirectories(path);
         Writer writer = Files.newBufferedWriter(path.resolve(s), StandardCharsets.UTF_8);

         try {
            PrintWriter printwriter = new PrintWriter(writer);

            for(CommandFunction commandfunction : p_180067_) {
               printwriter.println((Object)commandfunction.getId());
               DebugCommand.Tracer debugcommand$tracer = new DebugCommand.Tracer(printwriter);
               i += p_180066_.getServer().getFunctions().execute(commandfunction, p_180066_.withSource(debugcommand$tracer).withMaximumPermission(2), debugcommand$tracer);
            }
         } catch (Throwable throwable1) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (writer != null) {
            writer.close();
         }
      } catch (IOException | UncheckedIOException uncheckedioexception) {
         LOGGER.warn("Tracing failed", (Throwable)uncheckedioexception);
         p_180066_.sendFailure(new TranslatableComponent("commands.debug.function.traceFailed"));
      }

      if (p_180067_.size() == 1) {
         p_180066_.sendSuccess(new TranslatableComponent("commands.debug.function.success.single", i, p_180067_.iterator().next().getId(), s), true);
      } else {
         p_180066_.sendSuccess(new TranslatableComponent("commands.debug.function.success.multiple", i, p_180067_.size(), s), true);
      }

      return i;
   }

   static class Tracer implements CommandSource, ServerFunctionManager.TraceCallbacks {
      public static final int INDENT_OFFSET = 1;
      private final PrintWriter output;
      private int lastIndent;
      private boolean waitingForResult;

      Tracer(PrintWriter p_180079_) {
         this.output = p_180079_;
      }

      private void indentAndSave(int p_180082_) {
         this.printIndent(p_180082_);
         this.lastIndent = p_180082_;
      }

      private void printIndent(int p_180098_) {
         for(int i = 0; i < p_180098_ + 1; ++i) {
            this.output.write("    ");
         }

      }

      private void newLine() {
         if (this.waitingForResult) {
            this.output.println();
            this.waitingForResult = false;
         }

      }

      public void onCommand(int p_180084_, String p_180085_) {
         this.newLine();
         this.indentAndSave(p_180084_);
         this.output.print("[C] ");
         this.output.print(p_180085_);
         this.waitingForResult = true;
      }

      public void onReturn(int p_180087_, String p_180088_, int p_180089_) {
         if (this.waitingForResult) {
            this.output.print(" -> ");
            this.output.println(p_180089_);
            this.waitingForResult = false;
         } else {
            this.indentAndSave(p_180087_);
            this.output.print("[R = ");
            this.output.print(p_180089_);
            this.output.print("] ");
            this.output.println(p_180088_);
         }

      }

      public void onCall(int p_180091_, ResourceLocation p_180092_, int p_180093_) {
         this.newLine();
         this.indentAndSave(p_180091_);
         this.output.print("[F] ");
         this.output.print((Object)p_180092_);
         this.output.print(" size=");
         this.output.println(p_180093_);
      }

      public void onError(int p_180100_, String p_180101_) {
         this.newLine();
         this.indentAndSave(p_180100_ + 1);
         this.output.print("[E] ");
         this.output.print(p_180101_);
      }

      public void sendMessage(Component p_180095_, UUID p_180096_) {
         this.newLine();
         this.printIndent(this.lastIndent + 1);
         this.output.print("[M] ");
         if (p_180096_ != Util.NIL_UUID) {
            this.output.print((Object)p_180096_);
            this.output.print(": ");
         }

         this.output.println(p_180095_.getString());
      }

      public boolean acceptsSuccess() {
         return true;
      }

      public boolean acceptsFailure() {
         return true;
      }

      public boolean shouldInformAdmins() {
         return false;
      }

      public boolean alwaysAccepts() {
         return true;
      }
   }
}