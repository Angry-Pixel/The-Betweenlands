package thebetweenlands.api.runechain;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.api.runechain.chain.IRuneChain;
import thebetweenlands.common.herblore.rune.RuneChainComposition;

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
	 * Returns whether this rune chain user is still active and exists
	 * @return
	 */
	public boolean isActive();
	
	/**
	 * Returns whether the rune chain user is currently activating/"using"
	 * the rune chain (e.g. player holding right-click with a rune chain item)
	 * @param runeChain rune chain to check for
	 * @return Whether the rune chain user is currently activating/"using"
	 * the rune chain
	 */
	public boolean isActivatingRuneChain(IRuneChain runeChain);

	/**
	 * Sends a packet over the network. Once received {@link IRuneChain#processPacket(IRuneChainUser, PacketBuffer)} is called with the packet's data.
	 * @see IRuneChain#processPacket(IRuneChainUser, PacketBuffer)
	 * @param runeChain rune chain that the packet is being sent from
	 * @param serializer serializer that writes the data to a packet buffer. This may be called off main-thread!
	 * @param target targets to receive this packet. If null all players tracking the rune chain's user receive the packet.
	 */
	public void sendPacket(IRuneChain runeChain, Consumer<PacketBuffer> serializer, @Nullable TargetPoint target);
}
