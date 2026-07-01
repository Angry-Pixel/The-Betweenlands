package thebetweenlands.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.client.renderer.entity.rowboat.WeedwoodRowboatRenderer;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.rowboat.WeedwoodRowboat;
import thebetweenlands.util.Matrix;

import javax.annotation.Nullable;
import java.util.Objects;

public class WeedwoodRowboatHandler {

	public static final WeedwoodRowboatHandler INSTANCE = new WeedwoodRowboatHandler();

	private static final Minecraft MC = Minecraft.getInstance();

	private boolean isPlayerInRowboat;

	@Nullable
	private MouseHandler lastMouseHelper;

	private View view = View.ROWBOAT;
	@Nullable
	private View changedTo = null;
	@Nullable
	private CameraType lastCameraType = null;

	public void init() {
		NeoForge.EVENT_BUS.addListener(this::hijackRenderer);
		NeoForge.EVENT_BUS.addListener(this::changeBoatPerspective);
		NeoForge.EVENT_BUS.addListener(this::dontRenderHandWhenBoated);
		NeoForge.EVENT_BUS.addListener(this::hackPlayerTick);
		NeoForge.EVENT_BUS.addListener(this::onOverlayRenderPre);
		NeoForge.EVENT_BUS.addListener(this::onOverlayRenderPost);
		NeoForge.EVENT_BUS.addListener(this::updateBoatCamera);
		NeoForge.EVENT_BUS.addListener(this::zoomOnBoat);
	}

	private void hijackRenderer(RenderPlayerEvent.Pre event) {
		if (event.getEntity().getVehicle() instanceof WeedwoodRowboat rowboat && event.getEntity() instanceof AbstractClientPlayer player && rowboat.getControllingPassenger() == player) {
			if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(rowboat) instanceof WeedwoodRowboatRenderer renderer) {
				event.setCanceled(true);
				renderer.setupAndRenderRower(rowboat, player, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
			}
		}
	}

	public void onPilotEnterWeedwoodRowboat(Entity pilot) {
		if (pilot == MC.player) {
			if (!MC.options.getCameraType().isFirstPerson()) {
				this.lastCameraType = MC.options.getCameraType();
				this.changedTo = View.ROWBOAT;
			}
			if ((this.changedTo != null && this.changedTo == View.ROWBOAT) || BetweenlandsConfig.rowboatView)
				this.enterRowboatPerspective();
			else
				this.leaveRowboatPerspective();
		}
	}

	public void onPilotExitWeedwoodRowboat(WeedwoodRowboat rowboat, Entity pilot) {
		if (pilot == MC.player) {
			double dx = rowboat.getX() - pilot.getX();
			double dy = rowboat.getY() + rowboat.getBbHeight() - (pilot.getY() + pilot.getEyeHeight());
			double dz = rowboat.getZ() - pilot.getZ();
			double h = Mth.sqrt((float) (dx * dx + dz * dz));
			pilot.setXRot((float) -Math.toDegrees(Mth.atan2(dy, h)));
			float yaw = (float) Math.toDegrees(Mth.atan2(dz, dx)) - 90;
			pilot.setYRot(yaw);
			pilot.setYHeadRot(yaw);
			pilot.setYBodyRot(yaw);
			this.leaveRowboatPerspective();
			if (this.lastCameraType != null && this.changedTo != null && this.changedTo == View.ROWBOAT) {
				MC.options.setCameraType(this.lastCameraType);
			}
			this.changedTo = null;
		}
	}

	public void updateBoatCamera(RenderFrameEvent.Pre event) {
		Entity entity = MC.getCameraEntity();
		if (entity instanceof RowboatCam cam && this.isPlayerInRowboat && !MC.isWindowActive()) {
			cam.tick(MC.player.getVehicle(), event.getPartialTick().getGameTimeDeltaPartialTick(true));
		}
	}

	@Nullable
	private Entity prevRenderViewEntity = null;

	public void onOverlayRenderPre(RenderGuiLayerEvent.Pre event) {
		if (this.isPlayerInRowboat) {
			if (event.getName() != VanillaGuiLayers.CROSSHAIR) { //first layer rendered
				//Set render view entity to player during GUI overlay rendering so that HUD renders
				this.prevRenderViewEntity = MC.getCameraEntity();
				MC.setCameraEntity(MC.player);
			} else {
				event.setCanceled(true);
			}
		}
	}

