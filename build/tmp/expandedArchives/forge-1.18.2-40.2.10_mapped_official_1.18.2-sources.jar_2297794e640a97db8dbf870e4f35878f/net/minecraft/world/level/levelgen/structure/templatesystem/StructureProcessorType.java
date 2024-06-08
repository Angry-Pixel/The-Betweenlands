package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;

public interface StructureProcessorType<P extends StructureProcessor> {
   StructureProcessorType<BlockIgnoreProcessor> BLOCK_IGNORE = register("block_ignore", BlockIgnoreProcessor.CODEC);
   StructureProcessorType<BlockRotProcessor> BLOCK_ROT = register("block_rot", BlockRotProcessor.CODEC);
   StructureProcessorType<GravityProcessor> GRAVITY = register("gravity", GravityProcessor.CODEC);
   StructureProcessorType<JigsawReplacementProcessor> JIGSAW_REPLACEMENT = register("jigsaw_replacement", JigsawReplacementProcessor.CODEC);
   StructureProcessorType<RuleProcessor> RULE = register("rule", RuleProcessor.CODEC);
   StructureProcessorType<NopProcessor> NOP = register("nop", NopProcessor.CODEC);
   StructureProcessorType<BlockAgeProcessor> BLOCK_AGE = register("block_age", BlockAgeProcessor.CODEC);
   StructureProcessorType<BlackstoneReplaceProcessor> BLACKSTONE_REPLACE = register("blackstone_replace", BlackstoneReplaceProcessor.CODEC);
   StructureProcessorType<LavaSubmergedBlockProcessor> LAVA_SUBMERGED_BLOCK = register("lava_submerged_block", LavaSubmergedBlockProcessor.CODEC);
   StructureProcessorType<ProtectedBlockProcessor> PROTECTED_BLOCKS = register("protected_blocks", ProtectedBlockProcessor.CODEC);
   Codec<StructureProcessor> SINGLE_CODEC = Registry.STRUCTURE_PROCESSOR.byNameCodec().dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
   Codec<StructureProcessorList> LIST_OBJECT_CODEC = SINGLE_CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::list);
   Codec<StructureProcessorList> DIRECT_CODEC = Codec.either(LIST_OBJECT_CODEC.fieldOf("processors").codec(), LIST_OBJECT_CODEC).xmap((p_74471_) -> {
      return p_74471_.map((p_163788_) -> {
         return p_163788_;
      }, (p_163786_) -> {
         return p_163786_;
      });
   }, Either::left);
   Codec<Holder<StructureProcessorList>> LIST_CODEC = RegistryFileCodec.create(Registry.PROCESSOR_LIST_REGISTRY, DIRECT_CODEC);

   Codec<P> codec();

   static <P extends StructureProcessor> StructureProcessorType<P> register(String p_74477_, Codec<P> p_74478_) {
      return Registry.register(Registry.STRUCTURE_PROCESSOR, p_74477_, () -> {
         return p_74478_;
      });
   }
}