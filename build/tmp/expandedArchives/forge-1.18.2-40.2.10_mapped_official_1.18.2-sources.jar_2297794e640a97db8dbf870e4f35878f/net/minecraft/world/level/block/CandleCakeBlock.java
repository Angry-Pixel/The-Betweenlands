package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleCakeBlock extends AbstractCandleBlock {
   public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
   protected static final float AABB_OFFSET = 1.0F;
   protected static final VoxelShape CAKE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D);
   protected static final VoxelShape CANDLE_SHAPE = Block.box(7.0D, 8.0D, 7.0D, 9.0D, 14.0D, 9.0D);
   protected static final VoxelShape SHAPE = Shapes.or(CAKE_SHAPE, CANDLE_SHAPE);
   private static final Map<Block, CandleCakeBlock> BY_CANDLE = Maps.newHashMap();
   private static final Iterable<Vec3> PARTICLE_OFFSETS = ImmutableList.of(new Vec3(0.5D, 1.0D, 0.5D));

   public CandleCakeBlock(Block p_152859_, BlockBehaviour.Properties p_152860_) {
      super(p_152860_);
      this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)));
      BY_CANDLE.put(p_152859_, this);
   }

   protected Iterable<Vec3> getParticleOffsets(BlockState p_152868_) {
      return PARTICLE_OFFSETS;
   }

   public VoxelShape getShape(BlockState p_152875_, BlockGetter p_152876_, BlockPos p_152877_, CollisionContext p_152878_) {
      return SHAPE;
   }

   public InteractionResult use(BlockState p_152884_, Level p_152885_, BlockPos p_152886_, Player p_152887_, InteractionHand p_152888_, BlockHitResult p_152889_) {
      ItemStack itemstack = p_152887_.getItemInHand(p_152888_);
      if (!itemstack.is(Items.FLINT_AND_STEEL) && !itemstack.is(Items.FIRE_CHARGE)) {
         if (candleHit(p_152889_) && p_152887_.getItemInHand(p_152888_).isEmpty() && p_152884_.getValue(LIT)) {
            extinguish(p_152887_, p_152884_, p_152885_, p_152886_);
            return InteractionResult.sidedSuccess(p_152885_.isClientSide);
         } else {
            InteractionResult interactionresult = CakeBlock.eat(p_152885_, p_152886_, Blocks.CAKE.defaultBlockState(), p_152887_);
            if (interactionresult.consumesAction()) {
               dropResources(p_152884_, p_152885_, p_152886_);
            }

            return interactionresult;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private static boolean candleHit(BlockHitResult p_152907_) {
      return p_152907_.getLocation().y - (double)p_152907_.getBlockPos().getY() > 0.5D;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_152905_) {
      p_152905_.add(LIT);
   }

   public ItemStack getCloneItemStack(BlockGetter p_152862_, BlockPos p_152863_, BlockState p_152864_) {
      return new ItemStack(Blocks.CAKE);
   }

   public BlockState updateShape(BlockState p_152898_, Direction p_152899_, BlockState p_152900_, LevelAccessor p_152901_, BlockPos p_152902_, BlockPos p_152903_) {
      return p_152899_ == Direction.DOWN && !p_152898_.canSurvive(p_152901_, p_152902_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_152898_, p_152899_, p_152900_, p_152901_, p_152902_, p_152903_);
   }

   public boolean canSurvive(BlockState p_152891_, LevelReader p_152892_, BlockPos p_152893_) {
      return p_152892_.getBlockState(p_152893_.below()).getMaterial().isSolid();
   }

   public int getAnalogOutputSignal(BlockState p_152880_, Level p_152881_, BlockPos p_152882_) {
      return CakeBlock.FULL_CAKE_SIGNAL;
   }

   public boolean hasAnalogOutputSignal(BlockState p_152909_) {
      return true;
   }

   public boolean isPathfindable(BlockState p_152870_, BlockGetter p_152871_, BlockPos p_152872_, PathComputationType p_152873_) {
      return false;
   }

   public static BlockState byCandle(Block p_152866_) {
      return BY_CANDLE.get(p_152866_).defaultBlockState();
   }

   public static boolean canLight(BlockState p_152911_) {
      return p_152911_.is(BlockTags.CANDLE_CAKES, (p_152896_) -> {
         return p_152896_.hasProperty(LIT) && !p_152911_.getValue(LIT);
      });
   }
}