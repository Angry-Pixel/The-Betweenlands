#version 120

//Sampler that holds the rendered world
uniform sampler2D DiffuseSampler;

//DepthSampler holds the depth buffer
uniform sampler2D DepthSampler;

//Matrix to transform screen space coordinates to world space coordinates
uniform mat4 INVMVP;

//Light data
uniform float   LightSourcesX[64];
uniform float   LightSourcesY[64];
uniform float   LightSourcesZ[64];
uniform float   LightColorsR[64];
uniform float   LightColorsG[64];
uniform float   LightColorsB[64];
uniform float   LightRadii[64];
uniform float   LightSources;

//Fragment position [0.0, 1.0][0.0, 1.0]
varying vec2 texCoord;

//Size of one texel
varying vec2 oneTexel;

void main() {
    //Using the texture coordinate and the depth, the original vertex in world space coordinates can be calculated
    //Allowing to perform 3D calculations in a post-processing shader
    //The depth value is linearized to make it more visible
    
    float fragDepth = texture2D(DepthSampler, texCoord).x;
    
    vec4 fragRelPos = vec4(texCoord.xy * 2.0 - 1.0, fragDepth, 1.0) * INVMVP;
    fragRelPos.xyz /= fragRelPos.w;
    
    vec4 color = texture2D(DiffuseSampler, texCoord);
    vec4 initColor = vec4(color.r, color.g, color.b, color.a);
    
    for(int i = 0; i < int(LightSources); i++) {
        vec3 lightPos = vec3(LightSourcesX[i], LightSourcesY[i], LightSourcesZ[i]);
        float dist = distance(lightPos, fragRelPos.xyz);
        float radius = LightRadii[i];
        if(dist < radius) {
            color.xyz += vec3(LightColorsR[i], LightColorsG[i], LightColorsB[i]) * (1.0 - dist / radius);
        }
    }
    
    gl_FragColor = color;
}