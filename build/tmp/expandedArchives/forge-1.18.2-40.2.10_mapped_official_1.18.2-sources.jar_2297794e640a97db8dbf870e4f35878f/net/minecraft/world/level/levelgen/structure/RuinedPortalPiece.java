package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlackstoneReplaceProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.LavaSubmergedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

public class RuinedPortalPiece extends TemplateStructurePiece {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float PROBABILITY_OF_GOLD_GONE = 0.3F;
   private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_NETHERRACK = 0.07F;
   private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_LAVA = 0.2F;
   private static final float DEFAULT_MOSSINESS = 0.2F;
   private final RuinedPortalPiece.VerticalPlacement verticalPlacement;
   private final RuinedPortalPiece.Properties properties;

   public RuinedPortalPiece(StructureManager p_163138_, BlockPos p_163139_, RuinedPortalPiece.VerticalPlacement p_163140_, RuinedPortalPiece.Properties p_163141_, ResourceLocation p_163142_, StructureTemplate p_163143_, Rotation p_163144_, Mirror p_163145_, BlockPos p_163146_) {
      super(StructurePieceType.RUINED_PORTAL, 0, p_163138_, p_163142_, p_163142_.toString(), makeSettings(p_163145_, p_163144_, p_163140_, p_163146_, p_163141_), p_163139_);
      this.verticalPlacement = p_163140_;
      this.properties = p_163141_;
   }

