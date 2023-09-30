package ru.be_more.orange_forum.presentation.screens.favorite

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.presentation.screens.base.Screen

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
) {
/*    Text(
        text = "Board screen ${viewModel.text}",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize()
    )*/
}

fun NavGraphBuilder.favoriteScreen() {
    composable(route = Screen.Favorite.route) {
        FavoriteScreen(
            viewModel = koinViewModel(),
        )
    }
}



