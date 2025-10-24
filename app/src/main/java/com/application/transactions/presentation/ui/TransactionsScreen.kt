package com.application.transactions.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.transactions.R
import com.application.transactions.domain.CreditDebitIndicator
import com.application.transactions.presentation.TransactionGroupUi
import com.application.transactions.presentation.TransactionsScreenState
import com.application.transactions.presentation.TransactionsSectionUi
import com.application.transactions.presentation.TransactionsViewModel
import com.application.transactions.presentation.utils.UiError
import com.application.transactions.theme.AppThemeTokens
import com.application.transactions.theme.TransactionsAppTheme
import com.application.transactions.presentation.ui.component.DirectionArrow
import com.application.transactions.presentation.ui.component.ExpandableText
import com.application.transactions.presentation.ui.component.InfoState
import com.application.transactions.presentation.ui.component.InfoStateContent
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        val errState = (state as? TransactionsScreenState.HasError)
        if (errState != null && errState.sections.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = context.getString(errState.uiError.messageRes),
                withDismissAction = true
            )
        }
    }

    TransactionScreen(
        uiState = state,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::fetchData
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionScreen(
    uiState: TransactionsScreenState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { TransactionsScreenTopBar(scrollBehavior) }) { paddingValues ->
        val pullRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = uiState is TransactionsScreenState.Loading,
            onRefresh = onRefresh,
            state = pullRefreshState
        ) {
            TransactionsScreenContent(uiState = uiState, paddingValues = paddingValues)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionsScreenTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(R.string.transactions),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun TransactionsScreenContent(
    uiState: TransactionsScreenState,
    paddingValues: PaddingValues
) {
    AnimatedContent(
        targetState = uiState,

        transitionSpec = {
            val spec = spring<Float>(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )

            val enter = fadeIn(animationSpec = spec) + scaleIn(
                initialScale = 0.90f,
                animationSpec = spec
            )

            val exit = fadeOut(animationSpec = spec) + scaleOut(
                targetScale = 0.90f,
                animationSpec = spec
            )

            (enter togetherWith exit).using(SizeTransform(clip = false))
        },
    ) { state ->
        when (state) {
            is TransactionsScreenState.Empty -> InfoStateContent(
                state = InfoState.EmptyContent,
                paddingValues = paddingValues
            )

            is TransactionsScreenState.HasError -> {
                if (state.sections.isEmpty()) {
                    InfoStateContent(
                        state = InfoState.GenericError(state.uiError),
                        paddingValues = paddingValues
                    )
                } else {
                    TransactionsList(
                        sections = state.sections,
                        isLoading = false,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            is TransactionsScreenState.HasData -> TransactionsList(
                sections = state.sections,
                isLoading = state is TransactionsScreenState.Loading,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun TransactionsList(
    sections: List<TransactionsSectionUi>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppThemeTokens.spacing.betweenCards),
        contentPadding = PaddingValues(
            top = AppThemeTokens.spacing.cardPadding,
            start = AppThemeTokens.spacing.cardPadding,
            end = AppThemeTokens.spacing.cardPadding,
            bottom = AppThemeTokens.spacing.cardPadding + navInsets.calculateBottomPadding()
        )
    ) {
        sections.forEach { section ->
            section.apply {
                item(key = "header-${titleRes}") {
                    Text(
                        text = stringResource(titleRes),
                        modifier = Modifier.placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.shimmer(),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                items(groups, key = { group -> group.id }) { group ->
                    TransactionGroupCard(
                        group = group,
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionGroupCard(
    group: TransactionGroupUi,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = AppThemeTokens.shapes.cornerLarge
    ) {
        Column(
            Modifier.padding(AppThemeTokens.spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(
                AppThemeTokens.spacing.cardContentSpacing
            )
        ) {
            TransactionGroupSection(
                group = group,
                isLoading = isLoading
            )

            TransactionsDescriptionSection(
                group = group,
                expanded = expanded,
                isLoading = isLoading,
                onExpandedChange = { expanded = !expanded })

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline
            )

            TransactionAmountSection(
                group = group,
                isLoading = isLoading
            )
        }
    }
}

@Composable
private fun TransactionGroupSection(
    group: TransactionGroupUi,
    isLoading: Boolean
) {
    group.apply {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    AppThemeTokens.spacing.small
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DirectionArrow(
                    isPositive = indicator == CreditDebitIndicator.CRDT,
                    isLoading = isLoading
                )
                Text(
                    text = countLabel,
                    modifier = Modifier.placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = dateText,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun TransactionsDescriptionSection(
    group: TransactionGroupUi,
    expanded: Boolean,
    isLoading: Boolean = false,
    onExpandedChange: (Boolean) -> Unit
) {
    group.apply {
        ExpandableText(
            text = fullDescriptionText,
            expanded = expanded,
            isLoading = isLoading,
            isExpandable = isExpandable,
            onExpandedChange = onExpandedChange
        )
    }
}


@Composable
private fun TransactionAmountSection(
    group: TransactionGroupUi,
    isLoading: Boolean
) {
    group.apply {
        val color = when (indicator) {
            CreditDebitIndicator.CRDT -> MaterialTheme.colorScheme.tertiary
            CreditDebitIndicator.DBIT -> MaterialTheme.colorScheme.onSurface
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = amountText,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ),
                color = color,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ---------------------------- PREVIEW ---------------------------- */

private class TransactionsStateProvider :
    PreviewParameterProvider<TransactionsScreenState> {

    override val values: Sequence<TransactionsScreenState> = sequenceOf(
        TransactionsScreenState.Loading(TransactionsViewModel.loadingSections),
        TransactionsScreenState.Empty,
        TransactionsScreenState.Error(UiError(R.string.err_no_internet), sections = emptyList()),
        TransactionsScreenState.Success(sampleUiSections())
    )
}

private fun sampleUiSections(): List<TransactionsSectionUi> {
    val pending = listOf(
        sampleUiGroup(
            id = "2022-08-31_CRDT",
            dateText = "Wednesday, August 31, 2022",
            indicator = CreditDebitIndicator.CRDT,
            count = 1,
            descriptions = listOf("Income"),
            amountText = "+€2,195.00"
        ),
        sampleUiGroup(
            id = "2019-08-28_DBIT",
            dateText = "Sunday, August 28, 2019",
            indicator = CreditDebitIndicator.DBIT,
            count = 2,
            descriptions = listOf("Uber", "Mediamarkt"),
            amountText = "-€1,340.00"
        )
    )

    val completed = listOf(
        sampleUiGroup(
            id = "2022-05-16_CRDT",
            dateText = "Monday, May 16, 2022",
            indicator = CreditDebitIndicator.CRDT,
            count = 1,
            descriptions = listOf("Income"),
            amountText = "+€2,200.00"
        )
    )

    return listOf(
        TransactionsSectionUi.Pending(groups = pending),
        TransactionsSectionUi.Completed(groups = completed)
    )
}

private fun sampleUiGroup(
    id: String,
    dateText: String,
    indicator: CreditDebitIndicator,
    count: Int,
    descriptions: List<String>,
    amountText: String
): TransactionGroupUi {
    val full = descriptions.joinToString("\n")
    return TransactionGroupUi(
        id = id,
        dateText = dateText,
        countLabel = if (indicator == CreditDebitIndicator.CRDT) "$count Credit" else "$count Debits",
        indicator = indicator,
        fullDescriptionText = full,
        isExpandable = descriptions.size > 3,
        amountText = amountText
    )
}

@Preview(showBackground = true, name = "Transactions – All States")
@Composable
private fun Transactions_AllStates_Preview(
    @PreviewParameter(TransactionsStateProvider::class) state: TransactionsScreenState
) {
    TransactionsAppTheme {
        TransactionScreen(state, onRefresh = {}, snackbarHostState = remember { SnackbarHostState() })
    }
}
