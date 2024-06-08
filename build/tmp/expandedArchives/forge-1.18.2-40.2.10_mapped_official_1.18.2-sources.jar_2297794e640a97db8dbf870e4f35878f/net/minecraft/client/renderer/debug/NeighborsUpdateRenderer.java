package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

   NeighborsUpdateRenderer(Minecraft p_113595_) {
      this.minecraft = p_113595_;
   }

   public void addUpdate(long p_113597_, BlockPos p_113598_) {
      Map<BlockPos, Integer> map = this.lastUpdate.computeIfAbsent(p_113597_, (p_113606_) -> {
         return Maps.newHashMap();
      });
      int i = map.getOrDefault(p_113598_, 0);
      map.put(p_113598_, i + 1);
   }

   public void render(PoseStack p_113600_, MultiBufferSource p_113601_, double p_113602_, double p_113603_, double p_113604_) {
      long i = this.minecraft.level.getGameTime();
      int j = 200;
      double d0 = 0.0025D;
      Set<BlockPos> set = Sets.newHashSet();
      Map<BlockPos, Integer> map = Maps.newHashMap();
      VertexConsumer vertexconsumer = p_113601_.getBuffer(RenderType.lines());
      Iterator<Entry<Long, Map<BlockPos, Integer>>> iterator = this.lastUpdate.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<Long, Map<BlockPos, Integer>> entry = iterator.next();
         Long olong = entry.getKey();
         Map<BlockPos, Integer> map1 = entry.getValue();
         long k = i - olong;
         if (k > 200L) {
            iterator.remove();
         } else {
            for(Entry<BlockPos, Integer> entry1 : map1.entrySet()) {
               BlockPos blockpos = entry1.getKey();
               Integer integer = entry1.getValue();
               if (set.add(blockpos)) {
                  AABB aabb = (new AABB(BlockPos.ZERO)).inflate(0.002D).deflate(0.0025D * (double)k).move((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()).move(-p_113602_, -p_113603_, -p_113604_);
                  LevelRenderer.renderLineBox(p_113600_, vertexconsumer, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
                  map.put(blockpos, integer);
               }
            }
         }
      }

      for(Entry<BlockPos, Integer> entry2 : map.entrySet()) {
         BlockPos blockpos1 = entry2.getKey();
         Integer integer1 = entry2.getValue();
         DebugRenderer.renderFloatingText(String.valueOf((Object)integer1), blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), -1);
      }

   }
}