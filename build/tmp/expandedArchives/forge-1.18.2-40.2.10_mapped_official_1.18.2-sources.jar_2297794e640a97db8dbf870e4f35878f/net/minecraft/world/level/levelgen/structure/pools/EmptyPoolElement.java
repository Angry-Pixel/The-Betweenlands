package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class EmptyPoolElement extends StructurePoolElement {
   public static final Codec<EmptyPoolElement> CODEC = Codec.unit(() -> {
      return EmptyPoolElement.INSTANCE;
   });
   public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

   private EmptyPoolElement() {
      super(StructureTemplatePool.Projection.TERRAIN_MATCHING);
   }

   public Vec3i getSize(StructureManager p_210191_, Rotation p_210192_) {
      return Vec3i.ZERO;
   }

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager p_210198_, BlockPos p_210199_, Rotation p_210200_, Random p_210201_) {
      return Collections.emptyList();
   }

   public BoundingBox getBoundingBox(StructureManager p_210194_, BlockPos p_210195_, Rotation p_210196_) {
      throw new IllegalStateException("Invalid call to EmtyPoolElement.getBoundingBox, filter me!");
   }

   public boolean place(StructureManager p_210180_, WorldGenLevel p_210181_, StructureFeatureManager p_210182_, ChunkGenerator p_210183_, BlockPos p_210184_, BlockPos p_210185_, Rotation p_210186_, BoundingBox p_210187_, Random p_210188_, boolean p_210189_) {
      return true;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.EMPTY;
   }

   public String toString() {
      return "Empty";
   }
}