	public void onOverlayRenderPost(RenderGuiLayerEvent.Post event) {
		if (this.isPlayerInRowboat) {
			if (MC.getCameraEntity() == MC.player || this.prevRenderViewEntity instanceof RowboatCam) {
				MC.setCameraEntity(this.prevRenderViewEntity);
			}
		}
	}

	public void zoomOnBoat(InputEvent.MouseScrollingEvent event) {
		Entity entity = MC.getCameraEntity();
		if (entity instanceof RowboatCam cam && event.getScrollDeltaY() != 0) {
			cam.dolly = Mth.clamp(cam.dolly - Math.signum(event.getScrollDeltaY()) * (cam.dolly - 1) * 0.1, 1, 10);
			event.setCanceled(true);
		}
	}

	public void changeBoatPerspective(InputEvent.Key event) {
		if (this.isPlayerInRowboat) {
			if (MC.options.keyTogglePerspective.consumeClick() && event.getAction() == InputConstants.PRESS) {
				if (this.view == View.FIRST_PERSON) {
					this.enterRowboatPerspective();
					this.view = View.ROWBOAT;
					BetweenlandsConfig.rowboatView = true;
				} else {
					this.leaveRowboatPerspective();
					this.view = View.FIRST_PERSON;
					BetweenlandsConfig.rowboatView = false;
				}
				this.changedTo = this.view;
			}
		}
	}

	public void dontRenderHandWhenBoated(RenderHandEvent event) {
		if (MC.player.getVehicle() instanceof WeedwoodRowboat) {
			event.setCanceled(true);
		}
	}

	private void enterRowboatPerspective() {
		Entity entity = MC.player.getVehicle();
		MC.setCameraEntity(new RowboatCam(MC.level, entity == null ? 0 : entity.getYRot(), 30));
		this.lastMouseHelper = MC.mouseHandler;
		MC.mouseHandler = new RowboatCamUpdater(MC);
		MC.mouseHandler.setup(MC.getWindow().getWindow());
		MC.mouseHandler.grabMouse();
		this.view = View.ROWBOAT;
		MC.options.setCameraType(CameraType.FIRST_PERSON);
	}

	private void leaveRowboatPerspective() {
		MC.setCameraEntity(MC.player);
		if (MC.mouseHandler instanceof RowboatCamUpdater) {
			MC.mouseHandler = Objects.requireNonNullElseGet(this.lastMouseHelper, () -> new MouseHandler(MC));
			MC.mouseHandler.setup(MC.getWindow().getWindow());
			MC.mouseHandler.grabMouse();
		}
		this.lastMouseHelper = null;
		this.view = View.FIRST_PERSON;
		MC.options.setCameraType(CameraType.FIRST_PERSON);
	}

