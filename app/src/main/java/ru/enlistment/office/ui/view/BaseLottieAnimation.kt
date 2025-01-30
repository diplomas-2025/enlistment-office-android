package ru.enlistment.office.ui.view

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*
import ru.enlistment.office.R

enum class LottieAnimationType(@RawRes val resId: Int) {
    Registration(R.raw.registration)
}

@Composable
fun BaseLottieAnimation(
    modifier: Modifier = Modifier,
    type: LottieAnimationType,
    iterations:Int = com.airbnb.lottie.compose.LottieConstants.IterateForever
){
    val compositionResult =
        com.airbnb.lottie.compose.rememberLottieComposition(
            spec = com.airbnb.lottie.compose.LottieCompositionSpec.RawRes(
                type.resId
            )
        )

    Animation(
        modifier = modifier,
        iterations = iterations,
        compositionResult = compositionResult
    )
}

@Composable
private fun Animation(
    modifier: Modifier = Modifier,
    iterations:Int = LottieConstants.IterateForever,
    compositionResult: LottieCompositionResult
){
    val progress = com.airbnb.lottie.compose.animateLottieCompositionAsState(
        composition = compositionResult.value,
        iterations = iterations,
    )

    com.airbnb.lottie.compose.LottieAnimation(
        composition = compositionResult.value,
        progress = progress.progress,
        modifier = modifier
    )
}