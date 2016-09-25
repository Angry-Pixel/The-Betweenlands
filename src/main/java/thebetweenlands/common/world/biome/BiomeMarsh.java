package thebetweenlands.common.world.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.TreeSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorMarsh;
import thebetweenlands.common.world.gen.biome.feature.Marsh1Feature;
import thebetweenlands.common.world.gen.biome.feature.Marsh2Feature;
import thebetweenlands.common.world.gen.biome.feature.PatchFeature;
import thebetweenlands.util.FogGenerator;

public class BiomeMarsh extends BiomeBetweenlands {

	public BiomeMarsh(int type) {
		super(new BiomeProperties("marsh_" + type).setBaseHeight(119F).setHeightVariation(1.1F).setWaterColor(0x485E18).setTemperature(0.8F).setRainfall(0.9F));
		//this.setWeight(10);
		this.setFogColor(10, 30, 12);
		this.getBiomeGenerator()
		.addFeature(type == 0 ? new Marsh1Feature() : new Marsh2Feature())
		.addFeature(new PatchFeature(0.03125D * 3.5D, 0.03125D * 3.5D, BlockRegistry.PEAT.getDefaultState()))
		.addFeature(new PatchFeature(0.03125D * 12.5D, 0.03125D * 12.5D, BlockRegistry.PEAT.getDefaultState()))
		.addFeature(new PatchFeature(0.03125D * 5.5D, 0.03125D * 5.5D, BlockRegistry.MUD.getDefaultState()))
		.addFeature(new PatchFeature(0.03125D * 8.5D, 0.03125D * 8.5D, BlockRegistry.MUD.getDefaultState()))
		.setDecorator(new BiomeDecoratorMarsh());
		this.setFoliageColors(0x627017, 0x627017);
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 20).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new TreeSpawnEntry(EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityWight.class, (short) 4).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 14).setHostile(true).setSpawnCheckRadius(64.0D));
//		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityPeatMummy.class, (short) 12).setHostile(true).setSpawnCheckRadius(64.0D)); TODO
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityChiromaw.class, (short) 12).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 54).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntitySwampHag.class, (short) 140).setHostile(true).setSpawnCheckRadius(6.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityAngler.class, (short) 35).setHostile(true).setGroupSize(1, 3));
//		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityGasCloud.class, (short) 8).setHostile(true).setSpawnCheckRadius(40.0D).setGroupSize(1, 3));
	}

	private float fogRangeInterpolateStart = 0.0F;
	private float fogRangeInterpolateEnd = 0.0F;

	@Override
	public void updateFog() {
		float[] range = FogGenerator.getFogRange(0.0F, 1.0F, Minecraft.getMinecraft().theWorld.getSeed());
		this.fogRangeInterpolateStart = range[0];
		this.fogRangeInterpolateEnd = range[1];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getFogStart(float farPlaneDistance, int mode) {
		float fogStart = Math.min(10, super.getFogStart(farPlaneDistance, mode));

		Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewEntity.posY <= WorldProviderBetweenlands.CAVE_START) 
			return fogStart;

		float fogEnd = super.getFogEnd(farPlaneDistance, mode);

		return fogStart + (fogEnd - fogStart) * this.fogRangeInterpolateStart;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getFogEnd(float farPlaneDistance, int mode) {
		float fogEnd = super.getFogEnd(farPlaneDistance, mode);

		Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewEntity.posY <= WorldProviderBetweenlands.CAVE_START) 
			return fogEnd;

		float fogStart = Math.min(10, super.getFogStart(farPlaneDistance, mode));

		return fogStart + (fogEnd - fogStart) * this.fogRangeInterpolateEnd;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int[] getFogRGB() {
		Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewEntity.posY <= WorldProviderBetweenlands.CAVE_START) 
			return super.getFogRGB();

		int[] targetFogColor = super.getFogRGB().clone();
		float m = 150.0F - this.fogRangeInterpolateEnd * 150.0F;

		if(m < 0) {
			m = 0.0f;
		} else if(m > 150) {
			m = 150;
		}
		for(int i = 0; i < 3; i++) {
			int diff = 255 - targetFogColor[i];
			targetFogColor[i] = (int) (targetFogColor[i] + (diff / 255.0D * m));
		}

		return targetFogColor;
	}
}
