#version 120

uniform sampler2D s_world_depth;
uniform sampler2D s_clipPlane_depth;

void main(){
	vec2 texCoord = gl_TexCoord[0].st;
	float worldDepth = texture2D(s_world_depth, texCoord).x;
	float clipPlaneDepth = texture2D(s_clipPlane_depth, texCoord).x;
	if(clipPlaneDepth > worldDepth) {
		gl_FragColor = vec4(0, 0, 0, 1);
	} else if(clipPlaneDepth <= worldDepth) {
		gl_FragColor = vec4(1, 1, 1, 1);
	}
}