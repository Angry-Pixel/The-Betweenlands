#version 120

uniform sampler2D s_diffuse;
uniform sampler2D s_bayerMatrix;


void main(){
	gl_FragColor = texture2D(s_diffuse, gl_TexCoord[0].st);
	gl_FragColor += vec4(1, 0, 0, 1) * vec4(texture2D(s_bayerMatrix, gl_TexCoord[0].st / 8.0).r / 32.0 - (1.0 / 128.0));
}