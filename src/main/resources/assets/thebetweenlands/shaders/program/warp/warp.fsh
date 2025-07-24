#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 u_oneTexel;
uniform float u_msTime;
uniform float u_scale;
uniform float u_timeScale;
uniform float u_multiplier;
uniform float u_xOffset;
uniform float u_yOffset;
uniform float u_warpX;
uniform float u_warpY;

//Input coords
in vec2 v_texCoord;

//Output color
out vec4 o_fragColor;

vec4 mod289(vec4 x) {
	return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec4 permute(vec4 x) {
	return mod289(((x*34.0)+1.0)*x);
}

vec4 taylorInvSqrt(vec4 r) {
	return 1.79284291400159 - 0.85373472095314 * r;
}

vec2 fade(vec2 t) {
	return t*t*t*(t*(t*6.0-15.0)+10.0);
}

// Classic Perlin noise
float cnoise(vec2 P) {
	vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
	vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
	Pi = mod289(Pi); // To avoid truncation effects in permutation
	vec4 ix = Pi.xzxz;
	vec4 iy = Pi.yyww;
	vec4 fx = Pf.xzxz;
	vec4 fy = Pf.yyww;

	vec4 i = permute(permute(ix) + iy);

	vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;
	vec4 gy = abs(gx) - 0.5 ;
	vec4 tx = floor(gx + 0.5);
	gx = gx - tx;

	vec2 g00 = vec2(gx.x,gy.x);
	vec2 g10 = vec2(gx.y,gy.y);
	vec2 g01 = vec2(gx.z,gy.z);
	vec2 g11 = vec2(gx.w,gy.w);

	vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));
	g00 *= norm.x;
	g01 *= norm.y;
	g10 *= norm.z;
	g11 *= norm.w;

	float n00 = dot(g00, vec2(fx.x, fy.x));
	float n10 = dot(g10, vec2(fx.y, fy.y));
	float n01 = dot(g01, vec2(fx.z, fy.z));
	float n11 = dot(g11, vec2(fx.w, fy.w));

	vec2 fade_xy = fade(Pf.xy);
	vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
	float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
	return 2.3 * n_xy;
}

// Classic Perlin noise, periodic variant
float pnoise(vec2 P, vec2 rep) {
	vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
	vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
	Pi = mod(Pi, rep.xyxy); // To create noise with explicit period
	Pi = mod289(Pi);        // To avoid truncation effects in permutation
	vec4 ix = Pi.xzxz;
	vec4 iy = Pi.yyww;
	vec4 fx = Pf.xzxz;
	vec4 fy = Pf.yyww;

	vec4 i = permute(permute(ix) + iy);

	vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;
	vec4 gy = abs(gx) - 0.5 ;
	vec4 tx = floor(gx + 0.5);
	gx = gx - tx;

	vec2 g00 = vec2(gx.x,gy.x);
	vec2 g10 = vec2(gx.y,gy.y);
	vec2 g01 = vec2(gx.z,gy.z);
	vec2 g11 = vec2(gx.w,gy.w);

	vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));
	g00 *= norm.x;
	g01 *= norm.y;
	g10 *= norm.z;
	g11 *= norm.w;

	float n00 = dot(g00, vec2(fx.x, fy.x));
	float n10 = dot(g10, vec2(fx.y, fy.y));
	float n01 = dot(g01, vec2(fx.z, fy.z));
	float n11 = dot(g11, vec2(fx.w, fy.w));

	vec2 fade_xy = fade(Pf.xy);
	vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
	float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
	return 2.3 * n_xy;
}

float fbm(vec2 P, int octaves, float lacunarity, float gain) {
	float sum = 0.0;
	float amp = 1.0;
	vec2 pp = P;

	int i;

	for(i = 0; i < octaves; i+=1)
	{
		amp *= gain;
		//sum += amp * pnoise(pp, vec2(1.0, 1.0));
		sum += amp * cnoise(pp);
		pp *= lacunarity;
	}
	return sum;
}

float pattern(in vec2 p) {
	float l = 2.5;
	float g = 0.4;
	int oc = 10;

	vec2 q = vec2( fbm( p + vec2(0.0,0.0),oc,l,g),fbm( p + vec2(5.2,1.3),oc,l,g));
	vec2 r = vec2( fbm( p + 4.0*q + vec2(1.7,9.2),oc,l,g ), fbm( p + 4.0*q + vec2(8.3,2.8) ,oc,l,g));
	return fbm( p + 4.0*r ,oc,l,g);
}

float pattern2( in vec2 p, out vec2 q, out vec2 r , in float time) {
	float l = 2.3;
	float g = 0.4;
	int oc = 10;

	vec2 warpDir = vec2(u_warpX, u_warpY);

	q.x = fbm(p + vec2(time,time) * warpDir,oc,l,g);
	q.y = fbm(p + vec2(5.2*time,1.3*time) * warpDir,oc,l,g);

	r.x = fbm(p + 4.0*q + vec2(1.7,9.2) * warpDir,oc,l,g);
	r.y = fbm(p + 4.0*q + vec2(8.3,2.8) * warpDir,oc,l,g);

	return fbm(p + 4.0*r ,oc,l,g);
}

void main(){
	vec2 qq;
	vec2 r;
	vec2 uv = v_texCoord.xy;
	vec2 texCoord = uv * vec2(0.5,-0.5)+vec2(0.5001,0.5001);
	float noise = pattern2((texCoord + vec2(u_xOffset, u_yOffset)) * u_scale, qq, r, u_msTime * u_timeScale) * u_multiplier;
	vec4 color = vec4(1.0f,1.0f,1.0f,1.0f) * noise;
	float alpha = 1.0F;
	float centerDistance = length(uv - vec2(0.5F, 0.5F));
	float fadeStart = 0.0F;
	float fadeEnd = 0.6F;
	alpha *= 1.0F - (clamp(centerDistance, fadeStart, fadeEnd) - fadeStart) / (fadeEnd - fadeStart);
    o_fragColor = color * alpha;
    //o_fragColor = vec4(uv,0,1);
}