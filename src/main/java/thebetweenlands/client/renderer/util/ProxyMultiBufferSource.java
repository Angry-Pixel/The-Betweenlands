package thebetweenlands.client.renderer.util;

import java.util.Map;
import java.util.function.BiFunction;

import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/**
 * Used to swap out the RenderType and VertexConsumer from a delegate MultiBufferSource
 */
public class ProxyMultiBufferSource implements MultiBufferSource {

	protected final MultiBufferSource delegate;
	protected final Map<RenderType, RenderType> proxyCache;
	protected final BiFunction<RenderType, RenderType, RenderType> proxyFunction;
	protected final VertexConsumerProxy vertexConsumerProxy;

	public ProxyMultiBufferSource(MultiBufferSource delegate, BiFunction<RenderType, RenderType, RenderType> proxyFunction) {
		this(delegate, new Object2ObjectArrayMap<>(), proxyFunction, VertexConsumerProxy.IDENTITY);
	}

	public ProxyMultiBufferSource(MultiBufferSource delegate, VertexConsumerProxy vertexConsumerProxy) {
		this(delegate, new Object2ObjectArrayMap<>(), ((original, proxy) -> proxy != null ? proxy : original), vertexConsumerProxy);
	}

	public ProxyMultiBufferSource(MultiBufferSource delegate, BiFunction<RenderType, RenderType, RenderType> proxyFunction, VertexConsumerProxy vertexConsumerProxy) {
		this(delegate, new Object2ObjectArrayMap<>(), proxyFunction, vertexConsumerProxy);
	}

	public ProxyMultiBufferSource(MultiBufferSource delegate, Map<RenderType, RenderType> proxyCache, BiFunction<RenderType, RenderType, RenderType> proxyFunction, VertexConsumerProxy vertexConsumerProxy) {
		this.delegate = delegate;
		this.proxyCache = proxyCache;
		this.proxyFunction = proxyFunction;
		this.vertexConsumerProxy = vertexConsumerProxy;
	}
	
	public MultiBufferSource getDelegate() {
		return this.delegate;
	}

	public Map<RenderType, RenderType> getCache() {
		return this.proxyCache;
	}

	public void clearCache() {
		this.proxyCache.clear();
	}

	public BiFunction<RenderType, RenderType, RenderType> getProxyFunction() {
		return this.proxyFunction;
	}
	
	public VertexConsumerProxy getVertexConsumerProxy() {
		return this.vertexConsumerProxy;
	}
	
	public RenderType getRenderType(RenderType renderType) {
		return this.proxyCache.compute(renderType, this.proxyFunction);
	}
	
	@Override
	public VertexConsumer getBuffer(RenderType renderType) {
		RenderType proxyRenderType = this.getRenderType(renderType);
		if(proxyRenderType == null) {
			return NULL_CONSUMER;
		}
		VertexConsumer vertexConsumer = this.delegate.getBuffer(proxyRenderType);
		return this.vertexConsumerProxy.apply(renderType, proxyRenderType, vertexConsumer);
	}

	@FunctionalInterface
	public static interface VertexConsumerProxy {
		public static final VertexConsumerProxy IDENTITY = ((originalRenderType, proxyRenderType, vertexConsumer) -> vertexConsumer);
		
		public VertexConsumer apply(RenderType originalRenderType, RenderType proxyRenderType, VertexConsumer vertexConsumer);
	}
	
	public static final VertexConsumer NULL_CONSUMER = new VertexConsumer() {
		
		@Override
		public VertexConsumer setUv2(int u, int v) {
			return this;
		}
		
		@Override
		public VertexConsumer setUv1(int u, int v) {
			return this;
		}
		
		@Override
		public VertexConsumer setUv(float u, float v) {
			return this;
		}
		
		@Override
		public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
			return this;
		}
		
		@Override
		public VertexConsumer setColor(int red, int green, int blue, int alpha) {
			return this;
		}
		
		@Override
		public VertexConsumer addVertex(float x, float y, float z) {
			return this;
		}
	};
}
