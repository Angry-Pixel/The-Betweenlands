package thebetweenlands.common.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.client.ISkyRenderHandler;
import net.minecraftforge.client.IWeatherParticleRenderHandler;
import net.minecraftforge.client.IWeatherRenderHandler;
import thebetweenlands.client.rendering.BetweenlandsSkyHandler;

@OnlyIn(Dist.CLIENT)
public class BetweenlandsSpecialEffects extends DimensionSpecialEffects {

	public boolean setCompositeSky = true;

	// These replace standard rendering
	public BetweenlandsWeatherParticleHandler betweenlandsweatherpartical = new BetweenlandsWeatherParticleHandler();
	public BetweenlandsWeatherHandler betweenlandsweather = new BetweenlandsWeatherHandler();
	public BetweenlandsCloudHandler betweenlandscloud = new BetweenlandsCloudHandler();
	public BetweenlandsSkyHandler betweenlandsSky = new BetweenlandsSkyHandler();

	public BetweenlandsSpecialEffects(float p_108866_, boolean p_108867_, SkyType p_108868_, boolean p_108869_,
									  boolean p_108870_) {
		super(p_108866_, p_108867_, p_108868_, p_108869_, p_108870_);
	}

	public static DimensionSpecialEffects BetweenlandsEffects() {
		return new BetweenlandsSpecialEffects(192, true, SkyType.NORMAL, false, false);
	}

	// Fog settings
	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 Color, float Light) {
		// new Vec3(0.75f, 0.75f, 0.75f)
		// Color.scale(Light);

		// overworld sky color
		return new Vec3(0.753f, 0.847f, 1.0f).scale(Light);
	}

	@Override
	public float[] getSunriseColor(float p_108872_, float p_108873_) {
		if (betweenlandsSky.compositeRender) {
			return super.getSunriseColor(p_108872_, p_108873_);
		}
		return null;
	}

	@Override
	public boolean isFoggyAt(int p_108874_, int p_108875_) {
		if (betweenlandsSky.compositeRender) {
			return false;
		}
		return true;
	}

	// Renderers
	@Override
	public ICloudRenderHandler getCloudRenderHandler() {
		//return betweenlandscloud;
		if (betweenlandsSky.compositeRender) {
			return null;
		}
		return betweenlandscloud;
	}

	@Override
	public ISkyRenderHandler getSkyRenderHandler() {

		// Update composite sky for custom shaders
		// Untested for shaders
		if (betweenlandsSky.compositeRender) {
			return null;
		}

		return betweenlandsSky;
	}

	@Override
	public IWeatherParticleRenderHandler getWeatherParticleRenderHandler() {
		return betweenlandsweatherpartical;
	}

	@Override
	public IWeatherRenderHandler getWeatherRenderHandler() {
		return betweenlandsweather;
	}
}
