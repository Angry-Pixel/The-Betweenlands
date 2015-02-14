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
	
	/**
	 * Creates a new Betweenlands biome.
	 * @param biomeID int
	 * @param decorator BiomeDecoratorBaseBetweenlands
	 */
	public BiomeGenBaseBetweenlands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID);
		this.decorator = decorator;
	}
	
	/**
	 * Sets the median height of the biome in block height (0 - 128).
	 * @param height int
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setRootHeight(int height) {
		this.rootHeight = ((float)height / 128.0f) * 4.0f - 2.0f;
		return this;
	}
	
	/**
	 * Sets the biome grass and foliage colors.
	 * @param grassColor int
	 * @param foliageColor int
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setColors(int grassColor, int foliageColor) {
		this.grassColor = grassColor;
		this.foliageColor = foliageColor;
		return this;
	}

	/**
	 * Sets the biome fog color.
	 * @param red short
	 * @param green short
	 * @param blue short
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setFogColor(short red, short green, short blue) {
		this.fogColorRGB[0] = red;
		this.fogColorRGB[0] = green;
		this.fogColorRGB[0] = blue;
		return this;
	}
	
	@Override
	/**
	 * Decorates the biome, called after BiomeGenBaseBetweenlands#populate.
	 * Used to generate features such as trees.
	 */
	public void decorate(World world, Random rand, int x, int z) {
		this.decorator.decorate(world, rand, x, z);
    }
	
	/**
	 * Populates the biome, called before BiomeGenBasebetweenlands#decorate.
	 * Used to generate features such as lakes.
	 * @param world
	 * @param rand
	 * @param x
	 * @param z
	 */
	public void populate(World world, Random rand, int x, int z) {
		this.decorator.populate(world, rand, x, z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns the biome grass color.
	 * @return int
	 */
	public final int getBiomeGrassColor(int x, int y, int z) {
		return this.grassColor;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns the biome foliage color.
	 * @return int
	 */
	public final int getBiomeFoliageColor(int x, int y, int z) {
		return this.foliageColor;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Returns the fog RGB color.
	 * @return short[3]
	 */
	public final short[] getFogRGB() {
		return this.fogColorRGB;
	}
}
