package gr.hua.it21533.kitchenerMap.interfaces

interface MenuView {
    fun didFilterChange(filterValue: String, filterType: String)
    fun backToMenu()
}