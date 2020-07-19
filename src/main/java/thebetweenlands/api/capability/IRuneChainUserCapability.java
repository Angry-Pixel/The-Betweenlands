package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.RuneChainComposition;

public interface IRuneChainUserCapability extends ITickable {
	/**
	 * Returns the rune chain user instance of the thing this capability is attached to
	 * @return Rune chain user instance of the thing this capability is attached to
	 */
	public IRuneChainUser getUser();

	/**
	 * Adds a rune chain instance to this capability and assigns it a unique ID
	 * @param chain Data to create the rune chain from
	 * @return Unique ID assigned to the rune chain instance
	 */
	public int addRuneChain(IRuneChainData data);

	/**
	 * Adds a rune chain instance synced over the network to this capability
	 * @param chain Rune chain instance to add to this capability
	 * @param id Unique ID to be assigned to the rune chain instance
	 */
	@SideOnly(Side.CLIENT)
	public void addRuneChain(RuneChainComposition chain, int id);
	
	/**
	 * Sets whether the rune chain should automatically be updated by the capability holder
	 * @param id ID that was assigned to the rune chain instance
	 * @param updating Whether the rune chain should automatically be updated
	 * @param removeOnFinish Whether the rune chain should automatically be removed from this capability once {@link RuneChainComposition#isRunning()} returns false. Only works if
	 * <code>updating</code> is true. The rune chain must already be running otherwise it will be removed immediately in the first update.
	 * @return True if <code>updating</code> was successfully set, false otherwise (i.e. when no rune chain instance exists with the specified ID)
	 */
	public boolean setUpdating(int id, boolean updating, boolean removeOnFinish);

	/**
	 * Updates all updating rune chains in this capability
	 */
	@Override
	public void update();

	/**
	 * Removes the rune chain instance with the specified ID
	 * @param id ID that was assigned to the rune chain instance that is to be removed
	 * @return Rune chain instance that was removed, or null if this capability did not
	 * contain a rune chain instance with the specified ID
	 */
	@Nullable
	public RuneChainComposition removeRuneChain(int id);

	/**
	 * Returns the rune chain instance with the specified ID
	 * @param id ID that was assigned to the rune chain instance
	 * @return Rune chain instance with the specified ID, or null if this capability does
	 * not contain a rune chain instance with the specified ID
	 */
	@Nullable
	public RuneChainComposition getRuneChain(int id);
}
