package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
   public static final VoxelShape SHAPE_BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final VoxelShape SHAPE_POST = Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
   public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
   public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
   public static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_COMMON);
   public static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON);
   public static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(10.666667D, 10.0D, 0.0D, 15.0D, 14.0D, 16.0D), Block.box(6.333333D, 12.0D, 0.0D, 10.666667D, 16.0D, 16.0D), Block.box(2.0D, 14.0D, 0.0D, 6.333333D, 18.0D, 16.0D), SHAPE_COMMON);
   public static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0.0D, 10.0D, 10.666667D, 16.0D, 14.0D, 15.0D), Block.box(0.0D, 12.0D, 6.333333D, 16.0D, 16.0D, 10.666667D), Block.box(0.0D, 14.0D, 2.0D, 16.0D, 18.0D, 6.333333D), SHAPE_COMMON);
   private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

   public LecternBlock(BlockBehaviour.Properties p_54479_) {
      super(p_54479_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(HAS_BOOK, Boolean.valueOf(false)));
   }

   public RenderShape getRenderShape(BlockState p_54559_) {
      return RenderShape.MODEL;
   }

   public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
      return SHAPE_COMMON;
   }

   public boolean useShapeForLightOcclusion(BlockState p_54582_) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54481_) {
      Level level = p_54481_.getLevel();
      ItemStack itemstack = p_54481_.getItemInHand();
      Player player = p_54481_.getPlayer();
      boolean flag = false;
      if (!level.isClientSide && player != null && player.canUseGameMasterBlocks()) {
         CompoundTag compoundtag = BlockItem.getBlockEntityData(itemstack);
         if (compoundtag != null && compoundtag.contains("Book")) {
            flag = true;
         }
      }

      return this.defaultBlockState().setValue(FACING, p_54481_.getHorizontalDirection().getOpposite()).setValue(HAS_BOOK, Boolean.valueOf(flag));
   }

   public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
      return SHAPE_COLLISION;
   }

   public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
      switch((Direction)p_54561_.getValue(FACING)) {
      case NORTH:
         return SHAPE_NORTH;
      case SOUTH:
         return SHAPE_SOUTH;
      case EAST:
         return SHAPE_EAST;
      case WEST:
         return SHAPE_WEST;
      default:
         return SHAPE_COMMON;
      }
   }

   public BlockState rotate(BlockState p_54540_, Rotation p_54541_) {
      return p_54540_.setValue(FACING, p_54541_.rotate(p_54540_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_54537_, Mirror p_54538_) {
      return p_54537_.rotate(p_54538_.getRotation(p_54537_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54543_) {
      p_54543_.add(FACING, POWERED, HAS_BOOK);
   }

   public BlockEntity newBlockEntity(BlockPos p_153573_, BlockState p_153574_) {
      return new LecternBlockEntity(p_153573_, p_153574_);
   }

   public static boolean tryPlaceBook(@Nullable Player p_153567_, Level p_153568_, BlockPos p_153569_, BlockState p_153570_, ItemStack p_153571_) {
      if (!p_153570_.getValue(HAS_BOOK)) {
         if (!p_153568_.isClientSide) {
            placeBook(p_153567_, p_153568_, p_153569_, p_153570_, p_153571_);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void placeBook(@Nullable Player p_153576_, Level p_153577_, BlockPos p_153578_, BlockState p_153579_, ItemStack p_153580_) {
      BlockEntity blockentity = p_153577_.getBlockEntity(p_153578_);
      if (blockentity instanceof LecternBlockEntity) {
         LecternBlockEntity lecternblockentity = (LecternBlockEntity)blockentity;
         lecternblockentity.setBook(p_153580_.split(1));
         resetBookState(p_153577_, p_153578_, p_153579_, true);
         p_153577_.playSound((Player)null, p_153578_, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
         p_153577_.gameEvent(p_153576_, GameEvent.BLOCK_CHANGE, p_153578_);
      }

   }

   public static void resetBookState(Level p_54498_, BlockPos p_54499_, BlockState p_54500_, boolean p_54501_) {
      p_54498_.setBlock(p_54499_, p_54500_.setValue(POWERED, Boolean.valueOf(false)).setValue(HAS_BOOK, Boolean.valueOf(p_54501_)), 3);
      updateBelow(p_54498_, p_54499_, p_54500_);
   }

   public static void signalPageChange(Level p_54489_, BlockPos p_54490_, BlockState p_54491_) {
      changePowered(p_54489_, p_54490_, p_54491_, true);
      p_54489_.scheduleTick(p_54490_, p_54491_.getBlock(), 2);
      p_54489_.levelEvent(1043, p_54490_, 0);
   }

   private static void changePowered(Level p_54554_, BlockPos p_54555_, BlockState p_54556_, boolean p_54557_) {
      p_54554_.setBlock(p_54555_, p_54556_.setValue(POWERED, Boolean.valueOf(p_54557_)), 3);
      updateBelow(p_54554_, p_54555_, p_54556_);
   }

   private static void updateBelow(Level p_54545_, BlockPos p_54546_, BlockState p_54547_) {
      p_54545_.updateNeighborsAt(p_54546_.below(), p_54547_.getBlock());
   }

   public void tick(BlockState p_54505_, ServerLevel p_54506_, BlockPos p_54507_, Random p_54508_) {
      changePowered(p_54506_, p_54507_, p_54505_, false);
   }

   public void onRemove(BlockState p_54531_, Level p_54532_, BlockPos p_54533_, BlockState p_54534_, boolean p_54535_) {
      if (!p_54531_.is(p_54534_.getBlock())) {
         if (p_54531_.getValue(HAS_BOOK)) {
            this.popBook(p_54531_, p_54532_, p_54533_);
         }

         if (p_54531_.getValue(POWERED)) {
            p_54532_.updateNeighborsAt(p_54533_.below(), this);
         }

         super.onRemove(p_54531_, p_54532_, p_54533_, p_54534_, p_54535_);
      }
   }

   private void popBook(BlockState p_54588_, Level p_54589_, BlockPos p_54590_) {
      BlockEntity blockentity = p_54589_.getBlockEntity(p_54590_);
      if (blockentity instanceof LecternBlockEntity) {
         LecternBlockEntity lecternblockentity = (LecternBlockEntity)blockentity;
         Direction direction = p_54588_.getValue(FACING);
         ItemStack itemstack = lecternblockentity.getBook().copy();
         float f = 0.25F * (float)direction.getStepX();
         float f1 = 0.25F * (float)direction.getStepZ();
         ItemEntity itementity = new ItemEntity(p_54589_, (double)p_54590_.getX() + 0.5D + (double)f, (double)(p_54590_.getY() + 1), (double)p_54590_.getZ() + 0.5D + (double)f1, itemstack);
         itementity.setDefaultPickUpDelay();
         p_54589_.addFreshEntity(itementity);
         lecternblockentity.clearContent();
      }

   }

   public boolean isSignalSource(BlockState p_54575_) {
      return true;
   }

   public int getSignal(BlockState p_54515_, BlockGetter p_54516_, BlockPos p_54517_, Direction p_54518_) {
      return p_54515_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_54566_, BlockGetter p_54567_, BlockPos p_54568_, Direction p_54569_) {
      return p_54569_ == Direction.UP && p_54566_.getValue(POWERED) ? 15 : 0;
   }

   public boolean hasAnalogOutputSignal(BlockState p_54503_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_54520_, Level p_54521_, BlockPos p_54522_) {
      if (p_54520_.getValue(HAS_BOOK)) {
         BlockEntity blockentity = p_54521_.getBlockEntity(p_54522_);
         if (blockentity instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)blockentity).getRedstoneSignal();
         }
      }

      return 0;
   }

   public InteractionResult use(BlockState p_54524_, Level p_54525_, BlockPos p_54526_, Player p_54527_, InteractionHand p_54528_, BlockHitResult p_54529_) {
      if (p_54524_.getValue(HAS_BOOK)) {
         if (!p_54525_.isClientSide) {
            this.openScreen(p_54525_, p_54526_, p_54527_);
         }

         return InteractionResult.sidedSuccess(p_54525_.isClientSide);
      } else {
         ItemStack itemstack = p_54527_.getItemInHand(p_54528_);
         return !itemstack.isEmpty() && !itemstack.is(ItemTags.LECTERN_BOOKS) ? InteractionResult.CONSUME : InteractionResult.PASS;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState p_54571_, Level p_54572_, BlockPos p_54573_) {
      return !p_54571_.getValue(HAS_BOOK) ? null : super.getMenuProvider(p_54571_, p_54572_, p_54573_);
   }

   private void openScreen(Level p_54485_, BlockPos p_54486_, Player p_54487_) {
      BlockEntity blockentity = p_54485_.getBlockEntity(p_54486_);
      if (blockentity instanceof LecternBlockEntity) {
         p_54487_.openMenu((LecternBlockEntity)blockentity);
         p_54487_.awardStat(Stats.INTERACT_WITH_LECTERN);
      }

   }

   public boolean isPathfindable(BlockState p_54510_, BlockGetter p_54511_, BlockPos p_54512_, PathComputationType p_54513_) {
      return false;
   }
}