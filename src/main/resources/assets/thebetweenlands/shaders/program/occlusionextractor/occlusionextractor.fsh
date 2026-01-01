#version 150

uniform sampler2D s_world_depth;
uniform sampler2D s_clipPlane_depth;

//Input coords
in vec2 v_texCoord;

//Output color
out vec4 o_fragColor;

void main(){
	vec2 texCoord = v_texCoord.st;
	float worldDepth = texture2D(s_world_depth, texCoord).x;
	float clipPlaneDepth = texture2D(s_clipPlane_depth, texCoord).x;
	if(clipPlaneDepth > worldDepth) {
        o_fragColor = vec4(0, 0, 0, 1);
	} else if(clipPlaneDepth <= worldDepth) {
        o_fragColor = vec4(1, 1, 1, 1);
	}
}