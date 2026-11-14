package thebetweenlands.client.renderer.util;

import org.lwjgl.opengl.GL11;

public enum StencilWriteType {
	STENCIL_IS_SET,
	STENCIL_IS_REMOVED;
	
	public void setupStencil(Stencil stencil, boolean clear) {
		switch (this) {
			case STENCIL_IS_SET -> {
				if (clear) {
					// Clear stencil so everything is discarded by default
					stencil.setAllZeros();
				}
				
				// Every time a pixel is drawn, the stencil bit will be set to 1
				stencil.func(GL11.GL_ALWAYS, true);
			}
			case STENCIL_IS_REMOVED -> {
				if (clear) {
					// Clear stencil so everything is included by default
					stencil.setAllOnes();
				}
				
				// Every time a pixel is drawn, the stencil bit will be set to 0
				stencil.func(GL11.GL_ALWAYS, false);
			}
		}
		
		// Only draw pixels when the stencil test and depth test succeed
		stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
	}
}
