package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record HerbloreBookData(List<String> discoveredPages, CurrentPage page) {

	public static final Codec<HerbloreBookData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.listOf().fieldOf("discovered_pages").forGetter(HerbloreBookData::discoveredPages),
		CurrentPage.CODEC.fieldOf("current_page").forGetter(HerbloreBookData::page)
	).apply(instance, HerbloreBookData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, HerbloreBookData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), HerbloreBookData::discoveredPages,
		CurrentPage.STREAM_CODEC, HerbloreBookData::page,
		HerbloreBookData::new
	);

	public HerbloreBookData addPages(String... page) {
		List<String> pagesCopy = new ArrayList<>(this.discoveredPages());
		pagesCopy.addAll(List.of(page));
		return new HerbloreBookData(pagesCopy, this.page());
	}

	public HerbloreBookData setCurrentPage(String category, int number) {
		return new HerbloreBookData(this.discoveredPages(), new CurrentPage(category, number));
	}

	public record CurrentPage(String category, int number) {
		public static final Codec<CurrentPage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("category").forGetter(CurrentPage::category),
			Codec.INT.fieldOf("number").forGetter(CurrentPage::number)
		).apply(instance, CurrentPage::new));

		public static final StreamCodec<? super RegistryFriendlyByteBuf, CurrentPage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, CurrentPage::category,
			ByteBufCodecs.INT, CurrentPage::number,
			CurrentPage::new
		);
	}
}
