package thebetweenlands.client.render.render.entity.layer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.inventory.EntityEquipmentSlot;

import thebetweenlands.client.render.model.entity.ModelPlayerRower;

public class LayerRowerArmor extends LayerArmorBase<ModelPlayerRower> {
    public LayerRowerArmor(RenderLivingBase<?> renderer) {
        super(renderer);
    }

    @Override
    protected void initArmor() {
        modelLeggings = new ModelPlayerRower(0.5F, false);
        modelArmor = new ModelPlayerRower(1, false);
    }

    public ModelPlayerRower getLeggings() {
        return modelLeggings;
    }

    public ModelPlayerRower getChest() {
        return modelArmor;
    }

    @Override
    protected void setModelSlotVisible(ModelPlayerRower rower, EntityEquipmentSlot slot) {
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
