package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType((p_136948_) -> {
      return new TranslatableComponent("commands.difficulty.failure", p_136948_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_136939_) {
      LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("difficulty");

      for(Difficulty difficulty : Difficulty.values()) {
         literalargumentbuilder.then(Commands.literal(difficulty.getKey()).executes((p_136937_) -> {
            return setDifficulty(p_136937_.getSource(), difficulty);
         }));
      }

      p_136939_.register(literalargumentbuilder.requires((p_136943_) -> {
         return p_136943_.hasPermission(2);
      }).executes((p_136941_) -> {
         Difficulty difficulty1 = p_136941_.getSource().getLevel().getDifficulty();
         p_136941_.getSource().sendSuccess(new TranslatableComponent("commands.difficulty.query", difficulty1.getDisplayName()), false);
         return difficulty1.getId();
      }));
   }

   public static int setDifficulty(CommandSourceStack p_136945_, Difficulty p_136946_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_136945_.getServer();
      if (minecraftserver.getWorldData().getDifficulty() == p_136946_) {
         throw ERROR_ALREADY_DIFFICULT.create(p_136946_.getKey());
      } else {
         minecraftserver.setDifficulty(p_136946_, true);
         p_136945_.sendSuccess(new TranslatableComponent("commands.difficulty.success", p_136946_.getDisplayName()), true);
         return 0;
      }
   }
}