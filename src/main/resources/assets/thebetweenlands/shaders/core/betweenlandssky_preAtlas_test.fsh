#version 150

// Star Nest by Pablo Román Andrioli
// This content is under the MIT License.
// This dude made one hell of a star field shader

// updated to v150 by probably from mars

#define iterations 13
#define formuparam 0.51

#define volsteps 5
#define stepsize 0.25

#define zoom   0.700
#define tile   0.850
#define speed  0.4

#define brightness 0.0015
#define darkmatter 0.300
#define distfading 0.730
#define saturation 0.850

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float GameTime;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec4 ColorModulator;
uniform vec4 UpperFogColor;
uniform vec4 LowerFogColor;
uniform float LowerFogApeture;
uniform float LowerFogRange;
uniform float DenseFog;
uniform float FogRotation;

in vec2 texCoord0;
in vec2 texCoord1;

out vec4 fragColor;

// To use static sky texture, use Sampler0 at texCoord0
// Use Sampler1 at texCoord1 to get fog detailing texture 
void main() {

	//get coords and direction
	vec2 uv = texCoord0.xy;
	vec3 dir = vec3(uv*zoom, 1.0F);
	float time=GameTime*speed+.25;

	//rotation
	vec3 from = vec3(1.0F,0.5F,0.5F);
	from += vec3(time*2.0F,time,-2.0F);
	from += vec3(0, 0, 0);
	
	//volumetric rendering
	float s = 0.1F;
	float fade = 1.0F;
	vec3 v=vec3(0.0F);
	for (int r = 0; r < volsteps; r++) {
		vec3 p = from + s * dir * 0.5F;
		p = abs(vec3(tile) - mod(p, vec3(tile*2.0F))); // tiling fold
		float pa, a = pa = 0.0F;
		for (int i = 0; i < iterations; i++) { 
			p = abs(p) / dot(p, p) - formuparam; // the magic formula
			a += abs(length(p) - pa); // absolute sum of average change
			pa = length(p);
		}
		float dm = max(0.0F, darkmatter - a * a * 0.001F); //dark matter
		a *= a * a; // add contrast
		if (r > 6) fade *= 1.0F -dm ; // dark matter, don't render near
		v += fade;
		v += vec3(s, s*s, s*s*s*s) * a * brightness * fade; // coloring based on distance
		fade *= distfading; // distance fading
		s += stepsize;
	}
	v = mix(vec3(length(v)), v, saturation); // color adjust
	
	// Const fog
	
	// Calculate fade distance
	float distFromCenter = distance(texCoord0, vec2(0.5, 0.5));
	
	// use "distFromCenter" to crate a new rotated vector
	// Why is glsl's atan input inverted?
	float originAng = atan(texCoord0[1]-0.5, texCoord0[0]-0.5);
	vec2 fogLayerTexCoord1 = vec2((sin(FogRotation + originAng) * distFromCenter) + 0.5f, (cos(FogRotation + originAng) * distFromCenter) + 0.5f);
	vec2 fogLayerTexCoord3 = vec2((sin((-FogRotation) + originAng) * distFromCenter) + 0.5f, (cos((-FogRotation) + originAng) * distFromCenter) + 0.5f);
	vec2 fogLayerTexCoord2 = vec2((sin((FogRotation * 0.5) + originAng) * distFromCenter) + 0.5f, (cos((FogRotation * 0.5) + originAng) * distFromCenter) + 0.5f);
	vec2 fogLayerTexCoord4 = vec2((sin((-FogRotation * 0.5) + originAng) * distFromCenter) + 0.5f, (cos((-FogRotation * 0.5) + originAng) * distFromCenter) + 0.5f);
	
	// blend mask factors
	vec4 LowerFogMask = (texture(Sampler1, fogLayerTexCoord1) + texture(Sampler1, fogLayerTexCoord2) + texture(Sampler1, fogLayerTexCoord3) + texture(Sampler1, fogLayerTexCoord4)) * 0.25f;
	
	// Use uv distance to set fog color
	float maskfactor = 0;
	vec4 FinalLowerFogColor = ((LowerFogColor * (2-DenseFog)) + (LowerFogMask * DenseFog)) * 0.5;
	vec4 FogFactor = UpperFogColor;
	
	// Hard fog start
	if (distFromCenter > LowerFogApeture) {
		FogFactor = LowerFogColor;
	}
	// Blend lower fog with upper fog
	else if (distFromCenter > LowerFogApeture - LowerFogRange && LowerFogRange != 0.0f) {
		maskfactor = (distFromCenter - LowerFogApeture + LowerFogRange) * (2 / LowerFogRange) * (1-LowerFogMask[3]); 
		FogFactor = ((UpperFogColor * (2-maskfactor)) + (LowerFogColor * maskfactor)) * 0.5;
	}
	
	// sample output of starfield method: (vec4(v * 0.01F * vec3(0.1F, 1.0F, 0.5F), 1.0F)
	maskfactor = 1.7f;
	vec4 outcolor = ((vec4(v * 0.01F * vec3(0.1F, 1.0F, 0.5F), 1.0F) * (2-maskfactor)) + (FogFactor * maskfactor)) * 0.5;
	
	// Hard fog range (to be replaced with cloud noise stuff thing idk)
	if (distFromCenter > 0.4) {
		outcolor = FogFactor;
	}
	// Upper Fog
	else if (distFromCenter > 0.2) {
		maskfactor = (distFromCenter - 0.2) * 10;
		// Blend starfield
		outcolor = ((outcolor * (2-maskfactor)) + (FogFactor * maskfactor)) * 0.5;
	}
	
	// Return pixel color
	fragColor = outcolor;
}