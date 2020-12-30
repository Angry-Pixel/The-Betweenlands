package thebetweenlands.api.runechain.chain;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.api.runechain.IAspectBuffer;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.rune.AbstractRune;

public interface IRuneExecutionContext {
	/**
	 * Returns the rune chain
	 * @return the rune chain
	 */
	public IRuneChain getRuneChain();
	
	/**
	 * Returns the user that activated the rune chain
	 * @return the user that activated the rune chain
	 */
	public IRuneChainUser getUser();

	/**
	 * Returns the aspect buffer that provides the runes with aspect.
	 * @return the aspect buffer that provides the runes with aspect
	 */
	public IAspectBuffer getAspectBuffer();

	/**
	 * Returns the number of currently active branches.
	 * @return the number of currently active branches
	 */
	public int getBranchIndexCount();

	/**
	 * Returns the current active branch starting at 0.
	 * @return the current active branch starting at 0
	 */
	public int getBranchIndex();

	/**
	 * Returns the number of current input combinations.
	 * @return the number of current input combinations
	 */
	public int getInputIndexCount();

	/**
	 * Returns the current input combination index.
	 * @return the current input combination index
	 */
	public int getInputIndex();

	/**
	 * Returns the currently activating rune index.
	 * @return the currently activating rune index
	 */
	public int getRune();

	/**
	 * Sends a packet over the network. Once arrived {@link AbstractRune.Blueprint#processPacket(AbstractRune, IRuneChainUser, PacketBuffer)} is called with the packet's data.
	 * @param serializer serializer that writes the data to a packet buffer. This may be called off main-thread!
	 * @param target targets to receive this packet. If null all players tracking the rune chain's user receive the packet.
	 * @see IRuneChainUser#sendPacket(IRuneChain, Consumer, TargetPoint)
	 * @see IRuneChain#processPacket(IRuneChainUser, PacketBuffer)
	 */
	public default void sendPacket(Consumer<PacketBuffer> serializer, @Nullable TargetPoint target) {
		this.getUser().sendPacket(this.getRuneChain(), buffer -> {
			buffer.writeVarInt(this.getRune());
			serializer.accept(buffer);
		}, target);
	}
}
