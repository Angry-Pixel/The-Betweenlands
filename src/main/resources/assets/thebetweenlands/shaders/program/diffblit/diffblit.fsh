#version 150

uniform sampler2D beforeTarget;
uniform sampler2D afterTarget;
uniform sampler2D DiffuseSampler;

//Input coords
in vec2 v_texCoord;

//Output color
out vec4 o_fragColor;

void main(){
    vec4 before     = texture(beforeTarget, v_texCoord);
    vec4 after      = texture(afterTarget, v_texCoord);
    vec4 base       = texture(DiffuseSampler, v_texCoord);
    float epsilon   = 1e-5;
    float changed   = step(epsilon, length(after.rgb - before.rgb));
    o_fragColor     = mix(base, after, changed);
}