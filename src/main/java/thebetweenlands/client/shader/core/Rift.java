package thebetweenlands.client.shader.core;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.io.IOException;

public class Rift extends ShaderInstance implements AutoCloseable {

	// Uniforms
	@Nullable
	public Uniform texMatrixUniform;
	@Nullable
	public Uniform overlayUniform;

	public Rift(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_) throws IOException {
		super(p_173336_, shaderLocation, p_173338_);

		// TEMP: Use separate texture matrix from render system, debug
		this.texMatrixUniform = this.getUniform("TexMat");
		this.overlayUniform = this.getUniform("OverlayBlend");
	}

	public Rift setTexMatrix(Matrix4f texMatrix) {
		if (texMatrixUniform != null) {
			this.texMatrixUniform.set(texMatrix);
		}
		return this;
	}

	public Rift setOverlay(float skyBrightness) {
		if (overlayUniform != null) {
			this.overlayUniform.set(skyBrightness);
		}
		return this;
	}
}
