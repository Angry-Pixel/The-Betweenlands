#version 120

uniform sampler2D s_diffuse;
uniform vec2 u_oneTexel;

uniform float offset[5] = float[](0.0, 1.0, 2.0, 3.0, 4.0);
uniform float weight[5] = float[](0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162);

void main(){
	gl_FragColor = texture2D(s_diffuse, gl_TexCoord[0].st) * weight[0];
	for (int i=1; i<5; i++) {
		gl_FragColor += texture2D(s_diffuse, (gl_TexCoord[0].st + vec2(0.0, offset[i] * u_oneTexel.y))) * weight[i];
		gl_FragColor += texture2D(s_diffuse, (gl_TexCoord[0].st - vec2(0.0, offset[i] * u_oneTexel.y))) * weight[i];
	}
}