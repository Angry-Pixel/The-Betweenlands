package thebetweenlands.client.renderer.util.rendertype;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.renderer.util.rendertype.modifier.RenderTypeModifier;

public class ModifiedRenderType extends ProxyRenderType implements ModifierSupportedRenderType {

	protected final ImmutableList<ModifierInstance<?>> modifierInstances;

	public ModifiedRenderType(RenderType delegate, ImmutableList<ModifierInstance<?>> modifiers) {
		this("thebetweenlands:modified/" + delegate.name, delegate, modifiers);
	}

	public ModifiedRenderType(RenderType delegate, List<RenderTypeModifier<?>> modifiers) {
		this("thebetweenlands:modified/" + delegate.name, delegate, modifiers);
	}

	public ModifiedRenderType(String name, RenderType delegate, List<RenderTypeModifier<?>> modifiers) {
		super(name, delegate);
		ImmutableList.Builder<ModifierInstance<?>> builder = ImmutableList.builder();
		for(RenderTypeModifier<?> modifier : modifiers) {
			builder.add(ModifierInstance.create(modifier, delegate));
		}
		this.modifierInstances = builder.build();
	}

	public ModifiedRenderType(String name, RenderType delegate, ImmutableList<ModifierInstance<?>> modifiers) {
		super(name, delegate);
		this.modifierInstances = modifiers;
	}

	@Override
	public void setupRenderState() {
		final RenderType delegate = this.getDelegate();

		final var modifiers = this.modifierInstances;
		
		// ImmutableList implements RandomAccess
		for(int i = modifiers.size() - 1; i >= 0; --i) {
			modifiers.get(i).beforeSetupRenderState(delegate);
		}
		
		super.setupRenderState();

		// ImmutableList implements RandomAccess
		for(int i = 0; i < modifiers.size(); ++i) {
			modifiers.get(i).setupRenderState(delegate);
		}
	}
	
	@Override
	public void draw(MeshData meshData) {
		this.setupRenderState();
        BufferUploader.drawWithShader(meshData);
        this.clearRenderState();
	}
	
	@Override
	public void clearRenderState() {
		final RenderType delegate = this.getDelegate();

		final var modifiers = this.modifierInstances;
		
		// ImmutableList implements RandomAccess
		for(int i = modifiers.size() - 1; i >= 0; --i) {
			// have to be called in the opposite order as setupRenderState
			modifiers.get(i).clearRenderState(delegate);
		}
		
		super.clearRenderState();

		// ImmutableList implements RandomAccess
		for(int i = 0; i < modifiers.size(); ++i) {
			// have to be called in the opposite order as beforeSetupRenderState
			modifiers.get(i).afterClearRenderState(delegate);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> getContextOptional(RenderTypeModifier<T> modifier) {
		if(modifier != null) {
			for(ModifierInstance<?> instance : this.modifierInstances) {
				if(instance.modifier() == modifier) {
					return Optional.of((T)instance.context());
				}
			}
		}
		return Optional.empty();
	}
	
	@Override
	public <T> Optional<T> getContextOptional(Class<T> contextType, ResourceLocation modifierId) {
		Objects.requireNonNull(contextType);
		for(ModifierInstance<?> instance : this.modifierInstances) {
			// check modifier id is correct
			if(Objects.equals(instance.modifier().getId(), modifierId)) {
				// check if context is compatible with class
				Object context = instance.context();
				if(contextType.isInstance(context)) {
					return Optional.of(contextType.cast(context));
				}
			}
		}
		return Optional.empty();
	}
	
	public static record ModifierInstance<T>(RenderTypeModifier<T> modifier, T context) {
		public void beforeSetupRenderState(RenderType originalRenderType) {
			modifier.beforeSetupRenderState(originalRenderType, context);
		}

		public void setupRenderState(RenderType originalRenderType) {
			modifier.setupRenderState(originalRenderType, context);
		}

		public void clearRenderState(RenderType originalRenderType) {
			modifier.clearRenderState(originalRenderType, context);
		}

		public void afterClearRenderState(RenderType originalRenderType) {
			modifier.afterClearRenderState(originalRenderType, context);
		}
		
		public static <T> ModifierInstance<T> create(RenderTypeModifier<T> modifier, RenderType renderType) {
			return new ModifierInstance<T>(modifier, modifier.createDefaultData(renderType));
		}
	}

}
