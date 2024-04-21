package com.messenger.toaster.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ToasterRipple : RippleTheme{
    @Composable
    override fun defaultColor(): Color {
        return Orange
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha {
        return RippleTheme.defaultRippleAlpha(Orange, isSystemInDarkTheme())
    }

}