	private void hackPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof LocalPlayer player) {
			Entity riding = player.getVehicle();
			if (riding instanceof WeedwoodRowboat boat && riding.getControllingPassenger() == player) {
				if (!this.isPlayerInRowboat) {
					player.setXRot(player.xRotO = 0);
					player.setYRot(player.yHeadRot = player.yHeadRotO = player.yRotO = Mth.wrapDegrees(riding.getYRot() - 180));
					boat.positionRider(player);
					player.yBodyRotO = player.yBodyRot;
					player.xo = player.xOld = player.getX();
					player.yo = player.yOld = player.getY();
					player.zo = player.zOld = player.getZ();
					this.isPlayerInRowboat = true;
				}
			} else {
				this.isPlayerInRowboat = false;
			}
		}
	}

	private enum View {
		FIRST_PERSON,
		ROWBOAT;
	}

	private class RowboatCamUpdater extends MouseHandler {

		public RowboatCamUpdater(Minecraft minecraft) {
			super(minecraft);
		}

		@Override
		public void handleAccumulatedMovement() {
			if (MC.player != null) {
				boolean reset = true;
				Entity entity = MC.getCameraEntity();
				if (entity instanceof RowboatCam cam) {
					var event = ClientHooks.getTurnPlayerValues(MC.options.sensitivity().get(), MC.options.smoothCamera);
					double d2 = event.getMouseSensitivity() * 0.6F + 0.2F;
					double d3 = d2 * d2 * d2;
					double d4 = d3 * 8.0D;
					Player player = MC.player;
					Entity riding = player.getVehicle();
					if (riding instanceof WeedwoodRowboat) {
						double deltaX = this.accumulatedDX * d4;
						double deltaY = this.accumulatedDY * d4;
						if (MC.isWindowActive() && this.isMouseGrabbed()) {
							cam.setYRot(cam.yRotO = (float) Mth.wrapDegrees(cam.getYRot() + deltaX * 0.15F));
							cam.setXRot(cam.xRotO = (float) Mth.clamp(cam.getXRot() - deltaY * 0.15F, 0, 90));
						}
						cam.tick(riding, MC.getTimer().getGameTimeDeltaPartialTick(true));
						reset = false;
					}
				}
				if (reset) {
					WeedwoodRowboatHandler.this.leaveRowboatPerspective();
				}
			}

			this.accumulatedDX = 0.0;
			this.accumulatedDY = 0.0;
		}
	}

	private static class RowboatCam extends Entity {
		private final Matrix mat = new Matrix();

		public double dolly = 5;

		public RowboatCam(Level level, float yaw, float pitch) {
			super(EntityType.PLAYER, level);
			this.setRot(yaw, pitch);
			this.yRotO = yaw;
			this.xRotO = pitch;
			this.setBoundingBox(new AABB(0, 0, 0, 0, 0, 0));
		}

		@Override
		protected void defineSynchedData(SynchedEntityData.Builder builder) {

		}

		@Override
		public EntityDimensions getDimensions(Pose pose) {
			return EntityDimensions.fixed(0.0F, 0.0F);
		}

		public void tick(Entity rowboat, float partialTick) {
			double x = Mth.lerp(partialTick, rowboat.xo, rowboat.getX());
			double y = Mth.lerp(partialTick, rowboat.yo, rowboat.getY());
			double z = Mth.lerp(partialTick, rowboat.zo, rowboat.getZ());
			final double offsetY = -0.25;
			this.mat.setIdentity();
			this.mat.translate(x, y + offsetY, z);
			this.mat.rotate(-this.getYRot() * Mth.DEG_TO_RAD, 0, 1, 0);
			this.mat.rotate(this.getXRot() * Mth.DEG_TO_RAD, 1, 0, 0);
			this.mat.translate(0, 0, -this.getDistance(this.level(), x, y + offsetY, z, this.getYRot(), this.getXRot()));
			Vec3 point = this.mat.transform(Vec3.ZERO);
			this.xOld = this.xo = point.x;
			this.yOld = this.yo = point.y;
			this.zOld = this.zo = point.z;
			this.setPos(point);
		}

		@Override
		protected AABB makeBoundingBox() {
			return new AABB(0, 0, 0, 0, 0, 0);
		}

		private double getDistance(Level level, double x, double y, double z, float yaw, float pitch) {
			double extent = this.dolly;
			float cosPitch = Mth.cos(pitch * Mth.DEG_TO_RAD);
			double extentX = -Mth.sin(yaw * Mth.DEG_TO_RAD) * cosPitch * extent;
			double extentZ = Mth.cos(yaw * Mth.DEG_TO_RAD) * cosPitch * extent;
			double extentY = -Mth.sin(pitch * Mth.DEG_TO_RAD) * extent;
			for (int zyx = 0; zyx < 8; zyx++) {
				float dx = ((zyx & 1) * 2 - 1) * 0.1F;
				float dy = ((zyx >> 1 & 1) * 2 - 1) * 0.1F;
				float dz = ((zyx >> 2 & 1) * 2 - 1) * 0.1F;
				HitResult vector = level.clip(new ClipContext(new Vec3(x + dx, y + dy, z + dz), new Vec3(x - extentX + dx, y - extentY + dy, z - extentZ + dz), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
				if (vector.getType() != HitResult.Type.MISS) {
					double distance = vector.getLocation().distanceTo(new Vec3(x, y, z));
					if (distance < extent) {
						extent = distance;
					}
				}
			}
			return extent;
		}

		@Override
		protected void readAdditionalSaveData(CompoundTag compound) {

		}

		@Override
		protected void addAdditionalSaveData(CompoundTag compound) {

		}

		@Override
		public boolean isAttackable() {
			return false;
		}

		@Override
		public boolean skipAttackInteraction(Entity entity) {
			return true;
		}
	}
}
