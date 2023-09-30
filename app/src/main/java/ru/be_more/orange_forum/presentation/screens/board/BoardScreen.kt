package ru.be_more.orange_forum.presentation.screens.board

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.Screen

@Composable
fun BoardScreen(
    viewModel: BoardViewModel,
    onNavigateToThread: (String, Int) -> Unit
) {
    Text(
        text = "Board screen ${viewModel.text}",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize()
    )
}

fun NavGraphBuilder.boardScreen(onNavigateToThread: (String, Int) -> Unit) {
    composable(
        route = Screen.Board.route + "?boardId={boardId}",
        arguments = listOf(
            navArgument(name = "boardId") {
                type = NavType.StringType
                nullable = true
            },
        )
    ) { entry ->
        val id = entry.arguments?.getString("boardId") ?: return@composable
        BoardScreen(
            viewModel = koinViewModel(
                parameters = { parametersOf(id) }
            ),
            onNavigateToThread = onNavigateToThread,
        )
    }
}



