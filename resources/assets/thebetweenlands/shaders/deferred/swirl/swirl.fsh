#version 120

uniform sampler2D s_diffuse;

// Swirl effect parameters
uniform float u_angle;
uniform float radius = 0.8;
uniform vec2 center = vec2(0.5, 0.5);
uniform float rt_w = 1.0F; 
uniform float rt_h = 1.0F; 

vec4 swirlEffect(sampler2D tex, vec2 uv)
{
  vec2 texSize = vec2(rt_w, rt_h);
  vec2 tc = uv * texSize;
  tc -= center;
  float dist = length(tc);
  if (dist < radius) 
  {
    float percent = (radius - dist) / radius;
    float theta = percent * percent * u_angle * 8.0;
    float s = sin(theta);
    float c = cos(theta);
    tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
  }
  tc += center;
  vec3 color = texture2D(tex, tc / texSize).rgb;
  return vec4(color, 1.0);
}

void main(){
	vec2 texCoord = gl_TexCoord[0].st;
	gl_FragColor = swirlEffect(s_diffuse, texCoord);
}