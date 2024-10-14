package net.primal.android.explore.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.primal.android.LocalContentDisplaySettings
import net.primal.android.R
import net.primal.android.core.compose.IconText
import net.primal.android.core.compose.PrimalDivider
import net.primal.android.core.compose.PrimalTopLevelDestination
import net.primal.android.core.compose.icons.PrimalIcons
import net.primal.android.core.compose.icons.primaliconpack.AdvancedSearch
import net.primal.android.core.compose.icons.primaliconpack.ArrowBack
import net.primal.android.core.compose.preview.PrimalPreview
import net.primal.android.core.errors.UiError
import net.primal.android.core.errors.resolveUiErrorMessage
import net.primal.android.drawer.DrawerScreenDestination
import net.primal.android.drawer.PrimalDrawerScaffold
import net.primal.android.explore.home.feeds.ExploreFeeds
import net.primal.android.explore.home.people.ExplorePeople
import net.primal.android.explore.home.topics.ExploreTopics
import net.primal.android.explore.home.ui.EXPLORE_HOME_TAB_COUNT
import net.primal.android.explore.home.ui.ExploreHomeTabs
import net.primal.android.explore.home.ui.FEEDS_INDEX
import net.primal.android.explore.home.ui.MEDIA_INDEX
import net.primal.android.explore.home.ui.PEOPLE_INDEX
import net.primal.android.explore.home.ui.TOPICS_INDEX
import net.primal.android.explore.home.ui.ZAPS_INDEX
import net.primal.android.explore.home.zaps.ExploreZaps
import net.primal.android.feeds.domain.buildExploreMediaFeedSpec
import net.primal.android.notes.feed.MediaFeedGrid
import net.primal.android.notes.feed.note.ui.events.NoteCallbacks
import net.primal.android.theme.AppTheme
import net.primal.android.theme.domain.PrimalTheme

