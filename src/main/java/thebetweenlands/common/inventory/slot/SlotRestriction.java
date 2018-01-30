package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thebetweenlands.common.inventory.container.ContainerBLDualFurnace;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public class SlotRestriction extends Slot {
	private ItemStack item;
	private int maxItems;
	private Container container;

	public SlotRestriction(IInventory inventory, int slotIndex, int x, int y, ItemStack item, int maxItems, Container container) {
		super(inventory, slotIndex, x, y);
		this.item = item;
		this.maxItems = maxItems;
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack.getItem() == item.getItem() && stack.getItemDamage() == item.getItemDamage())
			return true;
		return false;
	}
	
    @Override
	public int getSlotStackLimit()
    {
        return maxItems;
    }

	@Override
	public void putStack(ItemStack stack) {
		super.putStack(stack);
		if (!stack.isEmpty() && container instanceof ContainerBLDualFurnace && ItemMisc.EnumItemMisc.LIMESTONE_FLUX.isItemOf(item) && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if(server != null) {
				PlayerList manager = server.getPlayerList();
				if (manager != null) {
					for (EntityPlayerMP entityPlayerMP : manager.getPlayers()) {
						if (entityPlayerMP.openContainer == container && container.canInteractWith(entityPlayerMP) && container.getCanCraft(entityPlayerMP)) {
							AdvancementCriterionRegistry.FLUX_ADDED.trigger(entityPlayerMP);
						}
					}
				}
			}
		}
	}
}
