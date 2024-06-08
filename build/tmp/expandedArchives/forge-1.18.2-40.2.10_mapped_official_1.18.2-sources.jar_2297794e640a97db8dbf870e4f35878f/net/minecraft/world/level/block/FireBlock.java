package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FireBlock extends BaseFireBlock {
   public static final int MAX_AGE = 15;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty UP = PipeBlock.UP;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_53467_) -> {
      return p_53467_.getKey() != Direction.DOWN;
   }).collect(Util.toMap());
   private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   private final Map<BlockState, VoxelShape> shapesCache;
   private static final int FLAME_INSTANT = 60;
   private static final int FLAME_EASY = 30;
   private static final int FLAME_MEDIUM = 15;
   private static final int FLAME_HARD = 5;
   private static final int BURN_INSTANT = 100;
   private static final int BURN_EASY = 60;
   private static final int BURN_MEDIUM = 20;
   private static final int BURN_HARD = 5;
   private final Object2IntMap<Block> flameOdds = new Object2IntOpenHashMap<>();
   private final Object2IntMap<Block> burnOdds = new Object2IntOpenHashMap<>();

   public FireBlock(BlockBehaviour.Properties p_53425_) {
      super(p_53425_, 1.0F);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(UP, Boolean.valueOf(false)));
      this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().filter((p_53497_) -> {
         return p_53497_.getValue(AGE) == 0;
      }).collect(Collectors.toMap(Function.identity(), FireBlock::calculateShape)));
   }

   private static VoxelShape calculateShape(BlockState p_53491_) {
      VoxelShape voxelshape = Shapes.empty();
      if (p_53491_.getValue(UP)) {
         voxelshape = UP_AABB;
      }

      if (p_53491_.getValue(NORTH)) {
         voxelshape = Shapes.or(voxelshape, NORTH_AABB);
      }

      if (p_53491_.getValue(SOUTH)) {
         voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
      }

      if (p_53491_.getValue(EAST)) {
         voxelshape = Shapes.or(voxelshape, EAST_AABB);
      }

      if (p_53491_.getValue(WEST)) {
         voxelshape = Shapes.or(voxelshape, WEST_AABB);
      }

      return voxelshape.isEmpty() ? DOWN_AABB : voxelshape;
   }

   public BlockState updateShape(BlockState p_53458_, Direction p_53459_, BlockState p_53460_, LevelAccessor p_53461_, BlockPos p_53462_, BlockPos p_53463_) {
      return this.canSurvive(p_53458_, p_53461_, p_53462_) ? this.getStateWithAge(p_53461_, p_53462_, p_53458_.getValue(AGE)) : Blocks.AIR.defaultBlockState();
   }

   public VoxelShape getShape(BlockState p_53474_, BlockGetter p_53475_, BlockPos p_53476_, CollisionContext p_53477_) {
      return this.shapesCache.get(p_53474_.setValue(AGE, Integer.valueOf(0)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_53427_) {
      return this.getStateForPlacement(p_53427_.getLevel(), p_53427_.getClickedPos());
   }

   protected BlockState getStateForPlacement(BlockGetter p_53471_, BlockPos p_53472_) {
      BlockPos blockpos = p_53472_.below();
      BlockState blockstate = p_53471_.getBlockState(blockpos);
      if (!this.canCatchFire(p_53471_, p_53472_, Direction.UP) && !blockstate.isFaceSturdy(p_53471_, blockpos, Direction.UP)) {
         BlockState blockstate1 = this.defaultBlockState();

         for(Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(direction);
            if (booleanproperty != null) {
               blockstate1 = blockstate1.setValue(booleanproperty, Boolean.valueOf(this.canCatchFire(p_53471_, p_53472_.relative(direction), direction.getOpposite())));
            }
         }

         return blockstate1;
      } else {
         return this.defaultBlockState();
      }
   }

   public boolean canSurvive(BlockState p_53454_, LevelReader p_53455_, BlockPos p_53456_) {
      BlockPos blockpos = p_53456_.below();
      return p_53455_.getBlockState(blockpos).isFaceSturdy(p_53455_, blockpos, Direction.UP) || this.isValidFireLocation(p_53455_, p_53456_);
   }

   public void tick(BlockState p_53449_, ServerLevel p_53450_, BlockPos p_53451_, Random p_53452_) {
      p_53450_.scheduleTick(p_53451_, this, getFireTickDelay(p_53450_.random));
      if (p_53450_.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         if (!p_53449_.canSurvive(p_53450_, p_53451_)) {
            p_53450_.removeBlock(p_53451_, false);
         }

         BlockState blockstate = p_53450_.getBlockState(p_53451_.below());
         boolean flag = blockstate.isFireSource(p_53450_, p_53451_, Direction.UP);
         int i = p_53449_.getValue(AGE);
         if (!flag && p_53450_.isRaining() && this.isNearRain(p_53450_, p_53451_) && p_53452_.nextFloat() < 0.2F + (float)i * 0.03F) {
            p_53450_.removeBlock(p_53451_, false);
         } else {
            int j = Math.min(15, i + p_53452_.nextInt(3) / 2);
            if (i != j) {
               p_53449_ = p_53449_.setValue(AGE, Integer.valueOf(j));
               p_53450_.setBlock(p_53451_, p_53449_, 4);
            }

            if (!flag) {
               if (!this.isValidFireLocation(p_53450_, p_53451_)) {
                  BlockPos blockpos = p_53451_.below();
                  if (!p_53450_.getBlockState(blockpos).isFaceSturdy(p_53450_, blockpos, Direction.UP) || i > 3) {
                     p_53450_.removeBlock(p_53451_, false);
                  }

                  return;
               }

               if (i == 15 && p_53452_.nextInt(4) == 0 && !this.canCatchFire(p_53450_, p_53451_.below(), Direction.UP)) {
                  p_53450_.removeBlock(p_53451_, false);
                  return;
               }
            }

            boolean flag1 = p_53450_.isHumidAt(p_53451_);
            int k = flag1 ? -50 : 0;
            this.tryCatchFire(p_53450_, p_53451_.east(), 300 + k, p_53452_, i, Direction.WEST);
            this.tryCatchFire(p_53450_, p_53451_.west(), 300 + k, p_53452_, i, Direction.EAST);
            this.tryCatchFire(p_53450_, p_53451_.below(), 250 + k, p_53452_, i, Direction.UP);
            this.tryCatchFire(p_53450_, p_53451_.above(), 250 + k, p_53452_, i, Direction.DOWN);
            this.tryCatchFire(p_53450_, p_53451_.north(), 300 + k, p_53452_, i, Direction.SOUTH);
            this.tryCatchFire(p_53450_, p_53451_.south(), 300 + k, p_53452_, i, Direction.NORTH);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int l = -1; l <= 1; ++l) {
               for(int i1 = -1; i1 <= 1; ++i1) {
                  for(int j1 = -1; j1 <= 4; ++j1) {
                     if (l != 0 || j1 != 0 || i1 != 0) {
                        int k1 = 100;
                        if (j1 > 1) {
                           k1 += (j1 - 1) * 100;
                        }

                        blockpos$mutableblockpos.setWithOffset(p_53451_, l, j1, i1);
                        int l1 = this.getFireOdds(p_53450_, blockpos$mutableblockpos);
                        if (l1 > 0) {
                           int i2 = (l1 + 40 + p_53450_.getDifficulty().getId() * 7) / (i + 30);
                           if (flag1) {
                              i2 /= 2;
                           }

                           if (i2 > 0 && p_53452_.nextInt(k1) <= i2 && (!p_53450_.isRaining() || !this.isNearRain(p_53450_, blockpos$mutableblockpos))) {
                              int j2 = Math.min(15, i + p_53452_.nextInt(5) / 4);
                              p_53450_.setBlock(blockpos$mutableblockpos, this.getStateWithAge(p_53450_, blockpos$mutableblockpos, j2), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean isNearRain(Level p_53429_, BlockPos p_53430_) {
      return p_53429_.isRainingAt(p_53430_) || p_53429_.isRainingAt(p_53430_.west()) || p_53429_.isRainingAt(p_53430_.east()) || p_53429_.isRainingAt(p_53430_.north()) || p_53429_.isRainingAt(p_53430_.south());
   }

   @Deprecated //Forge: Use IForgeBlockState.getFlammability, Public for default implementation only.
   public int getBurnOdd(BlockState p_53493_) {
      return p_53493_.hasProperty(BlockStateProperties.WATERLOGGED) && p_53493_.getValue(BlockStateProperties.WATERLOGGED) ? 0 : this.burnOdds.getInt(p_53493_.getBlock());
   }

   @Deprecated //Forge: Use IForgeBlockState.getFireSpreadSpeed
   public int getFlameOdds(BlockState p_53495_) {
      return p_53495_.hasProperty(BlockStateProperties.WATERLOGGED) && p_53495_.getValue(BlockStateProperties.WATERLOGGED) ? 0 : this.flameOdds.getInt(p_53495_.getBlock());
   }

   private void tryCatchFire(Level p_53432_, BlockPos p_53433_, int p_53434_, Random p_53435_, int p_53436_, Direction face) {
      int i = p_53432_.getBlockState(p_53433_).getFlammability(p_53432_, p_53433_, face);
      if (p_53435_.nextInt(p_53434_) < i) {
         BlockState blockstate = p_53432_.getBlockState(p_53433_);
         if (p_53435_.nextInt(p_53436_ + 10) < 5 && !p_53432_.isRainingAt(p_53433_)) {
            int j = Math.min(p_53436_ + p_53435_.nextInt(5) / 4, 15);
            p_53432_.setBlock(p_53433_, this.getStateWithAge(p_53432_, p_53433_, j), 3);
         } else {
            p_53432_.removeBlock(p_53433_, false);
         }

         blockstate.onCaughtFire(p_53432_, p_53433_, face, null);
      }

   }

   private BlockState getStateWithAge(LevelAccessor p_53438_, BlockPos p_53439_, int p_53440_) {
      BlockState blockstate = getState(p_53438_, p_53439_);
      return blockstate.is(Blocks.FIRE) ? blockstate.setValue(AGE, Integer.valueOf(p_53440_)) : blockstate;
   }

   private boolean isValidFireLocation(BlockGetter p_53486_, BlockPos p_53487_) {
      for(Direction direction : Direction.values()) {
         if (this.canCatchFire(p_53486_, p_53487_.relative(direction), direction.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   private int getFireOdds(LevelReader p_53442_, BlockPos p_53443_) {
      if (!p_53442_.isEmptyBlock(p_53443_)) {
         return 0;
      } else {
         int i = 0;

         for(Direction direction : Direction.values()) {
            BlockState blockstate = p_53442_.getBlockState(p_53443_.relative(direction));
            i = Math.max(blockstate.getFireSpreadSpeed(p_53442_, p_53443_.relative(direction), direction.getOpposite()), i);
         }

         return i;
      }
   }

   @Deprecated //Forge: Use canCatchFire with more context
   protected boolean canBurn(BlockState p_53489_) {
      return this.getFlameOdds(p_53489_) > 0;
   }

   public void onPlace(BlockState p_53479_, Level p_53480_, BlockPos p_53481_, BlockState p_53482_, boolean p_53483_) {
      super.onPlace(p_53479_, p_53480_, p_53481_, p_53482_, p_53483_);
      p_53480_.scheduleTick(p_53481_, this, getFireTickDelay(p_53480_.random));
   }

   private static int getFireTickDelay(Random p_53469_) {
      return 30 + p_53469_.nextInt(10);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53465_) {
      p_53465_.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
   }

   private void setFlammable(Block p_53445_, int p_53446_, int p_53447_) {
      if (p_53445_ == Blocks.AIR) throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
      this.flameOdds.put(p_53445_, p_53446_);
      this.burnOdds.put(p_53445_, p_53447_);
   }

   /**
    * Side sensitive version that calls the block function.
    *
    * @param world The current world
    * @param pos Block position
    * @param face The side the fire is coming from
    * @return True if the face can catch fire.
    */
   public boolean canCatchFire(BlockGetter world, BlockPos pos, Direction face) {
      return world.getBlockState(pos).isFlammable(world, pos, face);
   }

   public static void bootStrap() {
      FireBlock fireblock = (FireBlock)Blocks.FIRE;
      fireblock.setFlammable(Blocks.OAK_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.OAK_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.OAK_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.OAK_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.OAK_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.SPRUCE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.BIRCH_LOG, 5, 5);
      fireblock.setFlammable(Blocks.JUNGLE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.ACACIA_LOG, 5, 5);
      fireblock.setFlammable(Blocks.DARK_OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.SPRUCE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.BIRCH_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.JUNGLE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.ACACIA_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.DARK_OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.OAK_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.SPRUCE_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.BIRCH_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.JUNGLE_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.ACACIA_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.DARK_OAK_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.BOOKSHELF, 30, 20);
      fireblock.setFlammable(Blocks.TNT, 15, 100);
      fireblock.setFlammable(Blocks.GRASS, 60, 100);
      fireblock.setFlammable(Blocks.FERN, 60, 100);
      fireblock.setFlammable(Blocks.DEAD_BUSH, 60, 100);
      fireblock.setFlammable(Blocks.SUNFLOWER, 60, 100);
      fireblock.setFlammable(Blocks.LILAC, 60, 100);
      fireblock.setFlammable(Blocks.ROSE_BUSH, 60, 100);
      fireblock.setFlammable(Blocks.PEONY, 60, 100);
      fireblock.setFlammable(Blocks.TALL_GRASS, 60, 100);
      fireblock.setFlammable(Blocks.LARGE_FERN, 60, 100);
      fireblock.setFlammable(Blocks.DANDELION, 60, 100);
      fireblock.setFlammable(Blocks.POPPY, 60, 100);
      fireblock.setFlammable(Blocks.BLUE_ORCHID, 60, 100);
      fireblock.setFlammable(Blocks.ALLIUM, 60, 100);
      fireblock.setFlammable(Blocks.AZURE_BLUET, 60, 100);
      fireblock.setFlammable(Blocks.RED_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.ORANGE_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.WHITE_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.PINK_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.OXEYE_DAISY, 60, 100);
      fireblock.setFlammable(Blocks.CORNFLOWER, 60, 100);
      fireblock.setFlammable(Blocks.LILY_OF_THE_VALLEY, 60, 100);
      fireblock.setFlammable(Blocks.WITHER_ROSE, 60, 100);
      fireblock.setFlammable(Blocks.WHITE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.ORANGE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.MAGENTA_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.YELLOW_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.LIME_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.PINK_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.GRAY_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.CYAN_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.PURPLE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.BLUE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.BROWN_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.GREEN_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.RED_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.BLACK_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.VINE, 15, 100);
      fireblock.setFlammable(Blocks.COAL_BLOCK, 5, 5);
      fireblock.setFlammable(Blocks.HAY_BLOCK, 60, 20);
      fireblock.setFlammable(Blocks.TARGET, 15, 20);
      fireblock.setFlammable(Blocks.WHITE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.ORANGE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.MAGENTA_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.YELLOW_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.LIME_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.PINK_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.GRAY_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.CYAN_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.PURPLE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.BLUE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.BROWN_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.GREEN_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.RED_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.BLACK_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.DRIED_KELP_BLOCK, 30, 60);
      fireblock.setFlammable(Blocks.BAMBOO, 60, 60);
      fireblock.setFlammable(Blocks.SCAFFOLDING, 60, 60);
      fireblock.setFlammable(Blocks.LECTERN, 30, 20);
      fireblock.setFlammable(Blocks.COMPOSTER, 5, 20);
      fireblock.setFlammable(Blocks.SWEET_BERRY_BUSH, 60, 100);
      fireblock.setFlammable(Blocks.BEEHIVE, 5, 20);
      fireblock.setFlammable(Blocks.BEE_NEST, 30, 20);
      fireblock.setFlammable(Blocks.AZALEA_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.FLOWERING_AZALEA_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.CAVE_VINES, 15, 60);
      fireblock.setFlammable(Blocks.CAVE_VINES_PLANT, 15, 60);
      fireblock.setFlammable(Blocks.SPORE_BLOSSOM, 60, 100);
      fireblock.setFlammable(Blocks.AZALEA, 30, 60);
      fireblock.setFlammable(Blocks.FLOWERING_AZALEA, 30, 60);
      fireblock.setFlammable(Blocks.BIG_DRIPLEAF, 60, 100);
      fireblock.setFlammable(Blocks.BIG_DRIPLEAF_STEM, 60, 100);
      fireblock.setFlammable(Blocks.SMALL_DRIPLEAF, 60, 100);
      fireblock.setFlammable(Blocks.HANGING_ROOTS, 30, 60);
      fireblock.setFlammable(Blocks.GLOW_LICHEN, 15, 100);
   }
}
