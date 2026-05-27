package com.santiago.soberpath.domain.model

data class OnboardingSlide(
    val id: Int,
    val title: Map<String, String>,
    val description: Map<String, String>,
    val imageUrl: Map<String, String>
)