package thebetweenlands.client.renderer.util.rendertype.modifier;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * 
 * <p>
 *     If multiple modifiers are applied to a single {@link RenderType}, they should be applied in a last-in-first-out (LIFO) order. <br/>
 *     The modifier that had {@linkplain #beforeSetupRenderState(RenderType, Object)} called last
 *     should have {@linkplain #setupRenderState(RenderType, Object)} called first.
 * </p>
 * <p>
 *     Additionally, the {@linkplain #clearRenderState(RenderType, Object)} and {@linkplain #afterClearRenderState(RenderType, Object)} methods must be called in the reverse of the order 
 *     that {@linkplain #setupRenderState(RenderType, Object)} and {@linkplain #beforeSetupRenderState(RenderType, Object)} were called, respectively.
 * </p>
 * <p>
 *     Implementors should assume all methods are called while on the render thread and they do not need to call {@linkplain RenderSystem#assertOnRenderThread()}.
 * </p>
 * @param <Context> a mutable data storage class, allowing a single modifier to be re-used multiple times
 */
public interface RenderTypeModifier<Context> {

	/**
	 * Used to uniquely identify an individual modifier
	 */
	@Nonnull
	public ResourceLocation getId();
	
	/**
	 * <p>Called <em>before</em> the default {@link RenderType#setupRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @param context A custom data storage class for this modifier; this should be mutable if you want to transfer data between methods
	 */
	public void beforeSetupRenderState(RenderType originalRenderType, Context context);

	/**
	 * <p>Called <em>after</em> the default {@link RenderType#setupRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @param context A custom data storage class for this modifier
	 */
	public void setupRenderState(RenderType originalRenderType, Context context);

	/**
	 * <p>Called <em>before</em> the default {@link RenderType#clearRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @param context A custom data storage class for this modifier
	 */
	public void clearRenderState(RenderType originalRenderType, Context context);

	/**
	 * <p>Called <em>after</em> the default {@link RenderType#clearRenderState()} method is called</p>
	 * @param originalRenderType The original render type that is being modified
	 * @param context A custom data storage class for this modifier
	 */
	public void afterClearRenderState(RenderType originalRenderType, Context context);
	
	/**
	 * @return a new instance of the modifier's default context
	 */
	public Context createDefaultData(RenderType originalRenderType);
}
