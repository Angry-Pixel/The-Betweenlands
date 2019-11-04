#version 120

//Definitions
#define CONST_EXP 2048
#define CONST_EXP2 2049
#define CONST_LINEAR 9729

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

//Light data
struct LightSource {
    vec3 position;
    vec3 color;
    float radius;
};
uniform LightSource u_lightSources[32];
uniform int u_lightSourcesAmount;

//Fog mode
uniform int u_fogMode;

//View pos (i.e. the "eye"), relative to the render position
uniform vec3 u_viewPos;

//Render pos (i.e. the coordinate of the player)
//Everything is rendered relative to this position
uniform vec3 u_renderPos;

//World time in ticks
uniform float u_worldTime;

//Fragment position [0.0, 1.0][0.0, 1.0]
varying vec2 v_texCoord;

//Calculates the fragment world position (relative to camera)
vec3 getFragPos(sampler2D depthMap) {
    //Using the texture coordinate and the depth, the original vertex in world space coordinates can be calculated
    //The depth value from the depth buffer is not linear
    float zBuffer = texture2D(depthMap, v_texCoord).x;
    //float fragDepth = pow(zBuffer, 2);
	float fragDepth = zBuffer * 2.0F - 1.0F;
    
    //Calculate fragment world position relative to the camera position
    vec4 fragRelPos = vec4(v_texCoord.xy * 2.0F - 1.0F, fragDepth, 1.0F) * u_INVMVP;
    fragRelPos.xyz /= fragRelPos.w;
    
    return fragRelPos.xyz;
}

//Returns the fog color multiplier for a fragment
float getFogMultiplier(vec3 fragPos) {
    if(u_fogMode == CONST_LINEAR) {
        //Calculate linear fog
        return clamp((length(fragPos) - gl_Fog.start) * gl_Fog.scale, 0.0F, 1.0F);
    } else if(u_fogMode == CONST_EXP) {
        //Calculate exponential fog
        return 1.0F - clamp(exp(-gl_Fog.density * length(fragPos)), 0.0F, 1.0F);
    } else if(u_fogMode == CONST_EXP2) {
        //Calculate exponential^2 fog
        return 1.0F - clamp(exp(-pow(gl_Fog.density * length(fragPos), 2.0F)), 0.0F, 1.0F);
    }
    return 0.0F;
}

//Applies fog to the color of a fragment
vec4 applyFog(vec3 fragPos, vec4 color) {
    return mix(color, vec4(0.0F, 0.0F, 0.0F, 0.0F), getFogMultiplier(fragPos));
}

