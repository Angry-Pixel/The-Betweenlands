package thebetweenlands.world.biomes;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntitySludge;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.utils.FogGenerator;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorMarsh;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.PatchNoiseFeature;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeMarsh
extends BiomeGenBaseBetweenlands
{
	public BiomeMarsh(int biomeID, String name, BiomeNoiseFeature feature) {
		this(biomeID, new BiomeDecoratorMarsh(), feature, name);
	}

	public BiomeMarsh(int biomeID, BiomeDecoratorBaseBetweenlands decorator, BiomeNoiseFeature feature, String name) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT - 10, 0);
		this.setBiomeName(name);
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(feature)
		.addFeature(new PatchNoiseFeature(0.03125D * 3.5D, 0.03125D * 3.5D, BLBlockRegistry.peat))
		.addFeature(new PatchNoiseFeature(0.03125D * 12.5D, 0.03125D * 12.5D, BLBlockRegistry.peat))
		.addFeature(new PatchNoiseFeature(0.03125D * 5.5D, 0.03125D * 5.5D, BLBlockRegistry.mud))
		.addFeature(new PatchNoiseFeature(0.03125D * 8.5D, 0.03125D * 8.5D, BLBlockRegistry.mud));
		this.waterColorMultiplier = 0x184220;
		
		spawnableMonsterList.add(new SpawnListEntry(EntitySludge.class, 35, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityTarBeast.class, 15, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 2, 1, 1));
		spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));
	}

	private byte[] recalculatedFogColor = new byte[]{(byte) 255, (byte) 255, (byte) 255};
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getFogStart(float farPlaneDistance) {
		EntityLivingBase viewEntity = Minecraft.getMinecraft().renderViewEntity;
		float noise = FogGenerator.INSTANCE.getFogRange(viewEntity.posX, viewEntity.posZ, farPlaneDistance, Minecraft.getMinecraft().theWorld.getSeed())[0];
		byte[] targetFogColor = this.fogColorRGB.clone();
		float m = (float) ((50 - noise) / 5.0f);
		if(m < 0) {
			m = 0.0f;
		} else if(m > 150) {
			m = 150;
		}
		for(int i = 0; i < 3; i++) {
			int diff = 255 - targetFogColor[i];
			targetFogColor[i] = (byte) (targetFogColor[i] + (diff / 255.0D * m));
		}
		this.recalculatedFogColor = targetFogColor;
		return noise;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getFogEnd(float farPlaneDistance) {
		EntityLivingBase viewEntity = Minecraft.getMinecraft().renderViewEntity;
		return FogGenerator.INSTANCE.getFogRange(viewEntity.posX, viewEntity.posZ, farPlaneDistance, Minecraft.getMinecraft().theWorld.getSeed())[1];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public byte[] getFogRGB() {
		return this.recalculatedFogColor;
	}
}