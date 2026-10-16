package dev.zoriya.watchnext

import android.content.ContentResolver
import android.content.ContentUris
import android.media.tv.TvContentRating
import android.net.Uri
import androidx.annotation.Keep
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.TvContractCompat.PreviewProgramColumns
import androidx.tvprovider.media.tv.TvContractCompat.WatchNextPrograms
import androidx.tvprovider.media.tv.WatchNextProgram as TvWatchNextProgram
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.margelo.nitro.watchnext.AspectRatio
import com.margelo.nitro.watchnext.Availability
import com.margelo.nitro.watchnext.HybridWatchNextSpec
import com.margelo.nitro.watchnext.InteractionType
import com.margelo.nitro.watchnext.ProgramType
import com.margelo.nitro.watchnext.ReviewRatingStyle
import com.margelo.nitro.watchnext.StoredWatchNextProgram
import com.margelo.nitro.watchnext.TvSeriesItemType
import com.margelo.nitro.watchnext.WatchNextProgram
import com.margelo.nitro.watchnext.WatchNextType

@Keep
@DoNotStrip
class HybridWatchNext : HybridWatchNextSpec() {
  private val resolver: ContentResolver
    get() = (NitroModules.applicationContext ?: throw Error("Missing context")).contentResolver

  override fun list() = Promise.parallel {
    resolver.query(WatchNextPrograms.CONTENT_URI, TvWatchNextProgram.PROJECTION, null, null, null)?.use { cursor ->
      buildList {
        while (cursor.moveToNext()) add(fromTv(TvWatchNextProgram.fromCursor(cursor)))
      }.toTypedArray()
    } ?: emptyArray()
  }

  override fun insert(program: WatchNextProgram) = Promise.parallel {
    val uri = resolver.insert(WatchNextPrograms.CONTENT_URI, toTv(program).toContentValues())
      ?: throw Error("Could not insert the program")
    ContentUris.parseId(uri).toDouble()
  }

  override fun update(id: Double, program: WatchNextProgram) = Promise.parallel {
    resolver.update(TvContractCompat.buildWatchNextProgramUri(id.toLong()), toTv(program).toContentValues(), null, null).toDouble()
  }

  override fun remove(id: Double) = Promise.parallel {
    resolver.delete(TvContractCompat.buildWatchNextProgramUri(id.toLong()), null, null).toDouble()
  }
}

private fun toTv(p: WatchNextProgram): TvWatchNextProgram = TvWatchNextProgram.Builder().apply {
  p.title?.let { setTitle(it) }
  p.episodeTitle?.let { setEpisodeTitle(it) }
  p.seasonNumber?.let { setSeasonNumber(it.toInt()) }
  p.episodeNumber?.let { setEpisodeNumber(it.toInt()) }
  p.seasonTitle?.let { setSeasonTitle(it) }
  p.description?.let { setDescription(it) }
  p.longDescription?.let { setLongDescription(it) }
  p.videoWidth?.let { setVideoWidth(it.toInt()) }
  p.videoHeight?.let { setVideoHeight(it.toInt()) }
  p.canonicalGenres?.let { setCanonicalGenres(it) }
  p.contentRatings?.let { setContentRatings(it.map(TvContentRating::unflattenFromString).toTypedArray()) }
  p.audioLanguages?.let { setAudioLanguages(it) }
  p.posterArtUri?.let { setPosterArtUri(Uri.parse(it)) }
  p.thumbnailUri?.let { setThumbnailUri(Uri.parse(it)) }
  p.searchable?.let { setSearchable(it) }
  p.reviewRatingStyle?.let { setReviewRatingStyle(it.toTv()) }
  p.reviewRating?.let { setReviewRating(it) }

  p.internalProviderId?.let { setInternalProviderId(it) }
  p.contentId?.let { setContentId(it) }
  p.type?.let { setType(it.toTv()) }
  p.previewVideoUri?.let { setPreviewVideoUri(Uri.parse(it)) }
  p.previewAudioUri?.let { setPreviewAudioUri(Uri.parse(it)) }
  p.lastPlaybackPositionMillis?.let { setLastPlaybackPositionMillis(it.toInt()) }
  p.durationMillis?.let { setDurationMillis(it.toInt()) }
  p.intentUri?.let { setIntentUri(Uri.parse(it)) }
  p.transient?.let { setTransient(it) }
  p.posterArtAspectRatio?.let { setPosterArtAspectRatio(it.toTv()) }
  p.thumbnailAspectRatio?.let { setThumbnailAspectRatio(it.toTv()) }
  p.logoUri?.let { setLogoUri(Uri.parse(it)) }
  p.logoContentDescription?.let { setLogoContentDescription(it) }
  p.availability?.let { setAvailability(it.toTv()) }
  p.startingPrice?.let { setStartingPrice(it) }
  p.offerPrice?.let { setOfferPrice(it) }
  p.releaseDate?.let { setReleaseDate(it) }
  p.itemCount?.let { setItemCount(it.toInt()) }
  p.live?.let { setLive(it) }
  p.interactionType?.let { setInteractionType(it.toTv()) }
  p.interactionCount?.let { setInteractionCount(it.toLong()) }
  p.author?.let { setAuthor(it) }
  p.genre?.let { setGenre(it) }
  p.startTimeUtcMillis?.let { setStartTimeUtcMillis(it.toLong()) }
  p.endTimeUtcMillis?.let { setEndTimeUtcMillis(it.toLong()) }
  p.tvSeriesItemType?.let { setTvSeriesItemType(it.toTv()) }

  p.watchNextType?.let { setWatchNextType(it.toTv()) }
  p.lastEngagementTimeUtcMillis?.let { setLastEngagementTimeUtcMillis(it.toLong()) }
}.build()

