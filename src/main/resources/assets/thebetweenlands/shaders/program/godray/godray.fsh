#version 150

const int NUM_SAMPLES = 60;

uniform sampler2D DiffuseSampler;
uniform sampler2D s_occlusion;
uniform float u_godRayX;
uniform float u_godRayY;
uniform vec4 u_color;

uniform float u_exposure;
uniform float u_decay;
uniform float u_density;
uniform float u_weight;
uniform float u_illuminationDecay;

in vec2 v_texCoord;

//Output color
out vec4 o_fragColor;

void main() {
    vec2 rayPos = vec2(u_godRayX, u_godRayY);
    vec2 deltaTextCoord = vec2(v_texCoord - rayPos.xy);
    vec2 textCoo = v_texCoord;
    deltaTextCoord *= 1.0 /  float(NUM_SAMPLES) * u_density;
    float illuminationDecay = u_illuminationDecay;

    vec4 diffuseColor = texture2D(DiffuseSampler, v_texCoord);
    o_fragColor = texture2D(s_occlusion, v_texCoord);

    /*if(gl_FragColor.r == 1.0F) {
		gl_FragColor = vec4(0, 0, 0, 0);
		return;
	}*/

    for(int i=0; i < NUM_SAMPLES ; i++) {
        textCoo -= deltaTextCoord;
        vec4 sample = texture2D(s_occlusion, textCoo );

        sample *= illuminationDecay * u_weight;

        o_fragColor += sample;

        illuminationDecay *= u_decay;
    }

    vec4 rays = clamp(o_fragColor * u_exposure, 0.0f, 1.0f);
    rays *= u_color;
    rays.rgb *= rays.a;
    o_fragColor = vec4(diffuseColor.rgb + rays.rgb, 1.0f);
}