package syntepro.util.picker

interface ListablePicker {
    abstract fun getCodeValue(): String
    abstract fun getTitleValue(): String
    abstract fun getDescValue(): String
}