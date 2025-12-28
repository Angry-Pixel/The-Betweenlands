package thebetweenlands.client.renderer.util.rendertype.modifier;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.GlStateBackup;
import thebetweenlands.client.renderer.util.Stencil;
import thebetweenlands.client.renderer.util.StencilInfo;
import thebetweenlands.client.renderer.util.StencilState;
import thebetweenlands.client.renderer.util.StencilType;
import thebetweenlands.common.TheBetweenlands;

public class StencilModifier implements RenderTypeModifier<StencilModifier.StencilContext> {

	// People should use INSTANCE and a configurator if they only need to use one stencil
	public static final StencilModifier INSTANCE = new StencilModifier(TheBetweenlands.prefix("stencil"));

	protected final ResourceLocation id;

	public StencilModifier(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class StencilContext {
		protected Runnable stencilRenderer;
		protected Deque<StencilInfo> stencilStack;
		protected StencilType stencilType;

		public StencilContext setStencilType(StencilType stencilType) {
			this.stencilType = Objects.requireNonNull(stencilType);
			return this;
		}

		public StencilContext setStencilRenderer(Runnable stencilRenderer) {
			this.stencilRenderer = stencilRenderer;
			return this;
		}

		public StencilType getStencilType() {
			return this.stencilType;
		}

		public Runnable getStencilRenderer() {
			return this.stencilRenderer;
		}

		public Deque<StencilInfo> getStencilStack() {
			if(this.stencilStack == null) {
				this.stencilStack = new ArrayDeque<>();
			}
			return this.stencilStack;
		}
	}

	@Override
	public void beforeSetupRenderState(RenderType originalRenderType, StencilContext context) {

		StencilType stencilType = Objects.requireNonNull(context.getStencilType());
		Runnable stencilRenderer = Objects.requireNonNull(context.getStencilRenderer());
		Deque<StencilInfo> stencilStack = Objects.requireNonNull(context.getStencilStack());

		// TODO get a better RenderTarget
		Stencil stencil = Stencil.reserve(Minecraft.getInstance().getMainRenderTarget());

		if(stencil == null || !stencil.isValid()) {
			// invalid or null stencil
			stencilStack.addLast(StencilInfo.INVALID);
		} else {
			boolean stencilPushed = false;

			try {
				// Calculate stencil beforehand to avoid polluting the delegate's render state
				// (though most things should be covered by the state backup)
				GlStateBackup backup = new GlStateBackup();
				RenderSystem.backupGlState(backup);

				StencilState before = StencilState.get();

				// revert any changes to the stencil state (except to the stencil buffer itself) after running
				try (before) {
					GL11.glEnable(GL11.GL_STENCIL_TEST);

					if(stencilType == StencilType.STENCIL_IS_KEPT) {
						stencil.setAllZeros();
						// every time a pixel is drawn in stencilRenderer, the stencil bit will be set to 1
						stencil.func(GL11.GL_ALWAYS, true);
					} else {
						stencil.setAllOnes();
						// every time a pixel is drawn in stencilRenderer, the stencil bit will be set to 0
						stencil.func(GL11.GL_ALWAYS, false);
					}

					stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

					// draw primitives etc that get converted into the stencil mask
					stencilRenderer.run();
				}

				// undo (most) changes that could be done by stencilRenderer
				RenderSystem.restoreGlState(backup);

				// push stencil info to stack so it can be reverted in clearRenderState()
				stencilStack.addLast(new StencilInfo(stencil, before, null));
				stencilPushed = true;

			} catch(Exception e) {
				// **NOT** a try-with-resources
				// if there is no exception, we expect the stencil to be closed in clearRenderState()
				stencil.close();

				// remove this last element off the stencil stack if we got to the point of adding one
				if(stencilPushed) {
					stencilStack.pollLast();
				}

				// because it's not impossible for the error to be handled and for setupRenderState()/clearRenderState()
				// to be called anyways, we have to keep the stack balanced to not lose the stencils
				stencilStack.addLast(StencilInfo.INVALID);
				throw e;
			}
		}
	}

	@Override
	public void setupRenderState(RenderType originalRenderType, StencilContext context) {

		Deque<StencilInfo> stencilStack = Objects.requireNonNull(context.getStencilStack());

		StencilInfo stencilInfo = Objects.requireNonNull(stencilStack.pollLast());
		boolean stencilPushed = false;

		Stencil stencil = stencilInfo.stencil();

		if(stencil == null || !stencil.isValid()) {
			// invalid or null stencil
			stencilStack.addLast(StencilInfo.INVALID);
		} else {
			try {
				// cache state after setup
				StencilState after = StencilState.get();

				// push stencil info to stack so it can be reverted in clearRenderState()
				stencilStack.addLast(new StencilInfo(stencil, stencilInfo.before(), after));
				stencilPushed = true;

//				TheBetweenlands.LOGGER.info("Stencil Modifier {}, second stencil state: {}", this.getId(), after);

				// enable stenciling
				GL11.glEnable(GL11.GL_STENCIL_TEST);
				// if previous stencil state was compatible (e.g. multiple StencilledRenderType have been applied),
				// then we require that both stencils pass
				if(after.stencilTestEnabled() && after.stencilFunc() == GL11.GL_EQUAL) {
//					TheBetweenlands.LOGGER.info("Compatible stencil state found, current stencil is {}", stencil);
					@SuppressWarnings("removal")
					int mask = stencil.getMask();
					RenderSystem.stencilFunc(GL11.GL_EQUAL, mask | after.stencilRef(), after.stencilMask() | mask);
//					TheBetweenlands.LOGGER.info("New stencil stencil state is {}", StencilState.get());
				} else {
					stencil.func(GL11.GL_EQUAL, true);
				}

				// don't change stencils when drawing
				stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			} catch(Exception e) {
				if(stencil != null) {
					// **NOT** a try-with-resources
					// if there is no exception, we expect the stencil to be closed in clearRenderState()
					stencil.close();
				}

				// remove this last element off the stencil stack if we got to the point of adding one
				if(stencilPushed) {
					stencilStack.pollLast();
				}

				// because it's not impossible for the error to be handled and for clearRenderState()
				// to be called anyways, we have to keep the stack balanced to not lose the stencils
				stencilStack.addLast(StencilInfo.INVALID);
				throw e;
			}
		}
	}

	@Override
	public void clearRenderState(RenderType originalRenderType, StencilContext context) {
		StencilInfo stencilInfo = context.getStencilStack().peekLast();

		// we no longer need the stencil around, close ASAP
		stencilInfo.stencil().close();

		// reverse order because working backwards
		// (leaving stencil data as it was before beforeSetupRenderState)
		if(stencilInfo.after() != null) {
			stencilInfo.after().close();
		}
	}

	@Override
	public void afterClearRenderState(RenderType originalRenderType, StencilContext context) {
		StencilInfo stencilInfo = context.getStencilStack().pollLast();

		// reverse order because working backwards
		// (leaving stencil data as it was before setupRenderState)
		if(stencilInfo.before() != null) {
			stencilInfo.before().close();
		}
    }

	@Override
	public StencilContext createDefaultData(RenderType originalRenderType) {
		return new StencilContext();
	}

}
