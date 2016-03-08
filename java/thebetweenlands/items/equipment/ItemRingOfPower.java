package thebetweenlands.items.equipment;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.items.loot.ItemRing;
import thebetweenlands.manual.IManualEntryItem;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemRingOfPower extends ItemRing implements IManualEntryItem {
	public ItemRingOfPower() {
		super();
		this.setMaxDamage(1800);
		this.setUnlocalizedName("thebetweenlands.ringOfPower");
		this.setTextureName("thebetweenlands:ringOfPower");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("ring.power.bonus"));
	}

	@Override
	public String manualName(int meta) {
		return "ringOfPower";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{6};
	}

	@Override
	public int metas() {
		return 0;
	}
}
