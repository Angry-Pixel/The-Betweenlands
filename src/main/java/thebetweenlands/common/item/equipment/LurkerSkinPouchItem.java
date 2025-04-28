package thebetweenlands.common.item.equipment;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
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
import thebetweenlands.common.inventory.container.SecureItemContainer;
import thebetweenlands.common.network.clientbound.OpenRenameScreenPacket;
import thebetweenlands.common.registries.DataComponentRegistry;

public class LurkerSkinPouchItem extends Item {

	private final int slots;

	public LurkerSkinPouchItem(int slots, Properties properties) {
		super(properties);
		this.slots = slots;
	}
	
	public static void openMenu(Player player, ItemStack stack, int slots) {
		player.openMenu(new MenuProvider() {
			@Override
			public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
				return new LurkerSkinPouchMenu(containerId, playerInventory, new SecureItemContainer(stack, slots));
			}

			@Override
			public Component getDisplayName() {
				return stack.getHoverName();
			}
		}, buf -> {
			ItemStack.STREAM_CODEC.encode(buf, stack);
			buf.writeInt(slots);
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		boolean shouldOpenMenu = !player.isShiftKeyDown();
		 // TODO move renaming to an event handler
		boolean shouldRename = hand == InteractionHand.MAIN_HAND && stack.has(DataComponentRegistry.RENAMABLE);
		
		if (level.isClientSide() && (shouldOpenMenu || shouldRename)) {
			return InteractionResultHolder.success(stack);
		} 
		
		if (shouldOpenMenu) {
			LurkerSkinPouchItem.openMenu(player, stack, this.slots);
			return InteractionResultHolder.consume(stack);
		} else if(shouldRename) { // Don't rename if in offhand, because that renames the mainhand item instead
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenRenameScreenPacket(stack));
			return InteractionResultHolder.consume(stack);
		}
		
		return super.use(level, player, hand);
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
