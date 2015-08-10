#version 120

const int NUM_SAMPLES = 60;

uniform sampler2D s_occlusion;
uniform float u_godRayX;
uniform float u_godRayY;

uniform float u_exposure;
uniform float u_decay;
uniform float u_density;
uniform float u_weight;
uniform float u_illuminationDecay;

void main() {
	vec2 rayPos = vec2(u_godRayX, u_godRayY);
	vec2 deltaTextCoord = vec2( gl_TexCoord[0].st - rayPos.xy );
	vec2 textCoo = gl_TexCoord[0].st;
	deltaTextCoord *= 1.0 /  float(NUM_SAMPLES) * u_density;
	float illuminationDecay = u_illuminationDecay;
	
	gl_FragColor = texture2D(s_occlusion, gl_TexCoord[0].st);

	/*if(gl_FragColor.r == 1.0F) {
		gl_FragColor = vec4(0, 0, 0, 0);
		return;
	}*/
	
	for(int i=0; i < NUM_SAMPLES ; i++) {
			 textCoo -= deltaTextCoord;
			 vec4 sample = texture2D(s_occlusion, textCoo );
		
			 sample *= illuminationDecay * u_weight;

			 gl_FragColor += sample;

			 illuminationDecay *= u_decay;
	 }
	 
	 gl_FragColor *= u_exposure;
}
