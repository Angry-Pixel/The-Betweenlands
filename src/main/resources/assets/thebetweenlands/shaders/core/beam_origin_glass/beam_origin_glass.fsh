#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in float vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
	float visibility = vertexColor;
	// First pass = {visibility, visibility, visibility, 0.75f + 0.25f * (1.0 - visibility)}
	// Second pass = {0, 0, 0, 0.9f * (1.0 - visibility)}
	// Third pass = {visibility, visibility, visibility, visibility} + has texture
	vec4 texColour = texture(Sampler0, texCoord0) * visibility;
	float component = ((visibility) * (0.75 + 0.25 * (1.0 - visibility))) * ((1.0 - (0.9 * (1.0 - visibility))) * (1.0 - (texColour.a)));
	float alpha =       1.0 - ((1.0 - (0.75 + 0.25 * (1.0 - visibility))) * ((1.0 - (0.9 * (1.0 - visibility))) * (1.0 - (texColour.a))));
	// Premultiplied vertex colour
	vec4 vertexColor = vec4(
		component + texColour.r * texColour.a,
		component + texColour.g * texColour.a,
		component + texColour.b * texColour.a,
		alpha
	);
	vec4 color = vertexColor * ColorModulator;
	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
