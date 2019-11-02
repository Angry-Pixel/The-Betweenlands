#version 120

//Definitions
#define CONST_EXP 2048
#define CONST_EXP2 2049
#define CONST_LINEAR 9729

//Sampler that holds the rendered world
uniform sampler2D s_diffuse;

//Sampler that holds the depth map
uniform sampler2D s_diffuse_depth;

//Sampler that holds the height map
uniform sampler2D s_height_map;

//Matrix to transform screen space coordinates to world space coordinates
uniform mat4 u_INVMVP;

struct FogVolume {
    vec3 position;
    vec3 color;
    vec3 size;
    float extinction;
    float inScattering;
};
uniform FogVolume u_fogVolumes[16];
uniform int u_fogVolumesAmount;

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

//http://iquilezles.org/www/articles/spheredensity/spheredensity.htm
/*float computeFog(vec3  ro, vec3  rd,   // ray origin, ray direction
                 vec3  sc, float sr,   // sphere center, sphere radius
                 float dbuffer) {
    // normalize the problem to the canonical sphere
    float ndbuffer = dbuffer / sr;
    vec3  rc = (ro - sc) / sr;
	
    // find intersection with sphere
    float b = dot(rd, rc);
    float c = dot(rc, rc) - 1.0f;
    float h = b*b - c;

    // not intersecting
    if(h<0.0f) {
    	return 0.0f;
	}
	
    h = sqrt( h );
    float t1 = -b - h;
    float t2 = -b + h;

    // not visible (behind camera or behind ndbuffer)
    if(t2<0.0f || t1>ndbuffer) {
    	return 0.0f;
	}

    // clip integration segment from camera to ndbuffer
    t1 = max(t1, 0.0f);
    t2 = min(t2, ndbuffer);

    // analytical integration of an inverse squared density
    float i1 = -(c*t1 + b*t1*t1 + t1*t1*t1/3.0f);
    float i2 = -(c*t2 + b*t2*t2 + t2*t2*t2/3.0f);
    
    return (i2 - i1) * (3.0f / 4.0f);
}*/

float volFog(float a, float b, float h, float h0, float alpha, float l) {
	float sina = -sin(alpha);
	float dh = h - h0;
	float f1n = a * exp(b * (sina * l - h0)) * (b * (dh + sina * l) - 1);
	float f2n = a * exp(b * (-h0)) * (b * dh - 1);
	float fd = b * b * sina;
	return (f1n - f2n) / fd;
}

void intersect(FogVolume volume, vec3 origin, vec3 invRayDir, out float entry, out float exit) {
    vec3 vMin = volume.position;
    vec3 vMax = vMin + volume.size;
    vec3 v1 = (vMin - origin) * invRayDir;
    vec3 v2 = (vMax - origin) * invRayDir;
    vec3 n = min(v1, v2);
    vec3 f = max(v1, v2);
    entry = max(n.x, max(n.y, n.z));
    exit = min(f.x, min(f.y, f.z));
}

float mod_float(float x, float y) {
    return x - y * floor(x / y);
}

void main() {
    vec3 fragPos = getFragPos(s_diffuse_depth);
    
    vec4 color = vec4(texture2D(s_diffuse, v_texCoord));
    
    vec3 rayDir = normalize(fragPos - u_viewPos);
    vec3 invRayDir = vec3(1.0F / rayDir.x, 1.0F / rayDir.y, 1.0F / rayDir.z);
    float dst = length(fragPos - u_viewPos);
    
    vec2 heightMapUv = vec2(mod_float((fragPos.x + u_renderPos.x + u_worldTime * 0.03F), 100.0F) / 25.0F, mod_float((fragPos.z + u_renderPos.z + u_worldTime * 0.05F), 100.0F) / 25.0F);
    float yo = (texture2D(s_height_map, heightMapUv).r - 0.5F) * 1.5F;
    vec3 f = vec3(fragPos.x, fragPos.y + yo, fragPos.z);
    vec3 ray = f - u_viewPos;
    float alpha = atan(ray.y / sqrt(ray.x*ray.x + ray.z*ray.z));  
    
    //TODO Get rid of for loop; render fog volumes to a texture with its ID in the red component,
    //then read from that texture and look up the fog volumes by its ID in the red component.
    for(int i = 0; i < u_fogVolumesAmount; i++) {
        FogVolume volume = u_fogVolumes[i];
        
        float entry;
        float exit;
        
        intersect(volume, u_viewPos, invRayDir, entry, exit);

        //TODO Get rid of branching
        if(exit > 0.0F && entry < exit) {
            entry = max(0, entry);
        
            float interaction = min(dst, exit) - entry;
        
            vec3 entryPoint = u_viewPos + rayDir * entry;
            
            float h0 = entryPoint.y - volume.position.y;
            
    	    float volFog = clamp(volFog(volume.inScattering, volume.extinction, volume.size.y, h0, alpha, interaction), 0, 1);
    	    
    	    color = mix(color, vec4(volume.color, 0), volFog * (1 - getFogMultiplier(fragPos)));
        }
    }
    
    gl_FragColor = color;
}


