package thebetweenlands.api.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.registries.AspectRegistry;

/**
 * The aspect container contains dynamic and static aspects.
 * Static aspects are only saved when their amount changes, so that they
 * can be updated in case the aspect distribution seed is changed. Once
 * the amount of a static aspect in this container has changed it is no 
 * longer treated as a static aspect.
 */
public class AspectContainer {
	protected static final class Storage {
		private final IAspectType type;
		private final AspectContainer container;
		private int dynamicAmount;
		private int storedStaticAmount;
		private boolean hasStoredStaticAmount;

		private Storage(IAspectType type, AspectContainer container) {
			this.type = type;
			this.dynamicAmount = 0;
			this.storedStaticAmount = 0;
			this.hasStoredStaticAmount = false;
			this.container = container;
		}

		/**
		 * Writes the storage data to the specified NBT
		 * @param nbt
		 * @return
		 */
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setInteger("dynamic", this.dynamicAmount);
			nbt.setInteger("storedStatic", this.storedStaticAmount);
			nbt.setBoolean("hasStoredStatic", this.hasStoredStaticAmount);
			return nbt;
		}

		/**
		 * Reads the storage data from the specified NBT
		 * @param nbt
		 * @return
		 */
		public Storage readFromNBT(NBTTagCompound nbt) {
			this.dynamicAmount = nbt.getInteger("dynamic");
			this.storedStaticAmount = nbt.getInteger("storedStatic");
			this.hasStoredStaticAmount = nbt.getBoolean("hasStoredStatic");
			return this;
		}

		/**
		 * Returns the amount
		 * @param dynamic
		 * @param staticAspects
		 * @return
		 */
		public int getAmount(boolean dynamic) {
			if(dynamic) {
				return this.dynamicAmount;
			} else {
				if(this.hasStoredStaticAmount) {
					//Return stored static amount if it has been changed previously
					return this.storedStaticAmount;
				} else {
					List<Aspect> staticAspects = this.container.getStaticAspects();
					if(staticAspects != null) {
						int amount = 0;
						for(Aspect aspect : staticAspects) {
							if(aspect.type == this.type) {
								amount += aspect.amount;
							}
						}
						return amount;
					} else {
						return 0;
					}
				}
			}
		}

		/**
		 * Sets the amount
		 * @param dynamic
		 * @param amount
		 * @return
		 */
		public Storage setAmount(boolean dynamic, int amount) {
			if(dynamic) {
				this.dynamicAmount = amount;
			} else {
				this.storedStaticAmount = amount;
				this.hasStoredStaticAmount = true;
			}
			return this;
		}

