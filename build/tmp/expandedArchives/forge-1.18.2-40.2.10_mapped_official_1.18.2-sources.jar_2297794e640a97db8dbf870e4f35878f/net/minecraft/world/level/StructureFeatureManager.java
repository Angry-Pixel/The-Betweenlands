package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.FeatureAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureFeatureManager {
   private final LevelAccessor level;
   private final WorldGenSettings worldGenSettings;
   private final StructureCheck structureCheck;

   public StructureFeatureManager(LevelAccessor p_196667_, WorldGenSettings p_196668_, StructureCheck p_196669_) {
      this.level = p_196667_;
      this.worldGenSettings = p_196668_;
      this.structureCheck = p_196669_;
   }

   public StructureFeatureManager forWorldGenRegion(WorldGenRegion p_47273_) {
      if (p_47273_.getLevel() != this.level) {
         throw new IllegalStateException("Using invalid feature manager (source level: " + p_47273_.getLevel() + ", region: " + p_47273_);
      } else {
         return new StructureFeatureManager(p_47273_, this.worldGenSettings, this.structureCheck);
      }
   }

   public List<StructureStart> startsForFeature(SectionPos p_207812_, Predicate<ConfiguredStructureFeature<?, ?>> p_207813_) {
      Map<ConfiguredStructureFeature<?, ?>, LongSet> map = this.level.getChunk(p_207812_.x(), p_207812_.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
      Builder<StructureStart> builder = ImmutableList.builder();

      for(Entry<ConfiguredStructureFeature<?, ?>, LongSet> entry : map.entrySet()) {
         ConfiguredStructureFeature<?, ?> configuredstructurefeature = entry.getKey();
         if (p_207813_.test(configuredstructurefeature)) {
            this.fillStartsForFeature(configuredstructurefeature, entry.getValue(), builder::add);
         }
      }

      return builder.build();
   }

   public List<StructureStart> startsForFeature(SectionPos p_207795_, ConfiguredStructureFeature<?, ?> p_207796_) {
      LongSet longset = this.level.getChunk(p_207795_.x(), p_207795_.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(p_207796_);
      Builder<StructureStart> builder = ImmutableList.builder();
      this.fillStartsForFeature(p_207796_, longset, builder::add);
      return builder.build();
   }

   public void fillStartsForFeature(ConfiguredStructureFeature<?, ?> p_207782_, LongSet p_207783_, Consumer<StructureStart> p_207784_) {
      for(long i : p_207783_) {
         SectionPos sectionpos = SectionPos.of(new ChunkPos(i), this.level.getMinSection());
         StructureStart structurestart = this.getStartForFeature(sectionpos, p_207782_, this.level.getChunk(sectionpos.x(), sectionpos.z(), ChunkStatus.STRUCTURE_STARTS));
         if (structurestart != null && structurestart.isValid()) {
            p_207784_.accept(structurestart);
         }
      }

   }

   @Nullable
   public StructureStart getStartForFeature(SectionPos p_207803_, ConfiguredStructureFeature<?, ?> p_207804_, FeatureAccess p_207805_) {
      return p_207805_.getStartForFeature(p_207804_);
   }

   public void setStartForFeature(SectionPos p_207807_, ConfiguredStructureFeature<?, ?> p_207808_, StructureStart p_207809_, FeatureAccess p_207810_) {
      p_207810_.setStartForFeature(p_207808_, p_207809_);
   }

   public void addReferenceForFeature(SectionPos p_207798_, ConfiguredStructureFeature<?, ?> p_207799_, long p_207800_, FeatureAccess p_207801_) {
      p_207801_.addReferenceForFeature(p_207799_, p_207800_);
   }

   public boolean shouldGenerateFeatures() {
      return this.worldGenSettings.generateFeatures();
   }

   public StructureStart getStructureAt(BlockPos p_207786_, ConfiguredStructureFeature<?, ?> p_207787_) {
      for(StructureStart structurestart : this.startsForFeature(SectionPos.of(p_207786_), p_207787_)) {
         if (structurestart.getBoundingBox().isInside(p_207786_)) {
            return structurestart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public StructureStart getStructureWithPieceAt(BlockPos p_207792_, ResourceKey<ConfiguredStructureFeature<?, ?>> p_207793_) {
      ConfiguredStructureFeature<?, ?> configuredstructurefeature = this.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(p_207793_);
      return configuredstructurefeature == null ? StructureStart.INVALID_START : this.getStructureWithPieceAt(p_207792_, configuredstructurefeature);
   }

   public StructureStart getStructureWithPieceAt(BlockPos p_207818_, ConfiguredStructureFeature<?, ?> p_207819_) {
      for(StructureStart structurestart : this.startsForFeature(SectionPos.of(p_207818_), p_207819_)) {
         if (this.structureHasPieceAt(p_207818_, structurestart)) {
            return structurestart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public boolean structureHasPieceAt(BlockPos p_207789_, StructureStart p_207790_) {
      for(StructurePiece structurepiece : p_207790_.getPieces()) {
         if (structurepiece.getBoundingBox().isInside(p_207789_)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasAnyStructureAt(BlockPos p_186606_) {
      SectionPos sectionpos = SectionPos.of(p_186606_);
      return this.level.getChunk(sectionpos.x(), sectionpos.z(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
   }

   public Map<ConfiguredStructureFeature<?, ?>, LongSet> getAllStructuresAt(BlockPos p_207816_) {
      SectionPos sectionpos = SectionPos.of(p_207816_);
      return this.level.getChunk(sectionpos.x(), sectionpos.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
   }

   public StructureCheckResult checkStructurePresence(ChunkPos p_207778_, ConfiguredStructureFeature<?, ?> p_207779_, boolean p_207780_) {
      return this.structureCheck.checkStart(p_207778_, p_207779_, p_207780_);
   }

   public void addReference(StructureStart p_196675_) {
      p_196675_.addReference();
      this.structureCheck.incrementReference(p_196675_.getChunkPos(), p_196675_.getFeature());
   }

   public RegistryAccess registryAccess() {
      return this.level.registryAccess();
   }
}