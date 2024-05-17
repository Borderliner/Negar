package meshki.studio.negarname.entities

sealed class OrderBy(val orderType: OrderType) {
    class Title(orderType: OrderType): OrderBy(orderType)
    class Date(orderType: OrderType): OrderBy(orderType)
    class Color(orderType: OrderType): OrderBy(orderType)
    class Completed(orderType: OrderType): OrderBy(orderType)

    //change note order type
    fun getType(orderType: OrderType = this.orderType) : OrderBy {
        return when(this){
            is Title -> Title(orderType)
            is Date -> Date(orderType)
            is Color -> Color(orderType)
            is Completed -> Completed(orderType)
        }
    }
}
