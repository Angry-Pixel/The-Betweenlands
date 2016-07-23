package thebetweenlands.client.render.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelLootPot1;
import thebetweenlands.client.render.model.tile.ModelLootPot2;
import thebetweenlands.client.render.model.tile.ModelLootPot3;
import thebetweenlands.common.tile.TileEntityLootPot;

public class LootPotRenderer extends TileEntitySpecialRenderer<TileEntityLootPot> {

    private final ModelLootPot1 LOOT_POT = new ModelLootPot1();
    private final ModelLootPot2 LOOT_POT_2 = new ModelLootPot2();
    private final ModelLootPot3 LOOT_POT_3 = new ModelLootPot3();

    private final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/loot_pot_1.png");
    private final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/loot_pot_2.png");
    private final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/loot_pot_3.png");

    @Override
    public void renderTileEntityAt(TileEntityLootPot te, double x, double y, double z, float partialTicks, int destroyStage) {
        int rotation = te.getBlockMetadata() % 4;
        int type = (te.getBlockMetadata() - (te.getBlockMetadata() % 4)) / 4;
        int offset = te.getModelRotationOffset();

        switch (type){
            case 0: {
                bindTexture(TEXTURE_1);
                break;
            }
            case 1: {
                bindTexture(TEXTURE_2);
                break;
            }
            case 2: {
                bindTexture(TEXTURE_3);
                break;
            }
        }
        switch (rotation) {
            case 0:
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
                GlStateManager.scale(1F, -1F, -1F);
                GlStateManager.rotate(180F + offset, 0.0F, 1F, 0F);
                renderType(type);
                GlStateManager.popMatrix();
                break;
            case 1:
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
                GlStateManager.scale(1F, -1F, -1F);
                GlStateManager.rotate(offset, 0.0F, 1F, 0F);
                renderType(type);
                GlStateManager.popMatrix();
                break;
            case 2:
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
                GlStateManager.scale(1F, -1F, -1F);
                GlStateManager.rotate(90F + offset, 0.0F, 1F, 0F);
                renderType(type);
                GlStateManager.popMatrix();
                break;
            case 3:
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
                GlStateManager.scale(1F, -1F, -1F);
                GlStateManager.rotate(-90F + offset, 0.0F, 1F, 0F);
                renderType(type);
                GlStateManager.popMatrix();
                break;
        }

    }

    private void renderType(int type){
        switch (type){
            case 0: {
                LOOT_POT.render();
                break;
            }
            case 1: {
                LOOT_POT_2.render();
                break;
            }
            case 2: {
                LOOT_POT_3.render();
                break;
            }
        }
    }

}
