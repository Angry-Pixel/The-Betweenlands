package thebetweenlands.client.render.shader.effect;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class StarfieldEffect extends PostProcessingEffect {
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

	public StarfieldEffect(boolean faded) {
		this.faded = faded;
	}

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
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/starfield/starfield.vsh"), 
				this.faded ? new ResourceLocation("thebetweenlands:shaders/deferred/starfield/starfieldFaded.fsh") : new ResourceLocation("thebetweenlands:shaders/deferred/starfield/starfield.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.timeUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "u_msTime");
		this.timeScaleUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "u_timeScale");
		this.zoomUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "u_zoom");
		this.offsetXUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "u_offsetX");
		this.offsetYUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "u_offsetY");
		this.offsetZUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "u_offsetZ");
		return true;
	}

	@Override
	protected void uploadUniforms() {
		OpenGlHelper.func_153168_a(this.timeUniformID, this.getSingleFloatBuffer(System.nanoTime() / 1000000.0F));
		OpenGlHelper.func_153168_a(this.timeScaleUniformID, this.getSingleFloatBuffer(this.timeScale));
		OpenGlHelper.func_153168_a(this.zoomUniformID, this.getSingleFloatBuffer(this.zoom));
		OpenGlHelper.func_153168_a(this.offsetXUniformID, this.getSingleFloatBuffer(this.offsetX));
		OpenGlHelper.func_153168_a(this.offsetYUniformID, this.getSingleFloatBuffer(this.offsetY));
		OpenGlHelper.func_153168_a(this.offsetZUniformID, this.getSingleFloatBuffer(this.offsetZ));
	}
}
