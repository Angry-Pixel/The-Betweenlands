package thebetweenlands.client.render.shader.postprocessing;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class Starfield extends PostProcessingEffect<Starfield> {
	private final boolean faded;

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

	public Starfield(boolean faded) {
		this.faded = faded;
	}

	public Starfield setTimeScale(float timeScale) {
		this.timeScale = timeScale;
		return this;
	}

	public Starfield setZoom(float zoom) {
		this.zoom = zoom;
		return this;
	}

	public Starfield setOffset(float x, float y, float z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		return this;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/starfield/starfield.vsh"), 
				this.faded ? new ResourceLocation("thebetweenlands:shaders/postprocessing/starfield/starfield_faded.fsh") : new ResourceLocation("thebetweenlands:shaders/postprocessing/starfield/starfield.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.timeUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_msTime");
		this.timeScaleUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_timeScale");
		this.zoomUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_zoom");
		this.offsetXUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_offsetX");
		this.offsetYUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_offsetY");
		this.offsetZUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_offsetZ");
		return true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		this.uploadFloat(this.timeUniformID, System.nanoTime() / 1000000.0F);
		this.uploadFloat(this.timeScaleUniformID, this.timeScale);
		this.uploadFloat(this.zoomUniformID, this.zoom);
		this.uploadFloat(this.offsetXUniformID, this.offsetX);
		this.uploadFloat(this.offsetYUniformID, this.offsetY);
		this.uploadFloat(this.offsetZUniformID, this.offsetZ);
	}
}
