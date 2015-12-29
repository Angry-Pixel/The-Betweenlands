package thebetweenlands.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.herblore.elixirs.ElixirRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.IManualEntryItem;

public class ItemImprovedRubberBoots extends ItemRubberBoots implements IManualEntryItem {
	private static final int MAX_WALK_TICKS = 20;

	public ItemImprovedRubberBoots() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return "thebetweenlands:textures/armour/rubberBoots.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.RUBBER_BALL.id;
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.stackTagCompound = new NBTTagCompound();
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(world.isRemote) {
			if(itemStack.stackTagCompound != null) {
				int walkTicksLeft = itemStack.stackTagCompound.getInteger("walkTicksLeft");
				if(player.onGround && world.getBlock((int)Math.floor(player.posX), (int)Math.floor(player.posY)-2, (int)Math.floor(player.posZ)) == BLBlockRegistry.swampWater) {
					player.motionX *= 1.0D / MAX_WALK_TICKS * walkTicksLeft;
					player.motionZ *= 1.0D / MAX_WALK_TICKS * walkTicksLeft;
				}
			}
			return;
		}
		if(itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound(); 
		}
		int walkTicksLeft = itemStack.stackTagCompound.getInteger("walkTicksLeft");
		Block blockBelowPlayer = world.getBlock((int)Math.floor(player.posX), (int)Math.floor(player.posY), (int)Math.floor(player.posZ));
		Block blockBelowPlayer2 = world.getBlock((int)Math.floor(player.posX), (int)Math.floor(player.posY) - 1, (int)Math.floor(player.posZ));
		boolean playerOnGround = player.onGround && !player.isInWater() && blockBelowPlayer != BLBlockRegistry.swampWater && blockBelowPlayer2 != BLBlockRegistry.swampWater;
		if(walkTicksLeft == 0 || playerOnGround) {
			itemStack.stackTagCompound.setInteger("walkTicksLeft", MAX_WALK_TICKS);
		} else {
			if(walkTicksLeft > 1) {
				walkTicksLeft--;
				itemStack.stackTagCompound.setInteger("walkTicksLeft", walkTicksLeft);
			}
		}
	}

	public static boolean checkPlayerEffect(EntityPlayer player) {
		if(ElixirRegistry.EFFECT_HEAVYWEIGHT.isActive(player)) return false;
		ItemStack boots = player.inventory.armorInventory[0];
		if(boots != null && boots.getItem() instanceof ItemImprovedRubberBoots) {
			if(boots.stackTagCompound != null && boots.stackTagCompound.getInteger("walkTicksLeft") > 1) {
				return true;
			}
		}
		return false;
	}

	public static double getWalkPercentage(EntityPlayer player) {
		ItemStack boots = player.inventory.armorInventory[0];
		if(boots != null && boots.getItem() instanceof ItemImprovedRubberBoots) {
			if(boots.stackTagCompound != null) {
				return (double)boots.stackTagCompound.getInteger("walkTicksLeft") / (double)MAX_WALK_TICKS;
			}
		}
		return 0.0D;
	}

	@Override
	public String manualName(int meta) {
		return "improvedRubberBoots";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2};
	}

	@Override
	public int metas() {
		return 0;
	}
}