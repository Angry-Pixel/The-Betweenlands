package thebetweenlands.common.component;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ISyncedAttachment<T extends ISyncedAttachment<T>> {

	StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

}