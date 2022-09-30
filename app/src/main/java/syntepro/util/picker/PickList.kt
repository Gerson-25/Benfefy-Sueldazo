package syntepro.util

import syntepro.util.picker.ListablePicker

class PickList: ListablePicker {
    var code: String = ""
    private var title: String = ""
    private var description: String = ""

    constructor() {}
    constructor(code: String, title: String, desc: String) {
        this.code = code
        this.title = title
        this.description = desc
    }

    override fun getCodeValue(): String  {return code}
    override fun getTitleValue(): String  {return title}
    override fun getDescValue(): String  {return description}

}