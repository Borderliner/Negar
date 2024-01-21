package meshki.studio.negarname.entity

import java.text.DateFormatSymbols
import java.util.Calendar

val DAY_OF_WEEK_TO_CALENDAR = arrayOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)

fun DateFormatSymbols.getShortDayOfWeek(dayOfWeek: Int) = shortWeekdays[DAY_OF_WEEK_TO_CALENDAR[dayOfWeek]]

data class Day(val name: String, var calendarIndex: Int, var value: Boolean)

class Week {
    companion object {
        fun fromAlarms(alarms: List<Alarm>): Week {
            val week = Week()
            alarms.forEach {
                val cal = Calendar.getInstance().apply {
                    timeInMillis = it.time
                }
                val calDay = cal.get(Calendar.DAY_OF_WEEK)
                week.setDayValueByIndex(calDay - 1, true)
            }
            return week
        }
    }

    val list = mutableListOf<Day>()
    private val firstDayOfWeek = Calendar.getInstance().firstDayOfWeek - 1

    init {
        println("First day of week index: $firstDayOfWeek")
        for(i in 0..6) {
            val dayIndex = if (firstDayOfWeek + i > 6) i - 1 else firstDayOfWeek + i
            val dayOfWeekName = DateFormatSymbols().getShortDayOfWeek(dayIndex)
            list.add(Day(dayOfWeekName, DAY_OF_WEEK_TO_CALENDAR[dayIndex], false))
        }
    }

    fun setDayValueByName(name: String, value: Boolean) {
        list.forEachIndexed { idx, item ->
            if (item.name == name) {
                list[idx] = Day(name, item.calendarIndex, value)
            }
        }
    }

    fun setDayValueByIndex(idx: Int, value: Boolean) {
        val day = list[idx]
        list[idx] = day.copy(
            name = day.name,
            calendarIndex = day.calendarIndex,
            value = value
        )
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

    fun getCalendarDay(idx: Int): Int {
        return list[idx].calendarIndex
    }
}