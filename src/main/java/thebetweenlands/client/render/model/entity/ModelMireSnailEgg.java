package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMireSnailEgg extends ModelBase {
    ModelRenderer shell;
    ModelRenderer inners;

    public ModelMireSnailEgg() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.shell = new ModelRenderer(this, 0, 0);
        this.shell.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.shell.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        this.inners = new ModelRenderer(this, 0, 5);
        this.inners.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.inners.addBox(-0.5F, -1.5F, -0.5F, 1, 1, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
       
        GlStateManager.pushMatrix();
        
        this.inners.render(unitPixel);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        
        this.shell.render(unitPixel);
        
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }
}
