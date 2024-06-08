package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public class BlockLightSectionStorage extends LayerLightSectionStorage<BlockLightSectionStorage.BlockDataLayerStorageMap> {
   protected BlockLightSectionStorage(LightChunkGetter p_75511_) {
      super(LightLayer.BLOCK, p_75511_, new BlockLightSectionStorage.BlockDataLayerStorageMap(new Long2ObjectOpenHashMap<>()));
   }

   protected int getLightValue(long p_75513_) {
      long i = SectionPos.blockToSection(p_75513_);
      DataLayer datalayer = this.getDataLayer(i, false);
      return datalayer == null ? 0 : datalayer.get(SectionPos.sectionRelative(BlockPos.getX(p_75513_)), SectionPos.sectionRelative(BlockPos.getY(p_75513_)), SectionPos.sectionRelative(BlockPos.getZ(p_75513_)));
   }

   protected static final class BlockDataLayerStorageMap extends DataLayerStorageMap<BlockLightSectionStorage.BlockDataLayerStorageMap> {
      public BlockDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> p_75515_) {
         super(p_75515_);
      }

      public BlockLightSectionStorage.BlockDataLayerStorageMap copy() {
         return new BlockLightSectionStorage.BlockDataLayerStorageMap(this.map.clone());
      }
   }
}