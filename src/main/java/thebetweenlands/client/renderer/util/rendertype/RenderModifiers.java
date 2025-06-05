package thebetweenlands.client.renderer.util.rendertype;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import thebetweenlands.client.renderer.util.ProxyMultiBufferSource;
import thebetweenlands.client.renderer.util.rendertype.modifier.RenderTypeModifier;

public class RenderModifiers {

    public final String name;
    public final ImmutableList<RenderTypeModifier<?>> modifiers;
    
	public RenderModifiers(String name, List<RenderTypeModifier<?>> modifiers) {
		this.name = name;
		this.modifiers = ImmutableList.copyOf(modifiers);
	}
	
	public String getName() {
		return this.name;
	}

	/**
	 * <p>Applies modifiers to a RenderType, without modifying the original RenderType</p>
	 * <p>General contract: the return value must also be an instance of {@link ModifierSupportedRenderType}</p>
	 * @param renderType
	 * @return
	 */
	public RenderType apply(RenderType renderType) {
		return new ModifiedRenderType(renderType, modifiers);
	}

	public RenderType compute(RenderType originalRenderType, @Nullable RenderType currentRenderType) {
		if(currentRenderType != null) {
			return currentRenderType;
		} else {
			return this.apply(originalRenderType);
		}
	}

	public MultiBufferSource wrapBufferSource(MultiBufferSource multiBufferSource) {
		return new ProxyMultiBufferSource(multiBufferSource, this::compute);
	}


	public RenderType applyAndConfigure(RenderType renderType, ModifierConfigurator configurator) {
		RenderType modifiedRenderType = this.apply(renderType);
		if(renderType != null) {
			configurator.configure(renderType, (ModifierSupportedRenderType)modifiedRenderType);
		}
		return modifiedRenderType;
	}

	public RenderType computeAndConfigure(RenderType originalRenderType, @Nullable RenderType currentRenderType, ModifierConfigurator configurator) {
		if(currentRenderType != null) {
			return currentRenderType;
		} else {
			return this.applyAndConfigure(originalRenderType, configurator);
		}
	}
	
	public MultiBufferSource wrapBufferSourceAndConfigure(MultiBufferSource multiBufferSource, ModifierConfigurator configurator) {
		return new ProxyMultiBufferSource(multiBufferSource, (a, b) -> this.computeAndConfigure(a, b, configurator));
	}
	
	public static Builder build() {
		return new Builder();
	}
	
	public static RenderModifiers create(String name, RenderTypeModifier<?> ...modifiers) {
		return new RenderModifiers(name, ImmutableList.copyOf(modifiers));
	}
	
	public static class Builder {
		protected final ImmutableList.Builder<RenderTypeModifier<?>> modifiers;
		
		public Builder() {
			this.modifiers = ImmutableList.builder();
		}
		
		public Builder add(RenderTypeModifier<?> modifier) {
			this.modifiers.add(modifier);
			return this;
		}

		public RenderModifiers build(String name) {
			return new RenderModifiers(name, modifiers.build());
		}

		public CachingRenderModifiers buildWithCache(String name) {
			return new CachingRenderModifiers(name, modifiers.build());
		}
		
		public CachingRenderModifiers buildWithCacheAndConfiguration(String name, ModifierConfigurator configurator) {
			return new CachingRenderModifiers(name, modifiers.build(), configurator);
		}
	}
	
	@FunctionalInterface
	public static interface ModifierConfigurator {
		public static final ModifierConfigurator IDENTITY = (originalRenderType, modifiers) -> {};
		
		public void configure(RenderType originalRenderType, ModifierSupportedRenderType modifiers);
	}
	
	public static class CachingRenderModifiers extends RenderModifiers {
		protected final Map<RenderType, RenderType> cache = new WeakHashMap<RenderType, RenderType>();
		protected final ModifierConfigurator configurator;

		public CachingRenderModifiers(String name, List<RenderTypeModifier<?>> modifiers) {
			this(name, modifiers, ModifierConfigurator.IDENTITY);
		}
		
		public CachingRenderModifiers(String name, List<RenderTypeModifier<?>> modifiers, ModifierConfigurator configurator) {
			super(name, modifiers);
			this.configurator = configurator;
		}
		
		@Override
		public RenderType apply(RenderType renderType) {
			return this.cache.computeIfAbsent(renderType, (type) -> {
				RenderType modifiedRenderType = super.apply(type);
				if(modifiedRenderType instanceof ModifierSupportedRenderType modifiers) {
					configurator.configure(renderType, modifiers);
				}
				return modifiedRenderType;
			});
		}
		
		@Override
		public RenderType applyAndConfigure(RenderType renderType, ModifierConfigurator configurator) {
			throw new UnsupportedOperationException("CachingRenderModifiers can not use dynamic configurators");
		}

		@Override
		public RenderType computeAndConfigure(RenderType originalRenderType, @Nullable RenderType currentRenderType, ModifierConfigurator configurator) {
			throw new UnsupportedOperationException("CachingRenderModifiers can not use dynamic configurators");
		}

		@Override
		public MultiBufferSource wrapBufferSourceAndConfigure(MultiBufferSource multiBufferSource, ModifierConfigurator configurator) {
			throw new UnsupportedOperationException("CachingRenderModifiers can not use dynamic configurators");
		}

		/**
		 * @param renderType the key to check the cache of
		 * @return if this contains a cache entry for the {@code renderType}
		 * @see Map#containsKey(Object)
		 */
		public boolean containsKey(RenderType renderType) {
			return this.cache.containsKey(renderType);
		}

		/**
		 * @param renderType the key to check the cache of
		 * @return the cached value of the {@code renderType}, or {@code null} if it is not mapped
		 * @see Map#get(Object)
		 */
		@Nullable
		public RenderType get(RenderType renderType) {
			return this.cache.get(renderType);
		}

		/**
		 * @param renderType the key to remove the cache of
		 * @return the cached value of the {@code renderType}, or {@code null} if it is not mapped
		 * @see Map#remove(Object)
		 */
		@Nullable
		public RenderType remove(RenderType renderType) {
			return this.cache.remove(renderType);
		}

		/**
		 * Clear the cache for this modifier
		 * @see Map#clear()
		 */
		public void clear() {
			this.cache.clear();
		}

		/**
		 * Clear the cache for this modifier
		 * @return this instance
		 * @see Map#clear()
		 */
		public CachingRenderModifiers invalidate() {
			this.clear();
			return this;
		}
	}
}