// unset int/long columns are returned as -1 by the getters
private fun Int.orNull() = takeIf { it != -1 }?.toDouble()
private fun Long.orNull() = takeIf { it != -1L }?.toDouble()

private fun fromTv(p: TvWatchNextProgram) = StoredWatchNextProgram(
  id = p.id.toDouble(),

  title = p.title,
  episodeTitle = p.episodeTitle,
  seasonNumber = p.seasonNumber?.toDoubleOrNull(),
  episodeNumber = p.episodeNumber?.toDoubleOrNull(),
  seasonTitle = p.seasonTitle,
  description = p.description,
  longDescription = p.longDescription,
  videoWidth = p.videoWidth.orNull(),
  videoHeight = p.videoHeight.orNull(),
  canonicalGenres = p.canonicalGenres,
  contentRatings = p.contentRatings?.map { it.flattenToString() }?.toTypedArray(),
  audioLanguages = p.audioLanguages,
  posterArtUri = p.posterArtUri?.toString(),
  thumbnailUri = p.thumbnailUri?.toString(),
  searchable = p.isSearchable,
  reviewRatingStyle = ReviewRatingStyle.entries.find { it.toTv() == p.reviewRatingStyle },
  reviewRating = p.reviewRating,

  internalProviderId = p.internalProviderId,
  contentId = p.contentId,
  type = ProgramType.entries.find { it.toTv() == p.type },
  previewVideoUri = p.previewVideoUri?.toString(),
  previewAudioUri = p.previewAudioUri?.toString(),
  lastPlaybackPositionMillis = p.lastPlaybackPositionMillis.orNull(),
  durationMillis = p.durationMillis.orNull(),
  intentUri = p.intentUri?.toString(),
  transient = p.isTransient,
  posterArtAspectRatio = AspectRatio.entries.find { it.toTv() == p.posterArtAspectRatio },
  thumbnailAspectRatio = AspectRatio.entries.find { it.toTv() == p.thumbnailAspectRatio },
  logoUri = p.logoUri?.toString(),
  logoContentDescription = p.logoContentDescription,
  availability = Availability.entries.find { it.toTv() == p.availability },
  startingPrice = p.startingPrice,
  offerPrice = p.offerPrice,
  releaseDate = p.releaseDate,
  itemCount = p.itemCount.orNull(),
  live = p.isLive,
  interactionType = InteractionType.entries.find { it.toTv() == p.interactionType },
  interactionCount = p.interactionCount.orNull(),
  author = p.author,
  browsable = p.isBrowsable,
  genre = p.genre,
  startTimeUtcMillis = p.startTimeUtcMillis.orNull(),
  endTimeUtcMillis = p.endTimeUtcMillis.orNull(),
  tvSeriesItemType = TvSeriesItemType.entries.find { it.toTv() == p.tvSeriesItemType },

  watchNextType = WatchNextType.entries.find { it.toTv() == p.watchNextType },
  lastEngagementTimeUtcMillis = p.lastEngagementTimeUtcMillis.orNull(),
)

