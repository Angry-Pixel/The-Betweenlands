package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

import thebetweenlands.client.render.entity.RenderWeedwoodRowboat.ArmArticulation;
import thebetweenlands.client.render.entity.layer.LayerRowerArmor;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower;
import thebetweenlands.client.render.model.entity.rowboat.ModelPlayerRower;

public class RenderPlayerRower extends RenderLivingBase<AbstractClientPlayer> {
    private ModelBipedRower[] models;

    public RenderPlayerRower(RenderManager mgr, boolean slimArms) {
        super(mgr, new ModelPlayerRower(0, slimArms), 0.5F);
        layerRenderers.clear();
        LayerRowerArmor armor = new LayerRowerArmor(this);
        addLayer(armor);
        models = new ModelBipedRower[] { (ModelBipedRower) mainModel, armor.getChest(), armor.getLeggings() };
    }

    @Override
    public ModelPlayerRower getMainModel() {
        return (ModelPlayerRower) super.getMainModel();
    }

    public void renderPilot(AbstractClientPlayer pilot, ArmArticulation leftArm, ArmArticulation rightArm, float bodyRotateAngleX, float bodyRotateAngleY, double x, double y, double z, float delta) {
        for (ModelBipedRower model : models) {
            model.bipedLeftArm.rotateAngleX = leftArm.shoulderAngleX;
            model.bipedLeftArm.rotateAngleY = leftArm.shoulderAngleY;
            model.setLeftArmFlexionAngle(leftArm.flexionAngle);
            model.bipedRightArm.rotateAngleX = rightArm.shoulderAngleX;
            model.bipedRightArm.rotateAngleY = rightArm.shoulderAngleY;
            model.setRightArmFlexionAngle(rightArm.flexionAngle);
            model.bipedBody.rotateAngleX = bodyRotateAngleX;
            model.bipedBody.rotateAngleY = bodyRotateAngleY;
            model.bipedHead.rotateAngleX = -bodyRotateAngleX * 0.75F;
            model.bipedHead.rotateAngleY = -bodyRotateAngleY * 0.75F;
            model.bipedLeftArm.rotationPointZ = leftArm.shoulderZ * 16;
            model.bipedRightArm.rotationPointZ = rightArm.shoulderZ * 16;
        }
        doRender(pilot, x, y, z, pilot.prevRotationYaw + (pilot.rotationYaw - pilot.prevRotationYaw) * delta, delta);
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float yaw, float delta) {
        if (!entity.isUser() || renderManager.renderViewEntity == entity) {
            setModelVisibilities(entity);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            super.doRender(entity, x, y, z, yaw, delta);
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }

    private void setModelVisibilities(AbstractClientPlayer player) {
        ModelPlayerRower model = this.getMainModel();
        if (player.isSpectator()) {
            model.setInvisible(false);
            model.bipedHead.showModel = true;
            model.bipedHeadwear.showModel = true;
        } else {
            model.setInvisible(true);
            /*
             * model.bipedHeadwear.showModel = player.isWearing(EnumPlayerModelParts.HAT); modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET); modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG); modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG); modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE); modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
             */
        }
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer player, float delta) {
        float scale = 0.9375F;
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected void renderEntityName(AbstractClientPlayer player, double x, double y, double z, String name, double distance) {
        if (distance < 100) {
            Scoreboard scoreboard = player.getWorldScoreboard();
            ScoreObjective sidebarObj = scoreboard.getObjectiveInDisplaySlot(2);
            if (sidebarObj != null) {
                Score score = scoreboard.getOrCreateScore(player.getName(), sidebarObj);
                renderLivingLabel(player, score.getScorePoints() + " " + sidebarObj.getDisplayName(), x, y, z, 64);
                y += getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.025F;
            }
        }
        super.renderEntityName(player, x, y, z, name, distance);
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        return entity.getLocationSkin();
    }
}