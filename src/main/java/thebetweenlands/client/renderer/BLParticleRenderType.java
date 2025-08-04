package thebetweenlands.client.renderer;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thebetweenlands.client.shader.ShaderHelper;

@OnlyIn(Dist.CLIENT)
public interface BLParticleRenderType extends ParticleRenderType {

	BLParticleRenderType BL_GAS_CLOUD = new BLParticleRenderType() {
		public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {

			// Static gas texture
			//RenderSystem.setShaderTexture(0, ShaderHandler.GasWarpShader.gasCloudTexture.getColorTextureId());
			// Dynamic gas texture

			if (true) {
				RenderSystem.setShaderTexture(0, ShaderHelper.INSTANCE.getWorldShader().getGasTexture());
			}

			RenderSystem.disableDepthTest();

			return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		public String toString() {
			return "BL_GAS_CLOUD";
		}

		public boolean isTranslucent() {
			return true;
		}
	};

	// Used to identify gas cloud particles using the haze shader input buffer
	BLParticleRenderType BL_HAZE_CLOUD = new BLParticleRenderType() {
		public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {

			// Static gas texture
			//RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			// Dynamic gas texture

			if (true) {
				RenderSystem.setShaderTexture(0, ShaderHelper.INSTANCE.getWorldShader().getGasTexture());
			}

			return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		public String toString() {
			return "BL_HAZE_CLOUD";
		}
	};
}
