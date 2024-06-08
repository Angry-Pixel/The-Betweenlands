package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
   private static final int MAX_CLONE_AREA = 32768;
   private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_136743_, p_136744_) -> {
      return new TranslatableComponent("commands.clone.toobig", p_136743_, p_136744_);
   });
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.failed"));
   public static final Predicate<BlockInWorld> FILTER_AIR = (p_136762_) -> {
      return !p_136762_.getState().isAir();
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_136730_) {
      p_136730_.register(Commands.literal("clone").requires((p_136734_) -> {
         return p_136734_.hasPermission(2);
      }).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).executes((p_136778_) -> {
         return clone(p_136778_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136778_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136778_, "end"), BlockPosArgument.getLoadedBlockPos(p_136778_, "destination"), (p_180041_) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      }).then(Commands.literal("replace").executes((p_136776_) -> {
         return clone(p_136776_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136776_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136776_, "end"), BlockPosArgument.getLoadedBlockPos(p_136776_, "destination"), (p_180039_) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_136774_) -> {
         return clone(p_136774_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136774_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136774_, "end"), BlockPosArgument.getLoadedBlockPos(p_136774_, "destination"), (p_180037_) -> {
            return true;
         }, CloneCommands.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_136772_) -> {
         return clone(p_136772_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136772_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136772_, "end"), BlockPosArgument.getLoadedBlockPos(p_136772_, "destination"), (p_180035_) -> {
            return true;
         }, CloneCommands.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_136770_) -> {
         return clone(p_136770_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136770_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136770_, "end"), BlockPosArgument.getLoadedBlockPos(p_136770_, "destination"), (p_180033_) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      }))).then(Commands.literal("masked").executes((p_136768_) -> {
         return clone(p_136768_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136768_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136768_, "end"), BlockPosArgument.getLoadedBlockPos(p_136768_, "destination"), FILTER_AIR, CloneCommands.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_136766_) -> {
         return clone(p_136766_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136766_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136766_, "end"), BlockPosArgument.getLoadedBlockPos(p_136766_, "destination"), FILTER_AIR, CloneCommands.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_136764_) -> {
         return clone(p_136764_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136764_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136764_, "end"), BlockPosArgument.getLoadedBlockPos(p_136764_, "destination"), FILTER_AIR, CloneCommands.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_136760_) -> {
         return clone(p_136760_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136760_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136760_, "end"), BlockPosArgument.getLoadedBlockPos(p_136760_, "destination"), FILTER_AIR, CloneCommands.Mode.NORMAL);
      }))).then(Commands.literal("filtered").then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((p_136756_) -> {
         return clone(p_136756_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136756_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136756_, "end"), BlockPosArgument.getLoadedBlockPos(p_136756_, "destination"), BlockPredicateArgument.getBlockPredicate(p_136756_, "filter"), CloneCommands.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_136752_) -> {
         return clone(p_136752_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136752_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136752_, "end"), BlockPosArgument.getLoadedBlockPos(p_136752_, "destination"), BlockPredicateArgument.getBlockPredicate(p_136752_, "filter"), CloneCommands.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_136748_) -> {
         return clone(p_136748_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136748_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136748_, "end"), BlockPosArgument.getLoadedBlockPos(p_136748_, "destination"), BlockPredicateArgument.getBlockPredicate(p_136748_, "filter"), CloneCommands.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_136732_) -> {
         return clone(p_136732_.getSource(), BlockPosArgument.getLoadedBlockPos(p_136732_, "begin"), BlockPosArgument.getLoadedBlockPos(p_136732_, "end"), BlockPosArgument.getLoadedBlockPos(p_136732_, "destination"), BlockPredicateArgument.getBlockPredicate(p_136732_, "filter"), CloneCommands.Mode.NORMAL);
      }))))))));
   }

   private static int clone(CommandSourceStack p_136736_, BlockPos p_136737_, BlockPos p_136738_, BlockPos p_136739_, Predicate<BlockInWorld> p_136740_, CloneCommands.Mode p_136741_) throws CommandSyntaxException {
      BoundingBox boundingbox = BoundingBox.fromCorners(p_136737_, p_136738_);
      BlockPos blockpos = p_136739_.offset(boundingbox.getLength());
      BoundingBox boundingbox1 = BoundingBox.fromCorners(p_136739_, blockpos);
      if (!p_136741_.canOverlap() && boundingbox1.intersects(boundingbox)) {
         throw ERROR_OVERLAP.create();
      } else {
         int i = boundingbox.getXSpan() * boundingbox.getYSpan() * boundingbox.getZSpan();
         if (i > 32768) {
            throw ERROR_AREA_TOO_LARGE.create(32768, i);
         } else {
            ServerLevel serverlevel = p_136736_.getLevel();
            if (serverlevel.hasChunksAt(p_136737_, p_136738_) && serverlevel.hasChunksAt(p_136739_, blockpos)) {
               List<CloneCommands.CloneBlockInfo> list = Lists.newArrayList();
               List<CloneCommands.CloneBlockInfo> list1 = Lists.newArrayList();
               List<CloneCommands.CloneBlockInfo> list2 = Lists.newArrayList();
               Deque<BlockPos> deque = Lists.newLinkedList();
               BlockPos blockpos1 = new BlockPos(boundingbox1.minX() - boundingbox.minX(), boundingbox1.minY() - boundingbox.minY(), boundingbox1.minZ() - boundingbox.minZ());

               for(int j = boundingbox.minZ(); j <= boundingbox.maxZ(); ++j) {
                  for(int k = boundingbox.minY(); k <= boundingbox.maxY(); ++k) {
                     for(int l = boundingbox.minX(); l <= boundingbox.maxX(); ++l) {
                        BlockPos blockpos2 = new BlockPos(l, k, j);
                        BlockPos blockpos3 = blockpos2.offset(blockpos1);
                        BlockInWorld blockinworld = new BlockInWorld(serverlevel, blockpos2, false);
                        BlockState blockstate = blockinworld.getState();
                        if (p_136740_.test(blockinworld)) {
                           BlockEntity blockentity = serverlevel.getBlockEntity(blockpos2);
                           if (blockentity != null) {
                              CompoundTag compoundtag = blockentity.saveWithoutMetadata();
                              list1.add(new CloneCommands.CloneBlockInfo(blockpos3, blockstate, compoundtag));
                              deque.addLast(blockpos2);
                           } else if (!blockstate.isSolidRender(serverlevel, blockpos2) && !blockstate.isCollisionShapeFullBlock(serverlevel, blockpos2)) {
                              list2.add(new CloneCommands.CloneBlockInfo(blockpos3, blockstate, (CompoundTag)null));
                              deque.addFirst(blockpos2);
                           } else {
                              list.add(new CloneCommands.CloneBlockInfo(blockpos3, blockstate, (CompoundTag)null));
                              deque.addLast(blockpos2);
                           }
                        }
                     }
                  }
               }

               if (p_136741_ == CloneCommands.Mode.MOVE) {
                  for(BlockPos blockpos4 : deque) {
                     BlockEntity blockentity1 = serverlevel.getBlockEntity(blockpos4);
                     Clearable.tryClear(blockentity1);
                     serverlevel.setBlock(blockpos4, Blocks.BARRIER.defaultBlockState(), 2);
                  }

                  for(BlockPos blockpos5 : deque) {
                     serverlevel.setBlock(blockpos5, Blocks.AIR.defaultBlockState(), 3);
                  }
               }

               List<CloneCommands.CloneBlockInfo> list3 = Lists.newArrayList();
               list3.addAll(list);
               list3.addAll(list1);
               list3.addAll(list2);
               List<CloneCommands.CloneBlockInfo> list4 = Lists.reverse(list3);

               for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo : list4) {
                  BlockEntity blockentity2 = serverlevel.getBlockEntity(clonecommands$cloneblockinfo.pos);
                  Clearable.tryClear(blockentity2);
                  serverlevel.setBlock(clonecommands$cloneblockinfo.pos, Blocks.BARRIER.defaultBlockState(), 2);
               }

               int i1 = 0;

               for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo1 : list3) {
                  if (serverlevel.setBlock(clonecommands$cloneblockinfo1.pos, clonecommands$cloneblockinfo1.state, 2)) {
                     ++i1;
                  }
               }

               for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo2 : list1) {
                  BlockEntity blockentity3 = serverlevel.getBlockEntity(clonecommands$cloneblockinfo2.pos);
                  if (clonecommands$cloneblockinfo2.tag != null && blockentity3 != null) {
                     blockentity3.load(clonecommands$cloneblockinfo2.tag);
                     blockentity3.setChanged();
                  }

                  serverlevel.setBlock(clonecommands$cloneblockinfo2.pos, clonecommands$cloneblockinfo2.state, 2);
               }

               for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo3 : list4) {
                  serverlevel.blockUpdated(clonecommands$cloneblockinfo3.pos, clonecommands$cloneblockinfo3.state.getBlock());
               }

               serverlevel.getBlockTicks().copyArea(boundingbox, blockpos1);
               if (i1 == 0) {
                  throw ERROR_FAILED.create();
               } else {
                  p_136736_.sendSuccess(new TranslatableComponent("commands.clone.success", i1), true);
                  return i1;
               }
            } else {
               throw BlockPosArgument.ERROR_NOT_LOADED.create();
            }
         }
      }
   }

   static class CloneBlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      @Nullable
      public final CompoundTag tag;

      public CloneBlockInfo(BlockPos p_136783_, BlockState p_136784_, @Nullable CompoundTag p_136785_) {
         this.pos = p_136783_;
         this.state = p_136784_;
         this.tag = p_136785_;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean canOverlap;

      private Mode(boolean p_136795_) {
         this.canOverlap = p_136795_;
      }

      public boolean canOverlap() {
         return this.canOverlap;
      }
   }
}