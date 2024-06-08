package net.minecraft.client.profiling;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.profiling.ProfilerSamplerAdapter;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientMetricsSamplersProvider implements MetricsSamplerProvider {
   private final LevelRenderer levelRenderer;
   private final Set<MetricSampler> samplers = new ObjectOpenHashSet<>();
   private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

   public ClientMetricsSamplersProvider(LongSupplier p_172540_, LevelRenderer p_172541_) {
      this.levelRenderer = p_172541_;
      this.samplers.add(ServerMetricsSamplersProvider.tickTimeSampler(p_172540_));
      this.registerStaticSamplers();
   }

   private void registerStaticSamplers() {
      this.samplers.addAll(ServerMetricsSamplersProvider.runtimeIndependentSamplers());
      this.samplers.add(MetricSampler.create("totalChunks", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::getTotalChunks));
      this.samplers.add(MetricSampler.create("renderedChunks", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::countRenderedChunks));
      this.samplers.add(MetricSampler.create("lastViewDistance", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::getLastViewDistance));
      ChunkRenderDispatcher chunkrenderdispatcher = this.levelRenderer.getChunkRenderDispatcher();
      this.samplers.add(MetricSampler.create("toUpload", MetricCategory.CHUNK_RENDERING_DISPATCHING, chunkrenderdispatcher, ChunkRenderDispatcher::getToUpload));
      this.samplers.add(MetricSampler.create("freeBufferCount", MetricCategory.CHUNK_RENDERING_DISPATCHING, chunkrenderdispatcher, ChunkRenderDispatcher::getFreeBufferCount));
      this.samplers.add(MetricSampler.create("toBatchCount", MetricCategory.CHUNK_RENDERING_DISPATCHING, chunkrenderdispatcher, ChunkRenderDispatcher::getToBatchCount));
   }

   public Set<MetricSampler> samplers(Supplier<ProfileCollector> p_172544_) {
      this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(p_172544_));
      return this.samplers;
   }
}