package thebetweenlands.common.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.misc.ItemGem;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public class AdvancementHandler {

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() == Side.SERVER && event.getEntityPlayer() instanceof EntityPlayerMP) {
            ItemStack stack = event.getItemStack();
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            AdvancementCriterionRegistry.CLICK_BLOCK.trigger(stack, (EntityPlayerMP) event.getEntityPlayer(), event.getPos(), state, event.getFace());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if(player != null && !event.getWorld().isRemote && player instanceof EntityPlayerMP) {
            AdvancementCriterionRegistry.BREAK_BLOCK.trigger((EntityPlayerMP) player, event.getPos(), event.getState());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase == TickEvent.Phase.END && player instanceof EntityPlayerMP && player.ticksExisted % 50 == 0) {
            AdvancementCriterionRegistry.HAS_ADVANCEMENT.trigger((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public static void onItemCrafting(PlayerEvent.ItemCraftedEvent event) {
        if (event.player != null && !event.player.world.isRemote && event.craftMatrix instanceof InventoryCrafting) {
            if (CircleGemHelper.getGem(event.crafting) != CircleGemType.NONE) {
                boolean hadGem = false;
                for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
                    ItemStack stack = event.craftMatrix.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        if (stack.getItem() instanceof ItemGem) {
                            hadGem = true;
                            break;
                        }
                    }
                }
                if (hadGem) {
                    EntityPlayerMP playerMP = getActivePlayer((InventoryCrafting) event.craftMatrix);
                    if (playerMP != null) {
                        AdvancementCriterionRegistry.MIDDLE_GEM_UPGRADE.trigger(playerMP);
                    }
                }
            } else if (event.crafting.getItem() instanceof ICorrodible) {
                boolean hadScabyst = false;
                for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
                    ItemStack stack = event.craftMatrix.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        if(ItemMisc.EnumItemMisc.SCABYST.isItemOf(stack)) {
                            hadScabyst = true;
                            break;
                        }
                    }
                }
                if (hadScabyst) {
                    EntityPlayerMP playerMP = getActivePlayer((InventoryCrafting) event.craftMatrix);
                    if (playerMP != null) {
                        AdvancementCriterionRegistry.COAT_TOOL.trigger(playerMP);
                    }
                }

            }
        }
    }

    private static EntityPlayerMP getActivePlayer(InventoryCrafting crafting) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server != null) {
            PlayerList manager = server.getPlayerList();
            if(manager != null) {
                Container container = crafting.eventHandler;
                if(container == null) {
                    return null;
                }
                for (EntityPlayerMP entityPlayerMP : manager.getPlayers()) {
                    if (entityPlayerMP.openContainer == container && container.canInteractWith(entityPlayerMP) && container.getCanCraft(entityPlayerMP)) {
                        return entityPlayerMP;
                    }
                }
            }
        }
        return null;
    }
}
