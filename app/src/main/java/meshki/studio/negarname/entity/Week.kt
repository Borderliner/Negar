package meshki.studio.negarname.entity

import java.text.DateFormatSymbols
import java.util.Calendar

val DAY_OF_WEEK_TO_CALENDAR = arrayOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)

fun DateFormatSymbols.getShortDayOfWeek(dayOfWeek: Int) = shortWeekdays[DAY_OF_WEEK_TO_CALENDAR[dayOfWeek]]

data class Day(val name: String, var value: Boolean)

class Week {
    val list = mutableListOf<Day>()
    private val firstDayOfWeek = Calendar.getInstance().firstDayOfWeek - 1

    init {
        for(i in 0..6) {
            val dayOfWeekName = DateFormatSymbols().getShortDayOfWeek(firstDayOfWeek + i)
            list.add(Day(dayOfWeekName, false))
        }

    }
    fun setDayValueByName(name: String, value: Boolean) {
        list.forEachIndexed { idx, item ->
            if (item.name == name) {
                list[idx] = Day(name, value)
            }
        }
    }

    fun setDayValueByIndex(idx: Int, value: Boolean) {
        list[idx].value = value
    }

    fun setDay(day: Day) {
        list.forEachIndexed { idx, item ->
            if (item.name == day.name) {
                list[idx] = day
            }
        }
    }

    fun getDay(idx: Int): Day {
        return list[idx]
    }
}