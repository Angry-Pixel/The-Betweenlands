package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeehiveBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.LEVEL_HONEY;
   public static final int MAX_HONEY_LEVELS = 5;
   private static final int SHEARED_HONEYCOMB_COUNT = 3;

   public BeehiveBlock(BlockBehaviour.Properties p_49568_) {
      super(p_49568_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HONEY_LEVEL, Integer.valueOf(0)).setValue(FACING, Direction.NORTH));
   }

   public boolean hasAnalogOutputSignal(BlockState p_49618_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_49620_, Level p_49621_, BlockPos p_49622_) {
      return p_49620_.getValue(HONEY_LEVEL);
   }
   // Forge: Fixed MC-227255 Beehives and bee nests do not rotate/mirror correctly in structure blocks
   @Override public BlockState rotate(BlockState blockState, net.minecraft.world.level.block.Rotation rotation) { return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING))); }
   @Override public BlockState mirror(BlockState blockState, net.minecraft.world.level.block.Mirror mirror) { return blockState.rotate(mirror.getRotation(blockState.getValue(FACING))); }

   public void playerDestroy(Level p_49584_, Player p_49585_, BlockPos p_49586_, BlockState p_49587_, @Nullable BlockEntity p_49588_, ItemStack p_49589_) {
      super.playerDestroy(p_49584_, p_49585_, p_49586_, p_49587_, p_49588_, p_49589_);
      if (!p_49584_.isClientSide && p_49588_ instanceof BeehiveBlockEntity) {
         BeehiveBlockEntity beehiveblockentity = (BeehiveBlockEntity)p_49588_;
         if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, p_49589_) == 0) {
            beehiveblockentity.emptyAllLivingFromHive(p_49585_, p_49587_, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            p_49584_.updateNeighbourForOutputSignal(p_49586_, this);
            this.angerNearbyBees(p_49584_, p_49586_);
         }

         CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayer)p_49585_, p_49587_, p_49589_, beehiveblockentity.getOccupantCount());
      }

   }

   private void angerNearbyBees(Level p_49650_, BlockPos p_49651_) {
      List<Bee> list = p_49650_.getEntitiesOfClass(Bee.class, (new AABB(p_49651_)).inflate(8.0D, 6.0D, 8.0D));
      if (!list.isEmpty()) {
         List<Player> list1 = p_49650_.getEntitiesOfClass(Player.class, (new AABB(p_49651_)).inflate(8.0D, 6.0D, 8.0D));
         if (list1.isEmpty()) return; //Forge: Prevent Error when no players are around.
         int i = list1.size();

         for(Bee bee : list) {
            if (bee.getTarget() == null) {
               bee.setTarget(list1.get(p_49650_.random.nextInt(i)));
            }
         }
      }

   }

   public static void dropHoneycomb(Level p_49601_, BlockPos p_49602_) {
      popResource(p_49601_, p_49602_, new ItemStack(Items.HONEYCOMB, 3));
   }

   public InteractionResult use(BlockState p_49624_, Level p_49625_, BlockPos p_49626_, Player p_49627_, InteractionHand p_49628_, BlockHitResult p_49629_) {
      ItemStack itemstack = p_49627_.getItemInHand(p_49628_);
      int i = p_49624_.getValue(HONEY_LEVEL);
      boolean flag = false;
      if (i >= 5) {
         Item item = itemstack.getItem();
         if (itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SHEARS_HARVEST)) {
            p_49625_.playSound(p_49627_, p_49627_.getX(), p_49627_.getY(), p_49627_.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
            dropHoneycomb(p_49625_, p_49626_);
            itemstack.hurtAndBreak(1, p_49627_, (p_49571_) -> {
               p_49571_.broadcastBreakEvent(p_49628_);
            });
            flag = true;
            p_49625_.gameEvent(p_49627_, GameEvent.SHEAR, p_49626_);
         } else if (itemstack.is(Items.GLASS_BOTTLE)) {
            itemstack.shrink(1);
            p_49625_.playSound(p_49627_, p_49627_.getX(), p_49627_.getY(), p_49627_.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            if (itemstack.isEmpty()) {
               p_49627_.setItemInHand(p_49628_, new ItemStack(Items.HONEY_BOTTLE));
            } else if (!p_49627_.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
               p_49627_.drop(new ItemStack(Items.HONEY_BOTTLE), false);
            }

            flag = true;
            p_49625_.gameEvent(p_49627_, GameEvent.FLUID_PICKUP, p_49626_);
         }

         if (!p_49625_.isClientSide() && flag) {
            p_49627_.awardStat(Stats.ITEM_USED.get(item));
         }
      }

      if (flag) {
         if (!CampfireBlock.isSmokeyPos(p_49625_, p_49626_)) {
            if (this.hiveContainsBees(p_49625_, p_49626_)) {
               this.angerNearbyBees(p_49625_, p_49626_);
            }

            this.releaseBeesAndResetHoneyLevel(p_49625_, p_49624_, p_49626_, p_49627_, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         } else {
            this.resetHoneyLevel(p_49625_, p_49624_, p_49626_);
         }

         return InteractionResult.sidedSuccess(p_49625_.isClientSide);
      } else {
         return super.use(p_49624_, p_49625_, p_49626_, p_49627_, p_49628_, p_49629_);
      }
   }

   private boolean hiveContainsBees(Level p_49655_, BlockPos p_49656_) {
      BlockEntity blockentity = p_49655_.getBlockEntity(p_49656_);
      if (blockentity instanceof BeehiveBlockEntity) {
         BeehiveBlockEntity beehiveblockentity = (BeehiveBlockEntity)blockentity;
         return !beehiveblockentity.isEmpty();
      } else {
         return false;
      }
   }

   public void releaseBeesAndResetHoneyLevel(Level p_49595_, BlockState p_49596_, BlockPos p_49597_, @Nullable Player p_49598_, BeehiveBlockEntity.BeeReleaseStatus p_49599_) {
      this.resetHoneyLevel(p_49595_, p_49596_, p_49597_);
      BlockEntity blockentity = p_49595_.getBlockEntity(p_49597_);
      if (blockentity instanceof BeehiveBlockEntity) {
         BeehiveBlockEntity beehiveblockentity = (BeehiveBlockEntity)blockentity;
         beehiveblockentity.emptyAllLivingFromHive(p_49598_, p_49596_, p_49599_);
      }

   }

   public void resetHoneyLevel(Level p_49591_, BlockState p_49592_, BlockPos p_49593_) {
      p_49591_.setBlock(p_49593_, p_49592_.setValue(HONEY_LEVEL, Integer.valueOf(0)), 3);
   }

   public void animateTick(BlockState p_49631_, Level p_49632_, BlockPos p_49633_, Random p_49634_) {
      if (p_49631_.getValue(HONEY_LEVEL) >= 5) {
         for(int i = 0; i < p_49634_.nextInt(1) + 1; ++i) {
            this.trySpawnDripParticles(p_49632_, p_49633_, p_49631_);
         }
      }

   }

   private void trySpawnDripParticles(Level p_49604_, BlockPos p_49605_, BlockState p_49606_) {
      if (p_49606_.getFluidState().isEmpty() && !(p_49604_.random.nextFloat() < 0.3F)) {
         VoxelShape voxelshape = p_49606_.getCollisionShape(p_49604_, p_49605_);
         double d0 = voxelshape.max(Direction.Axis.Y);
         if (d0 >= 1.0D && !p_49606_.is(BlockTags.IMPERMEABLE)) {
            double d1 = voxelshape.min(Direction.Axis.Y);
            if (d1 > 0.0D) {
               this.spawnParticle(p_49604_, p_49605_, voxelshape, (double)p_49605_.getY() + d1 - 0.05D);
            } else {
               BlockPos blockpos = p_49605_.below();
               BlockState blockstate = p_49604_.getBlockState(blockpos);
               VoxelShape voxelshape1 = blockstate.getCollisionShape(p_49604_, blockpos);
               double d2 = voxelshape1.max(Direction.Axis.Y);
               if ((d2 < 1.0D || !blockstate.isCollisionShapeFullBlock(p_49604_, blockpos)) && blockstate.getFluidState().isEmpty()) {
                  this.spawnParticle(p_49604_, p_49605_, voxelshape, (double)p_49605_.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(Level p_49613_, BlockPos p_49614_, VoxelShape p_49615_, double p_49616_) {
      this.spawnFluidParticle(p_49613_, (double)p_49614_.getX() + p_49615_.min(Direction.Axis.X), (double)p_49614_.getX() + p_49615_.max(Direction.Axis.X), (double)p_49614_.getZ() + p_49615_.min(Direction.Axis.Z), (double)p_49614_.getZ() + p_49615_.max(Direction.Axis.Z), p_49616_);
   }

   private void spawnFluidParticle(Level p_49577_, double p_49578_, double p_49579_, double p_49580_, double p_49581_, double p_49582_) {
      p_49577_.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp(p_49577_.random.nextDouble(), p_49578_, p_49579_), p_49582_, Mth.lerp(p_49577_.random.nextDouble(), p_49580_, p_49581_), 0.0D, 0.0D, 0.0D);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_49573_) {
      return this.defaultBlockState().setValue(FACING, p_49573_.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49646_) {
      p_49646_.add(HONEY_LEVEL, FACING);
   }

   public RenderShape getRenderShape(BlockState p_49653_) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos p_152184_, BlockState p_152185_) {
      return new BeehiveBlockEntity(p_152184_, p_152185_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return p_152180_.isClientSide ? null : createTickerHelper(p_152182_, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
   }

   public void playerWillDestroy(Level p_49608_, BlockPos p_49609_, BlockState p_49610_, Player p_49611_) {
      if (!p_49608_.isClientSide && p_49611_.isCreative() && p_49608_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         BlockEntity blockentity = p_49608_.getBlockEntity(p_49609_);
         if (blockentity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveblockentity = (BeehiveBlockEntity)blockentity;
            ItemStack itemstack = new ItemStack(this);
            int i = p_49610_.getValue(HONEY_LEVEL);
            boolean flag = !beehiveblockentity.isEmpty();
            if (flag || i > 0) {
               if (flag) {
                  CompoundTag compoundtag = new CompoundTag();
                  compoundtag.put("Bees", beehiveblockentity.writeBees());
                  BlockItem.setBlockEntityData(itemstack, BlockEntityType.BEEHIVE, compoundtag);
               }

               CompoundTag compoundtag1 = new CompoundTag();
               compoundtag1.putInt("honey_level", i);
               itemstack.addTagElement("BlockStateTag", compoundtag1);
               ItemEntity itementity = new ItemEntity(p_49608_, (double)p_49609_.getX(), (double)p_49609_.getY(), (double)p_49609_.getZ(), itemstack);
               itementity.setDefaultPickUpDelay();
               p_49608_.addFreshEntity(itementity);
            }
         }
      }

      super.playerWillDestroy(p_49608_, p_49609_, p_49610_, p_49611_);
   }

   public List<ItemStack> getDrops(BlockState p_49636_, LootContext.Builder p_49637_) {
      Entity entity = p_49637_.getOptionalParameter(LootContextParams.THIS_ENTITY);
      if (entity instanceof PrimedTnt || entity instanceof Creeper || entity instanceof WitherSkull || entity instanceof WitherBoss || entity instanceof MinecartTNT) {
         BlockEntity blockentity = p_49637_.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
         if (blockentity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveblockentity = (BeehiveBlockEntity)blockentity;
            beehiveblockentity.emptyAllLivingFromHive((Player)null, p_49636_, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         }
      }

      return super.getDrops(p_49636_, p_49637_);
   }

   public BlockState updateShape(BlockState p_49639_, Direction p_49640_, BlockState p_49641_, LevelAccessor p_49642_, BlockPos p_49643_, BlockPos p_49644_) {
      if (p_49642_.getBlockState(p_49644_).getBlock() instanceof FireBlock) {
         BlockEntity blockentity = p_49642_.getBlockEntity(p_49643_);
         if (blockentity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveblockentity = (BeehiveBlockEntity)blockentity;
            beehiveblockentity.emptyAllLivingFromHive((Player)null, p_49639_, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         }
      }

      return super.updateShape(p_49639_, p_49640_, p_49641_, p_49642_, p_49643_, p_49644_);
   }
}
