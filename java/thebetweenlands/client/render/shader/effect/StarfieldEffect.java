package thebetweenlands.client.render.shader.effect;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class StarfieldEffect extends DeferredEffect {
	private float timeScale = 1.0F;
	private float zoom = 1.0F;
	private float offsetX = 0.0F;
	private float offsetY = 0.0F;
	private float offsetZ = 0.0F;
	
	private int timeUniformID = -1;
	private int timeScaleUniformID = -1;
	private int zoomUniformID = -1;
	private int offsetXUniformID = -1;
	private int offsetYUniformID = -1;
	private int offsetZUniformID = -1;
	
	public StarfieldEffect setTimeScale(float timeScale) {
		this.timeScale = timeScale;
		return this;
	}
	
	public StarfieldEffect setZoom(float zoom) {
		this.zoom = zoom;
		return this;
	}
	
	public StarfieldEffect setOffset(float x, float y, float z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		return this;
	}
	
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/starfield/starfield.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/starfield/starfield.fsh")};
	}
	
	@Override
	protected boolean initEffect() {
		this.timeUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_msTime");
		this.timeScaleUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_timeScale");
		this.zoomUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_zoom");
		this.offsetXUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_offsetX");
		this.offsetYUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_offsetY");
		this.offsetZUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_offsetZ");
		return true;
	}
	
	@Override
	protected void uploadUniforms() {
		ARBShaderObjects.glUniform1fARB(this.timeUniformID, System.nanoTime() / 1000000.0F);
		ARBShaderObjects.glUniform1fARB(this.timeScaleUniformID, this.timeScale);
		ARBShaderObjects.glUniform1fARB(this.zoomUniformID, this.zoom);
		ARBShaderObjects.glUniform1fARB(this.offsetXUniformID, this.offsetX);
		ARBShaderObjects.glUniform1fARB(this.offsetYUniformID, this.offsetY);
		ARBShaderObjects.glUniform1fARB(this.offsetZUniformID, this.offsetZ);
	}
}
