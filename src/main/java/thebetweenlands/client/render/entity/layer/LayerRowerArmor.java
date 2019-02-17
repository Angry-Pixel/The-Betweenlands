package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower;

public class LayerRowerArmor extends LayerArmorBase<ModelBiped> {
    public LayerRowerArmor(RenderLivingBase<?> renderer) {
        super(renderer);
    }

    @Override
    protected void initArmor() {
        modelLeggings = new ModelBipedRower(0.5F);
        modelArmor = new ModelBipedRower(1);
    }

    public ModelBiped getLeggings() {
        return modelLeggings;
    }

    public ModelBiped getChest() {
        return modelArmor;
    }

    @Override
    protected void setModelSlotVisible(ModelBiped armor, EntityEquipmentSlot slot) {
        armor.setVisible(true);
        switch (slot) {
            case HEAD:
                armor.bipedHead.showModel = true;
                armor.bipedHeadwear.showModel = true;
                break;
            case CHEST:
                armor.bipedBody.showModel = true;
                armor.bipedRightArm.showModel = true;
                armor.bipedLeftArm.showModel = true;
                break;
            case LEGS:
                armor.bipedBody.showModel = true;
                armor.bipedRightLeg.showModel = true;
                armor.bipedLeftLeg.showModel = true;
                break;
            case FEET:
                armor.bipedRightLeg.showModel = true;
                armor.bipedLeftLeg.showModel = true;
            default:
        }
    }
    
    @Override
    protected ModelBiped getArmorModelHook(net.minecraft.entity.EntityLivingBase entity, net.minecraft.item.ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }
}
