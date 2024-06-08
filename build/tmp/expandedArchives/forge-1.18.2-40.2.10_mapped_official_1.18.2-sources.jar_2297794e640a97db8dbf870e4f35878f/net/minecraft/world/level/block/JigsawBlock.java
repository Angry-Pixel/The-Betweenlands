package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;

public class JigsawBlock extends Block implements EntityBlock, GameMasterBlock {
   public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

   public JigsawBlock(BlockBehaviour.Properties p_54225_) {
      super(p_54225_);
      this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54244_) {
      p_54244_.add(ORIENTATION);
   }

   public BlockState rotate(BlockState p_54241_, Rotation p_54242_) {
      return p_54241_.setValue(ORIENTATION, p_54242_.rotation().rotate(p_54241_.getValue(ORIENTATION)));
   }

   public BlockState mirror(BlockState p_54238_, Mirror p_54239_) {
      return p_54238_.setValue(ORIENTATION, p_54239_.rotation().rotate(p_54238_.getValue(ORIENTATION)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54227_) {
      Direction direction = p_54227_.getClickedFace();
      Direction direction1;
      if (direction.getAxis() == Direction.Axis.Y) {
         direction1 = p_54227_.getHorizontalDirection().getOpposite();
      } else {
         direction1 = Direction.UP;
      }

      return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(direction, direction1));
   }

   public BlockEntity newBlockEntity(BlockPos p_153448_, BlockState p_153449_) {
      return new JigsawBlockEntity(p_153448_, p_153449_);
   }

   public InteractionResult use(BlockState p_54231_, Level p_54232_, BlockPos p_54233_, Player p_54234_, InteractionHand p_54235_, BlockHitResult p_54236_) {
      BlockEntity blockentity = p_54232_.getBlockEntity(p_54233_);
      if (blockentity instanceof JigsawBlockEntity && p_54234_.canUseGameMasterBlocks()) {
         p_54234_.openJigsawBlock((JigsawBlockEntity)blockentity);
         return InteractionResult.sidedSuccess(p_54232_.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean canAttach(StructureTemplate.StructureBlockInfo p_54246_, StructureTemplate.StructureBlockInfo p_54247_) {
      Direction direction = getFrontFacing(p_54246_.state);
      Direction direction1 = getFrontFacing(p_54247_.state);
      Direction direction2 = getTopFacing(p_54246_.state);
      Direction direction3 = getTopFacing(p_54247_.state);
      JigsawBlockEntity.JointType jigsawblockentity$jointtype = JigsawBlockEntity.JointType.byName(p_54246_.nbt.getString("joint")).orElseGet(() -> {
         return direction.getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
      });
      boolean flag = jigsawblockentity$jointtype == JigsawBlockEntity.JointType.ROLLABLE;
      return direction == direction1.getOpposite() && (flag || direction2 == direction3) && p_54246_.nbt.getString("target").equals(p_54247_.nbt.getString("name"));
   }

   public static Direction getFrontFacing(BlockState p_54251_) {
      return p_54251_.getValue(ORIENTATION).front();
   }

   public static Direction getTopFacing(BlockState p_54253_) {
      return p_54253_.getValue(ORIENTATION).top();
   }
}