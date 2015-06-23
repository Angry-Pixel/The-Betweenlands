#version 120

//Definitions
#define GL_EXP 2048
#define GL_LINEAR 9729

//Sampler that holds the rendered world
uniform sampler2D DiffuseSampler;

//DepthSampler holds the depth buffer
uniform sampler2D DepthSampler;

//G-Buffers
uniform sampler2D GBuffer1;
uniform sampler2D GBuffer1_Depth;

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

//Fog mode
uniform float FogMode;

//Cam pos
uniform vec3 CamPos;

//Time in milliseconds
uniform float MSTime;

//Fragment position [0.0, 1.0][0.0, 1.0]
varying vec2 texCoord;

//Size of one texel
varying vec2 oneTexel;

//Calculates the fragment world position (relative to camera)
vec3 getFragPos(sampler2D depthMap) {
    //Using the texture coordinate and the depth, the original vertex in world space coordinates can be calculated
    //The depth value from the depth buffer is not linear
    float zBuffer = texture2D(depthMap, texCoord).x;
    float fragDepth = pow(zBuffer, 2);
    
    //Calculate fragment world position relative to the camera position
    vec4 fragRelPos = vec4(texCoord.xy * 2.0 - 1.0, fragDepth, 1.0) * INVMVP;
    fragRelPos.xyz /= fragRelPos.w;
    
    return fragRelPos.xyz;
}

//Returns the fog color multiplier for a fragment
float getFogMultiplier(vec3 fragPos) {
    if(FogMode == GL_LINEAR) {
        //Calculate linear fog
        return clamp((length(fragPos) - gl_Fog.start) * gl_Fog.scale, 0.0F, 1.0F);
    } else if(FogMode == GL_EXP) {
        //Calculate exponential fog
        return 1.0F - clamp(exp(-gl_Fog.density * length(fragPos)), 0.0F, 1.0F);
    }
    return 0.0F;
}

//Applies fog to the color of a fragment
vec4 applyFog(vec3 fragPos, vec4 color) {
    return mix(color, vec4(0, 0, 0, 0), getFogMultiplier(fragPos));
}

void main() {
    //Get fragment world position
    vec3 fragPos = getFragPos(DepthSampler);
    
    //A color multiplier that is applied to the final color
    float colorMultiplier = 1.0F;
    
    //Set to true if fragment should be distorted
    bool distortion = false;
    //Strength of distortion, depends on fragment to source distance
    float distortionMultiplier = 0.0F;
    
    //Holds the calculated color
    vec4 color = vec4(0.0F, 0.0F, 0.0F, 0.0F);
    
    
    
    
    //////// Lighting ////////
    //Calculate distance from fragment to light sources and apply color
    for(int i = 0; i < int(LightSources); i++) {
        vec3 lightPos = vec3(LightSourcesX[i], LightSourcesY[i], LightSourcesZ[i]);
        float dist = distance(lightPos, fragPos);
        float radius = LightRadii[i];
        if(dist < radius) {
            if(LightColorsR[i] == -1 && LightColorsG[i] == -1 && LightColorsB[i] == -1) {
                distortion = true;
                distortionMultiplier += max(distortionMultiplier, 1.0F - pow(dist / radius, 4));
            } else {
                color += vec4(LightColorsR[i], LightColorsG[i], LightColorsB[i], 0.0F) * (1.0F - dist / radius);
            }
        }
    }
    
    
    
    
    //////// G-Buffer 1 - Repeller shield ////////
    vec4 GBuff1Col = texture2D(GBuffer1, texCoord);
    bool inShield = GBuff1Col.a == 1.0F;
    if(inShield) {
        //Get shield frag pos
        vec3 fragPos2 = getFragPos(GBuffer1_Depth);
        
        //Get depth (distance to camera) and distance between fragments
        float dist = distance(fragPos2, fragPos);
        float fragCamDist = length(fragPos);
        float fragCamDist2 = length(fragPos2);
        
        //Check if repeller shield is behind or in front of the diffuse fragment
        bool inBack = fragCamDist <= fragCamDist2;
        if(!inBack) {
            //Calculate distortion and color multiplier
            distortion = true;
            distortionMultiplier += 1.5F / (pow(fragCamDist2 - fragCamDist, 2) / 100.0F + 1.0F);
            
            //Calculate color multiplier (affected by fog)
            colorMultiplier *= 1.0F - mix(0.1F, 0.0F, getFogMultiplier(fragPos2));
            
            //Calculate color distortion
            float fragDistortion = (fragPos2.y + CamPos.y + (cos(fragPos2.x + CamPos.x) * sin(fragPos2.z + CamPos.z)) * 2.0F) * 8.0F;
            float colorDistortion = (sin(fragDistortion + MSTime / 300.0F) / 800.0F);
            color += applyFog(fragPos2, vec4(GBuff1Col.rgb * colorDistortion * 5.0F, 1.0F));
        }
        
        //Apply intersection glow
        if(dist / 2.0F < 0.1F) {
            float dstMultiplier = 200.0F;
            float dstFalloff = 2.0F;
            if(inBack) {
                dstFalloff = 4.0F;
                dstMultiplier = 3000.0F;
            }
            float dsCol = pow((0.1F - dist / 2.0F), dstFalloff) * dstMultiplier;
            color += applyFog(fragPos2, vec4(GBuff1Col.rgb * dsCol, 0.0F));
        }
    }
    
    
    
    //////// Distortion and diffuse texel ////////
    if(!distortion) {
        color += vec4(texture2D(DiffuseSampler, texCoord));
    } else {
        float fragDistortion = (fragPos.y + CamPos.y + (cos(fragPos.x + CamPos.x) * sin(fragPos.z + CamPos.z))) * 5.0F;
        color += vec4(texture2D(DiffuseSampler, texCoord + vec2(sin(fragDistortion + MSTime / 300.0F) / 800.0F, 0.0F) * distortionMultiplier));
    }
    
    
    
    //Return final color
    gl_FragColor = color * colorMultiplier;
}