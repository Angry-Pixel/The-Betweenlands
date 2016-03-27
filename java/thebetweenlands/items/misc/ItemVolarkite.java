package thebetweenlands.items.misc;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import thebetweenlands.entities.EntityVolarkite;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.IManualEntryItem;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVolarkite extends Item implements IManualEntryItem {
    public ItemVolarkite(){
        maxStackSize = 1;
        setUnlocalizedName("thebetweenlands.volarkite");
    }

    @Override
    public String manualName(int meta) {
        return "volarKite";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[0];
    }

    @Override
    public int metas() {
        return 0;
    }

    @Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		Map<EntityPlayer, Integer> map = EntityVolarkite.getMapForSide(world.isRemote);
		if (map.containsKey(player)) {
			int entityId = map.get(player);
			Entity entity = world.getEntityByID(entityId);
			if (!(entity instanceof EntityVolarkite)) 
				return itemStack;
			EntityVolarkite kite = (EntityVolarkite)entity;
			kite.despawnGlider();
		} else
			spawnGlider(world, player);
		return itemStack;
	}

	private void spawnGlider(World world, EntityPlayer player) {
		if (!world.isRemote) {
			ItemStack heldStack = player.getHeldItem();
			if (heldStack != null && heldStack.getItem() == this) {
				EntityVolarkite kite = new EntityVolarkite(world, player);
				kite.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationPitch, player.rotationYaw);
				kite.onUpdate();
				world.spawnEntityInWorld(kite);
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRenderPre(RenderPlayerEvent.Pre e) {
		GL11.glPushMatrix();
		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		ItemStack heldStack = player.getHeldItem();
		if (heldStack != null && heldStack.getItem() == BLItemRegistry.volarkite) {
			if(!player.onGround && EntityVolarkite.isEntityHoldingGlider(player)) {
				player.limbSwingAmount = 0.001F;
				float yaw = player.rotationYaw;
				float x = (float) Math.cos(Math.PI * yaw / 180F);
				float y = (float) Math.sin(Math.PI * yaw / 180F);
				GL11.glRotatef(60.0F, x, 0.0F, y);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRenderPost(RenderPlayerEvent.Post e) {
		GL11.glPopMatrix();
	}
}
