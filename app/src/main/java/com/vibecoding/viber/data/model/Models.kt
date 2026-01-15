package com.vibecoding.viber.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("login") val login: String,
    @SerializedName("id") val id: Long,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("public_repos") val publicRepos: Int = 0,
    @SerializedName("followers") val followers: Int = 0,
    @SerializedName("following") val following: Int = 0
)

data class Repository(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("owner") val owner: User,
    @SerializedName("description") val description: String?,
    @SerializedName("private") val isPrivate: Boolean,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    @SerializedName("language") val language: String?,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("open_issues_count") val openIssues: Int = 0
)

data class Issue(
    @SerializedName("id") val id: Long,
    @SerializedName("number") val number: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String?,
    @SerializedName("state") val state: String,
    @SerializedName("user") val user: User,
    @SerializedName("labels") val labels: List<Label>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("comments") val comments: Int,
    @SerializedName("html_url") val htmlUrl: String
)

data class PullRequest(
    @SerializedName("id") val id: Long,
    @SerializedName("number") val number: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String?,
    @SerializedName("state") val state: String,
    @SerializedName("user") val user: User,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("draft") val isDraft: Boolean = false,
    @SerializedName("mergeable") val isMergeable: Boolean? = null
)

data class Label(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String,
    @SerializedName("description") val description: String?
)

data class AccessTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("scope") val scope: String
)

data class CopilotSuggestion(
    val id: String,
    val text: String,
    val displayText: String,
    val position: Int,
    val range: IntRange
)

data class SearchResponse<T>(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<T>
)
