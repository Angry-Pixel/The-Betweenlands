package thebetweenlands.client.render.shader.effect;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class GodRayEffect extends DeferredEffect {
	private Framebuffer occlusionMap = null;

	private int occlusionMapUniformID = -1;
	private int godRayXUniformID = -1;
	private int godRayYUniformID = -1;
	private int exposureUniformID = -1;
	private int decayUniformID = -1;
	private int densityUniformID = -1;
	private int weightUniformID = -1;
	private int illuminationDecayUniformID = -1;
	
	private float godRayX = 0.5F;
	private float godRayY = 0.5F;
	
	private float exposure = 1.0F;
	private float decay = 1.0F;
	private float density = 1.0F;
	private float weight = 1.0F;
	private float illuminationDecay = 1.0F;

	public GodRayEffect setOcclusionMap(Framebuffer occlusionMap) {
		this.occlusionMap = occlusionMap;
		return this;
	}
	
	public GodRayEffect setRayPos(float x, float y) {
		this.godRayX = x;
		this.godRayY = y;
		return this;
	}

	public GodRayEffect setParams(float exposure, float decay, float density, float weight, float illuminationDecay) {
		this.exposure = exposure;
		this.decay = decay;
		this.density = density;
		this.weight = weight;
		this.illuminationDecay = illuminationDecay;
		return this;
	}
	
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/godray/godray.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/godray/godray.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.occlusionMapUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "s_occlusion");
		this.godRayXUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_godRayX");
		this.godRayYUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_godRayY");
		this.exposureUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_exposure");
		this.decayUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_decay");
		this.densityUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_density");
		this.weightUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_weight");
		this.illuminationDecayUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_illuminationDecay");
		return true;
	}

	@Override
	protected void uploadUniforms() {
		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.occlusionMap.framebufferTexture);
		ARBShaderObjects.glUniform1iARB(this.occlusionMapUniformID, 0);
		
		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
		
		ARBShaderObjects.glUniform1fARB(this.godRayXUniformID, this.godRayX);
		ARBShaderObjects.glUniform1fARB(this.godRayYUniformID, this.godRayY);
		ARBShaderObjects.glUniform1fARB(this.exposureUniformID, this.exposure);
		ARBShaderObjects.glUniform1fARB(this.decayUniformID, this.decay);
		ARBShaderObjects.glUniform1fARB(this.densityUniformID, this.density);
		ARBShaderObjects.glUniform1fARB(this.weightUniformID, this.weight);
		ARBShaderObjects.glUniform1fARB(this.illuminationDecayUniformID, this.illuminationDecay);
	}
}
