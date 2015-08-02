package thebetweenlands.client.render.shader.effect;

public class GaussianBlur extends DeferredEffect {
	@Override
	protected DeferredEffect[] getStages() {
		DeferredEffect stages[] = new DeferredEffect[]{
				new GaussianBlur() {
					@Override
					protected DeferredEffect[] getStages() {
						return new DeferredEffect[0];
					}

					@Override
					protected String[] getShaders() {
						String[] shaders = new String[2];
						shaders[0] =
								"#version 120 \n" + 
										"void main() { \n" +
										"gl_TexCoord[0] = gl_MultiTexCoord0; \n" +
										"gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex; \n" +
										"}";

						shaders[1] =
								"#version 120 \n" + 
										"uniform sampler2D DiffuseSampler; \n" +
										"uniform vec2 TexelSize; \n" +
										"uniform float offset[5] = float[](0.0, 1.0, 2.0, 3.0, 4.0); \n" +
										"uniform float weight[5] = float[](0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162); \n" +
										"void main(){ \n" +
										"gl_FragColor = texture2D(DiffuseSampler, gl_TexCoord[0].st) * weight[0]; \n" +
										"for (int i=1; i<5; i++) { \n" +
										"gl_FragColor += texture2D(DiffuseSampler, (gl_TexCoord[0].st + vec2(offset[i] * TexelSize.x, 0.0))) * weight[i]; \n" + 
										"gl_FragColor += texture2D(DiffuseSampler, (gl_TexCoord[0].st - vec2(offset[i] * TexelSize.x, 0.0))) * weight[i]; \n" + 
										"} \n" + 
										"}";

						return shaders;
					}
				}
		};
		return stages;
	}

	@Override
	protected String[] getShaders() {
		String[] shaders = new String[2];
		shaders[0] =
				"#version 120 \n" + 
						"void main() { \n" +
						"gl_TexCoord[0] = gl_MultiTexCoord0; \n" +
						"gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex; \n" +
						"}";

		shaders[1] =
				"#version 120 \n" + 
						"uniform sampler2D DiffuseSampler; \n" +
						"uniform vec2 TexelSize; \n" +
						"uniform float offset[5] = float[](0.0, 1.0, 2.0, 3.0, 4.0); \n" +
						"uniform float weight[5] = float[](0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162); \n" +
						"void main(){ \n" +
						"gl_FragColor = texture2D(DiffuseSampler, gl_TexCoord[0].st) * weight[0]; \n" +
						"for (int i=1; i<5; i++) { \n" +
						"gl_FragColor += texture2D(DiffuseSampler, (gl_TexCoord[0].st + vec2(0.0, offset[i] * TexelSize.y))) * weight[i]; \n" + 
						"gl_FragColor += texture2D(DiffuseSampler, (gl_TexCoord[0].st - vec2(0.0, offset[i] * TexelSize.y))) * weight[i]; \n" + 
						"} \n" + 
						"}";

		return shaders;
	}
}
