#version 430

// Star Nest by Pablo Román Andrioli
// This content is under the MIT License.
// This dude made one hell of a star field shader

#define iterations 13
#define formuparam 0.51

#define volsteps 5
#define stepsize 0.25

#define zoom   0.500
#define tile   0.850
#define speed  0.4

#define brightness 0.0015
#define darkmatter 0.300
#define distfading 0.730
#define saturation 0.850

uniform float DebugMode;
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
uniform float DayLight;
uniform vec2 Resolution;
uniform vec2 FOV;
uniform vec3 Rotation;
uniform vec3 shadertest;

in vec4 gl_FragCoord;

in vec2 texCoord0;
in vec2 texCoord1;

out vec4 fragColor;
out float gl_FragDepth;

vec4 SampleTexture(int type, int index, int subindex, vec2 samplepoint, vec4 cullset);
vec4 SampleNoise(vec2 samplepoint, vec2 scale, vec2 timeScale);

layout(std430, binding = 1) buffer uvinput {
	int skyUVIndex;
	int effectUVIndex;		// effects uv starting point in data[]
	int riftUVIndex;		// rift uv starting point in data[]
	int riftDataIndex;		// rift data starting point in data[]
	int riftcount;			// count of rifts
	float data[];			// raw data from shader handler (dynamic array)
	// shame hlsl 5.0 doesn't have dynamic arrays
};

// Functions
// To collect rift texture
vec4 SampleTexture(int type, int index, int subindex, vec2 samplepoint, vec4 cullset) {
	// Get cords
	int startpoint = skyUVIndex + (4 * index);
	
	if (type == 1) {
		startpoint = effectUVIndex + (4 * index);
	}
	else if (type == 2) {
		startpoint = riftUVIndex + (12 * index) + (4 * subindex);
	}

	vec2 OutCord = vec2((samplepoint.x * (data[startpoint+2] - data[startpoint])) + data[startpoint], (samplepoint.y * (data[startpoint+3] - data[startpoint+1])) + data[startpoint+1]);
	
	// Cull check
	if (OutCord.x <= data[startpoint+2] && OutCord.x >= data[startpoint] && OutCord.y <= data[startpoint+3] && OutCord.x >= data[startpoint+1] && OutCord.x >= 0.01f && OutCord.y >= 0.01f && OutCord.x <= 0.99f && OutCord.y <= 0.99f) {
		return texture(Sampler0, OutCord);
	}
	
	return cullset;
}

// Noise sample
// Samples noise texture moved by time passed
vec4 SampleNoise(vec2 samplepoint, vec2 scale, vec2 timeScale) {
    // Get time sample pos
    float time = (GameTime*speed+.25)*100.0f;
    vec4 texpos = vec4(data[0], data[1], data[2], data[3]);
    vec2 texsize = vec2(data[2]-data[0], data[3]-data[1]);
    vec2 timepos = vec2(mod((time * timeScale.x) + (samplepoint.x * scale.x), texsize.x-0.02) + 0.01, mod((time * timeScale.y) + (samplepoint.y * scale.y), texsize.y-0.02) + 0.01);

    // Get the noise texture cords
    // I wanted to avoid modulus here, too bad
    return texture(Sampler0, vec2(((timepos.x * (texpos[2] - texpos.x)) + texpos.x) + timepos.x, ((timepos.y * (texpos[3] - texpos.y)) + texpos.y) + timepos.y));
    //return texture(Sampler0, vec2(texpos.x + mod(((timepos.x * scale.x) - texpos.x), texsize.x), texpos.y + mod(((timepos.y * scale.y) - texpos.y), texsize.y)));
}

