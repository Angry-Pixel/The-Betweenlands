package thebetweenlands.client.renderer.util.rendertype.modifier;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class RunnableModifier implements RenderTypeModifier<RunnableModifier.RunnableShard> {

	public static final RunnableModifier INSTANCE = new RunnableModifier(TheBetweenlands.prefix("custom"));
	
	protected final ResourceLocation id;
	
	public RunnableModifier(ResourceLocation id) {
		this.id = id;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public void beforeSetupRenderState(RenderType originalRenderType, RunnableShard context) {
		if(context.beforeSetupRenderState != null) {
			context.beforeSetupRenderState.run();
		}
	}

	@Override
	public void setupRenderState(RenderType originalRenderType, RunnableShard context) {
		if(context.setupRenderState != null) {
			context.setupRenderState.run();
		}
	}

	@Override
	public void clearRenderState(RenderType originalRenderType, RunnableShard context) {
		if(context.clearRenderState != null) {
			context.clearRenderState.run();
		}
	}

	@Override
	public void afterClearRenderState(RenderType originalRenderType, RunnableShard context) {
		if(context.afterClearRenderState != null) {
			context.afterClearRenderState.run();
		}
	}

	@Override
	public RunnableShard createDefaultData(RenderType originalRenderType) {
		return new RunnableShard();
	}

	public static class RunnableShard {
		public Runnable beforeSetupRenderState;
		public Runnable setupRenderState;
		public Runnable clearRenderState;
		public Runnable afterClearRenderState;

		public RunnableShard setBeforeSetupRenderState(Runnable runnable) {
			this.beforeSetupRenderState = runnable;
			return this;
		}

		public RunnableShard setSetupRenderState(Runnable runnable) {
			this.setupRenderState = runnable;
			return this;
		}

		public RunnableShard setClearRenderState(Runnable runnable) {
			this.clearRenderState = runnable;
			return this;
		}

		public RunnableShard setAfterClearRenderState(Runnable runnable) {
			this.afterClearRenderState = runnable;
			return this;
		}
	}
}
