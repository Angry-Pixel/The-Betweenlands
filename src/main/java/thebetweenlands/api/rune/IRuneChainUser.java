package thebetweenlands.api.rune;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.IAspectType;

public interface IRuneChainUser {
	/**
	 * Returns the world the rune chain was activated in
	 * @return the world the rune chain was activated in
	 */
	public World getWorld();

	/**
	 * Returns the position of the thing that activated
	 * the rune chain
	 * @return the position of the thing that activated the
	 * rune chain
	 */
	public Vec3d getPosition();
	
	/**
	 * Returns the eye position of the thing that activated the rune chain
	 * @return the eye position of the thing that activated the
	 * rune chain
	 */
	public Vec3d getEyesPosition();

	/**
	 * Returns the look vector of the thing that activated
	 * the rune chain
	 * @return the look vector of the thing that activated the
	 * rune chain
	 */
	public Vec3d getLook();
	
	/**
	 * Returns the inventory of the thing that activated
	 * the rune chain
	 * @return the inventory of the thing that activated the
	 * rune chain
	 */
	@Nullable
	public IInventory getInventory();

	/**
	 * Returns the entity that activated the rune chain
	 * @return the entity that activated the rune chain
	 */
	@Nullable
	public Entity getEntity();
	
	/**
	 * Returns whether the rune chain user is currently "using"
	 * the rune chain (e.g. player holding right-click with a rune chain item)
	 * @return Whether the rune chain user is currently "using"
	 * the rune chain
	 */
	public boolean isUsingRuneChain();
}
