#version 120

//Sampler that holds the rendered world
uniform sampler2D s_diffuse;

//Sampler that holds the depth map
uniform sampler2D s_diffuse_depth;

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

vec4 mod289(vec4 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0; }

float mod289(float x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0; }

vec4 permute(vec4 x) {
     return mod289(((x*34.0)+1.0)*x);
}

float permute(float x) {
     return mod289(((x*34.0)+1.0)*x);
}

vec4 taylorInvSqrt(vec4 r)
{
  return 1.79284291400159 - 0.85373472095314 * r;
}

float taylorInvSqrt(float r)
{
  return 1.79284291400159 - 0.85373472095314 * r;
}

vec4 grad4(float j, vec4 ip)
  {
  const vec4 ones = vec4(1.0, 1.0, 1.0, -1.0);
  vec4 p,s;

  p.xyz = floor( fract (vec3(j) * ip.xyz) * 7.0) * ip.z - 1.0;
  p.w = 1.5 - dot(abs(p.xyz), ones.xyz);
  s = vec4(lessThan(p, vec4(0.0)));
  p.xyz = p.xyz + (s.xyz*2.0 - 1.0) * s.www;

  return p;
  }

// (sqrt(5) - 1)/4 = F4, used once below
#define F4 0.309016994374947451

float snoise(vec4 v)
  {
  const vec4  C = vec4( 0.138196601125011,  // (5 - sqrt(5))/20  G4
                        0.276393202250021,  // 2 * G4
                        0.414589803375032,  // 3 * G4
                       -0.447213595499958); // -1 + 4 * G4

// First corner
  vec4 i  = floor(v + dot(v, vec4(F4)) );
  vec4 x0 = v -   i + dot(i, C.xxxx);

// Other corners

// Rank sorting originally contributed by Bill Licea-Kane, AMD (formerly ATI)
  vec4 i0;
  vec3 isX = step( x0.yzw, x0.xxx );
  vec3 isYZ = step( x0.zww, x0.yyz );
//  i0.x = dot( isX, vec3( 1.0 ) );
  i0.x = isX.x + isX.y + isX.z;
  i0.yzw = 1.0 - isX;
//  i0.y += dot( isYZ.xy, vec2( 1.0 ) );
  i0.y += isYZ.x + isYZ.y;
  i0.zw += 1.0 - isYZ.xy;
  i0.z += isYZ.z;
  i0.w += 1.0 - isYZ.z;

  // i0 now contains the unique values 0,1,2,3 in each channel
  vec4 i3 = clamp( i0, 0.0, 1.0 );
  vec4 i2 = clamp( i0-1.0, 0.0, 1.0 );
  vec4 i1 = clamp( i0-2.0, 0.0, 1.0 );

  //  x0 = x0 - 0.0 + 0.0 * C.xxxx
  //  x1 = x0 - i1  + 1.0 * C.xxxx
  //  x2 = x0 - i2  + 2.0 * C.xxxx
  //  x3 = x0 - i3  + 3.0 * C.xxxx
  //  x4 = x0 - 1.0 + 4.0 * C.xxxx
  vec4 x1 = x0 - i1 + C.xxxx;
  vec4 x2 = x0 - i2 + C.yyyy;
  vec4 x3 = x0 - i3 + C.zzzz;
  vec4 x4 = x0 + C.wwww;

// Permutations
  i = mod289(i);
  float j0 = permute( permute( permute( permute(i.w) + i.z) + i.y) + i.x);
  vec4 j1 = permute( permute( permute( permute (
             i.w + vec4(i1.w, i2.w, i3.w, 1.0 ))
           + i.z + vec4(i1.z, i2.z, i3.z, 1.0 ))
           + i.y + vec4(i1.y, i2.y, i3.y, 1.0 ))
           + i.x + vec4(i1.x, i2.x, i3.x, 1.0 ));

// Gradients: 7x7x6 points over a cube, mapped onto a 4-cross polytope
// 7*7*6 = 294, which is close to the ring size 17*17 = 289.
  vec4 ip = vec4(1.0/294.0, 1.0/49.0, 1.0/7.0, 0.0) ;

  vec4 p0 = grad4(j0,   ip);
  vec4 p1 = grad4(j1.x, ip);
  vec4 p2 = grad4(j1.y, ip);
  vec4 p3 = grad4(j1.z, ip);
  vec4 p4 = grad4(j1.w, ip);

// Normalise gradients
  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
  p0 *= norm.x;
  p1 *= norm.y;
  p2 *= norm.z;
  p3 *= norm.w;
  p4 *= taylorInvSqrt(dot(p4,p4));

// Mix contributions from the five corners
  vec3 m0 = max(0.6 - vec3(dot(x0,x0), dot(x1,x1), dot(x2,x2)), 0.0);
  vec2 m1 = max(0.6 - vec2(dot(x3,x3), dot(x4,x4)            ), 0.0);
  m0 = m0 * m0;
  m1 = m1 * m1;
  return 49.0 * ( dot(m0*m0, vec3( dot( p0, x0 ), dot( p1, x1 ), dot( p2, x2 )))
               + dot(m1*m1, vec2( dot( p3, x3 ), dot( p4, x4 ) ) ) ) ;

  }

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

void main() {
    vec3 fragPos = getFragPos(s_diffuse_depth);
    
    vec4 color = vec4(texture2D(s_diffuse, v_texCoord));
    
    vec3 rayDir = normalize(fragPos - u_viewPos);
    vec3 invRayDir = vec3(1.0F / rayDir.x, 1.0F / rayDir.y, 1.0F / rayDir.z);
    float dst = length(fragPos - u_viewPos);
    
    //TODO Get rid of noise and use tiling noise texture instead
    
    vec3 noiseVec = vec3(fragPos.x + u_worldTime * 0.05, fragPos.y, fragPos.z + u_worldTime * 0.05);
    float yo = snoise(vec4((noiseVec + u_renderPos) * 0.1, u_worldTime * 0.01)) * 0.2;
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
    	    
    	    color = mix(color, vec4(volume.color, 0), volFog);
        }
    }
    
    gl_FragColor = color;
}


