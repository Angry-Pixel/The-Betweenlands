package thebetweenlands.client.rendering;

//Rift object
public class RiftObject {
	
		// Instance of a rift to be sent via buffer to the shader
		int texureindex;
		float u;
		float v;
		float width;
		float height;
		float opacity;	// 0.0f transperent,  1.0f fully opaque
		float ang;
			
		public RiftObject(int texureindex, float u, float v, float width, float height, float opacity, float ang) {
			this.texureindex = texureindex;
			this.u = u;
			this.v = v;
			this.width = width;
			this.height = height;
			this.opacity = opacity;
			this.ang = (float) Math.toRadians(ang);
		}
}
