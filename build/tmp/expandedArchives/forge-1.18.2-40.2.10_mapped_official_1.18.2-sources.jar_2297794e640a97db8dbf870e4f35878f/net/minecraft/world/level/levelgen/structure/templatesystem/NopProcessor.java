package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class NopProcessor extends StructureProcessor {
   public static final Codec<NopProcessor> CODEC = Codec.unit(() -> {
      return NopProcessor.INSTANCE;
   });
   public static final NopProcessor INSTANCE = new NopProcessor();

   private NopProcessor() {
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74180_, BlockPos p_74181_, BlockPos p_74182_, StructureTemplate.StructureBlockInfo p_74183_, StructureTemplate.StructureBlockInfo p_74184_, StructurePlaceSettings p_74185_) {
      return p_74184_;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.NOP;
   }
}