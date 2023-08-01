package com.palkowski.friendupp.Categories

import com.palkowski.friendupp.R

enum class Category(val label: String, val icon: Int, val subCategories: List<SubCategory>) {


    SPORTS(
        "Sports", R.drawable.ic_volleyball, subCategories = listOf(
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
        )
    ),


    EntertainmentAndCulture(
        label = "Entertainment&Culture", icon = R.drawable.ic_culture, subCategories = listOf(
            SubCategory.NIGHTLIFE,
            SubCategory.BOOKS,
            SubCategory.MOVIES,
        )
    ),

    HOUSEANDGARDEN(
        label = "House&Garden", icon = R.drawable.ic_garden, subCategories = listOf()
    ),
    OUTDOOR(
        label = "Outdoor", icon = R.drawable.ic_outdoor, subCategories = listOf(
            SubCategory.HIKING,
            SubCategory.CAMPING,
            SubCategory.FISHING,
            SubCategory.SAILING,
            SubCategory.HUNTING,
            SubCategory.CYCLING,
        )
    ),
    BEAUTYANDSTYLE(label = "Beauty&Style", icon = R.drawable.ic_style, subCategories = listOf()),
    FoodAndDrink(
        label = "Food&Drink", icon = R.drawable.ic_food, subCategories = listOf(
            SubCategory.COOKING,
            SubCategory.ICECREAM,
            SubCategory.PIZZA,
            SubCategory.FASTFOOD,
            SubCategory.WINETASTING
        )
    ),
    AutomotiveAndVehicles(
        label = "Automotive&Vehicles",
        icon = R.drawable.ic_auto,
        subCategories = listOf()
    ),
    ARTS(
        label = "Arts", icon = R.drawable.ic_arts, subCategories = listOf(
            SubCategory.PAINTING,
            SubCategory.PHOTOGRAPHY,
        )
    ),
    ComputerGames(label = "Computer games", icon = R.drawable.ic_game, subCategories = listOf()),
    Animals(label = "Animals", icon = R.drawable.ic_animal, subCategories = listOf()),
    FITNESSANDHEALTH(
        label = "Fitness&Health", icon = R.drawable.ic_wellness, subCategories = listOf(
            SubCategory.GYM_WORKOUT,
            SubCategory.YOGA,
            SubCategory.RUNNING,
            SubCategory.POOL,
            SubCategory.MEDITATION
        )
    ),
    TRAVEL(
        label = "Travel", icon = R.drawable.ic_travel, subCategories = listOf()
    ),
    DYI(label = "Dyi", icon = R.drawable.ic_dyi, subCategories = listOf()),
    STUDYINGANDEDUCATION(
        label = "Study&Education",
        icon = R.drawable.ic_education,
        subCategories = listOf()
    ),
    MUSIC(label = "Music", icon = R.drawable.ic_music, subCategories = listOf()),
    CREATIVE(
        "Creative", R.drawable.ic_creative, subCategories = listOf(
            SubCategory.WRITING,
            SubCategory.FILMMAKING
        )
    ),
    SOCIAL(
        "Social events", R.drawable.ic_event, subCategories = listOf(
            SubCategory.PARTY,
            SubCategory.BIRTHDAY,
            SubCategory.FESTIVALS
        )
    );


    sealed class SubCategory(val label: String, val icon: Int) {
        object FISHING: SubCategory("Fishing", R.drawable.ic_fishing)
        object NIGHTLIFE: SubCategory("Night life", R.drawable.ic_nightlife)
        object BOOKS: SubCategory("Books", R.drawable.ic_books)
        object MOVIES: SubCategory("Movies", R.drawable.ic_films)
        object SAILING: SubCategory("Sailing", R.drawable.ic_sailing)
        object CAMPING: SubCategory("Camping", R.drawable.ic_camping)
        object HUNTING: SubCategory("Pizza", R.drawable.ic_pizza)
        object PIZZA: SubCategory("Pizza", R.drawable.ic_pizza)
        object HIKING: SubCategory("Hiking", R.drawable.ic_hiking)
        object ICECREAM : SubCategory("Ice cream", R.drawable.ic_icecream)
        object FASTFOOD : SubCategory("Fastfood", R.drawable.ic_fastfood)
        object MEDITATION : SubCategory("Meditation", R.drawable.ic_selfimprovment)
        object COOKING : SubCategory("Cooking", R.drawable.ic_cooking)
        object PHOTOGRAPHY : SubCategory("Photography", R.drawable.ic_camera_flip)
        object PAINTING : SubCategory("Painting", R.drawable.ic_selfimprovment)
        object FILMMAKING : SubCategory("Film making", R.drawable.ic_movie)
        object WRITING : SubCategory("Writing", R.drawable.ic_edit)
        object FESTIVALS : SubCategory("Festivals", R.drawable.ic_festivals)
        object BIRTHDAY : SubCategory("Birthday", R.drawable.ic_cake)
        object PARTY : SubCategory("Party", R.drawable.ic_celebration)
        object WINETASTING : SubCategory("Wine tasting", R.drawable.ic_liquor)
        object TENNIS : SubCategory("Tennis", R.drawable.ic_tennis)
        object FOOTBALL : SubCategory("Football(Soccer)", R.drawable.ic_football)
        object POOL : SubCategory("Swimming", R.drawable.ic_pool)
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

