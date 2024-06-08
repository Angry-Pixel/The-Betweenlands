package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderers {
   private static final Map<BlockEntityType<?>, BlockEntityRendererProvider<?>> PROVIDERS = new java.util.concurrent.ConcurrentHashMap<>();

   public static <T extends BlockEntity> void register(BlockEntityType<? extends T> p_173591_, BlockEntityRendererProvider<T> p_173592_) {
      PROVIDERS.put(p_173591_, p_173592_);
   }

   public static Map<BlockEntityType<?>, BlockEntityRenderer<?>> createEntityRenderers(BlockEntityRendererProvider.Context p_173599_) {
      Builder<BlockEntityType<?>, BlockEntityRenderer<?>> builder = ImmutableMap.builder();
      PROVIDERS.forEach((p_173596_, p_173597_) -> {
         try {
            builder.put(p_173596_, p_173597_.create(p_173599_));
         } catch (Exception exception) {
            throw new IllegalStateException("Failed to create model for " + Registry.BLOCK_ENTITY_TYPE.getKey(p_173596_), exception);
         }
      });
      return builder.build();
   }

   static {
      register(BlockEntityType.SIGN, SignRenderer::new);
      register(BlockEntityType.MOB_SPAWNER, SpawnerRenderer::new);
      register(BlockEntityType.PISTON, PistonHeadRenderer::new);
      register(BlockEntityType.CHEST, ChestRenderer::new);
      register(BlockEntityType.ENDER_CHEST, ChestRenderer::new);
      register(BlockEntityType.TRAPPED_CHEST, ChestRenderer::new);
      register(BlockEntityType.ENCHANTING_TABLE, EnchantTableRenderer::new);
      register(BlockEntityType.LECTERN, LecternRenderer::new);
      register(BlockEntityType.END_PORTAL, TheEndPortalRenderer::new);
      register(BlockEntityType.END_GATEWAY, TheEndGatewayRenderer::new);
      register(BlockEntityType.BEACON, BeaconRenderer::new);
      register(BlockEntityType.SKULL, SkullBlockRenderer::new);
      register(BlockEntityType.BANNER, BannerRenderer::new);
      register(BlockEntityType.STRUCTURE_BLOCK, StructureBlockRenderer::new);
      register(BlockEntityType.SHULKER_BOX, ShulkerBoxRenderer::new);
      register(BlockEntityType.BED, BedRenderer::new);
      register(BlockEntityType.CONDUIT, ConduitRenderer::new);
      register(BlockEntityType.BELL, BellRenderer::new);
      register(BlockEntityType.CAMPFIRE, CampfireRenderer::new);
   }
}
