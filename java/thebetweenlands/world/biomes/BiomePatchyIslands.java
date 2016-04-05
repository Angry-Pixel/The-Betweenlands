package thebetweenlands.world.biomes;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityBlindCaveFish;
import thebetweenlands.entities.mobs.EntityBloodSnail;
import thebetweenlands.entities.mobs.EntityChiromaw;
import thebetweenlands.entities.mobs.EntityDragonFly;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityGecko;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntityLurker;
import thebetweenlands.entities.mobs.EntityMireSnail;
import thebetweenlands.entities.mobs.EntitySiltCrab;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorPatchyIslands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.PatchyIslandNoiseFeature;
import thebetweenlands.world.biomes.feature.SiltNoiseFeature;
import thebetweenlands.world.biomes.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.TreeSpawnEntry;

public class BiomePatchyIslands
extends BiomeGenBaseBetweenlands
{
	public BiomePatchyIslands(int biomeID) {
		this(biomeID, new BiomeDecoratorPatchyIslands());
	}

	public BiomePatchyIslands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		setWeight(20);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 3);
		this.setBiomeName("patchyIslands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(new PatchyIslandNoiseFeature())
		.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature());
		this.waterColorMultiplier = 0x184220;

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityDragonFly.class, (short) 35).setGroupSize(1, 2).setSpawnCheckRadius(32.0D).setSpawningInterval(400));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 20).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityMireSnail.class, (short) 60).setGroupSize(1, 5).setSpawnCheckRadius(32.0D).setSpawningInterval(800));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityGecko.class, (short) 40).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(600));
		this.blSpawnEntries.add(new TreeSpawnEntry(EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLurker.class, (short) 35).setHostile(true).setSpawnCheckRadius(16.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityAngler.class, (short) 45).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntitySwampHag.class, (short) 30).setHostile(true));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntitySwampHag.class, (short) 140).setHostile(true).setSpawnCheckRadius(6.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityWight.class, (short) 12).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntitySiltCrab.class, (short) 50).setHostile(true).setGroupSize(2, 8));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityBloodSnail.class, (short) 30).setHostile(true));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLeech.class, (short) 35).setHostile(true));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityChiromaw.class, (short) 2).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 30).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
	
		BiomeDictionary.registerBiomeType(this, Type.SWAMP, Type.WET, Type.WATER);
	}
}
