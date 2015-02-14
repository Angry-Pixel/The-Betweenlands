package thebetweenlands.world.biomes.decorators;

import java.util.Random;

import net.minecraft.world.World;

/**
 * 
 * @author The Erebus Team
 *
 */
public class BiomeDecoratorBaseBetweenlands {
	private int x = 0, z = 0;
	private Random rng;
	private World world;
	
	public final int getX() {
		return this.x;
	}
	
	public final int getZ() {
		return this.z;
	}
	
	public final Random getRNG() {
		return this.rng;
	}
	
	public final World getWorld() {
		return this.world;
	}
	
	public final void populate(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rng = rand;
		this.world = world;
		this.populate();
	}
	
	public final void decorate(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rng = rand;
		this.world = world;
		this.decorate();
	}
	
	protected void populate() { }
	protected void decorate() { }
}
