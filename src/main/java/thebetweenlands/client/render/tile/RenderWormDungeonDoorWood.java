package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelWormDungeonDoorWood;
import thebetweenlands.common.block.structure.BlockWormDungeonDoorWood;
import thebetweenlands.common.tile.TileEntityWormDungeonDoorWood;
import thebetweenlands.util.StatePropertyHelper;

@SideOnly(Side.CLIENT)
public class RenderWormDungeonDoorWood extends TileEntitySpecialRenderer<TileEntityWormDungeonDoorWood> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/worm_dungeon_door_wood.png");
    private final ModelWormDungeonDoorWood model = new ModelWormDungeonDoorWood();

    @Override
    public void render(TileEntityWormDungeonDoorWood te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        EnumFacing facing = StatePropertyHelper.getStatePropertySafely(te, BlockWormDungeonDoorWood.class, BlockWormDungeonDoorWood.FACING, EnumFacing.NORTH);

        bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GlStateManager.scale(-1, -1, 1);

        switch (facing) {
            case NORTH:
                GlStateManager.rotate(0F, 0.0F, 1F, 0F);
                break;
            case SOUTH:
                GlStateManager.rotate(180F, 0.0F, 1F, 0F);
                break;
            case WEST:
                GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
                break;
            case EAST:
                GlStateManager.rotate(90F, 0.0F, 1F, 0F);
                break;
        }

        model.render();
        GlStateManager.popMatrix();
    }
}