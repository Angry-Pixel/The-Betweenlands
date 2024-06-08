package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class ShipwreckFeature extends StructureFeature<ShipwreckConfiguration> {
   public ShipwreckFeature(Codec<ShipwreckConfiguration> p_66782_) {
      super(p_66782_, PieceGeneratorSupplier.simple(ShipwreckFeature::checkLocation, ShipwreckFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<ShipwreckConfiguration> p_197155_) {
      Heightmap.Types heightmap$types = (p_197155_.config()).isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
      return p_197155_.validBiomeOnTop(heightmap$types);
   }

   private static void generatePieces(StructurePiecesBuilder p_197157_, PieceGenerator.Context<ShipwreckConfiguration> p_197158_) {
      Rotation rotation = Rotation.getRandom(p_197158_.random());
      BlockPos blockpos = new BlockPos(p_197158_.chunkPos().getMinBlockX(), 90, p_197158_.chunkPos().getMinBlockZ());
      ShipwreckPieces.addPieces(p_197158_.structureManager(), blockpos, rotation, p_197157_, p_197158_.random(), p_197158_.config());
   }
}