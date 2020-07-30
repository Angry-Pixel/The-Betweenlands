package thebetweenlands.common.inventory.container;

import java.util.List;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.api.capability.IRuneCapability;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.item.IRuneletItem;
import thebetweenlands.api.rune.RuneCategory;
import thebetweenlands.api.rune.RuneTier;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.inventory.InventoryCustomCraftResult;
import thebetweenlands.common.inventory.InventoryCustomCrafting;
import thebetweenlands.common.inventory.InventoryPassthroughCraftingInput;
import thebetweenlands.common.inventory.InventoryRuneCarveResult;
import thebetweenlands.common.inventory.InventoryRuneletCrafting;
import thebetweenlands.common.inventory.slot.SlotAspectContainer;
import thebetweenlands.common.inventory.slot.SlotPassthroughCraftingInput;
import thebetweenlands.common.inventory.slot.SlotRuneCarving;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;

public class ContainerRuneCarvingTable extends ContainerWorkbench {
	protected final World world;
	protected final EntityPlayer player;
	protected final TileEntityRuneCarvingTable tile;
	protected final boolean fullGrid;

	protected final Slot aspectSlot;
	protected final SlotPassthroughCraftingInput craftingSlot;

	protected final InventoryPassthroughCraftingInput carvingMatrix;
	protected final InventoryRuneCarveResult[] runeCarveResults;

	public ContainerRuneCarvingTable(InventoryPlayer playerInventory, TileEntityRuneCarvingTable tile, boolean fullGrid) {
		super(playerInventory, tile.getWorld(), tile.getPos());
		this.world = tile.getWorld();
		this.player = playerInventory.player;
		this.tile = tile;
		this.fullGrid = fullGrid;

		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();

		this.craftMatrix = new InventoryRuneletCrafting(this, tile, tile.getCraftingGrid(), 3, 3);
		this.craftMatrix.openInventory(playerInventory.player);

		this.craftResult = new InventoryCustomCraftResult(tile, this);

		//Crafting Result
		this.craftingSlot = new SlotPassthroughCraftingInput(playerInventory.player, this.craftMatrix, this.craftResult, 9, 80, fullGrid ? 121 : 120, tile, this) {
			@Override
			protected void onCrafting(ItemStack stack) {
				super.onCrafting(stack);
				ContainerRuneCarvingTable.this.onCrafting();
			}
		};
		this.addSlotToContainer(this.craftingSlot); //0

		this.runeCarveResults = new InventoryRuneCarveResult[4];
		for(int i = 0; i < 4; i++) {
			this.runeCarveResults[i] = new InventoryRuneCarveResult(tile, null);
		}

		this.carvingMatrix = new InventoryPassthroughCraftingInput(this, this.craftingSlot);
		this.carvingMatrix.openInventory(playerInventory.player);

		//Crafting matrix
		if(fullGrid) {
			for (int y = 0; y < 3; ++y) {
				for (int x = 0; x < 3; ++x) {
					this.addSlotToContainer(new Slot(this.craftMatrix, x + y * 3, 62 + x * 18, 36 + y * 18)); //1-9
				}
			}
		} else {
			this.addSlotToContainer(new Slot(this.craftMatrix, 0, 80, 72)); //1

			for(int i = 0; i < 8; i++) {
				this.addSlotToContainer(new Slot(this.craftMatrix, i, 0, 0) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@SideOnly(Side.CLIENT)
					@Override
					public boolean isEnabled() {
						return false;
					}
				}); //2-9, dummy slots
			}
		}

		//Aspect input
		this.aspectSlot = this.addSlotToContainer(new SlotAspectContainer(new InventoryCustomCrafting(this, tile, tile.getAspectGrid(), 1, 1, "container.bl.rune_carving_table"), 0, 134, 98, AspectManager.get(this.world))); //10

