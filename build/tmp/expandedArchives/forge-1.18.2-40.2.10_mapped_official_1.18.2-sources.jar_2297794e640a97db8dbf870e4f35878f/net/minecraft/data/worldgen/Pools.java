package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class Pools {
   public static final ResourceKey<StructureTemplatePool> EMPTY = ResourceKey.create(Registry.TEMPLATE_POOL_REGISTRY, new ResourceLocation("empty"));
   private static final Holder<StructureTemplatePool> BUILTIN_EMPTY = register(new StructureTemplatePool(EMPTY.location(), EMPTY.location(), ImmutableList.of(), StructureTemplatePool.Projection.RIGID));

   public static Holder<StructureTemplatePool> register(StructureTemplatePool p_211104_) {
      return BuiltinRegistries.register(BuiltinRegistries.TEMPLATE_POOL, p_211104_.getName(), p_211104_);
   }

   public static Holder<StructureTemplatePool> bootstrap() {
      BastionPieces.bootstrap();
      PillagerOutpostPools.bootstrap();
      VillagePools.bootstrap();
      return BUILTIN_EMPTY;
   }

   static {
      bootstrap();
   }
}