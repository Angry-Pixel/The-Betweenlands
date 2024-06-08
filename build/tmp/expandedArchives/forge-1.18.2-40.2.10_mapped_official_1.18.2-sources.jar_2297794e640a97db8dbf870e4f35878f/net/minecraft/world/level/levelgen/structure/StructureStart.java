package net.minecraft.world.level.levelgen.structure;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public final class StructureStart {
   public static final String INVALID_START_ID = "INVALID";
   public static final StructureStart INVALID_START = new StructureStart((ConfiguredStructureFeature<?, ?>)null, new ChunkPos(0, 0), 0, new PiecesContainer(List.of()));
   private final ConfiguredStructureFeature<?, ?> feature;
   private final PiecesContainer pieceContainer;
   private final ChunkPos chunkPos;
   private int references;
   @Nullable
   private volatile BoundingBox cachedBoundingBox;

   public StructureStart(ConfiguredStructureFeature<?, ?> p_210077_, ChunkPos p_210078_, int p_210079_, PiecesContainer p_210080_) {
      this.feature = p_210077_;
      this.chunkPos = p_210078_;
      this.references = p_210079_;
      this.pieceContainer = p_210080_;
   }

   public BoundingBox getBoundingBox() {
      BoundingBox boundingbox = this.cachedBoundingBox;
      if (boundingbox == null) {
         boundingbox = this.feature.adjustBoundingBox(this.pieceContainer.calculateBoundingBox());
         this.cachedBoundingBox = boundingbox;
      }

      return boundingbox;
   }

   public void placeInChunk(WorldGenLevel p_73584_, StructureFeatureManager p_73585_, ChunkGenerator p_73586_, Random p_73587_, BoundingBox p_73588_, ChunkPos p_73589_) {
      List<StructurePiece> list = this.pieceContainer.pieces();
      if (!list.isEmpty()) {
         BoundingBox boundingbox = (list.get(0)).boundingBox;
         BlockPos blockpos = boundingbox.getCenter();
         BlockPos blockpos1 = new BlockPos(blockpos.getX(), boundingbox.minY(), blockpos.getZ());

         for(StructurePiece structurepiece : list) {
            if (structurepiece.getBoundingBox().intersects(p_73588_)) {
               structurepiece.postProcess(p_73584_, p_73585_, p_73586_, p_73587_, p_73588_, p_73589_, blockpos1);
            }
         }

         this.feature.feature.getPostPlacementProcessor().afterPlace(p_73584_, p_73585_, p_73586_, p_73587_, p_73588_, p_73589_, this.pieceContainer);
      }
   }

   public CompoundTag createTag(StructurePieceSerializationContext p_192661_, ChunkPos p_192662_) {
      CompoundTag compoundtag = new CompoundTag();
      if (this.isValid()) {
         if (p_192661_.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getKey(this.getFeature()) == null) { // FORGE: This is just a more friendly error instead of the 'Null String' below
            throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\": \"" + this.getFeature() + "\" missing ID Mapping, Modder see MapGenStructureIO");
         }
         compoundtag.putString("id", p_192661_.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getKey(this.feature).toString());
         compoundtag.putInt("ChunkX", p_192662_.x);
         compoundtag.putInt("ChunkZ", p_192662_.z);
         compoundtag.putInt("references", this.references);
         compoundtag.put("Children", this.pieceContainer.save(p_192661_));
         return compoundtag;
      } else {
         compoundtag.putString("id", "INVALID");
         return compoundtag;
      }
   }

   public boolean isValid() {
      return !this.pieceContainer.isEmpty();
   }

   public ChunkPos getChunkPos() {
      return this.chunkPos;
   }

   public boolean canBeReferenced() {
      return this.references < this.getMaxReferences();
   }

   public void addReference() {
      ++this.references;
   }

   public int getReferences() {
      return this.references;
   }

   protected int getMaxReferences() {
      return 1;
   }

   public ConfiguredStructureFeature<?, ?> getFeature() {
      return this.feature;
   }

   public List<StructurePiece> getPieces() {
      return this.pieceContainer.pieces();
   }
}
