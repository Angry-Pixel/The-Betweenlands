package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class StructurePoolElement {
   public static final Codec<StructurePoolElement> CODEC = Registry.STRUCTURE_POOL_ELEMENT.byNameCodec().dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
   @Nullable
   private volatile StructureTemplatePool.Projection projection;

   protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructureTemplatePool.Projection> projectionCodec() {
      return StructureTemplatePool.Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
   }

   protected StructurePoolElement(StructureTemplatePool.Projection p_210471_) {
      this.projection = p_210471_;
   }

   public abstract Vec3i getSize(StructureManager p_210493_, Rotation p_210494_);

   public abstract List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager p_210498_, BlockPos p_210499_, Rotation p_210500_, Random p_210501_);

   public abstract BoundingBox getBoundingBox(StructureManager p_210495_, BlockPos p_210496_, Rotation p_210497_);

   public abstract boolean place(StructureManager p_210483_, WorldGenLevel p_210484_, StructureFeatureManager p_210485_, ChunkGenerator p_210486_, BlockPos p_210487_, BlockPos p_210488_, Rotation p_210489_, BoundingBox p_210490_, Random p_210491_, boolean p_210492_);

   public abstract StructurePoolElementType<?> getType();

   public void handleDataMarker(LevelAccessor p_210473_, StructureTemplate.StructureBlockInfo p_210474_, BlockPos p_210475_, Rotation p_210476_, Random p_210477_, BoundingBox p_210478_) {
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection p_210479_) {
      this.projection = p_210479_;
      return this;
   }

   public StructureTemplatePool.Projection getProjection() {
      StructureTemplatePool.Projection structuretemplatepool$projection = this.projection;
      if (structuretemplatepool$projection == null) {
         throw new IllegalStateException();
      } else {
         return structuretemplatepool$projection;
      }
   }

   public int getGroundLevelDelta() {
      return 1;
   }

   public static Function<StructureTemplatePool.Projection, EmptyPoolElement> empty() {
      return (p_210525_) -> {
         return EmptyPoolElement.INSTANCE;
      };
   }

   public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String p_210508_) {
      return (p_210530_) -> {
         return new LegacySinglePoolElement(Either.left(new ResourceLocation(p_210508_)), ProcessorLists.EMPTY, p_210530_);
      };
   }

   public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String p_210513_, Holder<StructureProcessorList> p_210514_) {
      return (p_210537_) -> {
         return new LegacySinglePoolElement(Either.left(new ResourceLocation(p_210513_)), p_210514_, p_210537_);
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String p_210527_) {
      return (p_210511_) -> {
         return new SinglePoolElement(Either.left(new ResourceLocation(p_210527_)), ProcessorLists.EMPTY, p_210511_);
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String p_210532_, Holder<StructureProcessorList> p_210533_) {
      return (p_210518_) -> {
         return new SinglePoolElement(Either.left(new ResourceLocation(p_210532_)), p_210533_, p_210518_);
      };
   }

   public static Function<StructureTemplatePool.Projection, FeaturePoolElement> feature(Holder<PlacedFeature> p_210503_) {
      return (p_210506_) -> {
         return new FeaturePoolElement(p_210503_, p_210506_);
      };
   }

   public static Function<StructureTemplatePool.Projection, ListPoolElement> list(List<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>> p_210520_) {
      return (p_210523_) -> {
         return new ListPoolElement(p_210520_.stream().map((p_210482_) -> {
            return p_210482_.apply(p_210523_);
         }).collect(Collectors.toList()), p_210523_);
      };
   }
}