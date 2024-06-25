package thebetweenlands.client.renderer.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.function.Consumer;

// Adds some uniform types that shader can use
public class BetweenlandsSkyShaderInstance extends ShaderInstance {

	public final Uniform BETWEENLANDS_TIME;
	public final Uniform BETWEENLANDS_TIME_FRACTION;
	// Sky Shader's fog
	public final Uniform LOWER_FOG_COLOR;                // Lower fog color
	public final Uniform LOWER_FOG_APERTURE;            // The uv distance away from the top of the dome that lower fog extends to
	public final Uniform LOWER_FOG_RANGE;                // The uv range of fog blending (lowerApeture - lowerRange = bottom, lowerApeture + lowerRange = top)
	public final Uniform UPPER_FOG_COLOR;                // Upper fog color
	public final Uniform UPPER_FOG_APERTURE;            // same as above, howerver is rendered under lower fog but above starfield
	public final Uniform UPPER_FOG_RANGE;                // ^ ^ ^
	public final Uniform FOG_ROTATION;                    // tells the fog shader how far to tranalate the texture
	// Event
	// Note:	Auroras are handeled in CloudHandeler to be rendered over level geometry
	public final Uniform DENSE_FOG;                // 0 = use texture as mask on fog, 1 = draw texture over fog
	public final Uniform BLOOD_SKY;                // enables blood sky's godrays
	public final Uniform SPOOK;
	// Rifts
	public final Uniform DAY_LIGHT;                // 1 = day overlay, 0 = night overlay
	// Other rift vales are handeled individualy in SkyShaderHandeler and shader
	// Settings and ect
	public final Uniform DEBUG_MODE;            // non 0 values = true
	public final Uniform RESOLUTION;            // the resolution of the viewport
	public final Uniform ROTATION;
	public final Uniform FOV;
	public final Uniform shadertest;

	public static final Consumer<ShaderInstance> onLoad = new BetweenlandsShaderInstanceConsumer();

	public BetweenlandsSkyShaderInstance(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_) throws IOException {
		super(p_173336_, shaderLocation, p_173338_);
		// TODO: Set the shader time factor in seconds (there is GameTime however it jumps and stutters alot)
		this.BETWEENLANDS_TIME = this.getUniform("TBTime");
		// Fraction of the current second
		this.BETWEENLANDS_TIME_FRACTION = this.getUniform("TBTimeF");

		// replace fog in shader
		this.LOWER_FOG_COLOR = this.getUniform("LowerFogColor");
		this.LOWER_FOG_RANGE = this.getUniform("LowerFogRange");
		this.LOWER_FOG_APERTURE = this.getUniform("LowerFogApeture");
		this.UPPER_FOG_COLOR = this.getUniform("UpperFogColor");
		this.UPPER_FOG_RANGE = this.getUniform("UpperFogRange");
		this.UPPER_FOG_APERTURE = this.getUniform("UpperFogApeture");
		this.FOG_ROTATION = this.getUniform("FogRotation");

		// Events
		this.DENSE_FOG = this.getUniform("DenseFog");
		this.BLOOD_SKY = this.getUniform("BloodSky");
		this.SPOOK = this.getUniform("Spook");

		// Rifts
		this.DAY_LIGHT = this.getUniform("DayLight");

		// Debug
		this.DEBUG_MODE = this.getUniform("DebugMode");
		this.RESOLUTION = this.getUniform("Resolution");
		this.ROTATION = this.getUniform("Rotation");
		this.FOV = this.getUniform("FOV");
		this.shadertest = this.getUniform("shadertest");
	}

	/*
	public void invokeUniformFetch() {
		Method m;
		try {
			m = this.getClass().getSuperclass().getDeclaredMethod("updateLocations");
			m.setAccessible(true);
			try {
				m.invoke(this);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}*/
}
