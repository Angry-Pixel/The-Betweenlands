#version 120

uniform sampler2D DiffuseSampler;

//DepthSampler holds the depth buffer
uniform sampler2D DepthSampler;

uniform mat4 InverseMatrix;
uniform mat4 MVP;
uniform float zNear;
uniform float zFar;
uniform vec3 CamPos;

uniform float LightSourcesX[64];
uniform float LightSourcesY[64];
uniform float LightSourcesZ[64];
uniform float LightSources;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
    //Using the texture coordinate and the depth, the original vertex in world space coordinates can be calculated
    //Allowing to perform 3D calculations in a post-processing shader
    //The depth value is linearized to make it more visible
    //float n = zNear; // camera z near
    //float f = zFar; // camera z far
    //float z = texture2D(DepthSampler, texCoord).x;
    //float depth = (2.0 * n) / (f + n - z * (f - n)); 
    //vec4 color = texture2D(DiffuseSampler, texCoord);
    //Currently the color just fades away with the distance
    //gl_FragColor = vec4(color.r / (depth * 2.0) + (0.5 - 0.5 / depth), color.gba / (depth * 2.0));
    //gl_FragColor = color / depth;
    
    float depth = texture2D(DepthSampler, texCoord).x;
    
    vec4 world = vec4(texCoord.xy * 2.0 - 1.0, depth, 1.0) * InverseMatrix;
    world.xyz /= world.w;
    
    //vec3 lightSource = vec3(4, 4, 0) - CamPos;
    
    vec4 color = texture2D(DiffuseSampler, texCoord);
    
    for(int i = 0; i < int(LightSources); i++) {
        vec3 lightPos = vec3(LightSourcesX[i], LightSourcesY[i], LightSourcesZ[i]);
        float dist = distance(lightPos, world.xyz);
        if(dist < 8) {
            vec3 addColor = (color.rgb * (1.0 - dist / 8.0)) * 5.0;
            addColor = clamp(addColor, vec3(0, 0, 0), vec3((1.0 - color.r) / 1.5, (1.0 - color.g) / 1.5, (1.0 - color.b) / 1.5));
            addColor += vec3(0.5 * (1.0 - dist / 8.0), 0.3 * (1.0 - dist / 8.0), 0.0);
            color.xyz += addColor;
        }
    }
    
    gl_FragColor = color;
    
    //float dist = distance(CamPos, world.xyz);
    
    //vec4 color = texture2D(DiffuseSampler, texCoord);
    
    //if(dist < 8) {
    //    vec3 addColor = (color.rgb * (1.0 - dist / 8.0)) * 5.0;
    //    addColor = clamp(addColor, vec3(0, 0, 0), vec3((1.0 - color.r) / 1.5, (1.0 - color.g) / 1.5, (1.0 - color.b) / 1.5));
    //    addColor += vec3(0.5 * (1.0 - dist / 8.0), 0.3 * (1.0 - dist / 8.0), 0.0);
    //    color.xyz += addColor;
    //}
    //gl_FragColor = color;
    
    //gl_FragColor = vec4(color.rgb / (dist / 8.0), color.a);
    //gl_FragColor = vec4(world.xyz, 1.0);
    
    //if(depth < 0.98) {
    //    gl_FragColor = color;
    //} else {
    //    gl_FragColor = vec4(1, 0, 0, 1);
    //}
}