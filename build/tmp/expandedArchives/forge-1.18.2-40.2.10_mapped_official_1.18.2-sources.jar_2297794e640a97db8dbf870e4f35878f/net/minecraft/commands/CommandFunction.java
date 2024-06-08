package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
   private final CommandFunction.Entry[] entries;
   final ResourceLocation id;

   public CommandFunction(ResourceLocation p_77979_, CommandFunction.Entry[] p_77980_) {
      this.id = p_77979_;
      this.entries = p_77980_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public CommandFunction.Entry[] getEntries() {
      return this.entries;
   }

   public static CommandFunction fromLines(ResourceLocation p_77985_, CommandDispatcher<CommandSourceStack> p_77986_, CommandSourceStack p_77987_, List<String> p_77988_) {
      List<CommandFunction.Entry> list = Lists.newArrayListWithCapacity(p_77988_.size());

      for(int i = 0; i < p_77988_.size(); ++i) {
         int j = i + 1;
         String s = p_77988_.get(i).trim();
         StringReader stringreader = new StringReader(s);
         if (stringreader.canRead() && stringreader.peek() != '#') {
            if (stringreader.peek() == '/') {
               stringreader.skip();
               if (stringreader.peek() == '/') {
                  throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
               }

               String s1 = stringreader.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (did you mean '" + s1 + "'? Do not use a preceding forwards slash.)");
            }

            try {
               ParseResults<CommandSourceStack> parseresults = p_77986_.parse(stringreader, p_77987_);
               if (parseresults.getReader().canRead()) {
                  throw Commands.getParseException(parseresults);
               }

               list.add(new CommandFunction.CommandEntry(parseresults));
            } catch (CommandSyntaxException commandsyntaxexception) {
               throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + commandsyntaxexception.getMessage());
            }
         }
      }

      return new CommandFunction(p_77985_, list.toArray(new CommandFunction.Entry[0]));
   }

   public static class CacheableFunction {
      public static final CommandFunction.CacheableFunction NONE = new CommandFunction.CacheableFunction((ResourceLocation)null);
      @Nullable
      private final ResourceLocation id;
      private boolean resolved;
      private Optional<CommandFunction> function = Optional.empty();

      public CacheableFunction(@Nullable ResourceLocation p_77998_) {
         this.id = p_77998_;
      }

      public CacheableFunction(CommandFunction p_77996_) {
         this.resolved = true;
         this.id = null;
         this.function = Optional.of(p_77996_);
      }

      public Optional<CommandFunction> get(ServerFunctionManager p_78003_) {
         if (!this.resolved) {
            if (this.id != null) {
               this.function = p_78003_.get(this.id);
            }

            this.resolved = true;
         }

         return this.function;
      }

      @Nullable
      public ResourceLocation getId() {
         return this.function.map((p_78001_) -> {
            return p_78001_.id;
         }).orElse(this.id);
      }
   }

   public static class CommandEntry implements CommandFunction.Entry {
      private final ParseResults<CommandSourceStack> parse;

      public CommandEntry(ParseResults<CommandSourceStack> p_78006_) {
         this.parse = p_78006_;
      }

      public void execute(ServerFunctionManager p_164879_, CommandSourceStack p_164880_, Deque<ServerFunctionManager.QueuedCommand> p_164881_, int p_164882_, int p_164883_, @Nullable ServerFunctionManager.TraceCallbacks p_164884_) throws CommandSyntaxException {
         if (p_164884_ != null) {
            String s = this.parse.getReader().getString();
            p_164884_.onCommand(p_164883_, s);
            int i = this.execute(p_164879_, p_164880_);
            p_164884_.onReturn(p_164883_, s, i);
         } else {
            this.execute(p_164879_, p_164880_);
         }

      }

      private int execute(ServerFunctionManager p_164876_, CommandSourceStack p_164877_) throws CommandSyntaxException {
         return p_164876_.getDispatcher().execute(new ParseResults<>(this.parse.getContext().withSource(p_164877_), this.parse.getReader(), this.parse.getExceptions()));
      }

      public String toString() {
         return this.parse.getReader().getString();
      }
   }

   @FunctionalInterface
   public interface Entry {
      void execute(ServerFunctionManager p_164885_, CommandSourceStack p_164886_, Deque<ServerFunctionManager.QueuedCommand> p_164887_, int p_164888_, int p_164889_, @Nullable ServerFunctionManager.TraceCallbacks p_164890_) throws CommandSyntaxException;
   }

   public static class FunctionEntry implements CommandFunction.Entry {
      private final CommandFunction.CacheableFunction function;

      public FunctionEntry(CommandFunction p_78019_) {
         this.function = new CommandFunction.CacheableFunction(p_78019_);
      }

      public void execute(ServerFunctionManager p_164902_, CommandSourceStack p_164903_, Deque<ServerFunctionManager.QueuedCommand> p_164904_, int p_164905_, int p_164906_, @Nullable ServerFunctionManager.TraceCallbacks p_164907_) {
         Util.ifElse(this.function.get(p_164902_), (p_164900_) -> {
            CommandFunction.Entry[] acommandfunction$entry = p_164900_.getEntries();
            if (p_164907_ != null) {
               p_164907_.onCall(p_164906_, p_164900_.getId(), acommandfunction$entry.length);
            }

            int i = p_164905_ - p_164904_.size();
            int j = Math.min(acommandfunction$entry.length, i);

            for(int k = j - 1; k >= 0; --k) {
               p_164904_.addFirst(new ServerFunctionManager.QueuedCommand(p_164903_, p_164906_ + 1, acommandfunction$entry[k]));
            }

         }, () -> {
            if (p_164907_ != null) {
               p_164907_.onCall(p_164906_, this.function.getId(), -1);
            }

         });
      }

      public String toString() {
         return "function " + this.function.getId();
      }
   }
}