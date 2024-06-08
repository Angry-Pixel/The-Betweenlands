package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(new TranslatableComponent("commands.playsound.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_138157_) {
      RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> requiredargumentbuilder = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);

      for(SoundSource soundsource : SoundSource.values()) {
         requiredargumentbuilder.then(source(soundsource));
      }

      p_138157_.register(Commands.literal("playsound").requires((p_138159_) -> {
         return p_138159_.hasPermission(2);
      }).then(requiredargumentbuilder));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> source(SoundSource p_138152_) {
      return Commands.literal(p_138152_.getName()).then(Commands.argument("targets", EntityArgument.players()).executes((p_138180_) -> {
         return playSound(p_138180_.getSource(), EntityArgument.getPlayers(p_138180_, "targets"), ResourceLocationArgument.getId(p_138180_, "sound"), p_138152_, p_138180_.getSource().getPosition(), 1.0F, 1.0F, 0.0F);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_138177_) -> {
         return playSound(p_138177_.getSource(), EntityArgument.getPlayers(p_138177_, "targets"), ResourceLocationArgument.getId(p_138177_, "sound"), p_138152_, Vec3Argument.getVec3(p_138177_, "pos"), 1.0F, 1.0F, 0.0F);
      }).then(Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((p_138174_) -> {
         return playSound(p_138174_.getSource(), EntityArgument.getPlayers(p_138174_, "targets"), ResourceLocationArgument.getId(p_138174_, "sound"), p_138152_, Vec3Argument.getVec3(p_138174_, "pos"), p_138174_.getArgument("volume", Float.class), 1.0F, 0.0F);
      }).then(Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((p_138171_) -> {
         return playSound(p_138171_.getSource(), EntityArgument.getPlayers(p_138171_, "targets"), ResourceLocationArgument.getId(p_138171_, "sound"), p_138152_, Vec3Argument.getVec3(p_138171_, "pos"), p_138171_.getArgument("volume", Float.class), p_138171_.getArgument("pitch", Float.class), 0.0F);
      }).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((p_138155_) -> {
         return playSound(p_138155_.getSource(), EntityArgument.getPlayers(p_138155_, "targets"), ResourceLocationArgument.getId(p_138155_, "sound"), p_138152_, Vec3Argument.getVec3(p_138155_, "pos"), p_138155_.getArgument("volume", Float.class), p_138155_.getArgument("pitch", Float.class), p_138155_.getArgument("minVolume", Float.class));
      }))))));
   }

   private static int playSound(CommandSourceStack p_138161_, Collection<ServerPlayer> p_138162_, ResourceLocation p_138163_, SoundSource p_138164_, Vec3 p_138165_, float p_138166_, float p_138167_, float p_138168_) throws CommandSyntaxException {
      double d0 = Math.pow(p_138166_ > 1.0F ? (double)(p_138166_ * 16.0F) : 16.0D, 2.0D);
      int i = 0;
      Iterator iterator = p_138162_.iterator();

      while(true) {
         ServerPlayer serverplayer;
         Vec3 vec3;
         float f;
         while(true) {
            if (!iterator.hasNext()) {
               if (i == 0) {
                  throw ERROR_TOO_FAR.create();
               }

               if (p_138162_.size() == 1) {
                  p_138161_.sendSuccess(new TranslatableComponent("commands.playsound.success.single", p_138163_, p_138162_.iterator().next().getDisplayName()), true);
               } else {
                  p_138161_.sendSuccess(new TranslatableComponent("commands.playsound.success.multiple", p_138163_, p_138162_.size()), true);
               }

               return i;
            }

            serverplayer = (ServerPlayer)iterator.next();
            double d1 = p_138165_.x - serverplayer.getX();
            double d2 = p_138165_.y - serverplayer.getY();
            double d3 = p_138165_.z - serverplayer.getZ();
            double d4 = d1 * d1 + d2 * d2 + d3 * d3;
            vec3 = p_138165_;
            f = p_138166_;
            if (!(d4 > d0)) {
               break;
            }

            if (!(p_138168_ <= 0.0F)) {
               double d5 = Math.sqrt(d4);
               vec3 = new Vec3(serverplayer.getX() + d1 / d5 * 2.0D, serverplayer.getY() + d2 / d5 * 2.0D, serverplayer.getZ() + d3 / d5 * 2.0D);
               f = p_138168_;
               break;
            }
         }

         serverplayer.connection.send(new ClientboundCustomSoundPacket(p_138163_, p_138164_, vec3, f, p_138167_));
         ++i;
      }
   }
}