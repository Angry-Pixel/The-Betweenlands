package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.ItemCommands;
import net.minecraft.server.commands.JfrCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateBiomeCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PerfCommand;
import net.minecraft.server.commands.PlaceFeatureCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int LEVEL_ALL = 0;
   public static final int LEVEL_MODERATORS = 1;
   public static final int LEVEL_GAMEMASTERS = 2;
   public static final int LEVEL_ADMINS = 3;
   public static final int LEVEL_OWNERS = 4;
   private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

   public Commands(Commands.CommandSelection p_82093_) {
      AdvancementCommands.register(this.dispatcher);
      AttributeCommand.register(this.dispatcher);
      ExecuteCommand.register(this.dispatcher);
      BossBarCommands.register(this.dispatcher);
      ClearInventoryCommands.register(this.dispatcher);
      CloneCommands.register(this.dispatcher);
      DataCommands.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommands.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommands.register(this.dispatcher);
      EmoteCommands.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher);
      HelpCommand.register(this.dispatcher);
      ItemCommands.register(this.dispatcher);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListPlayersCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher);
      LocateBiomeCommand.register(this.dispatcher);
      LootCommand.register(this.dispatcher);
      MsgCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher);
      PlaceFeatureCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher, p_82093_ != Commands.CommandSelection.INTEGRATED);
      SetBlockCommand.register(this.dispatcher);
      SetSpawnCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher);
      TriggerCommand.register(this.dispatcher);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if (JvmProfiler.INSTANCE.isAvailable()) {
         JfrCommand.register(this.dispatcher);
      }

      if (net.minecraftforge.gametest.ForgeGameTestHooks.isGametestEnabled()) {
         TestCommand.register(this.dispatcher);
      }

      if (p_82093_.includeDedicated) {
         BanIpCommands.register(this.dispatcher);
         BanListCommands.register(this.dispatcher);
         BanPlayerCommands.register(this.dispatcher);
         DeOpCommands.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         PerfCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetPlayerIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      if (p_82093_.includeIntegrated) {
         PublishCommand.register(this.dispatcher);
      }
      net.minecraftforge.event.ForgeEventFactory.onCommandRegister(this.dispatcher, p_82093_);

      this.dispatcher.findAmbiguities((p_82108_, p_82109_, p_82110_, p_82111_) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(p_82109_), this.dispatcher.getPath(p_82110_), p_82111_);
      });
      this.dispatcher.setConsumer((p_82104_, p_82105_, p_82106_) -> {
         p_82104_.getSource().onCommandComplete(p_82104_, p_82105_, p_82106_);
      });
   }

   public int performCommand(CommandSourceStack p_82118_, String p_82119_) {
      StringReader stringreader = new StringReader(p_82119_);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      p_82118_.getServer().getProfiler().push(p_82119_);

      try {
         try {
            ParseResults<CommandSourceStack> parse = this.dispatcher.parse(stringreader, p_82118_);
            net.minecraftforge.event.CommandEvent event = new net.minecraftforge.event.CommandEvent(parse);
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
               if (event.getException() instanceof Exception exception) {
                  throw exception;
               } else if (event.getException() != null) {
                  com.google.common.base.Throwables.throwIfUnchecked(event.getException());
               }
               return 1;
            }
            return this.dispatcher.execute(event.getParseResults());
         } catch (CommandRuntimeException commandruntimeexception) {
            p_82118_.sendFailure(commandruntimeexception.getComponent());
            return 0;
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_82118_.sendFailure(ComponentUtils.fromMessage(commandsyntaxexception.getRawMessage()));
            if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
               int j = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
               MutableComponent mutablecomponent1 = (new TextComponent("")).withStyle(ChatFormatting.GRAY).withStyle((p_82134_) -> {
                  return p_82134_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p_82119_));
               });
               if (j > 10) {
                  mutablecomponent1.append("...");
               }

               mutablecomponent1.append(commandsyntaxexception.getInput().substring(Math.max(0, j - 10), j));
               if (j < commandsyntaxexception.getInput().length()) {
                  Component component = (new TextComponent(commandsyntaxexception.getInput().substring(j))).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.UNDERLINE});
                  mutablecomponent1.append(component);
               }

               mutablecomponent1.append((new TranslatableComponent("command.context.here")).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.ITALIC}));
               p_82118_.sendFailure(mutablecomponent1);
            }
         } catch (Exception exception) {
            MutableComponent mutablecomponent = new TextComponent(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            if (LOGGER.isDebugEnabled()) {
               LOGGER.error("Command exception: {}", p_82119_, exception);
               StackTraceElement[] astacktraceelement = exception.getStackTrace();

               for(int i = 0; i < Math.min(astacktraceelement.length, 3); ++i) {
                  mutablecomponent.append("\n\n").append(astacktraceelement[i].getMethodName()).append("\n ").append(astacktraceelement[i].getFileName()).append(":").append(String.valueOf(astacktraceelement[i].getLineNumber()));
               }
            }

            p_82118_.sendFailure((new TranslatableComponent("command.failed")).withStyle((p_82137_) -> {
               return p_82137_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutablecomponent));
            }));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               p_82118_.sendFailure(new TextComponent(Util.describeError(exception)));
               LOGGER.error("'{}' threw an exception", p_82119_, exception);
            }

            return 0;
         }

         return 0;
      } finally {
         p_82118_.getServer().getProfiler().pop();
      }
   }

   public void sendCommands(ServerPlayer p_82096_) {
      Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> map = Maps.newHashMap();
      RootCommandNode<SharedSuggestionProvider> rootcommandnode = new RootCommandNode<>();
      map.put(this.dispatcher.getRoot(), rootcommandnode);
      // FORGE: Use our own command node merging method to handle redirect nodes properly, see issue #7551
      net.minecraftforge.server.command.CommandHelper.mergeCommandNode(this.dispatcher.getRoot(), rootcommandnode, map, p_82096_.createCommandSourceStack(), ctx -> 0, suggest -> SuggestionProviders.safelySwap((com.mojang.brigadier.suggestion.SuggestionProvider<SharedSuggestionProvider>) (com.mojang.brigadier.suggestion.SuggestionProvider<?>) suggest));
      p_82096_.connection.send(new ClientboundCommandsPacket(rootcommandnode));
   }

   private void fillUsableCommands(CommandNode<CommandSourceStack> p_82113_, CommandNode<SharedSuggestionProvider> p_82114_, CommandSourceStack p_82115_, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> p_82116_) {
      for(CommandNode<CommandSourceStack> commandnode : p_82113_.getChildren()) {
         if (commandnode.canUse(p_82115_)) {
            ArgumentBuilder<SharedSuggestionProvider, ?> argumentbuilder = (ArgumentBuilder)commandnode.createBuilder();
            argumentbuilder.requires((p_82126_) -> {
               return true;
            });
            if (argumentbuilder.getCommand() != null) {
               argumentbuilder.executes((p_82102_) -> {
                  return 0;
               });
            }

            if (argumentbuilder instanceof RequiredArgumentBuilder) {
               RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder)argumentbuilder;
               if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                  requiredargumentbuilder.suggests(SuggestionProviders.safelySwap(requiredargumentbuilder.getSuggestionsProvider()));
               }
            }

            if (argumentbuilder.getRedirect() != null) {
               argumentbuilder.redirect(p_82116_.get(argumentbuilder.getRedirect()));
            }

            CommandNode<SharedSuggestionProvider> commandnode1 = argumentbuilder.build();
            p_82116_.put(commandnode, commandnode1);
            p_82114_.addChild(commandnode1);
            if (!commandnode.getChildren().isEmpty()) {
               this.fillUsableCommands(commandnode, commandnode1, p_82115_, p_82116_);
            }
         }
      }

   }

   public static LiteralArgumentBuilder<CommandSourceStack> literal(String p_82128_) {
      return LiteralArgumentBuilder.literal(p_82128_);
   }

   public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String p_82130_, ArgumentType<T> p_82131_) {
      return RequiredArgumentBuilder.argument(p_82130_, p_82131_);
   }

   public static Predicate<String> createValidator(Commands.ParseFunction p_82121_) {
      return (p_82124_) -> {
         try {
            p_82121_.parse(new StringReader(p_82124_));
            return true;
         } catch (CommandSyntaxException commandsyntaxexception) {
            return false;
         }
      };
   }

   public CommandDispatcher<CommandSourceStack> getDispatcher() {
      return this.dispatcher;
   }

   @Nullable
   public static <S> CommandSyntaxException getParseException(ParseResults<S> p_82098_) {
      if (!p_82098_.getReader().canRead()) {
         return null;
      } else if (p_82098_.getExceptions().size() == 1) {
         return p_82098_.getExceptions().values().iterator().next();
      } else {
         return p_82098_.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(p_82098_.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(p_82098_.getReader());
      }
   }

   public static void validate() {
      RootCommandNode<CommandSourceStack> rootcommandnode = (new Commands(Commands.CommandSelection.ALL)).getDispatcher().getRoot();
      Set<ArgumentType<?>> set = ArgumentTypes.findUsedArgumentTypes(rootcommandnode);
      Set<ArgumentType<?>> set1 = set.stream().filter((p_82140_) -> {
         return !ArgumentTypes.isTypeRegistered(p_82140_);
      }).collect(Collectors.toSet());
      if (!set1.isEmpty()) {
         LOGGER.warn("Missing type registration for following arguments:\n {}", set1.stream().map((p_82100_) -> {
            return "\t" + p_82100_;
         }).collect(Collectors.joining(",\n")));
         throw new IllegalStateException("Unregistered argument types");
      }
   }

   public static enum CommandSelection {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      final boolean includeIntegrated;
      final boolean includeDedicated;

      private CommandSelection(boolean p_82151_, boolean p_82152_) {
         this.includeIntegrated = p_82151_;
         this.includeDedicated = p_82152_;
      }
   }

   @FunctionalInterface
   public interface ParseFunction {
      void parse(StringReader p_82161_) throws CommandSyntaxException;
   }
}
