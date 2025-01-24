package com.example.aquariumcontroller.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FeedProfileImport {
    @SerialName("ProfileID")
    var profileId: Int = 0
    @SerialName("ProfileName")
    var profileName: String = ""
    
    // Phase #1
    @SerialName("Pause01Duration") val pause01duration: Int = 0
    @SerialName("Pause01Skimmer") val pause01skimmer: Int = 1
    @SerialName("Pause01MPmp1") var pause01mainPump1: Int = 0
    @SerialName("Pause01MPmp2") var pause01mainPump2: Int = 0
    @SerialName("Pause01APmp1") var pause01auxPump1: Int = 0
    @SerialName("Pause01APmp2") var pause01auxPump2: Int = 0
    @SerialName("Feed01Duration") val feed01duration: Int = 0
    @SerialName("Feed01Skimmer") val feed01skimmer: Int = 1
    @SerialName("Feed01MPmp1") var feed01mainPump1: Int = 0
    @SerialName("Feed01MPmp2") var feed01mainPump2: Int = 0
    @SerialName("Feed01APmp1") var feed01auxPump1: Int = 0
    @SerialName("Feed01APmp2") var feed01auxPump2: Int = 0

    // Phase #2
    @SerialName("Pause02Duration") val pause02duration: Int = 0
    @SerialName("Pause02Skimmer") val pause02skimmer: Int = 1
    @SerialName("Pause02MPmp1") var pause02mainPump1: Int = 0
    @SerialName("Pause02MPmp2") var pause02mainPump2: Int = 0
    @SerialName("Pause02APmp1") var pause02auxPump1: Int = 0
    @SerialName("Pause02APmp2") var pause02auxPump2: Int = 0
    @SerialName("Feed02Duration") val feed02duration: Int = 0
    @SerialName("Feed02Skimmer") val feed02skimmer: Int = 1
    @SerialName("Feed02MPmp1") var feed02mainPump1: Int = 0
    @SerialName("Feed02MPmp2") var feed02mainPump2: Int = 0
    @SerialName("Feed02APmp1") var feed02auxPump1: Int = 0
    @SerialName("Feed02APmp2") var feed02auxPump2: Int = 0

    // Phase #3
    @SerialName("Pause03Duration") val pause03duration: Int = 0
    @SerialName("Pause03Skimmer") val pause03skimmer: Int = 1
    @SerialName("Pause03MPmp1") var pause03mainPump1: Int = 0
    @SerialName("Pause03MPmp2") var pause03mainPump2: Int = 0
    @SerialName("Pause03APmp1") var pause03auxPump1: Int = 0
    @SerialName("Pause03APmp2") var pause03auxPump2: Int = 0
    @SerialName("Feed03Duration") val feed03duration: Int = 0
    @SerialName("Feed03Skimmer") val feed03skimmer: Int = 1
    @SerialName("Feed03MPmp1") var feed03mainPump1: Int = 0
    @SerialName("Feed03MPmp2") var feed03mainPump2: Int = 0
    @SerialName("Feed03APmp1") var feed03auxPump1: Int = 0
    @SerialName("Feed03APmp2") var feed03auxPump2: Int = 0

    // Phase #4
    @SerialName("Pause04Duration") val pause04duration: Int = 0
    @SerialName("Pause04Skimmer") val pause04skimmer: Int = 1
    @SerialName("Pause04MPmp1") var pause04mainPump1: Int = 0
    @SerialName("Pause04MPmp2") var pause04mainPump2: Int = 0
    @SerialName("Pause04APmp1") var pause04auxPump1: Int = 0
    @SerialName("Pause04APmp2") var pause04auxPump2: Int = 0
    @SerialName("Feed04Duration") val feed04duration: Int = 0
    @SerialName("Feed04Skimmer") val feed04skimmer: Int = 1
    @SerialName("Feed04MPmp1") var feed04mainPump1: Int = 0
    @SerialName("Feed04MPmp2") var feed04mainPump2: Int = 0
    @SerialName("Feed04APmp1") var feed04auxPump1: Int = 0
    @SerialName("Feed04APmp2") var feed04auxPump2: Int = 0

    // Phase #5
    @SerialName("Pause05Duration") val pause05duration: Int = 0
    @SerialName("Pause05Skimmer") val pause05skimmer: Int = 1
    @SerialName("Pause05MPmp1") var pause05mainPump1: Int = 0
    @SerialName("Pause05MPmp2") var pause05mainPump2: Int = 0
    @SerialName("Pause05APmp1") var pause05auxPump1: Int = 0
    @SerialName("Pause05APmp2") var pause05auxPump2: Int = 0
    @SerialName("Feed05Duration") val feed05duration: Int = 0
    @SerialName("Feed05Skimmer") val feed05skimmer: Int = 1
    @SerialName("Feed05MPmp1") var feed05mainPump1: Int = 0
    @SerialName("Feed05MPmp2") var feed05mainPump2: Int = 0
    @SerialName("Feed05APmp1") var feed05auxPump1: Int = 0
    @SerialName("Feed05APmp2") var feed05auxPump2: Int = 0

    // Phase #6
    @SerialName("Pause06Duration") val pause06duration: Int = 0
    @SerialName("Pause06Skimmer") val pause06skimmer: Int = 1
    @SerialName("Pause06MPmp1") var pause06mainPump1: Int = 0
    @SerialName("Pause06MPmp2") var pause06mainPump2: Int = 0
    @SerialName("Pause06APmp1") var pause06auxPump1: Int = 0
    @SerialName("Pause06APmp2") var pause06auxPump2: Int = 0
    @SerialName("Feed06Duration") val feed06duration: Int = 0
    @SerialName("Feed06Skimmer") val feed06skimmer: Int = 1
    @SerialName("Feed06MPmp1") var feed06mainPump1: Int = 0
    @SerialName("Feed06MPmp2") var feed06mainPump2: Int = 0
    @SerialName("Feed06APmp1") var feed06auxPump1: Int = 0
    @SerialName("Feed06APmp2") var feed06auxPump2: Int = 0

    // Phase #7
    @SerialName("Pause07Duration") val pause07duration: Int = 0
    @SerialName("Pause07Skimmer") val pause07skimmer: Int = 1
    @SerialName("Pause07MPmp1") var pause07mainPump1: Int = 0
    @SerialName("Pause07MPmp2") var pause07mainPump2: Int = 0
    @SerialName("Pause07APmp1") var pause07auxPump1: Int = 0
    @SerialName("Pause07APmp2") var pause07auxPump2: Int = 0
    @SerialName("Feed07Duration") val feed07duration: Int = 0
    @SerialName("Feed07Skimmer") val feed07skimmer: Int = 1
    @SerialName("Feed07MPmp1") var feed07mainPump1: Int = 0
    @SerialName("Feed07MPmp2") var feed07mainPump2: Int = 0
    @SerialName("Feed07APmp1") var feed07auxPump1: Int = 0
    @SerialName("Feed07APmp2") var feed07auxPump2: Int = 0

    // Phase #8
    @SerialName("Pause08Duration") val pause08duration: Int = 0
    @SerialName("Pause08Skimmer") val pause08skimmer: Int = 1
    @SerialName("Pause08MPmp1") var pause08mainPump1: Int = 0
    @SerialName("Pause08MPmp2") var pause08mainPump2: Int = 0
    @SerialName("Pause08APmp1") var pause08auxPump1: Int = 0
    @SerialName("Pause08APmp2") var pause08auxPump2: Int = 0
    @SerialName("Feed08Duration") val feed08duration: Int = 0
    @SerialName("Feed08Skimmer") val feed08skimmer: Int = 1
    @SerialName("Feed08MPmp1") var feed08mainPump1: Int = 0
    @SerialName("Feed08MPmp2") var feed08mainPump2: Int = 0
    @SerialName("Feed08APmp1") var feed08auxPump1: Int = 0
    @SerialName("Feed08APmp2") var feed08auxPump2: Int = 0

    // Phase #9
    @SerialName("Pause09Duration") val pause09duration: Int = 0
    @SerialName("Pause09Skimmer") val pause09skimmer: Int = 1
    @SerialName("Pause09MPmp1") var pause09mainPump1: Int = 0
    @SerialName("Pause09MPmp2") var pause09mainPump2: Int = 0
    @SerialName("Pause09APmp1") var pause09auxPump1: Int = 0
    @SerialName("Pause09APmp2") var pause09auxPump2: Int = 0
    @SerialName("Feed09Duration") val feed09duration: Int = 0
    @SerialName("Feed09Skimmer") val feed09skimmer: Int = 1
    @SerialName("Feed09MPmp1") var feed09mainPump1: Int = 0
    @SerialName("Feed09MPmp2") var feed09mainPump2: Int = 0
    @SerialName("Feed09APmp1") var feed09auxPump1: Int = 0
    @SerialName("Feed09APmp2") var feed09auxPump2: Int = 0

    // Phase #10
    @SerialName("Pause10Duration") val pause10duration: Int = 0
    @SerialName("Pause10Skimmer") val pause10skimmer: Int = 1
    @SerialName("Pause10MPmp1") var pause10mainPump1: Int = 0
    @SerialName("Pause10MPmp2") var pause10mainPump2: Int = 0
    @SerialName("Pause10APmp1") var pause10auxPump1: Int = 0
    @SerialName("Pause10APmp2") var pause10auxPump2: Int = 0
    @SerialName("Feed10Duration") val feed10duration: Int = 0
    @SerialName("Feed10Skimmer") val feed10skimmer: Int = 1
    @SerialName("Feed10MPmp1") var feed10mainPump1: Int = 0
    @SerialName("Feed10MPmp2") var feed10mainPump2: Int = 0
    @SerialName("Feed10APmp1") var feed10auxPump1: Int = 0
    @SerialName("Feed10APmp2") var feed10auxPump2: Int = 0
}