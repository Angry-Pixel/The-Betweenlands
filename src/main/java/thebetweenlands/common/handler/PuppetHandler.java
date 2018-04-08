package thebetweenlands.common.handler;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.render.entity.RenderSwordEnergy;
import thebetweenlands.client.render.entity.layer.LayerPuppetOverlay;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFollowTarget;
import thebetweenlands.common.entity.ai.puppet.EntityAIGoTo;
import thebetweenlands.common.entity.ai.puppet.EntityAIPuppet;
import thebetweenlands.common.entity.ai.puppet.EntityAIStay;
import thebetweenlands.common.item.equipment.ItemRingOfRecruitment;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.RenderUtils;

public class PuppetHandler {
	private PuppetHandler() { }

	@SubscribeEvent
	public static void onUpdateLiving(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity instanceof EntityLiving && entity.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
			IPuppetCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
			EntityLiving living = (EntityLiving) entity;

			if(cap.hasPuppeteer()) {
				if(!entity.world.isRemote) {
					cap.setRemainingTicks(cap.getRemainingTicks() - 1);

					if(cap.getRemainingTicks() <= 0 || (cap.getPuppeteer() != null && !ItemRingOfRecruitment.isRingActive(cap.getPuppeteer()))) {
						cap.setPuppeteer(null);
						cap.setRemainingTicks(0);
						living.setAttackTarget(null);
						living.setRevengeTarget(null);
						EntityAIPuppet.removePuppetAI(living.tasks);
						EntityAIPuppet.removePuppetAI(living.targetTasks);
					} else {
						if(EntityAIPuppet.getPuppetAI(living.targetTasks) == null) {
							/*EntityAINearestAttackableTarget<EntityLiving> aiTarget = new EntityAINearestAttackableTarget<EntityLiving>(creature, EntityLiving.class, 0, true, true, living -> {
								if(living.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
									IPuppetCapability targetCap = living.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
									if(targetCap.getPuppeteer() == cap.getPuppeteer()) {
										//Don't attack puppets from same owner
										return false;
									}
								}
								return living instanceof EntityMob || living instanceof IMob;
							});
							aiTarget.setMutexBits(1); //01*/

							if(living instanceof EntityCreature) {
								EntityAIAttackMelee aiMelee = new EntityAIAttackMelee((EntityCreature) living, 1.2D, true);
								aiMelee.setMutexBits(2); //10
								
								EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), living, living.targetTasks,
										ImmutableList.of(aiMelee));
							} else {
								EntityAIAttackOnCollide aiAttack = new EntityAIAttackOnCollide(living, true);
								aiAttack.setMutexBits(2); //10
								
								EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), living, living.targetTasks,
										ImmutableList.of(aiAttack));
							}
						}
						
