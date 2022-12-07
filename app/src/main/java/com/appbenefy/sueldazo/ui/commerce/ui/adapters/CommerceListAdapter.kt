package com.appbenefy.sueldazo.ui.commerce.ui.adapters

//class CommerceListAdapter
//@Inject constructor() :
//        PagedListAdapter<Commerce, CommerceListAdapter.ViewHolder>(
//                CommerceDiff()
//        ) {
//
//    private var activity: CommerceList2Activity? = null
//    internal var collection: List<Commerce> by Delegates.observable(emptyList()) { _, _, _ ->
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount() = collection.size
//    fun setActivity(activity: CommerceList2Activity) { this.activity = activity }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_commerce_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        collection[position].let { holder.bind(it) }
//    }
//
//    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        fun bind(model: Commerce) {
//            Picasso.get().load(model.urlImage).into(view.imageId)
//            view.nameId.text = model.nombre
//            view.categoryId.text = model.categoryName
//            view.setOnClickListener { activity?.openDetail(model) }
//        }
//    }
//
//}