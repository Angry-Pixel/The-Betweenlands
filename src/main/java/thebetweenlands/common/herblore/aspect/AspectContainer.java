package thebetweenlands.common.herblore.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.herblore.aspect.type.IAspectType;
import thebetweenlands.common.registries.AspectRegistry;

public class AspectContainer {
	public static final String ASPECTS_NBT_TAG = "blHerbloreAspects";

	protected static final class InternalAspect {
		public final IAspectType type;
		public final int amount;
		public final boolean isDynamic;
		private boolean isSaved = false;

		protected InternalAspect(IAspectType type, int amount, boolean isDynamic, boolean isSaved) {
			this.type = type;
			this.amount = amount;
			this.isDynamic = isDynamic;
			this.isSaved = isSaved;
		}

		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setString("aspect", this.type.getName());
			nbt.setInteger("amount", this.amount);
			nbt.setBoolean("dynamic", this.isDynamic);
			return nbt;
		}

		public static InternalAspect readFromNBT(NBTTagCompound nbt) {
			String aspectName = nbt.getString("aspect");
			int amount = nbt.getInteger("amount");
			boolean isDynamic = nbt.getBoolean("dynamic");
			IAspectType aspectType = AspectRegistry.getAspectTypeFromName(aspectName);
			if(aspectType != null) {
				return new InternalAspect(aspectType, amount, isDynamic, true);
			}
			return null;
		}

		public InternalAspect setSaved() {
			this.isSaved = true;
			return this;
		}

