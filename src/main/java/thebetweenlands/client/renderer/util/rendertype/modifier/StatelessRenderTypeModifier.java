package thebetweenlands.client.renderer.util.rendertype.modifier;

import net.minecraft.client.renderer.RenderType;

public interface StatelessRenderTypeModifier extends RenderTypeModifier<Void> {

	/**
	 * <p>Called <em>before</em> the default {@link RenderType#setupRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#beforeSetupRenderState(RenderType, Object)
	 */
	void beforeSetupRenderState(RenderType originalRenderType);

	/**
	 * <p>Called <em>after</em> the default {@link RenderType#setupRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#setupRenderState(RenderType, Object)
	 */
	void setupRenderState(RenderType originalRenderType);

	/**
	 * <p>Called <em>before</em> the default {@link RenderType#clearRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#clearRenderState(RenderType, Object)
	 */
	void clearRenderState(RenderType originalRenderType);

	/**
	 * <p>Called <em>after</em> the default {@link RenderType#clearRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#afterClearRenderState(RenderType, Object)
	 */
	void afterClearRenderState(RenderType originalRenderType);

	@Deprecated
	@Override
	default void beforeSetupRenderState(RenderType originalRenderType, Void context) {
		this.beforeSetupRenderState(originalRenderType);
	}

	@Deprecated
	@Override
	default void setupRenderState(RenderType originalRenderType, Void context) {
		this.setupRenderState(originalRenderType);
	}

	@Deprecated
	@Override
	default void clearRenderState(RenderType originalRenderType, Void context) {
		this.clearRenderState(originalRenderType);
	}

	@Deprecated
	@Override
	default void afterClearRenderState(RenderType originalRenderType, Void context) {
		this.afterClearRenderState(originalRenderType);
	}

	@Deprecated
	@Override
    default Void createDefaultData(RenderType originalRenderType) {
		return null;
	}
}
