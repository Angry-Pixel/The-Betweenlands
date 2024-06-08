package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class BastionPieces {
   public static final Holder<StructureTemplatePool> START = Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/starts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(StructurePoolElement.single("bastion/hoglin_stable/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(StructurePoolElement.single("bastion/treasure/big_air_full", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)), StructureTemplatePool.Projection.RIGID));

   public static void bootstrap() {
      BastionHousingUnitsPools.bootstrap();
      BastionHoglinStablePools.bootstrap();
      BastionTreasureRoomPools.bootstrap();
      BastionBridgePools.bootstrap();
      BastionSharedPools.bootstrap();
   }
}