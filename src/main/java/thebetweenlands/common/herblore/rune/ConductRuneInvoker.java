package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public ConductRuneInvoker create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new ConductRuneInvoker(this, index, composition, (RuneConfiguration) configuration);
		}

		private void returnExcessItems(IRuneChainUser user, List<NonNullList<ItemStack>> excess) {
			Entity entity = user.getEntity();

			if(entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;

				for(NonNullList<ItemStack> stacks : excess) {
					for(ItemStack stack : stacks) {
						if(!stack.isEmpty()) {
							if(!player.inventory.addItemStackToInventory(stack)) {
								player.entityDropItem(stack, 0);
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
								Vec3d pos = user.getPosition();
								EntityItem entityitem = new EntityItem(user.getWorld(), pos.x, pos.y, pos.z, stack);
								entityitem.setDefaultPickupDelay();
								user.getWorld().spawnEntity(entityitem);
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

		@Override
		protected RuneEffectModifier.Subject activate(ConductRuneInvoker state, RuneExecutionContext context, INodeIO io) {

			if(context.getUser().getWorld() instanceof WorldServer) {
				WorldServer world = (WorldServer) context.getUser().getWorld();

				IRuneChainUser user = context.getUser();
				Entity entity = user.getEntity();

				List<NonNullList<ItemStack>> excess = new ArrayList<>();

				EntityPlayerDelegate.Builder delegateBuilder = EntityPlayerDelegate.from(world, new GameProfile(UUID.randomUUID(), "[RuneChain]"), excess);

				delegateBuilder.entity(entity);

				if(entity instanceof EntityPlayer) {
					delegateBuilder.playerInventory(((EntityPlayer) entity).inventory);
				} else {
					delegateBuilder.mainInventory(context.getUser().getInventory());
				}

				EntityPlayerMP delegate = delegateBuilder.build();

				if(state.getConfiguration() == CONFIGURATION_1) {
					IRuneItemStackAccess access = IN_ITEM_1.get(io);
					ItemStack inputStack = access.get();

					if(!inputStack.isEmpty() && access.set(ItemStack.EMPTY)) {
						BlockPos block = IN_POSITION_1.get(io);
						Pair<Float, Float> rotations = getRotationsFromDir(IN_DIRECTION_1.get(io));

						delegate.setLocationAndAngles(block.getX() + 0.5f, block.getY() + 0.5f - delegate.getEyeHeight(), block.getZ() + 0.5f, rotations.getLeft(), rotations.getRight());

						ItemStack prevHeldStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

						delegate.setHeldItem(EnumHand.MAIN_HAND, inputStack);

						if(inputStack.onItemUseFirst(delegate, delegate.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f) == EnumActionResult.PASS) {
							inputStack.onItemUse(delegate, delegate.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f);
						}

						ItemStack outputStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

						if(!access.set(outputStack)) {
							InventoryHelper.spawnItemStack(context.getUser().getWorld(), block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f, outputStack);
						}

						delegate.setHeldItem(EnumHand.MAIN_HAND, prevHeldStack);

						delegate.setDead();

						this.returnExcessItems(user, excess);
					}
				} else {
					IRuneItemStackAccess access = IN_ITEM_2.get(io);
					ItemStack[] inputStack = new ItemStack[] { access.get() };

					if(!inputStack[0].isEmpty() && access.set(ItemStack.EMPTY)) {
						Vec3d position = IN_POSITION_2.get(io);
						Pair<Float, Float> rotations = getRotationsFromDir(IN_DIRECTION_2.get(io));

						io.schedule(scheduler -> {
							boolean terminated = false;

							int i = scheduler.getUpdateCount();

							delegate.setLocationAndAngles(position.x, position.y - delegate.getEyeHeight(), position.z, rotations.getLeft(), rotations.getRight());

							ItemStack prevHeldStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

							delegate.setHeldItem(EnumHand.MAIN_HAND, inputStack[0]);

							if(i == 0) {
								ItemStack resultStack = inputStack[0].useItemRightClick(delegate.world, delegate, EnumHand.MAIN_HAND).getResult();

								if(resultStack != inputStack[0] || resultStack.getCount() != i) {
									delegate.setHeldItem(EnumHand.MAIN_HAND, resultStack);

									if(resultStack.isEmpty()) {
										net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(delegate, inputStack[0], EnumHand.MAIN_HAND);
									}

									inputStack[0] = resultStack;
								}
							}

							if(!delegate.isHandActive() || inputStack[0].getMaxItemUseDuration() <= 0) {
								delegate.setDead();

								scheduler.terminate();
								terminated = true;
							} else {
								inputStack[0].updateAnimation(delegate.world, delegate, 0 /*TODO Is this right? */, true);

								inputStack[0].getItem().onUsingTick(inputStack[0], delegate, i);

								if(i >= inputStack[0].getMaxItemUseDuration() || i >= 20 * 5) {
									inputStack[0].onPlayerStoppedUsing(delegate.world, delegate, 0);

									ItemStack resultStack = inputStack[0].onItemUseFinish(delegate.world, delegate);
									resultStack = net.minecraftforge.event.ForgeEventFactory.onItemUseFinish(delegate, inputStack[0], i, resultStack);
									delegate.setHeldItem(EnumHand.MAIN_HAND, resultStack);

									inputStack[0] = resultStack;

									delegate.setDead();

									scheduler.terminate();
									terminated = true;
								}
							}

							ItemStack outputStack = delegate.getHeldItem(EnumHand.MAIN_HAND);

							inputStack[0] = outputStack;

							if(terminated && !access.set(outputStack)) {
								InventoryHelper.spawnItemStack(context.getUser().getWorld(), position.x, position.y, position.z, outputStack);
							}

							delegate.setHeldItem(EnumHand.MAIN_HAND, prevHeldStack);

							this.returnExcessItems(user, excess);

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
