package meshki.studio.negarname.util

import androidx.compose.runtime.Composable
import java.lang.IllegalArgumentException

class Zodiac {
    companion object {
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

        fun calculateZodiacPersian(month: Int, day: Int): String {
            return when (calculateZodiac(month, day)) {
                "aries" -> "بَرّه"
                "taurus" -> "گاو"
                "gemini" -> "دو پیکَر"
                "cancer" -> "خرچنگ"
                "leo" -> "شیر"
                "virgo" -> "خوشه"
                "libra" -> "ترازو"
                "scorpio" -> "کَژدُم"
                "sagittarius" -> "کَمان"
                "capricorn" -> "بُز"
                "aquarius" -> "آب‌ریز"
                "pisces" -> "ماهی"
                else -> ""
            }
        }

        fun calculateZodiacPersian(zodiac: String): String {
            return when (zodiac) {
                "aries" -> "بَرّه"
                "taurus" -> "گاو"
                "gemini" -> "دو پیکَر"
                "cancer" -> "خرچنگ"
                "leo" -> "شیر"
                "virgo" -> "خوشه"
                "libra" -> "ترازو"
                "scorpio" -> "کَژدُم"
                "sagittarius" -> "کَمان"
                "capricorn" -> "بُز"
                "aquarius" -> "آب‌ریز"
                "pisces" -> "ماهی"
                else -> ""
            }
        }

        fun zodiacToEmoji(zodiac: String): String {
            return when (zodiac) {
                "aries" -> "♈"
                "taurus" -> "♉"
                "gemini" -> "♊"
                "cancer" -> "♋"
                "leo" -> "♌"
                "virgo" -> "♍"
                "libra" -> "♎"
                "scorpio" -> "♏"
                "sagittarius" -> "♐"
                "capricorn" -> "♑"
                "aquarius" -> "♒"
                "pisces" -> "♓"
                else -> ""
            }
        }

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
                else -> ""
            }
        }

        fun calculateChineseYearPersian(year: Int): String {
            return when (year % 12) {
                0 -> "میمون"
                1 -> "خروس"
                2 -> "سگ"
                3 -> "خوک"
                4 -> "موش"
                5 -> "گاو"
                6 -> "ببر"
                7 -> "خرگوش"
                8 -> "اژدها"
                9 -> "مار"
                10 -> "اسب"
                11 -> "گوسپند"
                else -> ""
            }
        }

        fun chineseYearToEmoji(year: Int): String {
            return when (year % 12) {
                0 -> "\uD83D\uDC12"
                1 -> "\uD83D\uDC13"
                2 -> "\uD83D\uDC15"
                3 -> "\uD83D\uDC16"
                4 -> "\uD83D\uDC00"
                5 -> "\uD83D\uDC02"
                6 -> "\uD83D\uDC05"
                7 -> "\uD83D\uDC07"
                8 -> "\uD83D\uDC09"
                9 -> "\uD83D\uDC0D"
                10 -> "\uD83D\uDC0E"
                11 -> "\uD83D\uDC0F"
                else -> ""
            }
        }
    }
}
