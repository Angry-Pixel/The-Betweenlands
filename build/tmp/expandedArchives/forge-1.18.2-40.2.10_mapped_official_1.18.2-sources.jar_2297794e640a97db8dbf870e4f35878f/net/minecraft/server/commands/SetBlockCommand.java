package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SetBlockCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.setblock.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_138602_) {
      p_138602_.register(Commands.literal("setblock").requires((p_138606_) -> {
         return p_138606_.hasPermission(2);
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("block", BlockStateArgument.block()).executes((p_138618_) -> {
         return setBlock(p_138618_.getSource(), BlockPosArgument.getLoadedBlockPos(p_138618_, "pos"), BlockStateArgument.getBlock(p_138618_, "block"), SetBlockCommand.Mode.REPLACE, (Predicate<BlockInWorld>)null);
      }).then(Commands.literal("destroy").executes((p_138616_) -> {
         return setBlock(p_138616_.getSource(), BlockPosArgument.getLoadedBlockPos(p_138616_, "pos"), BlockStateArgument.getBlock(p_138616_, "block"), SetBlockCommand.Mode.DESTROY, (Predicate<BlockInWorld>)null);
      })).then(Commands.literal("keep").executes((p_138614_) -> {
         return setBlock(p_138614_.getSource(), BlockPosArgument.getLoadedBlockPos(p_138614_, "pos"), BlockStateArgument.getBlock(p_138614_, "block"), SetBlockCommand.Mode.REPLACE, (p_180517_) -> {
            return p_180517_.getLevel().isEmptyBlock(p_180517_.getPos());
         });
      })).then(Commands.literal("replace").executes((p_138604_) -> {
         return setBlock(p_138604_.getSource(), BlockPosArgument.getLoadedBlockPos(p_138604_, "pos"), BlockStateArgument.getBlock(p_138604_, "block"), SetBlockCommand.Mode.REPLACE, (Predicate<BlockInWorld>)null);
      })))));
   }

   private static int setBlock(CommandSourceStack p_138608_, BlockPos p_138609_, BlockInput p_138610_, SetBlockCommand.Mode p_138611_, @Nullable Predicate<BlockInWorld> p_138612_) throws CommandSyntaxException {
      ServerLevel serverlevel = p_138608_.getLevel();
      if (p_138612_ != null && !p_138612_.test(new BlockInWorld(serverlevel, p_138609_, true))) {
         throw ERROR_FAILED.create();
      } else {
         boolean flag;
         if (p_138611_ == SetBlockCommand.Mode.DESTROY) {
            serverlevel.destroyBlock(p_138609_, true);
            flag = !p_138610_.getState().isAir() || !serverlevel.getBlockState(p_138609_).isAir();
         } else {
            BlockEntity blockentity = serverlevel.getBlockEntity(p_138609_);
            Clearable.tryClear(blockentity);
            flag = true;
         }

         if (flag && !p_138610_.place(serverlevel, p_138609_, 2)) {
            throw ERROR_FAILED.create();
         } else {
            serverlevel.blockUpdated(p_138609_, p_138610_.getState().getBlock());
            p_138608_.sendSuccess(new TranslatableComponent("commands.setblock.success", p_138609_.getX(), p_138609_.getY(), p_138609_.getZ()), true);
            return 1;
         }
      }
   }

   public interface Filter {
      @Nullable
      BlockInput filter(BoundingBox p_138620_, BlockPos p_138621_, BlockInput p_138622_, ServerLevel p_138623_);
   }

   public static enum Mode {
      REPLACE,
      DESTROY;
   }
}