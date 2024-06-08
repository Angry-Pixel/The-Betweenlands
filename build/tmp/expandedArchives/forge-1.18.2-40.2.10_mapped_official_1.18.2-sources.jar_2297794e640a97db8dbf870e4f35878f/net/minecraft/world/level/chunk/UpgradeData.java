package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData(EmptyBlockGetter.INSTANCE);
   private static final String TAG_INDICES = "Indices";
   private static final Direction8[] DIRECTIONS = Direction8.values();
   private final EnumSet<Direction8> sides = EnumSet.noneOf(Direction8.class);
   private final List<SavedTick<Block>> neighborBlockTicks = Lists.newArrayList();
   private final List<SavedTick<Fluid>> neighborFluidTicks = Lists.newArrayList();
   private final int[][] index;
   static final Map<Block, UpgradeData.BlockFixer> MAP = new IdentityHashMap<>();
   static final Set<UpgradeData.BlockFixer> CHUNKY_FIXERS = Sets.newHashSet();

   private UpgradeData(LevelHeightAccessor p_156506_) {
      this.index = new int[p_156506_.getSectionsCount()][];
   }

   public UpgradeData(CompoundTag p_156508_, LevelHeightAccessor p_156509_) {
      this(p_156509_);
      if (p_156508_.contains("Indices", 10)) {
         CompoundTag compoundtag = p_156508_.getCompound("Indices");

         for(int i = 0; i < this.index.length; ++i) {
            String s = String.valueOf(i);
            if (compoundtag.contains(s, 11)) {
               this.index[i] = compoundtag.getIntArray(s);
            }
         }
      }

      int j = p_156508_.getInt("Sides");

      for(Direction8 direction8 : Direction8.values()) {
         if ((j & 1 << direction8.ordinal()) != 0) {
            this.sides.add(direction8);
         }
      }

      loadTicks(p_156508_, "neighbor_block_ticks", (p_208144_) -> {
         return Registry.BLOCK.getOptional(ResourceLocation.tryParse(p_208144_)).or(() -> {
            return Optional.of(Blocks.AIR);
         });
      }, this.neighborBlockTicks);
      loadTicks(p_156508_, "neighbor_fluid_ticks", (p_208131_) -> {
         return Registry.FLUID.getOptional(ResourceLocation.tryParse(p_208131_)).or(() -> {
            return Optional.of(Fluids.EMPTY);
         });
      }, this.neighborFluidTicks);
   }

   private static <T> void loadTicks(CompoundTag p_208133_, String p_208134_, Function<String, Optional<T>> p_208135_, List<SavedTick<T>> p_208136_) {
      if (p_208133_.contains(p_208134_, 9)) {
         for(Tag tag : p_208133_.getList(p_208134_, 10)) {
            SavedTick.loadTick((CompoundTag)tag, p_208135_).ifPresent(p_208136_::add);
         }
      }

   }

   public void upgrade(LevelChunk p_63342_) {
      this.upgradeInside(p_63342_);

      for(Direction8 direction8 : DIRECTIONS) {
         upgradeSides(p_63342_, direction8);
      }

      Level level = p_63342_.getLevel();
      this.neighborBlockTicks.forEach((p_208142_) -> {
         Block block = p_208142_.type() == Blocks.AIR ? level.getBlockState(p_208142_.pos()).getBlock() : p_208142_.type();
         level.scheduleTick(p_208142_.pos(), block, p_208142_.delay(), p_208142_.priority());
      });
      this.neighborFluidTicks.forEach((p_208125_) -> {
         Fluid fluid = p_208125_.type() == Fluids.EMPTY ? level.getFluidState(p_208125_.pos()).getType() : p_208125_.type();
         level.scheduleTick(p_208125_.pos(), fluid, p_208125_.delay(), p_208125_.priority());
      });
      CHUNKY_FIXERS.forEach((p_208122_) -> {
         p_208122_.processChunk(level);
      });
   }

   private static void upgradeSides(LevelChunk p_63344_, Direction8 p_63345_) {
      Level level = p_63344_.getLevel();
      if (p_63344_.getUpgradeData().sides.remove(p_63345_)) {
         Set<Direction> set = p_63345_.getDirections();
         int i = 0;
         int j = 15;
         boolean flag = set.contains(Direction.EAST);
         boolean flag1 = set.contains(Direction.WEST);
         boolean flag2 = set.contains(Direction.SOUTH);
         boolean flag3 = set.contains(Direction.NORTH);
         boolean flag4 = set.size() == 1;
         ChunkPos chunkpos = p_63344_.getPos();
         int k = chunkpos.getMinBlockX() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 1);
         int l = chunkpos.getMinBlockX() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 14);
         int i1 = chunkpos.getMinBlockZ() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 1);
         int j1 = chunkpos.getMinBlockZ() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 14);
         Direction[] adirection = Direction.values();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(BlockPos blockpos : BlockPos.betweenClosed(k, level.getMinBuildHeight(), i1, l, level.getMaxBuildHeight() - 1, j1)) {
            BlockState blockstate = level.getBlockState(blockpos);
            BlockState blockstate1 = blockstate;

            for(Direction direction : adirection) {
               blockpos$mutableblockpos.setWithOffset(blockpos, direction);
               blockstate1 = updateState(blockstate1, direction, level, blockpos, blockpos$mutableblockpos);
            }

            Block.updateOrDestroy(blockstate, blockstate1, level, blockpos, 18);
         }

      }
   }

   private static BlockState updateState(BlockState p_63336_, Direction p_63337_, LevelAccessor p_63338_, BlockPos p_63339_, BlockPos p_63340_) {
      return MAP.getOrDefault(p_63336_.getBlock(), UpgradeData.BlockFixers.DEFAULT).updateShape(p_63336_, p_63337_, p_63338_.getBlockState(p_63340_), p_63338_, p_63339_, p_63340_);
   }

   private void upgradeInside(LevelChunk p_63348_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
      ChunkPos chunkpos = p_63348_.getPos();
      LevelAccessor levelaccessor = p_63348_.getLevel();

      for(int i = 0; i < this.index.length; ++i) {
         LevelChunkSection levelchunksection = p_63348_.getSection(i);
         int[] aint = this.index[i];
         this.index[i] = null;
         if (aint != null && aint.length > 0) {
            Direction[] adirection = Direction.values();
            PalettedContainer<BlockState> palettedcontainer = levelchunksection.getStates();

            for(int j : aint) {
               int k = j & 15;
               int l = j >> 8 & 15;
               int i1 = j >> 4 & 15;
               blockpos$mutableblockpos.set(chunkpos.getMinBlockX() + k, levelchunksection.bottomBlockY() + l, chunkpos.getMinBlockZ() + i1);
               BlockState blockstate = palettedcontainer.get(j);
               BlockState blockstate1 = blockstate;

               for(Direction direction : adirection) {
                  blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, direction);
                  if (SectionPos.blockToSectionCoord(blockpos$mutableblockpos.getX()) == chunkpos.x && SectionPos.blockToSectionCoord(blockpos$mutableblockpos.getZ()) == chunkpos.z) {
                     blockstate1 = updateState(blockstate1, direction, levelaccessor, blockpos$mutableblockpos, blockpos$mutableblockpos1);
                  }
               }

               Block.updateOrDestroy(blockstate, blockstate1, levelaccessor, blockpos$mutableblockpos, 18);
            }
         }
      }

      for(int j1 = 0; j1 < this.index.length; ++j1) {
         if (this.index[j1] != null) {
            LOGGER.warn("Discarding update data for section {} for chunk ({} {})", levelaccessor.getSectionYFromSectionIndex(j1), chunkpos.x, chunkpos.z);
         }

         this.index[j1] = null;
      }

   }

   public boolean isEmpty() {
      for(int[] aint : this.index) {
         if (aint != null) {
            return false;
         }
      }

      return this.sides.isEmpty();
   }

   public CompoundTag write() {
      CompoundTag compoundtag = new CompoundTag();
      CompoundTag compoundtag1 = new CompoundTag();

      for(int i = 0; i < this.index.length; ++i) {
         String s = String.valueOf(i);
         if (this.index[i] != null && this.index[i].length != 0) {
            compoundtag1.putIntArray(s, this.index[i]);
         }
      }

      if (!compoundtag1.isEmpty()) {
         compoundtag.put("Indices", compoundtag1);
      }

      int j = 0;

      for(Direction8 direction8 : this.sides) {
         j |= 1 << direction8.ordinal();
      }

      compoundtag.putByte("Sides", (byte)j);
      if (!this.neighborBlockTicks.isEmpty()) {
         ListTag listtag = new ListTag();
         this.neighborBlockTicks.forEach((p_208147_) -> {
            listtag.add(p_208147_.save((p_208127_) -> {
               return Registry.BLOCK.getKey(p_208127_).toString();
            }));
         });
         compoundtag.put("neighbor_block_ticks", listtag);
      }

      if (!this.neighborFluidTicks.isEmpty()) {
         ListTag listtag1 = new ListTag();
         this.neighborFluidTicks.forEach((p_208139_) -> {
            listtag1.add(p_208139_.save((p_208129_) -> {
               return Registry.FLUID.getKey(p_208129_).toString();
            }));
         });
         compoundtag.put("neighbor_fluid_ticks", listtag1);
      }

      return compoundtag;
   }

   public interface BlockFixer {
      BlockState updateShape(BlockState p_63352_, Direction p_63353_, BlockState p_63354_, LevelAccessor p_63355_, BlockPos p_63356_, BlockPos p_63357_);

      default void processChunk(LevelAccessor p_63351_) {
      }
   }

   static enum BlockFixers implements UpgradeData.BlockFixer {
      BLACKLIST(Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN) {
         public BlockState updateShape(BlockState p_63394_, Direction p_63395_, BlockState p_63396_, LevelAccessor p_63397_, BlockPos p_63398_, BlockPos p_63399_) {
            return p_63394_;
         }
      },
      DEFAULT {
         public BlockState updateShape(BlockState p_63405_, Direction p_63406_, BlockState p_63407_, LevelAccessor p_63408_, BlockPos p_63409_, BlockPos p_63410_) {
            return p_63405_.updateShape(p_63406_, p_63408_.getBlockState(p_63410_), p_63408_, p_63409_, p_63410_);
         }
      },
      CHEST(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
         public BlockState updateShape(BlockState p_63416_, Direction p_63417_, BlockState p_63418_, LevelAccessor p_63419_, BlockPos p_63420_, BlockPos p_63421_) {
            if (p_63418_.is(p_63416_.getBlock()) && p_63417_.getAxis().isHorizontal() && p_63416_.getValue(ChestBlock.TYPE) == ChestType.SINGLE && p_63418_.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
               Direction direction = p_63416_.getValue(ChestBlock.FACING);
               if (p_63417_.getAxis() != direction.getAxis() && direction == p_63418_.getValue(ChestBlock.FACING)) {
                  ChestType chesttype = p_63417_ == direction.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                  p_63419_.setBlock(p_63421_, p_63418_.setValue(ChestBlock.TYPE, chesttype.getOpposite()), 18);
                  if (direction == Direction.NORTH || direction == Direction.EAST) {
                     BlockEntity blockentity = p_63419_.getBlockEntity(p_63420_);
                     BlockEntity blockentity1 = p_63419_.getBlockEntity(p_63421_);
                     if (blockentity instanceof ChestBlockEntity && blockentity1 instanceof ChestBlockEntity) {
                        ChestBlockEntity.swapContents((ChestBlockEntity)blockentity, (ChestBlockEntity)blockentity1);
                     }
                  }

                  return p_63416_.setValue(ChestBlock.TYPE, chesttype);
               }
            }

            return p_63416_;
         }
      },
      LEAVES(true, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
         private final ThreadLocal<List<ObjectSet<BlockPos>>> queue = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public BlockState updateShape(BlockState p_63432_, Direction p_63433_, BlockState p_63434_, LevelAccessor p_63435_, BlockPos p_63436_, BlockPos p_63437_) {
            BlockState blockstate = p_63432_.updateShape(p_63433_, p_63435_.getBlockState(p_63437_), p_63435_, p_63436_, p_63437_);
            if (p_63432_ != blockstate) {
               int i = blockstate.getValue(BlockStateProperties.DISTANCE);
               List<ObjectSet<BlockPos>> list = this.queue.get();
               if (list.isEmpty()) {
                  for(int j = 0; j < 7; ++j) {
                     list.add(new ObjectOpenHashSet<>());
                  }
               }

               list.get(i).add(p_63436_.immutable());
            }

            return p_63432_;
         }

         public void processChunk(LevelAccessor p_63430_) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            List<ObjectSet<BlockPos>> list = this.queue.get();

            for(int i = 2; i < list.size(); ++i) {
               int j = i - 1;
               ObjectSet<BlockPos> objectset = list.get(j);
               ObjectSet<BlockPos> objectset1 = list.get(i);

               for(BlockPos blockpos : objectset) {
                  BlockState blockstate = p_63430_.getBlockState(blockpos);
                  if (blockstate.getValue(BlockStateProperties.DISTANCE) >= j) {
                     p_63430_.setBlock(blockpos, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(j)), 18);
                     if (i != 7) {
                        for(Direction direction : DIRECTIONS) {
                           blockpos$mutableblockpos.setWithOffset(blockpos, direction);
                           BlockState blockstate1 = p_63430_.getBlockState(blockpos$mutableblockpos);
                           if (blockstate1.hasProperty(BlockStateProperties.DISTANCE) && blockstate.getValue(BlockStateProperties.DISTANCE) > i) {
                              objectset1.add(blockpos$mutableblockpos.immutable());
                           }
                        }
                     }
                  }
               }
            }

            list.clear();
         }
      },
      STEM_BLOCK(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
         public BlockState updateShape(BlockState p_63443_, Direction p_63444_, BlockState p_63445_, LevelAccessor p_63446_, BlockPos p_63447_, BlockPos p_63448_) {
            if (p_63443_.getValue(StemBlock.AGE) == 7) {
               StemGrownBlock stemgrownblock = ((StemBlock)p_63443_.getBlock()).getFruit();
               if (p_63445_.is(stemgrownblock)) {
                  return stemgrownblock.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, p_63444_);
               }
            }

            return p_63443_;
         }
      };

      public static final Direction[] DIRECTIONS = Direction.values();

      BlockFixers(Block... p_63380_) {
         this(false, p_63380_);
      }

      BlockFixers(boolean p_63369_, Block... p_63370_) {
         for(Block block : p_63370_) {
            UpgradeData.MAP.put(block, this);
         }

         if (p_63369_) {
            UpgradeData.CHUNKY_FIXERS.add(this);
         }

      }
   }
}