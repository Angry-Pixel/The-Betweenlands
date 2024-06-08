package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LavaSubmergedBlockProcessor extends StructureProcessor {
   public static final Codec<LavaSubmergedBlockProcessor> CODEC = Codec.unit(() -> {
      return LavaSubmergedBlockProcessor.INSTANCE;
   });
   public static final LavaSubmergedBlockProcessor INSTANCE = new LavaSubmergedBlockProcessor();

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74140_, BlockPos p_74141_, BlockPos p_74142_, StructureTemplate.StructureBlockInfo p_74143_, StructureTemplate.StructureBlockInfo p_74144_, StructurePlaceSettings p_74145_) {
      BlockPos blockpos = p_74144_.pos;
      boolean flag = p_74140_.getBlockState(blockpos).is(Blocks.LAVA);
      return flag && !Block.isShapeFullBlock(p_74144_.state.getShape(p_74140_, blockpos)) ? new StructureTemplate.StructureBlockInfo(blockpos, Blocks.LAVA.defaultBlockState(), p_74144_.nbt) : p_74144_;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
   }
}