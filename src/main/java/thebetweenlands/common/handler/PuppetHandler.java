package thebetweenlands.common.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFollowTarget;
import thebetweenlands.common.entity.ai.puppet.EntityAIGoTo;
import thebetweenlands.common.entity.ai.puppet.EntityAIGuardHome;
import thebetweenlands.common.entity.ai.puppet.EntityAIPuppet;
import thebetweenlands.common.entity.ai.puppet.EntityAIStay;
import thebetweenlands.common.item.equipment.ItemRing;
import thebetweenlands.common.item.equipment.ItemRingOfRecruitment;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.RenderUtils;

public class PuppetHandler {
	private PuppetHandler() { }

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity instanceof EntityLiving) {
			IPuppetCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

			if(cap != null && cap.hasPuppeteer()) {
				//Refund recruitment cost
				ItemStack ring = ItemRingOfRecruitment.getActiveRing(cap.getPuppeteer(), null);
				if(!ring.isEmpty()) {
					ring.setItemDamage(ring.getItemDamage() - cap.getRecruitmentCost());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		EntityLivingBase target = event.getEntityLiving();
		DamageSource source = event.getSource();

		//Prevent damage from controller
		IPuppetCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
		if(cap != null && cap.hasPuppeteer()) {
			Entity controller = cap.getPuppeteer();

			if(controller != null && (source.getImmediateSource() == controller || source.getTrueSource() == controller)) {
				event.setCanceled(true);
				return;
			}
		}

		if(!checkValidAttack(source.getImmediateSource(), target) && !checkValidAttack(source.getTrueSource(), target)) {
			//Prevents attacks of recruited mobs that aren't attacking its target
			event.setCanceled(true);
		} else {
			Set<Entity> attackers = new HashSet<>();
			attackers.add(source.getImmediateSource());
			attackers.add(source.getTrueSource());

			for(Entity attacker : attackers) {
				if(attacker != null) {
					if(attacker instanceof EntityLiving) {
						IPuppetCapability attackerCap = attacker.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

						if(attackerCap != null && attackerCap.hasPuppeteer() && attacker.world.rand.nextInt(4) == 0) {
							//Set revenge target so it can be attacked by the target
							target.setRevengeTarget((EntityLiving) attacker);
						}
					}

					if(event.getAmount() > 1.0f) {
						consumeAttackXp(attacker);
					}
				}
			}
		}
	}

	private static boolean checkValidAttack(Entity attacker, Entity target) {
		if(attacker instanceof EntityLiving) {
			IPuppetCapability cap = attacker.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

			if(cap != null && cap.hasPuppeteer()) {
				//If recruited only allow damage to the target
				return ((EntityLiving) attacker).getAttackTarget() == target;
			}
		}

		return true;
	}

	private static void consumeAttackXp(Entity attacker) {
		IPuppetCapability cap = attacker.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

		if(cap != null && cap.hasPuppeteer()) {
			Entity controller = cap.getPuppeteer();

			if(controller instanceof EntityPlayer) {
				ItemRing.removeXp((EntityPlayer) controller, 1 + attacker.world.rand.nextInt(2));
			}
		}
	}
	
	private static void cycleAiState(EntityLiving entity, IPuppetCapability cap, EntityPlayer player, BlockPos pos) {
		if(!cap.getStay() && !cap.getGuard()) {
			//From follow state to guard state
			cap.setStay(false);
			cap.setGuard(true, pos);
			
			player.sendStatusMessage(new TextComponentTranslation("chat.ring_of_recruitment.state.guard", entity.getDisplayName()), true);
		} else if(cap.getGuard()) {
			//From guard state to stay state
			cap.setStay(true);
			cap.setGuard(false, null);
			
			player.sendStatusMessage(new TextComponentTranslation("chat.ring_of_recruitment.state.stay", entity.getDisplayName()), true);
		} else {
			//From stay state to follow state
			cap.setStay(false);
			cap.setGuard(false, null);
			
			player.sendStatusMessage(new TextComponentTranslation("chat.ring_of_recruitment.state.follow", entity.getDisplayName()), true);
		}
	}

	@SubscribeEvent
	public static void onUpdateLiving(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity instanceof EntityLiving) {
			IPuppetCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

			if(cap != null && cap.hasPuppeteer()) {
				EntityLiving living = (EntityLiving) entity;

				if(!entity.world.isRemote) {
					cap.setRemainingTicks(cap.getRemainingTicks() - 1);

					Entity controller = cap.getPuppeteer();

					if(cap.getRemainingTicks() <= 0 || (controller != null && !ItemRingOfRecruitment.isRingActive(controller, cap))) {
						if(controller != null) {
							//Refund recruitment cost
							ItemStack ring = ItemRingOfRecruitment.getActiveRing(controller, null);
							if(!ring.isEmpty()) {
								ring.setItemDamage(ring.getItemDamage() - cap.getRecruitmentCost());
							}
						}

						cap.setRecruitmentCost(0);
						cap.setPuppeteer(null);
						cap.setRemainingTicks(0);
						cap.setRingUuid(null);

						living.setAttackTarget(null);
						living.setRevengeTarget(null);

						EntityAIPuppet.removePuppetAI(living.tasks);
						EntityAIPuppet.removePuppetAI(living.targetTasks);
					} else {
						living.enablePersistence();

						if(controller instanceof EntityPlayer && living.getHealth() < living.getMaxHealth() - 2 && living.ticksExisted % 40 == 0 &&
								((!cap.getStay() && !cap.getGuard()) || controller.getDistance(entity) < 6) &&
								ItemRing.removeXp((EntityPlayer) controller, 3) >= 3) {

							living.heal(2.0f);

							if(living.world instanceof WorldServer) {
								((WorldServer) living.world).spawnParticle(EnumParticleTypes.HEART, living.posX, living.posY + living.getEyeHeight() + 0.25f, living.posZ, 1, 0.0f, 0.0f, 0.0f, 0.0f);
							}
						}

						if(EntityAIPuppet.getPuppetAI(living.targetTasks) == null && living instanceof EntityCreature) {
							EntityAINearestAttackableTarget<EntityLiving> aiTarget = new EntityAINearestAttackableTarget<EntityLiving>((EntityCreature) living, EntityLiving.class, 0, true, true, target -> {
								IPuppetCapability targetCap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
								if(targetCap != null && targetCap.hasPuppeteer()) {
									return false;
								}
								if(target instanceof IEntityOwnable && ((IEntityOwnable) target).getOwnerId() != null) {
									return false;
								}
								return target instanceof IMob;
							}) {
								@Override
								protected double getTargetDistance() {
									return 16;
								}
								
								@Override
								public boolean shouldExecute() {
									if(super.shouldExecute()) {
										IPuppetCapability thisCap = this.taskOwner.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
										if(thisCap != null && thisCap.getGuard()) {
											return true;
										}
									}
									return false;
								}
							};
							aiTarget.setMutexBits(1); //01

							EntityAITasks tasks = EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), living, living.targetTasks);
							tasks.addTask(0, aiTarget);
						}

						EntityAIPuppet puppetAI = EntityAIPuppet.getPuppetAI(living.tasks);

						if(puppetAI == null) {
							EntityAISwimming aiSwim = new EntityAISwimming(living);
							
							EntityAIStay aiStay = new EntityAIStay(living);
							aiStay.setMutexBits(3); //11

							EntityAIGuardHome aiGuardHome = null;
							EntityAIWander aiWander = null;
							
							EntityAIBase aiAttack = null;
							
							if(living instanceof EntityCreature) {
								aiGuardHome = new EntityAIGuardHome((EntityCreature) living, 1.0D, 24);
								
								aiWander = new EntityAIWander((EntityCreature) living, 1.0D);
								
								aiAttack = new EntityAIAttackMelee((EntityCreature) living, 1.2D, true) {
									@Override
									protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
										double d0 = this.getAttackReachSqr(enemy);

										if (distToEnemySqr <= d0 && this.attackTick <= 0) {
											this.attackTick = 20;
											this.attacker.swingArm(EnumHand.MAIN_HAND);

											//Try regular attack first and otherwise use fallback because
											//Passive animals usually just return false in attackEntityAsMob.
											if(!this.attacker.attackEntityAsMob(enemy)) {
												EntityAIAttackOnCollide.useStandardAttack(this.attacker, enemy);
											}
										}
									}
								};
								aiAttack.setMutexBits(2); //10
							} else {
								aiAttack = new EntityAIAttackOnCollide(living, true);
								aiAttack.setMutexBits(2); //10
							}
							
							EntityAIGoTo aiGoTo = new EntityAIGoTo(living, 1.2D) {
								@Override
								public boolean shouldExecute() {
									if(super.shouldExecute()) {
										IPuppetCapability thisCap = this.taskOwner.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
										if(thisCap != null && !thisCap.getGuard()) {
											return true;
										}
									}
									return false;
								}
							};
							aiGoTo.setMutexBits(3);

							EntityAIFollowTarget aiFollow = new EntityAIFollowTarget(living, () -> {
								Entity puppeteer = cap.getPuppeteer();
								if(puppeteer instanceof EntityLivingBase) {
									return (EntityLivingBase) puppeteer;
								}
								return null;
							}, 1.2D, 10.0F, 2.0F, true) {
								@Override
								public boolean shouldExecute() {
									if(super.shouldExecute()) {
										IPuppetCapability thisCap = this.taskOwner.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
										if(thisCap != null && !thisCap.getGuard()) {
											return true;
										}
									}
									return false;
								}
							};
							aiFollow.setMutexBits(1);

							if(aiGuardHome != null && aiWander != null) {
								EntityAITasks tasks = EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), living, living.tasks);
								tasks.addTask(0, aiSwim);
								tasks.addTask(1, aiStay);
								tasks.addTask(2, aiGuardHome);
								tasks.addTask(3, aiFollow);
								tasks.addTask(4, aiGoTo);
								tasks.addTask(5, aiAttack);
								tasks.addTask(6, aiWander);
							} else {
								EntityAITasks tasks = EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), living, living.tasks);
								tasks.addTask(0, aiSwim);
								tasks.addTask(1, aiStay);
								tasks.addTask(3, aiFollow);
								tasks.addTask(4, aiGoTo);
								tasks.addTask(5, aiAttack);
							}
						} else {
							EntityAIStay aiStay = getAI(EntityAIStay.class, puppetAI.getSubTasks());
							if (aiStay != null) {
								if(cap.getStay()) {
									aiStay.setStay(true);
									living.setAttackTarget(null);
								} else {
									aiStay.setStay(false);
								}
							}
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
					
					if(living.ticksExisted % 50 == 0) {
						ParticleArgs<?> args = ParticleArgs.get().withScale(1.5f).withColor(1, 1, 1, 0.3f).withData(living);
						Particle particle;
						
						if(!cap.getStay() && !cap.getGuard()) {
							particle = BLParticles.RING_OF_RECRUITMENT_FOLLOW.create(living.world, 0, living.height + 0.25f, 0, args);
						} else if(cap.getStay()) {
							particle = BLParticles.RING_OF_RECRUITMENT_STAY.create(living.world, 0, living.height + 0.25f, 0, args);
						} else {
							particle = BLParticles.RING_OF_RECRUITMENT_GUARD.create(living.world, 0, living.height + 0.25f, 0, args);
						}
						
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, particle);
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

			if(source.getTrueSource() instanceof EntityPlayer) {
				IPuppeteerCapability cap = source.getTrueSource().getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
				if (cap != null) {
					List<Entity> puppets = cap.getPuppets();

					if (!puppets.contains(attackedEntity)) {
						for (Entity entity : puppets) {
							if (entity instanceof EntityLiving) {
								((EntityLiving) entity).setAttackTarget(attackedEntity);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends EntityAIBase> T getAI(Class<T> cls, EntityAITasks tasks) {
		for(EntityAITaskEntry entry : tasks.taskEntries) {
			if(cls.isAssignableFrom(entry.action.getClass())) {
				return (T) entry.action;
			}
		}
		return null;
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();

		if(event.getHand() == EnumHand.MAIN_HAND && player != null && player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && target instanceof EntityLiving) {
			ItemStack ring = ItemRingOfRecruitment.getActiveRing(player, null);

			if(!ring.isEmpty()) {
				IPuppetCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

				if (cap != null) {
					EntityLiving living = (EntityLiving) target;

					if (cap.hasPuppeteer()) {
						Entity puppeteer = cap.getPuppeteer();

						if (player == puppeteer) {
							if (player.isSneaking()) {
								if (!player.world.isRemote) {
									cap.setRemainingTicks(0);
								}
								player.swingArm(EnumHand.MAIN_HAND);
							} else {
								if (!player.world.isRemote) {
									cycleAiState(living, cap, player, target.getPosition());
								}
								player.swingArm(EnumHand.MAIN_HAND);
							}
						}
					} else if (!player.world.isRemote) {
						int recruitmentCost = ((ItemRingOfRecruitment) ring.getItem()).getRecruitmentCost(living);

						if(ring.getItemDamage() <= ring.getMaxDamage() - recruitmentCost) {
							IPuppeteerCapability capPlayer = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);

							if (capPlayer != null && capPlayer.getActivatingEntity() == null) {
								capPlayer.setActivatingEntity(living);
								capPlayer.setActivatingTicks(0);
							}
						} else {
							player.sendStatusMessage(new TextComponentTranslation("chat.ring_of_recruitment.not_enough_power"), true);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerUpdate(PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			IPuppeteerCapability cap = event.player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);

			if (cap != null) {
				Entity activatingEntity = cap.getActivatingEntity();

				if (activatingEntity instanceof EntityLiving) {
					EntityLiving living = (EntityLiving) activatingEntity;

					if (!event.player.world.isRemote) {
						if(event.player.getHeldItemMainhand().isEmpty() && !event.player.isSwingInProgress) {
							if (living.getDistance(event.player) > 5.0D) {
								cap.setActivatingEntity(null);
								cap.setActivatingTicks(0);
							} else {
								cap.setActivatingTicks(cap.getActivatingTicks() + 1);

								//Consume XP while recruiting
								ItemRing.removeXp(event.player, 2);

								if (cap.getActivatingTicks() > living.getMaxHealth()) {
									ItemStack ring = ItemRingOfRecruitment.getActiveRing(event.player, null);

									if (!ring.isEmpty()) {
										UUID ringUuid = ((ItemRingOfRecruitment) ring.getItem()).getRingUuid(ring);

										if(ringUuid != null) {
											IPuppetCapability puppetCap = living.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

											if (puppetCap != null && !puppetCap.hasPuppeteer()) {
												int recruitmentCost = ((ItemRingOfRecruitment) ring.getItem()).getRecruitmentCost(living);

												if(ring.getItemDamage() <= ring.getMaxDamage() - recruitmentCost) {
													ring.setItemDamage(ring.getItemDamage() + recruitmentCost);

													puppetCap.setRecruitmentCost(recruitmentCost);
													puppetCap.setPuppeteer(event.player);
													puppetCap.setRemainingTicks(Integer.MAX_VALUE); //No longer time limit, just use max int
													puppetCap.setRingUuid(ringUuid);
												}
											}
										}
									}

									cap.setActivatingEntity(null);
									cap.setActivatingTicks(0);
								}
							}
						} else {
							cap.setActivatingEntity(null);
							cap.setActivatingTicks(0);
						}
					} else {
						Vec3d vec = new Vec3d(living.posX - event.player.posX, (living.posY + living.getEyeHeight() * 0.8F) - (event.player.posY + event.player.getEyeHeight() * 0.8F), living.posZ - event.player.posZ);
						vec = vec.normalize();
						vec = vec.add((event.player.world.rand.nextFloat() - 0.5F) / 3.0F, (event.player.world.rand.nextFloat() - 0.5F) / 3.0F, (event.player.world.rand.nextFloat() - 0.5F) / 3.0F);
						vec = vec.normalize();
						double dist = event.player.getDistance(living);
						vec = vec.scale(dist / 15.0F);
						BLParticles.SPAWNER.spawn(event.player.world, event.player.posX, event.player.posY + event.player.getEyeHeight() * 0.8F, event.player.posZ, ParticleArgs.get().withData(40).withColor(0.2F, 0.8F, 0.4F, 1).withMotion(vec.x, vec.y, vec.z));
					}

					event.player.motionX *= 0.05D;
					event.player.motionZ *= 0.05D;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getHand() == EnumHand.MAIN_HAND && event instanceof PlayerInteractEvent.RightClickBlock && event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND).isEmpty() && ItemRingOfRecruitment.isRingActive(event.getEntityPlayer(), null)) {
			EntityPlayer player = event.getEntityPlayer();

			IPuppeteerCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
			if (cap != null) {
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

		IPuppetCapability cap = living.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
		if (cap != null && cap.hasPuppeteer()) {
			if (!RenderUtils.doesRendererHaveLayer(event.getRenderer(), LayerPuppetOverlay.class, false)) {
				event.getRenderer().addLayer(new LayerPuppetOverlay(event.getRenderer()));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderLivingPost(RenderLivingEvent.Post<EntityLivingBase> event) {
		EntityLivingBase living = event.getEntity();

		IPuppetCapability cap = living.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
		if (cap != null && cap.hasPuppeteer()) {
			Entity puppeteer = cap.getPuppeteer();

			if (puppeteer != null && ((!cap.getStay() && !cap.getGuard()) || puppeteer.getDistance(living) < 6)) {
				event.getRenderer().bindTexture(LayerPuppetOverlay.OVERLAY_TEXTURE);

				float alpha = 0.25f;
				if(cap.getStay() || cap.getGuard()) {
					alpha *= 1.0f - Math.max(0, (puppeteer.getDistance(living) - 3)) / 3.0f;
				}

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				GlStateManager.translate((living.ticksExisted + WorldRenderHandler.getPartialTicks()) / 40.0F, 0, 0.0F);
				GlStateManager.scale(4, 1, 1);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);

				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001f);
				GlStateManager.enableTexture2D();
				GlStateManager.disableCull();
				GlStateManager.depthMask(false);


				float animationTimer = (living.ticksExisted + event.getPartialRenderTick());

				float colorSine = (MathHelper.cos(animationTimer * 0.1f) + 1) * 0.3f;

				alpha *= (MathHelper.sin(animationTimer * 0.0333333f) + 1) * 0.25f + 0.3f;

				GlStateManager.color(colorSine, 1.25f - colorSine, 2.0f, alpha);

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
				for (int i = 0; i < iter; i++) {
					double multiplier = (1.0D - Math.abs((i + 1) - iter / 2.0D) / iter * 2.0D) * 0.15D;
					double xOffset = Math.cos((i + 1) * 4.0F / iter + ticks) * multiplier;
					double yOffset = Math.cos((i + 1) * 4.0F / iter + ticks + Math.PI) * multiplier;
					double zOffset = Math.sin((i + 1) * 4.0F / iter + ticks) * multiplier;

					RenderSwordEnergy.renderBeam(new Vec3d(sx + prevXOffset + dx / iter * i, sy + prevYOffset + dy / iter * i, sz + prevZOffset + dz / iter * i), new Vec3d(sx + xOffset + dx / iter * (i + 1), sy + yOffset + dy / iter * (i + 1), sz + zOffset + dz / iter * (i + 1)), ew + sw - sw / iter * i, ew + sw - sw / iter * (i + 1), i == 0, i == iter - 1);

					prevXOffset = xOffset;
					prevYOffset = yOffset;
					prevZOffset = zOffset;
				}

				GlStateManager.depthMask(true);
				GlStateManager.enableCull();
				GlStateManager.enableTexture2D();
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.enableLighting();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
				GlStateManager.color(1, 1, 1, 1);
			}
		}
	}
}
