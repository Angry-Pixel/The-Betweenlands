package thebetweenlands.api.storage;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class LocalStorageMetadata {
	private final StorageID storageId;

	@Nullable
	private final LocalRegion region;

	private final ResourceLocation registryId;

	@Nullable
	private final NBTTagCompound nbt;

	public LocalStorageMetadata(LocalStorageReference reference, ResourceLocation registryId) {
		this(reference, registryId, null);
	}

	public LocalStorageMetadata(LocalStorageReference reference, ResourceLocation registryId, @Nullable NBTTagCompound nbt) {
		this(reference.getID(), reference.getRegion(), registryId, nbt);
	}

	public LocalStorageMetadata(StorageID storageId, @Nullable LocalRegion region, ResourceLocation registryId, @Nullable NBTTagCompound nbt) {
		this.storageId = storageId;
		this.region = region;
		this.registryId = registryId;
		this.nbt = nbt;
	}

	public ResourceLocation getRegistryId() {
		return this.registryId;
	}

	public StorageID getStorageId() {
		return this.storageId;
	}

	@Nullable
	public LocalRegion getRegion() {
		return this.region;
	}

	public NBTTagCompound getMetadata() {
		return this.nbt != null ? this.nbt : new NBTTagCompound();
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		this.storageId.writeToNBT(nbt);
		if(this.region != null) {
			nbt.setTag("region", this.region.writeToNBT(new NBTTagCompound()));
		}
		nbt.setString("type", this.registryId.toString());
		if(this.nbt != null) {
			nbt.setTag("data", this.nbt);
		}
		return nbt;
	}

	public static LocalStorageMetadata readFromNBT(NBTTagCompound nbt) {
		LocalRegion region = null;
		if(nbt.hasKey("region")) {
			region = LocalRegion.readFromNBT(nbt.getCompoundTag("region"));
		}
		return new LocalStorageMetadata(StorageID.readFromNBT(nbt), region, new ResourceLocation(nbt.getString("type")), nbt.getCompoundTag("data"));
	}
}
