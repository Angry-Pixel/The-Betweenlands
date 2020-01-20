package thebetweenlands.common.handler;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.client.audio.PortalSound;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.teleporter.TeleporterHandler;

public class PlayerPortalHandler {
	public static final int MAX_PORTAL_TIME = 120;

	@SubscribeEvent
	public static void teleportCheck(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			EntityPlayer player = event.player;
	
			IPortalCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);
			if (cap != null) {
				if(cap.isInPortal()){
					BlockPos pos = new BlockPos(player.posX, player.posY + 0.5D, player.posZ);
					IBlockState state = player.world.getBlockState(pos);
	
					boolean inPortalBlock = false;
					if(state.getBlock() instanceof BlockTreePortal) {
						AxisAlignedBB aabb = state.getBoundingBox(player.world, pos);
						if(aabb != null && aabb.offset(pos).intersects(player.getEntityBoundingBox())) {
							inPortalBlock = true;
						}
					}
	
					if(inPortalBlock) {
						if(!cap.wasTeleported()) {
							if (cap.getTicksUntilTeleport() <= 0 || player.capabilities.isCreativeMode) {
								if(player.world instanceof WorldServer) {
									BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(player.world);
									AxisAlignedBB entityAabb = player.getEntityBoundingBox();
									List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, entityAabb, loc -> loc.intersects(entityAabb));
									LocationPortal portal = null;
									if(!portals.isEmpty()) {
										portal = portals.get(0);
									}
									int targetDim = BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
									if(portal != null && (portal.getOtherPortalPosition() != null || portal.hasTargetDimension())) {
										//Portal already linked, teleport to linked dimension
										targetDim = portal.getOtherPortalDimension();
									} else if (player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
										targetDim = BetweenlandsConfig.WORLD_AND_DIMENSION.portalDefaultReturnDimension;
									}
									if(targetDim != player.dimension) {
										WorldServer otherDim = ((WorldServer) player.world).getMinecraftServer().getWorld(targetDim);
										if(otherDim != null) {
											TeleporterHandler.transferToDim(player, otherDim);
										}
									}
								}
								player.timeUntilPortal = 10;
								cap.setInPortal(false);
								cap.setTicksUntilTeleport(MAX_PORTAL_TIME);
							} else {
								cap.setTicksUntilTeleport(cap.getTicksUntilTeleport() - 1);
							}
						}
					} else {
						cap.setTicksUntilTeleport(MAX_PORTAL_TIME);
						cap.setInPortal(false);
						cap.setWasTeleported(false);
					}
				} else {
					cap.setTicksUntilTeleport(MAX_PORTAL_TIME);
					if(!cap.isInPortal()) {
						cap.setWasTeleported(false);
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;

			if (player != null) {
				IPortalCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);
				if (cap != null) {

					boolean renderPortalEffect = cap.isInPortal() && !cap.wasTeleported();

					if (renderPortalEffect) {
						int timer = cap.getTicksUntilTeleport();
						if (timer < MAX_PORTAL_TIME) {
							player.closeScreen();

							if (timer == MAX_PORTAL_TIME - 1) {
								Minecraft.getMinecraft().getSoundHandler().playSound(new PortalSound(SoundRegistry.PORTAL_TRIGGER, SoundCategory.BLOCKS, player));
							}

							if (timer == 2) {
								player.playSound(SoundRegistry.PORTAL_TRAVEL, 1.0F, 0.8F);
							}
						}
					}

					if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
						WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();

						if (!renderPortalEffect) {
							shader.setSwirlAngle(0.0F);
						} else {
							float swirl = shader.getSwirlAngle(1);
							if (swirl < 2) {
								shader.setSwirlAngle(swirl + (swirl * 0.055F) + 0.0005F);
							} else {
								shader.setSwirlAngle(swirl + ((swirl * 0.055F) / (swirl - 1)) + 0.0005F);
							}
						}
					}
				}
			}
		}
	}
}