		//Carving Results
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[0], 11, 56, 108, this.craftingSlot, this.aspectSlot) {
			@Override
			protected void onCrafting(ItemStack stack) {
				super.onCrafting(stack);
				ContainerRuneCarvingTable.this.onCrafting();
			}
		}); //11
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[1], 12, 56, 132, this.craftingSlot, this.aspectSlot) {
			@Override
			protected void onCrafting(ItemStack stack) {
				super.onCrafting(stack);
				ContainerRuneCarvingTable.this.onCrafting();
			}
		}); //12
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[2], 13, 104, 132, this.craftingSlot, this.aspectSlot) {
			@Override
			protected void onCrafting(ItemStack stack) {
				super.onCrafting(stack);
				ContainerRuneCarvingTable.this.onCrafting();
			}
		}); //13
		this.addSlotToContainer(new SlotRuneCarving(playerInventory.player, this.carvingMatrix, this.runeCarveResults[3], 14, 104, 108, this.craftingSlot, this.aspectSlot) {
			@Override
			protected void onCrafting(ItemStack stack) {
				super.onCrafting(stack);
				ContainerRuneCarvingTable.this.onCrafting();
			}
		}); //14

		//Player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 81 + 84 + y * 18)); //15-41
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 81 + 142)); //42-51
		}

		tile.onCraftMatrixChanged();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack currentStack = slot.getStack();
			result = currentStack.copy();

			if(index == 0 || index == 11 || index == 12 || index == 13 || index == 14) {
				currentStack.getItem().onCreated(currentStack, this.world, playerIn);

				if(!this.mergeItemStack(currentStack, 15, 51, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(currentStack, result);
			} else if(index >= 15 && index <= 41) {
				if(!this.mergeItemStack(currentStack, 10, 11, false) && !this.mergeItemStack(currentStack, 1, 10, false) && !this.mergeItemStack(currentStack, 42, 51, false)) {
					return ItemStack.EMPTY;
				}
			} else if(index >= 42 && index <= 50) {
				if(!this.mergeItemStack(currentStack, 10, 11, false) && !this.mergeItemStack(currentStack, 1, 10, false) && !this.mergeItemStack(currentStack, 15, 42, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(currentStack, 15, 51, false)) {
				return ItemStack.EMPTY;
			}

			if(currentStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if(currentStack.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack takenStack = slot.onTake(playerIn, currentStack);

			if(index == 0) {
				playerIn.dropItem(takenStack, false);
			}
		}

		return result;
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
					this.runeletSlotChangedCraftingGrid(this.world, this.player, this.carvingMatrix, this.runeCarveResults[i], 11 + i, RuneCategory.VALUES[i], RuneTier.TIER_1 /*TODO*/, true, true);
				}
			}

			return result;
		} finally {
			this.updateCarveResults = true;
		}
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		if(inventoryIn == this.craftMatrix) {
			this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);
		}

		this.carveInputChanged = true;

		if(this.updateCarveResults) {
			for(int i = 0; i < 4; i++) {
				this.runeletSlotChangedCraftingGrid(this.world, this.player, this.carvingMatrix, this.runeCarveResults[i], 11 + i, RuneCategory.VALUES[i], RuneTier.TIER_1 /*TODO*/, true, true);
			}
		}
	}

	@Nullable
	protected IRecipe findRuneletSlotRecipe(InventoryCrafting craftingMatrix, World world, boolean runeletsOnly) {
		IRecipe regularMatchingRecipe = null;

		for(IRecipe recipe : CraftingManager.REGISTRY) {
			if(recipe.matches(craftingMatrix, world)) {
				if(regularMatchingRecipe == null) {
					regularMatchingRecipe = recipe;
				}

				ItemStack output = recipe.getRecipeOutput();
				if(!output.isEmpty() && output.getItem() instanceof IRuneletItem) {
					//Prioritize runelet recipes
					return recipe;
				}
			}
		}

		return runeletsOnly ? null : regularMatchingRecipe;
	}

	@Override
	protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftingMatrix, InventoryCraftResult craftingResult) {
		if(!world.isRemote && !this.craftingSlot.hasPersistentItem()) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP)player;

			ItemStack result = ItemStack.EMPTY;

			IRecipe recipe = this.findRuneletSlotRecipe(craftingMatrix, world, !this.fullGrid);

			if(recipe != null && (recipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(recipe))) {
				craftingResult.setRecipeUsed(recipe);
				result = recipe.getCraftingResult(craftingMatrix);
			}

			craftingResult.setInventorySlotContents(0, result);
			entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, result));
		}
	}

	protected void runeletSlotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftingMatrix, InventoryRuneCarveResult craftingResult, int slotNumber, RuneCategory category, RuneTier tier, boolean infuse, boolean hideUselessRunes) {
		if(!world.isRemote) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP)player;

			ItemStack result = ItemStack.EMPTY;

			ItemStack input = craftingMatrix.getStackInSlot(0);

			if(!input.isEmpty() && input.getItem() instanceof IRuneletItem) {
				ItemStack carveInput = input.copy();
				carveInput.setCount(1);
				input = result = ((IRuneletItem) input.getItem()).carve(carveInput, category);
			}

			if(infuse && !input.isEmpty() && input.getItem() instanceof IRuneItem && ((IRuneItem) input.getItem()).getRuneCategory(input) == category) {
				ItemStack aspectStack = this.aspectSlot.getStack();

				if(!aspectStack.isEmpty()) {
					AspectContainer container = ItemAspectContainer.fromItem(aspectStack, AspectManager.get(world));
					List<Aspect> aspects = container.getAspects();

					if(!aspects.isEmpty()) {
						Aspect aspect = aspects.get(0);

						IRuneItem rune = (IRuneItem) input.getItem();

						ItemStack infuseInput = input.copy();
						infuseInput.setCount(1);

						int cost = rune.getInfusionCost(infuseInput, aspect.type, tier);

						if(cost <= aspect.amount) {
							ItemStack infused = rune.infuse(infuseInput, aspect.type, tier);

							if(!infused.isEmpty()) {
								boolean isValidRune = false;

								if(!hideUselessRunes) {
									isValidRune = true;
								} else {
									IRuneCapability runeCap = infused.getCapability(CapabilityRegistry.CAPABILITY_RUNE, null);
									isValidRune = runeCap != null && runeCap.getRuneContainerFactory() != null;
								}

								if(isValidRune) {
									input = result = infused;
									craftingResult.setAspectUsed(aspect.type, cost);
								}
							}
						}
					}
				}
			}

			craftingResult.setInventorySlotContents(0, result);
			entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, slotNumber, result));
		}
	}

	protected void onCrafting() {

	}
}
