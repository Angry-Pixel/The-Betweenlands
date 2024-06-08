package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.NoiseEffect;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public abstract class StructurePiece {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
   protected BoundingBox boundingBox;
   @Nullable
   private Direction orientation;
   private Mirror mirror;
   private Rotation rotation;
   protected int genDepth;
   private final StructurePieceType type;
   private static final Set<Block> SHAPE_CHECK_BLOCKS = ImmutableSet.<Block>builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();

   protected StructurePiece(StructurePieceType p_209994_, int p_209995_, BoundingBox p_209996_) {
      this.type = p_209994_;
      this.genDepth = p_209995_;
      this.boundingBox = p_209996_;
   }

   public StructurePiece(StructurePieceType p_209998_, CompoundTag p_209999_) {
      this(p_209998_, p_209999_.getInt("GD"), BoundingBox.CODEC.parse(NbtOps.INSTANCE, p_209999_.get("BB")).resultOrPartial(LOGGER::error).orElseThrow(() -> {
         return new IllegalArgumentException("Invalid boundingbox");
      }));
      int i = p_209999_.getInt("O");
      this.setOrientation(i == -1 ? null : Direction.from2DDataValue(i));
   }

   protected static BoundingBox makeBoundingBox(int p_163542_, int p_163543_, int p_163544_, Direction p_163545_, int p_163546_, int p_163547_, int p_163548_) {
      return p_163545_.getAxis() == Direction.Axis.Z ? new BoundingBox(p_163542_, p_163543_, p_163544_, p_163542_ + p_163546_ - 1, p_163543_ + p_163547_ - 1, p_163544_ + p_163548_ - 1) : new BoundingBox(p_163542_, p_163543_, p_163544_, p_163542_ + p_163548_ - 1, p_163543_ + p_163547_ - 1, p_163544_ + p_163546_ - 1);
   }

   protected static Direction getRandomHorizontalDirection(Random p_163581_) {
      return Direction.Plane.HORIZONTAL.getRandomDirection(p_163581_);
   }

   public final CompoundTag createTag(StructurePieceSerializationContext p_192645_) {
      if (Registry.STRUCTURE_PIECE.getKey(this.getType()) == null) { // FORGE: Friendlier error then the Null String error below.
         throw new RuntimeException("StructurePiece \"" + this.getClass().getName() + "\": \"" + this.getType() + "\" missing ID Mapping, Modder see MapGenStructureIO");
      }
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("id", Registry.STRUCTURE_PIECE.getKey(this.getType()).toString());
      BoundingBox.CODEC.encodeStart(NbtOps.INSTANCE, this.boundingBox).resultOrPartial(LOGGER::error).ifPresent((p_163579_) -> {
         compoundtag.put("BB", p_163579_);
      });
      Direction direction = this.getOrientation();
      compoundtag.putInt("O", direction == null ? -1 : direction.get2DDataValue());
      compoundtag.putInt("GD", this.genDepth);
      this.addAdditionalSaveData(p_192645_, compoundtag);
      return compoundtag;
   }

   protected abstract void addAdditionalSaveData(StructurePieceSerializationContext p_192646_, CompoundTag p_192647_);

   public NoiseEffect getNoiseEffect() {
      return NoiseEffect.BEARD;
   }

   public void addChildren(StructurePiece p_163574_, StructurePieceAccessor p_163575_, Random p_163576_) {
   }

   public abstract void postProcess(WorldGenLevel p_192637_, StructureFeatureManager p_192638_, ChunkGenerator p_192639_, Random p_192640_, BoundingBox p_192641_, ChunkPos p_192642_, BlockPos p_192643_);

   public BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getGenDepth() {
      return this.genDepth;
   }

   public boolean isCloseToChunk(ChunkPos p_73412_, int p_73413_) {
      int i = p_73412_.getMinBlockX();
      int j = p_73412_.getMinBlockZ();
      return this.boundingBox.intersects(i - p_73413_, j - p_73413_, i + 15 + p_73413_, j + 15 + p_73413_);
   }

   public BlockPos getLocatorPosition() {
      return new BlockPos(this.boundingBox.getCenter());
   }

   protected BlockPos.MutableBlockPos getWorldPos(int p_163583_, int p_163584_, int p_163585_) {
      return new BlockPos.MutableBlockPos(this.getWorldX(p_163583_, p_163585_), this.getWorldY(p_163584_), this.getWorldZ(p_163583_, p_163585_));
   }

   protected int getWorldX(int p_73393_, int p_73394_) {
      Direction direction = this.getOrientation();
      if (direction == null) {
         return p_73393_;
      } else {
         switch(direction) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX() + p_73393_;
         case WEST:
            return this.boundingBox.maxX() - p_73394_;
         case EAST:
            return this.boundingBox.minX() + p_73394_;
         default:
            return p_73393_;
         }
      }
   }

   protected int getWorldY(int p_73545_) {
      return this.getOrientation() == null ? p_73545_ : p_73545_ + this.boundingBox.minY();
   }

   protected int getWorldZ(int p_73526_, int p_73527_) {
      Direction direction = this.getOrientation();
      if (direction == null) {
         return p_73527_;
      } else {
         switch(direction) {
         case NORTH:
            return this.boundingBox.maxZ() - p_73527_;
         case SOUTH:
            return this.boundingBox.minZ() + p_73527_;
         case WEST:
         case EAST:
            return this.boundingBox.minZ() + p_73526_;
         default:
            return p_73527_;
         }
      }
   }

   protected void placeBlock(WorldGenLevel p_73435_, BlockState p_73436_, int p_73437_, int p_73438_, int p_73439_, BoundingBox p_73440_) {
      BlockPos blockpos = this.getWorldPos(p_73437_, p_73438_, p_73439_);
      if (p_73440_.isInside(blockpos)) {
         if (this.canBeReplaced(p_73435_, p_73437_, p_73438_, p_73439_, p_73440_)) {
            if (this.mirror != Mirror.NONE) {
               p_73436_ = p_73436_.mirror(this.mirror);
            }

            if (this.rotation != Rotation.NONE) {
               p_73436_ = p_73436_.rotate(this.rotation);
            }

            p_73435_.setBlock(blockpos, p_73436_, 2);
            FluidState fluidstate = p_73435_.getFluidState(blockpos);
            if (!fluidstate.isEmpty()) {
               p_73435_.scheduleTick(blockpos, fluidstate.getType(), 0);
            }

            if (SHAPE_CHECK_BLOCKS.contains(p_73436_.getBlock())) {
               p_73435_.getChunk(blockpos).markPosForPostprocessing(blockpos);
            }

         }
      }
   }

   protected boolean canBeReplaced(LevelReader p_163553_, int p_163554_, int p_163555_, int p_163556_, BoundingBox p_163557_) {
      return true;
   }

   protected BlockState getBlock(BlockGetter p_73399_, int p_73400_, int p_73401_, int p_73402_, BoundingBox p_73403_) {
      BlockPos blockpos = this.getWorldPos(p_73400_, p_73401_, p_73402_);
      return !p_73403_.isInside(blockpos) ? Blocks.AIR.defaultBlockState() : p_73399_.getBlockState(blockpos);
   }

   protected boolean isInterior(LevelReader p_73415_, int p_73416_, int p_73417_, int p_73418_, BoundingBox p_73419_) {
      BlockPos blockpos = this.getWorldPos(p_73416_, p_73417_ + 1, p_73418_);
      if (!p_73419_.isInside(blockpos)) {
         return false;
      } else {
         return blockpos.getY() < p_73415_.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockpos.getX(), blockpos.getZ());
      }
   }

   protected void generateAirBox(WorldGenLevel p_73536_, BoundingBox p_73537_, int p_73538_, int p_73539_, int p_73540_, int p_73541_, int p_73542_, int p_73543_) {
      for(int i = p_73539_; i <= p_73542_; ++i) {
         for(int j = p_73538_; j <= p_73541_; ++j) {
            for(int k = p_73540_; k <= p_73543_; ++k) {
               this.placeBlock(p_73536_, Blocks.AIR.defaultBlockState(), j, i, k, p_73537_);
            }
         }
      }

   }

   protected void generateBox(WorldGenLevel p_73442_, BoundingBox p_73443_, int p_73444_, int p_73445_, int p_73446_, int p_73447_, int p_73448_, int p_73449_, BlockState p_73450_, BlockState p_73451_, boolean p_73452_) {
      for(int i = p_73445_; i <= p_73448_; ++i) {
         for(int j = p_73444_; j <= p_73447_; ++j) {
            for(int k = p_73446_; k <= p_73449_; ++k) {
               if (!p_73452_ || !this.getBlock(p_73442_, j, i, k, p_73443_).isAir()) {
                  if (i != p_73445_ && i != p_73448_ && j != p_73444_ && j != p_73447_ && k != p_73446_ && k != p_73449_) {
                     this.placeBlock(p_73442_, p_73451_, j, i, k, p_73443_);
                  } else {
                     this.placeBlock(p_73442_, p_73450_, j, i, k, p_73443_);
                  }
               }
            }
         }
      }

   }

   protected void generateBox(WorldGenLevel p_163559_, BoundingBox p_163560_, BoundingBox p_163561_, BlockState p_163562_, BlockState p_163563_, boolean p_163564_) {
      this.generateBox(p_163559_, p_163560_, p_163561_.minX(), p_163561_.minY(), p_163561_.minZ(), p_163561_.maxX(), p_163561_.maxY(), p_163561_.maxZ(), p_163562_, p_163563_, p_163564_);
   }

   protected void generateBox(WorldGenLevel p_73465_, BoundingBox p_73466_, int p_73467_, int p_73468_, int p_73469_, int p_73470_, int p_73471_, int p_73472_, boolean p_73473_, Random p_73474_, StructurePiece.BlockSelector p_73475_) {
      for(int i = p_73468_; i <= p_73471_; ++i) {
         for(int j = p_73467_; j <= p_73470_; ++j) {
            for(int k = p_73469_; k <= p_73472_; ++k) {
               if (!p_73473_ || !this.getBlock(p_73465_, j, i, k, p_73466_).isAir()) {
                  p_73475_.next(p_73474_, j, i, k, i == p_73468_ || i == p_73471_ || j == p_73467_ || j == p_73470_ || k == p_73469_ || k == p_73472_);
                  this.placeBlock(p_73465_, p_73475_.getNext(), j, i, k, p_73466_);
               }
            }
         }
      }

   }

   protected void generateBox(WorldGenLevel p_163566_, BoundingBox p_163567_, BoundingBox p_163568_, boolean p_163569_, Random p_163570_, StructurePiece.BlockSelector p_163571_) {
      this.generateBox(p_163566_, p_163567_, p_163568_.minX(), p_163568_.minY(), p_163568_.minZ(), p_163568_.maxX(), p_163568_.maxY(), p_163568_.maxZ(), p_163569_, p_163570_, p_163571_);
   }

   protected void generateMaybeBox(WorldGenLevel p_73477_, BoundingBox p_73478_, Random p_73479_, float p_73480_, int p_73481_, int p_73482_, int p_73483_, int p_73484_, int p_73485_, int p_73486_, BlockState p_73487_, BlockState p_73488_, boolean p_73489_, boolean p_73490_) {
      for(int i = p_73482_; i <= p_73485_; ++i) {
         for(int j = p_73481_; j <= p_73484_; ++j) {
            for(int k = p_73483_; k <= p_73486_; ++k) {
               if (!(p_73479_.nextFloat() > p_73480_) && (!p_73489_ || !this.getBlock(p_73477_, j, i, k, p_73478_).isAir()) && (!p_73490_ || this.isInterior(p_73477_, j, i, k, p_73478_))) {
                  if (i != p_73482_ && i != p_73485_ && j != p_73481_ && j != p_73484_ && k != p_73483_ && k != p_73486_) {
                     this.placeBlock(p_73477_, p_73488_, j, i, k, p_73478_);
                  } else {
                     this.placeBlock(p_73477_, p_73487_, j, i, k, p_73478_);
                  }
               }
            }
         }
      }

   }

   protected void maybeGenerateBlock(WorldGenLevel p_73492_, BoundingBox p_73493_, Random p_73494_, float p_73495_, int p_73496_, int p_73497_, int p_73498_, BlockState p_73499_) {
      if (p_73494_.nextFloat() < p_73495_) {
         this.placeBlock(p_73492_, p_73499_, p_73496_, p_73497_, p_73498_, p_73493_);
      }

   }

   protected void generateUpperHalfSphere(WorldGenLevel p_73454_, BoundingBox p_73455_, int p_73456_, int p_73457_, int p_73458_, int p_73459_, int p_73460_, int p_73461_, BlockState p_73462_, boolean p_73463_) {
      float f = (float)(p_73459_ - p_73456_ + 1);
      float f1 = (float)(p_73460_ - p_73457_ + 1);
      float f2 = (float)(p_73461_ - p_73458_ + 1);
      float f3 = (float)p_73456_ + f / 2.0F;
      float f4 = (float)p_73458_ + f2 / 2.0F;

      for(int i = p_73457_; i <= p_73460_; ++i) {
         float f5 = (float)(i - p_73457_) / f1;

         for(int j = p_73456_; j <= p_73459_; ++j) {
            float f6 = ((float)j - f3) / (f * 0.5F);

            for(int k = p_73458_; k <= p_73461_; ++k) {
               float f7 = ((float)k - f4) / (f2 * 0.5F);
               if (!p_73463_ || !this.getBlock(p_73454_, j, i, k, p_73455_).isAir()) {
                  float f8 = f6 * f6 + f5 * f5 + f7 * f7;
                  if (f8 <= 1.05F) {
                     this.placeBlock(p_73454_, p_73462_, j, i, k, p_73455_);
                  }
               }
            }
         }
      }

   }

   protected void fillColumnDown(WorldGenLevel p_73529_, BlockState p_73530_, int p_73531_, int p_73532_, int p_73533_, BoundingBox p_73534_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = this.getWorldPos(p_73531_, p_73532_, p_73533_);
      if (p_73534_.isInside(blockpos$mutableblockpos)) {
         while(this.isReplaceableByStructures(p_73529_.getBlockState(blockpos$mutableblockpos)) && blockpos$mutableblockpos.getY() > p_73529_.getMinBuildHeight() + 1) {
            p_73529_.setBlock(blockpos$mutableblockpos, p_73530_, 2);
            blockpos$mutableblockpos.move(Direction.DOWN);
         }

      }
   }

   protected boolean isReplaceableByStructures(BlockState p_163573_) {
      return p_163573_.isAir() || p_163573_.getMaterial().isLiquid() || p_163573_.is(Blocks.GLOW_LICHEN) || p_163573_.is(Blocks.SEAGRASS) || p_163573_.is(Blocks.TALL_SEAGRASS);
   }

   protected boolean createChest(WorldGenLevel p_73509_, BoundingBox p_73510_, Random p_73511_, int p_73512_, int p_73513_, int p_73514_, ResourceLocation p_73515_) {
      return this.createChest(p_73509_, p_73510_, p_73511_, this.getWorldPos(p_73512_, p_73513_, p_73514_), p_73515_, (BlockState)null);
   }

   public static BlockState reorient(BlockGetter p_73408_, BlockPos p_73409_, BlockState p_73410_) {
      Direction direction = null;

      for(Direction direction1 : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_73409_.relative(direction1);
         BlockState blockstate = p_73408_.getBlockState(blockpos);
         if (blockstate.is(Blocks.CHEST)) {
            return p_73410_;
         }

         if (blockstate.isSolidRender(p_73408_, blockpos)) {
            if (direction != null) {
               direction = null;
               break;
            }

            direction = direction1;
         }
      }

      if (direction != null) {
         return p_73410_.setValue(HorizontalDirectionalBlock.FACING, direction.getOpposite());
      } else {
         Direction direction2 = p_73410_.getValue(HorizontalDirectionalBlock.FACING);
         BlockPos blockpos1 = p_73409_.relative(direction2);
         if (p_73408_.getBlockState(blockpos1).isSolidRender(p_73408_, blockpos1)) {
            direction2 = direction2.getOpposite();
            blockpos1 = p_73409_.relative(direction2);
         }

         if (p_73408_.getBlockState(blockpos1).isSolidRender(p_73408_, blockpos1)) {
            direction2 = direction2.getClockWise();
            blockpos1 = p_73409_.relative(direction2);
         }

         if (p_73408_.getBlockState(blockpos1).isSolidRender(p_73408_, blockpos1)) {
            direction2 = direction2.getOpposite();
            p_73409_.relative(direction2);
         }

         return p_73410_.setValue(HorizontalDirectionalBlock.FACING, direction2);
      }
   }

   protected boolean createChest(ServerLevelAccessor p_73421_, BoundingBox p_73422_, Random p_73423_, BlockPos p_73424_, ResourceLocation p_73425_, @Nullable BlockState p_73426_) {
      if (p_73422_.isInside(p_73424_) && !p_73421_.getBlockState(p_73424_).is(Blocks.CHEST)) {
         if (p_73426_ == null) {
            p_73426_ = reorient(p_73421_, p_73424_, Blocks.CHEST.defaultBlockState());
         }

         p_73421_.setBlock(p_73424_, p_73426_, 2);
         BlockEntity blockentity = p_73421_.getBlockEntity(p_73424_);
         if (blockentity instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockentity).setLootTable(p_73425_, p_73423_.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean createDispenser(WorldGenLevel p_73501_, BoundingBox p_73502_, Random p_73503_, int p_73504_, int p_73505_, int p_73506_, Direction p_73507_, ResourceLocation p_73508_) {
      BlockPos blockpos = this.getWorldPos(p_73504_, p_73505_, p_73506_);
      if (p_73502_.isInside(blockpos) && !p_73501_.getBlockState(blockpos).is(Blocks.DISPENSER)) {
         this.placeBlock(p_73501_, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, p_73507_), p_73504_, p_73505_, p_73506_, p_73502_);
         BlockEntity blockentity = p_73501_.getBlockEntity(blockpos);
         if (blockentity instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)blockentity).setLootTable(p_73508_, p_73503_.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   public void move(int p_73395_, int p_73396_, int p_73397_) {
      this.boundingBox.move(p_73395_, p_73396_, p_73397_);
   }

   public static BoundingBox createBoundingBox(Stream<StructurePiece> p_192652_) {
      return BoundingBox.encapsulatingBoxes(p_192652_.map(StructurePiece::getBoundingBox)::iterator).orElseThrow(() -> {
         return new IllegalStateException("Unable to calculate boundingbox without pieces");
      });
   }

   @Nullable
   public static StructurePiece findCollisionPiece(List<StructurePiece> p_192649_, BoundingBox p_192650_) {
      for(StructurePiece structurepiece : p_192649_) {
         if (structurepiece.getBoundingBox().intersects(p_192650_)) {
            return structurepiece;
         }
      }

      return null;
   }

   @Nullable
   public Direction getOrientation() {
      return this.orientation;
   }

   public void setOrientation(@Nullable Direction p_73520_) {
      this.orientation = p_73520_;
      if (p_73520_ == null) {
         this.rotation = Rotation.NONE;
         this.mirror = Mirror.NONE;
      } else {
         switch(p_73520_) {
         case SOUTH:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.NONE;
            break;
         case WEST:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.NONE;
         }
      }

   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public StructurePieceType getType() {
      return this.type;
   }

   protected abstract static class BlockSelector {
      protected BlockState next = Blocks.AIR.defaultBlockState();

      public abstract void next(Random p_73556_, int p_73557_, int p_73558_, int p_73559_, boolean p_73560_);

      public BlockState getNext() {
         return this.next;
      }
   }
}
