package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LightningRodBlock extends RodBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final int ACTIVATION_TICKS = 8;
   public static final int RANGE = 128;
   private static final int SPARK_CYCLE = 200;

   public LightningRodBlock(BlockBehaviour.Properties p_153709_) {
      super(p_153709_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_153711_) {
      FluidState fluidstate = p_153711_.getLevel().getFluidState(p_153711_.getClickedPos());
      boolean flag = fluidstate.getType() == Fluids.WATER;
      return this.defaultBlockState().setValue(FACING, p_153711_.getClickedFace()).setValue(WATERLOGGED, Boolean.valueOf(flag));
   }

   public BlockState updateShape(BlockState p_153739_, Direction p_153740_, BlockState p_153741_, LevelAccessor p_153742_, BlockPos p_153743_, BlockPos p_153744_) {
      if (p_153739_.getValue(WATERLOGGED)) {
         p_153742_.scheduleTick(p_153743_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153742_));
      }

      return super.updateShape(p_153739_, p_153740_, p_153741_, p_153742_, p_153743_, p_153744_);
   }

   public FluidState getFluidState(BlockState p_153759_) {
      return p_153759_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_153759_);
   }

   public int getSignal(BlockState p_153723_, BlockGetter p_153724_, BlockPos p_153725_, Direction p_153726_) {
      return p_153723_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_153748_, BlockGetter p_153749_, BlockPos p_153750_, Direction p_153751_) {
      return p_153748_.getValue(POWERED) && p_153748_.getValue(FACING) == p_153751_ ? 15 : 0;
   }

   public void onLightningStrike(BlockState p_153761_, Level p_153762_, BlockPos p_153763_) {
      p_153762_.setBlock(p_153763_, p_153761_.setValue(POWERED, Boolean.valueOf(true)), 3);
      this.updateNeighbours(p_153761_, p_153762_, p_153763_);
      p_153762_.scheduleTick(p_153763_, this, 8);
      p_153762_.levelEvent(3002, p_153763_, p_153761_.getValue(FACING).getAxis().ordinal());
   }

   private void updateNeighbours(BlockState p_153765_, Level p_153766_, BlockPos p_153767_) {
      p_153766_.updateNeighborsAt(p_153767_.relative(p_153765_.getValue(FACING).getOpposite()), this);
   }

   public void tick(BlockState p_153718_, ServerLevel p_153719_, BlockPos p_153720_, Random p_153721_) {
      p_153719_.setBlock(p_153720_, p_153718_.setValue(POWERED, Boolean.valueOf(false)), 3);
      this.updateNeighbours(p_153718_, p_153719_, p_153720_);
   }

   public void animateTick(BlockState p_153734_, Level p_153735_, BlockPos p_153736_, Random p_153737_) {
      if (p_153735_.isThundering() && (long)p_153735_.random.nextInt(200) <= p_153735_.getGameTime() % 200L && p_153736_.getY() == p_153735_.getHeight(Heightmap.Types.WORLD_SURFACE, p_153736_.getX(), p_153736_.getZ()) - 1) {
         ParticleUtils.spawnParticlesAlongAxis(p_153734_.getValue(FACING).getAxis(), p_153735_, p_153736_, 0.125D, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(1, 2));
      }
   }

   public void onRemove(BlockState p_153728_, Level p_153729_, BlockPos p_153730_, BlockState p_153731_, boolean p_153732_) {
      if (!p_153728_.is(p_153731_.getBlock())) {
         if (p_153728_.getValue(POWERED)) {
            this.updateNeighbours(p_153728_, p_153729_, p_153730_);
         }

         super.onRemove(p_153728_, p_153729_, p_153730_, p_153731_, p_153732_);
      }
   }

   public void onPlace(BlockState p_153753_, Level p_153754_, BlockPos p_153755_, BlockState p_153756_, boolean p_153757_) {
      if (!p_153753_.is(p_153756_.getBlock())) {
         if (p_153753_.getValue(POWERED) && !p_153754_.getBlockTicks().hasScheduledTick(p_153755_, this)) {
            p_153754_.setBlock(p_153755_, p_153753_.setValue(POWERED, Boolean.valueOf(false)), 18);
         }

      }
   }

   public void onProjectileHit(Level p_153713_, BlockState p_153714_, BlockHitResult p_153715_, Projectile p_153716_) {
      if (p_153713_.isThundering() && p_153716_ instanceof ThrownTrident && ((ThrownTrident)p_153716_).isChanneling()) {
         BlockPos blockpos = p_153715_.getBlockPos();
         if (p_153713_.canSeeSky(blockpos)) {
            LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(p_153713_);
            lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos.above()));
            Entity entity = p_153716_.getOwner();
            lightningbolt.setCause(entity instanceof ServerPlayer ? (ServerPlayer)entity : null);
            p_153713_.addFreshEntity(lightningbolt);
            p_153713_.playSound((Player)null, blockpos, SoundEvents.TRIDENT_THUNDER, SoundSource.WEATHER, 5.0F, 1.0F);
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153746_) {
      p_153746_.add(FACING, POWERED, WATERLOGGED);
   }

   public boolean isSignalSource(BlockState p_153769_) {
      return true;
   }
}