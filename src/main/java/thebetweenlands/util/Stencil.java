package thebetweenlands.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * OpenGL stencil wrapper class that exposes only safe operations
 * that can't modify unreserved stencil bits.
 * Stencil resources must be released with {@link #close()} and should be
 * used in try-with-resources statements.
 */
public class Stencil implements AutoCloseable {
	private static final Stencil INVALID = new Stencil(-1);

	public final int bit;
	public final int mask;

	private Stencil(int bit) {
		this.bit = bit;
		this.mask = !this.valid() ? 0 : 1 << bit;
	}

	/**
	 * Whether the stencil is valid, i.e. has been reserved
	 * and is compatible with the FBO.
	 */
	public boolean valid() {
		return this.bit != -1;
	}

	/**
	 * Clears the stencil bit
	 * @param value Value of the bit: 1 or 0
	 */
	public void clear(boolean value) {
		GL11.glStencilMask(this.mask);
		GL11.glClearStencil(value ? this.mask : 0);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
	}

	/**
	 * Applies stencil operation on reserved bit mask, see {@link GL11#glStencilOp(int, int, int)}
	 * @param fail Stencil action function to be applied when stencil comparison function fails
	 * @param zfail Stencil action function to be applied when depth test fails
	 * @param zpass Stencil action function to be applied when depth test passes
	 */
	public void op(int fail, int zfail, int zpass) {
		GL11.glStencilMask(this.mask);
		GL11.glStencilOp(fail, zfail, zpass);
	}
	
	/**
	 * Applies the same stencil operation to all three cases.
	 * See {@link #op(int, int, int)}
	 * @param func Stencil action function
	 */
	public void op(int func) {
		this.op(func, func, func);
	}

	/**
	 * Applies stencil function with reserved bit mask, see {@link GL11#glStencilFunc(int, int, int)}
	 * @param func Stencil comparison function
	 * @param value Value of the bit: 1 or 0
	 */
	public void func(int func, boolean value) {
		GL11.glStencilFunc(func, value ? this.mask : 0, this.mask);
	}

	@Override
	public void close() {
		if(this.valid()) {
			MinecraftForgeClient.releaseStencilBit(this.bit);
		}
	}

	/**
	 * Tries to reserve a stencil bit.
	 * Check if returned stencil is valid and can be used with {@link #valid()}.
	 * Call {@link #close()} to release the stencil bit.
	 * @param framebuffer Framebuffer to enable and check stencil buffer
	 * @return New stencil bit wrapper.
	 */
	public static Stencil reserve(Framebuffer framebuffer) {
		int stencilBit = MinecraftForgeClient.reserveStencilBit();

		if(stencilBit >= 0) {
			if(framebuffer.isStencilEnabled() || framebuffer.enableStencil()) {
				return new Stencil(stencilBit);
			}

			//FBO doesn't support stencil, need to release the bit again
			MinecraftForgeClient.releaseStencilBit(stencilBit);
		}

		return INVALID;
	}
}
