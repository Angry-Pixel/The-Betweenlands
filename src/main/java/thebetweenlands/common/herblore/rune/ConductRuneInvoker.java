package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneItemStackAccess;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class ConductRuneInvoker extends AbstractRune<ConductRuneInvoker> {

	public static final class Blueprint extends AbstractRune.Blueprint<ConductRuneInvoker> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.1f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final InputPort<IRuneItemStackAccess> IN_ITEM_1;
		private static final InputPort<BlockPos> IN_POSITION_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final InputPort<IRuneItemStackAccess> IN_ITEM_2;
		private static final InputPort<Vec3d> IN_POSITION_2;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_ITEM_1 = builder.in(RuneTokenDescriptors.ITEM, null, IRuneItemStackAccess.class);
			IN_POSITION_1 = builder.in(RuneTokenDescriptors.BLOCK, InputSerializers.BLOCK, BlockPos.class);
			CONFIGURATION_1 = builder.build();

			IN_ITEM_2 = builder.in(RuneTokenDescriptors.ITEM, null, IRuneItemStackAccess.class);
			IN_POSITION_2 = builder.in(RuneTokenDescriptors.POSITION, InputSerializers.VECTOR, Vec3d.class);
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public ConductRuneInvoker create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new ConductRuneInvoker(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(ConductRuneInvoker state, RuneExecutionContext context, INodeIO io) {

			if(context.getUser().getWorld() instanceof WorldServer) {
				WorldServer world = (WorldServer) context.getUser().getWorld();

				Entity entity = context.getUser().getEntity();

				EntityPlayerMP fakePlayer = new FakePlayer(world, new GameProfile(UUID.randomUUID(), "[RuneChain]")) {
					@Override
					protected void applyEntityAttributes() { }
					
					@Override
					public boolean isSilent() {
						return true;
					}

					@Override
					public void playSound(SoundEvent soundIn, float volume, float pitch) { }

					@Override
					public boolean startRiding(Entity entityIn) { return false; }

					@Override
					public boolean startRiding(Entity entityIn, boolean force) { return false; }

					@Override
					public AbstractAttributeMap getAttributeMap() {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).getAttributeMap();
						}
						return super.getAttributeMap();
					}

					@Override
					public void addPotionEffect(PotionEffect potioneffectIn) {
						if(entity instanceof EntityLivingBase) {
							((EntityLivingBase) entity).addPotionEffect(potioneffectIn);
						}
					}

					@Override
					public void curePotionEffects(ItemStack curativeItem) {
						if(entity instanceof EntityLivingBase) {
							((EntityLivingBase) entity).curePotionEffects(curativeItem);
						}
					}

					@Override
					public boolean canBeHitWithPotion() {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).canBeHitWithPotion();
						}
						return super.canBeHitWithPotion();
					}

					@Override
					public void clearActivePotions() {
						if(entity instanceof EntityLivingBase) {
							((EntityLivingBase) entity).clearActivePotions();
						} else {
							super.clearActivePotions();
						}
					}

					@Override
					public PotionEffect getActivePotionEffect(Potion potionIn) {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).getActivePotionEffect(potionIn);
						}
						return super.getActivePotionEffect(potionIn);
					}

					@Override
					public Collection<PotionEffect> getActivePotionEffects() {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).getActivePotionEffects();
						}
						return super.getActivePotionEffects();
					}

					@Override
					public Map<Potion, PotionEffect> getActivePotionMap() {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).getActivePotionMap();
						}
						return super.getActivePotionMap();
					}

					@Override
					public boolean isPotionActive(Potion potionIn) {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).isPotionActive(potionIn);
						}
						return super.isPotionActive(potionIn);
					}

					@Override
					public boolean isPotionApplicable(PotionEffect potioneffectIn) {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).isPotionApplicable(potioneffectIn);
						}
						return super.isPotionApplicable(potioneffectIn);
					}

					@Override
					public PotionEffect removeActivePotionEffect(Potion potioneffectin) {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).removeActivePotionEffect(potioneffectin);
						}
						return super.removeActivePotionEffect(potioneffectin);
					}

					@Override
					public void removePotionEffect(Potion potionIn) {
						if(entity instanceof EntityLivingBase) {
							((EntityLivingBase) entity).removePotionEffect(potionIn);
						} else {
							super.removePotionEffect(potionIn);
						}
					}

					@Override
					public boolean attackEntityAsMob(Entity entityIn) {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).attackEntityAsMob(entityIn);
						}
						return super.attackEntityAsMob(entityIn);
					}

					@Override
					public boolean attackable() {
						if(entity instanceof EntityLivingBase) {
							return ((EntityLivingBase) entity).attackable();
						}
						return super.attackable();
					}

					@Override
					public boolean attackEntityFrom(DamageSource source, float amount) {
						if(entity != null) {
							return entity.attackEntityFrom(source, amount);
						}
						return super.attackEntityFrom(source, amount);
					}

					@Override
					public boolean canBeAttackedWithItem() {
						if(entity != null) {
							return entity.canBeAttackedWithItem();
						}
						return super.canBeAttackedWithItem();
					}

					@Override
					public boolean canAttackPlayer(EntityPlayer player) {
						if(entity instanceof EntityPlayer) {
							return ((EntityPlayer) entity).canAttackPlayer(player);
						}
						return super.canAttackPlayer(player);
					}

					@Override
					public boolean getIsInvulnerable() {
						if(entity != null) {
							return entity.getIsInvulnerable();
						}
						return super.getIsInvulnerable();
					}

					@Override
					public boolean isEntityInvulnerable(DamageSource source) {
						if(entity != null) {
							return entity.isEntityInvulnerable(source);
						}
						return super.isEntityInvulnerable(source);
					}
				};

				if(entity instanceof EntityPlayer) {
					fakePlayer.inventory = ((EntityPlayer) entity).inventory;
				}
				//TODO Wrapper for IRuneChainUser inventory

				fakePlayer.connection = new NetHandlerPlayServer(world.getMinecraftServer(), new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer);

				if(state.getConfiguration() == CONFIGURATION_1) {
					IRuneItemStackAccess access = IN_ITEM_1.get(io);
					ItemStack inputStack = access.get();

					if(!inputStack.isEmpty() && access.set(ItemStack.EMPTY)) {
						BlockPos block = IN_POSITION_1.get(io);

						fakePlayer.setLocationAndAngles(block.getX() + 0.5f, block.getY() + 0.5f - fakePlayer.getEyeHeight(), block.getZ() + 0.5f, fakePlayer.rotationYaw, fakePlayer.rotationPitch);

						ItemStack prevHeldStack = fakePlayer.getHeldItem(EnumHand.MAIN_HAND);

						fakePlayer.setHeldItem(EnumHand.MAIN_HAND, inputStack);

						if(inputStack.onItemUseFirst(fakePlayer, fakePlayer.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f) == EnumActionResult.PASS) {
							inputStack.onItemUse(fakePlayer, fakePlayer.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f);
						}

						ItemStack outputStack = fakePlayer.getHeldItem(EnumHand.MAIN_HAND);
						
						if(!access.set(outputStack)) {
							InventoryHelper.spawnItemStack(context.getUser().getWorld(), block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f, outputStack);
						}

						fakePlayer.setHeldItem(EnumHand.MAIN_HAND, prevHeldStack);

						fakePlayer.setDead();
					}
				} else {
					IRuneItemStackAccess access = IN_ITEM_2.get(io);
					ItemStack[] inputStack = new ItemStack[] { access.get() };
					
					if(!inputStack[0].isEmpty() && access.set(ItemStack.EMPTY)) {
						Vec3d position = IN_POSITION_2.get(io);

						io.schedule(scheduler -> {
							boolean terminated = false;
							
							int i = scheduler.getUpdateCount();

							fakePlayer.setLocationAndAngles(position.x, position.y - fakePlayer.getEyeHeight(), position.z, fakePlayer.rotationYaw, fakePlayer.rotationPitch);

							ItemStack prevHeldStack = fakePlayer.getHeldItem(EnumHand.MAIN_HAND);

							fakePlayer.setHeldItem(EnumHand.MAIN_HAND, inputStack[0]);

							if(i == 0) {
								ItemStack resultStack = inputStack[0].useItemRightClick(fakePlayer.world, fakePlayer, EnumHand.MAIN_HAND).getResult();

								if(resultStack != inputStack[0] || resultStack.getCount() != i) {
									fakePlayer.setHeldItem(EnumHand.MAIN_HAND, resultStack);

									if(resultStack.isEmpty()) {
										net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(fakePlayer, inputStack[0], EnumHand.MAIN_HAND);
									}
									
									inputStack[0] = resultStack;
								}
							}

							if(!fakePlayer.isHandActive() || inputStack[0].getMaxItemUseDuration() <= 0) {
								fakePlayer.setDead();

								scheduler.terminate();
								terminated = true;
							} else {
								inputStack[0].updateAnimation(fakePlayer.world, fakePlayer, 0 /*TODO Is this right? */, true);
								
								inputStack[0].getItem().onUsingTick(inputStack[0], fakePlayer, i);

								if(i >= inputStack[0].getMaxItemUseDuration() || i >= 20 * 5) {
									inputStack[0].onPlayerStoppedUsing(fakePlayer.world, fakePlayer, 0);

									ItemStack resultStack = inputStack[0].onItemUseFinish(fakePlayer.world, fakePlayer);
						            resultStack = net.minecraftforge.event.ForgeEventFactory.onItemUseFinish(fakePlayer, inputStack[0], i, resultStack);
						            fakePlayer.setHeldItem(EnumHand.MAIN_HAND, resultStack);
									
						            inputStack[0] = resultStack;
						            
									fakePlayer.setDead();

									scheduler.terminate();
									terminated = true;
								}
							}

							ItemStack outputStack = fakePlayer.getHeldItem(EnumHand.MAIN_HAND);
							
							inputStack[0] = outputStack;
							
							if(terminated && !access.set(outputStack)) {
								InventoryHelper.spawnItemStack(context.getUser().getWorld(), position.x, position.y, position.z, outputStack);
							}
							
							fakePlayer.setHeldItem(EnumHand.MAIN_HAND, prevHeldStack);

							scheduler.sleep(1);
						});
					}
				}
			}

			return null;
		}
	}

	private ConductRuneInvoker(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
