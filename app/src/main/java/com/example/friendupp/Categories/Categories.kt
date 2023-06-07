package com.example.friendupp.Categories

import com.example.friendupp.R

enum class Category(val label: String, val icon: Int, val subCategories: List<SubCategory>) {
    EntertainmentAndCulture(label="Entertainment&Culture",icon=R.drawable.ic_culture, subCategories = listOf()),

    HOUSEANDGARDEN(label="House&Garden",icon=R.drawable.ic_garden, subCategories = listOf()),

    OUTDOOR(label="Outdoor",icon=R.drawable.ic_outdoor, subCategories = listOf()),
    BEAUTYANDSTYLE(label="Beauty&Style",icon=R.drawable.ic_style, subCategories = listOf()),
    FoodAndDrink(label="Food&Drink",icon=R.drawable.ic_food, subCategories = listOf()),
    AutomotiveAndVehicles(label="Automotive&Vehicles",icon=R.drawable.ic_auto, subCategories = listOf()),
    ARTS(label="Arts",icon=R.drawable.ic_arts, subCategories = listOf()),
    ComputerGames(label="Computer games",icon=R.drawable.ic_game, subCategories = listOf()),
    Animals(label="Animals",icon=R.drawable.ic_animal, subCategories = listOf()),
    FITNESSANDHEALTH(label="Fitness&Health",icon=R.drawable.ic_wellness, subCategories = listOf()),
    TRAVEL(label="Travel",icon=R.drawable.ic_travel, subCategories = listOf()),
    DYI(label="Dyi",icon=R.drawable.ic_dyi, subCategories = listOf()),
    STUDYINGANDEDUCATION(label="Study&Education",icon=R.drawable.ic_education, subCategories = listOf()),
    MUSIC(label="Music",icon=R.drawable.ic_music, subCategories = listOf()),
    CREATIVE("Creative",R.drawable.ic_creative,subCategories= listOf()),
    SOCIAL("Social events",R.drawable.ic_event,subCategories= listOf()),
    SPORTS("Sports", R.drawable.ic_volleyball, subCategories = listOf(
        SubCategory.FOOTBALL,
        SubCategory.TENNIS,
        SubCategory.BASKETBALL,
        SubCategory.VOLLEYBALL,
        SubCategory.RUNNING
    ));

    sealed class SubCategory(val label: String, val icon: Int) {
        object TENNIS : SubCategory("Tennis", R.drawable.ic_tennis)
        object FOOTBALL : SubCategory("Football", R.drawable.ic_football)
        object POOL : SubCategory("Pool", R.drawable.ic_pool)
        object BASKETBALL : SubCategory("Basketball", R.drawable.ic_volleyball)
        object GYM_WORKOUT : SubCategory("Basketball", R.drawable.ic_volleyball)
        object PILATES : SubCategory("Basketball", R.drawable.ic_volleyball)
        object RUNNING : SubCategory("Basketball", R.drawable.ic_volleyball)
        object YOGA : SubCategory("Basketball", R.drawable.ic_volleyball)
        object VOLLEYBALL : SubCategory("Volleyball", R.drawable.ic_volleyball)

    }
}

