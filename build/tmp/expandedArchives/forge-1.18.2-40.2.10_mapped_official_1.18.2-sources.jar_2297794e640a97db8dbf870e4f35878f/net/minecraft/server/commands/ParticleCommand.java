package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ParticleCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.particle.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_138123_) {
      p_138123_.register(Commands.literal("particle").requires((p_138127_) -> {
         return p_138127_.hasPermission(2);
      }).then(Commands.argument("name", ParticleArgument.particle()).executes((p_138148_) -> {
         return sendParticles(p_138148_.getSource(), ParticleArgument.getParticle(p_138148_, "name"), p_138148_.getSource().getPosition(), Vec3.ZERO, 0.0F, 0, false, p_138148_.getSource().getServer().getPlayerList().getPlayers());
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_138146_) -> {
         return sendParticles(p_138146_.getSource(), ParticleArgument.getParticle(p_138146_, "name"), Vec3Argument.getVec3(p_138146_, "pos"), Vec3.ZERO, 0.0F, 0, false, p_138146_.getSource().getServer().getPlayerList().getPlayers());
      }).then(Commands.argument("delta", Vec3Argument.vec3(false)).then(Commands.argument("speed", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("count", IntegerArgumentType.integer(0)).executes((p_138144_) -> {
         return sendParticles(p_138144_.getSource(), ParticleArgument.getParticle(p_138144_, "name"), Vec3Argument.getVec3(p_138144_, "pos"), Vec3Argument.getVec3(p_138144_, "delta"), FloatArgumentType.getFloat(p_138144_, "speed"), IntegerArgumentType.getInteger(p_138144_, "count"), false, p_138144_.getSource().getServer().getPlayerList().getPlayers());
      }).then(Commands.literal("force").executes((p_138142_) -> {
         return sendParticles(p_138142_.getSource(), ParticleArgument.getParticle(p_138142_, "name"), Vec3Argument.getVec3(p_138142_, "pos"), Vec3Argument.getVec3(p_138142_, "delta"), FloatArgumentType.getFloat(p_138142_, "speed"), IntegerArgumentType.getInteger(p_138142_, "count"), true, p_138142_.getSource().getServer().getPlayerList().getPlayers());
      }).then(Commands.argument("viewers", EntityArgument.players()).executes((p_138140_) -> {
         return sendParticles(p_138140_.getSource(), ParticleArgument.getParticle(p_138140_, "name"), Vec3Argument.getVec3(p_138140_, "pos"), Vec3Argument.getVec3(p_138140_, "delta"), FloatArgumentType.getFloat(p_138140_, "speed"), IntegerArgumentType.getInteger(p_138140_, "count"), true, EntityArgument.getPlayers(p_138140_, "viewers"));
      }))).then(Commands.literal("normal").executes((p_138138_) -> {
         return sendParticles(p_138138_.getSource(), ParticleArgument.getParticle(p_138138_, "name"), Vec3Argument.getVec3(p_138138_, "pos"), Vec3Argument.getVec3(p_138138_, "delta"), FloatArgumentType.getFloat(p_138138_, "speed"), IntegerArgumentType.getInteger(p_138138_, "count"), false, p_138138_.getSource().getServer().getPlayerList().getPlayers());
      }).then(Commands.argument("viewers", EntityArgument.players()).executes((p_138125_) -> {
         return sendParticles(p_138125_.getSource(), ParticleArgument.getParticle(p_138125_, "name"), Vec3Argument.getVec3(p_138125_, "pos"), Vec3Argument.getVec3(p_138125_, "delta"), FloatArgumentType.getFloat(p_138125_, "speed"), IntegerArgumentType.getInteger(p_138125_, "count"), false, EntityArgument.getPlayers(p_138125_, "viewers"));
      })))))))));
   }

   private static int sendParticles(CommandSourceStack p_138129_, ParticleOptions p_138130_, Vec3 p_138131_, Vec3 p_138132_, float p_138133_, int p_138134_, boolean p_138135_, Collection<ServerPlayer> p_138136_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayer serverplayer : p_138136_) {
         if (p_138129_.getLevel().sendParticles(serverplayer, p_138130_, p_138135_, p_138131_.x, p_138131_.y, p_138131_.z, p_138134_, p_138132_.x, p_138132_.y, p_138132_.z, (double)p_138133_)) {
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_FAILED.create();
      } else {
         p_138129_.sendSuccess(new TranslatableComponent("commands.particle.success", Registry.PARTICLE_TYPE.getKey(p_138130_.getType()).toString()), true);
         return i;
      }
   }
}