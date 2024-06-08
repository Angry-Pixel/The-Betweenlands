package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.QuartPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class NetherFortressFeature extends StructureFeature<NoneFeatureConfiguration> {
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES = WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4));

   public NetherFortressFeature(Codec<NoneFeatureConfiguration> p_66384_) {
      super(p_66384_, PieceGeneratorSupplier.simple(NetherFortressFeature::checkLocation, NetherFortressFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> p_197127_) {
      return p_197127_.validBiome().test(p_197127_.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(p_197127_.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(64), QuartPos.fromBlock(p_197127_.chunkPos().getMiddleBlockZ())));
   }

   private static void generatePieces(StructurePiecesBuilder p_197129_, PieceGenerator.Context<NoneFeatureConfiguration> p_197130_) {
      NetherBridgePieces.StartPiece netherbridgepieces$startpiece = new NetherBridgePieces.StartPiece(p_197130_.random(), p_197130_.chunkPos().getBlockX(2), p_197130_.chunkPos().getBlockZ(2));
      p_197129_.addPiece(netherbridgepieces$startpiece);
      netherbridgepieces$startpiece.addChildren(netherbridgepieces$startpiece, p_197129_, p_197130_.random());
      List<StructurePiece> list = netherbridgepieces$startpiece.pendingChildren;

      while(!list.isEmpty()) {
         int i = p_197130_.random().nextInt(list.size());
         StructurePiece structurepiece = list.remove(i);
         structurepiece.addChildren(netherbridgepieces$startpiece, p_197129_, p_197130_.random());
      }

      p_197129_.moveInsideHeights(p_197130_.random(), 48, 70);
   }
}