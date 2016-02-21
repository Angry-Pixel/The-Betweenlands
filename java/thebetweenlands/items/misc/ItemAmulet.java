package thebetweenlands.items.misc;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import thebetweenlands.entities.mobs.EntityTarminion;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.GemCircleHelper;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.ItemRenderHelper;
import thebetweenlands.utils.LightingUtil;

public class ItemAmulet extends Item {
	private List<Class<? extends EntityLivingBase>> supportedEntities = new ArrayList<Class<? extends EntityLivingBase>>();

	public ItemAmulet() {
		this.setUnlocalizedName("item.thebetweenlands.amulet");

		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);

		this.setTextureName("thebetweenlands:strictlyHerblore/misc/vialGreen");

		this.supportedEntities.add(EntityTarminion.class);
	}

	public static ItemStack createStack(CircleGem gem) {
		ItemStack stack = new ItemStack(BLItemRegistry.amulet, 1, 0);
		stack.stackTagCompound = new NBTTagCompound();
		GemCircleHelper.setGem(stack, gem);
		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands.amulet." + GemCircleHelper.getGem(stack).name;
		} catch (Exception e) {
			return "item.thebetweenlands.amulet";
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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float bx, float by, float bz) {
		return addAmulet(player, stack, player);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
		if(!this.supportedEntities.isEmpty() && this.supportedEntities.contains(entity.getClass())) {
			return addAmulet(player, itemstack, entity);
		}
		return false;
	}

	public static boolean addAmulet(EntityPlayer player, ItemStack stack, EntityLivingBase entity) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			if(!property.hasAmulet() && property.getGem() == CircleGem.NONE && GemCircleHelper.getGem(stack) != CircleGem.NONE) {
				if(!player.worldObj.isRemote) {
					property.addAmulet(true);
					property.setGem(GemCircleHelper.getGem(stack));
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
		if(event.entityPlayer != null && entity instanceof EntityLivingBase && !this.supportedEntities.isEmpty() && this.supportedEntities.contains(entity.getClass())) {
			EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
			if(property != null) {
				if(event.entityPlayer.isSneaking() && event.entityPlayer.getHeldItem() == null && property.hasAmulet() && property.isRemovable()) {
					if(!event.entityPlayer.worldObj.isRemote) {
						CircleGem gem = property.getGem();
						ItemStack stack = createStack(gem);
						entity.entityDropItem(stack, entity.getEyeHeight());
						property.removeAmulet();
						property.setGem(CircleGem.NONE);
					}
					event.entityPlayer.swingItem();
					event.setCanceled(true);
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
				if(property.hasAmulet()) {
					CircleGem entityGem = GemCircleHelper.getGem(entity);
					ItemStack stack = createStack(entityGem);
					entity.entityDropItem(stack, entity.getEyeHeight());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Pre event) {
		if(event.entity != null && !this.supportedEntities.isEmpty() && this.supportedEntities.contains(event.entity.getClass())) {
			this.renderAmulet(event.entity, event.x, event.y, event.z, 0.0F);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Specials.Pre event) {
		if(event.entityPlayer != null) {
			GL11.glPushMatrix();
			GL11.glRotated(-event.entityPlayer.rotationYaw, 0, 1, 0);
			GL11.glScaled(1, -1, 1);
			GL11.glTranslated(0, -0.5D, 0);
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
			if(property.hasAmulet()) {
				CircleGem entityGem = property.getGem();
				ItemStack gemItem = new ItemStack(BLItemRegistry.amulet, 1, 0);
				GemCircleHelper.setGem(gemItem, entityGem);
				if(gemItem != null) {
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					GL11.glRotated((entity.ticksExisted + partialTicks) * 1.5D, 0, 1, 0);
					GL11.glTranslated(0, entity.getEyeHeight() / 1.5D + Math.sin((entity.ticksExisted + partialTicks) / 60.0D) / 2.0D * entity.height / 4.0D, entity.width / 1.5D);
					GL11.glScaled(0.35F * entity.height / 2.0D, 0.35F * entity.height / 2.0D, 0.35F * entity.height / 2.0D);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glColor4f(1, 1, 1, 0.8F);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					LightingUtil.INSTANCE.setLighting(255);
					ItemRenderHelper.renderItem(gemItem, 0);
					LightingUtil.INSTANCE.revert();
					GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
					GL11.glPushMatrix();
					float scale = ((float) Math.cos(entity.ticksExisted / 5.0F) + 1.0F) / 15.0F + 1.05F;
					GL11.glScalef(scale, scale, scale);
					GL11.glColorMask(false, false, false, false);
					ItemRenderHelper.renderItem(gemItem, 0);
					GL11.glColorMask(true, true, true, true);
					ItemRenderHelper.renderItem(gemItem, 0);
					GL11.glPopMatrix();
					GL11.glColor4f(1, 1, 1, 1);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glPopMatrix();
				}
			}
		}
	}
}
