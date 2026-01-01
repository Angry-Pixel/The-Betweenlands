#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;

uniform float OverlayBlend;
uniform vec4 ColorModulator;
uniform vec2 ScreenSize;

in vec3 projUV;
out vec4 fragColor;

void main() {
    float mask = texture(Sampler1, projUV.xy).a;
    vec4 sky = texture(Sampler0, gl_FragCoord.xy / ScreenSize);

    vec4 overlay = mix(
        texture(Sampler2, projUV.xy),   // Night overlay
        texture(Sampler3, projUV.xy),   // Day overlay
        OverlayBlend);

    vec3 rgb = mix(sky.rgb, overlay.rgb, mask);
    float alpha = clamp(1.0f - mask + overlay.a, 0.0, 1.0);

    fragColor = vec4(rgb, alpha) * ColorModulator;
}