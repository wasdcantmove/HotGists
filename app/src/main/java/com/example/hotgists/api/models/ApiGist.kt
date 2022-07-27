package com.example.hotgists.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GistList(
    @JsonProperty("url") val url: String = "",
    @JsonProperty("forks_url") var forksUrl: String? = null,
    @JsonProperty("commits_url") var commitsUrl: String? = null,
    @JsonProperty("id") var id: String? = null,
    @JsonProperty("node_id") var nodeId: String? = null,
    @JsonProperty("git_pull_url") var gitPullUrl: String? = null,
    @JsonProperty("git_push_url") var gitPushUrl: String? = null,
    @JsonProperty("html_url") var htmlUrl: String? = null,
    @JsonProperty("public") var public: Boolean? = null,
    @JsonProperty("created_at") var createdAt: String? = null,
    @JsonProperty("updated_at") var updatedAt: String? = null,
    @JsonProperty("description") var description: String? = null,
    @JsonProperty("comments") var comments: Int? = null,
    @JsonProperty("user") var user: String? = null,
    @JsonProperty("comments_url") var commentsUrl: String? = null,
    @JsonProperty("owner") var owner: Owner? = Owner(),
    @JsonProperty("truncated") var truncated: Boolean? = null,
    @JsonProperty("files") var files: Map<String, File>
)

data class Owner(
    @JsonProperty("avatar_url") val avatar_url: String = "",
    @JsonProperty("events_url") val events_url: String = "",
    @JsonProperty("followers_url") val followers_url: String = "",
    @JsonProperty("following_url") val following_url: String = "",
    @JsonProperty("gists_url") val gists_url: String = "",
    @JsonProperty("gravatar_id") val gravatar_id: String = "",
    @JsonProperty("html_url") val html_url: String = "",
    @JsonProperty("id") val id: Int = 0,
    @JsonProperty("login") val login: String = "",
    @JsonProperty("node_id") val node_id: String = "",
    @JsonProperty("organizations_url") val organizations_url: String = "",
    @JsonProperty("received_events_url") val received_events_url: String = "",
    @JsonProperty("repos_url") val repos_url: String = "",
    @JsonProperty("site_admin") val site_admin: Boolean = false,
    @JsonProperty("starred_url") val starred_url: String = "",
    @JsonProperty("subscriptions_url") val subscriptions_url: String = "",
    @JsonProperty("type") val type: String = "",
    @JsonProperty("url") val url: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Gist(
    @JsonProperty("avatar_url") val avatar_url: String,
    @JsonProperty("bio") val bio: Any,
    @JsonProperty("blog") val blog: String,
    @JsonProperty("company") val company: Any,
    @JsonProperty("created_at") val created_at: String,
    @JsonProperty("email") val email: Any,
    @JsonProperty("events_url") val events_url: String,
    @JsonProperty("followers") val followers: Int,
    @JsonProperty("followers_url") val followers_url: String,
    @JsonProperty("following") val following: Int,
    @JsonProperty("following_url") val following_url: String,
    @JsonProperty("gists_url") val gists_url: String,
    @JsonProperty("gravatar_id") val gravatar_id: String,
    @JsonProperty("hireable") val hireable: Any,
    @JsonProperty("html_url") val html_url: String,
    @JsonProperty("id") val id: Int,
    @JsonProperty("location") val location: Any,
    @JsonProperty("login") val login: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("node_id") val node_id: String,
    @JsonProperty("organizations_url") val organizations_url: String,
    @JsonProperty("public_gists") val public_gists: Int,
    @JsonProperty("public_repos") val public_repos: Int,
    @JsonProperty("received_events_url") val received_events_url: String,
    @JsonProperty("repos_url") val repos_url: String,
    @JsonProperty("site_admin") val site_admin: Boolean,
    @JsonProperty("starred_url") val starred_url: String,
    @JsonProperty("subscriptions_url") val subscriptions_url: String,
    @JsonProperty("twitter_username") val twitter_username: Any,
    @JsonProperty("type") val type: String,
    @JsonProperty("updated_at") val updated_at: String,
    @JsonProperty("url") val url: String
)

data class File(
    @JsonProperty("filename") val filename: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("language") val language: String,
    @JsonProperty("raw_url") val raw_url: String,
    @JsonProperty("size") val size: String,
)

