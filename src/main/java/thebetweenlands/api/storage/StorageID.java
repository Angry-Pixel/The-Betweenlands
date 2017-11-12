package thebetweenlands.api.storage;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public class StorageID {
	private final String strID;

	protected StorageID(String strID) {
		this.strID = strID;
	}

	public static StorageID fromString(String id) {
		return new StorageID(id);
	}
	
	public final NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("id", this.strID);
		return nbt;
	}

	@Nullable
	public static StorageID readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("id", Constants.NBT.TAG_STRING)) {
			return new StorageID(nbt.getString("id"));
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
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.strID == null) ? 0 : this.strID.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
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
