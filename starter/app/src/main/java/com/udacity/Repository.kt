package com.udacity

enum class Repository {

    GLIDE {
        override val repoUrl: String
            get() = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
    },

    LOAD_APP {
        override val repoUrl: String
            get() = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
    },

    RETROFIT {
        override val repoUrl: String
            get() = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    };

    abstract val repoUrl: String
}