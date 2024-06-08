package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class MineshaftFeature extends StructureFeature<MineshaftConfiguration> {
   public MineshaftFeature(Codec<MineshaftConfiguration> p_66273_) {
      super(p_66273_, PieceGeneratorSupplier.simple(MineshaftFeature::checkLocation, MineshaftFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<MineshaftConfiguration> p_197122_) {
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setLargeFeatureSeed(p_197122_.seed(), p_197122_.chunkPos().x, p_197122_.chunkPos().z);
      double d0 = (double)(p_197122_.config()).probability;
      return worldgenrandom.nextDouble() >= d0 ? false : p_197122_.validBiome().test(p_197122_.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(p_197122_.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(50), QuartPos.fromBlock(p_197122_.chunkPos().getMiddleBlockZ())));
   }

   private static void generatePieces(StructurePiecesBuilder p_197124_, PieceGenerator.Context<MineshaftConfiguration> p_197125_) {
      MineShaftPieces.MineShaftRoom mineshaftpieces$mineshaftroom = new MineShaftPieces.MineShaftRoom(0, p_197125_.random(), p_197125_.chunkPos().getBlockX(2), p_197125_.chunkPos().getBlockZ(2), (p_197125_.config()).type);
      p_197124_.addPiece(mineshaftpieces$mineshaftroom);
      mineshaftpieces$mineshaftroom.addChildren(mineshaftpieces$mineshaftroom, p_197124_, p_197125_.random());
      int i = p_197125_.chunkGenerator().getSeaLevel();
      if ((p_197125_.config()).type == MineshaftFeature.Type.MESA) {
         BlockPos blockpos = p_197124_.getBoundingBox().getCenter();
         int j = p_197125_.chunkGenerator().getBaseHeight(blockpos.getX(), blockpos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, p_197125_.heightAccessor());
         int k = j <= i ? i : Mth.randomBetweenInclusive(p_197125_.random(), i, j);
         int l = k - blockpos.getY();
         p_197124_.offsetPiecesVertically(l);
      } else {
         p_197124_.moveBelowSeaLevel(i, p_197125_.chunkGenerator().getMinY(), p_197125_.random(), 10);
      }

   }

   public static enum Type implements StringRepresentable {
      NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
      MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

      public static final Codec<MineshaftFeature.Type> CODEC = StringRepresentable.fromEnum(MineshaftFeature.Type::values, MineshaftFeature.Type::byName);
      private static final Map<String, MineshaftFeature.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(MineshaftFeature.Type::getName, (p_66333_) -> {
         return p_66333_;
      }));
      private final String name;
      private final BlockState woodState;
      private final BlockState planksState;
      private final BlockState fenceState;

      private Type(String p_160057_, Block p_160058_, Block p_160059_, Block p_160060_) {
         this.name = p_160057_;
         this.woodState = p_160058_.defaultBlockState();
         this.planksState = p_160059_.defaultBlockState();
         this.fenceState = p_160060_.defaultBlockState();
      }

      public String getName() {
         return this.name;
      }

      private static MineshaftFeature.Type byName(String p_66335_) {
         return BY_NAME.get(p_66335_);
      }

      public static MineshaftFeature.Type byId(int p_66331_) {
         return p_66331_ >= 0 && p_66331_ < values().length ? values()[p_66331_] : NORMAL;
      }

      public BlockState getWoodState() {
         return this.woodState;
      }

      public BlockState getPlanksState() {
         return this.planksState;
      }

      public BlockState getFenceState() {
         return this.fenceState;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}