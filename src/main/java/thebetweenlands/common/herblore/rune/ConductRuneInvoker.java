package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.IRuneItemStackAccess;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.entity.EntityPlayerDelegate;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.util.InventoryUtil;

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
		private static final InputPort<Vec3d> IN_DIRECTION_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final InputPort<IRuneItemStackAccess> IN_ITEM_2;
		private static final InputPort<Vec3d> IN_POSITION_2;
		private static final InputPort<Vec3d> IN_DIRECTION_2;

		public static final RuneConfiguration CONFIGURATION_3;
		private static final InputPort<IRuneItemStackAccess> IN_ITEM_3;
		private static final InputPort<Entity> IN_ENTITY_3;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_ITEM_1 = builder.in(RuneTokenDescriptors.ITEM, null, IRuneItemStackAccess.class);
			IN_POSITION_1 = builder.in(RuneTokenDescriptors.BLOCK, InputSerializers.BLOCK, BlockPos.class);
			IN_DIRECTION_1 = builder.in(RuneTokenDescriptors.DIRECTION, InputSerializers.VECTOR, Vec3d.class);
			CONFIGURATION_1 = builder.build();

			IN_ITEM_2 = builder.in(RuneTokenDescriptors.ITEM, null, IRuneItemStackAccess.class);
			IN_POSITION_2 = builder.in(RuneTokenDescriptors.POSITION, InputSerializers.VECTOR, Vec3d.class);
			IN_DIRECTION_2 = builder.in(RuneTokenDescriptors.DIRECTION, InputSerializers.VECTOR, Vec3d.class);
			CONFIGURATION_2 = builder.build();

			IN_ITEM_3 = builder.in(RuneTokenDescriptors.ITEM, null, IRuneItemStackAccess.class);
			IN_ENTITY_3 = builder.in(RuneTokenDescriptors.ENTITY, InputSerializers.ENTITY, Entity.class);
			CONFIGURATION_3 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3);
		}

		@Override
		public ConductRuneInvoker create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new ConductRuneInvoker(this, index, composition, (RuneConfiguration) configuration);
		}

		private void returnExcessItems(IRuneChainUser user, Vec3d pos, List<NonNullList<ItemStack>> excess) {
			Entity entity = user.getEntity();

			if(entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;

				for(NonNullList<ItemStack> stacks : excess) {
					for(ItemStack stack : stacks) {
						if(!stack.isEmpty()) {
							if(!player.inventory.addItemStackToInventory(stack)) {
								InventoryHelper.spawnItemStack(user.getWorld(), pos.x, pos.y, pos.z, stack);
							}
						}
					}

					stacks.clear();
				}
			} else {
				IInventory inv = user.getInventory();

				for(NonNullList<ItemStack> stacks : excess) {
					for(ItemStack stack : stacks) {
						if(!stack.isEmpty()) {
							if(inv != null) {
								InventoryUtil.addItemToInventory(inv, stack);
							}

							if(!stack.isEmpty()) {
								InventoryHelper.spawnItemStack(user.getWorld(), pos.x, pos.y, pos.z, stack);
							}
						}
					}

					stacks.clear();
				}
			}
		}

		public static Pair<Float, Float> getRotationsFromDir(Vec3d dir) {
			double magnitudeXZ = (double)MathHelper.sqrt(dir.x * dir.x + dir.z * dir.z);
			float yaw = (float)(MathHelper.atan2(dir.z, dir.x) * (180D / Math.PI)) - 90.0F;
			float pitch = (float)(-(MathHelper.atan2(dir.y, magnitudeXZ) * (180D / Math.PI)));
			return Pair.of(yaw, pitch);
		}

		private void invokeImmediateUse(INodeIO io, IRuneChainUser user, IRuneItemStackAccess access, EntityPlayerDelegate delegate, Vec3d pos, float yaw, float pitch, Consumer<EntityPlayerDelegate> action) {
			ItemStack inputStack = access.get();

			if(!inputStack.isEmpty() && access.set(ItemStack.EMPTY)) {
				delegate.setLocationAndAngles(pos.x, pos.y - delegate.getEyeHeight(), pos.z, yaw, pitch);

				ItemStack prevHeldStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

				delegate.setHeldItem(EnumHand.MAIN_HAND, inputStack);

				action.accept(delegate);

				ItemStack outputStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

				if(!access.set(outputStack)) {
					this.returnExcessItems(user, pos, Arrays.asList(NonNullList.from(ItemStack.EMPTY, outputStack)));
				}

				delegate.setHeldItem(EnumHand.MAIN_HAND, prevHeldStack);

				delegate.setDead();

				this.returnExcessItems(user, pos, delegate.getExcessInventories());
			}
		}

		private void invokeContinuousUse(INodeIO io, IRuneChainUser user, IRuneItemStackAccess access, EntityPlayerDelegate delegate, Vec3d pos, float yaw, float pitch, BiFunction<EntityPlayerDelegate, Integer, Boolean> action) {
			ItemStack[] stack = new ItemStack[] { access.get() };

			if(!stack[0].isEmpty() && access.set(ItemStack.EMPTY)) {
				io.schedule(scheduler -> {
					boolean terminated = false;

					int i = scheduler.getUpdateCount();

					delegate.setLocationAndAngles(pos.x, pos.y - delegate.getEyeHeight(), pos.z, yaw, pitch);

					ItemStack prevHeldStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

					delegate.setHeldItem(EnumHand.MAIN_HAND, stack[0]);

					if(action.apply(delegate, i)) {
						delegate.setDead();
						terminated = true;
						scheduler.terminate();
					}

					ItemStack outputStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

					stack[0] = outputStack;

					if(terminated && !access.set(outputStack)) {
						this.returnExcessItems(user, pos, Arrays.asList(NonNullList.from(ItemStack.EMPTY, outputStack)));
					}

					delegate.setHeldItem(EnumHand.MAIN_HAND, prevHeldStack);

					this.returnExcessItems(user, pos, delegate.getExcessInventories());

					scheduler.sleep(1);
				});
			}
		}

		@Override
		protected RuneEffectModifier.Subject activate(ConductRuneInvoker state, RuneExecutionContext context, INodeIO io) {

			if(context.getUser().getWorld() instanceof WorldServer) {
				WorldServer world = (WorldServer) context.getUser().getWorld();

				IRuneChainUser user = context.getUser();
				Entity userEntity = user.getEntity();

				List<NonNullList<ItemStack>> excess = new ArrayList<>();

				EntityPlayerDelegate.Builder delegateBuilder = EntityPlayerDelegate.from(world, new GameProfile(UUID.randomUUID(), "[RuneChain]"), excess);

				delegateBuilder.entity(userEntity);

				if(userEntity instanceof EntityPlayer) {
					delegateBuilder.playerInventory(((EntityPlayer) userEntity).inventory);
				} else {
					delegateBuilder.mainInventory(context.getUser().getInventory());
				}

				EntityPlayerDelegate delegate = delegateBuilder.build();

				if(state.getConfiguration() == CONFIGURATION_1) {
					IRuneItemStackAccess access = IN_ITEM_1.get(io);

					BlockPos block = IN_POSITION_1.get(io);
					Pair<Float, Float> rotations = getRotationsFromDir(IN_DIRECTION_1.get(io));

					this.invokeImmediateUse(io, user, access, delegate, new Vec3d(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f), rotations.getLeft(), rotations.getRight(), d -> {
						ItemStack stack = delegate.getHeldItemMainhand();

						if(stack.onItemUseFirst(delegate, delegate.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f) == EnumActionResult.PASS) {
							stack.onItemUse(delegate, delegate.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f);
						}
					});

				} else if(state.getConfiguration() == CONFIGURATION_2) {
					IRuneItemStackAccess access = IN_ITEM_2.get(io);

					Vec3d pos = IN_POSITION_2.get(io);
					Pair<Float, Float> rotations = getRotationsFromDir(IN_DIRECTION_2.get(io));

					this.invokeContinuousUse(io, user, access, delegate, pos, rotations.getLeft(), rotations.getRight(), (d, i) -> {
						ItemStack stack = delegate.getHeldItemMainhand();

						if(i == 0) {
							ItemStack resultStack = stack.useItemRightClick(delegate.world, delegate, EnumHand.MAIN_HAND).getResult();

							if(resultStack != stack || resultStack.getCount() != i) {
								delegate.setHeldItem(EnumHand.MAIN_HAND, resultStack);

								if(resultStack.isEmpty()) {
									net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(delegate, stack, EnumHand.MAIN_HAND);
								}

								stack = resultStack;
								delegate.setHeldItem(EnumHand.MAIN_HAND, stack);
							}
						}

						if(!delegate.isHandActive() || stack.getMaxItemUseDuration() <= 0) {
							delegate.setHeldItem(EnumHand.MAIN_HAND, stack);

							return true;
						} else {
							stack.updateAnimation(delegate.world, delegate, 0 /*TODO Is this right? */, true);

							stack.getItem().onUsingTick(stack, delegate, i);

							if(i >= stack.getMaxItemUseDuration() || i >= 20 * 5) {
								stack.onPlayerStoppedUsing(delegate.world, delegate, 0);

								ItemStack resultStack = stack.onItemUseFinish(delegate.world, delegate);
								resultStack = net.minecraftforge.event.ForgeEventFactory.onItemUseFinish(delegate, stack, i, resultStack);
								delegate.setHeldItem(EnumHand.MAIN_HAND, resultStack);

								stack = resultStack;
								delegate.setHeldItem(EnumHand.MAIN_HAND, stack);

								return true;
							}
						}

						return false;
					});

				} else {
					IRuneItemStackAccess access = IN_ITEM_3.get(io);

					Entity target = IN_ENTITY_3.get(io);

					if(target != null) {
						this.invokeImmediateUse(io, user, access, delegate, new Vec3d(target.posX, target.posY, target.posZ), 0, 0, d -> {
							EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onInteractEntity(delegate, target, EnumHand.MAIN_HAND);
							if(cancelResult != null) {
								return;
							}

							ItemStack stack = delegate.getHeldItemMainhand();
							ItemStack copy = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();

							if(target.processInitialInteract(delegate, EnumHand.MAIN_HAND)) {
								if(stack.isEmpty()) {
									net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(delegate, copy, EnumHand.MAIN_HAND);
								}
							} else {
								if(!stack.isEmpty() && target instanceof EntityLivingBase) {
									if(stack.interactWithEntity(delegate, (EntityLivingBase) target, EnumHand.MAIN_HAND)) {
										if(stack.isEmpty()) {
											net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(delegate, copy, EnumHand.MAIN_HAND);
											delegate.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
										}
									}
								}
							}
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
