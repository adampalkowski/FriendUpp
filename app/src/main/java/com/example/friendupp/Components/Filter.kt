package com.example.friendupp.Components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.friendupp.Categories.Category
import com.example.friendupp.Components.Calendar.HorizontalDatePicker
import com.example.friendupp.ui.theme.Lexend
import com.example.friendupp.R
import com.example.friendupp.ui.theme.SocialTheme

// Function to derive the expanded tags based on the selected tags
fun getExpandedTags(selectedTags: List<String>): List<Category> {
    val expandedTags = mutableListOf<Category>()

    for (tag in selectedTags) {
        val matchingCategory = Category.values().find { it.label == tag }
        if (matchingCategory != null) {
            expandedTags.add(matchingCategory)
        }
    }

    return expandedTags
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilterList(
    tags: List<String>,
    onSelected: (String) -> Unit,
    onDeSelected: (String) -> Unit,
) {
    val selectedTags = remember { mutableStateListOf<String>() }

    // Whenever the tags list changes, update the selectedTags list accordingly
    DisposableEffect(tags) {
        selectedTags.clear()
        selectedTags.addAll(tags)
        onDispose { }
    }
    val expandedTags = remember { mutableStateListOf<Category>() }

    // Whenever the selectedTags list changes, update the expandedTags list accordingly
    DisposableEffect(selectedTags) {
        expandedTags.clear()
        expandedTags.addAll(getExpandedTags(selectedTags))
        onDispose { }
    }


    Column() {

        LazyHorizontalStaggeredGrid(modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 30.dp, max = 200.dp)
            .padding(8.dp),
            rows = StaggeredGridCells.Adaptive(minSize = 30.dp),
            horizontalItemSpacing = 4.dp,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = {

                items(Category.values()) { it ->
                    var isExpanded by remember { mutableStateOf(expandedTags.contains(it)) }
                    TagItem(
                        title = it.label, it.icon,
                        onClick = {
                            if (isExpanded) {
                                expandedTags.remove(it)
                                isExpanded = false
                                onDeSelected(it.label)
                            } else {
                                expandedTags.add(it)
                                isExpanded = true
                                onSelected(it.label)
                            }
                        }
                    ,selected=isExpanded)
                }

            }
        )
        AnimatedVisibility(
            visible = expandedTags.isNotEmpty(),
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            Row {
                ParallelLines()
                LazyHorizontalStaggeredGrid(modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 30.dp, max = 68.dp)
                    .padding(horizontal = 8.dp),
                    rows = StaggeredGridCells.Adaptive(minSize = 30.dp),
                    horizontalItemSpacing = 4.dp,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    content = {

                        expandedTags.forEach { category ->
                            items(category.subCategories) {subCategory->
                                var isSelected by remember {mutableStateOf(selectedTags.contains(subCategory.label)) }
                                TagItem(
                                    title = subCategory.label, subCategory.icon,
                                    onClick = {
                                        if (isSelected) {
                                            isSelected = false
                                            onDeSelected(subCategory.label)
                                        } else {
                                            isSelected = true
                                            onSelected(subCategory.label)
                                        }
                                    },
                                    selected=isSelected)
                            }
                        }

                    }
                )
            }

        }

    }

}


@Composable
fun ParallelLines() {
    val color = SocialTheme.colors.uiBorder
    Canvas(
        modifier = Modifier
            .padding(start = 6.dp)
            .width(24.dp)
            .height(50.dp)
    ) {
        val startX = 0f
        val endX = 0f
        val startY = 0f
        val endY = size.height / 2
        val strokeWidth = 4f
        drawLine(
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            color = color,
            strokeWidth = strokeWidth
        )


        val startX2 = 0f
        val endX2 = size.width
        val startY2 = size.height / 2
        val endY2 = size.height / 2
        drawLine(
            start = Offset(startX2, startY2),
            end = Offset(endX2, endY),
            color = color,
            strokeWidth = strokeWidth
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TagItem(title: String, icon: Int, onClick: () -> Unit,selected:Boolean) {

    var color =
        if (selected) SocialTheme.colors.textInteractive else SocialTheme.colors.uiBackground
    var border = if (selected) null else BorderStroke(1.dp, SocialTheme.colors.uiBorder)
    var textColor = if (selected) Color.White else SocialTheme.colors.textPrimary.copy(0.7f)
    androidx.compose.material.Card(modifier = Modifier
        .wrapContentHeight()
        .wrapContentWidth(), shape = RoundedCornerShape(24.dp), elevation=4.dp,
        backgroundColor = color, contentColor = color, onClick = {
            onClick()
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.padding(vertical = 4.dp), text = title, style = TextStyle(
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Normal, fontSize = 14.sp, color = textColor
                )
            )

        }
    }
}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagItem(title:String,icon:Int) {
    var clicked  by rememberSaveable{
        mutableStateOf(false)
    }
    var color = if (clicked) SocialTheme.colors.textInteractive  else  SocialTheme.colors.uiBackground
    var border = if (clicked) null else BorderStroke(1.dp,SocialTheme.colors.uiBorder)
    var textColor = if (clicked) Color.White else SocialTheme.colors.textPrimary.copy(0.7f)
    Card(modifier= Modifier
        .wrapContentHeight()
        .wrapContentWidth(),shape = RoundedCornerShape(24.dp), border =border ,
        colors = CardDefaults.cardColors(contentColor = Color(0xFF4870FD).copy(alpha = 0.9f), containerColor =color), onClick = {clicked=!clicked}) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
            Icon(modifier = Modifier.size(12.dp),painter = painterResource(id = icon), contentDescription = null,tint=textColor)
            Spacer(modifier = Modifier.width(4.dp))
            Text(modifier = Modifier.padding( vertical = 4.dp),text =title, style = TextStyle(fontFamily = Lexend,
                fontWeight = FontWeight.Light, fontSize = 12.sp, color = textColor))

        }
    }
}*/