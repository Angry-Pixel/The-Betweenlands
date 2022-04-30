package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityBLItemFrame;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


public class ItemBLItemFrame extends Item implements ItemRegistry.IMultipleItemModelDefinition, ITintedItem {
    public ItemBLItemFrame() {
        setMaxDamage(0);
        setHasSubtypes(true);
        this.setCreativeTab(BLCreativeTabs.ITEMS);
    }


    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        BlockPos blockpos = pos.offset(facing);

        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && playerIn.canPlayerEdit(blockpos, facing, stack)) {
            EntityHanging entityhanging = this.createEntity(worldIn, blockpos, facing, stack.getItemDamage());

            if (entityhanging.onValidSurface())
            {
                if (!worldIn.isRemote)
                {
                    entityhanging.playPlaceSound();
                    worldIn.spawnEntity(entityhanging);
                }

                stack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide, int color) {
        return new EntityBLItemFrame(worldIn, pos, clickedSide, color);
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getTranslationKey() + "." + EnumBLDyeColor.byMetadata(i).getTranslationKey();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            Stream.of(EnumBLDyeColor.values()).forEach(t -> list.add(new ItemStack(this, 1, t.getMetadata())));
        }
    }

    @Override
    public Map<Integer, ResourceLocation> getModels() {
        Map<Integer, ResourceLocation> models = new HashMap<>();
        for(EnumBLDyeColor color : EnumBLDyeColor.values()) {
            models.put(color.getMetadata(), new ResourceLocation(ModInfo.ID, "item_frame"));
        }
        return models;
    }

    @Override
    public int getColorMultiplier(ItemStack stack, int tintIndex) {
        if(tintIndex == 0) {
            return EnumBLDyeColor.byMetadata(stack.getItemDamage()).getColorValue();
        }

        return -1;
    }
}
