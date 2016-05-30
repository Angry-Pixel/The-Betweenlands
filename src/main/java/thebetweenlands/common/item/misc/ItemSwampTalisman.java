package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.Registries;

public class ItemSwampTalisman extends Item implements ItemRegistry.ISingleJsonSubItems{
    public ItemSwampTalisman() {
        this.setMaxDamage(0);
        this.maxStackSize = 1;
        this.setHasSubtypes(true);
    }

    public static ItemStack createStack(EnumTalisman swampTalisman, int size) {
        return new ItemStack(Registries.INSTANCE.itemRegistry.swampTalisman, size, swampTalisman.ordinal());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < EnumTalisman.VALUES.length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.thebetweenlands.swamp_talisman_" + stack.getItemDamage();
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (!playerIn.canPlayerEdit(pos, facing, stack))
                return EnumActionResult.FAIL;
            else {
                if (stack.getItem().getDamage(stack) == 0) {
                    Block block = worldIn.getBlockState(pos).getBlock();
                    if (block instanceof BlockSapling) {
                        /*if(new WorldGenWeedWoodPortalTree().generate(world, itemRand, x, y, z)) {
                            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "thebetweenlands:portalActivate", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                            player.setLocationAndAngles(x + 0.5D, y + 2D, z + 0.5D, player.rotationYaw, player.rotationPitch);
                        }*/
                    }
                    stack.damageItem(1, playerIn);
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public List<String> getTypes() {
        List<String> models = new ArrayList<String>();
        for (EnumTalisman type : EnumTalisman.values())
            models.add(type.name());
        return models;
    }

    public enum EnumTalisman {
        SWAMP_TALISMAN_0,
        SWAMP_TALISMAN_1,
        SWAMP_TALISMAN_2,
        SWAMP_TALISMAN_3,
        SWAMP_TALISMAN_4;

        public static final EnumTalisman[] VALUES = values();
        public final String name;

        EnumTalisman() {
            name = name().toLowerCase(Locale.ENGLISH);
        }
    }
}
