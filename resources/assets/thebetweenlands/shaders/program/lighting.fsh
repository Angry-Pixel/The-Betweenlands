#version 120

//Sampler that holds the rendered world
uniform sampler2D DiffuseSampler;

//DepthSampler holds the depth buffer
uniform sampler2D DepthSampler;

//Matrix to transform screen space coordinates to world space coordinates
uniform mat4 INVMVP;

//zNear and zFar
uniform float zNear;
uniform float zFar;

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
    //The depth value from the depth buffer is not linear
    float zBuffer = texture2D(DepthSampler, texCoord).x;
    float fragDepth = pow(zBuffer, 2);
    
    //Calculate fragment world position relative to the camera position
    vec4 fragRelPos = vec4(texCoord.xy * 2.0 - 1.0, fragDepth, 1.0) * INVMVP;
    fragRelPos.xyz /= fragRelPos.w;
    
    //Get diffuse color
    vec4 color = texture2D(DiffuseSampler, texCoord);
    
    //Calculate distance from fragment to light sources and apply color
    for(int i = 0; i < int(LightSources); i++) {
        vec3 lightPos = vec3(LightSourcesX[i], LightSourcesY[i], LightSourcesZ[i]);
        float dist = distance(lightPos, fragRelPos.xyz);
        float radius = LightRadii[i];
        if(dist < radius) {
            color.xyz += vec3(LightColorsR[i], LightColorsG[i], LightColorsB[i]) * (1.0 - dist / radius);
        }
    }
    
    //Return calculated color
    gl_FragColor = color;
}