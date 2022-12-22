package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.InventoryPouch;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;

public class ItemLurkerSkinPouch extends Item implements IEquippable, IRenamableItem, ITintedItem {
    public ItemLurkerSkinPouch() {
        this.setMaxStackSize(1);
        this.setCreativeTab(BLCreativeTabs.GEARS);
        this.setMaxDamage(3);
        this.setNoRepair();

        this.addPropertyOverride(new ResourceLocation("pouch_size"), (stack, worldIn, entityIn) -> stack.getItemDamage());
        IEquippable.addEquippedPropertyOverrides(this);
    }

    /**
     * Returns the first accessible pouch of the players equipment (first priority) or hotbar
     *
     * @param player
     * @return
     */
    public static ItemStack getFirstPouch(EntityPlayer player) {
        IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
        if (cap != null) {
            IInventory inv = cap.getInventory(EnumEquipmentInventory.MISC);

            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
                    return stack;
                }
            }
        }

        InventoryPlayer playerInventory = player.inventory;
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            ItemStack stack = playerInventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean isRepairable() {
    	return false;
    }
    
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    	return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        int slots = 9 + (stack.getItemDamage() * 9);
        list.add(TextFormatting.GRAY + I18n.format("tooltip.bl.lurker_skin_pouch.size", slots));
        list.add(I18n.format("tooltip.bl.lurker_skin_pouch.usage", KeyBindRegistry.OPEN_POUCH.getDisplayName()));
        if (stack.getItemDamage() < stack.getMaxDamage()) {
            list.add(I18n.format("tooltip.bl.lurker_skin_pouch.upgrade"));
        }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
    	if(player.isSneaking()) {
    		ItemStack heldItem = player.getHeldItem(hand);
    		if(!heldItem.isEmpty() && heldItem.getItem() == this) {
    			if(!world.isRemote) {
	    			InventoryItem inventory = new InventoryPouch(heldItem, 9 + (heldItem.getItemDamage() * 9), "Lurker Skin Pouch");
	    			TileEntity tile = world.getTileEntity(pos);
	        		if(tile != null) {
	        			IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	        			if (itemHandler != null) {
                            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                                ItemStack stack = inventory.getStackInSlot(i);
                                if (!stack.isEmpty()) {
                                    stack = ItemHandlerHelper.insertItemStacked(itemHandler, stack, false);
                                    inventory.setInventorySlotContents(i, stack);
                                }
                            }
                        }
	        		}
    			}
        		return EnumActionResult.SUCCESS;
    		}
    	}
    	return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH, world, 0, 0, 0);
            } else {
                player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
            }
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			ItemStack basePouch = new ItemStack(this);
            items.add(basePouch);
            items.add(new ItemStack(this, 1, basePouch.getMaxDamage()));
        }
	}
    
    private static boolean isRenderingWorld;
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors event) {
        isRenderingWorld = true;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        isRenderingWorld = false;
    }
    
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
	public static void onRenderPlayer(RenderLivingEvent.Specials.Post<EntityLivingBase> event) {
		if(event.getEntity() instanceof EntityPlayer) {
			renderPouch((EntityPlayer) event.getEntity(), event.getX(), event.getY(), event.getZ(), WorldRenderHandler.getPartialTicks());
		}
	}

	@SideOnly(Side.CLIENT)
	private static void renderPouch(EntityPlayer player, double x, double y, double z, float partialTicks) {
        IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
        if(cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.MISC);
			ItemStack pouch = ItemStack.EMPTY;

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
					pouch = stack;
					break;
				}
			}

			if(!pouch.isEmpty()) {
				TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
				RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				ITextureObject texture = textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.setBlurMipmap(false, false);

				IBakedModel model = renderItem.getItemModelWithOverrides(pouch, null, null);

				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + 1.0D, z);
				if(!isRenderingWorld) {
					GlStateManager.rotate(90 - player.renderYawOffset, 0, 1, 0);
				} else {
					GlStateManager.rotate(90 - (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks), 0, 1, 0);
				}
				GlStateManager.translate(player.isSneaking() ? 0.25D : 0.0D, (player.isSneaking() ? -0.15D : 0) - 0.2D, -0.25D);
				float limbSwingAmount = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTicks;
				float swing = (float)Math.sin((player.limbSwing - limbSwingAmount * (1.0F - partialTicks)) / 1.4F) * limbSwingAmount;
				GlStateManager.rotate(swing * 25.0F, 0, 0, 1);
				GlStateManager.rotate(swing * 13.0F, 1, 0, 0);
				GlStateManager.rotate(swing * -10.0F, 0, 0, 1);
				GlStateManager.translate(0, -0.1D, 0);
				GlStateManager.enableBlend();
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 0.02D);
				GlStateManager.scale(0.4D, 0.4D, 0.5D);

				renderItem.renderItem(pouch, model);

				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.scale(0.37D, 0.37D, 0.5D);

				renderItem.renderItem(pouch, model);

				GlStateManager.popMatrix();

				GlStateManager.popMatrix();

				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.restoreLastBlurMipmap();
			}
		}
	}

    @Override
    public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
        return EnumEquipmentInventory.MISC;
    }

    @Override
    public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target) {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityPlayer player, Entity target) {
        return target == player && EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, target, this).isEmpty();
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity target, IInventory inventory) {
        return true;
    }

    @Override
    public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
        return true;
    }

    @Override
    public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
    }

    @Override
    public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
    }

    @Override
    public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
    }

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorMultiplier(ItemStack stack, int tintIndex) {
		if (tintIndex == 1) {
			if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("type")) {
				int type = stack.getTagCompound().getInteger("type");
				return EnumBLDyeColor.byMetadata(type).getColorValue();
			}
			else
				return EnumBLDyeColor.byMetadata(EnumBLDyeColor.CHAMPAGNE.getMetadata()).getColorValue(); // default to Pewter Grey
		}
		return  -1;
	}
}
