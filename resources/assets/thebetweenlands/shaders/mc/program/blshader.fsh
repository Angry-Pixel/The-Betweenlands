#version 120

//Definitions
#define GL_EXP 2048
#define GL_LINEAR 9729

//Sampler that holds the rendered world
uniform sampler2D s_diffuse;

//Sampler that holds the depth map
uniform sampler2D s_diffuse_depth;

//G-Buffers
uniform sampler2D s_repellerShield;
uniform sampler2D s_repellerShield_depth;
uniform sampler2D s_gasParticles;
uniform sampler2D s_gasParticles_depth;

//Matrix to transform screen space coordinates to world space coordinates
uniform mat4 u_INVMVP;

//zNear and zFar
uniform float u_zNear;
uniform float u_zFar;

//Light data
uniform float u_lightSourcesX[32];
uniform float u_lightSourcesY[32];
uniform float u_lightSourcesZ[32];
uniform float u_lightColorsR[32];
uniform float u_lightColorsG[32];
uniform float u_lightColorsB[32];
uniform float u_lightRadii[32];
uniform float u_lightSources;

//Fog mode
uniform float u_fogMode;

//Cam pos
uniform vec3 u_camPos;

//Time in milliseconds
uniform float u_msTime;

//Fragment position [0.0, 1.0][0.0, 1.0]
varying vec2 v_texCoord;

//Size of one texel
varying vec2 v_oneTexel;

//Calculates the fragment world position (relative to camera)
vec3 getFragPos(sampler2D depthMap) {
    //Using the texture coordinate and the depth, the original vertex in world space coordinates can be calculated
    //The depth value from the depth buffer is not linear
    float zBuffer = texture2D(depthMap, v_texCoord).x;
    //float fragDepth = pow(zBuffer, 2);
	float fragDepth = zBuffer * 2.0F - 1.0F;
    
    //Calculate fragment world position relative to the camera position
    vec4 fragRelPos = vec4(v_texCoord.xy * 2.0 - 1.0, fragDepth, 1.0) * u_INVMVP;
    fragRelPos.xyz /= fragRelPos.w;
    
    return fragRelPos.xyz;
}

//Returns the fog color multiplier for a fragment
float getFogMultiplier(vec3 fragPos) {
    if(u_fogMode == GL_LINEAR) {
        //Calculate linear fog
        return clamp((length(fragPos) - gl_Fog.start) * gl_Fog.scale, 0.0F, 1.0F);
    } else if(u_fogMode == GL_EXP) {
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
    vec3 fragPos = getFragPos(s_diffuse_depth);
    
    //A color multiplier that is applied to the final color
    float colorMultiplier = 1.0F;
    
    //Set to true if fragment should be distorted
    bool distortion = false;
    //Strength of distortion
    float distortionMultiplier = 0.0F;
    
    //Holds the calculated color
    vec4 color = vec4(0.0F, 0.0F, 0.0F, 0.0F);
    
    
    
    
    //////// Lighting (Distortion) ////////
    //Calculate distance from fragment to light sources and apply color
    for(int i = 0; i < int(u_lightSources); i++) {
        vec3 lightPos = vec3(u_lightSourcesX[i], u_lightSourcesY[i], u_lightSourcesZ[i]);
        float dist = distance(lightPos, fragPos);
        float radius = u_lightRadii[i];
        if(dist < radius) {
            if(u_lightColorsR[i] == -1 && u_lightColorsG[i] == -1 && u_lightColorsB[i] == -1) {
                distortion = true;
				if(distortionMultiplier < 0.6F) {
					distortionMultiplier += max(distortionMultiplier, 1.0F - pow(dist / radius, 4));
				}
            }
        }
    }
    
    
    
    
    //////// Repeller shield ////////
    vec4 repellerShieldBuffCol = texture2D(s_repellerShield, v_texCoord);
    bool inShield = repellerShieldBuffCol.a != 0.0F;
    if(inShield) {
        //Get shield frag pos
        vec3 fragPos2 = getFragPos(s_repellerShield_depth);
        
		//Holds calculated shield color
		vec4 shieldFragColor = vec4(0.0F, 0.0F, 0.0F, 0.0F);
		
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
            float fragDistortion = (fragPos2.y + u_camPos.y + (cos(fragPos2.x + u_camPos.x) * sin(fragPos2.z + u_camPos.z)) * 2.0F) * 8.0F;
            float colorDistortion = ((sin(fragDistortion + u_msTime / 300.0F) + 1.0F) / 800.0F);
            shieldFragColor += vec4(repellerShieldBuffCol.rgb * colorDistortion * 10.0F, 1.0F);
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
            shieldFragColor += vec4(repellerShieldBuffCol.rgb * dsCol, 1.0F);
        }
		
		//Applies fogged shield color to the fragment color
		color += applyFog(fragPos2, shieldFragColor);
    }
    
    
	
	
	//////// Gas Particles ////////
	vec4 gasParticlesBuffCol = texture2D(s_gasParticles, v_texCoord);
	bool inGas = gasParticlesBuffCol.a != 0.0F;
    if(inGas) {
		//Get gas frag pos
        vec3 fragPos2 = getFragPos(s_gasParticles_depth);
		
		//Get depth (distance to camera)
        float fragCamDist = length(fragPos);
        float fragCamDist2 = length(fragPos2);
        
        //Check if repeller shield is behind or in front of the diffuse fragment
        bool inBack = fragCamDist <= fragCamDist2;
        if(!inBack) {
			color += applyFog(fragPos2, gasParticlesBuffCol);
			distortion = true;
			distortionMultiplier += 1.5F;
			/////WIP stuff/////
		}
	}
	
	
    
    //////// Distortion and diffuse texel ////////
    if(!distortion) {
        color += vec4(texture2D(s_diffuse, v_texCoord));
    } else {
        float fragDistortion = (fragPos.y + u_camPos.y + (cos(fragPos.x + u_camPos.x) * sin(fragPos.z + u_camPos.z))) * 5.0F;
        color += vec4(texture2D(s_diffuse, v_texCoord + vec2(sin(fragDistortion + u_msTime / 300.0F) / 800.0F, 0.0F) * distortionMultiplier));
    }
    
    //////// Lighting ////////
    //Calculate distance from fragment to light sources and apply color
    for(int i = 0; i < int(u_lightSources); i++) {
        vec3 lightPos = vec3(u_lightSourcesX[i], u_lightSourcesY[i], u_lightSourcesZ[i]);
        float dist = distance(lightPos, fragPos);
        float radius = u_lightRadii[i];
        if(dist < radius) {
            if(u_lightColorsR[i] != -1 || u_lightColorsG[i] != -1 || u_lightColorsB[i] != -1) {
                color *= vec4(1.0F, 1.0F, 1.0F, 1.0F) + vec4(vec3(u_lightColorsR[i], u_lightColorsG[i], u_lightColorsB[i]) * (1.0F - dist / radius), 0.0F);
            }
        }
    }
    
    //Return final color
    gl_FragColor = color * colorMultiplier;
}