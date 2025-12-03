package com.example.lab_week_13

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDatabase
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MovieApplication : Application() {

    lateinit var movieRepository: MovieRepository

    override fun onCreate() {
        super.onCreate()

        // create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        // create a MovieService instance
        val movieService = retrofit.create(MovieService::class.java)

        // create a MovieDatabase instance
        val movieDatabase = MovieDatabase.getInstance(this)

        // create a MovieRepository instance
        movieRepository = MovieRepository(
            movieService,
            movieDatabase
        )

        // create a Constraints instance
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // create a WorkRequest instance
        val workRequest = PeriodicWorkRequest.Builder(
            MovieWorker::class.java,
            1,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("movie-work")
            .build()

        // schedule the background task
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
}
