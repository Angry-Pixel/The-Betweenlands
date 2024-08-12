package thebetweenlands.api.aspect;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.herblore.aspect.AspectManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//TODO a lot of this logic needs to be moved to data components
public class AspectContainerItem extends AspectContainer {
	public static final String ASPECTS_NBT_TAG = "blHerbloreAspects";

	/**
	 * The item stack this container belongs to
	 */
	public final ItemStack itemStack;

	/**
	 * The aspect manager that was used to load the static aspects. May be null
	 */
	@Nullable
	public final AspectManager manager;

	private AspectContainerItem(AspectManager manager, ItemStack stack) {
		this.itemStack = stack;
		this.manager = manager;
	}

	/**
	 * Creates an aspect container for the specified item stack.
	 * <p><b>If the aspect manager is null the container will not contain static aspects!</b>
	 * @param stack
	 * @param manager
	 * @return
	 */
	public static AspectContainerItem fromItem(ItemStack stack, @Nullable AspectManager manager) {
		AspectContainerItem container = new AspectContainerItem(manager, stack);
//		CompoundTag aspectNbt = stack.getTagCompound() != null ? stack.getTagCompound().getCompoundTag(ASPECTS_NBT_TAG) : null;
//		if(aspectNbt != null)
//			container.read(aspectNbt);
		return container;
	}

	/**
	 * Creates an aspect container for the specified item stack.
	 * <p><b>Does not contain static aspects!</b>
	 * @param stack
	 * @return
	 */
	public static AspectContainerItem fromItem(ItemStack stack) {
		AspectContainerItem container = new AspectContainerItem(null, stack);
//		CompoundTag aspectNbt = stack.getTagCompound() != null ? stack.getTagCompound().getCompoundTag(ASPECTS_NBT_TAG) : null;
//		if(aspectNbt != null)
//			container.read(aspectNbt);
		return container;
	}

	@Override
	protected void onChanged() {
//		CompoundTag nbt = this.itemStack.getTagCompound();
//		if(nbt == null)
//			this.itemStack.setTagCompound(nbt = new CompoundTag());
//		nbt.setTag(ASPECTS_NBT_TAG, this.save(new CompoundTag()));
	}

	/**
	 * Returns a list of all aspects in this container.
	 * Specify a discovery container if you only want discovered or dynamic aspects to be visible
	 * @param discoveries
	 * @return
	 */
	public List<Aspect> getAspects(DiscoveryContainer<?> discoveries) {
		List<Aspect> discoveredAspects = null;
		if(discoveries != null && this.manager != null)
			discoveredAspects = discoveries.getDiscoveredStaticAspects(this.manager, AspectManager.getAspectItem(this.itemStack));
		List<Aspect> aspects = new ArrayList<>();
		Set<AspectType> types = this.getStoredAspectTypes();
		for(AspectType type : types) {
			int amount = this.get(type, true);
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
				amount += this.get(type, false);
			if(amount > 0)
				aspects.add(new Aspect(type, amount));
		}
		return aspects;
	}

	/**
	 * Returns a list of all aspects that are discovered and visible to the specified player
	 * @param player
	 * @return
	 */
	public List<Aspect> getAspects(Player player) {
		return this.getAspects(DiscoveryContainer.getMergedDiscoveryContainer(player));
	}

	@Override
	protected List<Aspect> getStaticAspects() {
		return this.manager != null ? this.manager.getStaticAspects(AspectManager.getAspectItem(this.itemStack)) : ImmutableList.of();
	}
}
