#version 120

uniform sampler2D s_diffuse;

uniform float u_gamma;
uniform float u_exposure;

void main() {
    vec4 diffuse = texture2D(s_diffuse, gl_TexCoord[0].st);
  
    //Exposure
    vec3 hdr = vec3(1.0F) - exp(-diffuse.rgb * u_exposure);
    
    //Gamma
    hdr = pow(hdr, vec3(1.0F / u_gamma));
    
	gl_FragColor = vec4(hdr, diffuse.a);
}