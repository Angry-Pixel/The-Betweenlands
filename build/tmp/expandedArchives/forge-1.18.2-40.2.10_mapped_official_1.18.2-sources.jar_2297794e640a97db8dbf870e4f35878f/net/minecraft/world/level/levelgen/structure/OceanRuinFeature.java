package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanRuinFeature extends StructureFeature<OceanRuinConfiguration> {
   public OceanRuinFeature(Codec<OceanRuinConfiguration> p_72474_) {
      super(p_72474_, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG), OceanRuinFeature::generatePieces));
   }

   private static void generatePieces(StructurePiecesBuilder p_197233_, PieceGenerator.Context<OceanRuinConfiguration> p_197234_) {
      BlockPos blockpos = new BlockPos(p_197234_.chunkPos().getMinBlockX(), 90, p_197234_.chunkPos().getMinBlockZ());
      Rotation rotation = Rotation.getRandom(p_197234_.random());
      OceanRuinPieces.addPieces(p_197234_.structureManager(), blockpos, rotation, p_197233_, p_197234_.random(), p_197234_.config());
   }

   public static enum Type implements StringRepresentable {
      WARM("warm"),
      COLD("cold");

      public static final Codec<OceanRuinFeature.Type> CODEC = StringRepresentable.fromEnum(OceanRuinFeature.Type::values, OceanRuinFeature.Type::byName);
      private static final Map<String, OceanRuinFeature.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(OceanRuinFeature.Type::getName, (p_72512_) -> {
         return p_72512_;
      }));
      private final String name;

      private Type(String p_72509_) {
         this.name = p_72509_;
      }

      public String getName() {
         return this.name;
      }

      @Nullable
      public static OceanRuinFeature.Type byName(String p_72514_) {
         return BY_NAME.get(p_72514_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}