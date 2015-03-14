package thebetweenlands.world.biomes.decorators.base;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;

/**
 *
 * @author The Erebus Team
 *
 */
public class BiomeDecoratorBaseBetweenlands
{
	protected World world;
	protected Random rand;
	protected int x, z;
	protected int xx, yy, zz, attempt;
	private boolean isDecorating = false;

    public final int getX() {
        return this.x;
    }

    public final int getZ() {
        return this.z;
    }

    public final Random getRNG() {
        return this.rand;
    }

    public final World getWorld() {
        return this.world;
    }

    public final void populate(World world, Random rand, int x, int z) {
        this.x = x;
        this.z = z;
        this.rand = rand;
        this.world = world;
        this.populate();
    }

    public final void decorate(World world, Random rand, int x, int z) {
        this.x = x;
        this.z = z;
        this.rand = rand;
        this.world = world;
        this.decorate();
    }

    protected void populate() {
    }

    protected void decorate() {
    }
    
	protected final int offsetXZ() {
		return rand.nextInt(16) + 8;
	}
	
	protected boolean checkSurface(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(world.getBlock(x, y - 1, z)) && world.isAirBlock(x, y, z);
	}
}
