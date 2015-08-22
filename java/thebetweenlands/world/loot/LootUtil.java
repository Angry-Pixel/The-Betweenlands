package thebetweenlands.world.loot;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public class LootUtil {

	public static final WeightedLootList lootFuel = new WeightedLootList(new LootItemStack[] { new LootItemStack(Items.coal).setAmount(1, 16).setWeight(32), new LootItemStack(Items.coal).setDamage(1).setAmount(1, 16).setWeight(30), new LootItemStack(Blocks.coal_block).setAmount(1, 6).setWeight(10), new LootItemStack(Items.blaze_rod).setAmount(1, 8).setWeight(18), new LootItemStack(Items.lava_bucket).setWeight(15), new LootItemStack(Blocks.sapling).setAmount(1, 20).setDamage(0, 3).setWeight(10), new LootItemStack(Items.stick).setAmount(1, 32).setWeight(8) });

	public static void addLore(ItemStack is, String lore) {
		NBTTagCompound tag = is.stackTagCompound;
		if (tag == null)
			tag = new NBTTagCompound();
		if (!tag.hasKey("display"))
			tag.setTag("display", new NBTTagCompound());

		NBTTagList loreTag = tag.getCompoundTag("display").getTagList("Lore", NBT.TAG_STRING);
		if (lore == null)
			loreTag = new NBTTagList();
		loreTag.appendTag(new NBTTagString(lore));

		tag.getCompoundTag("display").setTag("Lore", loreTag);
		is.setTagCompound(tag);
	}

	public static void generateLoot(IInventory inventory, Random rand, WeightedLootList lootList, int minAmount, int maxAmount) {
		int amount = rand.nextInt(Math.max(1, maxAmount - minAmount + 1)) + minAmount;

		for (int a = 0; a < amount; a++)
			inventory.setInventorySlotContents(rand.nextInt(inventory.getSizeInventory()), lootList.generateIS(rand));
	}
}