package thebetweenlands.client.renderer.util.rendertype.modifier;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.GlStateBackup;
import thebetweenlands.common.TheBetweenlands;

public class InvertCullingModifier implements StatelessRenderTypeModifier {

	public static final InvertCullingModifier INSTANCE = new InvertCullingModifier(TheBetweenlands.prefix("invert_cullface"));

	protected final ResourceLocation id;
	
	public InvertCullingModifier(ResourceLocation id) {
		this.id = id;
	}
	
	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public void beforeSetupRenderState(RenderType originalRenderType) {
		// NO-OP
	}

	@Override
	public void setupRenderState(RenderType originalRenderType) {
		// glIsEnabled and glGetXXX etc are BAD and SLOW, so we try to use the stuff from GlStateManager
//		GL11.glFrontFace(GL11.GL_CW);
		GlStateBackup backup = new GlStateBackup();
		RenderSystem.backupGlState(backup);
        if(backup.cullEnabled) {
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
	public void clearRenderState(RenderType originalRenderType) {
		// TODO glIsEnabled and glGetXXX etc are BAD and SLOW, so we try to use the stuff from GlStateManager
//		GL11.glFrontFace(GL11.GL_CCW);
		GlStateBackup backup = new GlStateBackup();
		RenderSystem.backupGlState(backup);
        if(backup.cullEnabled) {
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
	public void afterClearRenderState(RenderType originalRenderType) {
		// NO-OP
	}

}
