package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ComposterBlock extends Block implements WorldlyContainerHolder {
   public static final int READY = 8;
   public static final int MIN_LEVEL = 0;
   public static final int MAX_LEVEL = 7;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
   public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap<>();
   private static final int AABB_SIDE_THICKNESS = 2;
   private static final VoxelShape OUTER_SHAPE = Shapes.block();
   private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[9], (p_51967_) -> {
      for(int i = 0; i < 8; ++i) {
         p_51967_[i] = Shapes.join(OUTER_SHAPE, Block.box(2.0D, (double)Math.max(2, 1 + i * 2), 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);
      }

      p_51967_[8] = p_51967_[7];
   });

   public static void bootStrap() {
      COMPOSTABLES.defaultReturnValue(-1.0F);
      float f = 0.3F;
      float f1 = 0.5F;
      float f2 = 0.65F;
      float f3 = 0.85F;
      float f4 = 1.0F;
      add(0.3F, Items.JUNGLE_LEAVES);
      add(0.3F, Items.OAK_LEAVES);
      add(0.3F, Items.SPRUCE_LEAVES);
      add(0.3F, Items.DARK_OAK_LEAVES);
      add(0.3F, Items.ACACIA_LEAVES);
      add(0.3F, Items.BIRCH_LEAVES);
      add(0.3F, Items.AZALEA_LEAVES);
      add(0.3F, Items.OAK_SAPLING);
      add(0.3F, Items.SPRUCE_SAPLING);
      add(0.3F, Items.BIRCH_SAPLING);
      add(0.3F, Items.JUNGLE_SAPLING);
      add(0.3F, Items.ACACIA_SAPLING);
      add(0.3F, Items.DARK_OAK_SAPLING);
      add(0.3F, Items.BEETROOT_SEEDS);
      add(0.3F, Items.DRIED_KELP);
      add(0.3F, Items.GRASS);
      add(0.3F, Items.KELP);
      add(0.3F, Items.MELON_SEEDS);
      add(0.3F, Items.PUMPKIN_SEEDS);
      add(0.3F, Items.SEAGRASS);
      add(0.3F, Items.SWEET_BERRIES);
      add(0.3F, Items.GLOW_BERRIES);
      add(0.3F, Items.WHEAT_SEEDS);
      add(0.3F, Items.MOSS_CARPET);
      add(0.3F, Items.SMALL_DRIPLEAF);
      add(0.3F, Items.HANGING_ROOTS);
      add(0.5F, Items.DRIED_KELP_BLOCK);
      add(0.5F, Items.TALL_GRASS);
      add(0.5F, Items.FLOWERING_AZALEA_LEAVES);
      add(0.5F, Items.CACTUS);
      add(0.5F, Items.SUGAR_CANE);
      add(0.5F, Items.VINE);
      add(0.5F, Items.NETHER_SPROUTS);
      add(0.5F, Items.WEEPING_VINES);
      add(0.5F, Items.TWISTING_VINES);
      add(0.5F, Items.MELON_SLICE);
      add(0.5F, Items.GLOW_LICHEN);
      add(0.65F, Items.SEA_PICKLE);
      add(0.65F, Items.LILY_PAD);
      add(0.65F, Items.PUMPKIN);
      add(0.65F, Items.CARVED_PUMPKIN);
      add(0.65F, Items.MELON);
      add(0.65F, Items.APPLE);
      add(0.65F, Items.BEETROOT);
      add(0.65F, Items.CARROT);
      add(0.65F, Items.COCOA_BEANS);
      add(0.65F, Items.POTATO);
      add(0.65F, Items.WHEAT);
      add(0.65F, Items.BROWN_MUSHROOM);
      add(0.65F, Items.RED_MUSHROOM);
      add(0.65F, Items.MUSHROOM_STEM);
      add(0.65F, Items.CRIMSON_FUNGUS);
      add(0.65F, Items.WARPED_FUNGUS);
      add(0.65F, Items.NETHER_WART);
      add(0.65F, Items.CRIMSON_ROOTS);
      add(0.65F, Items.WARPED_ROOTS);
      add(0.65F, Items.SHROOMLIGHT);
      add(0.65F, Items.DANDELION);
      add(0.65F, Items.POPPY);
      add(0.65F, Items.BLUE_ORCHID);
      add(0.65F, Items.ALLIUM);
      add(0.65F, Items.AZURE_BLUET);
      add(0.65F, Items.RED_TULIP);
      add(0.65F, Items.ORANGE_TULIP);
      add(0.65F, Items.WHITE_TULIP);
      add(0.65F, Items.PINK_TULIP);
      add(0.65F, Items.OXEYE_DAISY);
      add(0.65F, Items.CORNFLOWER);
      add(0.65F, Items.LILY_OF_THE_VALLEY);
      add(0.65F, Items.WITHER_ROSE);
      add(0.65F, Items.FERN);
      add(0.65F, Items.SUNFLOWER);
      add(0.65F, Items.LILAC);
      add(0.65F, Items.ROSE_BUSH);
      add(0.65F, Items.PEONY);
      add(0.65F, Items.LARGE_FERN);
      add(0.65F, Items.SPORE_BLOSSOM);
      add(0.65F, Items.AZALEA);
      add(0.65F, Items.MOSS_BLOCK);
      add(0.65F, Items.BIG_DRIPLEAF);
      add(0.85F, Items.HAY_BLOCK);
      add(0.85F, Items.BROWN_MUSHROOM_BLOCK);
      add(0.85F, Items.RED_MUSHROOM_BLOCK);
      add(0.85F, Items.NETHER_WART_BLOCK);
      add(0.85F, Items.WARPED_WART_BLOCK);
      add(0.85F, Items.FLOWERING_AZALEA);
      add(0.85F, Items.BREAD);
      add(0.85F, Items.BAKED_POTATO);
      add(0.85F, Items.COOKIE);
      add(1.0F, Items.CAKE);
      add(1.0F, Items.PUMPKIN_PIE);
   }

   private static void add(float p_51921_, ItemLike p_51922_) {
      COMPOSTABLES.put(p_51922_.asItem(), p_51921_);
   }

   public ComposterBlock(BlockBehaviour.Properties p_51919_) {
      super(p_51919_);
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
   }

   public static void handleFill(Level p_51924_, BlockPos p_51925_, boolean p_51926_) {
      BlockState blockstate = p_51924_.getBlockState(p_51925_);
      p_51924_.playLocalSound((double)p_51925_.getX(), (double)p_51925_.getY(), (double)p_51925_.getZ(), p_51926_ ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      double d0 = blockstate.getShape(p_51924_, p_51925_).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
      double d1 = (double)0.13125F;
      double d2 = (double)0.7375F;
      Random random = p_51924_.getRandom();

      for(int i = 0; i < 10; ++i) {
         double d3 = random.nextGaussian() * 0.02D;
         double d4 = random.nextGaussian() * 0.02D;
         double d5 = random.nextGaussian() * 0.02D;
         p_51924_.addParticle(ParticleTypes.COMPOSTER, (double)p_51925_.getX() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), (double)p_51925_.getY() + d0 + (double)random.nextFloat() * (1.0D - d0), (double)p_51925_.getZ() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), d3, d4, d5);
      }

   }

   public VoxelShape getShape(BlockState p_51973_, BlockGetter p_51974_, BlockPos p_51975_, CollisionContext p_51976_) {
      return SHAPES[p_51973_.getValue(LEVEL)];
   }

   public VoxelShape getInteractionShape(BlockState p_51969_, BlockGetter p_51970_, BlockPos p_51971_) {
      return OUTER_SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState p_51990_, BlockGetter p_51991_, BlockPos p_51992_, CollisionContext p_51993_) {
      return SHAPES[0];
   }

   public void onPlace(BlockState p_51978_, Level p_51979_, BlockPos p_51980_, BlockState p_51981_, boolean p_51982_) {
      if (p_51978_.getValue(LEVEL) == 7) {
         p_51979_.scheduleTick(p_51980_, p_51978_.getBlock(), 20);
      }

   }

   public InteractionResult use(BlockState p_51949_, Level p_51950_, BlockPos p_51951_, Player p_51952_, InteractionHand p_51953_, BlockHitResult p_51954_) {
      int i = p_51949_.getValue(LEVEL);
      ItemStack itemstack = p_51952_.getItemInHand(p_51953_);
      if (i < 8 && COMPOSTABLES.containsKey(itemstack.getItem())) {
         if (i < 7 && !p_51950_.isClientSide) {
            BlockState blockstate = addItem(p_51949_, p_51950_, p_51951_, itemstack);
            p_51950_.levelEvent(1500, p_51951_, p_51949_ != blockstate ? 1 : 0);
            p_51952_.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
            if (!p_51952_.getAbilities().instabuild) {
               itemstack.shrink(1);
            }
         }

         return InteractionResult.sidedSuccess(p_51950_.isClientSide);
      } else if (i == 8) {
         extractProduce(p_51949_, p_51950_, p_51951_);
         return InteractionResult.sidedSuccess(p_51950_.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public static BlockState insertItem(BlockState p_51930_, ServerLevel p_51931_, ItemStack p_51932_, BlockPos p_51933_) {
      int i = p_51930_.getValue(LEVEL);
      if (i < 7 && COMPOSTABLES.containsKey(p_51932_.getItem())) {
         BlockState blockstate = addItem(p_51930_, p_51931_, p_51933_, p_51932_);
         p_51932_.shrink(1);
         return blockstate;
      } else {
         return p_51930_;
      }
   }

   public static BlockState extractProduce(BlockState p_51999_, Level p_52000_, BlockPos p_52001_) {
      if (!p_52000_.isClientSide) {
         float f = 0.7F;
         double d0 = (double)(p_52000_.random.nextFloat() * 0.7F) + (double)0.15F;
         double d1 = (double)(p_52000_.random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
         double d2 = (double)(p_52000_.random.nextFloat() * 0.7F) + (double)0.15F;
         ItemEntity itementity = new ItemEntity(p_52000_, (double)p_52001_.getX() + d0, (double)p_52001_.getY() + d1, (double)p_52001_.getZ() + d2, new ItemStack(Items.BONE_MEAL));
         itementity.setDefaultPickUpDelay();
         p_52000_.addFreshEntity(itementity);
      }

      BlockState blockstate = empty(p_51999_, p_52000_, p_52001_);
      p_52000_.playSound((Player)null, p_52001_, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      return blockstate;
   }

   static BlockState empty(BlockState p_52003_, LevelAccessor p_52004_, BlockPos p_52005_) {
      BlockState blockstate = p_52003_.setValue(LEVEL, Integer.valueOf(0));
      p_52004_.setBlock(p_52005_, blockstate, 3);
      return blockstate;
   }

   static BlockState addItem(BlockState p_51984_, LevelAccessor p_51985_, BlockPos p_51986_, ItemStack p_51987_) {
      int i = p_51984_.getValue(LEVEL);
      float f = COMPOSTABLES.getFloat(p_51987_.getItem());
      if ((i != 0 || !(f > 0.0F)) && !(p_51985_.getRandom().nextDouble() < (double)f)) {
         return p_51984_;
      } else {
         int j = i + 1;
         BlockState blockstate = p_51984_.setValue(LEVEL, Integer.valueOf(j));
         p_51985_.setBlock(p_51986_, blockstate, 3);
         if (j == 7) {
            p_51985_.scheduleTick(p_51986_, p_51984_.getBlock(), 20);
         }

         return blockstate;
      }
   }

   public void tick(BlockState p_51935_, ServerLevel p_51936_, BlockPos p_51937_, Random p_51938_) {
      if (p_51935_.getValue(LEVEL) == 7) {
         p_51936_.setBlock(p_51937_, p_51935_.cycle(LEVEL), 3);
         p_51936_.playSound((Player)null, p_51937_, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public boolean hasAnalogOutputSignal(BlockState p_51928_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_51945_, Level p_51946_, BlockPos p_51947_) {
      return p_51945_.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51965_) {
      p_51965_.add(LEVEL);
   }

   public boolean isPathfindable(BlockState p_51940_, BlockGetter p_51941_, BlockPos p_51942_, PathComputationType p_51943_) {
      return false;
   }

   public WorldlyContainer getContainer(BlockState p_51956_, LevelAccessor p_51957_, BlockPos p_51958_) {
      int i = p_51956_.getValue(LEVEL);
      if (i == 8) {
         return new ComposterBlock.OutputContainer(p_51956_, p_51957_, p_51958_, new ItemStack(Items.BONE_MEAL));
      } else {
         return (WorldlyContainer)(i < 7 ? new ComposterBlock.InputContainer(p_51956_, p_51957_, p_51958_) : new ComposterBlock.EmptyContainer());
      }
   }

   static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
      public EmptyContainer() {
         super(0);
      }

      public int[] getSlotsForFace(Direction p_52012_) {
         return new int[0];
      }

      public boolean canPlaceItemThroughFace(int p_52008_, ItemStack p_52009_, @Nullable Direction p_52010_) {
         return false;
      }

      public boolean canTakeItemThroughFace(int p_52014_, ItemStack p_52015_, Direction p_52016_) {
         return false;
      }
   }

   static class InputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public InputContainer(BlockState p_52022_, LevelAccessor p_52023_, BlockPos p_52024_) {
         super(1);
         this.state = p_52022_;
         this.level = p_52023_;
         this.pos = p_52024_;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction p_52032_) {
         return p_52032_ == Direction.UP ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(int p_52028_, ItemStack p_52029_, @Nullable Direction p_52030_) {
         return !this.changed && p_52030_ == Direction.UP && ComposterBlock.COMPOSTABLES.containsKey(p_52029_.getItem());
      }

      public boolean canTakeItemThroughFace(int p_52034_, ItemStack p_52035_, Direction p_52036_) {
         return false;
      }

      public void setChanged() {
         ItemStack itemstack = this.getItem(0);
         if (!itemstack.isEmpty()) {
            this.changed = true;
            BlockState blockstate = ComposterBlock.addItem(this.state, this.level, this.pos, itemstack);
            this.level.levelEvent(1500, this.pos, blockstate != this.state ? 1 : 0);
            this.removeItemNoUpdate(0);
         }

      }
   }

   static class OutputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public OutputContainer(BlockState p_52042_, LevelAccessor p_52043_, BlockPos p_52044_, ItemStack p_52045_) {
         super(p_52045_);
         this.state = p_52042_;
         this.level = p_52043_;
         this.pos = p_52044_;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction p_52053_) {
         return p_52053_ == Direction.DOWN ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(int p_52049_, ItemStack p_52050_, @Nullable Direction p_52051_) {
         return false;
      }

      public boolean canTakeItemThroughFace(int p_52055_, ItemStack p_52056_, Direction p_52057_) {
         return !this.changed && p_52057_ == Direction.DOWN && p_52056_.is(Items.BONE_MEAL);
      }

      public void setChanged() {
         ComposterBlock.empty(this.state, this.level, this.pos);
         this.changed = true;
      }
   }
}