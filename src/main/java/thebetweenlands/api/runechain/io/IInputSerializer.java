package thebetweenlands.api.runechain.io;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;
import thebetweenlands.api.runechain.IRuneChainUser;

public interface IInputSerializer<T> {
	public void write(T obj, PacketBuffer buffer);

	public T read(IRuneChainUser user, PacketBuffer buffer) throws IOException;
}