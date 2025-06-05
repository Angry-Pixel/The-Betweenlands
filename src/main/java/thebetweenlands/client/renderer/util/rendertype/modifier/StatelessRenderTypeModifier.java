package thebetweenlands.client.renderer.util.rendertype.modifier;

import net.minecraft.client.renderer.RenderType;

public interface StatelessRenderTypeModifier extends RenderTypeModifier<Void> {

	/**
	 * <p>Called <em>before</em> the default {@link RenderType#setupRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#beforeSetupRenderState(RenderType, Object)
	 */
	public void beforeSetupRenderState(RenderType originalRenderType);

	/**
	 * <p>Called <em>after</em> the default {@link RenderType#setupRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#setupRenderState(RenderType, Object)
	 */
	public void setupRenderState(RenderType originalRenderType);

	/**
	 * <p>Called <em>before</em> the default {@link RenderType#clearRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#clearSetupRenderState(RenderType, Object)
	 */
	public void clearRenderState(RenderType originalRenderType);

	/**
	 * <p>Called <em>after</em> the default {@link RenderType#clearRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @see RenderTypeModifier#afterClearRenderState(RenderType, Object)
	 */
	public void afterClearRenderState(RenderType originalRenderType);
	
	@Deprecated
	@Override
	public default void beforeSetupRenderState(RenderType originalRenderType, Void context) {
		this.beforeSetupRenderState(originalRenderType);
	}

	@Deprecated
	@Override
	public default void setupRenderState(RenderType originalRenderType, Void context) {
		this.setupRenderState(originalRenderType);
	}

	@Deprecated
	@Override
	public default void clearRenderState(RenderType originalRenderType, Void context) {
		this.clearRenderState(originalRenderType);
	}

	@Deprecated
	@Override
	public default void afterClearRenderState(RenderType originalRenderType, Void context) {
		this.afterClearRenderState(originalRenderType);
	}

	@Deprecated
	@Override
	public default Void createDefaultData(RenderType originalRenderType) {
		return null;
	}
}
