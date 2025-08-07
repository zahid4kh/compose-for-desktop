
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import components.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestingMainLayoutGrid(
    window: WindowState,
    viewModel: MainViewModel,
    state: ViewState,
    coroutineScope: CoroutineScope,
    isExpanded: Boolean,
    lazyListState: LazyListState,
    modifier: Modifier
){
    val lazyGridState = rememberLazyGridState()
    val gridCells = GridCells.Adaptive(
        if (isExpanded) 420.dp else 300.dp
    )

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ){
        item{
            Header(window, state, viewModel::processIntent, viewModel::toggleDarkMode)

            HorizontalDivider()
        }

        item{
            LazyVerticalGrid(
                columns = gridCells,
                state = lazyGridState,
                modifier = Modifier
                    .fillMaxSize()
                    .height(650.dp)
            ){
                item{
                    ProjectInformationSection(
                        state,
                        onIntent = viewModel::processIntent,
                        modifier = Modifier
                            .animateItem(placementSpec = spring())
                            .animateContentSize()
                    )
                }

                item{
                    UIConfigSection(
                        state,
                        onIntent = viewModel::processIntent,
                        modifier = Modifier
                            .animateItem(placementSpec = spring())
                            .animateContentSize()
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }){
                    LinuxDevAuthorSection(
                        modifier = Modifier,
                        state = state,
                        onIntent = viewModel::processIntent
                    )
                }

                item(span = {GridItemSpan(maxLineSpan)}) {
                    AppIconAttachmentSection(
                        modifier = Modifier,
                        onIntent = viewModel::processIntent,
                    )
                }

                item(span = {GridItemSpan(maxLineSpan)}) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DependencySection(
                        state = state,
                        onIntent = viewModel::processIntent
                    )
                }

                item(span = {GridItemSpan(maxCurrentLineSpan)}) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GenerateButton(state, viewModel::processIntent)

                        Spacer(modifier = Modifier.width(16.dp))

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text("Go to Top")
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        lazyGridState.animateScrollToItem(0)
                                        lazyListState.animateScrollToItem(0)
                                    }
                                },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Go to Top",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}