#version 120

uniform sampler2D DiffuseSampler;

//DSampler holds the depth buffer
uniform sampler2D DepthSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
    //Using the texture coordinate and the depth, the original vertex in world space coordinates can be calculated
    //Allowing to perform 3D calculations in a post-processing shader
    
    //The depth value is linearized to make it more visible
    float n = 0.01; // camera z near
    float f = 20.0; // camera z far
    float z = texture2D(DepthSampler, texCoord).x;
    float depth = (2.0 * n) / (f + n - z * (f - n)); 
    
    vec4 color = texture2D(DiffuseSampler, texCoord);
    
    //Currently the color just fades away with the distance
    gl_FragColor = vec4(color.r / (depth * 2.0) + (0.5 - 0.5 / depth), color.gba / (depth * 2.0));
}
