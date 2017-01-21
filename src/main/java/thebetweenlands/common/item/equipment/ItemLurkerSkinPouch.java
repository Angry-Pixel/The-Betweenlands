package thebetweenlands.common.item.equipment;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.IEquipmentCapability;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemLurkerSkinPouch extends Item implements IEquippable {
	public ItemLurkerSkinPouch() {
		this.setMaxStackSize(1);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.setMaxDamage(3);

		this.addPropertyOverride(new ResourceLocation("pouch_size"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return stack.getItemDamage();
			}
		});
		IEquippable.addEquippedPropertyOverrides(this);
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
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean showAdvancedInfo) {
		int slots = 9 + (stack.getItemDamage() * 9);
		list.add(ChatFormatting.GRAY + I18n.format("lurkerSkinPouch.size", slots));
		list.add(ChatFormatting.GRAY + I18n.format("lurkerSkinPouch.info"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				int meta = stack.getItemDamage();
				player.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_LURKER_POUCH, world, meta, 0, 0);
			} else {
				player.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_LURKER_POUCH_NAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	/**
	 * Returns the first accessible pouch of the players equipment (first priority) or hotbar
	 * @param player
	 * @return
	 */
	public static ItemStack getFirstPouch(EntityPlayer player) {
		if(player.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
			IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			IInventory inv = cap.getInventory(EnumEquipmentInventory.POUCH);

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(stack != null && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
					return stack;
				}
			}
		}

		InventoryPlayer playerInventory = player.inventory;
		for(int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
			ItemStack stack = playerInventory.getStackInSlot(i);
			if(stack != null && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
				return stack;
			}
		}

		return null;
	}

	/*@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderPlayer(RenderLivingEvent.Specials.Post<EntityLivingBase> event) {
		if(event.getEntity() instanceof EntityPlayer) {
			renderPouch(event.getEntity(), event.getX(), event.getY(), event.getZ(), 0.0F);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void renderPouch(EntityLivingBase entity, double x, double y, double z, float partialTicks) {
		if(entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			IInventory inv = cap.getInventory(EnumEquipmentInventory.POUCH);

			ItemStack pouch = null;

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(stack != null && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
					pouch = stack;
					break;
				}
			}

			if(pouch != null) {
				TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
				RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				ITextureObject texture = textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.setBlurMipmap(false, false);

				IBakedModel model = renderItem.getItemModelMesher().getItemModel(pouch);

				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + 1.0D, z);
				GlStateManager.enableBlend();
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.rotate(entity.isSneaking() ? -28 : 0, 1, 0, 0);
				GlStateManager.translate(0, entity.isSneaking() ? -0.05D : 0, entity.isSneaking() ? -0.37D : -0.18D);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 0.02D);
				GlStateManager.scale(0.33D, 0.33D, 0.5D);

				renderItem.renderItem(pouch, model);

				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.scale(0.3D, 0.3D, 0.5D);

				renderItem.renderItem(pouch, model);

				GlStateManager.popMatrix();

				GlStateManager.popMatrix();

				texture.restoreLastBlurMipmap();
			}
		}
	}*/

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.POUCH;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target, IInventory inventory) {
		return false;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity target, IInventory inventory) {
		return target == player;
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
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) { }
}
