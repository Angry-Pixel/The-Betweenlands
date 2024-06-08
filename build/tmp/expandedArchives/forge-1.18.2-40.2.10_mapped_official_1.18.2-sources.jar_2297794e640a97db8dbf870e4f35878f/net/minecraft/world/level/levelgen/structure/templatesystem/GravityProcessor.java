package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

public class GravityProcessor extends StructureProcessor {
   public static final Codec<GravityProcessor> CODEC = RecordCodecBuilder.create((p_74116_) -> {
      return p_74116_.group(Heightmap.Types.CODEC.fieldOf("heightmap").orElse(Heightmap.Types.WORLD_SURFACE_WG).forGetter((p_163729_) -> {
         return p_163729_.heightmap;
      }), Codec.INT.fieldOf("offset").orElse(0).forGetter((p_163727_) -> {
         return p_163727_.offset;
      })).apply(p_74116_, GravityProcessor::new);
   });
   private final Heightmap.Types heightmap;
   private final int offset;

   public GravityProcessor(Heightmap.Types p_74105_, int p_74106_) {
      this.heightmap = p_74105_;
      this.offset = p_74106_;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74109_, BlockPos p_74110_, BlockPos p_74111_, StructureTemplate.StructureBlockInfo p_74112_, StructureTemplate.StructureBlockInfo p_74113_, StructurePlaceSettings p_74114_) {
      Heightmap.Types heightmap$types;
      if (p_74109_ instanceof ServerLevel) {
         if (this.heightmap == Heightmap.Types.WORLD_SURFACE_WG) {
            heightmap$types = Heightmap.Types.WORLD_SURFACE;
         } else if (this.heightmap == Heightmap.Types.OCEAN_FLOOR_WG) {
            heightmap$types = Heightmap.Types.OCEAN_FLOOR;
         } else {
            heightmap$types = this.heightmap;
         }
      } else {
         heightmap$types = this.heightmap;
      }

      int i = p_74109_.getHeight(heightmap$types, p_74113_.pos.getX(), p_74113_.pos.getZ()) + this.offset;
      int j = p_74112_.pos.getY();
      return new StructureTemplate.StructureBlockInfo(new BlockPos(p_74113_.pos.getX(), i + j, p_74113_.pos.getZ()), p_74113_.state, p_74113_.nbt);
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.GRAVITY;
   }
}