   public RuinedPortalPiece(StructureManager p_192446_, CompoundTag p_192447_) {
      super(StructurePieceType.RUINED_PORTAL, p_192447_, p_192446_, (p_192466_) -> {
         return makeSettings(p_192446_, p_192447_, p_192466_);
      });
      this.verticalPlacement = RuinedPortalPiece.VerticalPlacement.byName(p_192447_.getString("VerticalPlacement"));
      this.properties = RuinedPortalPiece.Properties.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_192447_.get("Properties"))).getOrThrow(true, LOGGER::error);
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext p_192457_, CompoundTag p_192458_) {
      super.addAdditionalSaveData(p_192457_, p_192458_);
      p_192458_.putString("Rotation", this.placeSettings.getRotation().name());
      p_192458_.putString("Mirror", this.placeSettings.getMirror().name());
      p_192458_.putString("VerticalPlacement", this.verticalPlacement.getName());
      RuinedPortalPiece.Properties.CODEC.encodeStart(NbtOps.INSTANCE, this.properties).resultOrPartial(LOGGER::error).ifPresent((p_163169_) -> {
         p_192458_.put("Properties", p_163169_);
      });
   }

   private static StructurePlaceSettings makeSettings(StructureManager p_192460_, CompoundTag p_192461_, ResourceLocation p_192462_) {
      StructureTemplate structuretemplate = p_192460_.getOrCreate(p_192462_);
      BlockPos blockpos = new BlockPos(structuretemplate.getSize().getX() / 2, 0, structuretemplate.getSize().getZ() / 2);
      return makeSettings(Mirror.valueOf(p_192461_.getString("Mirror")), Rotation.valueOf(p_192461_.getString("Rotation")), RuinedPortalPiece.VerticalPlacement.byName(p_192461_.getString("VerticalPlacement")), blockpos, RuinedPortalPiece.Properties.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_192461_.get("Properties"))).getOrThrow(true, LOGGER::error));
   }

   private static StructurePlaceSettings makeSettings(Mirror p_163155_, Rotation p_163156_, RuinedPortalPiece.VerticalPlacement p_163157_, BlockPos p_163158_, RuinedPortalPiece.Properties p_163159_) {
      BlockIgnoreProcessor blockignoreprocessor = p_163159_.airPocket ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
      List<ProcessorRule> list = Lists.newArrayList();
      list.add(getBlockReplaceRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
      list.add(getLavaProcessorRule(p_163157_, p_163159_));
      if (!p_163159_.cold) {
         list.add(getBlockReplaceRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
      }

      StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(p_163156_).setMirror(p_163155_).setRotationPivot(p_163158_).addProcessor(blockignoreprocessor).addProcessor(new RuleProcessor(list)).addProcessor(new BlockAgeProcessor(p_163159_.mossiness)).addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE)).addProcessor(new LavaSubmergedBlockProcessor());
      if (p_163159_.replaceWithBlackstone) {
         structureplacesettings.addProcessor(BlackstoneReplaceProcessor.INSTANCE);
      }

      return structureplacesettings;
   }

   private static ProcessorRule getLavaProcessorRule(RuinedPortalPiece.VerticalPlacement p_163161_, RuinedPortalPiece.Properties p_163162_) {
      if (p_163161_ == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR) {
         return getBlockReplaceRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
      } else {
         return p_163162_.cold ? getBlockReplaceRule(Blocks.LAVA, Blocks.NETHERRACK) : getBlockReplaceRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
      }
   }

   public void postProcess(WorldGenLevel p_192449_, StructureFeatureManager p_192450_, ChunkGenerator p_192451_, Random p_192452_, BoundingBox p_192453_, ChunkPos p_192454_, BlockPos p_192455_) {
      BoundingBox boundingbox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if (p_192453_.isInside(boundingbox.getCenter())) {
         p_192453_.encapsulate(boundingbox);
         super.postProcess(p_192449_, p_192450_, p_192451_, p_192452_, p_192453_, p_192454_, p_192455_);
         this.spreadNetherrack(p_192452_, p_192449_);
         this.addNetherrackDripColumnsBelowPortal(p_192452_, p_192449_);
         if (this.properties.vines || this.properties.overgrown) {
            BlockPos.betweenClosedStream(this.getBoundingBox()).forEach((p_163166_) -> {
               if (this.properties.vines) {
                  this.maybeAddVines(p_192452_, p_192449_, p_163166_);
               }

               if (this.properties.overgrown) {
                  this.maybeAddLeavesAbove(p_192452_, p_192449_, p_163166_);
               }

            });
         }

      }
   }

   protected void handleDataMarker(String p_72698_, BlockPos p_72699_, ServerLevelAccessor p_72700_, Random p_72701_, BoundingBox p_72702_) {
   }

   private void maybeAddVines(Random p_72707_, LevelAccessor p_72708_, BlockPos p_72709_) {
      BlockState blockstate = p_72708_.getBlockState(p_72709_);
      if (!blockstate.isAir() && !blockstate.is(Blocks.VINE)) {
         Direction direction = getRandomHorizontalDirection(p_72707_);
         BlockPos blockpos = p_72709_.relative(direction);
         BlockState blockstate1 = p_72708_.getBlockState(blockpos);
         if (blockstate1.isAir()) {
            if (Block.isFaceFull(blockstate.getCollisionShape(p_72708_, p_72709_), direction)) {
               BooleanProperty booleanproperty = VineBlock.getPropertyForFace(direction.getOpposite());
               p_72708_.setBlock(blockpos, Blocks.VINE.defaultBlockState().setValue(booleanproperty, Boolean.valueOf(true)), 3);
            }
         }
      }
   }

   private void maybeAddLeavesAbove(Random p_72723_, LevelAccessor p_72724_, BlockPos p_72725_) {
      if (p_72723_.nextFloat() < 0.5F && p_72724_.getBlockState(p_72725_).is(Blocks.NETHERRACK) && p_72724_.getBlockState(p_72725_.above()).isAir()) {
         p_72724_.setBlock(p_72725_.above(), Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.valueOf(true)), 3);
      }

   }

   private void addNetherrackDripColumnsBelowPortal(Random p_72704_, LevelAccessor p_72705_) {
      for(int i = this.boundingBox.minX() + 1; i < this.boundingBox.maxX(); ++i) {
         for(int j = this.boundingBox.minZ() + 1; j < this.boundingBox.maxZ(); ++j) {
            BlockPos blockpos = new BlockPos(i, this.boundingBox.minY(), j);
            if (p_72705_.getBlockState(blockpos).is(Blocks.NETHERRACK)) {
               this.addNetherrackDripColumn(p_72704_, p_72705_, blockpos.below());
            }
         }
      }

   }

   private void addNetherrackDripColumn(Random p_72728_, LevelAccessor p_72729_, BlockPos p_72730_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_72730_.mutable();
      this.placeNetherrackOrMagma(p_72728_, p_72729_, blockpos$mutableblockpos);
      int i = 8;

      while(i > 0 && p_72728_.nextFloat() < 0.5F) {
         blockpos$mutableblockpos.move(Direction.DOWN);
         --i;
         this.placeNetherrackOrMagma(p_72728_, p_72729_, blockpos$mutableblockpos);
      }

   }

   private void spreadNetherrack(Random p_72720_, LevelAccessor p_72721_) {
      boolean flag = this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE || this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
      BlockPos blockpos = this.boundingBox.getCenter();
      int i = blockpos.getX();
      int j = blockpos.getZ();
      float[] afloat = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
      int k = afloat.length;
      int l = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
      int i1 = p_72720_.nextInt(Math.max(1, 8 - l / 2));
      int j1 = 3;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = BlockPos.ZERO.mutable();

      for(int k1 = i - k; k1 <= i + k; ++k1) {
         for(int l1 = j - k; l1 <= j + k; ++l1) {
            int i2 = Math.abs(k1 - i) + Math.abs(l1 - j);
            int j2 = Math.max(0, i2 + i1);
            if (j2 < k) {
               float f = afloat[j2];
               if (p_72720_.nextDouble() < (double)f) {
                  int k2 = getSurfaceY(p_72721_, k1, l1, this.verticalPlacement);
                  int l2 = flag ? k2 : Math.min(this.boundingBox.minY(), k2);
                  blockpos$mutableblockpos.set(k1, l2, l1);
                  if (Math.abs(l2 - this.boundingBox.minY()) <= 3 && this.canBlockBeReplacedByNetherrackOrMagma(p_72721_, blockpos$mutableblockpos)) {
                     this.placeNetherrackOrMagma(p_72720_, p_72721_, blockpos$mutableblockpos);
                     if (this.properties.overgrown) {
                        this.maybeAddLeavesAbove(p_72720_, p_72721_, blockpos$mutableblockpos);
                     }

                     this.addNetherrackDripColumn(p_72720_, p_72721_, blockpos$mutableblockpos.below());
                  }
               }
            }
         }
      }

   }

   private boolean canBlockBeReplacedByNetherrackOrMagma(LevelAccessor p_72675_, BlockPos p_72676_) {
      BlockState blockstate = p_72675_.getBlockState(p_72676_);
      return !blockstate.is(Blocks.AIR) && !blockstate.is(Blocks.OBSIDIAN) && !blockstate.is(BlockTags.FEATURES_CANNOT_REPLACE) && (this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.IN_NETHER || !blockstate.is(Blocks.LAVA));
   }

   private void placeNetherrackOrMagma(Random p_72732_, LevelAccessor p_72733_, BlockPos p_72734_) {
      if (!this.properties.cold && p_72732_.nextFloat() < 0.07F) {
         p_72733_.setBlock(p_72734_, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
      } else {
         p_72733_.setBlock(p_72734_, Blocks.NETHERRACK.defaultBlockState(), 3);
      }

   }

   private static int getSurfaceY(LevelAccessor p_72670_, int p_72671_, int p_72672_, RuinedPortalPiece.VerticalPlacement p_72673_) {
      return p_72670_.getHeight(getHeightMapType(p_72673_), p_72671_, p_72672_) - 1;
   }

   public static Heightmap.Types getHeightMapType(RuinedPortalPiece.VerticalPlacement p_72693_) {
      return p_72693_ == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;
   }

   private static ProcessorRule getBlockReplaceRule(Block p_72686_, float p_72687_, Block p_72688_) {
      return new ProcessorRule(new RandomBlockMatchTest(p_72686_, p_72687_), AlwaysTrueTest.INSTANCE, p_72688_.defaultBlockState());
   }

   private static ProcessorRule getBlockReplaceRule(Block p_72690_, Block p_72691_) {
      return new ProcessorRule(new BlockMatchTest(p_72690_), AlwaysTrueTest.INSTANCE, p_72691_.defaultBlockState());
   }

   public static class Properties {
      public static final Codec<RuinedPortalPiece.Properties> CODEC = RecordCodecBuilder.create((p_72752_) -> {
         return p_72752_.group(Codec.BOOL.fieldOf("cold").forGetter((p_163185_) -> {
            return p_163185_.cold;
         }), Codec.FLOAT.fieldOf("mossiness").forGetter((p_163183_) -> {
            return p_163183_.mossiness;
         }), Codec.BOOL.fieldOf("air_pocket").forGetter((p_163181_) -> {
            return p_163181_.airPocket;
         }), Codec.BOOL.fieldOf("overgrown").forGetter((p_163179_) -> {
            return p_163179_.overgrown;
         }), Codec.BOOL.fieldOf("vines").forGetter((p_163177_) -> {
            return p_163177_.vines;
         }), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter((p_163175_) -> {
            return p_163175_.replaceWithBlackstone;
         })).apply(p_72752_, RuinedPortalPiece.Properties::new);
      });
      public boolean cold;
      public float mossiness = 0.2F;
      public boolean airPocket;
      public boolean overgrown;
      public boolean vines;
      public boolean replaceWithBlackstone;

      public Properties() {
      }

      public Properties(boolean p_72745_, float p_72746_, boolean p_72747_, boolean p_72748_, boolean p_72749_, boolean p_72750_) {
         this.cold = p_72745_;
         this.mossiness = p_72746_;
         this.airPocket = p_72747_;
         this.overgrown = p_72748_;
         this.vines = p_72749_;
         this.replaceWithBlackstone = p_72750_;
      }
   }

   public static enum VerticalPlacement {
      ON_LAND_SURFACE("on_land_surface"),
      PARTLY_BURIED("partly_buried"),
      ON_OCEAN_FLOOR("on_ocean_floor"),
      IN_MOUNTAIN("in_mountain"),
      UNDERGROUND("underground"),
      IN_NETHER("in_nether");

      private static final Map<String, RuinedPortalPiece.VerticalPlacement> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(RuinedPortalPiece.VerticalPlacement::getName, (p_72781_) -> {
         return p_72781_;
      }));
      private final String name;

      private VerticalPlacement(String p_72778_) {
         this.name = p_72778_;
      }

      public String getName() {
         return this.name;
      }

      public static RuinedPortalPiece.VerticalPlacement byName(String p_72783_) {
         return BY_NAME.get(p_72783_);
      }
   }
}