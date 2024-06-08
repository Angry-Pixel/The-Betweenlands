package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.slf4j.Logger;

public class StructureTemplatePool {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SIZE_UNSET = Integer.MIN_VALUE;
   public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create((p_210575_) -> {
      return p_210575_.group(ResourceLocation.CODEC.fieldOf("name").forGetter(StructureTemplatePool::getName), ResourceLocation.CODEC.fieldOf("fallback").forGetter(StructureTemplatePool::getFallback), Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter((p_210579_) -> {
         return p_210579_.rawTemplates;
      })).apply(p_210575_, StructureTemplatePool::new);
   });
   public static final Codec<Holder<StructureTemplatePool>> CODEC = RegistryFileCodec.create(Registry.TEMPLATE_POOL_REGISTRY, DIRECT_CODEC);
   private final ResourceLocation name;
   private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
   private final List<StructurePoolElement> templates;
   private final ResourceLocation fallback;
   private int maxSize = Integer.MIN_VALUE;

   public StructureTemplatePool(ResourceLocation p_210565_, ResourceLocation p_210566_, List<Pair<StructurePoolElement, Integer>> p_210567_) {
      this.name = p_210565_;
      this.rawTemplates = p_210567_;
      this.templates = Lists.newArrayList();

      for(Pair<StructurePoolElement, Integer> pair : p_210567_) {
         StructurePoolElement structurepoolelement = pair.getFirst();

         for(int i = 0; i < pair.getSecond(); ++i) {
            this.templates.add(structurepoolelement);
         }
      }

      this.fallback = p_210566_;
   }

   public StructureTemplatePool(ResourceLocation p_210569_, ResourceLocation p_210570_, List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> p_210571_, StructureTemplatePool.Projection p_210572_) {
      this.name = p_210569_;
      this.rawTemplates = Lists.newArrayList();
      this.templates = Lists.newArrayList();

      for(Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> pair : p_210571_) {
         StructurePoolElement structurepoolelement = pair.getFirst().apply(p_210572_);
         this.rawTemplates.add(Pair.of(structurepoolelement, pair.getSecond()));

         for(int i = 0; i < pair.getSecond(); ++i) {
            this.templates.add(structurepoolelement);
         }
      }

      this.fallback = p_210570_;
   }

   public int getMaxSize(StructureManager p_210581_) {
      if (this.maxSize == Integer.MIN_VALUE) {
         this.maxSize = this.templates.stream().filter((p_210577_) -> {
            return p_210577_ != EmptyPoolElement.INSTANCE;
         }).mapToInt((p_210584_) -> {
            return p_210584_.getBoundingBox(p_210581_, BlockPos.ZERO, Rotation.NONE).getYSpan();
         }).max().orElse(0);
      }

      return this.maxSize;
   }

   public ResourceLocation getFallback() {
      return this.fallback;
   }

   public StructurePoolElement getRandomTemplate(Random p_210586_) {
      return this.templates.get(p_210586_.nextInt(this.templates.size()));
   }

   public List<StructurePoolElement> getShuffledTemplates(Random p_210589_) {
      return ImmutableList.copyOf(ObjectArrays.shuffle(this.templates.toArray(new StructurePoolElement[0]), p_210589_));
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public int size() {
      return this.templates.size();
   }

   public static enum Projection implements StringRepresentable {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      public static final Codec<StructureTemplatePool.Projection> CODEC = StringRepresentable.fromEnum(StructureTemplatePool.Projection::values, StructureTemplatePool.Projection::byName);
      private static final Map<String, StructureTemplatePool.Projection> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(StructureTemplatePool.Projection::getName, (p_210606_) -> {
         return p_210606_;
      }));
      private final String name;
      private final ImmutableList<StructureProcessor> processors;

      private Projection(String p_210602_, ImmutableList<StructureProcessor> p_210603_) {
         this.name = p_210602_;
         this.processors = p_210603_;
      }

      public String getName() {
         return this.name;
      }

      public static StructureTemplatePool.Projection byName(String p_210608_) {
         return BY_NAME.get(p_210608_);
      }

      public ImmutableList<StructureProcessor> getProcessors() {
         return this.processors;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}