package thebetweenlands.client.render.shader.effect;

import org.lwjgl.opengl.ARBShaderObjects;

import net.minecraft.util.ResourceLocation;

public class SwirlEffect extends DeferredEffect {
	private float angle = 0.0F;
	private int angleUniformID = -1;
	
	public SwirlEffect setAngle(float angle) {
		this.angle = angle;
		return this;
	}
	
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/swirl/swirl.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/swirl/swirl.fsh")};
	}
	
	@Override
	protected boolean initEffect() {
		this.angleUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_angle");
		return /*this.timeUniformID >= 0 && this.scaleUniformID >= 0 && this.timeScaleUniformID >= 0 && this.multiplierUniformID >= 0 && this.xOffsetUniformID >= 0 && this.yOffsetUniformID >= 0*/true;
	}

	@Override
	protected void uploadUniforms() {
		ARBShaderObjects.glUniform1fARB(this.angleUniformID, this.angle);
	}
}
