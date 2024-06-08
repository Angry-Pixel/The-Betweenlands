package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class PlaceFeatureCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.placefeature.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_201836_) {
      p_201836_.register(Commands.literal("placefeature").requires((p_201840_) -> {
         return p_201840_.hasPermission(2);
      }).then(Commands.argument("feature", ResourceKeyArgument.key(Registry.CONFIGURED_FEATURE_REGISTRY)).executes((p_201846_) -> {
         return placeFeature(p_201846_.getSource(), ResourceKeyArgument.getConfiguredFeature(p_201846_, "feature"), new BlockPos(p_201846_.getSource().getPosition()));
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_201838_) -> {
         return placeFeature(p_201838_.getSource(), ResourceKeyArgument.getConfiguredFeature(p_201838_, "feature"), BlockPosArgument.getLoadedBlockPos(p_201838_, "pos"));
      }))));
   }

   public static int placeFeature(CommandSourceStack p_212219_, Holder<ConfiguredFeature<?, ?>> p_212220_, BlockPos p_212221_) throws CommandSyntaxException {
      ServerLevel serverlevel = p_212219_.getLevel();
      ConfiguredFeature<?, ?> configuredfeature = p_212220_.value();
      if (!configuredfeature.place(serverlevel, serverlevel.getChunkSource().getGenerator(), serverlevel.getRandom(), p_212221_)) {
         throw ERROR_FAILED.create();
      } else {
         String s = p_212220_.unwrapKey().map((p_212223_) -> {
            return p_212223_.location().toString();
         }).orElse("[unregistered]");
         p_212219_.sendSuccess(new TranslatableComponent("commands.placefeature.success", s, p_212221_.getX(), p_212221_.getY(), p_212221_.getZ()), true);
         return 1;
      }
   }
}