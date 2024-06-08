package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.GameRules;

public class ServerFunctionManager {
   private static final Component NO_RECURSIVE_TRACES = new TranslatableComponent("commands.debug.function.noRecursion");
   private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
   final MinecraftServer server;
   @Nullable
   private ServerFunctionManager.ExecutionContext context;
   private List<CommandFunction> ticking = ImmutableList.of();
   private boolean postReload;
   private ServerFunctionLibrary library;

   public ServerFunctionManager(MinecraftServer p_136110_, ServerFunctionLibrary p_136111_) {
      this.server = p_136110_;
      this.library = p_136111_;
      this.postReload(p_136111_);
   }

   public int getCommandLimit() {
      return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
   }

   public CommandDispatcher<CommandSourceStack> getDispatcher() {
      return this.server.getCommands().getDispatcher();
   }

   public void tick() {
      this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
      if (this.postReload) {
         this.postReload = false;
         Collection<CommandFunction> collection = this.library.getTag(LOAD_FUNCTION_TAG).getValues();
         this.executeTagFunctions(collection, LOAD_FUNCTION_TAG);
      }

   }

   private void executeTagFunctions(Collection<CommandFunction> p_136116_, ResourceLocation p_136117_) {
      this.server.getProfiler().push(p_136117_::toString);

      for(CommandFunction commandfunction : p_136116_) {
         this.execute(commandfunction, this.getGameLoopSender());
      }

      this.server.getProfiler().pop();
   }

   public int execute(CommandFunction p_136113_, CommandSourceStack p_136114_) {
      return this.execute(p_136113_, p_136114_, (ServerFunctionManager.TraceCallbacks)null);
   }

   public int execute(CommandFunction p_179961_, CommandSourceStack p_179962_, @Nullable ServerFunctionManager.TraceCallbacks p_179963_) {
      if (this.context != null) {
         if (p_179963_ != null) {
            this.context.reportError(NO_RECURSIVE_TRACES.getString());
            return 0;
         } else {
            this.context.delayFunctionCall(p_179961_, p_179962_);
            return 0;
         }
      } else {
         int i;
         try {
            this.context = new ServerFunctionManager.ExecutionContext(p_179963_);
            i = this.context.runTopCommand(p_179961_, p_179962_);
         } finally {
            this.context = null;
         }

         return i;
      }
   }

   public void replaceLibrary(ServerFunctionLibrary p_136121_) {
      this.library = p_136121_;
      this.postReload(p_136121_);
   }

   private void postReload(ServerFunctionLibrary p_136126_) {
      this.ticking = ImmutableList.copyOf(p_136126_.getTag(TICK_FUNCTION_TAG).getValues());
      this.postReload = true;
   }

   public CommandSourceStack getGameLoopSender() {
      return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
   }

   public Optional<CommandFunction> get(ResourceLocation p_136119_) {
      return this.library.getFunction(p_136119_);
   }

   public Tag<CommandFunction> getTag(ResourceLocation p_136124_) {
      return this.library.getTag(p_136124_);
   }

   public Iterable<ResourceLocation> getFunctionNames() {
      return this.library.getFunctions().keySet();
   }

   public Iterable<ResourceLocation> getTagNames() {
      return this.library.getAvailableTags();
   }

   class ExecutionContext {
      private int depth;
      @Nullable
      private final ServerFunctionManager.TraceCallbacks tracer;
      private final Deque<ServerFunctionManager.QueuedCommand> commandQueue = Queues.newArrayDeque();
      private final List<ServerFunctionManager.QueuedCommand> nestedCalls = Lists.newArrayList();

      ExecutionContext(ServerFunctionManager.TraceCallbacks p_179971_) {
         this.tracer = p_179971_;
      }

      void delayFunctionCall(CommandFunction p_179973_, CommandSourceStack p_179974_) {
         int i = ServerFunctionManager.this.getCommandLimit();
         if (this.commandQueue.size() + this.nestedCalls.size() < i) {
            this.nestedCalls.add(new ServerFunctionManager.QueuedCommand(p_179974_, this.depth, new CommandFunction.FunctionEntry(p_179973_)));
         }

      }

      int runTopCommand(CommandFunction p_179978_, CommandSourceStack p_179979_) {
         int i = ServerFunctionManager.this.getCommandLimit();
         int j = 0;
         CommandFunction.Entry[] acommandfunction$entry = p_179978_.getEntries();

         for(int k = acommandfunction$entry.length - 1; k >= 0; --k) {
            this.commandQueue.push(new ServerFunctionManager.QueuedCommand(p_179979_, 0, acommandfunction$entry[k]));
         }

         while(!this.commandQueue.isEmpty()) {
            try {
               ServerFunctionManager.QueuedCommand serverfunctionmanager$queuedcommand = this.commandQueue.removeFirst();
               ServerFunctionManager.this.server.getProfiler().push(serverfunctionmanager$queuedcommand::toString);
               this.depth = serverfunctionmanager$queuedcommand.depth;
               serverfunctionmanager$queuedcommand.execute(ServerFunctionManager.this, this.commandQueue, i, this.tracer);
               if (!this.nestedCalls.isEmpty()) {
                  Lists.reverse(this.nestedCalls).forEach(this.commandQueue::addFirst);
                  this.nestedCalls.clear();
               }
            } finally {
               ServerFunctionManager.this.server.getProfiler().pop();
            }

            ++j;
            if (j >= i) {
               return j;
            }
         }

         return j;
      }

      public void reportError(String p_179976_) {
         if (this.tracer != null) {
            this.tracer.onError(this.depth, p_179976_);
         }

      }
   }

   public static class QueuedCommand {
      private final CommandSourceStack sender;
      final int depth;
      private final CommandFunction.Entry entry;

      public QueuedCommand(CommandSourceStack p_179982_, int p_179983_, CommandFunction.Entry p_179984_) {
         this.sender = p_179982_;
         this.depth = p_179983_;
         this.entry = p_179984_;
      }

      public void execute(ServerFunctionManager p_179986_, Deque<ServerFunctionManager.QueuedCommand> p_179987_, int p_179988_, @Nullable ServerFunctionManager.TraceCallbacks p_179989_) {
         try {
            this.entry.execute(p_179986_, this.sender, p_179987_, p_179988_, this.depth, p_179989_);
         } catch (CommandSyntaxException commandsyntaxexception) {
            if (p_179989_ != null) {
               p_179989_.onError(this.depth, commandsyntaxexception.getRawMessage().getString());
            }
         } catch (Exception exception) {
            if (p_179989_ != null) {
               p_179989_.onError(this.depth, exception.getMessage());
            }
         }

      }

      public String toString() {
         return this.entry.toString();
      }
   }

   public interface TraceCallbacks {
      void onCommand(int p_179990_, String p_179991_);

      void onReturn(int p_179992_, String p_179993_, int p_179994_);

      void onError(int p_179998_, String p_179999_);

      void onCall(int p_179995_, ResourceLocation p_179996_, int p_179997_);
   }
}