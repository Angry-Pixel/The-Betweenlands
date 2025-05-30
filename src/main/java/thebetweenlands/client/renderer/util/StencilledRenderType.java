package thebetweenlands.client.renderer.util;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.GlStateBackup;

/**
 * Renders as the delegate render type, but only within the 
 * stencilled area in stencilRenderer
 */
public class StencilledRenderType extends ProxyRenderType {

	protected final Runnable stencilRenderer;
	protected final Deque<StencilInfo> stencilStack;
	protected final StencilType stencilType;
	
	public StencilledRenderType(RenderType delegate, Runnable stencilRenderer, StencilType stencilType) {
		this("thebetweenlands:stencilled/" + delegate.name, delegate, stencilRenderer, stencilType);
	}
	
	public StencilledRenderType(String name, RenderType delegate, Runnable stencilRenderer, StencilType stencilType) {
		super(name, delegate);
		this.stencilRenderer = stencilRenderer;
		this.stencilStack = new ArrayDeque<>();
		this.stencilType = stencilType;
	}

	@Override
	public void setupRenderState() {
		// We can't just wrap this around the base
		// setupState Runnable if we want compatibility, unfortunately
		
        RenderSystem.assertOnRenderThread();
        

		// TODO get a better RenderTarget
		Stencil stencil = Stencil.reserve(Minecraft.getInstance().getMainRenderTarget());
		boolean stencilPushed = false;
		
		if(stencil != null && stencil.isValid()) {
			try {
				// Calculate stencil beforehand to avoid polluting the delegate's render state
				// (though most things should be covered by the state backup)
				GlStateBackup backup = new GlStateBackup();
				RenderSystem.backupGlState(backup);
				
				StencilState before = StencilState.get();
				
				// revert any changes to the stencil state (except to the stencil buffer itself) after running
				try (before) {
					GL11.glEnable(GL11.GL_STENCIL_TEST);
					
					if(this.stencilType == StencilType.STENCIL_IS_KEPT) {
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
					this.stencilRenderer.run();
				}
				
				// undo (most) changes that could be done by stencilRenderer
				RenderSystem.restoreGlState(backup);
	
				// default render state setup
				super.setupRenderState();
				
				// cache state after setup
				StencilState after = StencilState.get();
				
				// push stencil info to stack so it can be reverted in clearRenderState()
				this.stencilStack.addLast(new StencilInfo(stencil, before, after));
				stencilPushed = true;
				
				// enable stenciling
				GL11.glEnable(GL11.GL_STENCIL_TEST);
				// if previous stencil state was compatible (e.g. multiple StencilledRenderType have been applied), 
				// then we require that both stencils pass
				if(after.stencilTestEnabled() && after.stencilFunc() == GL11.GL_EQUAL) {
					int mask = stencil.getMask();
					RenderSystem.stencilFunc(GL11.GL_EQUAL, mask | after.stencilValueMask(), after.stencilWriteMask() & ~mask);
				} else {
					stencil.func(GL11.GL_EQUAL, true);
				}
				
				// don't change stencils when drawing
				stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
				
			} catch(Exception e) {
				// **NOT** a try-with-resources
				// if there is no exception, we expect the stencil to be closed in clearRenderState()
				stencil.close();
				
				// remove this last element off the stencil stack if we got to the point of adding one
				if(stencilPushed) {
					this.stencilStack.pollLast();
				}
				
				// because it's not impossible for the error to be handled and for clearRenderState()
				// to be called anyways, we have to keep the stack balanced to not lose the stencils
				this.stencilStack.addLast(new StencilInfo(Stencil.INVALID, null, null));
				throw e;
			}
		} else {
			// invalid or null stencil
			this.stencilStack.addLast(new StencilInfo(Stencil.INVALID, null, null));
			super.setupRenderState();
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
		StencilInfo stencilInfo = this.stencilStack.pollLast();
		
		// we no longer need the stencil around, close ASAP
		stencilInfo.stencil().close();
		
		// reverse order because working backwards
		if(stencilInfo.after() != null) {
			stencilInfo.after().close();
		}
		
		super.clearRenderState();

		// reverse order because working backwards
		if(stencilInfo.before() != null) {
			stencilInfo.before().close();
		};
	}
	
	public static record StencilState(boolean stencilTestEnabled, int stencilFunc, int stencilValueMask, int stencilWriteMask) implements AutoCloseable {
		public static StencilState get() {
	        RenderSystem.assertOnRenderThread();
			boolean stencilTestEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
			int stencilFunc = GlStateManager._getInteger(GL11.GL_STENCIL_FUNC);
			int stencilMask = GlStateManager._getInteger(GL11.GL_STENCIL_VALUE_MASK);
			int stencilWriteMask = GlStateManager._getInteger(GL11.GL_STENCIL_WRITEMASK);
			return new StencilState(stencilTestEnabled, stencilFunc, stencilMask, stencilWriteMask);
		}

		public void apply() {
			if(stencilTestEnabled) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);
			} else {
				GL11.glDisable(GL11.GL_STENCIL_TEST);
			}
			RenderSystem.stencilFunc(stencilFunc, stencilValueMask, stencilWriteMask);
		}
		
		@Override
		public void close() {
			this.apply();
		}
	}
	
	protected static record StencilInfo(Stencil stencil, StencilState before, StencilState after) {}
	
	public static enum StencilType {
		STENCIL_IS_KEPT,
		STENCIL_IS_REMOVED;
	}
}
