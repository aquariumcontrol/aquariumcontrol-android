package com.laimburggasse.aquariumcontrol.feed.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FeedProfileImport {
    @SerialName(value = "ProfileID")
    var profileId: Int = 0
    @SerialName(value = "ProfileName")
    var profileName: String = ""

    // Phase #1
    @SerialName(value = "Pause01Duration")
    var pause01duration: Int? = 0
    @SerialName(value = "Pause01Skimmer")
    var pause01skimmer: Int = 1
    @SerialName(value = "Pause01MPmp1") var pause01mainPump1: Int = 0
    @SerialName(value = "Pause01MPmp2") var pause01mainPump2: Int = 0
    @SerialName(value = "Pause01APmp1") var pause01auxPump1: Int = 0
    @SerialName(value = "Pause01APmp2") var pause01auxPump2: Int = 0
    @SerialName(value = "Feed01Duration")
    var feed01duration: Int? = 0
    @SerialName(value = "Feed01Skimmer")
    var feed01skimmer: Int = 1
    @SerialName(value = "Feed01MPmp1") var feed01mainPump1: Int = 0
    @SerialName(value = "Feed01MPmp2") var feed01mainPump2: Int = 0
    @SerialName(value = "Feed01APmp1") var feed01auxPump1: Int = 0
    @SerialName(value = "Feed01APmp2") var feed01auxPump2: Int = 0

    // Phase #2
    @SerialName(value = "Pause02Duration")
    var pause02duration: Int? = 0
    @SerialName(value = "Pause02Skimmer")
    var pause02skimmer: Int = 1
    @SerialName(value = "Pause02MPmp1") var pause02mainPump1: Int = 0
    @SerialName(value = "Pause02MPmp2") var pause02mainPump2: Int = 0
    @SerialName(value = "Pause02APmp1") var pause02auxPump1: Int = 0
    @SerialName(value = "Pause02APmp2") var pause02auxPump2: Int = 0
    @SerialName(value = "Feed02Duration")
    var feed02duration: Int? = 0
    @SerialName(value = "Feed02Skimmer")
    var feed02skimmer: Int = 1
    @SerialName(value = "Feed02MPmp1") var feed02mainPump1: Int = 0
    @SerialName(value = "Feed02MPmp2") var feed02mainPump2: Int = 0
    @SerialName(value = "Feed02APmp1") var feed02auxPump1: Int = 0
    @SerialName(value = "Feed02APmp2") var feed02auxPump2: Int = 0

    // Phase #3
    @SerialName(value = "Pause03Duration")
    var pause03duration: Int? = 0
    @SerialName(value = "Pause03Skimmer")
    var pause03skimmer: Int = 1
    @SerialName(value = "Pause03MPmp1") var pause03mainPump1: Int = 0
    @SerialName(value = "Pause03MPmp2") var pause03mainPump2: Int = 0
    @SerialName(value = "Pause03APmp1") var pause03auxPump1: Int = 0
    @SerialName(value = "Pause03APmp2") var pause03auxPump2: Int = 0
    @SerialName(value = "Feed03Duration")
    var feed03duration: Int? = 0
    @SerialName(value = "Feed03Skimmer")
    var feed03skimmer: Int = 1
    @SerialName(value = "Feed03MPmp1") var feed03mainPump1: Int = 0
    @SerialName(value = "Feed03MPmp2") var feed03mainPump2: Int = 0
    @SerialName(value = "Feed03APmp1") var feed03auxPump1: Int = 0
    @SerialName(value = "Feed03APmp2") var feed03auxPump2: Int = 0

    // Phase #4
    @SerialName(value = "Pause04Duration")
    var pause04duration: Int? = 0
    @SerialName(value = "Pause04Skimmer")
    var pause04skimmer: Int = 1
    @SerialName(value = "Pause04MPmp1") var pause04mainPump1: Int = 0
    @SerialName(value = "Pause04MPmp2") var pause04mainPump2: Int = 0
    @SerialName(value = "Pause04APmp1") var pause04auxPump1: Int = 0
    @SerialName(value = "Pause04APmp2") var pause04auxPump2: Int = 0
    @SerialName(value = "Feed04Duration")
    var feed04duration: Int? = 0
    @SerialName(value = "Feed04Skimmer")
    var feed04skimmer: Int = 1
    @SerialName(value = "Feed04MPmp1") var feed04mainPump1: Int = 0
    @SerialName(value = "Feed04MPmp2") var feed04mainPump2: Int = 0
    @SerialName(value = "Feed04APmp1") var feed04auxPump1: Int = 0
    @SerialName(value = "Feed04APmp2") var feed04auxPump2: Int = 0

    // Phase #5
    @SerialName(value = "Pause05Duration")
    var pause05duration: Int? = 0
    @SerialName(value = "Pause05Skimmer")
    var pause05skimmer: Int = 1
    @SerialName(value = "Pause05MPmp1") var pause05mainPump1: Int = 0
    @SerialName(value = "Pause05MPmp2") var pause05mainPump2: Int = 0
    @SerialName(value = "Pause05APmp1") var pause05auxPump1: Int = 0
    @SerialName(value = "Pause05APmp2") var pause05auxPump2: Int = 0
    @SerialName(value = "Feed05Duration")
    var feed05duration: Int? = 0
    @SerialName(value = "Feed05Skimmer")
    var feed05skimmer: Int = 1
    @SerialName(value = "Feed05MPmp1") var feed05mainPump1: Int = 0
    @SerialName(value = "Feed05MPmp2") var feed05mainPump2: Int = 0
    @SerialName(value = "Feed05APmp1") var feed05auxPump1: Int = 0
    @SerialName(value = "Feed05APmp2") var feed05auxPump2: Int = 0

    // Phase #6
    @SerialName(value = "Pause06Duration")
    var pause06duration: Int? = 0
    @SerialName(value = "Pause06Skimmer")
    var pause06skimmer: Int = 1
    @SerialName(value = "Pause06MPmp1") var pause06mainPump1: Int = 0
    @SerialName(value = "Pause06MPmp2") var pause06mainPump2: Int = 0
    @SerialName(value = "Pause06APmp1") var pause06auxPump1: Int = 0
    @SerialName(value = "Pause06APmp2") var pause06auxPump2: Int = 0
    @SerialName(value = "Feed06Duration")
    var feed06duration: Int? = 0
    @SerialName(value = "Feed06Skimmer")
    var feed06skimmer: Int = 1
    @SerialName(value = "Feed06MPmp1") var feed06mainPump1: Int = 0
    @SerialName(value = "Feed06MPmp2") var feed06mainPump2: Int = 0
    @SerialName(value = "Feed06APmp1") var feed06auxPump1: Int = 0
    @SerialName(value = "Feed06APmp2") var feed06auxPump2: Int = 0

    // Phase #7
    @SerialName(value = "Pause07Duration")
    var pause07duration: Int? = 0
    @SerialName(value = "Pause07Skimmer")
    var pause07skimmer: Int = 1
    @SerialName(value = "Pause07MPmp1") var pause07mainPump1: Int = 0
    @SerialName(value = "Pause07MPmp2") var pause07mainPump2: Int = 0
    @SerialName(value = "Pause07APmp1") var pause07auxPump1: Int = 0
    @SerialName(value = "Pause07APmp2") var pause07auxPump2: Int = 0
    @SerialName(value = "Feed07Duration")
    var feed07duration: Int? = 0
    @SerialName(value = "Feed07Skimmer")
    var feed07skimmer: Int = 1
    @SerialName(value = "Feed07MPmp1") var feed07mainPump1: Int = 0
    @SerialName(value = "Feed07MPmp2") var feed07mainPump2: Int = 0
    @SerialName(value = "Feed07APmp1") var feed07auxPump1: Int = 0
    @SerialName(value = "Feed07APmp2") var feed07auxPump2: Int = 0

    // Phase #8
    @SerialName(value = "Pause08Duration")
    var pause08duration: Int? = 0
    @SerialName(value = "Pause08Skimmer")
    var pause08skimmer: Int = 1
    @SerialName(value = "Pause08MPmp1") var pause08mainPump1: Int = 0
    @SerialName(value = "Pause08MPmp2") var pause08mainPump2: Int = 0
    @SerialName(value = "Pause08APmp1") var pause08auxPump1: Int = 0
    @SerialName(value = "Pause08APmp2") var pause08auxPump2: Int = 0
    @SerialName(value = "Feed08Duration")
    var feed08duration: Int? = 0
    @SerialName(value = "Feed08Skimmer")
    var feed08skimmer: Int = 1
    @SerialName(value = "Feed08MPmp1") var feed08mainPump1: Int = 0
    @SerialName(value = "Feed08MPmp2") var feed08mainPump2: Int = 0
    @SerialName(value = "Feed08APmp1") var feed08auxPump1: Int = 0
    @SerialName(value = "Feed08APmp2") var feed08auxPump2: Int = 0

    // Phase #9
    @SerialName(value = "Pause09Duration")
    var pause09duration: Int? = 0
    @SerialName(value = "Pause09Skimmer")
    var pause09skimmer: Int = 1
    @SerialName(value = "Pause09MPmp1") var pause09mainPump1: Int = 0
    @SerialName(value = "Pause09MPmp2") var pause09mainPump2: Int = 0
    @SerialName(value = "Pause09APmp1") var pause09auxPump1: Int = 0
    @SerialName(value = "Pause09APmp2") var pause09auxPump2: Int = 0
    @SerialName(value = "Feed09Duration")
    var feed09duration: Int? = 0
    @SerialName(value = "Feed09Skimmer")
    var feed09skimmer: Int = 1
    @SerialName(value = "Feed09MPmp1") var feed09mainPump1: Int = 0
    @SerialName(value = "Feed09MPmp2") var feed09mainPump2: Int = 0
    @SerialName(value = "Feed09APmp1") var feed09auxPump1: Int = 0
    @SerialName(value = "Feed09APmp2") var feed09auxPump2: Int = 0

    // Phase #10
    @SerialName(value = "Pause10Duration")
    var pause10duration: Int? = 0
    @SerialName(value = "Pause10Skimmer")
    var pause10skimmer: Int = 1
    @SerialName(value = "Pause10MPmp1") var pause10mainPump1: Int = 0
    @SerialName(value = "Pause10MPmp2") var pause10mainPump2: Int = 0
    @SerialName(value = "Pause10APmp1") var pause10auxPump1: Int = 0
    @SerialName(value = "Pause10APmp2") var pause10auxPump2: Int = 0
    @SerialName(value = "Feed10Duration")
    var feed10duration: Int? = 0
    @SerialName(value = "Feed10Skimmer")
    var feed10skimmer: Int = 1
    @SerialName(value = "Feed10MPmp1") var feed10mainPump1: Int = 0
    @SerialName(value = "Feed10MPmp2") var feed10mainPump2: Int = 0
    @SerialName(value = "Feed10APmp1") var feed10auxPump1: Int = 0
    @SerialName(value = "Feed10APmp2") var feed10auxPump2: Int = 0
}