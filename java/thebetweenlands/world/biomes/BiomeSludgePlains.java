package thebetweenlands.world.biomes;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.*;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorSludgePlains;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.SludgePlainsNoiseFeature;

/**
 * Created by Bart on 9-10-2015.
 */
public class BiomeSludgePlains extends BiomeGenBaseBetweenlands {

	public BiomeSludgePlains(int biomeID) {
		this(biomeID, new BiomeDecoratorSludgePlains());
	}

	public BiomeSludgePlains(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte) 10, (byte) 30, (byte) 12);
		setColors(0x314D31, 0x314D31);
		setWeight(20);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT + 8, 2);
		this.setBiomeName("Sludge Plains");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.mud, BLBlockRegistry.mud, BLBlockRegistry.swampDirt, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte) 1);
		this.addFeature(new SludgePlainsNoiseFeature());
		this.waterColorMultiplier = 0x002f06;

		spawnableCreatureList.add(new SpawnListEntry(EntityFirefly.class, 25, 1, 3));
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 5, -1, -1));
		spawnableMonsterList.add(new SpawnListEntry(EntitySludge.class, 40, 1, 1));
		spawnableCaveCreatureList.add(new SpawnListEntry(EntityTarBeast.class, 300, 1, 1));
	}
}
