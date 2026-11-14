package thebetweenlands.client.renderer.util;

import org.lwjgl.opengl.GL11;

public enum StencilReadType {
	STENCIL_IS_KEPT,
	STENCIL_IS_REMOVED;
	
	public void setupStencil(Stencil stencil) {
		switch (this) {
			case STENCIL_IS_KEPT -> {
				// Only draw pixels where our bit is set in the stencil
				stencil.func(GL11.GL_EQUAL, true);
			}
			case STENCIL_IS_REMOVED -> {
				// Do NOT draw pixels where our bit is set in the stencil
				stencil.func(GL11.GL_EQUAL, false);
			}
		}
		
		// Never change the stencil
		stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
	}
}
