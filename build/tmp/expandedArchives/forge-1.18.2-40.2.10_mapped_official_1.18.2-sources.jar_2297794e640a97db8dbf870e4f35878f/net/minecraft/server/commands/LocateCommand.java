package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

public class LocateCommand {
   private static final DynamicCommandExceptionType ERROR_FAILED = new DynamicCommandExceptionType((p_201831_) -> {
      return new TranslatableComponent("commands.locate.failed", p_201831_);
   });
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((p_207534_) -> {
      return new TranslatableComponent("commands.locate.invalid", p_207534_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_137859_) {
      p_137859_.register(Commands.literal("locate").requires((p_207513_) -> {
         return p_207513_.hasPermission(2);
      }).then(Commands.argument("structure", ResourceOrTagLocationArgument.resourceOrTag(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY)).executes((p_207508_) -> {
         return locate(p_207508_.getSource(), ResourceOrTagLocationArgument.getStructureFeature(p_207508_, "structure"));
      })));
   }

   private static int locate(CommandSourceStack p_207515_, ResourceOrTagLocationArgument.Result<ConfiguredStructureFeature<?, ?>> p_207516_) throws CommandSyntaxException {
      Registry<ConfiguredStructureFeature<?, ?>> registry = p_207515_.getLevel().registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
      HolderSet<ConfiguredStructureFeature<?, ?>> holderset = p_207516_.unwrap().map((p_207532_) -> {
         return registry.getHolder(p_207532_).map((p_207529_) -> {
            return HolderSet.direct(p_207529_);
         });
      }, registry::getTag).orElseThrow(() -> {
         return ERROR_INVALID.create(p_207516_.asPrintable());
      });
      BlockPos blockpos = new BlockPos(p_207515_.getPosition());
      ServerLevel serverlevel = p_207515_.getLevel();
      Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair = serverlevel.getChunkSource().getGenerator().findNearestMapFeature(serverlevel, holderset, blockpos, 100, false);
      if (pair == null) {
         throw ERROR_FAILED.create(p_207516_.asPrintable());
      } else {
         return showLocateResult(p_207515_, p_207516_, blockpos, pair, "commands.locate.success");
      }
   }

   public static int showLocateResult(CommandSourceStack p_207518_, ResourceOrTagLocationArgument.Result<?> p_207519_, BlockPos p_207520_, Pair<BlockPos, ? extends Holder<?>> p_207521_, String p_207522_) {
      BlockPos blockpos = p_207521_.getFirst();
      String s = p_207519_.unwrap().map((p_207538_) -> {
         return p_207538_.location().toString();
      }, (p_207511_) -> {
         return "#" + p_207511_.location() + " (" + (String)p_207521_.getSecond().unwrapKey().map((p_207536_) -> {
            return p_207536_.location().toString();
         }).orElse("[unregistered]") + ")";
      });
      int i = Mth.floor(dist(p_207520_.getX(), p_207520_.getZ(), blockpos.getX(), blockpos.getZ()));
      Component component = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", blockpos.getX(), "~", blockpos.getZ())).withStyle((p_207527_) -> {
         return p_207527_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockpos.getX() + " ~ " + blockpos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip")));
      });
      p_207518_.sendSuccess(new TranslatableComponent(p_207522_, s, component, i), false);
      return i;
   }

   private static float dist(int p_137854_, int p_137855_, int p_137856_, int p_137857_) {
      int i = p_137856_ - p_137854_;
      int j = p_137857_ - p_137855_;
      return Mth.sqrt((float)(i * i + j * j));
   }
}