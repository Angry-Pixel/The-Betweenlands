package thebetweenlands.items.misc;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.RenderLivingEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.mobs.EntityGiantToad;
import thebetweenlands.entities.mobs.EntityTarminion;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.EntityGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.IEquippable;
import thebetweenlands.utils.ItemRenderHelper;
import thebetweenlands.utils.LightingUtil;

public class ItemAmulet extends Item implements IEquippable {
	public static List<Class<? extends EntityLivingBase>> supportedEntities = new ArrayList<Class<? extends EntityLivingBase>>();
	private IIcon[] gemIcons = new IIcon[CircleGem.TYPES.length];

	static {
		supportedEntities.add(EntityTarminion.class);
		supportedEntities.add(EntityGiantToad.class);
	}

	public ItemAmulet() {
		this.setUnlocalizedName("thebetweenlands.amulet");

		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);

		this.setTextureName("thebetweenlands:amuletSocket");
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon(this.getIconString());
		for(int i = 0; i < CircleGem.TYPES.length; i++) {
			CircleGem gem = CircleGem.TYPES[i];
			String texture = this.getIconString();
			switch(gem) {
			case CRIMSON:
				texture = "thebetweenlands:amuletCrimsonMiddleGem";
				break;
			case AQUA:
				texture = "thebetweenlands:amuletAquaMiddleGem";
				break;
			case GREEN:
				texture = "thebetweenlands:amuletGreenMiddleGem";
				break;
			default:
			}
			this.gemIcons[i] = register.registerIcon(texture);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack) {
		CircleGem gem = CircleGem.getGem(stack);
		if(gem == CircleGem.NONE) {
			return this.itemIcon;
		} else {
			return this.gemIcons[gem.ordinal()];
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	public static ItemStack createStack(CircleGem gem) {
		ItemStack stack = new ItemStack(BLItemRegistry.amulet, 1, 0);
		CircleGem.setGem(stack, gem);
		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands.amulet." + CircleGem.getGem(stack).name;
		} catch (Exception e) {
			return "item.thebetweenlands.amulet.none";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for(CircleGem gem : CircleGem.TYPES) {
			list.add(createStack(gem));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Post event) {
		if(event.entity != null) {
			GL11.glPushMatrix();
			if(event.entity == TheBetweenlands.proxy.getClientPlayer()) {
				GL11.glTranslated(0, 1.1D, 0);
			}
			this.renderAmulet(event.entity, event.x, event.y, event.z, 0.0F);
			GL11.glPopMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderAmulet(EntityLivingBase entity, double x, double y, double z, float partialTicks) {
		EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(entity);
		List<Equipment> equippedAmulets = equipmentInventory.getEquipment(EnumEquipmentCategory.AMULET);
		if(equippedAmulets.size() > 0) {
			int amulets = equippedAmulets.size();
			float degOffset = 360.0F / amulets;
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			for(int i = 0; i < amulets; i++) {
				GL11.glRotated(degOffset, 0, 1, 0);
				Equipment amuletEquipment = equippedAmulets.get(i);
				CircleGem gem = CircleGem.getGem(amuletEquipment.item);
				ItemStack gemItem = null;
				switch(gem) {
				case CRIMSON:
					gemItem = new ItemStack(BLItemRegistry.crimsonMiddleGem);
					break;
				case AQUA:
					gemItem = new ItemStack(BLItemRegistry.aquaMiddleGem);
					break;
				case GREEN:
					gemItem = new ItemStack(BLItemRegistry.greenMiddleGem);
					break;
				default:
				}
				if(gemItem != null) {
					GL11.glPushMatrix();
					GL11.glRotated((entity.ticksExisted + partialTicks) * 1.5D, 0, 1, 0);
					GL11.glTranslated(0, entity.getEyeHeight() / 1.5D + Math.sin((entity.ticksExisted + partialTicks) / 60.0D + (double)i / amulets * Math.PI * 2.0D) / 2.0D * entity.height / 4.0D, entity.width / 1.5D);
					GL11.glScaled(0.25F * entity.height / 2.0D, 0.25F * entity.height / 2.0D, 0.25F * entity.height / 2.0D);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glColor4f(1, 1, 1, 0.8F);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					LightingUtil.INSTANCE.setLighting(255);
					ItemRenderHelper.renderItem(gemItem, 0);
					LightingUtil.INSTANCE.revert();
					GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
					float scale = ((float) Math.cos(entity.ticksExisted / 5.0F) + 1.0F) / 15.0F + 1.05F;
					GL11.glScalef(scale, scale, scale);
					GL11.glColorMask(false, false, false, false);
					ItemRenderHelper.renderItem(gemItem, 0);
					GL11.glColorMask(true, true, true, true);
					ItemRenderHelper.renderItem(gemItem, 0);
					GL11.glPopMatrix();
				}
			}
			GL11.glPopMatrix();
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	public static void addAmulet(CircleGem gem, Entity entity, boolean canUnequip, boolean canDrop) {
		ItemStack amulet = createStack(gem);
		if(amulet.stackTagCompound == null)
			amulet.stackTagCompound = new NBTTagCompound();
		amulet.stackTagCompound.setBoolean("canUnequip", canUnequip);
		amulet.stackTagCompound.setBoolean("canDrop", canDrop);
		EquipmentInventory.equipItem(null, entity, amulet);
	}

	@Override
	public EnumEquipmentCategory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentCategory.AMULET;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		if(entity instanceof EntityPlayer == false && !this.supportedEntities.contains(entity.getClass()) && player != null)
			return false;
		if(CircleGem.getGem(stack) == CircleGem.NONE)
			return false;
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			if(inventory.getEquipment(EnumEquipmentCategory.AMULET).size() < property.getAmuletSlots()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return entity == player || stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("canUnequip") || stack.stackTagCompound.getBoolean("canUnequip");
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, EquipmentInventory inventory) {
		return stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("canDrop") || stack.stackTagCompound.getBoolean("canDrop");
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, EquipmentInventory inventory) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			property.addGem(CircleGem.getGem(stack), EntityGem.Type.BOTH);
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, EquipmentInventory inventory) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			for(EntityGem gem : property.getGems()) {
				if(gem.getGem() == CircleGem.getGem(stack) && gem.matches(EntityGem.Type.BOTH)) {
					property.removeGem(gem);
					break;
				}
			}
		}
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return true;
	}
}
