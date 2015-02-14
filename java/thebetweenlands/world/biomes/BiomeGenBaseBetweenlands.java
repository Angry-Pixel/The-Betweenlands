package thebetweenlands.world.biomes;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author The Erebus Team
 *
 */
public class BiomeGenBaseBetweenlands extends BiomeGenBase {

	private final BiomeDecoratorBaseBetweenlands decorator;
	private int grassColor, foliageColor;
	private short[] fogColorRGB = new short[] {255, 255, 255};
	
	public BiomeGenBaseBetweenlands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID);
		this.decorator = decorator;
	}
	
	protected final BiomeGenBaseBetweenlands setColors(int grassColor, int foliageColor) {
		this.grassColor = grassColor;
		this.foliageColor = foliageColor;
		return this;
	}

	protected final BiomeGenBaseBetweenlands setFogColor(short red, short green, short blue) {
		this.fogColorRGB[0] = red;
		this.fogColorRGB[0] = green;
		this.fogColorRGB[0] = blue;
		return this;
	}
	
	@Override
	public void decorate(World world, Random rand, int x, int z) {
		this.decorator.decorate(world, rand, x, z);
    }
	
	public void populate(World world, Random rand, int x, int z) {
		this.decorator.populate(world, rand, x, z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final int getBiomeGrassColor(int x, int y, int z) {
		return this.grassColor;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final int getBiomeFoliageColor(int x, int y, int z) {
		return this.foliageColor;
	}

	@SideOnly(Side.CLIENT)
	public final short[] getFogRGB() {
		return this.fogColorRGB;
	}
}
