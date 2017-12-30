package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import thebetweenlands.client.render.model.entity.ModelBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;

@SideOnly(Side.CLIENT)
public class RenderBlindCaveFish extends RenderLiving<EntityBlindCaveFish> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/blind_cave_fish.png");

    public RenderBlindCaveFish(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBlindCaveFish(), 0.2f);
    }

    @Override
    protected void preRenderCallback(EntityBlindCaveFish entitylivingbaseIn, float partialTickTime) {
        GL11.glTranslatef(0F, 0.1F, -0.1F);
        GL11.glScalef(0.2F, 0.2F, 0.2F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBlindCaveFish entity) {
        return TEXTURE;
    }
}
