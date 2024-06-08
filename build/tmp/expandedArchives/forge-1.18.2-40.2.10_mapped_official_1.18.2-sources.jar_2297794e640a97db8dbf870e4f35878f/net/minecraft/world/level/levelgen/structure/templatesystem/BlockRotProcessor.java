package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class BlockRotProcessor extends StructureProcessor {
   public static final Codec<BlockRotProcessor> CODEC = Codec.FLOAT.fieldOf("integrity").orElse(1.0F).xmap(BlockRotProcessor::new, (p_74088_) -> {
      return p_74088_.integrity;
   }).codec();
   private final float integrity;

   public BlockRotProcessor(float p_74078_) {
      this.integrity = p_74078_;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74081_, BlockPos p_74082_, BlockPos p_74083_, StructureTemplate.StructureBlockInfo p_74084_, StructureTemplate.StructureBlockInfo p_74085_, StructurePlaceSettings p_74086_) {
      Random random = p_74086_.getRandom(p_74085_.pos);
      return !(this.integrity >= 1.0F) && !(random.nextFloat() <= this.integrity) ? null : p_74085_;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_ROT;
   }
}