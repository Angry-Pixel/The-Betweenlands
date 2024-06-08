package net.minecraft.world.level.block.piston;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovingPistonBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = PistonHeadBlock.FACING;
   public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

   public MovingPistonBlock(BlockBehaviour.Properties p_60050_) {
      super(p_60050_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, PistonType.DEFAULT));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos p_155879_, BlockState p_155880_) {
      return null;
   }

   public static BlockEntity newMovingBlockEntity(BlockPos p_155882_, BlockState p_155883_, BlockState p_155884_, Direction p_155885_, boolean p_155886_, boolean p_155887_) {
      return new PistonMovingBlockEntity(p_155882_, p_155883_, p_155884_, p_155885_, p_155886_, p_155887_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_155875_, BlockState p_155876_, BlockEntityType<T> p_155877_) {
      return createTickerHelper(p_155877_, BlockEntityType.PISTON, PistonMovingBlockEntity::tick);
   }

   public void onRemove(BlockState p_60077_, Level p_60078_, BlockPos p_60079_, BlockState p_60080_, boolean p_60081_) {
      if (!p_60077_.is(p_60080_.getBlock())) {
         BlockEntity blockentity = p_60078_.getBlockEntity(p_60079_);
         if (blockentity instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)blockentity).finalTick();
         }

      }
   }

   public void destroy(LevelAccessor p_60061_, BlockPos p_60062_, BlockState p_60063_) {
      BlockPos blockpos = p_60062_.relative(p_60063_.getValue(FACING).getOpposite());
      BlockState blockstate = p_60061_.getBlockState(blockpos);
      if (blockstate.getBlock() instanceof PistonBaseBlock && blockstate.getValue(PistonBaseBlock.EXTENDED)) {
         p_60061_.removeBlock(blockpos, false);
      }

   }

   public InteractionResult use(BlockState p_60070_, Level p_60071_, BlockPos p_60072_, Player p_60073_, InteractionHand p_60074_, BlockHitResult p_60075_) {
      if (!p_60071_.isClientSide && p_60071_.getBlockEntity(p_60072_) == null) {
         p_60071_.removeBlock(p_60072_, false);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   public List<ItemStack> getDrops(BlockState p_60089_, LootContext.Builder p_60090_) {
      PistonMovingBlockEntity pistonmovingblockentity = this.getBlockEntity(p_60090_.getLevel(), new BlockPos(p_60090_.getParameter(LootContextParams.ORIGIN)));
      return pistonmovingblockentity == null ? Collections.emptyList() : pistonmovingblockentity.getMovedState().getDrops(p_60090_);
   }

   public VoxelShape getShape(BlockState p_60099_, BlockGetter p_60100_, BlockPos p_60101_, CollisionContext p_60102_) {
      return Shapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState p_60104_, BlockGetter p_60105_, BlockPos p_60106_, CollisionContext p_60107_) {
      PistonMovingBlockEntity pistonmovingblockentity = this.getBlockEntity(p_60105_, p_60106_);
      return pistonmovingblockentity != null ? pistonmovingblockentity.getCollisionShape(p_60105_, p_60106_) : Shapes.empty();
   }

   @Nullable
   private PistonMovingBlockEntity getBlockEntity(BlockGetter p_60054_, BlockPos p_60055_) {
      BlockEntity blockentity = p_60054_.getBlockEntity(p_60055_);
      return blockentity instanceof PistonMovingBlockEntity ? (PistonMovingBlockEntity)blockentity : null;
   }

   public ItemStack getCloneItemStack(BlockGetter p_60057_, BlockPos p_60058_, BlockState p_60059_) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState p_60086_, Rotation p_60087_) {
      return p_60086_.setValue(FACING, p_60087_.rotate(p_60086_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_60083_, Mirror p_60084_) {
      return p_60083_.rotate(p_60084_.getRotation(p_60083_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_60097_) {
      p_60097_.add(FACING, TYPE);
   }

   public boolean isPathfindable(BlockState p_60065_, BlockGetter p_60066_, BlockPos p_60067_, PathComputationType p_60068_) {
      return false;
   }
}