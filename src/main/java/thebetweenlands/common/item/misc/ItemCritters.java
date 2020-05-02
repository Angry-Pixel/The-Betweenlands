package thebetweenlands.common.item.misc;

import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.tools.ItemNet;

public class ItemCritters extends ItemMob {
	public ItemCritters() {
		super(1, null);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			for(Entry<Class<? extends Entity>, Pair<Supplier<? extends ItemMob>, Predicate<Entity>>> entry : ItemNet.CATCHABLE_ENTITIES.entrySet()) {
				if(entry.getValue().getLeft().get() == this) {
					items.add(this.capture(entry.getKey()));
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound entityNbt = stack.getTagCompound().getCompoundTag("Entity");
				ResourceLocation id = this.getCapturedEntityId(stack);
				if((id.getNamespace() + ":" + id.getPath()).equals("thebetweenlands:chiromaw_tame") || (id.getNamespace() + ":" + id.getPath()).equals("thebetweenlands:chiromaw_hatchling"))
					if(entityNbt.getBoolean("Electric")) {
						return true;
			}
		}
		return false;
	}
}
