package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class StopSoundCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138795_) {
      RequiredArgumentBuilder<CommandSourceStack, EntitySelector> requiredargumentbuilder = Commands.argument("targets", EntityArgument.players()).executes((p_138809_) -> {
         return stopSound(p_138809_.getSource(), EntityArgument.getPlayers(p_138809_, "targets"), (SoundSource)null, (ResourceLocation)null);
      }).then(Commands.literal("*").then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_138797_) -> {
         return stopSound(p_138797_.getSource(), EntityArgument.getPlayers(p_138797_, "targets"), (SoundSource)null, ResourceLocationArgument.getId(p_138797_, "sound"));
      })));

      for(SoundSource soundsource : SoundSource.values()) {
         requiredargumentbuilder.then(Commands.literal(soundsource.getName()).executes((p_138807_) -> {
            return stopSound(p_138807_.getSource(), EntityArgument.getPlayers(p_138807_, "targets"), soundsource, (ResourceLocation)null);
         }).then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_138793_) -> {
            return stopSound(p_138793_.getSource(), EntityArgument.getPlayers(p_138793_, "targets"), soundsource, ResourceLocationArgument.getId(p_138793_, "sound"));
         })));
      }

      p_138795_.register(Commands.literal("stopsound").requires((p_138799_) -> {
         return p_138799_.hasPermission(2);
      }).then(requiredargumentbuilder));
   }

   private static int stopSound(CommandSourceStack p_138801_, Collection<ServerPlayer> p_138802_, @Nullable SoundSource p_138803_, @Nullable ResourceLocation p_138804_) {
      ClientboundStopSoundPacket clientboundstopsoundpacket = new ClientboundStopSoundPacket(p_138804_, p_138803_);

      for(ServerPlayer serverplayer : p_138802_) {
         serverplayer.connection.send(clientboundstopsoundpacket);
      }

      if (p_138803_ != null) {
         if (p_138804_ != null) {
            p_138801_.sendSuccess(new TranslatableComponent("commands.stopsound.success.source.sound", p_138804_, p_138803_.getName()), true);
         } else {
            p_138801_.sendSuccess(new TranslatableComponent("commands.stopsound.success.source.any", p_138803_.getName()), true);
         }
      } else if (p_138804_ != null) {
         p_138801_.sendSuccess(new TranslatableComponent("commands.stopsound.success.sourceless.sound", p_138804_), true);
      } else {
         p_138801_.sendSuccess(new TranslatableComponent("commands.stopsound.success.sourceless.any"), true);
      }

      return p_138802_.size();
   }
}