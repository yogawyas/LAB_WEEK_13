package com.example.lab_week_13

import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDao
import com.example.lab_week_13.database.MovieDatabase
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class MovieRepository(
    private val movieService: MovieService,
    private val movieDatabase: MovieDatabase
) {

    private val apiKey = "8d91158c670562612203f3dcb45c5b60"

    fun fetchMovies(): Flow<List<Movie>> {
        return flow {

            val movieDao: MovieDao = movieDatabase.movieDao()
            val savedMovies = movieDao.getMovies()

            if (savedMovies.isEmpty()) {
                val movies = movieService.getPopularMovies(apiKey).results
                movieDao.addMovies(movies)
                emit(movies)
            } else {
                emit(savedMovies)
            }
        }.flowOn(Dispatchers.IO) as Flow<List<Movie>>
    }
}