// To use static sky texture, use Sampler0 at texCoord0
// Use Sampler1 at texCoord1 to get fog detailing texture
void main() {

	// TODO: Major cleanup of this mess of stuff...

	// TODO: Add noise texture and use to distort rift texture position sampling

	// div by zero safety check
	if (Resolution.x == 0.0f || Resolution.y == 0.0f) {
		return;
	}
	
	// if x = 0.5 expected output = translatematrix.x
	
	vec2 normalfactor = vec2(0.5f, 0.5f);
	if (gl_FragCoord.x != 0.0) {
		normalfactor.x = (gl_FragCoord.x / Resolution.x) - 0.5f;
	}
	
	if (gl_FragCoord.y != 0.0) {
		normalfactor.y = (gl_FragCoord.y / Resolution.y) - 0.5f;
	}
	
	// TODO: send rotation directly to shader so eatch fragment dosent need to to compute it itself (done using uv data in vertices)
	
	vec3 sampleNormal = vec3(sin((FOV.x * normalfactor.x) + Rotation.y), cos((FOV.y * normalfactor.y) - Rotation.x), 0.0);
	
	if (DebugMode != 0.0f) {
		
		// Show Normal output of fragment
		vec3 outnormal = vec3(sampleNormal.x + (sin(3.14159)*normalfactor.x), sampleNormal.y + (sin(3.14159)*normalfactor.x), sampleNormal.z);
		
		
		fragColor = SampleTexture(0,0,0,sampleNormal.xy,vec4(0.0,0.0,0.0,1.0));
		
		//fragColor = vec4(sampleNormal, 1.0f);
		
		return;
	}
	
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
	float distFromCenter = distance(texCoord0, vec2(0.5, 0.5)) * 1.2f;
	
	// use "distFromCenter" to crate a new rotated vector
	// glsl's atan inputs are the wrong way around for what im used to
	float originAng = atan(texCoord0[1]-0.5, texCoord0[0]-0.5);
	vec2 fogLayerTexCoord1 = vec2((sin(FogRotation + originAng) * (distFromCenter - 0.005f)) + 0.5f, (cos(FogRotation + originAng) * (distFromCenter - 0.005f)) + 0.5f);
	vec2 fogLayerTexCoord3 = vec2((sin((-FogRotation) + originAng) * (distFromCenter - 0.005f)) + 0.5f, (cos((-FogRotation) + originAng) * (distFromCenter - 0.005f)) + 0.5f);
	vec2 fogLayerTexCoord2 = vec2((sin((FogRotation * 0.5) + originAng) * (distFromCenter + 0.025f)) + 0.5f, (cos((FogRotation * 0.5) + originAng) * (distFromCenter + 0.025f)) + 0.5f);
	vec2 fogLayerTexCoord4 = vec2((sin((-FogRotation * 0.5) + originAng) * (distFromCenter + 0.05f)) + 0.5f, (cos((-FogRotation * 0.5) + originAng) * (distFromCenter + 0.05f)) + 0.5f);
	
	// blend mask factors
	vec4 LowerFogMask = (SampleTexture(0,0,0,fogLayerTexCoord1,vec4(0,0,0,0)) + SampleTexture(0,0,0,fogLayerTexCoord2,vec4(0,0,0,0)) + SampleTexture(0,0,0,fogLayerTexCoord3,vec4(0,0,0,0)) + SampleTexture(0,0,0,fogLayerTexCoord4,vec4(0,0,0,0))) * 0.25f;
	
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
	vec4 outcolor = ((vec4(v * 0.01F * vec3(0.1F, 1.0F, 0.6F), 1.0F) * (2-maskfactor)) + (FogFactor * maskfactor)) * 0.5;
	
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
	// UV1 - UV0 stretch factor
	// model uv - stretchuv
	
	// Rift iterator
	for (int rift = 0; rift < riftcount; rift++) {

		// TODO: Add noise texture and use to distort rift texture position sampling
        // Get noise translation (make sure to clamp into texture bounds)

		int riftpointer = riftDataIndex + (rift * 7);
		vec4 testcolor = vec4(0.0f,0.0f,0.0f,1.0f);

		vec2 riftorigin = vec2((cos(data[riftpointer+1]) * data[riftpointer+2]) + 0.5, (sin(data[riftpointer+1]) * data[riftpointer+2]) + 0.5);
		vec2 riftsize = vec2(data[riftpointer+3], data[riftpointer+4]);
		float riftrotation = data[riftpointer+6];
		
		// if point sample pount is winthin these bounds
		
		// modifyed tex cord
		float distFTC = sqrt(pow(texCoord0.x - 0.5, 2) + pow(texCoord0.y - 0.5, 2)) * 1.7;
		
		
		float pixelangletost = atan(texCoord0.x - 0.5, texCoord0.y - 0.5);
		float pixelanglerift = atan(texCoord0.x - riftorigin.x, texCoord0.y - riftorigin.y);
		
		float riftdistmix = mix(riftsize.x, riftsize.y, (cos((pixelanglerift + data[riftpointer+1]) * 2.0) * 0.5) + 0.5);
		
		vec2 minusmod = vec2(sin(pixelangletost) * distFTC, cos(pixelangletost) * (1.0-distFTC));
		
		vec2 texCoordMod = vec2(texCoord0.x-0.5, texCoord0.y-0.5);
		
		texCoordMod *= (1.0+1.0+distFTC*distFTC);
		
		texCoordMod.x += 0.5;
		texCoordMod.y += 0.5;
		
		float distfromcore = sqrt(pow(texCoordMod.x - riftorigin.x, 2) + pow(texCoordMod.y - riftorigin.y, 2));
		
		// bad 2 point axis locked square
		// texCoord0.x <= point1.x && texCoord0.x >= point3.x && texCoord0.y <= point1.y && texCoord0.y >= point3.y;
		// bool rift = texCoord0.x <= point1.x && texCoord0.x >= point3.x && texCoord0.y <= point1.y && texCoord0.y >= point3.y;
		
		
		// check if in bounds
		if (distfromcore < riftdistmix) {
			
			//fragColor = vec4(0.0f,0.0f,1.0f,1.0f);
			
			//return;


			// sample rift texture at the point
			vec2 modoutput = vec2((((texCoordMod.x - riftorigin.x) * 0.5) / (riftsize.x*0.5)) + 1.0, (((texCoordMod.y - data[riftpointer+2]) * 0.5) / (riftsize.y*0.5)) + 1.0);
			
			// dist
			float dist = sqrt(pow(texCoordMod.x - riftorigin.x, 2) + pow(texCoordMod.y - riftorigin.y, 2)) * 3;
			
			// pixel angle
			float pixelangle = atan(texCoordMod.x - riftorigin.x, texCoordMod.y - riftorigin.y);
			
			// x strip
			float xstrip = ((sin(pixelangle+riftrotation)*dist)*0.125f)+0.5f;
			
			// y strip
			float ystrip = ((cos(pixelangle+riftrotation)*dist)*0.125f)+0.5f;
			
			if (xstrip > 1.0 || xstrip < 0.0 || ystrip > 1.0 || ystrip < 0.0) {
				break;
			}

            vec2 samplepos = vec2(xstrip, ystrip);

            // Get translation
            vec4 translation = SampleNoise(vec2(xstrip,ystrip), vec2(1.0f,1.0f), vec2(0.25f,0.25f));

            vec2 translatedpos = vec2(xstrip + (translation.x*0.0125) - 0.005125, ystrip + (translation.y*0.0125) - 0.005125);

			// get mask and fill in with blank color
			
			vec4 mask = SampleTexture(2, 0, 2, translatedpos, vec4(1.0f,1.0f,1.0f,1.0f));
			
			// if the fragment hasent been discarded then draw the overlay
			vec4 dayoverlay = SampleTexture(2, 0, 0, samplepos, vec4(1.0f,1.0f,1.0f,0.0f));
			vec4 nightoverlay = SampleTexture(2, 0, 1, translatedpos, vec4(1.0f,1.0f,1.0f,0.0f));
			vec4 overlay = mix(nightoverlay, dayoverlay, DayLight);
			
			float outalpha = mask[3];
			
			if (outcolor[3] <= mask[3]) {
				outalpha = outcolor[3];
			}

			outcolor = vec4(mix(outcolor.rgb,overlay.rgb, overlay[3]), outalpha);
			//outcolor = mix(outcolor, , 0.5f);
		}
	}
	
	fragColor = outcolor;
}