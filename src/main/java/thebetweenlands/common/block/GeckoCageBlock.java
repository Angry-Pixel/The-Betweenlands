package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.common.block.entity.GeckoCageBlockEntity;
import thebetweenlands.common.entities.Gecko;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class GeckoCageBlock extends HorizontalBaseEntityBlock {

	public GeckoCageBlock(Properties properties) {
		super(properties);
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
			if (player.isCrouching())
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

			if (!stack.isEmpty()) {
//				if (stack.getItem() instanceof CritterItem critter) {
//					if (!cage.hasGecko() && !level.isClientSide()) {
//						Entity entity = critter.createCapturedEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack, true);
//						if (entity instanceof EntityGecko gecko) {
//							cage.addGecko(level, pos, state, gecko.getHealth(), gecko.hasCustomName() ? gecko.getCustomName().getString() : null);
//							if (!player.isCreative())
//								stack.shrink(1);
//						}
//						return ItemInteractionResult.SUCCESS;
//					}
//
//					return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
//				}
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
						if (DiscoveryContainer.hasDiscoveryProvider(player)) {
							if (!level.isClientSide()) {
								AspectManager manager = AspectManager.get(level);
								AspectItem aspectItem = AspectManager.getAspectItem(stack);
								List<Aspect> aspects = manager.getStaticAspects(aspectItem);
								if (aspects.size() > 0) {
									DiscoveryContainer<?> mergedKnowledge = DiscoveryContainer.getMergedDiscoveryContainer(player);
									DiscoveryContainer.AspectDiscovery discovery = mergedKnowledge.discover(manager, aspectItem);
									switch (discovery.result) {
										case NEW:
										case LAST:
											DiscoveryContainer.addDiscoveryToContainers(player, aspectItem, discovery.discovered.type);
											cage.setAspectType(level, pos, state, discovery.discovered.type, 600);
											if (player instanceof ServerPlayer sp) {
												AdvancementCriteriaRegistry.GECKO.get().trigger(sp, true, false);
												if (discovery.result == DiscoveryContainer.AspectDiscovery.DiscoveryResult.LAST && DiscoveryContainer.getMergedDiscoveryContainer(player).haveDiscoveredAll(manager)) {
													AdvancementCriteriaRegistry.HERBLORE_FIND_ALL.get().trigger(sp);
												}
											}
											player.displayClientMessage(Component.translatable("chat.aspect.discovery." + discovery.discovered.type.getName().toLowerCase()), false);
											if (discovery.result == DiscoveryContainer.AspectDiscovery.DiscoveryResult.LAST) {
												player.displayClientMessage(Component.translatable("chat.aspect.discovery.last"), true);
												player.displayClientMessage(Component.translatable("chat.aspect.discovery.last"), false);
											} else {
												player.displayClientMessage(Component.translatable("chat.aspect.discovery.more"), true);
												player.displayClientMessage(Component.translatable("chat.aspect.discovery.more"), false);
											}
											stack.consume(1, player);
											return ItemInteractionResult.sidedSuccess(level.isClientSide());
										case END:
											//already all discovered
											player.displayClientMessage(Component.translatable("chat.aspect.discovery.end"), true);
											return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
										default:
											//no aspects
											player.displayClientMessage(Component.translatable("chat.aspect.discovery.none"), true);
											return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
									}
								} else {
									player.displayClientMessage(Component.translatable("chat.aspect.discovery.none"), true);
									return ItemInteractionResult.sidedSuccess(level.isClientSide());
								}
							} else {
								//no aspects
								return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
							}
						} else {
							//no herblore book
							if (!level.isClientSide())
								player.displayClientMessage(Component.translatable("chat.aspect.discovery.book.none"), true);
							return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
						}
					} else {
						//no gecko
						if (!level.isClientSide())
							player.displayClientMessage(Component.translatable("chat.aspect.discovery.gecko.none"), true);
						return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
					}
				} else {
					//recovering
					if (!level.isClientSide())
						player.displayClientMessage(Component.translatable("chat.aspect.discovery.gecko.recovering"), true);
					return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
