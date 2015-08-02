package thebetweenlands.client.render.shader.effect;

public class Blit extends DeferredEffect {
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
						"void main(){ \n" +
						"gl_FragColor = texture2D(DiffuseSampler, gl_TexCoord[0].st); \n" +
						"}";

		return shaders;
	}
}
