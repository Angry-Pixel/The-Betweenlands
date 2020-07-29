package thebetweenlands.common.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.item.IRunelet;
import thebetweenlands.api.rune.RuneCategory;
import thebetweenlands.common.inventory.InventoryCustomCraftResult;
import thebetweenlands.common.inventory.InventoryPassthroughCraftingInput;
import thebetweenlands.common.inventory.InventoryRuneletCrafting;
import thebetweenlands.common.inventory.slot.SlotPassthroughCraftingInput;
import thebetweenlands.common.inventory.slot.SlotRuneCarving;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;

public class ContainerRuneCarvingTable extends ContainerWorkbench {
	private final World world;
	private final EntityPlayer player;
	private final TileEntityRuneCarvingTable tile;

	private final SlotPassthroughCraftingInput craftingSlot;
	
	private final InventoryPassthroughCraftingInput carvingMatrix;
	private final InventoryCustomCraftResult[] runeCarveResults;

	public ContainerRuneCarvingTable(InventoryPlayer playerInventory, TileEntityRuneCarvingTable tile) {
		super(playerInventory, tile.getWorld(), tile.getPos());
		this.world = tile.getWorld();
		this.player = playerInventory.player;
		this.tile = tile;

		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();

		this.craftMatrix = new InventoryRuneletCrafting(this, tile);
		this.craftMatrix.openInventory(playerInventory.player);

		this.craftResult = new InventoryCustomCraftResult(tile, this);

		//Crafting Result
		this.craftingSlot = new SlotPassthroughCraftingInput(playerInventory.player, this.craftMatrix, this.craftResult, 9, 80, 121, tile, this);
		this.addSlotToContainer(this.craftingSlot);

		this.runeCarveResults = new InventoryCustomCraftResult[4];
		for(int i = 0; i < 4; i++) {
			this.runeCarveResults[i] = new InventoryCustomCraftResult(tile, null);
		}

		this.carvingMatrix = new InventoryPassthroughCraftingInput(this, this.craftingSlot);
		this.carvingMatrix.openInventory(playerInventory.player);

		//Crafting matrix
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				this.addSlotToContainer(new Slot(this.craftMatrix, x + y * 3, 62 + x * 18, 36 + y * 18));
			}
		}

		//Aspect input
		this.addSlotToContainer(new Slot(tile, 10, 134, 98));

		//Carving Results
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[0], 11, 56, 108, this.craftingSlot));
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[1], 12, 56, 132, this.craftingSlot));
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[2], 13, 104, 132, this.craftingSlot));
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[3], 14, 104, 108, this.craftingSlot));

		//Player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 81 + 84 + y * 18));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 81 + 142));
		}

		tile.onCraftMatrixChanged();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		BlockPos pos = this.tile.getPos();
		if(playerIn.world.getTileEntity(pos) != this.tile) {
			return false;
		} else {
			return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		this.craftMatrix.closeInventory(playerIn);
		this.carvingMatrix.closeInventory(playerIn);
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		for(InventoryCustomCraftResult carveResult : this.runeCarveResults) {
			if(slotIn.inventory == carveResult) {
				return false;
			}
		}
		return super.canMergeSlot(stack, slotIn);
	}

	private boolean updateCarveResults = true;
	private boolean carveInputChanged = false;

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		try {
			this.updateCarveResults = false;
			this.carveInputChanged = false;

			ItemStack result = super.slotClick(slotId, dragType, clickTypeIn, player);

			if(this.carveInputChanged) {
				for(int i = 0; i < 4; i++) {
					this.runeletSlotChangedCraftingGrid(this.world, this.player, this.carvingMatrix, this.runeCarveResults[i], 11 + i, RuneCategory.VALUES[i]);
				}
			}

			return result;
		} finally {
			this.updateCarveResults = true;
		}
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);

		this.carveInputChanged = true;

		if(this.updateCarveResults) {
			for(int i = 0; i < 4; i++) {
				this.runeletSlotChangedCraftingGrid(this.world, this.player, this.carvingMatrix, this.runeCarveResults[i], 11 + i, RuneCategory.VALUES[i]);
			}
		}
	}

	@Nullable
	protected IRecipe findRuneletSlotRecipe(InventoryCrafting craftingMatrix, World world) {
		IRecipe regularMatchingRecipe = null;

		for(IRecipe recipe : CraftingManager.REGISTRY) {
			if(recipe.matches(craftingMatrix, world)) {
				if(regularMatchingRecipe == null) {
					regularMatchingRecipe = recipe;
				}

				ItemStack output = recipe.getRecipeOutput();
				if(!output.isEmpty() && output.getItem() instanceof IRunelet) {
					//Prioritize runelet recipes
					return recipe;
				}
			}
		}

		return regularMatchingRecipe;
	}

	@Override
	protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftingMatrix, InventoryCraftResult craftingResult) {
		if(!world.isRemote && !this.craftingSlot.hasPersistentItem()) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP)player;

			ItemStack result = ItemStack.EMPTY;

			IRecipe recipe = this.findRuneletSlotRecipe(craftingMatrix, world);

			if(recipe != null && (recipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(recipe))) {
				craftingResult.setRecipeUsed(recipe);
				result = recipe.getCraftingResult(craftingMatrix);
			}

			craftingResult.setInventorySlotContents(0, result);
			entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, result));
		}
	}

	protected void runeletSlotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftingMatrix, InventoryCraftResult craftingResult, int slotNumber, RuneCategory category) {
		if(!world.isRemote) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP)player;

			ItemStack result = ItemStack.EMPTY;

			ItemStack stack = craftingMatrix.getStackInSlot(0);

			if(!stack.isEmpty() && stack.getItem() instanceof IRunelet) {
				result = ((IRunelet) stack.getItem()).carve(stack, category);
			}

			craftingResult.setInventorySlotContents(0, result);
			entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, slotNumber, result));
		}
	}
}
