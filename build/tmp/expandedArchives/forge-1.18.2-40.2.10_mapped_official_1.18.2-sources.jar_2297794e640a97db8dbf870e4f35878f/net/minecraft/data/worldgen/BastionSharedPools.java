package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class BastionSharedPools {
   public static void bootstrap() {
   }

   static {
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/mobs/piglin"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/mobs/melee_piglin"), 1), Pair.of(StructurePoolElement.single("bastion/mobs/sword_piglin"), 4), Pair.of(StructurePoolElement.single("bastion/mobs/crossbow_piglin"), 4), Pair.of(StructurePoolElement.single("bastion/mobs/empty"), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/mobs/hoglin"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/mobs/hoglin"), 2), Pair.of(StructurePoolElement.single("bastion/mobs/empty"), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/blocks/gold"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/blocks/air"), 3), Pair.of(StructurePoolElement.single("bastion/blocks/gold"), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/mobs/piglin_melee"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/mobs/melee_piglin_always"), 1), Pair.of(StructurePoolElement.single("bastion/mobs/melee_piglin"), 5), Pair.of(StructurePoolElement.single("bastion/mobs/sword_piglin"), 1)), StructureTemplatePool.Projection.RIGID));
   }
}