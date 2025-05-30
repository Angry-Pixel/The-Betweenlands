package thebetweenlands.client.renderer.util;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.neoforged.neoforge.client.StencilManager;

// Wrapper that makes sure stencil operations are done safely (and don't modify a bad bit)
public final class Stencil implements AutoCloseable {

	public static final Stencil INVALID = new Stencil(-1);
	
	/**
	 * Index of the bit this stencil represents
	 */
	private final int bit;
	
	/**
	 * Bitmask of the bit this stencil represents
	 */
	private final int mask;
	
	public Stencil(int bit) {
		this.bit = bit;
		this.mask = this.isValid() ? (1 << this.bit) : 0;
	}
	
	/**
	 * @return whether this Stencil represents a valid bit
	 */
	public boolean isValid() {
		return this.bit != -1;
	}

	public int getMask() {
		return this.mask;
	}

	/**
	 * Clears the stencil bit
	 * @param value Value of the bit: 1 or 0
	 */
	public void clear(boolean value) {
		RenderSystem.stencilMask(this.mask);
		RenderSystem.clearStencil(value ? this.mask : 0);
		RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, false);
	}
	
	/**
	 * Fills the entire stencil with 0's
	 */
	public void setAllZeros() {
		this.clear(false);
	}
	
	/**
	 * Fills the entire stencil with 1's
	 */
	public void setAllOnes() {
		this.clear(true);
	}
	

	/**
	 * Applies stencil operation on reserved bit mask, see {@link GL11#glStencilOp(int, int, int)}
	 * @param stencilTestFail Stencil action function to be applied when stencil comparison function fails
	 * @param depthTestFails Stencil action function to be applied when depth test fails
	 * @param depthTestPasses Stencil action function to be applied when depth test passes
	 */
	public void op(int stencilTestFail, int depthTestFails, int depthTestPasses) {
		RenderSystem.stencilMask(this.mask);
		RenderSystem.stencilOp(stencilTestFail, depthTestFails, depthTestPasses);
	}

	/**
	 * Applies stencil function with reserved bit mask, see {@link GL11#glStencilFunc(int, int, int)}
	 * @param func Stencil comparison function
	 * @param stencilBitPresent Value of the bit: 1 or 0
	 */
	public void func(int func, boolean stencilBitPresent) {
		RenderSystem.stencilFunc(func, stencilBitPresent ? this.mask : 0, this.mask);
	}
	
	@Override
	public void close() {
		if(this.isValid()) {
			StencilManager.releaseBit(this.bit);
		}
	}

	/**
	 * Attempts to reserve a stencil bit, regardless of render target
	 * Check if the operation was successful with {@link #isValid()}
	 * @return new stencil wrapper
	 */
	public static Stencil reserve() {
		return reserve(null);
	}

	/**
	 * Attempts to reserve a stencil bit
	 * Will attempt to enable stencils if the render target doesn't have them enabled already
	 * Check if the operation was successful with {@link #isValid()}
	 * @param fbo The render target to try to enable the stencil for
	 * @return new stencil wrapper
	 */
	public static Stencil reserve(RenderTarget fbo) {
		int bit = StencilManager.reserveBit();
		
		if(bit != -1) {
			// Attempt to enable stencils if they're disabled
			if(fbo != null && !fbo.isStencilEnabled()) {
				fbo.enableStencil();
			}
			
			if(fbo == null || fbo.isStencilEnabled()) {
				return new Stencil(bit);
			} else {
				StencilManager.releaseBit(bit);
			}
		}
		
		return Stencil.INVALID;
	}
}
