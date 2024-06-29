package thebetweenlands.client.shader.postprocessing;

import java.nio.ByteBuffer;

import net.minecraft.resources.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import thebetweenlands.common.TheBetweenlands;

public class Dither extends PostProcessingEffect<Dither> {
	private int bayerMatrixSamplerUniformID = -1;
	private int bayerMatrixTexture = -1;

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {TheBetweenlands.prefix("shaders/postprocessing/dither/dither.vsh"), TheBetweenlands.prefix("shaders/postprocessing/dither/dither.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.bayerMatrixSamplerUniformID = this.getUniform("s_bayerMatrix");

		ByteBuffer bayerMatrix = BufferUtils.createByteBuffer(64);
		bayerMatrix.put(new byte[] {
				0, 32,  8, 40,  2, 34, 10, 42,
				48, 16, 56, 24, 50, 18, 58, 26,
				12, 44,  4, 36, 14, 46,  6, 38,
				60, 28, 52, 20, 62, 30, 54, 22,
				3, 35, 11, 43,  1, 33,  9, 41,
				51, 19, 59, 27, 49, 17, 57, 25,
				15, 47,  7, 39, 13, 45,  5, 37,
				63, 31, 55, 23, 61, 29, 53, 21}
				);
		bayerMatrix.flip();

		this.bayerMatrixTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.bayerMatrixTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_LUMINANCE, 8, 8, 0, GL11.GL_LUMINANCE,
				GL11.GL_UNSIGNED_BYTE, bayerMatrix);

		return true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		this.uploadSampler(this.bayerMatrixSamplerUniformID, this.bayerMatrixTexture, 1);
	}

	@Override
	protected void deleteEffect() {
		if(this.bayerMatrixTexture >= 0)
			GL11.glDeleteTextures(this.bayerMatrixTexture);
	}
}
