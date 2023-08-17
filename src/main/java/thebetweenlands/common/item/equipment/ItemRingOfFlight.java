package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.IFlightCapability;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.network.serverbound.MessageFlightState;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfFlight extends ItemRing {
	public ItemRingOfFlight() {
		this.setMaxDamage(1800);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.ring.flight.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.bl.ring.flight", KeyBindRegistry.RADIAL_MENU.getDisplayName(), Minecraft.getMinecraft().gameSettings.keyBindJump.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			IFlightCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FLIGHT, null);
			if(cap != null) {
				cap.setFlightRing(true);
				if(!cap.canFlyWithoutRing(player) && cap.canFlyWithRing(player, stack)) {
					double flightHeight = 3.5D;
					if(player.world.isRemote || cap.isFlying()) {
						player.capabilities.allowFlying = true;
					}
					boolean isFlying = cap.isFlying();
					NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
					if(!entity.onGround) {
						if(isFlying) {
							cap.onFlightTick(player, stack, !nbt.getBoolean("ringActive"));

							nbt.setBoolean("ringActive", true);

							if(entity.isSneaking()) {
								entity.motionY = -0.2F;
							} else {
								double actualPosY = entity.posY;
								double height = this.getGroundHeight(player);
								Vec3d dir = new Vec3d(entity.getLookVec().x, 0, entity.getLookVec().z).normalize();

								if(player.moveForward > 0) {
									entity.motionX += dir.x * 0.02F;
									entity.motionZ += dir.z * 0.02F;
								}

								double my = 0.0D;
								boolean moveUp = false;
								if(player.world.isRemote) {
									if(player instanceof EntityPlayerSP) {
										moveUp = ((EntityPlayerSP)player).movementInput.jump;
									}
								}
								if(moveUp) {
									my = 0.1D;
								}
								double mul = ((height - (actualPosY - flightHeight)));
								entity.motionY = my * mul;
								if(actualPosY - flightHeight > height && entity.motionY > 0) {
									entity.motionY = 0;
								}
								if(actualPosY - flightHeight > height) {
									entity.motionY = -Math.min(actualPosY - flightHeight - height, 2.0D) / 8.0F;
								}
							}

							entity.fallDistance = 0.0F;

							if(!entity.onGround && entity.world.isRemote) {
								if(cap.getFlightTime() > 40) {
									BLParticles.LEAF_SWIRL.spawn(entity.world, entity.posX, entity.posY, entity.posZ, ParticleArgs.get().withData(400, 0.0F, entity));
								} else {
									for(int i = 0; i < 5; i++) {
										BLParticles.LEAF_SWIRL.spawn(entity.world, entity.posX, entity.posY, entity.posZ, ParticleArgs.get().withData(400, 1.0F - (cap.getFlightTime() + i / 5.0F) / 40.0F, entity));
									}
								}
							}
						} else {
							nbt.setBoolean("ringActive", false);
						}
					} else {
						cap.setFlying(false);
						nbt.setBoolean("ringActive", false);
					}
				} else {
					if(!player.world.isRemote) {
						NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
						nbt.setBoolean("ringActive", false);
						if(cap.isFlying()) {
							cap.setFlying(false);
						}
					}
					if(cap.isFlying() && !player.onGround && player.world.isRemote) {
						if(cap.getFlightTime() > 40) {
							BLParticles.LEAF_SWIRL.spawn(entity.world, entity.posX, entity.posY, entity.posZ, ParticleArgs.get().withData(400, 0.0F, entity));
						} else {
							for(int i = 0; i < 5; i++) {
								BLParticles.LEAF_SWIRL.spawn(entity.world, entity.posX, entity.posY, entity.posZ, ParticleArgs.get().withData(400, 1.0F - (cap.getFlightTime() + i / 5.0F) / 40.0F, entity));
							}
						}
					}
				}
			}
		}
	}

	private double getGroundHeight(EntityPlayer player) {
		RayTraceResult result = player.world.rayTraceBlocks(new Vec3d(player.posX, player.posY, player.posZ), new Vec3d(player.posX, player.posY - 64, player.posZ), true);
		if(result != null && result.typeOfHit == Type.BLOCK) {
			return result.hitVec.y;
		}
		return -512.0D;
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.player == null) {
			return;
		}
		
		EntityPlayer player = event.player;
		if(player.capabilities.isCreativeMode) {
			return;
		}
		
		IFlightCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FLIGHT, null);
		if(cap == null) {
			return;
		}
		
		if(cap.isFlying()) {
			cap.setFlightTime(cap.getFlightTime() + 1);
		}
		ItemStack flightRing = ItemStack.EMPTY;

		IEquipmentCapability equipmentCap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(equipmentCap != null) {
			IInventory inv = equipmentCap.getInventory(EnumEquipmentInventory.RING);
			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() == ItemRegistry.RING_OF_FLIGHT) {
					flightRing = stack;
					break;
				}
			}
		}

		if(!flightRing.isEmpty() && player.world.isRemote) {
			if(!cap.canFlyWithoutRing(player) && cap.canFlyWithRing(player, flightRing)) {
				if(event.phase == Phase.START) {
					player.capabilities.isFlying = false;
				} else {
					if(player.capabilities.isFlying) {
						cap.setFlying(!cap.isFlying());
						if(player == TheBetweenlands.proxy.getClientPlayer()) {
							TheBetweenlands.networkWrapper.sendToServer(new MessageFlightState(cap.isFlying()));
						}
					}
				}
				
				if(player != TheBetweenlands.proxy.getClientPlayer()) {
					if(cap.isFlying() && !player.onGround) {
						if(cap.getFlightTime() > 40) {
							BLParticles.LEAF_SWIRL.spawn(player.world, player.posX, player.posY, player.posZ, ParticleArgs.get().withData(400, 0.0F, player));
						} else {
							for(int i = 0; i < 5; i++) {
								BLParticles.LEAF_SWIRL.spawn(player.world, player.posX, player.posY, player.posZ, ParticleArgs.get().withData(400, 1.0F - (cap.getFlightTime() + i / 5.0F) / 40.0F, player));
							}
						}
					}
				}
			} else if(cap.isFlying()) {
				cap.setFlying(false);
				if(player == TheBetweenlands.proxy.getClientPlayer()) {
					TheBetweenlands.networkWrapper.sendToServer(new MessageFlightState(cap.isFlying()));
				} else {
					if(!player.onGround) {
						if(cap.getFlightTime() > 40) {
							BLParticles.LEAF_SWIRL.spawn(player.world, player.posX, player.posY, player.posZ, ParticleArgs.get().withData(400, 0.0F, player));
						} else {
							for(int i = 0; i < 5; i++) {
								BLParticles.LEAF_SWIRL.spawn(player.world, player.posX, player.posY, player.posZ, ParticleArgs.get().withData(400, 1.0F - (cap.getFlightTime() + i / 5.0F) / 40.0F, player));
							}
						}
					}
				}
			}
		}
		if(player == TheBetweenlands.proxy.getClientPlayer() && player.ticksExisted % 20 == 0) {
			TheBetweenlands.networkWrapper.sendToServer(new MessageFlightState(cap.isFlying()));
		}
		if(event.phase == Phase.END) {
			if(flightRing.isEmpty() || !cap.isFlying()) {
				if(cap.getFlightRing()) {
					if(!cap.canFlyWithoutRing(player)) {
						player.capabilities.isFlying = false;
						player.capabilities.allowFlying = false;
						if(player.world.isRemote) {
							player.capabilities.setFlySpeed(0.05F);
						}
					}
					cap.setFlightRing(false);
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("ringActive");
	}
}
