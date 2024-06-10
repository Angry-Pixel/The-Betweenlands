package thebetweenlands.client.rendering.shader;

import java.io.IOException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.client.rendering.vertex.BetweenlandsVertexFormats;

@OnlyIn(Dist.CLIENT)
public class BetweenlandsShaders {

	public static BetweenlandsSkyShaderInstance BetweenlandsSky;
	
	// Load shaders
	@SuppressWarnings("unchecked")
	public static void preloadShaders(ResourceManager resourcemanager) {
		
		
		// Try to set uniformMap to public
		
		// Reflect to uniformMap to add new uniforms
		/*Field reflection;
		try {
			reflection = ShaderInstance.class.getDeclaredField("uniformMap");
			reflection.setAccessible(true);
			try {
				((Map<String, Uniform>)reflection.get(reflection)).put("BetweenlandsFogColor", new Uniform("BetweenlandsFogColor", 4, 4, BetweenlandsSky));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		// Load shaders
		try {
			BetweenlandsSky = new BetweenlandsSkyShaderInstance(resourcemanager, new ResourceLocation(TheBetweenlands.ID, "betweenlandssky"), BetweenlandsVertexFormats.BETWEENLANDS_SKY);
		}
		// Thow exeption
		catch (IOException ioexception) {
			TheBetweenlands.LOGGER.warn("failed to preload shaders");
			throw new RuntimeException("could not preload shaders", ioexception);
		}
	}
}
