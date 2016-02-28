package thebetweenlands.utils;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

public final class Stencil {
	public class StencilFunc {
		public final int func_func, func_ref, func_mask, op_fail, op_zfail, op_zpass;
		public StencilFunc(int func_func, int func_ref, int func_mask, int op_fail, int op_zfail, int op_zpass) {
			this.func_func = func_func;
			this.func_ref = func_ref;
			this.func_mask = func_mask;
			this.op_fail = op_fail;
			this.op_zfail = op_zfail;
			this.op_zpass = op_zpass;
		}
		public void use() {
			GL11.glStencilFunc(this.func_func, this.func_ref, this.func_mask);
			GL11.glStencilOp(this.op_fail, this.op_zfail, this.op_zpass);
		}
	}
	
	private static final Stencil INSTANCE = new Stencil();
	private final HashMap<Integer, StencilFunc> stencilFuncs = new HashMap<Integer, StencilFunc>();
	private int layers = 1;
	private boolean renderMask;
	
	private Stencil() {}

	/**
	 * Returns the stencil instance
	 * @return Stencil
	 */
	public static Stencil getInstance() {
		return INSTANCE;
	}

	/**if buffer value != layers don't draw**/
	/**if buffer value == layers draw**/

	/**
	 * Sets the stencil rendering mode. If parameter is true, all pixels are drawn.
	 * @param renderMask Boolean
	 */
	public void setRenderMask(boolean renderMask) {
		this.renderMask = renderMask;
	}

	/**Creates a new layer and clears the stencil buffer if no layers exist yet.**/
	public void startLayer() {
		if(this.layers == 1) {
			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		}
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		this.layers++;
		if(this.layers > this.getMaximumLayers()) {
			System.out.println("StencilUtil: Reached maximum amount of layers!");
			this.layers = 1;
			return;
		}
	}

	/**Stops and removes the current layer and disables stencil test if none are left.**/
	public void stopLayer() {
		if(this.layers == 1) {
			System.out.println("StencilUtil: No layers found!");
			return;
		}
		this.layers--;
		if(this.layers == 1) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		} else {
			StencilFunc lastStencilFunc = this.stencilFuncs.remove(this.layers);
			if(lastStencilFunc != null) {
				lastStencilFunc.use();
			}
		}
	}

	/**Clears the stencil buffer and resets all layers.**/
	public void clear() {
		GL11.glClearStencil(0);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		this.stencilFuncs.clear();
		this.layers = 1;
	}

	/**Enables writing to the buffer. Buffer is set to the draw value if a pixel is drawn.**/
	public void setBuffer() {
		this.setStencilFunc(new StencilFunc(!this.renderMask ? GL11.GL_NEVER : GL11.GL_ALWAYS, this.layers, this.getMaximumLayers(), GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP));
	}

	/**
	 * Enables writing to the buffer. Sets the buffer values to the draw value if parameter is true.
	 * @param set Boolean
	 */
	public void setBuffer(boolean set) {
		this.setStencilFunc(new StencilFunc(!this.renderMask ? GL11.GL_NEVER : GL11.GL_ALWAYS, set ? this.layers : this.layers - 1, this.getMaximumLayers(), GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE));
	}

	/**Enables reading from the buffer. Only renders outside the mask.**/
	public void cropOutside() {
		this.setStencilFunc(new StencilFunc(GL11.GL_NOTEQUAL, this.layers, this.getMaximumLayers(), GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP));
	}

	/**Enables reading from the buffer. Only renders inside the mask.**/
	public void cropInside() {
		this.setStencilFunc(new StencilFunc(GL11.GL_EQUAL, this.layers, this.getMaximumLayers(), GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP));
	}

	/**
	 * Sets the stencil function and operator of the current layer.
	 * @param stencilFunc StencilFunc
	 */
	public void setStencilFunc(StencilFunc stencilFunc) {
		GL11.glStencilFunc(stencilFunc.func_func, stencilFunc.func_ref, stencilFunc.func_mask);
		GL11.glStencilOp(stencilFunc.op_fail, stencilFunc.op_zfail, stencilFunc.op_zpass);
		this.stencilFuncs.put(this.layers, stencilFunc);
	}

	/**
	 * Returns the current stencil function and operator.
	 * @return StencilFunc
	 */
	public StencilFunc getStencilFunc() {
		return this.stencilFuncs.get(this.layers);
	}

	/**
	 * Returns the current layer index.
	 * @return Integer
	 */
	public int getLayer() {
		return this.layers;
	}

	/**
	 * Returns the stencil buffer size.
	 * @return Integer
	 */
	public int getStencilBufferSize() {
		return GL11.glGetInteger(GL11.GL_STENCIL_BITS);
	}

	/**
	 * Returns the amount of possible layers.
	 * @return Integer
	 */
	public int getMaximumLayers() {
		return (int) (Math.pow(2, this.getStencilBufferSize()) - 1);
	}

	/**
	 * Creates a circular mask.
	 * @param x Integer
	 * @param y Integer
	 * @param radius Double
	 */
	public void createCirlce(double x, double y, double radius) {
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		for(int i = 0; i <= 360; i++) {
			double sin = Math.sin((i * Math.PI) / 180D) * radius;
			double cos = Math.cos((i * Math.PI) / 180D) * radius;
			GL11.glVertex2d(x + sin, y + cos);
		}
		GL11.glEnd();
	}

	/**
	 * Creates a rectangular mask.
	 * @param x1 Integer
	 * @param y1 Integer
	 * @param x2 Integer
	 * @param y2 Integer
	 */
	public void createRect(double x, double y, double x2, double y2) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2d(x, y2);
		GL11.glVertex2d(x2, y2);
		GL11.glVertex2d(x2, y);
		GL11.glVertex2d(x, y);
		GL11.glEnd();
	}
}
