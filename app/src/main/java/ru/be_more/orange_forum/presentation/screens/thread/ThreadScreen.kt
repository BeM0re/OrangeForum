package ru.be_more.orange_forum.presentation.screens.thread

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.presentation.screens.base.Screen

@Composable
fun ThreadScreen(
    viewModel: ThreadViewModel
) {
   /* Text(
        text = "Board screen ${viewModel.text}",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize()
    )*/
}

fun NavGraphBuilder.threadScreen() {
    composable(
        route = Screen.Thread.route + "?boardId={boardId}" + "?threadNum={threadNum}",
        arguments = listOf(
            navArgument(name = "boardId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument(name = "threadNum") {
                type = NavType.IntType
                nullable = false
            },
        )
    ) { entry ->
        val boardId = entry.arguments?.getString("boardId") ?: "asddsa"
        val threadNum = entry.arguments?.getInt("threadNum") ?: 0

        ThreadScreen(
            viewModel = koinViewModel(
                parameters = {
                    parametersOf(boardId)
                    parametersOf(threadNum)
                }
            )
        )
    }
}



