package meshki.studio.negarname.entity

sealed class OrderType {
    data object Ascending: OrderType()
    data object Descending: OrderType()
}
