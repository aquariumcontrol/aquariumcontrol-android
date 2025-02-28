package com.example.aquariumcontroller.feed

import com.example.aquariumcontroller.webapi.WebApiParams
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class FeedProfile(feedProfileImport: FeedProfileImport) {
    val profileID: Int = feedProfileImport.profileId
    val profileName: String = feedProfileImport.profileName
    var feedProfilePhases: List<FeedProfilePhase> = listOf()
    private var feedProfilePhasesBackup: List<FeedProfilePhase> = listOf()

    init {

        // Phase #1
        val pause01 = FeedProfilePhase(
            duration = feedProfileImport.pause01duration,
            skimmer = feedProfileImport.pause01skimmer,
            mainPump1 = feedProfileImport.pause01mainPump1,
            mainPump2 = feedProfileImport.pause01mainPump2,
            auxPump1 = feedProfileImport.pause01auxPump1,
            auxPump2 = feedProfileImport.pause01auxPump2,
            isPause = true,
            idx = 0
        )
        val feed01 = FeedProfilePhase(
            duration = feedProfileImport.feed01duration,
            skimmer = feedProfileImport.feed01skimmer,
            mainPump1 = feedProfileImport.feed01mainPump1,
            mainPump2 = feedProfileImport.feed01mainPump2,
            auxPump1 = feedProfileImport.feed01auxPump1,
            auxPump2 = feedProfileImport.feed01auxPump2,
            isPause = false,
            idx = 0
        )

        // Phase #2
        val pause02 = FeedProfilePhase(
            duration = feedProfileImport.pause02duration,
            skimmer = feedProfileImport.pause02skimmer,
            mainPump1 = feedProfileImport.pause02mainPump1,
            mainPump2 = feedProfileImport.pause02mainPump2,
            auxPump1 = feedProfileImport.pause02auxPump1,
            auxPump2 = feedProfileImport.pause02auxPump2,
            isPause = true,
            idx = 1
        )
        val feed02 = FeedProfilePhase(
            duration = feedProfileImport.feed02duration,
            skimmer = feedProfileImport.feed02skimmer,
            mainPump1 = feedProfileImport.feed02mainPump1,
            mainPump2 = feedProfileImport.feed02mainPump2,
            auxPump1 = feedProfileImport.feed02auxPump1,
            auxPump2 = feedProfileImport.feed02auxPump2,
            isPause = false,
            idx = 1
        )

        // Phase #3
        val pause03 = FeedProfilePhase(
            duration = feedProfileImport.pause03duration,
            skimmer = feedProfileImport.pause03skimmer,
            mainPump1 = feedProfileImport.pause03mainPump1,
            mainPump2 = feedProfileImport.pause03mainPump2,
            auxPump1 = feedProfileImport.pause03auxPump1,
            auxPump2 = feedProfileImport.pause03auxPump2,
            isPause = true,
            idx = 2
        )
        val feed03 = FeedProfilePhase(
            duration = feedProfileImport.feed03duration,
            skimmer = feedProfileImport.feed03skimmer,
            mainPump1 = feedProfileImport.feed03mainPump1,
            mainPump2 = feedProfileImport.feed03mainPump2,
            auxPump1 = feedProfileImport.feed03auxPump1,
            auxPump2 = feedProfileImport.feed03auxPump2,
            isPause = false,
            idx = 2
        )

        // Phase #4
        val pause04 = FeedProfilePhase(
            duration = feedProfileImport.pause04duration,
            skimmer = feedProfileImport.pause04skimmer,
            mainPump1 = feedProfileImport.pause04mainPump1,
            mainPump2 = feedProfileImport.pause04mainPump2,
            auxPump1 = feedProfileImport.pause04auxPump1,
            auxPump2 = feedProfileImport.pause04auxPump2,
            isPause = true,
            idx = 3
        )
        val feed04 = FeedProfilePhase(
            duration = feedProfileImport.feed04duration,
            skimmer = feedProfileImport.feed04skimmer,
            mainPump1 = feedProfileImport.feed04mainPump1,
            mainPump2 = feedProfileImport.feed04mainPump2,
            auxPump1 = feedProfileImport.feed04auxPump1,
            auxPump2 = feedProfileImport.feed04auxPump2,
            isPause = false,
            idx = 3
        )

        // Phase #5
        val pause05 = FeedProfilePhase(
            duration = feedProfileImport.pause05duration,
            skimmer = feedProfileImport.pause05skimmer,
            mainPump1 = feedProfileImport.pause05mainPump1,
            mainPump2 = feedProfileImport.pause05mainPump2,
            auxPump1 = feedProfileImport.pause05auxPump1,
            auxPump2 = feedProfileImport.pause05auxPump2,
            isPause = true,
            idx = 4
        )
        val feed05 = FeedProfilePhase(
            duration = feedProfileImport.feed05duration,
            skimmer = feedProfileImport.feed05skimmer,
            mainPump1 = feedProfileImport.feed05mainPump1,
            mainPump2 = feedProfileImport.feed05mainPump2,
            auxPump1 = feedProfileImport.feed05auxPump1,
            auxPump2 = feedProfileImport.feed05auxPump2,
            isPause = false,
            idx = 4
        )

        // Phase #6
        val pause06 = FeedProfilePhase(
            duration = feedProfileImport.pause06duration,
            skimmer = feedProfileImport.pause06skimmer,
            mainPump1 = feedProfileImport.pause06mainPump1,
            mainPump2 = feedProfileImport.pause06mainPump2,
            auxPump1 = feedProfileImport.pause06auxPump1,
            auxPump2 = feedProfileImport.pause06auxPump2,
            isPause = true,
            idx = 5
        )
        val feed06 = FeedProfilePhase(
            duration = feedProfileImport.feed06duration,
            skimmer = feedProfileImport.feed06skimmer,
            mainPump1 = feedProfileImport.feed06mainPump1,
            mainPump2 = feedProfileImport.feed06mainPump2,
            auxPump1 = feedProfileImport.feed06auxPump1,
            auxPump2 = feedProfileImport.feed06auxPump2,
            isPause = false,
            idx = 5
        )

        // Phase #7
        val pause07 = FeedProfilePhase(
            duration = feedProfileImport.pause07duration,
            skimmer = feedProfileImport.pause07skimmer,
            mainPump1 = feedProfileImport.pause07mainPump1,
            mainPump2 = feedProfileImport.pause07mainPump2,
            auxPump1 = feedProfileImport.pause07auxPump1,
            auxPump2 = feedProfileImport.pause07auxPump2,
            isPause = true,
            idx = 6
        )
        val feed07 = FeedProfilePhase(
            duration = feedProfileImport.feed07duration,
            skimmer = feedProfileImport.feed07skimmer,
            mainPump1 = feedProfileImport.feed07mainPump1,
            mainPump2 = feedProfileImport.feed07mainPump2,
            auxPump1 = feedProfileImport.feed07auxPump1,
            auxPump2 = feedProfileImport.feed07auxPump2,
            isPause = false,
            idx = 6
        )

        // Phase #8
        val pause08 = FeedProfilePhase(
            duration = feedProfileImport.pause08duration,
            skimmer = feedProfileImport.pause08skimmer,
            mainPump1 = feedProfileImport.pause08mainPump1,
            mainPump2 = feedProfileImport.pause08mainPump2,
            auxPump1 = feedProfileImport.pause08auxPump1,
            auxPump2 = feedProfileImport.pause08auxPump2,
            isPause = true,
            idx = 7
        )
        val feed08 = FeedProfilePhase(
            duration = feedProfileImport.feed08duration,
            skimmer = feedProfileImport.feed08skimmer,
            mainPump1 = feedProfileImport.feed08mainPump1,
            mainPump2 = feedProfileImport.feed08mainPump2,
            auxPump1 = feedProfileImport.feed08auxPump1,
            auxPump2 = feedProfileImport.feed08auxPump2,
            isPause = false,
            idx = 7
        )

        // Phase #9
        val pause09 = FeedProfilePhase(
            duration = feedProfileImport.pause09duration,
            skimmer = feedProfileImport.pause09skimmer,
            mainPump1 = feedProfileImport.pause09mainPump1,
            mainPump2 = feedProfileImport.pause09mainPump2,
            auxPump1 = feedProfileImport.pause09auxPump1,
            auxPump2 = feedProfileImport.pause09auxPump2,
            isPause = true,
            idx = 8
        )
        val feed09 = FeedProfilePhase(
            duration = feedProfileImport.feed09duration,
            skimmer = feedProfileImport.feed09skimmer,
            mainPump1 = feedProfileImport.feed09mainPump1,
            mainPump2 = feedProfileImport.feed09mainPump2,
            auxPump1 = feedProfileImport.feed09auxPump1,
            auxPump2 = feedProfileImport.feed09auxPump2,
            isPause = false,
            idx = 8
        )

        // Phase #10
        val pause10 = FeedProfilePhase(
            duration = feedProfileImport.pause10duration,
            skimmer = feedProfileImport.pause10skimmer,
            mainPump1 = feedProfileImport.pause10mainPump1,
            mainPump2 = feedProfileImport.pause10mainPump2,
            auxPump1 = feedProfileImport.pause10auxPump1,
            auxPump2 = feedProfileImport.pause10auxPump2,
            isPause = true,
            idx = 9
        )
        val feed10 = FeedProfilePhase(
            duration = feedProfileImport.feed10duration,
            skimmer = feedProfileImport.feed10skimmer,
            mainPump1 = feedProfileImport.feed10mainPump1,
            mainPump2 = feedProfileImport.feed10mainPump2,
            auxPump1 = feedProfileImport.feed10auxPump1,
            auxPump2 = feedProfileImport.feed10auxPump2,
            isPause = false,
            idx = 9
        )

        this.feedProfilePhases = listOf(
            pause01,
            feed01,
            pause02,
            feed02,
            pause03,
            feed03,
            pause04,
            feed04,
            pause05,
            feed05,
            pause06,
            feed06,
            pause07,
            feed07,
            pause08,
            feed08,
            pause09,
            feed09,
            pause10,
            feed10
        )

        this.feedProfilePhasesBackup = listOf(
            pause01.copy(),
            feed01.copy(),
            pause02.copy(),
            feed02.copy(),
            pause03.copy(),
            feed03.copy(),
            pause04.copy(),
            feed04.copy(),
            pause05.copy(),
            feed05.copy(),
            pause06.copy(),
            feed06.copy(),
            pause07.copy(),
            feed07.copy(),
            pause08.copy(),
            feed08.copy(),
            pause09.copy(),
            feed09.copy(),
            pause10.copy(),
            feed10.copy()
        )
    }

    fun reset() {
        // This function copies the content from the backup data structure to
        // the data structure actively used for display, editing and subsequent communication
        // to the controller.
        feedProfilePhases.zip(feedProfilePhasesBackup).forEach { (original, backup) ->
            original.overwriteWith(backup)
        }
    }

    fun update() {
        // This function copies the content from the main data structure to the backup
        // data structure. It is called after the user requested update of the feed profile to
        // the controller.
        feedProfilePhasesBackup.zip(feedProfilePhases).forEach { (backup, original) ->
            backup.overwriteWith(original)
        }
    }

    fun getFeedProfileApiCallParameters(): Map<String, String> {
        // This function iterates over the feed profile phases and adds all the parameters to the
        // map which is then used to form the GET part of the API call
        val myMap = mutableMapOf<String, String>()
        for ((idx, feedProfilePhase) in feedProfilePhases.withIndex()) {
            myMap += feedProfilePhase.getFeedProfilePhaseApiCallParameters(
                (idx.toFloat() / 2.0).toInt()
            )
        }
        myMap += mapOf(
            WebApiParams.PROFILE_NAME to this.profileName,
            WebApiParams.PROFILE_ID to this.profileID.toString()
        )
        return myMap
    }

    fun getFeedProfileSchedulingApiCallParameters(isDaily: Boolean, timestamp: Timestamp): Map<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStampString = dateFormat.format(timestamp).replace(" ", "T")

        val params = mutableMapOf<String, String>()
        params[WebApiParams.PROFILE_NAME] = profileName
        params[WebApiParams.REPEAT_DAILY] = if (isDaily) "1" else "0"
        params[WebApiParams.TIMESTAMP] = timeStampString
        return params
    }

    fun calcDuration(): Float {
        return feedProfilePhases.sumOf { it.duration }.toFloat()
    }
}
