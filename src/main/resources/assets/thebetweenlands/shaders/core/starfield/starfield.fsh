#version 120

// Star Nest by Pablo Román Andrioli
// This content is under the MIT License.

//#define iterations 17
#define iterations 13
#define formuparam 0.51

//#define volsteps 20
#define volsteps 5
#define stepsize 0.25

#define tile   0.850

#define brightness 0.0015
#define darkmatter 0.300
#define distfading 0.730
#define saturation 0.850

uniform float GameTime;
uniform float TimeScale;
uniform float Zoom;
uniform vec3 Offset;

void main(){
	//get coords and direction
	/*vec2 uv=fragCoord.xy/iResolution.xy-.5;
	uv.y*=iResolution.y/iResolution.x;
	vec3 dir=vec3(uv*zoom,1.);
	float time=iGlobalTime*speed+.25;*/

	vec2 uv = gl_TexCoord[0].st;
	vec3 dir = vec3(uv*Zoom, 1.0F);
	float time = GameTime * TimeScale + 0.25F;

	//rotation
	/*float a1=.5+iMouse.x/iResolution.x*2.;
	float a2=.8+iMouse.y/iResolution.y*2.;
	mat2 rot1=mat2(cos(a1),sin(a1),-sin(a1),cos(a1));
	mat2 rot2=mat2(cos(a2),sin(a2),-sin(a2),cos(a2));
	dir.xz*=rot1;
	dir.xy*=rot2;
	vec3 from=vec3(1.,.5,0.5);
	from+=vec3(time*2.,time,-2.);
	from.xz*=rot1;
	from.xy*=rot2;*/
	vec3 from = vec3(1.0F,0.5F,0.5F);
	from += vec3(time*2.0F,time,-2.0F);
	from += Offset;

	//volumetric rendering
	float s = 0.1F;
	float fade = 1.0F;
	vec3 v=vec3(0.0F);
	for (int r = 0; r < volsteps; r++) {
		vec3 p = from + s * dir * 0.5F;
		p = abs(vec3(tile) - mod(p, vec3(tile*2.0F))); // tiling fold
		float pa, a = pa = 0.0F;
		for (int i = 0; i < iterations; i++) {
			p = abs(p) / dot(p, p) - formuparam; // the magic formula
			a += abs(length(p) - pa); // absolute sum of average change
			pa = length(p);
		}
		float dm = max(0.0F, darkmatter - a * a * 0.001F); //dark matter
		a *= a * a; // add contrast
		if (r > 6) fade *= 1.0F -dm ; // dark matter, don't render near
		//v+=vec3(dm,dm*.5,0.);
		v += fade;
		v += vec3(s, s*s, s*s*s*s) * a * brightness * fade; // coloring based on distance
		fade *= distfading; // distance fading
		s += stepsize;
	}
	v = mix(vec3(length(v)), v, saturation); //color adjust

	gl_FragColor = vec4(v * 0.01F * vec3(0.4F, 1.0F, 1.0F), 1.0F);
}