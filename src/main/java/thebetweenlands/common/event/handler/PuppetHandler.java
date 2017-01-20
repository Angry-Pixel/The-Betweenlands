package thebetweenlands.common.event.handler;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.capability.recruitment.IPuppetCapability;
import thebetweenlands.common.capability.recruitment.IPuppeteerCapability;
import thebetweenlands.common.entity.ai.EntityAIFollowTarget;
import thebetweenlands.common.entity.ai.puppet.EntityAIGoTo;
import thebetweenlands.common.entity.ai.puppet.EntityAIPuppet;
import thebetweenlands.common.entity.ai.puppet.EntityAIStay;
import thebetweenlands.common.item.equipment.ItemRingOfRecruitment;
import thebetweenlands.common.registries.CapabilityRegistry;

public class PuppetHandler {
	private PuppetHandler() { }

	@SubscribeEvent
	public static void onUpdateLiving(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity instanceof EntityCreature && entity.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
			IPuppetCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
			EntityCreature creature = (EntityCreature) entity;

			if(cap.hasPuppeteer()) {
				if(!entity.worldObj.isRemote) {
					cap.setRemainingTicks(cap.getRemainingTicks() - 1);

					if(cap.getRemainingTicks() <= 0 || (cap.getPuppeteer() != null && !ItemRingOfRecruitment.isRingActive(cap.getPuppeteer()))) {
						cap.setPuppeteer(null);
						cap.setRemainingTicks(0);
						creature.setAttackTarget(null);
						creature.setRevengeTarget(null);
						EntityAIPuppet.removePuppetAI(creature.targetTasks);
					} else {
						if(EntityAIPuppet.getPuppetAI(creature.targetTasks) == null) {
							EntityAIStay aiStay = new EntityAIStay(creature);
							aiStay.setMutexBits(3); //11

							EntityAIGoTo aiGoTo = new EntityAIGoTo(creature, 1.2D);
							aiGoTo.setMutexBits(3);

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

							EntityAIAttackMelee aiMelee = new EntityAIAttackMelee(creature, 1.2D, true);
							aiMelee.setMutexBits(2); //10

							if(creature.getNavigator() instanceof PathNavigateGround) {
								EntityAIFollowTarget aiFollow = new EntityAIFollowTarget(creature, () -> {
									Entity puppeteer = cap.getPuppeteer();
									if(puppeteer instanceof EntityLivingBase) {
										return (EntityLivingBase) puppeteer;
									}
									return null;
								}, 1.2D, 10.0F, 2.0F);
								aiFollow.setMutexBits(1);

								EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), creature, creature.targetTasks,
										ImmutableList.of(aiFollow, aiStay, aiGoTo/*, aiTarget*/, aiMelee));
							} else {
								EntityAIPuppet.addPuppetAI(() -> cap.getPuppeteer(), creature, creature.targetTasks,
										ImmutableList.of(aiStay, aiGoTo/*, aiTarget*/, aiMelee));
							}
						}
					}
				} else {
					if(entity.worldObj.rand.nextInt(5) == 0) {
						BLParticles.SPAWNER.spawn(creature.worldObj, creature.posX + creature.motionX * 2, creature.posY + creature.height / 2.0D, creature.posZ + creature.motionZ * 2,
								ParticleArgs.get().withMotion(
										creature.motionX + (creature.worldObj.rand.nextFloat() - 0.5F) / 8.0F * entity.width, 
										(creature.worldObj.rand.nextFloat() - 0.5F) / 8.0F * entity.height, 
										creature.motionZ + (creature.worldObj.rand.nextFloat() - 0.5F) / 8.0F * entity.width
										).withData(40).withColor(0.2F, 0.8F, 0.4F, 1));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		EntityLivingBase attackedEntity = event.getEntityLiving();
		if(!attackedEntity.worldObj.isRemote) {
			DamageSource source = event.getSource();

			if(source.getEntity() instanceof EntityPlayer && source.getEntity().hasCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null)) {
				IPuppeteerCapability cap = source.getEntity().getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
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

		if(event.getHand() == EnumHand.MAIN_HAND && player != null && target instanceof EntityCreature && ItemRingOfRecruitment.isRingActive(player) && target.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
			IPuppetCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
			EntityCreature creature = (EntityCreature) target;

			if(cap.hasPuppeteer()) {
				Entity puppeteer = cap.getPuppeteer();

				if(player == puppeteer) {
					if(player.isSneaking()) {
						if(!player.worldObj.isRemote) {
							cap.setRemainingTicks(0);
						}
						player.swingArm(EnumHand.MAIN_HAND);
					} else {
						if(!player.worldObj.isRemote){
							EntityAIPuppet puppetAI = EntityAIPuppet.getPuppetAI(creature.targetTasks);
							if(puppetAI != null) {
								EntityAIStay aiStay = getAI(EntityAIStay.class, puppetAI.getSubTasks());
								if(aiStay != null) {
									aiStay.setStay(!aiStay.getStay());
									creature.setAttackTarget(null);
								}
							}
						}
						player.swingArm(EnumHand.MAIN_HAND);
					}
				}
			} else if(!player.worldObj.isRemote) {
				if(ItemRingOfRecruitment.isRingActive(player) && player.hasCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null)) {
					IPuppeteerCapability capPlayer = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
					if(capPlayer.getActivatingEntity() == null) {
						capPlayer.setActivatingEntity(creature);
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

			if(activatingEntity instanceof EntityCreature) {
				EntityCreature creature = (EntityCreature) activatingEntity;

				if(!event.player.worldObj.isRemote) {
					if(creature.getDistanceToEntity(event.player) > 5.0D) {
						cap.setActivatingEntity(null);
						cap.setActivatingTicks(0);
					} else {
						cap.setActivatingTicks(cap.getActivatingTicks() + 1);

						if(cap.getActivatingTicks() > creature.getMaxHealth()) {
							if(ItemRingOfRecruitment.isRingActive(event.player) && creature.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
								IPuppetCapability puppetCap = creature.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

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
					Vec3d vec = new Vec3d(creature.posX - event.player.posX, (creature.posY + creature.getEyeHeight() * 0.8F) - (event.player.posY + event.player.getEyeHeight() * 0.8F), creature.posZ - event.player.posZ);
					vec = vec.normalize();
					vec = vec.addVector((event.player.worldObj.rand.nextFloat() - 0.5F) / 3.0F, 
							(event.player.worldObj.rand.nextFloat() - 0.5F) / 3.0F, 
							(event.player.worldObj.rand.nextFloat() - 0.5F) / 3.0F);
					vec = vec.normalize();
					double dist = event.player.getDistanceToEntity(creature);
					vec = vec.scale(dist / 15.0F);
					BLParticles.SPAWNER.spawn(event.player.worldObj, event.player.posX, event.player.posY + event.player.getEyeHeight() * 0.8F, event.player.posZ, 
							ParticleArgs.get().withData(40).withColor(0.2F, 0.8F, 0.4F, 1).withMotion(vec.xCoord, vec.yCoord, vec.zCoord));
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
						if(!player.worldObj.isRemote) {
							EntityAIPuppet puppetAI = EntityAIPuppet.getPuppetAI(living.targetTasks);
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

					if(player.worldObj.isRemote) {
						for(int i = 0; i < 4; i++) {
							BLParticles.SPAWNER.spawn(player.worldObj, target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D,
									ParticleArgs.get().withMotion(
											(player.worldObj.rand.nextFloat() - 0.5F) / 16.0F, 
											(player.worldObj.rand.nextFloat() - 0.5F) / 16.0F, 
											(player.worldObj.rand.nextFloat() - 0.5F) / 16.0F
											).withData(30).withColor(0.2F, 0.8F, 0.25F, 1));
						}
					}
				}
			}
		}
	}
}
