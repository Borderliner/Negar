package meshki.studio.negarname.util

import java.lang.IllegalArgumentException

class Zodiac {
    companion object {
        fun calculateChineseYear(year: Int): String {
            return when (year % 12) {
                0 -> "monkey"
                1 -> "rooster"
                2 -> "dog"
                3 -> "pig"
                4 -> "rat"
                5 -> "ox"
                6 -> "tiger"
                7 -> "rabbit"
                8 -> "dragon"
                9 -> "snake"
                10 -> "horse"
                11 -> "sheep"
                else -> throw IllegalArgumentException()
            }
        }

        fun calculateZodiac(month: Int, day: Int): String {
            return when (month) {
                1 -> {
                    if (day < 20)
                        "capricorn"
                    else
                        "aquarius"
                }
                2 -> {
                    if (day < 19)
                        "aquarius"
                    else
                        "pisces"
                }
                3 -> {
                    if (day < 21)
                        "pisces"
                    else
                        "aries"
                }
                4 -> {
                    if (day < 20)
                        "aries"
                    else
                        "taurus"
                }
                5 -> {
                    if (day < 21)
                        "taurus"
                    else
                        "gemini"
                }
                6 -> {
                    if (day < 21)
                        "gemini"
                    else
                        "cancer"
                }
                7 -> {
                    if (day < 23)
                        "cancer"
                    else
                        "leo"
                }
                8 -> {
                    if (day < 23)
                        "leo"
                    else
                        "virgo"
                }
                9 -> {
                    if (day < 23)
                        "virgo"
                    else
                        "libra"
                }
                10 -> {
                    if (day < 23)
                        "libra"
                    else
                        "scorpio"
                }
                11 -> {
                    if (day < 22)
                        "scorpio"
                    else
                        "sagittarius"
                }
                12 -> {
                    if (day < 22)
                        "sagittarius"
                    else
                        "capricorn"
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
