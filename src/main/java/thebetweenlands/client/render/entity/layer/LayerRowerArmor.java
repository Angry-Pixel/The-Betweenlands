package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.inventory.EntityEquipmentSlot;

import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower;

public class LayerRowerArmor extends LayerArmorBase<ModelBipedRower> {
    public LayerRowerArmor(RenderLivingBase<?> renderer) {
        super(renderer);
    }

    @Override
    protected void initArmor() {
        modelLeggings = new ModelBipedRower(0.5F);
        modelArmor = new ModelBipedRower(1);
    }

    public ModelBipedRower getLeggings() {
        return modelLeggings;
    }

    public ModelBipedRower getChest() {
        return modelArmor;
    }

    @Override
    protected void setModelSlotVisible(ModelBipedRower rower, EntityEquipmentSlot slot) {
        rower.setInvisible(false);
        switch (slot) {
            case HEAD:
                rower.bipedHead.showModel = true;
                rower.bipedHeadwear.showModel = true;
                break;
            case CHEST:
                rower.bipedBody.showModel = true;
                rower.bipedRightArm.showModel = true;
                rower.bipedLeftArm.showModel = true;
                break;
            case LEGS:
                rower.bipedBody.showModel = true;
                rower.bipedRightLeg.showModel = true;
                rower.bipedLeftLeg.showModel = true;
                break;
            case FEET:
                rower.bipedRightLeg.showModel = true;
                rower.bipedLeftLeg.showModel = true;
            default:
        }
    }
}
