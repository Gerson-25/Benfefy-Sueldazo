package syntepro.util.picker

interface OnItemPickListClickListener<T> {
    fun onItemClicked(pickList: T, position: Int)
    fun onResetClicked()
}