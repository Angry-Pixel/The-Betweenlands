package thebetweenlands.common.herblore.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.common.herblore.aspect.AspectManager.AspectItem;
import thebetweenlands.common.herblore.aspect.type.IAspectType;

public final class ItemAspectContainer extends AspectContainer {
	/**
	 * The item stack this container belongs to
	 */
	public final ItemStack itemStack;

	/**
	 * The aspect manager that was used to load the static aspects. May be null
	 */
	@Nullable
	public final AspectManager manager;

	private ItemAspectContainer(AspectManager manager, ItemStack stack) {
		this.itemStack = stack;
		this.manager = manager;
	}

	/**
	 * Creates an aspect container for the specified item stack
	 * @param stack
	 * @param manager
	 * @return
	 */
	public static ItemAspectContainer fromItem(ItemStack stack, @Nullable AspectManager manager) {
		ItemAspectContainer container = new ItemAspectContainer(manager, stack);
		List<Aspect> staticAspects = manager != null ? manager.getStaticAspects(new AspectItem(stack)) : null;
		if(stack.getTagCompound() != null)
			container.create(stack.getTagCompound(), staticAspects);
		else 
			container.create(null, staticAspects);
		return container;
	}

	@Override
	protected void onChanged() {
		NBTTagCompound nbt = this.itemStack.getTagCompound();
		if(nbt == null)
			this.itemStack.setTagCompound(nbt = new NBTTagCompound());
		this.writeToNBT(nbt);
	}

	/**
	 * Returns a list of all aspects in this container.
	 * Specify a discovery container if you only want discovered or dynamic aspects to be visible.
	 * @param discoveries
	 * @return
	 */
	public List<Aspect> getAspects(DiscoveryContainer discoveries) {
		List<Aspect> discoveredAspects = null;
		if(discoveries != null && this.manager != null)
			discoveredAspects = discoveries.getDiscoveredStaticAspects(this.manager, new AspectItem(this.itemStack));
		List<Aspect> aspects = new ArrayList<Aspect>();
		Set<IAspectType> types = this.getAvailableAspectTypes();
		for(IAspectType type : types) {
			int amount = this.getAmount(type, true);
			boolean hasDiscovered = false;
			if(discoveredAspects != null) {
				for(Aspect discovered : discoveredAspects) {
					if(discovered.type == type) {
						hasDiscovered = true;
						break;
					}
				}
			}
			if(discoveredAspects == null || hasDiscovered)
				amount += this.getAmount(type, false);
			if(amount > 0)
				aspects.add(new Aspect(type, amount));
		}
		return aspects;
	}
}
