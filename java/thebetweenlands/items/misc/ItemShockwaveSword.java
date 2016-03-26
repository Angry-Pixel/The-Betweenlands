package thebetweenlands.items.misc;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.tools.ItemSwordBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShockwaveSword extends ItemSwordBL {

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
        setHasSubtypes(true);
        setUnlocalizedName("thebetweenlands.shockwaveSword");
        setTextureName("thebetweenlands:shockwaveSword");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
		if (!stack.hasTagCompound())
			stack.stackTagCompound = new NBTTagCompound();
		if (!stack.getTagCompound().hasKey("charge"))
			stack.getTagCompound().setInteger("charge", 0);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1000;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!stack.hasTagCompound()) {
			stack.stackTagCompound = new NBTTagCompound();
			return false;
		}
		if (side == 1 && player.isSneaking()) {
			if (stack.getTagCompound().getInteger("charge") > 0) {
				world.playSoundAtEntity(player, "mob.ghast.fireball", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				if (!world.isRemote) {
					double direction = Math.toRadians(player.rotationYaw);
					for(int distance = 2; distance < stack.getTagCompound().getInteger("charge"); distance++) {
						Block block = world.getBlock((MathHelper.floor_double(player.posX - Math.sin(direction) * distance)), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ + Math.cos(direction) * distance));
					if (block != null)
						world.setBlock((MathHelper.floor_double(player.posX - Math.sin(direction) * distance)), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ + Math.cos(direction) * distance), Blocks.stone);
					}
					stack.getTagCompound().setInteger("charge", 0);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (stack.getTagCompound().getInteger("charge") < 16)
			stack.getTagCompound().setInteger("charge", stack.getTagCompound().getInteger("charge") + 1);
		if (stack.getTagCompound().getInteger("charge") >= 16)
			stack.getTagCompound().setInteger("charge", 16);
		return stack;
	}
	
}
