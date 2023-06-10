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
        SubCategory.CYCLING,
        SubCategory.RUNNING,
        SubCategory.GOLF,
        SubCategory.GYMNASTICS,
        SubCategory.GYM_WORKOUT,
        SubCategory.ICEHOKEY,
        SubCategory.MARTIALARTS,
        SubCategory.POOL,
        SubCategory.YOGA,
        SubCategory.WINTERSPORTS,
        SubCategory.SKATEBOARDING,
        SubCategory.SKIING

    ));

    sealed class SubCategory(val label: String, val icon: Int) {
        object TENNIS : SubCategory("Tennis", R.drawable.ic_tennis)
        object FOOTBALL : SubCategory("Football(Soccer)", R.drawable.ic_football)
        object POOL : SubCategory("Pool", R.drawable.ic_pool)
        object BASKETBALL : SubCategory("Basketball", R.drawable.ic_basketball)
        object GYM_WORKOUT : SubCategory("Gym", R.drawable.ic_gym)
        object CYCLING : SubCategory("Cycling", R.drawable.ic_cycling)
        object RUNNING : SubCategory("Running", R.drawable.ic_running)
        object YOGA : SubCategory("Yoga", R.drawable.ic_yoga)
        object VOLLEYBALL : SubCategory("Volleyball", R.drawable.ic_volleyball)
        object GYMNASTICS : SubCategory("Gymnastics", R.drawable.ic_gymnastics)
        object MARTIALARTS : SubCategory("Martial Arts", R.drawable.ic_martial_arts)
        object GOLF : SubCategory("Golf", R.drawable.ic_golf)
        object SKIING : SubCategory("Skiing", R.drawable.ic_skiing)
        object SKATEBOARDING : SubCategory("Skateboarding", R.drawable.ic_skateboard)
        object ICEHOKEY : SubCategory("Ice Hockey", R.drawable.ic_martial_arts)
        object WINTERSPORTS : SubCategory("Winter sports", R.drawable.ic_winter)

    }
}

