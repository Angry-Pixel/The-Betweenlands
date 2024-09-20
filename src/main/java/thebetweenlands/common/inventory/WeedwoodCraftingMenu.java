package thebetweenlands.common.inventory;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import thebetweenlands.common.block.entity.WeedwoodCraftingTableBlockEntity;
import thebetweenlands.common.inventory.container.WeedwoodCraftingContainer;
import thebetweenlands.common.inventory.container.WeedwoodResultContainer;

public class WeedwoodCraftingMenu extends CraftingMenu {

	private final WeedwoodCraftingTableBlockEntity table;
	private final Player player;

	@SuppressWarnings("resource")
	public WeedwoodCraftingMenu(int i, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(i, playerInventory, (WeedwoodCraftingTableBlockEntity) Objects.requireNonNull(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) : null), ContainerLevelAccess.NULL);
	}

	public WeedwoodCraftingMenu(int containerId, Inventory playerInventory, WeedwoodCraftingTableBlockEntity table, ContainerLevelAccess access) {
		super(containerId, playerInventory, access);
		this.table = table;
		this.player = playerInventory.player;

		this.resultSlots = new WeedwoodResultContainer(table);
		this.craftSlots = new WeedwoodCraftingContainer(this, table);
		this.craftSlots.startOpen(playerInventory.player);

		// set the slots here because there's a bunch of extra tracking done that's not very useful
		int i = 0;

        this.slots.set(i++, new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));

		for(int x = 0; x < 3; ++x) {
			for(int y = 0; y < 3; ++y) {
				this.slots.set(i++, new Slot(this.craftSlots, y + x * 3, 30 + y * 18, 17 + x * 18)).index = i++;
			}
		}

		this.table.slotChangedCraftingGrid();
	}

	public void slotChangedCraftingGrid() {
		CraftingMenu.slotChangedCraftingGrid(this, this.player.level(), this.player, this.craftSlots, this.resultSlots, null);
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this.table, player, 8.0F);
	}

	@Override
	public void removed(Player player) {
		// super.removed will drop items and clear the crafting table inventory
		this.craftSlots.stopOpen(player);
	}

}