		public boolean isSaved() {
			return this.isSaved;
		}
	}

	private final Map<IAspectType, List<InternalAspect>> aspects = new HashMap<IAspectType, List<InternalAspect>>();

	private final List<InternalAspect> getEntries(IAspectType type) {
		List<InternalAspect> lst = this.aspects.get(type);
		if(lst == null)
			this.aspects.put(type, lst = new ArrayList<InternalAspect>());
		return lst;
	}

	public AspectContainer() { }

	/**
	 * Called when the data was changed
	 */
	protected void onChanged() { }

	/**
	 * Returns the set of all available aspect types
	 * @return
	 */
	protected final ImmutableSet<IAspectType> getAvailableAspectTypes() {
		return ImmutableSet.copyOf(this.aspects.keySet());
	}

	/**
	 * Adds the amount of the specified aspect type
	 * @param type
	 * @param amount
	 * @param isDynamic
	 * @return
	 */
	protected final AspectContainer addAmount(IAspectType type, int amount, boolean isDynamic) {
		List<InternalAspect> entries = this.getEntries(type);
		int dynAmounts = 0;
		int staticAmounts = 0;
		int prevStaticAmounts = this.getAmount(type, false);
		int prevDynAmounts = this.getAmount(type, true);
		for(InternalAspect aspect : entries) {
			if(!aspect.isDynamic)
				staticAmounts += aspect.amount;
			else
				dynAmounts += aspect.amount;
		}
		if(!isDynamic)
			staticAmounts += amount;
		else
			dynAmounts += amount;
		entries.clear();
		entries.add(new InternalAspect(type, staticAmounts > 0 ? staticAmounts : 0, false, true));
		entries.add(new InternalAspect(type, dynAmounts > 0 ? dynAmounts : 0, true, true));
		if(prevStaticAmounts != staticAmounts || prevDynAmounts != dynAmounts)
			this.onChanged();
		return this;
	}

	/**
	 * Removes all aspects of the specified type
	 * @param type
	 * @return
	 */
	protected final AspectContainer clearAspects(IAspectType type) {
		List<InternalAspect> entries = this.getEntries(type);
		int prevAmounts = this.getAmount(type, true) + this.getAmount(type, false);
		entries.clear();
		entries.add(new InternalAspect(type, 0, false, true));
		entries.add(new InternalAspect(type, 0, true, true));
		if(prevAmounts > 0)
			this.onChanged();
		return this;
	}

	/**
	 * Sets the amount of the specified aspect type
	 * @param type
	 * @param amount
	 * @param isDynamic
	 * @return
	 */
	protected final AspectContainer putAmount(IAspectType type, int amount, boolean isDynamic) {
		List<InternalAspect> entries = this.getEntries(type);
		int dynAmounts = 0;
		int staticAmounts = 0;
		int prevStaticAmounts = this.getAmount(type, false);
		int prevDynAmounts = this.getAmount(type, true);
		for(InternalAspect aspect : entries) {
			if(!aspect.isDynamic)
				staticAmounts += aspect.amount;
			else
				dynAmounts += aspect.amount;
		}
		if(!isDynamic)
			staticAmounts = amount;
		else
			dynAmounts = amount;
		entries.clear();
		entries.add(new InternalAspect(type, staticAmounts > 0 ? staticAmounts : 0, false, true));
		entries.add(new InternalAspect(type, dynAmounts > 0 ? dynAmounts : 0, true, true));
		if(prevStaticAmounts != staticAmounts || prevDynAmounts != dynAmounts)
			this.onChanged();
		return this;
	}

	/**
	 * Returns the amount of the specified aspect type
	 * @param type
	 * @param isDynamic
	 * @return
	 */
	protected final int getAmount(IAspectType type, boolean isDynamic) {
		List<InternalAspect> entries = this.getEntries(type);
		int dynAmounts = 0;
		int staticAmounts = 0;
		for(InternalAspect aspect : entries) {
			if(!aspect.isDynamic)
				staticAmounts += aspect.amount;
			else
				dynAmounts += aspect.amount;
		}
		if(!isDynamic)
			return staticAmounts;
		else
			return dynAmounts;
	}

	/**
	 * Clears this container
	 */
	public final AspectContainer clear() {
		Set<IAspectType> types = this.getAvailableAspectTypes();
		for(IAspectType type : types)
			this.clearAspects(type);
		return this;
	}

	/**
	 * Returns the amount of the specified aspect type
	 * @param type
	 * @return
	 */
	public final int get(IAspectType type) {
		return this.getAmount(type, false) + this.getAmount(type, true);
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
		if(amount > 0) {
			int currentAmount = this.get(type);
			int diff = currentAmount - amount;
			int dynAmounts = this.getAmount(type, true);
			this.putAmount(type, Math.max(dynAmounts - diff, 0), true);
			if(dynAmounts < diff)
				diff -= dynAmounts;
			else 
				diff = 0;
			int staticAmounts = this.getAmount(type, false);
			this.putAmount(type, Math.max(staticAmounts - diff, 0), false);
		} else {
			this.clearAspects(type);
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
		this.addAmount(type, amount, true);
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
	 * Writes this container to the specified NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList typesList = new NBTTagList();
		Set<Entry<IAspectType, List<InternalAspect>>> entrySet = this.aspects.entrySet();
		for(Entry<IAspectType, List<InternalAspect>> entry : entrySet) {
			NBTTagList aspectsList = new NBTTagList();
			for(InternalAspect aspect : entry.getValue()) {
				if(aspect.isSaved())
					aspectsList.appendTag(aspect.writeToNBT(new NBTTagCompound()));
			}
			typesList.appendTag(aspectsList);
		}
		nbt.setTag(ASPECTS_NBT_TAG, typesList);
		return nbt;
	}

	/**
	 * Reads the container from the specified NBT and adds the static aspects
	 * @param nbt
	 * @param staticAspects
	 * @return
	 */
	public AspectContainer create(@Nullable NBTTagCompound nbt, @Nullable List<Aspect> staticAspects) {
		List<InternalAspect> aspects = new ArrayList<InternalAspect>();
		if(nbt != null) {
			NBTTagList typesList = nbt.getTagList(ASPECTS_NBT_TAG, Constants.NBT.TAG_LIST);
			int typesCount = typesList.tagCount();
			for(int i = 0; i < typesCount; i++) {
				NBTTagList aspectsList = (NBTTagList) typesList.get(i);
				int aspectsCount = aspectsList.tagCount();
				for(int c = 0; c < aspectsCount; c++) {
					NBTTagCompound aspectNBT = aspectsList.getCompoundTagAt(c);
					InternalAspect aspect = InternalAspect.readFromNBT(aspectNBT);
					if(aspect != null)
						aspects.add(aspect);
				}
			}
		}
		if(staticAspects != null) {
			for(Aspect aspect : staticAspects) {
				boolean hasStatic = false;
				for(InternalAspect internalAspect : aspects) {
					if(!internalAspect.isDynamic && aspect.type == internalAspect.type)
						hasStatic = true;
				}
				if(!hasStatic)
					aspects.add(new InternalAspect(aspect.type, aspect.amount, false, false));
			}
		}
		for(InternalAspect aspect : aspects) {
			List<InternalAspect> entries = this.getEntries(aspect.type);
			entries.add(aspect);
		}
		return this;
	}
}
