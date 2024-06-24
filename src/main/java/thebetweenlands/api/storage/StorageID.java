package thebetweenlands.api.storage;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StorageID {

	public static final StreamCodec<ByteBuf, StorageID> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, o -> o.strID, StorageID::new);

	private final String strID;

	protected StorageID(String strID) {
		this.strID = strID;
	}

	public static StorageID fromString(String id) {
		return new StorageID(id);
	}

	public final CompoundTag writeToNBT(CompoundTag tag) {
		tag.putString("id", this.strID);
		return tag;
	}

	@Nullable
	public static StorageID readFromNBT(CompoundTag tag) {
		if(tag.contains("id", Tag.TAG_STRING)) {
			return new StorageID(tag.getString("id"));
		}
		return null;
	}

	public final String getStringID() {
		return this.strID;
	}

	@Override
	public final String toString() {
		return this.strID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.strID == null) ? 0 : this.strID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		StorageID other = (StorageID) obj;
		String id = this.strID;
		String otherId = other.strID;
		if (id == null) {
			if (otherId != null)
				return false;
		} else if (!id.equals(otherId))
			return false;
		return true;
	}
}
