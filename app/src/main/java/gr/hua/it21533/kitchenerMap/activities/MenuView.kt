package gr.hua.it21533.kitchenerMap.activities

interface MenuView {
    fun didFilterChange(filterValue: String, filterType: String)
    fun backToMenu()
}