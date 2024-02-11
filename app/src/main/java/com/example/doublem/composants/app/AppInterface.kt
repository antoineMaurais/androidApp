package com.example.doublem.composants.app

import androidx.annotation.DrawableRes

// Interface pour les applications affiché dans la liste des applications
data class AppItem(
    val name: String,
    @DrawableRes val icon: Int
)