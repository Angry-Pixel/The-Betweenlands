package thebetweenlands.client.renderer.util;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;

import net.minecraft.client.renderer.RenderType;

/**
 * Renders as the delegate render type, but flips the culled face (FRONT <-> BACK)
 * does nothing if culling is disabled or both faces are being culled
 */
public class InvertedCullingRenderType extends ProxyRenderType {

	public InvertedCullingRenderType(RenderType delegate) {
		this("thebetweenlands:inverted/" + delegate.name, delegate);
	}
	
	public InvertedCullingRenderType(String name, RenderType delegate) {
		super(name, delegate);
	}

	@Override
	public void setupRenderState() {
        RenderSystem.assertOnRenderThread();
        super.setupRenderState();
        
        if(GL11.glIsEnabled(GL11.GL_CULL_FACE)) {
            int cull = GlStateManager._getInteger(GL11.GL_CULL_FACE_MODE);
            switch(cull) {
            case GL11.GL_FRONT:
            	GL11.glCullFace(GL11.GL_BACK);
            	break;
            case GL11.GL_BACK:
            	GL11.glCullFace(GL11.GL_FRONT);
            	break;
        	default:
        		break;
            }
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
        if(GL11.glIsEnabled(GL11.GL_CULL_FACE)) {
            int cull = GlStateManager._getInteger(GL11.GL_CULL_FACE_MODE);
            switch(cull) {
            case GL11.GL_FRONT:
            	GL11.glCullFace(GL11.GL_BACK);
            	break;
            case GL11.GL_BACK:
            	GL11.glCullFace(GL11.GL_FRONT);
            	break;
        	default:
        		break;
            }
        }
        
		super.clearRenderState();
	}
}
