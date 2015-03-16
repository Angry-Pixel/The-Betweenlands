package thebetweenlands.world.biomes;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.FogGenerator;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorCoarseIslands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeMarsh
extends BiomeGenBaseBetweenlands
{
	public BiomeMarsh(int biomeID, String name, BiomeNoiseFeature feature) {
		//TODO: Use different decorator
		this(biomeID, new BiomeDecoratorCoarseIslands(), feature, name);
	}
	
	public BiomeMarsh(int biomeID, BiomeDecoratorBaseBetweenlands decorator, BiomeNoiseFeature feature, String name) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT - 10, 0);
		this.setBiomeName(name);
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(feature);
		this.waterColorMultiplier = 0x184220;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getFogStart(float farPlaneDistance) {
		EntityLivingBase viewEntity = Minecraft.getMinecraft().renderViewEntity;
		Random rng = Minecraft.getMinecraft().theWorld.rand;
		return FogGenerator.INSTANCE.getFogRange(viewEntity.posX, viewEntity.posZ, farPlaneDistance, rng)[0];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getFogEnd(float farPlaneDistance) {
		EntityLivingBase viewEntity = Minecraft.getMinecraft().renderViewEntity;
		Random rng = Minecraft.getMinecraft().theWorld.rand;
		return FogGenerator.INSTANCE.getFogRange(viewEntity.posX, viewEntity.posZ, farPlaneDistance, rng)[1];
	}
}