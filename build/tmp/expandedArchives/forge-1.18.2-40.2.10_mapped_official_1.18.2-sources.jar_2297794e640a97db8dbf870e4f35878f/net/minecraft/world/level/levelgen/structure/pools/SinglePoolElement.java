package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class SinglePoolElement extends StructurePoolElement {
   private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC = Codec.of(SinglePoolElement::encodeTemplate, ResourceLocation.CODEC.map(Either::left));
   public static final Codec<SinglePoolElement> CODEC = RecordCodecBuilder.create((p_210429_) -> {
      return p_210429_.group(templateCodec(), processorsCodec(), projectionCodec()).apply(p_210429_, SinglePoolElement::new);
   });
   protected final Either<ResourceLocation, StructureTemplate> template;
   protected final Holder<StructureProcessorList> processors;

   private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> p_210425_, DynamicOps<T> p_210426_, T p_210427_) {
      Optional<ResourceLocation> optional = p_210425_.left();
      return !optional.isPresent() ? DataResult.error("Can not serialize a runtime pool element") : ResourceLocation.CODEC.encode(optional.get(), p_210426_, p_210427_);
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
      return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter((p_210464_) -> {
         return p_210464_.processors;
      });
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
      return TEMPLATE_CODEC.fieldOf("location").forGetter((p_210431_) -> {
         return p_210431_.template;
      });
   }

   protected SinglePoolElement(Either<ResourceLocation, StructureTemplate> p_210415_, Holder<StructureProcessorList> p_210416_, StructureTemplatePool.Projection p_210417_) {
      super(p_210417_);
      this.template = p_210415_;
      this.processors = p_210416_;
   }

   public SinglePoolElement(StructureTemplate p_210419_) {
      this(Either.right(p_210419_), ProcessorLists.EMPTY, StructureTemplatePool.Projection.RIGID);
   }

   public Vec3i getSize(StructureManager p_210446_, Rotation p_210447_) {
      StructureTemplate structuretemplate = this.getTemplate(p_210446_);
      return structuretemplate.getSize(p_210447_);
   }

   private StructureTemplate getTemplate(StructureManager p_210433_) {
      return this.template.map(p_210433_::getOrCreate, Function.identity());
   }

   public List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureManager p_210458_, BlockPos p_210459_, Rotation p_210460_, boolean p_210461_) {
      StructureTemplate structuretemplate = this.getTemplate(p_210458_);
      List<StructureTemplate.StructureBlockInfo> list = structuretemplate.filterBlocks(p_210459_, (new StructurePlaceSettings()).setRotation(p_210460_), Blocks.STRUCTURE_BLOCK, p_210461_);
      List<StructureTemplate.StructureBlockInfo> list1 = Lists.newArrayList();

      for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : list) {
         if (structuretemplate$structureblockinfo.nbt != null) {
            StructureMode structuremode = StructureMode.valueOf(structuretemplate$structureblockinfo.nbt.getString("mode"));
            if (structuremode == StructureMode.DATA) {
               list1.add(structuretemplate$structureblockinfo);
            }
         }
      }

      return list1;
   }

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager p_210453_, BlockPos p_210454_, Rotation p_210455_, Random p_210456_) {
      StructureTemplate structuretemplate = this.getTemplate(p_210453_);
      List<StructureTemplate.StructureBlockInfo> list = structuretemplate.filterBlocks(p_210454_, (new StructurePlaceSettings()).setRotation(p_210455_), Blocks.JIGSAW, true);
      Collections.shuffle(list, p_210456_);
      return list;
   }

   public BoundingBox getBoundingBox(StructureManager p_210449_, BlockPos p_210450_, Rotation p_210451_) {
      StructureTemplate structuretemplate = this.getTemplate(p_210449_);
      return structuretemplate.getBoundingBox((new StructurePlaceSettings()).setRotation(p_210451_), p_210450_);
   }

   public boolean place(StructureManager p_210435_, WorldGenLevel p_210436_, StructureFeatureManager p_210437_, ChunkGenerator p_210438_, BlockPos p_210439_, BlockPos p_210440_, Rotation p_210441_, BoundingBox p_210442_, Random p_210443_, boolean p_210444_) {
      StructureTemplate structuretemplate = this.getTemplate(p_210435_);
      StructurePlaceSettings structureplacesettings = this.getSettings(p_210441_, p_210442_, p_210444_);
      if (!structuretemplate.placeInWorld(p_210436_, p_210439_, p_210440_, structureplacesettings, p_210443_, 18)) {
         return false;
      } else {
         for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : StructureTemplate.processBlockInfos(p_210436_, p_210439_, p_210440_, structureplacesettings, this.getDataMarkers(p_210435_, p_210439_, p_210441_, false))) {
            this.handleDataMarker(p_210436_, structuretemplate$structureblockinfo, p_210439_, p_210441_, p_210443_, p_210442_);
         }

         return true;
      }
   }

   protected StructurePlaceSettings getSettings(Rotation p_210421_, BoundingBox p_210422_, boolean p_210423_) {
      StructurePlaceSettings structureplacesettings = new StructurePlaceSettings();
      structureplacesettings.setBoundingBox(p_210422_);
      structureplacesettings.setRotation(p_210421_);
      structureplacesettings.setKnownShape(true);
      structureplacesettings.setIgnoreEntities(false);
      structureplacesettings.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      structureplacesettings.setFinalizeEntities(true);
      if (!p_210423_) {
         structureplacesettings.addProcessor(JigsawReplacementProcessor.INSTANCE);
      }

      this.processors.value().list().forEach(structureplacesettings::addProcessor);
      this.getProjection().getProcessors().forEach(structureplacesettings::addProcessor);
      return structureplacesettings;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.SINGLE;
   }

   public String toString() {
      return "Single[" + this.template + "]";
   }
}