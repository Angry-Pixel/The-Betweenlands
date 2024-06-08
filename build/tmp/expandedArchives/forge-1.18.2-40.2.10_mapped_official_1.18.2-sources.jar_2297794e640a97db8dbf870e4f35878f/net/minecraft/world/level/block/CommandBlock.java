package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

public class CommandBlock extends BaseEntityBlock implements GameMasterBlock {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;
   private final boolean automatic;

   public CommandBlock(BlockBehaviour.Properties p_153080_, boolean p_153081_) {
      super(p_153080_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CONDITIONAL, Boolean.valueOf(false)));
      this.automatic = p_153081_;
   }

   public BlockEntity newBlockEntity(BlockPos p_153083_, BlockState p_153084_) {
      CommandBlockEntity commandblockentity = new CommandBlockEntity(p_153083_, p_153084_);
      commandblockentity.setAutomatic(this.automatic);
      return commandblockentity;
   }

   public void neighborChanged(BlockState p_51838_, Level p_51839_, BlockPos p_51840_, Block p_51841_, BlockPos p_51842_, boolean p_51843_) {
      if (!p_51839_.isClientSide) {
         BlockEntity blockentity = p_51839_.getBlockEntity(p_51840_);
         if (blockentity instanceof CommandBlockEntity) {
            CommandBlockEntity commandblockentity = (CommandBlockEntity)blockentity;
            boolean flag = p_51839_.hasNeighborSignal(p_51840_);
            boolean flag1 = commandblockentity.isPowered();
            commandblockentity.setPowered(flag);
            if (!flag1 && !commandblockentity.isAutomatic() && commandblockentity.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
               if (flag) {
                  commandblockentity.markConditionMet();
                  p_51839_.scheduleTick(p_51840_, this, 1);
               }

            }
         }
      }
   }

   public void tick(BlockState p_51816_, ServerLevel p_51817_, BlockPos p_51818_, Random p_51819_) {
      BlockEntity blockentity = p_51817_.getBlockEntity(p_51818_);
      if (blockentity instanceof CommandBlockEntity) {
         CommandBlockEntity commandblockentity = (CommandBlockEntity)blockentity;
         BaseCommandBlock basecommandblock = commandblockentity.getCommandBlock();
         boolean flag = !StringUtil.isNullOrEmpty(basecommandblock.getCommand());
         CommandBlockEntity.Mode commandblockentity$mode = commandblockentity.getMode();
         boolean flag1 = commandblockentity.wasConditionMet();
         if (commandblockentity$mode == CommandBlockEntity.Mode.AUTO) {
            commandblockentity.markConditionMet();
            if (flag1) {
               this.execute(p_51816_, p_51817_, p_51818_, basecommandblock, flag);
            } else if (commandblockentity.isConditional()) {
               basecommandblock.setSuccessCount(0);
            }

            if (commandblockentity.isPowered() || commandblockentity.isAutomatic()) {
               p_51817_.scheduleTick(p_51818_, this, 1);
            }
         } else if (commandblockentity$mode == CommandBlockEntity.Mode.REDSTONE) {
            if (flag1) {
               this.execute(p_51816_, p_51817_, p_51818_, basecommandblock, flag);
            } else if (commandblockentity.isConditional()) {
               basecommandblock.setSuccessCount(0);
            }
         }

         p_51817_.updateNeighbourForOutputSignal(p_51818_, this);
      }

   }

   private void execute(BlockState p_51832_, Level p_51833_, BlockPos p_51834_, BaseCommandBlock p_51835_, boolean p_51836_) {
      if (p_51836_) {
         p_51835_.performCommand(p_51833_);
      } else {
         p_51835_.setSuccessCount(0);
      }

      executeChain(p_51833_, p_51834_, p_51832_.getValue(FACING));
   }

   public InteractionResult use(BlockState p_51825_, Level p_51826_, BlockPos p_51827_, Player p_51828_, InteractionHand p_51829_, BlockHitResult p_51830_) {
      BlockEntity blockentity = p_51826_.getBlockEntity(p_51827_);
      if (blockentity instanceof CommandBlockEntity && p_51828_.canUseGameMasterBlocks()) {
         p_51828_.openCommandBlock((CommandBlockEntity)blockentity);
         return InteractionResult.sidedSuccess(p_51826_.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public boolean hasAnalogOutputSignal(BlockState p_51814_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_51821_, Level p_51822_, BlockPos p_51823_) {
      BlockEntity blockentity = p_51822_.getBlockEntity(p_51823_);
      return blockentity instanceof CommandBlockEntity ? ((CommandBlockEntity)blockentity).getCommandBlock().getSuccessCount() : 0;
   }

   public void setPlacedBy(Level p_51804_, BlockPos p_51805_, BlockState p_51806_, LivingEntity p_51807_, ItemStack p_51808_) {
      BlockEntity blockentity = p_51804_.getBlockEntity(p_51805_);
      if (blockentity instanceof CommandBlockEntity) {
         CommandBlockEntity commandblockentity = (CommandBlockEntity)blockentity;
         BaseCommandBlock basecommandblock = commandblockentity.getCommandBlock();
         if (p_51808_.hasCustomHoverName()) {
            basecommandblock.setName(p_51808_.getHoverName());
         }

         if (!p_51804_.isClientSide) {
            if (BlockItem.getBlockEntityData(p_51808_) == null) {
               basecommandblock.setTrackOutput(p_51804_.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
               commandblockentity.setAutomatic(this.automatic);
            }

            if (commandblockentity.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
               boolean flag = p_51804_.hasNeighborSignal(p_51805_);
               commandblockentity.setPowered(flag);
            }
         }

      }
   }

   public RenderShape getRenderShape(BlockState p_51853_) {
      return RenderShape.MODEL;
   }

   public BlockState rotate(BlockState p_51848_, Rotation p_51849_) {
      return p_51848_.setValue(FACING, p_51849_.rotate(p_51848_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_51845_, Mirror p_51846_) {
      return p_51845_.rotate(p_51846_.getRotation(p_51845_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51851_) {
      p_51851_.add(FACING, CONDITIONAL);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_51800_) {
      return this.defaultBlockState().setValue(FACING, p_51800_.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(Level p_51810_, BlockPos p_51811_, Direction p_51812_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_51811_.mutable();
      GameRules gamerules = p_51810_.getGameRules();

      int i;
      BlockState blockstate;
      for(i = gamerules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH); i-- > 0; p_51812_ = blockstate.getValue(FACING)) {
         blockpos$mutableblockpos.move(p_51812_);
         blockstate = p_51810_.getBlockState(blockpos$mutableblockpos);
         Block block = blockstate.getBlock();
         if (!blockstate.is(Blocks.CHAIN_COMMAND_BLOCK)) {
            break;
         }

         BlockEntity blockentity = p_51810_.getBlockEntity(blockpos$mutableblockpos);
         if (!(blockentity instanceof CommandBlockEntity)) {
            break;
         }

         CommandBlockEntity commandblockentity = (CommandBlockEntity)blockentity;
         if (commandblockentity.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
            break;
         }

         if (commandblockentity.isPowered() || commandblockentity.isAutomatic()) {
            BaseCommandBlock basecommandblock = commandblockentity.getCommandBlock();
            if (commandblockentity.markConditionMet()) {
               if (!basecommandblock.performCommand(p_51810_)) {
                  break;
               }

               p_51810_.updateNeighbourForOutputSignal(blockpos$mutableblockpos, block);
            } else if (commandblockentity.isConditional()) {
               basecommandblock.setSuccessCount(0);
            }
         }
      }

      if (i <= 0) {
         int j = Math.max(gamerules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", (int)j);
      }

   }
}