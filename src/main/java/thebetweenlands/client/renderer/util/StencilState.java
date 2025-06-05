package thebetweenlands.client.renderer.util;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.neoforged.neoforge.client.GlStateBackup;

public record StencilState(boolean stencilTestEnabled, int stencilFunc, int stencilRef, int stencilMask) implements AutoCloseable {
	public static StencilState get() {
		// TODO glIsEnabled and glGetXXX etc are BAD and SLOW, so we use the stuff from GlStateManager
        RenderSystem.assertOnRenderThread();
        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        boolean stencilTestEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);//backup.stencilMask != -1;
        int stencilFunc = backup.stencilFuncFunc;
        int stencilRef = backup.stencilFuncRef;
        int stencilMask = backup.stencilFuncMask;
//		boolean stencilTestEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
//		int stencilFunc = GlStateManager._getInteger(GL11.GL_STENCIL_FUNC);
//		int stencilMask = GlStateManager._getInteger(GL11.GL_STENCIL_VALUE_MASK);
//		int stencilWriteMask = GlStateManager._getInteger(GL11.GL_STENCIL_WRITEMASK);
		return new StencilState(stencilTestEnabled, stencilFunc, stencilRef, stencilMask);
	}

	public void apply() {
		if(stencilTestEnabled) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
		} else {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
		RenderSystem.stencilFunc(stencilFunc, stencilRef, stencilMask);
	}
	
	@Override
	public void close() {
		this.apply();
	}
}