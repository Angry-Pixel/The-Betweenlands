package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class SetSpawnCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138644_) {
      p_138644_.register(Commands.literal("spawnpoint").requires((p_138648_) -> {
         return p_138648_.hasPermission(2);
      }).executes((p_138659_) -> {
         return setSpawn(p_138659_.getSource(), Collections.singleton(p_138659_.getSource().getPlayerOrException()), new BlockPos(p_138659_.getSource().getPosition()), 0.0F);
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_138657_) -> {
         return setSpawn(p_138657_.getSource(), EntityArgument.getPlayers(p_138657_, "targets"), new BlockPos(p_138657_.getSource().getPosition()), 0.0F);
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_138655_) -> {
         return setSpawn(p_138655_.getSource(), EntityArgument.getPlayers(p_138655_, "targets"), BlockPosArgument.getSpawnablePos(p_138655_, "pos"), 0.0F);
      }).then(Commands.argument("angle", AngleArgument.angle()).executes((p_138646_) -> {
         return setSpawn(p_138646_.getSource(), EntityArgument.getPlayers(p_138646_, "targets"), BlockPosArgument.getSpawnablePos(p_138646_, "pos"), AngleArgument.getAngle(p_138646_, "angle"));
      })))));
   }

   private static int setSpawn(CommandSourceStack p_138650_, Collection<ServerPlayer> p_138651_, BlockPos p_138652_, float p_138653_) {
      ResourceKey<Level> resourcekey = p_138650_.getLevel().dimension();

      for(ServerPlayer serverplayer : p_138651_) {
         serverplayer.setRespawnPosition(resourcekey, p_138652_, p_138653_, true, false);
      }

      String s = resourcekey.location().toString();
      if (p_138651_.size() == 1) {
         p_138650_.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.single", p_138652_.getX(), p_138652_.getY(), p_138652_.getZ(), p_138653_, s, p_138651_.iterator().next().getDisplayName()), true);
      } else {
         p_138650_.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.multiple", p_138652_.getX(), p_138652_.getY(), p_138652_.getZ(), p_138653_, s, p_138651_.size()), true);
      }

      return p_138651_.size();
   }
}