void main() {
    //Get fragment world position
    vec3 fragPos = getFragPos(s_diffuse_depth);
    
    //A color multiplier that is applied to the final color
    float colorMultiplier = 1.0F;
    
    //Strength of distortion
    float distortionMultiplier = 0.0F;
    
    //Holds the calculated color
    vec4 color = vec4(0.0F, 0.0F, 0.0F, 0.0F);
    
    
    
    
    //////// Lighting (Distortion) ////////
    //Calculate distance from fragment to light sources and apply color
    for(int i = 0; i < u_lightSourcesAmount; i++) {
        LightSource light = u_lightSources[i];
        vec3 lightPos = light.position;
        float dist = distance(lightPos, fragPos);
        float radius = light.radius;
        if(dist < radius) {
            vec3 lightColor = light.color;
            if(lightColor.r == -1 && lightColor.g == -1 && lightColor.b == -1) {
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
        vec3 shieldFragPos = getFragPos(s_repellerShield_depth);
        
		//Holds calculated shield color
		vec4 shieldFragColor = vec4(0.0F, 0.0F, 0.0F, 0.0F);
		
        //Get depth (distance to camera) and distance between fragments
        float dist = distance(shieldFragPos, fragPos);
        float fragCamDist = length(fragPos - u_viewPos);
        float shieldFragCamDist = length(shieldFragPos - u_viewPos);
        
        //Check if repeller shield is behind or in front of the diffuse fragment
        bool inBack = fragCamDist <= shieldFragCamDist;
        if(repellerShieldBuffCol.a < 0.99F) {
        	inBack = fragCamDist > shieldFragCamDist;
        }
        if(!inBack) {
            //Calculate distortion and color multiplier
            //distortionMultiplier += 1.5F / (pow(shieldFragCamDist - fragCamDist, 2) / 100.0F + 1.0F);
            
            //Calculate color multiplier (affected by fog)
            colorMultiplier *= 1.0F - mix(0.1F, 0.0F, getFogMultiplier(shieldFragPos));
            
            //Calculate color distortion
            float fragDistortion = (shieldFragPos.y + u_renderPos.y + (cos(shieldFragPos.x + u_renderPos.x) * sin(shieldFragPos.z + u_renderPos.z)) * 2.0F) * 8.0F;
            float colorDistortion = ((sin(fragDistortion + u_worldTime * 50.0F / 300.0F) + 1.0F) / 200.0F);
            shieldFragColor += vec4(repellerShieldBuffCol.rgb * colorDistortion * 10.0F, 0.0F);
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
            shieldFragColor += vec4(repellerShieldBuffCol.rgb * dsCol, 0.0F);
        }
		
		//Applies fogged shield color to the fragment color
		color += applyFog(shieldFragPos, shieldFragColor);
    }
    
    

	//////// Gas Particles ////////
	vec4 gasParticlesBuffCol = texture2D(s_gasParticles, v_texCoord);
	bool inGas = gasParticlesBuffCol.a != 0.0F;
    if(inGas) {
		//Get gas frag pos
        vec3 gasFragPos = getFragPos(s_gasParticles_depth);
		
		//Get depth (distance to camera)
        float fragCamDist = length(fragPos);
        float gasFragCamDist = length(gasFragPos);
        
        //Check if gas particle is behind or in front of the diffuse fragment
        bool inBack = fragCamDist <= gasFragCamDist;
        if(!inBack) {
			distortionMultiplier += 20.0F * applyFog(gasFragPos, gasParticlesBuffCol).a;
		}
	}
	
	
    
    //////// Distortion and diffuse texel ////////
    vec4 sourceColor;
    if(distortionMultiplier <= 0.0F) {
        sourceColor = vec4(texture2D(s_diffuse, v_texCoord));
        color += sourceColor;
    } else {
        float fragDistortion = (fragPos.y + u_renderPos.y + (cos(fragPos.x + u_renderPos.x) * sin(fragPos.z + u_renderPos.z))) * 5.0F;
        sourceColor = vec4(texture2D(s_diffuse, v_texCoord + vec2(sin(fragDistortion + u_worldTime * 50.0F / 300.0F) / 800.0F, 0.0F) * distortionMultiplier));
        color += sourceColor;
    }
    
    //////// Lighting ////////
    //Calculate distance from fragment to light sources and apply color
	float lightingFogMultiplier = 1.0F - getFogMultiplier(fragPos);
    for(int i = 0; i < u_lightSourcesAmount; i++) {
        LightSource light = u_lightSources[i];
        vec3 lightPos = light.position;
        float dist = distance(lightPos, fragPos);
        float radius = light.radius;
        vec3 lightColor = light.color;
        
        if(dist < radius) {
            if(lightColor.r != -1 || lightColor.g != -1 || lightColor.b != -1) {
                color += sourceColor * (vec4(lightColor * pow(1.0F - dist / radius, 2), 0.0F) * lightingFogMultiplier);
            }
        }
    }
    
    //Return final color
    //vec4 depthCol = texture2D(s_diffuse_depth, v_texCoord);
    //float fragDepth = depthCol.x * 2.0F - 1.0F;
    //gl_FragColor = vec4(fragDepth, fragDepth, fragDepth, 1.0F);//color * colorMultiplier;
	
    gl_FragColor = color * colorMultiplier;
}


