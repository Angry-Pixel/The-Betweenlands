package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_137745_) {
      final LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("gamerule").requires((p_137750_) -> {
         return p_137750_.hasPermission(2);
      });
      GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
         public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> p_137764_, GameRules.Type<T> p_137765_) {
            literalargumentbuilder.then(Commands.literal(p_137764_.getId()).executes((p_137771_) -> {
               return GameRuleCommand.queryRule(p_137771_.getSource(), p_137764_);
            }).then(p_137765_.createArgument("value").executes((p_137768_) -> {
               return GameRuleCommand.setRule(p_137768_, p_137764_);
            })));
         }
      });
      p_137745_.register(literalargumentbuilder);
   }

   static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> p_137755_, GameRules.Key<T> p_137756_) {
      CommandSourceStack commandsourcestack = p_137755_.getSource();
      T t = commandsourcestack.getServer().getGameRules().getRule(p_137756_);
      t.setFromArgument(p_137755_, "value");
      commandsourcestack.sendSuccess(new TranslatableComponent("commands.gamerule.set", p_137756_.getId(), t.toString()), true);
      return t.getCommandResult();
   }

   static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack p_137758_, GameRules.Key<T> p_137759_) {
      T t = p_137758_.getServer().getGameRules().getRule(p_137759_);
      p_137758_.sendSuccess(new TranslatableComponent("commands.gamerule.query", p_137759_.getId(), t.toString()), false);
      return t.getCommandResult();
   }
}