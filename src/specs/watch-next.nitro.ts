import type { HybridObject } from "react-native-nitro-modules";

export type ProgramType =
	| "movie"
	| "tvSeries"
	| "tvSeason"
	| "tvEpisode"
	| "clip"
	| "event"
	| "channel"
	| "track"
	| "album"
	| "artist"
	| "playlist"
	| "station"
	| "game";
export type WatchNextType = "continue" | "next" | "new" | "watchlist";
export type AspectRatio =
	| "16:9"
	| "3:2"
	| "4:3"
	| "1:1"
	| "2:3"
	| "3:4"
	| "moviePoster";
export type Availability =
	| "available"
	| "freeWithSubscription"
	| "paidContent"
	| "purchased"
	| "free"
	| "freeWithAds";
export type InteractionType =
	| "views"
	| "listens"
	| "followers"
	| "fans"
	| "likes"
	| "thumbs"
	| "viewers";
export type ReviewRatingStyle = "stars" | "thumbsUpDown" | "percentage";
export type TvSeriesItemType = "episode" | "chapter";

// see androidx.tvprovider's `WatchNextProgram`
export interface WatchNextProgram {
	title?: string;
	episodeTitle?: string;
	seasonNumber?: number;
	episodeNumber?: number;
	seasonTitle?: string;
	description?: string;
	longDescription?: string;
	videoWidth?: number;
	videoHeight?: number;
	canonicalGenres?: string[];
	contentRatings?: string[];
	audioLanguages?: string[];
	posterArtUri?: string;
	thumbnailUri?: string;
	searchable?: boolean;
	internalProviderData?: ArrayBuffer;
	internalProviderFlag1?: number;
	internalProviderFlag2?: number;
	internalProviderFlag3?: number;
	internalProviderFlag4?: number;
	reviewRatingStyle?: ReviewRatingStyle;
	reviewRating?: string;

	internalProviderId?: string;
	contentId?: string;
	type?: ProgramType;
	previewVideoUri?: string;
	previewAudioUri?: string;
	lastPlaybackPositionMillis?: number;
	durationMillis?: number;
	intentUri?: string;
	transient?: boolean;
	posterArtAspectRatio?: AspectRatio;
	thumbnailAspectRatio?: AspectRatio;
	logoUri?: string;
	logoContentDescription?: string;
	availability?: Availability;
	startingPrice?: string;
	offerPrice?: string;
	releaseDate?: string;
	itemCount?: number;
	live?: boolean;
	interactionType?: InteractionType;
	interactionCount?: number;
	author?: string;
	browsable?: boolean;
	genre?: string;
	startTimeUtcMillis?: number;
	endTimeUtcMillis?: number;
	tvSeriesItemType?: TvSeriesItemType;

	watchNextType?: WatchNextType;
	lastEngagementTimeUtcMillis?: number;
}

export interface StoredWatchNextProgram extends WatchNextProgram {
	id: number;
	packageName: string;
}

export interface WatchNext extends HybridObject<{ android: "kotlin" }> {
	// only returns programs inserted by this app
	list(): Promise<StoredWatchNextProgram[]>;
	insert(program: WatchNextProgram): Promise<number>;
	// only the given fields are updated
	update(id: number, program: WatchNextProgram): Promise<number>;
	remove(id: number): Promise<number>;
}
