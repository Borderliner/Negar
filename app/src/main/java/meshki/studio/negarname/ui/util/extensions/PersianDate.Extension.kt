package meshki.studio.negarname.ui.util.extensions

import saman.zamani.persiandate.PersianDate

fun grgMonthToPersian(gregMonth: String): String {
    return when (gregMonth) {
        "January" -> "ژانویه"
        "February" -> "فوریه"
        "March" -> "مارس"
        "April" -> "آوریل"
        "May" -> "مِی"
        "June" -> "ژوئَن"
        "July" -> "جولای"
        "August" -> "آگوست"
        "September" -> "سپتامبر"
        "October" -> "اکتبر"
        "November" -> "نوامبر"
        "December" -> "دسامبر"
        else -> ""
    }
}

fun PersianDate.grgMonthToPersian(): String {
    return when (this.grgMonthName) {
        "January" -> "ژانویه"
        "February" -> "فوریه"
        "March" -> "مارس"
        "April" -> "آوریل"
        "May" -> "مِی"
        "June" -> "ژوئَن"
        "July" -> "جولای"
        "August" -> "آگوست"
        "September" -> "سپتامبر"
        "October" -> "اکتبر"
        "November" -> "نوامبر"
        "December" -> "دسامبر"
        else -> ""
    }
}
