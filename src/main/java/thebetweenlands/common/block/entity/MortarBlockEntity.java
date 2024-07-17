package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Optional;

public class MortarBlockEntity extends BaseContainerBlockEntity {

	public int progress;
	public boolean hasPestle;
	public boolean hasCrystal;
	public boolean manualGrinding = false;
	public float crystalVelocity;
	public float crystalRotation;
	public int itemBob;
	public boolean countUp = true;
	private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

	public MortarBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.MORTAR.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, MortarBlockEntity entity) {
		if (level.isClientSide()) {
			if (entity.hasCrystal) {
				entity.crystalVelocity -= Math.signum(entity.crystalVelocity) * 0.05F;
				entity.crystalRotation += entity.crystalVelocity;
				if (entity.crystalRotation >= 360.0F)
					entity.crystalRotation -= 360.0F;
				else if (entity.crystalRotation <= 360.0F)
					entity.crystalRotation += 360.0F;
				if (Math.abs(entity.crystalVelocity) <= 1.0F && level.getRandom().nextInt(15) == 0)
					entity.crystalVelocity = level.getRandom().nextFloat() * 18.0F - 9.0F;
				if (entity.countUp && entity.itemBob <= 20) {
					entity.itemBob++;
					if (entity.itemBob == 20)
						entity.countUp = false;
				}
				if (!entity.countUp && entity.itemBob >= 0) {
					entity.itemBob--;
					if (entity.itemBob == 0)
						entity.countUp = true;
				}
			}

			if (entity.progress > 0 && entity.progress < 84) {
				entity.progress++;
			}

			return;
		}

		boolean validRecipe = false;
		boolean outputFull = entity.outputIsFull();

		if (entity.pestleInstalled()) {
			SingleRecipeInput input = new SingleRecipeInput(entity.getItem(0));
			Optional<RecipeHolder<MortarRecipe>> holder = level.getRecipeManager().getRecipeFor(RecipeRegistry.MORTAR_RECIPE.get(), input, level);

			if (holder.isPresent()) {
				MortarRecipe recipe = holder.get().value();
				ItemStack output = recipe.getOutput(input, entity.getItem(2).copy(), level.registryAccess());
				boolean replacesOutput = recipe.replacesOutput();

				outputFull &= !replacesOutput;

				if ((entity.isCrystalInstalled() && entity.getItem(3).getDamageValue() < entity.getItem(3).getMaxDamage()) || entity.manualGrinding) {
					if (!output.isEmpty() && (replacesOutput || entity.getItem(2).isEmpty() || (ItemStack.isSameItemSameComponents(entity.getItem(2), output) && entity.getItem(2).getCount() + output.getCount() <= output.getMaxStackSize()))) {
						validRecipe = true;

						entity.progress++;

						if (entity.progress == 1) {
							level.playSound(null, pos, SoundRegistry.GRIND.get(), SoundSource.BLOCKS, 1F, 1F);

							//Makes sure client knows that new grinding cycle has started
							level.sendBlockUpdated(pos, state, state, 2);
						}

						if (entity.progress == 64 || entity.progress == 84) {
							level.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 0.3F, 1F);
							level.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.3F, 1F);
						}

						if (!entity.getItem(1).isEmpty())
							NBTHelper.getStackNBTSafe(entity.getItem(1)).setBoolean("active", true);

						if (entity.progress > 84) {
							if (!entity.getItem(0).isEmpty())
								if (entity.getItem(0).getCount() - 1 <= 0)
									entity.setItem(0, ItemStack.EMPTY);
								else
									entity.getItem(0).shrink(1);

							if (replacesOutput || entity.getItem(2).isEmpty())
								entity.setItem(2, output.copy());
							else if (ItemStack.isSameItemSameComponents(entity.getItem(2), output))
								entity.getItem(2).grow(output.getCount());

							entity.getItem(1).setDamageValue(entity.getItem(1).getDamageValue() + 1);

							if (!entity.manualGrinding)
								entity.getItem(3).setDamageValue(entity.getItem(3).getDamageValue() + 1);

							entity.progress = 0;
							entity.manualGrinding = false;

							if (entity.getItem(1).getDamageValue() >= entity.getItem(1).getMaxDamage()) {
								entity.setItem(1, ItemStack.EMPTY);
								entity.hasPestle = false;
							}

							if (!entity.getItem(1).isEmpty())
								NBTHelper.getStackNBTSafe(entity.getItem(1)).setBoolean("active", false);

							entity.setChanged();
						}
					}
				}
			}
		}
		if (entity.progress > 0) {
			entity.setChanged();
		}
		if (entity.pestleInstalled()) {
			if (!entity.hasPestle) {
				entity.hasPestle = true;
				level.sendBlockUpdated(pos, state, state, 2);
			}
		} else {
			if (entity.hasPestle) {
				entity.hasPestle = false;
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}

		if (!validRecipe || entity.getItem(0).isEmpty() || entity.getItem(1).isEmpty() || outputFull) {
			if (!entity.getItem(1).isEmpty())
				NBTHelper.getStackNBTSafe(entity.getItem(1)).setBoolean("active", false);

			if (entity.progress > 0) {
				entity.progress = 0;
				level.sendBlockUpdated(pos, state, state, 2);
				entity.setChanged();
			}
		}
		if (entity.getItem(3).isEmpty() && entity.progress > 0 && !entity.manualGrinding) {
			if (!entity.getItem(1).isEmpty())
				NBTHelper.getStackNBTSafe(entity.getItem(1)).setBoolean("active", false);
			entity.progress = 0;
			entity.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);
		}
		if (entity.isCrystalInstalled()) {
			if (!entity.hasCrystal) {
				entity.hasCrystal = true;
				level.sendBlockUpdated(pos, state, state, 2);
			}
		} else {
			if (entity.hasCrystal) {
				entity.hasCrystal = false;
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}

	public boolean pestleInstalled() {
		return !this.getItem(1).isEmpty() && this.getItem(1).is(ItemRegistry.PESTLE);
	}

	public boolean isCrystalInstalled() {
		return !this.getItem(3).isEmpty() && this.getItem(3).getItem() instanceof LifeCrystalItem && this.getItem(3).getDamageValue() <= this.getItem(3).getMaxDamage();
	}

	private boolean outputIsFull() {
		return !this.getItem(2).isEmpty() && this.getItem(2).getCount() >= this.getMaxStackSize();
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.mortar");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 4;
	}
}
