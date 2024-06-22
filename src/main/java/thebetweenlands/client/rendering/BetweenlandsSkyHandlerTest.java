package thebetweenlands.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;
import thebetweenlands.common.TheBetweenlands;

@OnlyIn(Dist.CLIENT)
public class BetweenlandsSkyHandlerTest implements ISkyRenderHandler {

	// Renders horison texture, sky texture, blobs in sky and clouds
	public ResourceLocation SkyTexture = new ResourceLocation(TheBetweenlands.ID, "textures/sky/sky_texture.png");
	public ResourceLocation FogTexture = new ResourceLocation(TheBetweenlands.ID, "textures/sky/fog_texture.png");
	public BetweenlandsSkyRenderer skyrenderer = new BetweenlandsSkyRenderer();
	float ang = 0;

	public ISkyRenderHandler compositeSky;

	boolean test = true;

	// Betweenlands sky without a composite sky
	public BetweenlandsSkyHandlerTest() {
	}

	// Adds betweenlands sky renderer to a sky handeler
	public BetweenlandsSkyHandlerTest(ISkyRenderHandler compositeSky) {
		this.compositeSky = compositeSky;
	}


	public float UValueGet() {
		return 0;
	}

	public float VValueGet() {
		return 0;
	}

	@Override
	public void render(int ticks, float partialTick, PoseStack poseStack, ClientLevel level, Minecraft minecraft) {
		if (compositeSky != null) {
			compositeSky.render(ticks, partialTick, poseStack, level, minecraft);
		}

	}

}
