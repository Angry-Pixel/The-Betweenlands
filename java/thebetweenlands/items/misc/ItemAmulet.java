package thebetweenlands.items.misc;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.input.KeyBindingsBL;
import thebetweenlands.entities.mobs.EntityGiantToad;
import thebetweenlands.entities.mobs.EntityTarminion;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.EntityAmulet;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.client.PacketDropAmulet;
import thebetweenlands.utils.ItemRenderHelper;
import thebetweenlands.utils.LightingUtil;

public class ItemAmulet extends Item {
	public static List<Class<? extends EntityLivingBase>> supportedEntities = new ArrayList<Class<? extends EntityLivingBase>>();
	private IIcon[] gemIcons = new IIcon[CircleGem.TYPES.length];

	static {
		supportedEntities.add(EntityTarminion.class);
		supportedEntities.add(EntityGiantToad.class);
	}

	public ItemAmulet() {
		this.setUnlocalizedName("thebetweenlands.amulet.none");

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

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		addAmulet(player, stack, player);
		return stack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
		if(!supportedEntities.isEmpty() && supportedEntities.contains(entity.getClass())) {
			addAmulet(player, itemstack, entity);
		}
		return true;
	}

	public static boolean addAmulet(EntityPlayer player, ItemStack stack, EntityLivingBase entity) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			if(property.getAmulets().size() < property.getAmuletSlots() && CircleGem.getGem(stack) != CircleGem.NONE) {
				if(!player.worldObj.isRemote) {
					property.addAmulet(stack, true);
					if(!player.capabilities.isCreativeMode) 
						stack.stackSize--;
				}
				player.swingItem();
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onInteract(EntityInteractEvent event) {
		Entity entity = event.target;
		if(event.entityPlayer != null && entity instanceof EntityLivingBase && !supportedEntities.isEmpty() && supportedEntities.contains(entity.getClass())) {
			EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
			if(property != null) {
				if(event.entityPlayer.isSneaking() && event.entityPlayer.getHeldItem() == null && property.getAmulets().size() > 0) {
					EntityAmulet amulet = null;
					for(int i = property.getAmulets().size() - 1; i >= 0; i--) {
						EntityAmulet a = property.getAmulets().get(i);
						if(a.isRemovable()) {
							amulet = a;
							break;
						}
					}
					if(amulet != null) {
						if(!event.entityPlayer.worldObj.isRemote) {
							property.removeAmulet(amulet);
							ItemStack stack = createStack(amulet.getAmuletGem());
							entity.entityDropItem(stack, entity.getEyeHeight());
						}
						event.entityPlayer.swingItem();
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity != null && !entity.worldObj.isRemote) {
			EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
			if(property != null) {
				if(property.getAmulets().size() > 0) {
					for(EntityAmulet amulet : property.getAmulets()) {
						ItemStack stack = createStack(amulet.getAmuletGem());
						entity.entityDropItem(stack, entity.getEyeHeight());
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Post event) {
		if(event.entity != null && !supportedEntities.isEmpty() && supportedEntities.contains(event.entity.getClass())) {
			this.renderAmulet(event.entity, event.x, event.y, event.z, 0.0F);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event) {
		if(event.entityPlayer != null) {
			GL11.glPushMatrix();
			GL11.glRotated(-event.entityPlayer.rotationYaw, 0, 1, 0);
			GL11.glTranslated(0, -0.7D, 0);
			this.renderAmulet(
					event.entityPlayer,
					(event.entityPlayer.lastTickPosX + (event.entityPlayer.posX - event.entityPlayer.lastTickPosX) * event.partialRenderTick) - RenderManager.renderPosX,
					(event.entityPlayer.lastTickPosY + (event.entityPlayer.posY - event.entityPlayer.lastTickPosY) * event.partialRenderTick) - RenderManager.renderPosY,
					(event.entityPlayer.lastTickPosZ + (event.entityPlayer.posZ - event.entityPlayer.lastTickPosZ) * event.partialRenderTick) - RenderManager.renderPosZ,
					event.partialRenderTick);
			GL11.glPopMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderAmulet(EntityLivingBase entity, double x, double y, double z, float partialTicks) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			if(property.getAmulets().size() > 0) {
				int amulets = property.getAmulets().size();
				float degOffset = 360.0F / amulets;
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);
				for(int i = 0; i < amulets; i++) {
					GL11.glRotated(degOffset, 0, 1, 0);
					EntityAmulet amulet = property.getAmulets().get(i);
					CircleGem gem = amulet.getAmuletGem();
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
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(KeyBindingsBL.dropAmulet.isPressed()) {
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			if(player != null) {
				EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesCircleGem.class);
				if(property != null && property.getAmulets().size() > 0) {
					TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDropAmulet()));
				}
			}
		}
	}

	@SubscribePacket
	public static void onPacketDropAmulet(PacketDropAmulet packet) {
		if(packet.getContext().getServerHandler() != null) {
			EntityPlayer sender = packet.getContext().getServerHandler().playerEntity;
			if(sender != null) {
				EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(sender, EntityPropertiesCircleGem.class);
				if(property != null && property.getAmulets().size() > 0) {
					EntityAmulet amulet = null;
					for(int i = property.getAmulets().size() - 1; i >= 0; i--) {
						EntityAmulet a = property.getAmulets().get(i);
						if(a.isRemovable()) {
							amulet = a;
							break;
						}
					}
					if(amulet != null) {
						CircleGem gem = amulet.getAmuletGem();
						ItemStack stack = createStack(gem);
						if(!sender.inventory.addItemStackToInventory(stack))
							sender.entityDropItem(stack, sender.getEyeHeight());
						property.removeAmulet(amulet);
					}
				}
			}
		}
	}
}
