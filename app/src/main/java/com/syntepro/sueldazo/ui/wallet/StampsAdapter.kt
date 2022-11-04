package com.syntepro.sueldazo.ui.wallet

//class StampsAdapter @Inject constructor() :
//        PagedListAdapter<Card, StampsAdapter.ViewHolder>(
//                CardDiff()
//        ) {
//
//    private var _currentPlan: String? = null
//
//    internal var collection: List<Card> by Delegates.observable(emptyList()) { _, _, _ ->
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount() = collection.size
//    fun setCurrentPlan(plan: String) { _currentPlan = plan }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.stamps_row, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        collection[position].let { holder.bind(it) }
//    }
//
//    inner class ViewHolder (val view: View): RecyclerView.ViewHolder(view) {
//        private var arrRow = emptyArray<LinearLayout>()
//        private var mColumns: Int = 0
//        private var mRows: Int = 0
//
//        fun bind(model: Card) {
//            arrRow = arrayOf(view.row1Id, view.row2Id, view.row3Id)
//            setRowsAndColumns(model.requiredStamps)
//            val size = getControlSize()
//                drawSeal(size, model.obtainedStamps, model.idCard, model.stamps)
//
//                when {
//                    model.isRedeem -> {
//                        view.stampId.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_redimmed))
//                        view.stampId.visibility = View.VISIBLE
//                    }
//                    model.expired -> {
//                        view.stampId.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_expired))
//                        view.stampId.visibility = View.VISIBLE
//                    }
//                    else -> view.stampId.visibility = View.GONE
//                }
//
////                when (model.status) {
////                    TarjetaSellos.STATUS_REDEEMED -> {
////                        view.stampId.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_redimmed))
////                        view.stampId.visibility = View.VISIBLE
////                    }
////                    TarjetaSellos.STATUS_EXPIRED -> {
////                        view.stampId.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_expired))
////                        view.stampId.visibility = View.VISIBLE
////                    }
////                    else -> view.stampId.visibility = View.GONE
////                }
//
//            view.infoId.setOnClickListener { showStampCardInfo(model.idCard) }
//        }
//
//        private fun getControlSize(): Int {
//            val displayMetrics = DisplayMetrics()
//            (view.context as Activity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
//            val s = (displayMetrics.widthPixels / mColumns) - ((displayMetrics.widthPixels / mColumns)/3)
//            return if(s.rem(2) == 0) s else s - 1
//        }
//
//        private fun setRowsAndColumns(size: Int) {
//            if(size.rem(2) !=0 && size != 15) {
//                Functions.showError(view.context, view.context.getString(R.string.invalid_number_seals))
//                return
//            }
//
//            when(size) {
//                6 -> {
//                    mColumns = 3
//                    mRows = 2
//                }
//                8 -> {
//                    mColumns = 4
//                    mRows = 2
//                }
//                10 -> {
//                    mColumns = 5
//                    mRows = 2
//                }
//                12 -> {
//                    mColumns = 4
//                    mRows = 3
//                }
//                15 -> {
//                    mColumns = 5
//                    mRows = 3
//                }
//                else -> { Functions.showError(view.context, view.context.getString(R.string.invalid_number_seals)) }
//            }
//        }
//
//        private fun drawSeal(controlSize: Int, stamps: Int, cardId: String, seals: List<Stamp>? ) {
//            view.row1Id.removeAllViews()
//            view.row2Id.removeAllViews()
//            view.row3Id.removeAllViews()
//            var count = 0
//            for(x in 0 until mRows) {
//                arrRow[x].visibility = View.VISIBLE
//                for(y in 0 until mColumns) {
//                    addControl(arrRow[x], controlSize, (count < stamps), cardId,
//                            if(seals != null && count < seals.size) seals[count] else null)
//                    count ++
//                }
//            }
//        }
//
//        private fun addControl(row: LinearLayout, controlSize: Int, stamped: Boolean, cardId: String, stamp: Stamp?) {
//            val ll = LinearLayout(view.context)
//            val card = CardView(view.context)
//            val img = ImageView(view.context)
//
//            val cardLp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(controlSize, controlSize)
//            cardLp.gravity = Gravity.CENTER
//            cardLp.topMargin = 10
//            cardLp.bottomMargin = 10
//            card.layoutParams = cardLp
////            card.radius = (controlSize / 2).toFloat()
//            card.radius = 24f
//            card.cardElevation = 2f
//
////            card.setContentPadding(5,5,5,5)
//
//            ll.orientation = LinearLayout.VERTICAL
//            val llLp: LinearLayout.LayoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT)
//            llLp.weight = 1.0f
//            ll.layoutParams = llLp
//
//            val imgLp = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.MATCH_PARENT)
//            img.layoutParams = imgLp
//            img.scaleType = ImageView.ScaleType.CENTER_INSIDE
////            img.setPadding(5,5,5,5)
//
//            if(stamped) {
//                card.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
//                showRoundedImage(R.drawable.ic_benefy, img)
//                card.setOnClickListener {
//                    if (stamp != null) showStampInfo(stamp)
//                    else Functions.showError(view.context, view.context.getString(R.string.error_connection))
//                }
//            } else {
//                card.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.colorPrimaryDark))
//                showRoundedImage(R.drawable.ic_not_seal, img)
//            }
//
//            card.addView(img)
//            ll.addView(card)
//            row.addView(ll)
//        }
//
//        private fun showRoundedImage(image:Int, imageView: ImageView) {
//            Picasso.get()
//                .load(image)
////                .transform(CircleTransform())
//                .into(imageView)
//        }
//
//        private fun showStampQR(stampId: String?) {
//            val intent = Intent(view.context, StampsQRInfoDialog::class.java)
//            intent.putExtra("stampId", stampId)
//            view.context.startActivity(intent)
//        }
//
//        private fun showStampCardInfo(id: String) {
//            val intent = Intent(view.context, StampCardInfoDialog::class.java)
//            intent.putExtra("planId", _currentPlan)
//            intent.putExtra("stampId", id)
//            view.context.startActivity(intent)
//        }
//
//        private fun showStampInfo(model: Stamp) {
//            val intent = Intent(view.context, StampInfoDialog::class.java)
//            intent.putExtra("model", model)
//            view.context.startActivity(intent)
//        }
//    }
//
//}