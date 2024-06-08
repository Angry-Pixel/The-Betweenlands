package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class PillagerOutpostPools {
   public static final Holder<StructureTemplatePool> START = Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/base_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/base_plate"), 1)), StructureTemplatePool.Projection.RIGID));

   public static void bootstrap() {
   }

   static {
      Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/towers"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.list(ImmutableList.of(StructurePoolElement.legacy("pillager_outpost/watchtower"), StructurePoolElement.legacy("pillager_outpost/watchtower_overgrown", ProcessorLists.OUTPOST_ROT))), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/feature_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_plate"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
      Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/features"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage1"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage2"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_logs"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent1"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent2"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_targets"), 1), Pair.of(StructurePoolElement.empty(), 6)), StructureTemplatePool.Projection.RIGID));
   }
}