package thebetweenlands.items.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodPortalTree;

import java.util.List;

public class ItemSwampTalisman
        extends Item implements IManualEntryItem
{
	public static ItemStack createStack(EnumTalisman swampTalisman, int size) {
		return new ItemStack(BLItemRegistry.swampTalisman, size, swampTalisman.ordinal());
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemSwampTalisman() {
        this.setMaxDamage(0);
		this.maxStackSize = 1;
        this.setHasSubtypes(true);
        this.setUnlocalizedName("thebetweenlands.swampTalisman");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		this.icons = new IIcon[EnumTalisman.VALUES.length];
		for( int i = 0; i < EnumTalisman.VALUES.length; i++ ) {
            this.icons[i] = reg.registerIcon("thebetweenlands:" + EnumTalisman.VALUES[i].iconName);
        }
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		if( meta < 0 || meta >= this.icons.length ) {
            return null;
        }

		return this.icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for( int i = 0; i < EnumTalisman.VALUES.length; i++ ) {
            list.add(new ItemStack(item, 1, i));
        }
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + stack.getItemDamage();
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int meta, float hitX, float hitY, float hitZ) {
		if (!world.isRemote ) {
			if (!player.canPlayerEdit(x, y, z, meta, is))
				return false;
			else {
				if (is.getItem().getDamage(is) == 0) {
					Block block = world.getBlock(x, y, z);
					if (block instanceof BlockSapling) {
						if(new WorldGenWeedWoodPortalTree().generate(world, itemRand, x, y, z)) {
							world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "thebetweenlands:portalActivate", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
							player.setLocationAndAngles(x + 0.5D, y + 2D, z + 0.5D, player.rotationYaw, player.rotationPitch);
						}
					}
					is.damageItem(1, player);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String manualName(int meta) {
		return "swampTalisman";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{7};
	}

	@Override
	public int metas() {
		return 4;
	}


	public enum EnumTalisman
    {
        SWAMP_TALISMAN("swampTalisman"),
        SWAMP_TALISMAN_1("swampTalisman1"),
        SWAMP_TALISMAN_2("swampTalisman2"),
        SWAMP_TALISMAN_3("swampTalisman3"),
        SWAMP_TALISMAN_4("swampTalisman4");

        public final String iconName;

        EnumTalisman(String unlocName) {
            this.iconName = unlocName;
        }

        public static final EnumTalisman[] VALUES = values();
    }
}