		/**
		 * Resets the static amount to the default
		 * @return
		 */
		public Storage resetStaticAmount() {
			this.storedStaticAmount = 0;
			this.hasStoredStaticAmount = false;
			return this;
		}
	}

	private final Map<IAspectType, Storage> storage = new HashMap<IAspectType, Storage>();

	/**
	 * Returns the storage for the specified aspect
	 * @param type
	 * @return
	 */
	protected final Storage getStorage(IAspectType type) {
		Storage storage = this.storage.get(type);
		if(storage == null) {
			this.storage.put(type, storage = new Storage(type, this));
		}
		return storage;
	}

	public AspectContainer() {
		for(IAspectType type : AspectRegistry.ASPECT_TYPES) {
			this.storage.put(type, new Storage(type, this));
		}
	}

	/**
	 * Called when the data was changed
	 */
	protected void onChanged() { }

	/**
	 * Returns the set of all stored aspect types
	 * @return
	 */
	protected final ImmutableSet<IAspectType> getStoredAspectTypes() {
		return ImmutableSet.copyOf(this.storage.keySet());
	}

	/**
	 * Returns the amount of the specified aspect type
	 * @param type
	 * @return
	 */
	protected final int get(IAspectType type, boolean dynamic) {
		return this.getStorage(type).getAmount(dynamic);
	}
	
	/**
	 * Returns the amount of the specified aspect type
	 * @param type
	 * @return
	 */
	public final int get(IAspectType type) {
		Storage storage = this.getStorage(type);
		return storage.getAmount(true) + storage.getAmount(false);
	}

	/**
	 * Returns the {@link Aspect} object with the specified aspect type
	 * @param type
	 * @return
	 */
	public final Aspect getAspect(IAspectType type)  {
		return new Aspect(type, this.get(type));
	}

	/**
	 * Sets the amount of the specified aspect type
	 * @param type
	 * @param amount
	 * @return
	 */
	public final AspectContainer set(IAspectType type, int amount) {
		if(amount >= 0) {
			Storage storage = this.getStorage(type);
			int diff = this.get(type) - amount;
			if(diff > 0) {
				//Remove
				int dynAmounts = storage.getAmount(true);
				storage.setAmount(true, Math.max(dynAmounts - diff, 0));
				diff = Math.max(diff - dynAmounts, 0);
				if(diff > 0) {
					//Drain static aspect last and only if necessary
					storage.setAmount(false, storage.getAmount(false) - diff);
				}
				this.onChanged();
			} else if(diff < 0) {
				//Add
				storage.setAmount(true, amount - storage.getAmount(false));
				this.onChanged();
			}
		}
		return this;
	}

	/**
	 * Adds the amount of the specified aspect type
	 * @param type
	 * @param amount
	 * @return
	 */
	public final AspectContainer add(IAspectType type, int amount) {
		Storage storage = this.getStorage(type);

		storage.dynamicAmount += amount;

		if(amount != 0) {
			this.onChanged();
		}

		return this;
	}

	/**
	 * Drains an amount from the specified aspect type
	 * @param type
	 * @param desiredAmount
	 * @return the drained amount
	 */
	public final int drain(IAspectType type, int desiredAmount) {
		int amount = this.get(type);
		if(desiredAmount >= amount) {
			this.set(type, 0);
			return amount;
		}
		this.set(type, amount - desiredAmount);
		return desiredAmount;
	}

	/**
	 * Returns whether this container is empty
	 * @return
	 */
	public final boolean isEmpty() {
		for(Storage storage : this.storage.values()) {
			if(storage.getAmount(true) + storage.getAmount(false) > 0)
				return false;
		}
		return true;
	}

	/**
	 * Writes this container to the specified NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound save(NBTTagCompound nbt) {
		NBTTagList typesList = new NBTTagList();
		for(Entry<IAspectType, Storage> entry : this.storage.entrySet()) {
			IAspectType type = entry.getKey();
			Storage storage = entry.getValue();
			
			if(storage.dynamicAmount == 0 && !storage.hasStoredStaticAmount) {
				//Doesn't have to be saved
				continue;
			}
			
			NBTTagCompound storageNbt = new NBTTagCompound();
			storageNbt.setTag("aspect", type.writeToNBT(new NBTTagCompound()));
			storageNbt.setTag("storage", storage.writeToNBT(new NBTTagCompound()));

			typesList.appendTag(storageNbt);
		}
		nbt.setTag("container", typesList);
		return nbt;
	}

	/**
	 * Reads the container from the specified NBT and adds the static aspects
	 * @param nbt
	 * @param staticAspects
	 * @return
	 */
	public AspectContainer read(NBTTagCompound nbt) {
		NBTTagList typesList = nbt.getTagList("container", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < typesList.tagCount(); i++) {
			NBTTagCompound storageNbt = typesList.getCompoundTagAt(i);
			IAspectType type = IAspectType.readFromNBT(storageNbt.getCompoundTag("aspect"));
			if(type == null)
				continue;
			Storage storage = this.getStorage(type);
			storage.readFromNBT(storageNbt.getCompoundTag("storage"));
		}
		return this;
	}

	/**
	 * Returns a list of static aspects of this container
	 * @return
	 */
	@Nonnull
	protected List<Aspect> getStaticAspects() {
		return ImmutableList.of();
	}

	/**
	 * Returns a list of all aspects in this container
	 * @return
	 */
	@Nonnull
	public List<Aspect> getAspects() {
		List<Aspect> aspects = new ArrayList<Aspect>();
		Set<IAspectType> types = this.getStoredAspectTypes();
		for(IAspectType type : types) {
			int amount = this.get(type);
			if(amount > 0)
				aspects.add(new Aspect(type, amount));
		}
		return aspects;
	}
}
