package thebetweenlands.common.component;

public interface ISynchedAttachment<T extends ISynchedAttachment<T>> {
	
	public SynchedAttachmentType<T> getSynchedAttachmentType(AttachmentHolderIdentifier<?> attachee);

}
