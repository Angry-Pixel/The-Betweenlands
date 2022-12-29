package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelSpawnerCrystal;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.util.LightingUtil;

public class RenderSpawnerBetweenlands extends TileEntitySpecialRenderer<TileEntityMobSpawnerBetweenlands> {
    private ModelSpawnerCrystal spawnerCrystalModel = new ModelSpawnerCrystal();
    private final static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spawner_crystal.png");


    public static void renderSpawnerMob(MobSpawnerLogicBetweenlands spawnerLogic, double x, double y, double z, float partialTicks) {
        Entity entity = spawnerLogic.getCachedEntity();
        if (entity != null) {
            entity.setWorld(spawnerLogic.getSpawnerWorld());
            float scale = 0.4375F;
            GlStateManager.translate(0.0F, 0.4F, 0.0F);
            GlStateManager.rotate((float) (spawnerLogic.lastEntityRotation + (spawnerLogic.entityRotation - spawnerLogic.lastEntityRotation) * (double) partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.4F, 0.0F);
            GlStateManager.scale(scale, scale, scale);
            entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, true);
        }
    }

    @Override
    public void render(TileEntityMobSpawnerBetweenlands te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        float interpolatedCounter = 0;
        if (te != null) {
            interpolatedCounter = -(te.lastCounter + (te.counter - te.lastCounter) * partialTicks);
        }

        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        LightingUtil.INSTANCE.setLighting(255);
        float counter1 = interpolatedCounter;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.475F, y + 0.38F + (float) Math.sin(counter1) / 5.0F, z + 0.475F);
        GlStateManager.scale(0.3F, 0.3F, 0.3F);
        if (te != null) {
            renderSpawnerMob(te.getSpawnerLogic(), 0, 0, 0, partialTicks);
        }
        GlStateManager.popMatrix();
        LightingUtil.INSTANCE.revert();
        
        LightingUtil.INSTANCE.setLighting(255);
        this.bindTexture(TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.translate(x + 0.45F, y + 1.8F + (float) Math.sin(counter1) / 5.0F, z + 0.45F);
        GlStateManager.translate(0.025F, -0.5F, 0.025F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate((float) counter1 * 180, 0, 1, 0);
        GlStateManager.scale(2.5F, 2.5F, 2.5F);
        GlStateManager.color(1, 4 + (float) Math.sin(counter1) * 3, (float) Math.sin(counter1) * 2, 0.5F);
        this.spawnerCrystalModel.render();
        GlStateManager.popMatrix();

        float counter2 = interpolatedCounter / 1.5F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.475F, y + 1.5F + (float) Math.sin(counter2 * 3) / 2.0F - 0.5F, z + 0.475F);
        GlStateManager.translate(0.025F, -0.5F, 0.025F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate((float) counter2 * 720, 0, 1, 0);
        GlStateManager.translate(0.5F, 0, 0);
        GlStateManager.scale(2.25F, 2.25F, 2.25F);
        GlStateManager.scale(0.2F + (float) Math.sin(counter2) * (float) Math.sin(counter2) / 2, 0.2F + (float) Math.sin(counter2) * (float) Math.sin(counter2) / 2, 0.2F + (float) Math.sin(counter2) * (float) Math.sin(counter2) / 2);
        GlStateManager.color(1, 4 + (float) Math.sin(counter2) * 3, (float) Math.sin(counter2) * 2, (float) Math.cos(counter2) * (float) Math.cos(counter2) * 2);
        this.spawnerCrystalModel.render();
        GlStateManager.popMatrix();

        float counter3 = interpolatedCounter / 2.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.475F, y + 1.5F - (float) Math.cos(counter3 * 3) / 2.0F, z + 0.475F);
        GlStateManager.translate(0.025F, -0.5F, 0.025F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate((float) counter1 * 720, 0, 1, 0);
        GlStateManager.translate(0, 0, 0.5F);
        GlStateManager.scale(2.25F, 2.25F, 2.25F);
        GlStateManager.scale(0.2F + (float) Math.sin(counter3) * (float) Math.sin(counter3) / 2, 0.2F + (float) Math.sin(counter3) * (float) Math.sin(counter3) / 2, 0.2F + (float) Math.sin(counter3) * (float) Math.sin(counter3) / 2);
        GlStateManager.color(1, 4 + (float) Math.sin(counter3) * 3, (float) Math.sin(counter3) * 2, (float) Math.cos(counter3) * (float) Math.cos(counter3) * 2);
        this.spawnerCrystalModel.render();
        GlStateManager.popMatrix();

        float counter4 = interpolatedCounter / 2.5F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.475F, y + 1.5F + (float) Math.cos(counter4 * 3) / 2.0F - 0.5F, z + 0.475F);
        GlStateManager.translate(0.025F, -0.5F, 0.025F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate((float) counter1 * 720, 0, 1, 0);
        GlStateManager.translate(0.5F, 0, 0.5F);
        GlStateManager.scale(2.25F, 2.25F, 2.25F);
        GlStateManager.scale(0.2F + (float) Math.sin(counter4) * (float) Math.sin(counter4) / 2, 0.2F + (float) Math.sin(counter4) * (float) Math.sin(counter4) / 2, 0.2F + (float) Math.sin(counter4) * (float) Math.sin(counter4) / 2);
        GlStateManager.color(1, 4 + (float) Math.sin(counter4) * 3, (float) Math.sin(counter4) * 2, (float) Math.cos(counter4) * (float) Math.cos(counter4) * 2);
        this.spawnerCrystalModel.render();
        GlStateManager.popMatrix();

        float counter5 = interpolatedCounter / 3.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.475F, y + 1.5F + (float) Math.cos(counter5 * 3) / 2.0F - 0.5F, z + 0.475F);
        GlStateManager.translate(0.025F, -0.5F, 0.025F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate((float) counter1 * 720, 0, 1, 0);
        GlStateManager.translate(0F, 0, -0.5F);
        GlStateManager.scale(2.25F, 2.25F, 2.25F);
        GlStateManager.scale(0.2F + (float) Math.sin(counter5) * (float) Math.sin(counter5) / 2, 0.2F + (float) Math.sin(counter5) * (float) Math.sin(counter5) / 2, 0.2F + (float) Math.sin(counter5) * (float) Math.sin(counter5) / 2);
        GlStateManager.color(1, 4 + (float) Math.sin(counter5) * 3, (float) Math.sin(counter5) * 2, (float) Math.cos(counter5) * (float) Math.cos(counter5) * 2);
        this.spawnerCrystalModel.render();
        GlStateManager.popMatrix();

        LightingUtil.INSTANCE.revert();
    }
}
