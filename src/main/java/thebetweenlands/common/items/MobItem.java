package thebetweenlands.common.items;

import net.jodah.typetools.TypeResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobItem extends Item {

	private static final Map<EntityType<?>, Function<Entity, InteractionResult>> SPAWN_HANDLERS = new HashMap<>();

	public static final Function<Entity, InteractionResult> DEFAULT_SPAWN_HANDLER = entity -> {
		if ((entity.level().getDifficulty() != Difficulty.PEACEFUL || !(entity instanceof Enemy)) && entity.level().noBlockCollision(entity, entity.getBoundingBox())) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	};

	@Nullable
	private final EntityType<?> defaultMob;
	@Nullable
	private final Consumer<Entity> defaultMobSetter;


	@SuppressWarnings("unchecked")
	public <T extends Entity> MobItem(Item.Properties properties, @Nullable EntityType<?> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		super(properties);
		this.defaultMob = defaultMob;
		this.defaultMobSetter = (Consumer<Entity>) defaultMobSetter;
	}

	public static void registerSpawnHandler(EntityType<?> type, Function<Entity, InteractionResult> handler) {
		SPAWN_HANDLERS.merge(type, handler, (a, b) -> entity -> {
			InteractionResult result1 = b.apply(entity);
			if (result1 == InteractionResult.SUCCESS || result1 == InteractionResult.FAIL) {
				return result1;
			}
			InteractionResult result2 = a.apply(entity);
			if (result2 == InteractionResult.SUCCESS || result2 == InteractionResult.FAIL) {
				return result2;
			}
			return InteractionResult.PASS;
		});
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		ResourceLocation id = this.getCapturedEntityId(stack);
		if (id != null) {
			return BuiltInRegistries.ENTITY_TYPE.get(id).getDescriptionId();
		}
		return super.getDescriptionId(stack);
	}

	//TODO PLEASE find a better way to get the level
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (Minecraft.getInstance().level != null) {
			Level level = Minecraft.getInstance().level;
			Entity entity = this.createCapturedEntity(level, 0, 0, 0, stack, false);
			if (entity instanceof LivingEntity living) {
				tooltip.add(Component.translatable("tooltip.bl.item_mob.health", Mth.ceil(living.getHealth() / 2), Mth.ceil(living.getMaxHealth() / 2)));
			}
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		Vec3 hitVec = context.getClickLocation();

		final AtomicBoolean isNewEntity = new AtomicBoolean();

		Entity entity = this.createCapturedEntity(context.getLevel(), pos.getX() + hitVec.x(), pos.getY() + hitVec.y(), pos.getZ() + hitVec.z(), stack, e -> isNewEntity.set(true));
		if (entity != null) {
			if (direction.getStepX() != 0) {
				entity.setPos(entity.getX() + direction.getStepX() * entity.getBbWidth() * 0.5f, entity.getY(), entity.getZ());
			}
			if (direction.getStepY() < 0) {
				entity.setPos(entity.getX(), entity.getY() - entity.getBbHeight(), entity.getZ());
			}
			if (direction.getStepZ() != 0) {
				entity.setPos(entity.getX(), entity.getY(), entity.getZ() + direction.getStepZ() * entity.getBbWidth() * 0.5f);
			}

			Function<Entity, InteractionResult> spawnHandler = SPAWN_HANDLERS.get(entity.getType());
			if (spawnHandler == null) {
				spawnHandler = DEFAULT_SPAWN_HANDLER;
			}

			InteractionResult result = spawnHandler.apply(entity);

			if (result == InteractionResult.SUCCESS) {
				result = this.spawnCapturedEntity(context.getPlayer(), level, pos, context.getHand(), direction, hitVec, entity, isNewEntity.get());

				if (result == InteractionResult.SUCCESS) {
					if (!level.isClientSide()) {
						stack.shrink(1);
					}
				}
			}

			return result;
		}

		return InteractionResult.PASS;
	}

	public ItemStack capture(EntityType<?> type) {
		return this.capture(type, null);
	}

	public ItemStack capture(EntityType<?> type, @Nullable CompoundTag nbt) {
		ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
		if (id != null) {
			if (nbt == null) {
				nbt = new CompoundTag();
			}
			nbt.putString("id", id.toString());

			ItemStack stack = new ItemStack(this);

			stack.set(DataComponents.ENTITY_DATA, CustomData.of(nbt));

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public ItemStack capture(Entity entity) {
		CompoundTag nbt = new CompoundTag();

		if (this.getDefaultMaxStackSize() == 1) {
			entity.saveWithoutId(nbt);
		} else {
			CompoundTag entityNbt = new CompoundTag();
			if (entity.save(entityNbt) && entityNbt.contains("id", Tag.TAG_STRING)) {
				nbt.putString("id", entityNbt.getString("id"));
			}
		}

		if (!nbt.isEmpty()) {
			ItemStack stack = new ItemStack(this);

			stack.set(DataComponents.ENTITY_DATA, CustomData.of(nbt));

			if (entity.hasCustomName()) {
				stack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
			}

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public boolean isCapturedEntity(ItemStack stack, EntityType<?> type) {
		if (stack.getItem() != this) {
			return false;
		}

		if (stack.get(DataComponents.ENTITY_DATA) != null) {
			CompoundTag entityNbt = stack.get(DataComponents.ENTITY_DATA).copyTag();

			if (entityNbt.contains("id", Tag.TAG_STRING)) {
				EntityType<?> capturedType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityNbt.getString("id")));
				Class<?> cls = getEntityClass(type);
				Class<?> capturedCls = getEntityClass(capturedType);
				return cls != null && capturedCls != null && cls.isAssignableFrom(capturedCls);
			}
		} else {
			if (this.defaultMob != null) {
				Class<?> cls = getEntityClass(type);
				Class<?> defaultCls = getEntityClass(this.defaultMob);
				return cls != null && defaultCls != null && cls.isAssignableFrom(defaultCls);
			}
		}

		return false;
	}

	public boolean hasEntityData(ItemStack stack) {
		return stack.has(DataComponents.ENTITY_DATA);
	}

	@Nullable
	public CompoundTag getEntityData(ItemStack stack) {
		if (stack.get(DataComponents.ENTITY_DATA) != null) {
			return stack.get(DataComponents.ENTITY_DATA).copyTag();
		}
		return null;
	}

	public void setEntityData(ItemStack stack, @Nullable CompoundTag entityData) {
		if (entityData == null) {
			stack.remove(DataComponents.ENTITY_DATA);
		} else {
			stack.set(DataComponents.ENTITY_DATA, CustomData.of(entityData));
		}
	}

	@Nullable
	public ResourceLocation getCapturedEntityId(ItemStack stack) {
		if (!(stack.getItem() instanceof MobItem)) {
			return null;
		}

		if (stack.get(DataComponents.ENTITY_DATA) != null) {
			CompoundTag entityNbt = stack.get(DataComponents.ENTITY_DATA).copyTag();

			if (entityNbt.contains("id", Tag.TAG_STRING)) {
				return ResourceLocation.parse(entityNbt.getString("id"));
			}
		}

		if (this.defaultMob != null) {
			return BuiltInRegistries.ENTITY_TYPE.getKey(this.defaultMob);
		}

		return null;
	}

	protected static void handleOnInitialSpawn(Entity entity) {
		if (!entity.level().isClientSide() && entity instanceof Mob mob) {
			EventHooks.finalizeMobSpawn(mob, (ServerLevel) entity.level(), entity.level().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.BUCKET, null);
		}
	}

	@Nullable
	public Entity createCapturedEntity(Level level, double x, double y, double z, ItemStack stack, boolean allowOnInitialSpawn) {
		return this.createCapturedEntity(level, x, y, z, stack, entity -> {
			if (allowOnInitialSpawn) {
				handleOnInitialSpawn(entity);
			}
		});
	}

	@Nullable
	public Entity createCapturedEntity(Level level, double x, double y, double z, ItemStack stack, @Nullable Consumer<Entity> onNewEntityCreated) {
		if (stack.get(DataComponents.ENTITY_DATA) != null) {
			return this.createCapturedEntityFromNBT(level, x, y, z, stack.get(DataComponents.ENTITY_DATA).copyTag());
		}

		if (this.defaultMob != null) {
			Entity entity = this.defaultMob.create(level);
			if (entity != null) {
				entity.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0f, 0);
				if (this.defaultMobSetter != null) {
					this.defaultMobSetter.accept(entity);
				}
				if (onNewEntityCreated != null) {
					onNewEntityCreated.accept(entity);
				}
				return entity;
			}
		}

		return null;
	}

	@Nullable
	protected Entity createCapturedEntityFromNBT(Level level, double x, double y, double z, CompoundTag nbt) {
		Optional<Entity> entity = EntityType.create(nbt, level);

		if (entity.isPresent()) {
			entity.get().moveTo(x, y, z, level.getRandom().nextFloat() * 360.0f, 0);
			entity.get().setDeltaMovement(Vec3.ZERO);
			return entity.get();
		}

		return null;
	}

	protected InteractionResult spawnCapturedEntity(Player player, Level level, BlockPos pos, InteractionHand hand, Direction facing, Vec3 hitVec, Entity entity, boolean isNewEntity) {
		if (!level.isClientSide()) {
			if (isNewEntity) {
				handleOnInitialSpawn(entity);
			}

			level.addFreshEntity(entity);

			if (entity instanceof Mob mob) {
				mob.playAmbientSound();
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void onCapturedByPlayer(Player player, InteractionHand hand, ItemStack captured, LivingEntity entity) {

	}

	@SuppressWarnings("unchecked")
	@Nullable
	//this is actual insanity
	private static <T extends Entity> Class<T> getEntityClass(EntityType<T> type) {
		final Class<T> entityClass = (Class<T>) TypeResolver.resolveRawArgument(EntityType.EntityFactory.class, type.factory.getClass());
		if ((Class<?>) entityClass == TypeResolver.Unknown.class) {
			TheBetweenlands.LOGGER.error("Couldn't resolve entity class provided for entity {}", type.getDescriptionId());
			return null;
		}
		return entityClass;
	}
}
