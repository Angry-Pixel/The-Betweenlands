package thebetweenlands.common.item.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.BetweenlandsKeybinds;
import thebetweenlands.common.inventory.LurkerSkinPouchMenu;
import thebetweenlands.common.inventory.container.ItemContainer;
import thebetweenlands.common.network.clientbound.OpenRenameScreenPacket;

import java.util.List;

public class LurkerSkinPouchItem extends Item {

	private final int slots;

	public LurkerSkinPouchItem(int slots, Properties properties) {
		super(properties);
		this.slots = slots;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			if (!player.isShiftKeyDown()) {
				player.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
						return new LurkerSkinPouchMenu(containerId, playerInventory, new ItemContainer(stack, LurkerSkinPouchItem.this.slots));
					}

					@Override
					public Component getDisplayName() {
						return stack.getHoverName();
					}
				}, buf -> {
					ItemStack.STREAM_CODEC.encode(buf, stack);
					buf.writeInt(LurkerSkinPouchItem.this.slots);
				});
			} else {
				PacketDistributor.sendToPlayer((ServerPlayer) player, OpenRenameScreenPacket.INSTANCE);
			}
			return InteractionResultHolder.consume(stack);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !oldStack.getComponentsPatch().forget(type -> type == DataComponents.CONTAINER).equals(newStack.getComponentsPatch().forget(type -> type == DataComponents.CONTAINER));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.thebetweenlands.lurker_skin_pouch.size", String.valueOf(this.slots)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.thebetweenlands.lurker_skin_pouch.usage", BetweenlandsKeybinds.OPEN_POUCH.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
		if (this.slots < 36) {
			tooltip.add(Component.translatable("item.thebetweenlands.lurker_skin_pouch.upgrade").withStyle(ChatFormatting.GRAY));
		}
	}
}
