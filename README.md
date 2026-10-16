# react-native-watch-next

Publish programs to the android tv "Play Next"/"Continue watching" row, built with [Nitro](https://nitro.margelo.com).

This is a thin wrapper over androidx.tvprovider's [`WatchNextProgram`](https://developer.android.com/reference/androidx/tvprovider/media/tv/WatchNextProgram).

## Installation

```bash
bun add react-native-watch-next react-native-nitro-modules
```

## Usage

```ts
import { WatchNext } from "react-native-watch-next";

// `WatchNext` is null outside of android tv.
if (WatchNext) {
	const id = await WatchNext.insert({
		internalProviderId: "movie-123",
		type: "movie",
		watchNextType: "continue",
		title: "The Great Adventure",
		posterArtUri: "https://example.com/poster.jpg",
		posterArtAspectRatio: "2:3",
		intentUri: "myapp://watch/movie-123",
		lastPlaybackPositionMillis: 452_000,
		durationMillis: 5_400_000,
		lastEngagementTimeUtcMillis: Date.now(),
	});

	// only returns programs inserted by this app
	const programs = await WatchNext.list();

	// only the given fields are updated
	await WatchNext.update(id, { lastPlaybackPositionMillis: 600_000 });

	await WatchNext.remove(id);
}
```
