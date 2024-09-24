package thebetweenlands.common.block.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.block.entity.GeckoCageBlockEntity;
import thebetweenlands.common.component.item.DiscoveryContainerData;
import thebetweenlands.common.entity.creature.Gecko;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.List;

import static thebetweenlands.common.component.item.DiscoveryContainerData.AspectDiscovery;
import static thebetweenlands.common.component.item.DiscoveryContainerData.AspectDiscovery.DiscoveryResult;

public class GeckoCageBlock extends HorizontalBaseEntityBlock {

	public static final VoxelShape SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 2.0D),
		Block.box(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D),
		Block.box(0.0D, 0.0D, 14.0D, 2.0D, 2.0D, 16.0D),
		Block.box(14.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D),
		Block.box(0.0D, 2.0D, 0.0D, 16.0D, 16.0D, 16.0D)
	);

	public GeckoCageBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		if (!level.isClientSide()) {
			if (blockEntity instanceof GeckoCageBlockEntity cage) {
				if (cage.hasGecko()) {
					Gecko gecko = new Gecko(EntityRegistry.GECKO.get(), level);
					gecko.setHealth(cage.getGeckoUsages());
					gecko.moveTo(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0.0F, 0.0F);
					if (cage.getGeckoName() != null && !cage.getGeckoName().isEmpty())
						gecko.setCustomName(Component.literal(cage.getGeckoName()));
					level.addFreshEntity(gecko);
					gecko.playAmbientSound();
					if (player instanceof ServerPlayer sp) {
						AdvancementCriteriaRegistry.GECKO.get().trigger(sp, false, true);
					}
				}
			}
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof GeckoCageBlockEntity cage) {
			if (player.isShiftKeyDown())
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof MobItem mob) {
					if (!cage.hasGecko() && !level.isClientSide()) {
						if (mob.getCapturedEntityId(stack) == EntityRegistry.GECKO.getId()) {
							cage.addGecko(level, pos, state, Mth.floor(mob.getMobHealth(stack)), stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString());
							if (!player.isCreative())
								stack.shrink(1);
						}
						return ItemInteractionResult.SUCCESS;
					}

					return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				}
				if (stack.is(ItemRegistry.SAP_SPIT) && cage.hasGecko() && cage.getGeckoUsages() < 12) {
					if (!level.isClientSide()) {
						cage.setGeckoUsages(level, pos, state, 12);
						stack.consume(1, player);
					} else {
						this.spawnHeartParticles(level, pos);
					}
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				} else if (cage.getAspectType() == null) {
					if (cage.hasGecko()) {
						if (DiscoveryContainerData.hasDiscoveryProvider(player)) {
							if (!level.isClientSide()) {
								AspectManager manager = AspectManager.get(level);
								if (manager != null) {
									ResourceKey<AspectItem> aspectItem = AspectManager.getAspectItem(stack, level.registryAccess());
									List<Aspect> aspects = manager.getStaticAspects(aspectItem);
									if (!aspects.isEmpty()) {
										DiscoveryContainerData mergedKnowledge = DiscoveryContainerData.getMergedDiscoveryContainer(player);
										AspectDiscovery discovery = mergedKnowledge.discover(player, manager, aspectItem);
										switch (discovery.result) {
											case NEW:
											case LAST:
												DiscoveryContainerData.addDiscoveryToContainers(player, aspectItem, discovery.discovered.type());
												cage.setAspectType(level, pos, state, discovery.discovered.type(), 600);
												if (player instanceof ServerPlayer sp) {
													AdvancementCriteriaRegistry.GECKO.get().trigger(sp, true, false);
													if (discovery.result == DiscoveryResult.LAST && DiscoveryContainerData.getMergedDiscoveryContainer(player).haveDiscoveredAll(manager)) {
														AdvancementCriteriaRegistry.HERBLORE_FIND_ALL.get().trigger(sp);
													}
												}
												player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.discover_" + discovery.discovered.type().getKey().location().getPath().toLowerCase()), false);
												if (discovery.result == DiscoveryResult.LAST) {
													player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.last_aspect"), true);
													player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.last_aspect"), false);
												} else {
													player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.more_aspects"), true);
													player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.more_aspects"), false);
												}
												stack.consume(1, player);
												return ItemInteractionResult.sidedSuccess(level.isClientSide());
											case END:
												//already all discovered
												player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.all_discovered"), true);
												return ItemInteractionResult.CONSUME;
											default:
												//no aspects
												player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.no_aspects"), true);
												return ItemInteractionResult.CONSUME;
										}
									} else {
										player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.no_aspects"), true);
										return ItemInteractionResult.sidedSuccess(level.isClientSide());
									}
								} else {
									//no aspects
									return ItemInteractionResult.CONSUME;
								}
							}
						} else {
							//no herblore book
							if (!level.isClientSide())
								player.displayClientMessage(Component.translatable("cblock.thebetweenlands.gecko_cage.no_book"), true);
							return ItemInteractionResult.SUCCESS;
						}
					} else {
						//no gecko
						if (!level.isClientSide())
							player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.no_gecko"), true);
						return ItemInteractionResult.SUCCESS;
					}
				} else {
					//recovering
					if (!level.isClientSide())
						player.displayClientMessage(Component.translatable("block.thebetweenlands.gecko_cage.gecko_recovering"), true);
					return ItemInteractionResult.SUCCESS;
				}
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	protected void spawnHeartParticles(Level level, BlockPos pos) {
		for (int i = 0; i < 7; ++i) {
			double d0 = level.getRandom().nextGaussian() * 0.02D;
			double d1 = level.getRandom().nextGaussian() * 0.02D;
			double d2 = level.getRandom().nextGaussian() * 0.02D;
			level.addParticle(ParticleTypes.HEART, pos.getX() + level.getRandom().nextFloat(), pos.getY() + level.getRandom().nextFloat(), pos.getZ() + level.getRandom().nextFloat(), d0, d1, d2);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GeckoCageBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.GECKO_CAGE.get(), GeckoCageBlockEntity::tick);
	}
}
