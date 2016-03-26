package thebetweenlands.world.biomes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityBlindCaveFish;
import thebetweenlands.entities.mobs.EntityChiromaw;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityPeatMummy;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.utils.FogGenerator;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorMarsh;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.PatchNoiseFeature;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;
import thebetweenlands.world.biomes.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.TreeSpawnEntry;

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
		setWeight(10);
		this.setHeightAndVariation(WorldProviderBetweenlands.CAVE_START, 0);
		this.setBiomeName(name);
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(feature)
		.addFeature(new PatchNoiseFeature(0.03125D * 3.5D, 0.03125D * 3.5D, BLBlockRegistry.peat))
		.addFeature(new PatchNoiseFeature(0.03125D * 12.5D, 0.03125D * 12.5D, BLBlockRegistry.peat))
		.addFeature(new PatchNoiseFeature(0.03125D * 5.5D, 0.03125D * 5.5D, BLBlockRegistry.mud))
		.addFeature(new PatchNoiseFeature(0.03125D * 8.5D, 0.03125D * 8.5D, BLBlockRegistry.mud));
		this.waterColorMultiplier = 0x485E18;

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 20).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new TreeSpawnEntry(EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 20).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityPeatMummy.class, (short) 20).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 20).setHostile(true).setSpawnCheckRadius(30.0D).setGroupSize(1, 5));
	}

	private int[] recalculatedFogColor = new int[]{(int) 255, (int) 255, (int) 255};

	@Override
	@SideOnly(Side.CLIENT)
	public float getFogStart(float farPlaneDistance) {
		EntityLivingBase viewEntity = Minecraft.getMinecraft().renderViewEntity;
		if(viewEntity.posY <= WorldProviderBetweenlands.CAVE_START) return super.getFogStart(farPlaneDistance);
		float noise = FogGenerator.INSTANCE.getFogRange(viewEntity.posX, viewEntity.posZ, farPlaneDistance, Minecraft.getMinecraft().theWorld.getSeed())[0];
		int[] targetFogColor = this.fogColorRGB.clone();
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
		if(viewEntity.posY <= WorldProviderBetweenlands.CAVE_START) return super.getFogEnd(farPlaneDistance);
		return FogGenerator.INSTANCE.getFogRange(viewEntity.posX, viewEntity.posZ, farPlaneDistance, Minecraft.getMinecraft().theWorld.getSeed())[1];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int[] getFogRGB() {
		EntityLivingBase viewEntity = Minecraft.getMinecraft().renderViewEntity;
		if(viewEntity.posY <= WorldProviderBetweenlands.CAVE_START) return super.getFogRGB();
		return this.recalculatedFogColor;
	}
}
