package thebetweenlands.common.network.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.api.network.GenericDataAccessorAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;

import javax.annotation.Nullable;
import java.util.List;

public record SyncEnvironmentEventDataPacket(ResourceLocation eventName, List<GenericDataAccessorAccess.IDataEntry<?>> entries) implements CustomPacketPayload {

	public static final Type<SyncEnvironmentEventDataPacket> TYPE = new Type<>(TheBetweenlands.prefix("sync_environment_event_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncEnvironmentEventDataPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncEnvironmentEventDataPacket::serialize, SyncEnvironmentEventDataPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private SyncEnvironmentEventDataPacket(RegistryFriendlyByteBuf buffer) {
		this(buffer.readResourceLocation(), GenericDataAccessor.readEntries(buffer));
	}

	public SyncEnvironmentEventDataPacket(EnvironmentEvent event, boolean sendAll) {
		this(BLRegistries.ENVIRONMENT_EVENTS.getKey(event), getEntries(event.getDataManager(), sendAll));
	}

	private static List<GenericDataAccessorAccess.IDataEntry<?>> getEntries(@Nullable GenericDataAccessorAccess accessor, boolean sendAll) {
		if (accessor != null) {
			if (sendAll) {
				var list = accessor.getAll();
				accessor.setClean();
				return list;
			} else {
				return accessor.getDirty();
			}
		}
		return List.of();
	}

	public void serialize(RegistryFriendlyByteBuf buffer) {
		buffer.writeResourceLocation(this.eventName);
		GenericDataAccessor.writeEntries(this.entries, buffer);
	}

	public static void handle(SyncEnvironmentEventDataPacket packet, IPayloadContext context) {
		if (context.flow().isClientbound()) {
			context.enqueueWork(() -> {
				Level level = context.player().level();
				BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(level);
				if (storage != null) {
					BLEnvironmentEventRegistry registry = storage.getEnvironmentEventRegistry();
					EnvironmentEvent event = registry.forName(packet.eventName());
					GenericDataAccessorAccess dataManager = event.getDataManager();
					if (dataManager != null) {
						dataManager.setValuesFromPacket(packet.entries);
					}
					if (!event.isLoaded()) {
						event.setLoaded(level);
					}
				}
			});
		}
	}
}
