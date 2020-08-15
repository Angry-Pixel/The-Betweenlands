package thebetweenlands.common.herblore.rune;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
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

				EntityPlayerMP fakePlayer = new FakePlayer(world, new GameProfile(UUID.randomUUID(), "[RuneChain]")) {
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
				};
				
				Entity entity = context.getUser().getEntity();
				if(entity instanceof EntityPlayer) {
					fakePlayer.inventory = ((EntityPlayer) entity).inventory;
				}
				
				fakePlayer.connection = new NetHandlerPlayServer(world.getMinecraftServer(), new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer);

				if(state.getConfiguration() == CONFIGURATION_1) {
					IRuneItemStackAccess access = IN_ITEM_1.get(io);
					ItemStack stack = access.get();

					if(!stack.isEmpty()) {
						BlockPos block = IN_POSITION_1.get(io);

						fakePlayer.setLocationAndAngles(block.getX() + 0.5f, block.getY() + 0.5f - fakePlayer.getEyeHeight(), block.getZ() + 0.5f, fakePlayer.rotationYaw, fakePlayer.rotationPitch);

						ItemStack prevStack = fakePlayer.getHeldItem(EnumHand.MAIN_HAND);
						
						fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stack);

						if(stack.onItemUseFirst(fakePlayer, fakePlayer.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f) == EnumActionResult.PASS) {
							stack.onItemUse(fakePlayer, fakePlayer.world, block, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f);
						}
						
						access.set(fakePlayer.getHeldItem(EnumHand.MAIN_HAND));

						fakePlayer.setHeldItem(EnumHand.MAIN_HAND, prevStack);
						
						fakePlayer.setDead();
					}
				} else {
					IRuneItemStackAccess access = IN_ITEM_2.get(io);
					ItemStack stack = access.get();

					if(!stack.isEmpty()) {
						Vec3d position = IN_POSITION_2.get(io);

						io.schedule(scheduler -> {
							int i = scheduler.getUpdateCount();

							fakePlayer.setLocationAndAngles(position.x, position.y - fakePlayer.getEyeHeight(), position.z, fakePlayer.rotationYaw, fakePlayer.rotationPitch);

							ItemStack prevStack = fakePlayer.getHeldItem(EnumHand.MAIN_HAND);
							
							fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stack);
							
							if(i == 0) {
								ItemStack resultStack = stack.useItemRightClick(fakePlayer.world, fakePlayer, EnumHand.MAIN_HAND).getResult();
								
								if(resultStack != stack || resultStack.getCount() != i) {
									fakePlayer.setHeldItem(EnumHand.MAIN_HAND, resultStack);

									if(resultStack.isEmpty()) {
										net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(fakePlayer, stack, EnumHand.MAIN_HAND);
									}
								}
							}
							
							if(!fakePlayer.isHandActive() || stack.getMaxItemUseDuration() <= 0) {
								fakePlayer.setDead();
								
								scheduler.terminate();
							} else {
								stack.getItem().onUsingTick(stack, fakePlayer, i);
								
								if(i >= stack.getMaxItemUseDuration() || i >= 20 * 5) {
									stack.onPlayerStoppedUsing(fakePlayer.world, fakePlayer, 0);

									stack.onItemUseFinish(fakePlayer.world, fakePlayer);
									
									fakePlayer.setDead();

									scheduler.terminate();
								}
							}

							access.set(fakePlayer.getHeldItem(EnumHand.MAIN_HAND));

							fakePlayer.setHeldItem(EnumHand.MAIN_HAND, prevStack);
							
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