@Composable
fun ExploreHomeScreen(
    viewModel: ExploreHomeViewModel,
    onTopLevelDestinationChanged: (PrimalTopLevelDestination) -> Unit,
    onDrawerScreenClick: (DrawerScreenDestination) -> Unit,
    onDrawerQrCodeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAdvancedSearchClick: () -> Unit,
    onClose: () -> Unit,
    noteCallbacks: NoteCallbacks,
) {
    val uiState = viewModel.state.collectAsState()

    ExploreHomeScreen(
        state = uiState.value,
        onPrimaryDestinationChanged = onTopLevelDestinationChanged,
        onDrawerDestinationClick = onDrawerScreenClick,
        onDrawerQrCodeClick = onDrawerQrCodeClick,
        onSearchClick = onSearchClick,
        onAdvancedSearchClick = onAdvancedSearchClick,
        onClose = onClose,
        noteCallbacks = noteCallbacks,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreHomeScreen(
    state: ExploreHomeContract.UiState,
    onPrimaryDestinationChanged: (PrimalTopLevelDestination) -> Unit,
    onDrawerDestinationClick: (DrawerScreenDestination) -> Unit,
    onDrawerQrCodeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAdvancedSearchClick: () -> Unit,
    onClose: () -> Unit,
    noteCallbacks: NoteCallbacks,
) {
    val context = LocalContext.current
    val uiScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState { EXPLORE_HOME_TAB_COUNT }

    val topAppBarState: TopAppBarState = rememberTopAppBarState()
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    PrimalDrawerScaffold(
        drawerState = drawerState,
        activeDestination = PrimalTopLevelDestination.Explore,
        onPrimaryDestinationChanged = onPrimaryDestinationChanged,
        onDrawerDestinationClick = onDrawerDestinationClick,
        onDrawerQrCodeClick = onDrawerQrCodeClick,
        badges = state.badges,
        focusModeEnabled = LocalContentDisplaySettings.current.focusModeEnabled,
        topAppBarState = topAppBarState,
        topAppBar = {
            ExploreTopAppBar(
                onClose = onClose,
                onSearchClick = onSearchClick,
                onActionIconClick = onAdvancedSearchClick,
                actionIcon = PrimalIcons.AdvancedSearch,
                pagerState = pagerState,
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        content = { paddingValues ->
            HorizontalPager(
                state = pagerState,
            ) { pageIndex ->
                when (pageIndex) {
                    FEEDS_INDEX -> {
                        ExploreFeeds(
                            paddingValues = paddingValues,
                        )
                    }
                    PEOPLE_INDEX -> {
                        ExplorePeople(
                            paddingValues = paddingValues,
                            onProfileClick = { noteCallbacks.onProfileClick?.invoke(it) },
                            onUiError = { uiError: UiError ->
                                uiScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = uiError.resolveUiErrorMessage(context),
                                        duration = SnackbarDuration.Short,
                                    )
                                }
                            },
                        )
                    }
                    ZAPS_INDEX -> {
                        ExploreZaps(
                            paddingValues = paddingValues,
                            noteCallbacks = noteCallbacks,
                        )
                    }
                    MEDIA_INDEX -> {
                        MediaFeedGrid(
                            feedSpec = buildExploreMediaFeedSpec(),
                            contentPadding = paddingValues,
                            onNoteClick = { noteCallbacks.onNoteClick?.invoke(it) },
                        )
                    }
                    TOPICS_INDEX -> {
                        ExploreTopics(
                            paddingValues = paddingValues,
                            onHashtagClick = { noteCallbacks.onHashtagClick?.invoke(it) },
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    )
}

@ExperimentalMaterial3Api
@Composable
fun ExploreTopAppBar(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onSearchClick: () -> Unit,
    onActionIconClick: () -> Unit,
    onClose: () -> Unit,
    actionIcon: ImageVector,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .background(AppTheme.colorScheme.background)
            .wrapContentHeight(),
    ) {
        TopAppBar(
            title = {
                SearchBar(
                    onClick = onSearchClick,
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onClose,
                ) {
                    Icon(
                        imageVector = PrimalIcons.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onActionIconClick,
                ) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = AppTheme.colorScheme.surface,
                scrolledContainerColor = AppTheme.colorScheme.surface,
            ),
            scrollBehavior = scrollBehavior,
        )
        ExploreHomeTabs(
            selectedTabIndex = pagerState.currentPage,
            onFeedsTabClick = { scope.launch { pagerState.animateScrollToPage(FEEDS_INDEX) } },
            onPeopleTabClick = { scope.launch { pagerState.animateScrollToPage(PEOPLE_INDEX) } },
            onZapsTabClick = { scope.launch { pagerState.animateScrollToPage(ZAPS_INDEX) } },
            onMediaTabClick = { scope.launch { pagerState.animateScrollToPage(MEDIA_INDEX) } },
            onTopicsTabClick = { scope.launch { pagerState.animateScrollToPage(TOPICS_INDEX) } },
        )
        PrimalDivider()
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(34.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(AppTheme.shapes.extraLarge)
            .clickable { onClick() }
            .background(
                color = AppTheme.extraColorScheme.surfaceVariantAlt1,
                shape = AppTheme.shapes.extraLarge,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        IconText(
            modifier = Modifier.padding(horizontal = 8.dp),
            leadingIcon = Icons.Default.Search,
            leadingIconTintColor = AppTheme.extraColorScheme.onSurfaceVariantAlt3,
            text = stringResource(id = R.string.explore_search_nostr).lowercase(),
            color = AppTheme.extraColorScheme.onSurfaceVariantAlt3,
            style = AppTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewExploreTopAppBar() {
    PrimalPreview(primalTheme = PrimalTheme.Sunset) {
        Surface {
            ExploreTopAppBar(
                onSearchClick = {},
                onActionIconClick = {},
                onClose = {},
                actionIcon = Icons.Filled.Tune,
                pagerState = rememberPagerState { EXPLORE_HOME_TAB_COUNT },
            )
        }
    }
}
