# QWeather


Weather Report app 


App Name : QWeather

1.Splash screen with Lottie animation
2. Bookmarks screen
    All the saved place bookmarks will appear here
    Location fab button - will navigate to place selection scree which is map activity
3. Map Activity 
    Map will be shown with current location selected
    Select any place/location by tapping on map
    Bookmark any place/location by tapping “ADD TO BOOKMARK” button
    Once saved , will navigated to bookmarks screen with updated data [latest will be on top]
4. We can delete any saved book  by using delete button on each bookmark
5. Tapping on bookmark item will navigate to Weather report screen with Today and Weekly tabs to show today and weekly report [followed api calls]
 

- Database used : Room
- Architecture : MVVM [Model - View - ViewModel]
- Kotlin and coroutines used
- Places sdk for Maps activity
- Fragments used for today/weekly weather reports in single activity[WeatherReportActivity]
- Retrofit for network calls
- Used Android architectural components like LiveData, ViewModel
- Coordinator layout used ing BookmarkActivity
- ConstraintLayout,CardViews, BottomNavigation with tabs are also used in other activities

