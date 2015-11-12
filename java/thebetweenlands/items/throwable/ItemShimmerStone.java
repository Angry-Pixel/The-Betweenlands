package thebetweenlands.items.throwable;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

public class ItemShimmerStone extends Item implements IManualEntryItem {

	@SideOnly(Side.CLIENT)
	private IIcon shimmerIcon;

	private static final int MAX_SHIMMER_TICKS = 8;
	private int shimmerTicks = 0;
	private boolean shimmer = false;

	public ItemShimmerStone() {
		super();
		setUnlocalizedName("thebetweenlands.shimmerStone");
		setTextureName("thebetweenlands:shimmerStone");
	}

	public void tickTexture(Random rnd) {
		if(!shimmer && rnd.nextInt(30) == 0) {
			shimmer = true;
		}
		if(shimmer) {
			shimmerTicks++;
			if(shimmerTicks >= MAX_SHIMMER_TICKS) {
				shimmerTicks = 0;
				shimmer = false;
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--itemstack.stackSize;
		}

		if (!world.isRemote) {
			double px = player.posX;
			double py = player.posY + player.getEyeHeight();
			double pz = player.posZ;
			px -= (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
			py -= 0.25D;
			pz -= (double)(MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
			float strength = 0.8F;
			double mx = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * strength);
			double mz = (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * strength);
			double my = (double)(-MathHelper.sin((player.rotationPitch - 8) / 180.0F * (float)Math.PI) * strength);
			EntityItem itemEntity = new EntityItem(world, px, py, pz, new ItemStack(this));
			itemEntity.motionX = mx;
			itemEntity.motionY = my;
			itemEntity.motionZ = mz;
			itemEntity.delayBeforeCanPickup = 20;
			world.spawnEntityInWorld(itemEntity);
		}

		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.shimmerIcon = reg.registerIcon(this.getIconString() + "Shimmer");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if(shimmer) {
			return this.shimmerIcon;
		}
		return super.getIconFromDamage(damage);
	}

	@Override
	public String manualName(int meta) {
		return "shimmerStone";
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
	public int[] metas() {
		return new int[0];
	}
}