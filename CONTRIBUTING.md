You decided to contribute some feature or bugfix to this app? Awesome!

Here are some guidelines about how stuff is going on here:

1. This app is developed in Java. We do not and will not use Kotlin, native Code or WebViews. This is a maintainer decision and not debatable.
2. Do not introduce any new dependencies before discussing it in an Issue or Pull Request

# Coding style

Every developer has an own coding style. For example you will notice heavy reactive programming influence in the code. Keeping interfaces clean eliminates those differences because the methods can be refactored without any change to the surrounding code.

APIs should use the top most super class or interface that is sufficient to provide the features that are required and expected by API consumers. For example `Collection` is often sufficient, no need to declare a specific implementation like `ArrayList` unless you are writing an API that explicitly must return a serializable collection.

We implement defensively and immutable. We use `@NonNull` annotations until the upcoming `null` restricted types [JEP draft](https://openjdk.org/jeps/8303099) are usable on Android. We mark variables as `final` if there is not any proper reason to mutate the value of a variable.

We use explicit `ExecutorService`s until virtual threads ([JEP-425](https://openjdk.org/jeps/425)) are fully supported on Android.

# Architecture

Architecture is a moving target, it is evolving. However, we are aiming to use a clean [MVVM](https://developer.android.com/topic/architecture) pattern and separate the app in several layers:

1. **Database**: Contains Entities, DAOs, SQL queries. We use [Room](https://developer.android.com/training/data-storage/room?hl=de) and want to avoid custom queries (which are not compile time safe)
2. **Remote**: Completely separated from the `database` module. Contains REST endpoint interfaces with models that do not need to be the same as in `database` but have to match the corresponding backends. We make heavy use of [Retrofit](https://square.github.io/retrofit/) and [Android Single-Sign-On](https://github.com/nextcloud/android-singlesignon) and want to avoid usage of other libraries or native HTTP requests. 
3. **Repository**: Contains convenience access to different domains like accounts, tables, preferences and orchestrates the synchronization including the mapping between `remote` and `database` models
4. **App**: Contains the `Activities` and `Fragments` of the app. `ViewModel`s map data of the `repository` to whatever the view needs.
5. **UI**: Potentially reusable dumb components that do not know anything about the app
