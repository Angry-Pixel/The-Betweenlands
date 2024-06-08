package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
   private static final int MAX_FILL_AREA = 32768;
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_137392_, p_137393_) -> {
      return new TranslatableComponent("commands.fill.toobig", p_137392_, p_137393_);
   });
   static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.fill.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_137380_) {
      p_137380_.register(Commands.literal("fill").requires((p_137384_) -> {
         return p_137384_.hasPermission(2);
      }).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(Commands.argument("block", BlockStateArgument.block()).executes((p_137405_) -> {
         return fillBlocks(p_137405_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137405_, "from"), BlockPosArgument.getLoadedBlockPos(p_137405_, "to")), BlockStateArgument.getBlock(p_137405_, "block"), FillCommand.Mode.REPLACE, (Predicate<BlockInWorld>)null);
      }).then(Commands.literal("replace").executes((p_137403_) -> {
         return fillBlocks(p_137403_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137403_, "from"), BlockPosArgument.getLoadedBlockPos(p_137403_, "to")), BlockStateArgument.getBlock(p_137403_, "block"), FillCommand.Mode.REPLACE, (Predicate<BlockInWorld>)null);
      }).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((p_137401_) -> {
         return fillBlocks(p_137401_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137401_, "from"), BlockPosArgument.getLoadedBlockPos(p_137401_, "to")), BlockStateArgument.getBlock(p_137401_, "block"), FillCommand.Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(p_137401_, "filter"));
      }))).then(Commands.literal("keep").executes((p_137399_) -> {
         return fillBlocks(p_137399_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137399_, "from"), BlockPosArgument.getLoadedBlockPos(p_137399_, "to")), BlockStateArgument.getBlock(p_137399_, "block"), FillCommand.Mode.REPLACE, (p_180225_) -> {
            return p_180225_.getLevel().isEmptyBlock(p_180225_.getPos());
         });
      })).then(Commands.literal("outline").executes((p_137397_) -> {
         return fillBlocks(p_137397_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137397_, "from"), BlockPosArgument.getLoadedBlockPos(p_137397_, "to")), BlockStateArgument.getBlock(p_137397_, "block"), FillCommand.Mode.OUTLINE, (Predicate<BlockInWorld>)null);
      })).then(Commands.literal("hollow").executes((p_137395_) -> {
         return fillBlocks(p_137395_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137395_, "from"), BlockPosArgument.getLoadedBlockPos(p_137395_, "to")), BlockStateArgument.getBlock(p_137395_, "block"), FillCommand.Mode.HOLLOW, (Predicate<BlockInWorld>)null);
      })).then(Commands.literal("destroy").executes((p_137382_) -> {
         return fillBlocks(p_137382_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137382_, "from"), BlockPosArgument.getLoadedBlockPos(p_137382_, "to")), BlockStateArgument.getBlock(p_137382_, "block"), FillCommand.Mode.DESTROY, (Predicate<BlockInWorld>)null);
      }))))));
   }

   private static int fillBlocks(CommandSourceStack p_137386_, BoundingBox p_137387_, BlockInput p_137388_, FillCommand.Mode p_137389_, @Nullable Predicate<BlockInWorld> p_137390_) throws CommandSyntaxException {
      int i = p_137387_.getXSpan() * p_137387_.getYSpan() * p_137387_.getZSpan();
      if (i > 32768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, i);
      } else {
         List<BlockPos> list = Lists.newArrayList();
         ServerLevel serverlevel = p_137386_.getLevel();
         int j = 0;

         for(BlockPos blockpos : BlockPos.betweenClosed(p_137387_.minX(), p_137387_.minY(), p_137387_.minZ(), p_137387_.maxX(), p_137387_.maxY(), p_137387_.maxZ())) {
            if (p_137390_ == null || p_137390_.test(new BlockInWorld(serverlevel, blockpos, true))) {
               BlockInput blockinput = p_137389_.filter.filter(p_137387_, blockpos, p_137388_, serverlevel);
               if (blockinput != null) {
                  BlockEntity blockentity = serverlevel.getBlockEntity(blockpos);
                  Clearable.tryClear(blockentity);
                  if (blockinput.place(serverlevel, blockpos, 2)) {
                     list.add(blockpos.immutable());
                     ++j;
                  }
               }
            }
         }

         for(BlockPos blockpos1 : list) {
            Block block = serverlevel.getBlockState(blockpos1).getBlock();
            serverlevel.blockUpdated(blockpos1, block);
         }

         if (j == 0) {
            throw ERROR_FAILED.create();
         } else {
            p_137386_.sendSuccess(new TranslatableComponent("commands.fill.success", j), true);
            return j;
         }
      }
   }

   static enum Mode {
      REPLACE((p_137433_, p_137434_, p_137435_, p_137436_) -> {
         return p_137435_;
      }),
      OUTLINE((p_137428_, p_137429_, p_137430_, p_137431_) -> {
         return p_137429_.getX() != p_137428_.minX() && p_137429_.getX() != p_137428_.maxX() && p_137429_.getY() != p_137428_.minY() && p_137429_.getY() != p_137428_.maxY() && p_137429_.getZ() != p_137428_.minZ() && p_137429_.getZ() != p_137428_.maxZ() ? null : p_137430_;
      }),
      HOLLOW((p_137423_, p_137424_, p_137425_, p_137426_) -> {
         return p_137424_.getX() != p_137423_.minX() && p_137424_.getX() != p_137423_.maxX() && p_137424_.getY() != p_137423_.minY() && p_137424_.getY() != p_137423_.maxY() && p_137424_.getZ() != p_137423_.minZ() && p_137424_.getZ() != p_137423_.maxZ() ? FillCommand.HOLLOW_CORE : p_137425_;
      }),
      DESTROY((p_137418_, p_137419_, p_137420_, p_137421_) -> {
         p_137421_.destroyBlock(p_137419_, true);
         return p_137420_;
      });

      public final SetBlockCommand.Filter filter;

      private Mode(SetBlockCommand.Filter p_137416_) {
         this.filter = p_137416_;
      }
   }
}