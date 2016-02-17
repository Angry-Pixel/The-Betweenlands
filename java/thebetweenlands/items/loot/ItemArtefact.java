package thebetweenlands.items.loot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import thebetweenlands.items.BLItemRegistry;

import java.util.List;

/**
 * Created by Bart on 26/01/2016.
 */
public class ItemArtefact extends Item {

    public static ItemStack createStack(EnumItemArtefact enumGeneric) {
        return createStack(enumGeneric, 1);
    }

    public static ItemStack createStack(EnumItemArtefact enumGeneric, int size) {
        return new ItemStack(BLItemRegistry.itemArtefact, size, enumGeneric.id);
    }

    public static ItemStack createStack(Item item, int size, int meta) {
        return new ItemStack(item, size, meta);
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemArtefact() {
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("thebetweenlands.itemArtefact");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        int maxID = 0;
        for (int i = 0; i < EnumItemArtefact.VALUES.length; i++) {
            EnumItemArtefact enumGeneric = EnumItemArtefact.VALUES[i];
            if(enumGeneric != EnumItemArtefact.INVALID) {
                int enumID = enumGeneric.id;
                if(enumID > maxID) {
                    maxID = enumID;
                }
            }
        }
        icons = new IIcon[maxID + 1];
        for (int i = 0; i < EnumItemArtefact.VALUES.length; i++) {
            EnumItemArtefact enumGeneric = EnumItemArtefact.VALUES[i];
            if(enumGeneric != EnumItemArtefact.INVALID) {
                icons[enumGeneric.id] = reg.registerIcon("thebetweenlands:" + enumGeneric.iconName);
            }
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (meta < 0 || meta >= icons.length) {
            return null;
        }

        return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < EnumItemArtefact.VALUES.length; i++) {
            if(EnumItemArtefact.VALUES[i] != EnumItemArtefact.INVALID) list.add(new ItemStack(item, 1, EnumItemArtefact.VALUES[i].id));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            return "item.thebetweenlands." + getEnumFromID(stack.getItemDamage()).iconName;
        } catch (Exception e) {
            return "item.thebetweenlands.unknownArtefact";
        }
    }

    public static EnumItemArtefact getEnumFromID(int id) {
        for (int i = 0; i < EnumItemArtefact.VALUES.length; i++) {
            EnumItemArtefact enumItemArtefact = EnumItemArtefact.VALUES[i];
            if(enumItemArtefact.id == id) return enumItemArtefact;
        }
        return EnumItemArtefact.INVALID;
    }

    public static enum EnumItemArtefact {
        INVALID("invalid", 1024),
        GOLD_BONE_BOW("GoldBoneBow", 1);

        public final String iconName;
        public final int id;

        private EnumItemArtefact(String unlocName, int id) {
            iconName = "artefact" + unlocName;
            this.id = id;
        }

        public static final EnumItemArtefact[] VALUES = values();
    }
}
