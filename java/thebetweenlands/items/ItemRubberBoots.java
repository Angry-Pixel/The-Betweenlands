package thebetweenlands.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.recipes.BLMaterials;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRubberBoots extends ItemArmor {
	private static final int MAX_WALK_TICKS = 40;

	public ItemRubberBoots() {
		super(BLMaterials.armorRubber, 2, 3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return "thebetweenlands:textures/armour/rubberBoots.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.materialsBL && material.getItemDamage() == EnumMaterialsBL.RUBBER_BALL.ordinal();
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.stackTagCompound = new NBTTagCompound();
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(world.isRemote) return;
		if(itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound(); 
		}
		int walkTicksLeft = itemStack.stackTagCompound.getInteger("walkTicksLeft");
		Block blockBelowPlayer = world.getBlock((int)Math.floor(player.posX), (int)Math.floor(player.posY) - 1, (int)Math.floor(player.posZ));
		boolean playerOnGround = player.onGround && !player.isInWater() && blockBelowPlayer != BLBlockRegistry.swampWater && blockBelowPlayer != BLBlockRegistry.mud;
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
		ItemStack boots = player.inventory.armorInventory[0];
		if(boots != null && boots.getItem() instanceof ItemRubberBoots) {
			if(boots.stackTagCompound != null && boots.stackTagCompound.getInteger("walkTicksLeft") > 1) {
				return true;
			}
		}
		return false;
	}
}