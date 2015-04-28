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
uniform float   LightSourcesX[32];
uniform float   LightSourcesY[32];
uniform float   LightSourcesZ[32];
uniform float   LightColorsR[32];
uniform float   LightColorsG[32];
uniform float   LightColorsB[32];
uniform float   LightRadii[32];
uniform float   LightSources;

//Cam pos
uniform vec3 CamPos;

//Time in milliseconds
uniform float MSTime;

//Fragment position [0.0, 1.0][0.0, 1.0]
varying vec2 texCoord;

//Size of one texel
varying vec2 oneTexel;

//Claculates the fragment world position (relative to camera)
vec3 getFragPos() {
    //Using the texture coordinate and the depth, the original vertex in world space coordinates can be calculated
    //The depth value from the depth buffer is not linear
    float zBuffer = texture2D(DepthSampler, texCoord).x;
    float fragDepth = pow(zBuffer, 2);
    
    //Calculate fragment world position relative to the camera position
    vec4 fragRelPos = vec4(texCoord.xy * 2.0 - 1.0, fragDepth, 1.0) * INVMVP;
    fragRelPos.xyz /= fragRelPos.w;
    
    return fragRelPos.xyz;
}

void main() {
    //Get fragment world position
    vec3 fragPos = getFragPos();
    
    //Set to true if fragment should be distorted
    bool distortion = false;
    //Strength of distortion, depends on fragment to source distance
    float distortionMultiplier = 0.0F;
    
    //Holds the calculated color
    vec4 color;
    
    //Calculate distance from fragment to light sources and apply color
    for(int i = 0; i < int(LightSources); i++) {
        vec3 lightPos = vec3(LightSourcesX[i], LightSourcesY[i], LightSourcesZ[i]);
        float dist = distance(lightPos, fragPos);
        float radius = LightRadii[i];
        if(dist < radius) {
            if(LightColorsR[i] == -1 && LightColorsG[i] == -1 && LightColorsB[i] == -1) {
                distortion = true;
                distortionMultiplier = max(distortionMultiplier, 1.0 - pow(dist / radius, 4));
            } else {
                color.xyz += vec3(LightColorsR[i], LightColorsG[i], LightColorsB[i]) * (1.0 - dist / radius);
            }
        }
    }
    
    if(!distortion) {
        color += texture2D(DiffuseSampler, texCoord);
    } else {
        float fragDistortion = (fragPos.y + CamPos.y + (cos(fragPos.x + CamPos.x) * sin(fragPos.z + CamPos.z))) * 5;
        color += texture2D(DiffuseSampler, texCoord + vec2(sin(fragDistortion + MSTime / 300) / 800 * distortionMultiplier, 0));
    }
    
    //Return calculated color
    gl_FragColor = color;
}