						if(EntityAIPuppet.getPuppetAI(living.tasks) == null) {
							EntityAIStay aiStay = new EntityAIStay(living);
							aiStay.setMutexBits(3); //11

							EntityAIGoTo aiGoTo = new EntityAIGoTo(living, 1.2D);
							aiGoTo.setMutexBits(3);

							EntityAIFollowTarget aiFollow = new EntityAIFollowTarget(living, () -> {
								Entity puppeteer = cap.getPuppeteer();
								if(puppeteer instanceof EntityLivingBase) {
									return (EntityLivingBase) puppeteer;
								}
								return null;
							}, 1.2D, 10.0F, 2.0F);
							aiFollow.setMutexBits(1);

							EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), living, living.tasks,
									ImmutableList.of(aiStay, aiFollow, aiGoTo));
						}
					}
				} else {
					if(entity.world.rand.nextInt(5) == 0) {
						BLParticles.SPAWNER.spawn(living.world, living.posX + living.motionX * 2, living.posY + living.height / 2.0D, living.posZ + living.motionZ * 2,
								ParticleArgs.get().withMotion(
										living.motionX + (living.world.rand.nextFloat() - 0.5F) / 8.0F * entity.width, 
										(living.world.rand.nextFloat() - 0.5F) / 8.0F * entity.height, 
										living.motionZ + (living.world.rand.nextFloat() - 0.5F) / 8.0F * entity.width
										).withData(40).withColor(0.2F, 0.8F, 0.4F, 1));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		EntityLivingBase attackedEntity = event.getEntityLiving();
		if(!attackedEntity.world.isRemote) {
			DamageSource source = event.getSource();

			if(source.getTrueSource() instanceof EntityPlayer && source.getTrueSource().hasCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null)) {
				IPuppeteerCapability cap = source.getTrueSource().getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
				List<Entity> puppets = cap.getPuppets();

				if(!puppets.contains(attackedEntity)) {
					for(Entity entity : puppets) {
						if(entity instanceof EntityLiving) {
							((EntityLiving) entity).setAttackTarget(attackedEntity);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends EntityAIBase> T getAI(Class<T> cls, EntityAITasks tasks) {
		for(EntityAITaskEntry entry : tasks.taskEntries) {
			if(cls == entry.action.getClass()) {
				return (T) entry.action;
			}
		}
		return null;
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();

		if(event.getHand() == EnumHand.MAIN_HAND && player != null && target instanceof EntityLiving && ItemRingOfRecruitment.isRingActive(player) && target.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
			IPuppetCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
			EntityLiving living = (EntityLiving) target;

			if(cap.hasPuppeteer()) {
				Entity puppeteer = cap.getPuppeteer();

				if(player == puppeteer) {
					if(player.isSneaking()) {
						if(!player.world.isRemote) {
							cap.setRemainingTicks(0);
						}
						player.swingArm(EnumHand.MAIN_HAND);
					} else {
						if(!player.world.isRemote){
							EntityAIPuppet puppetAI = EntityAIPuppet.getPuppetAI(living.tasks);
							if(puppetAI != null) {
								EntityAIStay aiStay = getAI(EntityAIStay.class, puppetAI.getSubTasks());
								if(aiStay != null) {
									aiStay.setStay(!aiStay.getStay());
									living.setAttackTarget(null);
								}
							}
						}
						player.swingArm(EnumHand.MAIN_HAND);
					}
				}
			} else if(!player.world.isRemote) {
				if(ItemRingOfRecruitment.isRingActive(player) && player.hasCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null)) {
					IPuppeteerCapability capPlayer = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
					if(capPlayer.getActivatingEntity() == null) {
						capPlayer.setActivatingEntity(living);
						capPlayer.setActivatingTicks(0);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerUpdate(PlayerTickEvent event) {
		if(event.phase == Phase.END && event.player.hasCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null)) {
			IPuppeteerCapability cap = event.player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
			Entity activatingEntity = cap.getActivatingEntity();

			if(activatingEntity instanceof EntityLiving) {
				EntityLiving living = (EntityLiving) activatingEntity;

				if(!event.player.world.isRemote) {
					if(living.getDistance(event.player) > 5.0D) {
						cap.setActivatingEntity(null);
						cap.setActivatingTicks(0);
					} else {
						cap.setActivatingTicks(cap.getActivatingTicks() + 1);

						if(cap.getActivatingTicks() > living.getMaxHealth()) {
							if(ItemRingOfRecruitment.isRingActive(event.player) && living.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
								IPuppetCapability puppetCap = living.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

								if(puppetCap.getPuppeteer() == null) {
									puppetCap.setPuppeteer(event.player);
									puppetCap.setRemainingTicks(12000);
								}
							}

							cap.setActivatingEntity(null);
							cap.setActivatingTicks(0);
						}
					}
				} else {
					Vec3d vec = new Vec3d(living.posX - event.player.posX, (living.posY + living.getEyeHeight() * 0.8F) - (event.player.posY + event.player.getEyeHeight() * 0.8F), living.posZ - event.player.posZ);
					vec = vec.normalize();
					vec = vec.addVector((event.player.world.rand.nextFloat() - 0.5F) / 3.0F, 
							(event.player.world.rand.nextFloat() - 0.5F) / 3.0F, 
							(event.player.world.rand.nextFloat() - 0.5F) / 3.0F);
					vec = vec.normalize();
					double dist = event.player.getDistance(living);
					vec = vec.scale(dist / 15.0F);
					BLParticles.SPAWNER.spawn(event.player.world, event.player.posX, event.player.posY + event.player.getEyeHeight() * 0.8F, event.player.posZ, 
							ParticleArgs.get().withData(40).withColor(0.2F, 0.8F, 0.4F, 1).withMotion(vec.x, vec.y, vec.z));
				}

				event.player.motionX *= 0.05D;
				event.player.motionZ *= 0.05D;
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getHand() == EnumHand.MAIN_HAND && event instanceof PlayerInteractEvent.RightClickBlock && ItemRingOfRecruitment.isRingActive(event.getEntityPlayer())) {
			EntityPlayer player = event.getEntityPlayer();

			if(player.hasCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null)) {
				IPuppeteerCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
				List<Entity> puppets = cap.getPuppets();
				BlockPos target = event.getPos().offset(event.getFace());

				boolean ordered = false;

				for(Entity puppet : puppets) {
					if(puppet instanceof EntityLiving) {
						EntityLiving living = (EntityLiving) puppet;
						if(!player.world.isRemote) {
							EntityAIPuppet puppetAI = EntityAIPuppet.getPuppetAI(living.tasks);
							if(puppetAI != null) {
								EntityAIGoTo aiGoTo = getAI(EntityAIGoTo.class, puppetAI.getSubTasks());
								if(aiGoTo != null) {
									aiGoTo.setTarget(target);
								}
							}
							living.setAttackTarget(null);
						}
						ordered = true;
					}
				}

				if(ordered) {
					player.swingArm(EnumHand.MAIN_HAND);

					if(player.world.isRemote) {
						for(int i = 0; i < 4; i++) {
							BLParticles.SPAWNER.spawn(player.world, target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D,
									ParticleArgs.get().withMotion(
											(player.world.rand.nextFloat() - 0.5F) / 16.0F, 
											(player.world.rand.nextFloat() - 0.5F) / 16.0F, 
											(player.world.rand.nextFloat() - 0.5F) / 16.0F
											).withData(30).withColor(0.2F, 0.8F, 0.25F, 1));
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderLivingPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
		EntityLivingBase living = event.getEntity();

		if(living.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
			IPuppetCapability cap = living.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
			if(cap.hasPuppeteer()) {
				if(!RenderUtils.doesRendererHaveLayer(event.getRenderer(), LayerPuppetOverlay.class, false)) {
					event.getRenderer().addLayer(new LayerPuppetOverlay(event.getRenderer()));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderLivingPost(RenderLivingEvent.Post<EntityLivingBase> event) {
		EntityLivingBase living = event.getEntity();

		if(living.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
			IPuppetCapability cap = living.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
			if(cap.hasPuppeteer()) {
				Entity puppeteer = cap.getPuppeteer();
				if(puppeteer != null) {
					event.getRenderer().bindTexture(LayerPuppetOverlay.OVERLAY_TEXTURE);

					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.loadIdentity();
					GlStateManager.translate((living.ticksExisted + WorldRenderHandler.getPartialTicks()) / 40.0F, 0, 0.0F);
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
					GlStateManager.enableBlend();
					GlStateManager.color(1, 1, 1, 0.25F);
					GlStateManager.disableLighting();
					GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.enableTexture2D();
					GlStateManager.disableCull();
					GlStateManager.depthMask(false);

					double dx = puppeteer.lastTickPosX + (puppeteer.posX - puppeteer.lastTickPosX) * WorldRenderHandler.getPartialTicks() - (living.lastTickPosX + (living.posX - living.lastTickPosX) * WorldRenderHandler.getPartialTicks());
					double dy = -living.height / 2.0F + puppeteer.height / 2.0D + puppeteer.lastTickPosY + (puppeteer.posY - puppeteer.lastTickPosY) * WorldRenderHandler.getPartialTicks() - (living.lastTickPosY + (living.posY - living.lastTickPosY) * WorldRenderHandler.getPartialTicks());
					double dz = puppeteer.lastTickPosZ + (puppeteer.posZ - puppeteer.lastTickPosZ) * WorldRenderHandler.getPartialTicks() - (living.lastTickPosZ + (living.posZ - living.lastTickPosZ) * WorldRenderHandler.getPartialTicks());

					double sx = event.getX();
					double sy = event.getY() + living.height / 2.0F;
					double sz = event.getZ();

					float sw = 0.03F;
					float ew = 0.01F;

					double ticks = (living.ticksExisted + WorldRenderHandler.getPartialTicks()) / 5.0F;

					double prevXOffset = 0.0D;
					double prevYOffset = 0.0D;
					double prevZOffset = 0.0D;

					int iter = Minecraft.isFancyGraphicsEnabled() ? 8 : 4;
					for(int i = 0; i < iter; i++) {
						double multiplier = (1.0D - Math.abs((i + 1) - iter / 2.0D) / iter * 2.0D) * 0.15D;
						double xOffset = Math.cos((i + 1) * 4.0F / iter + ticks) * multiplier;
						double yOffset = Math.cos((i + 1) * 4.0F / iter + ticks + Math.PI) * multiplier;
						double zOffset = Math.sin((i + 1) * 4.0F / iter + ticks) * multiplier;

						RenderSwordEnergy.renderBeam(
								new Vec3d(sx + prevXOffset + dx / iter * i, sy + prevYOffset + dy / iter * i, sz + prevZOffset + dz / iter * i), 
								new Vec3d(sx + xOffset + dx / iter * (i + 1), sy + yOffset + dy / iter * (i + 1), sz + zOffset + dz / iter * (i + 1)), 
								ew + sw - sw / iter * i, ew + sw - sw / iter * (i + 1), i == 0, i == iter - 1);

						prevXOffset = xOffset;
						prevYOffset = yOffset;
						prevZOffset = zOffset;
					}

					GlStateManager.depthMask(true);
					GlStateManager.enableCull();
					GlStateManager.enableTexture2D();
					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.loadIdentity();
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
					GlStateManager.enableLighting();
					GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				}
			}
		}
	}
}
