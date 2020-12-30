package thebetweenlands.api.runechain.chain;

import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ITickable;
import thebetweenlands.api.runechain.IAspectBuffer;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.INodeComposition;

public interface IRuneChain extends INodeComposition<IRuneExecutionContext>, ITickable {
	/**
	 * Sets the aspect buffer that provides the runes with aspect. Must be set before calling
	 * {@link #run(IRuneChainUser)}.
	 * @param buffer the aspect buffer that provides the runes with aspect
	 */
	public void setAspectBuffer(IAspectBuffer buffer);

	/**
	 * Returns the aspect buffer that provides the runes with aspect. Must not be null while the rune chain
	 * is running, i.e. when {@link #isRunning()} is true.
	 * @return the aspect buffer that provides the runes with aspect
	 */
	@Nullable
	public IAspectBuffer getAspectBuffer();

	/**
	 * Starts the execution of this rune chain. Requires an aspect buffer
	 * before running, see {@link #setAspectBuffer(IAspectBuffer)}!
	 * @param user the rune chain user that is executing this rune chain
	 */
	public void run(IRuneChainUser user);

	/**
	 * Updates this rune chain. Must only be called when {@link #isRunning()} is true.
	 */
	@Override
	public void update();

	/**
	 * Updates the rune effect modifiers. This is separate from {@link #update()} such that it can also be
	 * updated on the client side, since only rune effect modifiers and not the entire rune chain state is synced
	 * to clients.
	 */
	public void updateRuneEffectModifiers();

	/**
	 * Processes a packet that was sent through {@link IRuneChainUser#sendPacket(IRuneChain, java.util.function.Consumer, net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint).
	 * This method must be called on main-thread!
	 * @see IRuneChainUser#sendPacket(IRuneChain, java.util.function.Consumer, net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint)
	 * @param user rune chain user that the packet was sent through
	 * @param buffer packet buffer to deserialize and process
	 * @throws IOException thrown if the deserialization has failed
	 */
	public void processPacket(IRuneChainUser user, PacketBuffer buffer) throws IOException;
}
