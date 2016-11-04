package thebetweenlands.common.item.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;

import java.util.List;

public class ItemPestle extends Item {

    public ItemPestle() {
        setMaxDamage(128);
        maxStackSize = 1;
        setCreativeTab(BLCreativeTabs.HERBLORE);
        addPropertyOverride(new ResourceLocation("remaining"), (stack, worldIn, entityIn) -> {
            if(hasTag(stack) && stack.getTagCompound().getBoolean("active"))
                return 1;
            return 0;
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        list.add("Place Pestle in Mortar");
        list.add(Math.round(100F - 100F / getMaxDamage() * getDamage(stack)) + "% Remaining: " + (getMaxDamage() - getDamage(stack)) +" more uses." );
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }


    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int tick, boolean map) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        stack.setTagCompound(new NBTTagCompound());
    }

    private boolean hasTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            return false;
        }
        return true;
    }
}