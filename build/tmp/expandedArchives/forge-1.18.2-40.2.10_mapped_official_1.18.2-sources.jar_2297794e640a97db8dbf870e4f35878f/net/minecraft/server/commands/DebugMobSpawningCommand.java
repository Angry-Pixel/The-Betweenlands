package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;

public class DebugMobSpawningCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_180111_) {
      LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("debugmobspawning").requires((p_180113_) -> {
         return p_180113_.hasPermission(2);
      });

      for(MobCategory mobcategory : MobCategory.values()) {
         literalargumentbuilder.then(Commands.literal(mobcategory.getName()).then(Commands.argument("at", BlockPosArgument.blockPos()).executes((p_180109_) -> {
            return spawnMobs(p_180109_.getSource(), mobcategory, BlockPosArgument.getLoadedBlockPos(p_180109_, "at"));
         })));
      }

      p_180111_.register(literalargumentbuilder);
   }

   private static int spawnMobs(CommandSourceStack p_180115_, MobCategory p_180116_, BlockPos p_180117_) {
      NaturalSpawner.spawnCategoryForPosition(p_180116_, p_180115_.getLevel(), p_180117_);
      return 1;
   }
}