private fun ProgramType.toTv() = when (this) {
  ProgramType.MOVIE -> PreviewProgramColumns.TYPE_MOVIE
  ProgramType.TVSERIES -> PreviewProgramColumns.TYPE_TV_SERIES
  ProgramType.TVSEASON -> PreviewProgramColumns.TYPE_TV_SEASON
  ProgramType.TVEPISODE -> PreviewProgramColumns.TYPE_TV_EPISODE
  ProgramType.CLIP -> PreviewProgramColumns.TYPE_CLIP
  ProgramType.EVENT -> PreviewProgramColumns.TYPE_EVENT
  ProgramType.CHANNEL -> PreviewProgramColumns.TYPE_CHANNEL
  ProgramType.TRACK -> PreviewProgramColumns.TYPE_TRACK
  ProgramType.ALBUM -> PreviewProgramColumns.TYPE_ALBUM
  ProgramType.ARTIST -> PreviewProgramColumns.TYPE_ARTIST
  ProgramType.PLAYLIST -> PreviewProgramColumns.TYPE_PLAYLIST
  ProgramType.STATION -> PreviewProgramColumns.TYPE_STATION
  ProgramType.GAME -> PreviewProgramColumns.TYPE_GAME
}

private fun WatchNextType.toTv() = when (this) {
  WatchNextType.CONTINUE -> WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE
  WatchNextType.NEXT -> WatchNextPrograms.WATCH_NEXT_TYPE_NEXT
  WatchNextType.NEW -> WatchNextPrograms.WATCH_NEXT_TYPE_NEW
  WatchNextType.WATCHLIST -> WatchNextPrograms.WATCH_NEXT_TYPE_WATCHLIST
}

private fun AspectRatio.toTv() = when (this) {
  AspectRatio._16_9 -> PreviewProgramColumns.ASPECT_RATIO_16_9
  AspectRatio._3_2 -> PreviewProgramColumns.ASPECT_RATIO_3_2
  AspectRatio._4_3 -> PreviewProgramColumns.ASPECT_RATIO_4_3
  AspectRatio._1_1 -> PreviewProgramColumns.ASPECT_RATIO_1_1
  AspectRatio._2_3 -> PreviewProgramColumns.ASPECT_RATIO_2_3
  AspectRatio._3_4 -> PreviewProgramColumns.ASPECT_RATIO_3_4
  AspectRatio.MOVIEPOSTER -> PreviewProgramColumns.ASPECT_RATIO_MOVIE_POSTER
}

private fun Availability.toTv() = when (this) {
  Availability.AVAILABLE -> PreviewProgramColumns.AVAILABILITY_AVAILABLE
  Availability.FREEWITHSUBSCRIPTION -> PreviewProgramColumns.AVAILABILITY_FREE_WITH_SUBSCRIPTION
  Availability.PAIDCONTENT -> PreviewProgramColumns.AVAILABILITY_PAID_CONTENT
  Availability.PURCHASED -> PreviewProgramColumns.AVAILABILITY_PURCHASED
  Availability.FREE -> PreviewProgramColumns.AVAILABILITY_FREE
  Availability.FREEWITHADS -> PreviewProgramColumns.AVAILABILITY_FREE_WITH_ADS
}

private fun InteractionType.toTv() = when (this) {
  InteractionType.VIEWS -> PreviewProgramColumns.INTERACTION_TYPE_VIEWS
  InteractionType.LISTENS -> PreviewProgramColumns.INTERACTION_TYPE_LISTENS
  InteractionType.FOLLOWERS -> PreviewProgramColumns.INTERACTION_TYPE_FOLLOWERS
  InteractionType.FANS -> PreviewProgramColumns.INTERACTION_TYPE_FANS
  InteractionType.LIKES -> PreviewProgramColumns.INTERACTION_TYPE_LIKES
  InteractionType.THUMBS -> PreviewProgramColumns.INTERACTION_TYPE_THUMBS
  InteractionType.VIEWERS -> PreviewProgramColumns.INTERACTION_TYPE_VIEWERS
}

// `TvContractCompat.ProgramColumns.REVIEW_RATING_STYLE_*`, the interface is package private.
private fun ReviewRatingStyle.toTv() = when (this) {
  ReviewRatingStyle.STARS -> 0
  ReviewRatingStyle.THUMBSUPDOWN -> 1
  ReviewRatingStyle.PERCENTAGE -> 2
}

private fun TvSeriesItemType.toTv() = when (this) {
  TvSeriesItemType.EPISODE -> PreviewProgramColumns.TV_SERIES_ITEM_TYPE_EPISODE
  TvSeriesItemType.CHAPTER -> PreviewProgramColumns.TV_SERIES_ITEM_TYPE_CHAPTER
}
