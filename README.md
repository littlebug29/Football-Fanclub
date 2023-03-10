# Zuju test project

Zuju app is designed for sports fans who want to stay up-to-date with their favorite teams' matches.
With our app, users can easily access information about all participating teams and view a
comprehensive list of all previous and upcoming matches.

One of the key features of the app is the ability for users to select a specific team and filter
matches based on that selection. This makes it easy for users to keep track of their favorite teams'
schedules and results.

In addition, the app allows users to watch highlights from previous matches, so they can catch up on
the action they might have missed. And for upcoming matches, users can set reminders to ensure they
don't forget to tune in. Our app will even notify users when a match is about to start, so they
never miss a moment of the action.

## Tech stack & library
- Minimum SDK 24
- Kotlin based, Coroutine for asynchronous.
- Jetpack:
  - Lifecycle: Observe Android lifecycles and handle UI states upon the lifecycle changes
  - ViewModel: Manages business logics, UI-related data holder and lifecycle aware.
  - ViewBinding: Binds UI layout to easily access UI elements in activities or fragments.
  - Room: Constructs database by providing an abstraction layer over SQLite to allow fluent database access
  - Hilt: Dependency injection.
- Architecture:
  - MVVM Architecture (View - ViewModel - Model).
  - Repository pattern.
- Retrofit2 & OkHttp3: Construct the REST APIs.
- Moshi: A modern JSON Library for Kotlin and Java.
- KSP: Kotlin symbol processing API.
- Material components: CardView.
- Glide: Loading images from network.
- JUnit 4: Unit test.

## Further improvements:
### Technical:
- Apply paging to optimize performance when loading match list or team data.
- Improve the Media viewer screen by using ExoPlayer.
- Implement more efficient local database to save matches and recording the notification of matches.
- Enhance the Application UI.
- Adding UI test code.

### Features:
- Add more feature like live match records details.
- Add favorite team, players of the team.
- Add comment on the match.
- Buy team items (clothes,...).
- Buy tickets for upcoming matches, or all matches of a team in the season.
- Fan group chatting.
- Fan group event, event invitation.
- Pre-match gameplay.
- ...

