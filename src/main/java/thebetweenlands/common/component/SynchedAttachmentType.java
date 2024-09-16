package thebetweenlands.common.component;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.attachment.AttachmentType;

public class SynchedAttachmentType<T> {
    private final Function<AttachmentHolderIdentifier<?>, StreamCodec<? super RegistryFriendlyByteBuf, T>> serializerSupplier;
    private final ResourceKey<AttachmentType<?>> attachmentKey;

    public SynchedAttachmentType(ResourceKey<AttachmentType<?>> attachmentKey, Function<AttachmentHolderIdentifier<?>, StreamCodec<? super RegistryFriendlyByteBuf, T>> serializerSupplier) {
    	this.serializerSupplier = serializerSupplier;
    	this.attachmentKey = attachmentKey;
    }

    public SynchedAttachmentType(ResourceKey<AttachmentType<?>> attachmentKey, Supplier<StreamCodec<? super RegistryFriendlyByteBuf, T>> serializerSupplier) {
    	this(attachmentKey, type -> serializerSupplier.get());
    }
    
    public ResourceKey<AttachmentType<?>> getAttachmentKey() {
    	return this.attachmentKey;
    }
    
    public StreamCodec<? super RegistryFriendlyByteBuf, T> getStreamCodec(AttachmentHolderIdentifier<?> holderIdentifier) {
    	return this.serializerSupplier.apply(holderIdentifier);
    }


//    public static <T> Builder<T> builder(Holder<AttachmentType<?>> attachment) {
//    	return new Builder<>(attachment.getKey());
//    }
//    
//    public static <T> Builder<T> builder(ResourceKey<AttachmentType<?>> attachmentKey) {
//    	return new Builder<T>(attachmentKey);
//    }
//    
//    public static class Builder<T> {
//        private Function<AttachmentHolderIdentifier<?>, StreamCodec<? super RegistryFriendlyByteBuf, T>> serializerSupplier;
//        private final ResourceKey<AttachmentType<?>> attachmentKey;
//        
//    	public Builder(ResourceKey<AttachmentType<?>> attachmentKey) {
//    		this.attachmentKey = attachmentKey;
//    	}
//
//    	public Builder<T> serialize(Supplier<StreamCodec<? super RegistryFriendlyByteBuf, T>> serializerSupplier) {
//    		this.serializerSupplier = (type -> serializerSupplier.get());
//    		return this;
//    	}
//
//    	public Builder<T> serialize(Function<AttachmentHolderIdentifier<?>, StreamCodec<? super RegistryFriendlyByteBuf, T>> serializerSupplier) {
//    		this.serializerSupplier = serializerSupplier;
//    		return this;
//    	}
//    	
//    	public SynchedAttachmentType<T> build() {
//    		return new SynchedAttachmentType<T>(this.attachmentKey, this.serializerSupplier);
//    	}
//    }
}
