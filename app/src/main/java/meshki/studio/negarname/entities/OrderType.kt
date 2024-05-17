package meshki.studio.negarname.entities

sealed class OrderType {
    data object Ascending: OrderType()
    data object Descending: OrderType()
}
