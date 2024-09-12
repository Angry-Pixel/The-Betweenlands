package thebetweenlands.common.component;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SimpleAttachmentType<T> {
    private final Class<T> attachmentClass;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> serializer;
    private final ResourceKey<AttachmentType<?>> attachmentKey;

    public SimpleAttachmentType(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> serializer, ResourceKey<AttachmentType<?>> attachmentKey) {
    	this.attachmentClass = clazz;
    	this.serializer = serializer;
    	this.attachmentKey = attachmentKey;
    }

    public boolean canSerialize(Object instance) {
    	return this.attachmentClass.isAssignableFrom(instance.getClass());
    }

    public void serialize(RegistryFriendlyByteBuf buffer, T instance) {
    	this.serializer.encode(buffer, instance);
    }

	public boolean canDeserialize() {
    	return NeoForgeRegistries.ATTACHMENT_TYPES.containsKey(this.getAttachmentKey().cast(NeoForgeRegistries.Keys.ATTACHMENT_TYPES).get());
    }
	
    public T deserialize(RegistryFriendlyByteBuf buffer) {
    	return this.serializer.decode(buffer);
    }
    
    public ResourceKey<AttachmentType<?>> getAttachmentKey() {
    	return this.attachmentKey;
    }
}
