package thebetweenlands.client.renderer.util.rendertype;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.renderer.util.rendertype.modifier.RenderTypeModifier;

public interface ModifierSupportedRenderType {

	/**
	 * Get the current context for the specified modifier, or {@code null} if absent
	 * @param <T> the context type
	 * @param modifier the modifier to get the context for
	 * @return the current context for the modifier, or {@code null}
	 */
	@Nullable
	public default <T> T getContext(RenderTypeModifier<T> modifier) {
		return this.getContextOptional(modifier).orElse(null);
	}

	/**
	 * Get the current context for the specified modifier, if it exists
	 * @param <T> the context type
	 * @param modifier the modifier to get the context for
	 * @return an optional of the current context for the modifier, or an absent one if the modifier does not exist
	 */
	public <T> Optional<T> getContextOptional(RenderTypeModifier<T> modifier);

	/**
	 * Get the current context for the modifier with the specified id, if it exists
	 * @param <T> the context type
	 * @param contextType the class of the modifier's context
	 * @param modifierId the id of the modifier to get the context for
	 * @return an optional of the current context for the modifier, or an absent one of the modifier does not exist or has an incompatible context type
	 */
	public <T> Optional<T> getContextOptional(Class<T> contextType, ResourceLocation modifierId);
	
}
