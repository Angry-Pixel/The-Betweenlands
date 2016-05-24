package thebetweenlands.common.item.food;

import thebetweenlands.client.tab.BLCreativeTabs;


public class ItemChiromawWing extends ItemBLFood {
    public ItemChiromawWing() {
        super(0, 0, false, "chiromawWing");
        this.setCreativeTab(BLCreativeTabs.items);
    }

    /*
    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if(player != null) {
            EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesFood.class);
            if(property != null) {
                if(Sickness.getSicknessForHatred(property.getFoodHatred(this)) != Sickness.SICK) {
                    property.increaseFoodHatred(this, 64, 32);
                } else {
                    player.addPotionEffect(new PotionEffect(Potion.hunger.id, 600, 2));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        list.add(TranslationHelper.translateToLocal("chiromawWing.taste"));
        EntityPropertiesFood prop = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesFood.class);
        if(prop != null) {
            if(prop.getSickness(this) == Sickness.SICK) {
                list.add(TranslationHelper.translateToLocal("chiromawWing.dontEat"));
            }
        }
    }*/
}
