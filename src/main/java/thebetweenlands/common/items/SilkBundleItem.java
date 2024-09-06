package thebetweenlands.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import thebetweenlands.common.inventory.SilkBundleMenu;
import thebetweenlands.common.inventory.container.ItemContainer;

import java.util.List;

public class SilkBundleItem extends Item {
	public SilkBundleItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isShiftKeyDown() && !level.isClientSide()) {
			player.openMenu(new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
					return new SilkBundleMenu(containerId, playerInventory, new ItemContainer(stack, 4));
				}

				@Override
				public Component getDisplayName() {
					return SilkBundleItem.this.getName(stack);
				}
			});
			return InteractionResultHolder.consume(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		for (ItemStack itemstack : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
			tooltip.add(Component.translatable("item.thebetweenlands.silk_bundle.item", itemstack.getHoverName()).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
}
