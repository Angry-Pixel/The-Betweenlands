package thebetweenlands.client.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.concurrent.FutureTask;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.Matrix;

public final class WeedwoodRowboatHandler {
    public static final WeedwoodRowboatHandler INSTANCE = new WeedwoodRowboatHandler();

    private static final Minecraft MC = Minecraft.getMinecraft();

    private boolean isPlayerInRowboat;

    private MouseHelper lastMouseHelper;

    private View view = View.ROWBOAT;

    private int lastView;

    private WeedwoodRowboatHandler() {}

    public void init() {
        try {
            Field mods = ReflectionHelper.findField(Field.class, "modifiers");
            Field scheduledTasks = ReflectionHelper.findField(Minecraft.class, "field_152351_aB", "scheduledTasks");
            mods.set(scheduledTasks, scheduledTasks.getModifiers() & ~Modifier.FINAL);
            scheduledTasks.set(MC, new ArrayDeque<FutureTask<?>>() {
                @Override
                public boolean isEmpty() {
                    if (super.isEmpty()) {
                        onMacgyveredGameLoop();
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Any problem can be solved with a little ingenuity, he said...", e);
        }
    }

    public void onPilotEnterWeedwoodRowboat(Entity pilot) {
        if (pilot == MC.thePlayer) {
            enterRowboatPerspective();
        }
    }

    public void onPilotExitWeedwoodRowboat(EntityWeedwoodRowboat rowboat, Entity pilot) {
        if (pilot == MC.thePlayer) {
            double dx = rowboat.posX - pilot.posX;
            double dy = rowboat.posY + rowboat.height - (pilot.posY + pilot.getEyeHeight());
            double dz = rowboat.posZ - pilot.posZ;
            double h = MathHelper.sqrt_double(dx * dx + dz * dz);
            pilot.rotationPitch = (float) -Math.toDegrees(MathHelper.atan2(dy, h));
            float yaw = (float) Math.toDegrees(MathHelper.atan2(dz, dx)) - 90;
            pilot.rotationYaw = yaw;
            pilot.setRotationYawHead(yaw);
            pilot.setRenderYawOffset(yaw);
            leaveRowboatPerspective();
        }
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Entity entity = MC.getRenderViewEntity();
            if (entity instanceof RowboatCam && isPlayerInRowboat && (!MC.inGameHasFocus || !Display.isActive())) {
                ((RowboatCam) entity).update(MC.thePlayer.getRidingEntity(), event.renderTickTime);
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        Entity entity = MC.getRenderViewEntity();
        if (entity instanceof RowboatCam) {
            RowboatCam cam = (RowboatCam) entity;
            cam.dolly = MathHelper.clamp_double(cam.dolly - Math.signum(event.getDwheel()) * (cam.dolly - 1) * 0.1, 1, 10);
            event.setCanceled(true);
            int key = event.getButton() - 100;
            KeyBinding.setKeyBindState(key, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
                KeyBinding.onTick(key);
            }
        }
    }

    @SubscribeEvent
    public void onTickAfterKeyboard(InputEvent.MouseInputEvent event) {
        int v = MC.gameSettings.thirdPersonView;
        if (isPlayerInRowboat && v != lastView) {
            if (v != 0 && view == View.FIRST_PERSON) {
                enterRowboatPerspective();
                lastView = 0;
            } else {
                leaveRowboatPerspective();
                lastView = v;
            }
        }
    }

    @SubscribeEvent
    public void onHandRender(RenderHandEvent event) {
        if (MC.getRenderViewEntity() instanceof RowboatCam) {
            event.setCanceled(true);
        }
    }

    private void enterRowboatPerspective() {
        Entity entity = MC.thePlayer.getRidingEntity();
        MC.setRenderViewEntity(new RowboatCam(MC.theWorld, entity == null ? 0 : entity.rotationYaw, 30));
        lastMouseHelper = MC.mouseHelper;
        MC.mouseHelper = new RowboatCamUpdater();
        view = View.ROWBOAT;
        MC.gameSettings.thirdPersonView = 0;
    }

    private void leaveRowboatPerspective() {
        MC.setRenderViewEntity(null);
        if (MC.mouseHelper instanceof RowboatCamUpdater) {
            if (lastMouseHelper == null) {
                MC.mouseHelper = new MouseHelper();
            } else {
                MC.mouseHelper = lastMouseHelper;
            }
        }
        lastMouseHelper = null;
        GuiIngameForge.renderCrosshairs = true;
        view = View.FIRST_PERSON;
        MC.gameSettings.thirdPersonView = 0;
    }

    // Do this so that when the player enters the rowboat they are rotated correctly after a SetPassengers packet is recieved
    private void onMacgyveredGameLoop() {
        EntityPlayerSP player = MC.thePlayer;
        if (player == null) {
            isPlayerInRowboat = false;
            return;
        }
        Entity riding = player.getRidingEntity();
        if (riding instanceof EntityWeedwoodRowboat && riding.getControllingPassenger() == player) {
            if (!isPlayerInRowboat) {
                player.prevRotationPitch = player.rotationPitch = 0;
                player.prevRotationYawHead = player.rotationYawHead = player.prevRotationYaw = player.rotationYaw = MathHelper.wrapDegrees(riding.rotationYaw - 180);
                riding.updatePassenger(player);
                player.prevRenderYawOffset = player.renderYawOffset;
                player.prevPosX = player.lastTickPosX = player.posX;
                player.prevPosY = player.lastTickPosY = player.posY;
                player.prevPosZ = player.lastTickPosZ = player.posZ;
                isPlayerInRowboat = true;
            }
        } else {
            isPlayerInRowboat = false;
        }
    }

    private enum View {
        FIRST_PERSON,
        ROWBOAT;
    }

    private class RowboatCamUpdater extends MouseHelper {
        @Override
        public void mouseXYChange() {
            boolean reset = true;
            Entity entity = MC.getRenderViewEntity();
            if (entity instanceof RowboatCam) {
                RowboatCam cam = (RowboatCam) entity;
                EntityPlayer player = MC.thePlayer;
                Entity riding = player.getRidingEntity();
                if (riding instanceof EntityWeedwoodRowboat) {
                    int deltaX = Mouse.getDX();
                    int deltaY = Mouse.getDY();
                    cam.prevRotationYaw = cam.rotationYaw = MathHelper.wrapDegrees(cam.rotationYaw + deltaX * 0.15F);
                    cam.prevRotationPitch = cam.rotationPitch = MathHelper.clamp_float(cam.rotationPitch - deltaY * 0.15F, 0, 90);
                    cam.update(riding, MC.getRenderPartialTicks());
                    GuiIngameForge.renderCrosshairs = false;
                    reset = false;
                }
            }
            if (reset) {
                leaveRowboatPerspective();
            }
        }
    }

    private class RowboatCam extends Entity {
        private final Matrix mat = new Matrix();

        public double dolly = 5;

        public RowboatCam(World world, float yaw, float pitch) {
            super(world);
            this.prevRotationYaw = rotationYaw = yaw;
            this.prevRotationPitch = rotationPitch = pitch;
        }

        @Override
        public float getEyeHeight() {
            return 0;
        }

        @Override
        protected void entityInit() {}

        public void update(Entity rowboat, float delta) {
            double x = rowboat.lastTickPosX + (rowboat.posX - rowboat.lastTickPosX) * delta;
            double y = rowboat.lastTickPosY + (rowboat.posY - rowboat.lastTickPosY) * delta;
            double z = rowboat.lastTickPosZ + (rowboat.posZ - rowboat.lastTickPosZ) * delta;
            final double offsetY = 1.12;
            mat.setIdentity();
            mat.translate(x, y + offsetY, z);
            mat.rotate(-rotationYaw * MathUtils.DEG_TO_RAD, 0, 1, 0);
            mat.rotate(rotationPitch * MathUtils.DEG_TO_RAD, 1, 0, 0);
            mat.translate(0, 0, -getDistance(worldObj, x, y + offsetY, z, rotationYaw, rotationPitch));
            Vec3d point = mat.transform(Vec3d.ZERO);
            lastTickPosX = prevPosX = posX = point.xCoord;
            lastTickPosY = prevPosY = posY = point.yCoord;
            lastTickPosZ = prevPosZ = posZ = point.zCoord;
        }

        private double getDistance(World world, double x, double y, double z, float yaw, float pitch) {
            double extent = dolly;
            float cosPitch = MathHelper.cos(pitch * MathUtils.DEG_TO_RAD);
            double extentX = -MathHelper.sin(yaw * MathUtils.DEG_TO_RAD) * cosPitch * extent;
            double extentZ = MathHelper.cos(yaw * MathUtils.DEG_TO_RAD) * cosPitch * extent;
            double extentY = -MathHelper.sin(pitch * MathUtils.DEG_TO_RAD) * extent;
            for (int zyx = 0; zyx < 8; zyx++) {
                float dx = ((zyx & 1) * 2 - 1) * 0.1F;
                float dy = ((zyx >> 1 & 1) * 2 - 1) * 0.1F;
                float dz = ((zyx >> 2 & 1) * 2 - 1) * 0.1F;
                RayTraceResult vector = world.rayTraceBlocks(new Vec3d(x + dx, y + dy, z + dz), new Vec3d(x - extentX + dx, y - extentY + dy, z - extentZ + dz), false, true, false);
                if (vector != null) {
                    double distance = vector.hitVec.distanceTo(new Vec3d(x, y, z));
                    if (distance < extent) {
                        extent = distance;
                    }
                }
            }
            return extent;
        }

        @Override
        protected void readEntityFromNBT(NBTTagCompound compound) {}

        @Override
        protected void writeEntityToNBT(NBTTagCompound compound) {}
    }
}
