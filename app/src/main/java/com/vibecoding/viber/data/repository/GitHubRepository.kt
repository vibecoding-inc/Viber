package com.vibecoding.viber.data.repository

import com.vibecoding.viber.data.local.PreferencesManager
import com.vibecoding.viber.data.model.*
import com.vibecoding.viber.data.remote.GitHubApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class GitHubRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val preferencesManager: PreferencesManager
) {
    val isAuthenticated: Flow<Boolean> = kotlinx.coroutines.flow.map(
        preferencesManager.accessToken
    ) { token ->
        !token.isNullOrEmpty()
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                preferencesManager.saveUserInfo(user.login, user.avatarUrl)
                Result.Success(user)
            } else {
                Result.Error("Failed to fetch user: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun getUserRepositories(page: Int = 1): Result<List<Repository>> {
        return try {
            val response = apiService.getUserRepositories(page = page)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to fetch repositories: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun getRepository(owner: String, repo: String): Result<Repository> {
        return try {
            val response = apiService.getRepository(owner, repo)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to fetch repository: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun getRepositoryIssues(
        owner: String,
        repo: String,
        state: String = "open",
        page: Int = 1
    ): Result<List<Issue>> {
        return try {
            val response = apiService.getRepositoryIssues(owner, repo, state, page = page)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to fetch issues: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun getRepositoryPullRequests(
        owner: String,
        repo: String,
        state: String = "open",
        page: Int = 1
    ): Result<List<PullRequest>> {
        return try {
            val response = apiService.getRepositoryPullRequests(owner, repo, state, page = page)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to fetch pull requests: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun getUserIssues(
        filter: String = "assigned",
        state: String = "open",
        page: Int = 1
    ): Result<List<Issue>> {
        return try {
            val response = apiService.getUserIssues(filter, state, page = page)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to fetch issues: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun searchRepositories(query: String, page: Int = 1): Result<List<Repository>> {
        return try {
            val response = apiService.searchRepositories(query, page = page)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.items)
            } else {
                Result.Error("Failed to search repositories: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun signOut() {
        preferencesManager.clearAll()
    }
}
