package thebetweenlands.items.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.tools.ItemSwordBL;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodPortalTree;

import java.util.List;

public class ItemShockwaveSword extends ItemSword {

	public static ItemStack createStack(EnumShockwaveSword shockwaveSword, int size) {
		return new ItemStack(BLItemRegistry.shockwaveSword, size, shockwaveSword.ordinal());
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
        setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[EnumShockwaveSword.VALUES.length];
		for( int i = 0; i < EnumShockwaveSword.VALUES.length; i++ ) {
            icons[i] = reg.registerIcon("thebetweenlands:" + EnumShockwaveSword.VALUES[i].iconName);
        }
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		if( meta < 0 || meta >= icons.length ) {
            return null;
        }

		return icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for( int i = 0; i < EnumShockwaveSword.VALUES.length; i++ ) {
            list.add(new ItemStack(item, 1, i));
        }
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + stack.getItemDamage();
	}

	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase entity, EntityLivingBase player) {
		if (is.getItem().getDamage(is) != 0)
			return false;
		return super.hitEntity(is, entity, player);
	}

	public enum EnumShockwaveSword {
        SHOCKWAVE_SWORD("shockwaveSword"),
        SHOCKWAVE_SWORD_1("shockwaveSword1"),
        SHOCKWAVE_SWORD_2("shockwaveSword2"),
        SHOCKWAVE_SWORD_3("shockwaveSword3"),
        SHOCKWAVE_SWORD_4("shockwaveSword4");

        public final String iconName;

        EnumShockwaveSword(String unlocName) {
            iconName = unlocName;
        }

        public static final EnumShockwaveSword[] VALUES = values();
    }
}
