//package www.qisu666.com.adapter
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.ImageView
//import www.qisu666.com.R
//import www.qisu666.com.utils.logI
//
///**
// * Created by wujiancheng on 2017/10/10.
// * 发票号码adapter
// */
//class InvoiceNosAdapter(val context: Context, var invoiceNos: ArrayList<String>) : RecyclerView.Adapter<InvoiceNosAdapter.InvoiceNosViewHolder>() {
//    private val MAX_SIZE = 20
//    override fun onBindViewHolder(viewHolder: InvoiceNosViewHolder?, position: Int) {
//        logI("invoiceNos onBindViewHolder==" + invoiceNos)
//        if (position in invoiceNos.indices) {
//            val invoiceNo = invoiceNos[position]
//            viewHolder?.etInvoiceNo?.setText(invoiceNo)
//            viewHolder?.etInvoiceNo?.tag = position
//            if (position == 0) {
//                viewHolder?.ivInvoiceEdit?.setImageResource(R.mipmap.edit_add)
//                //超过最大数目，隐藏添加按钮
//                if (invoiceNos.size >= MAX_SIZE) {
//                    viewHolder?.ivInvoiceEdit?.visibility = View.GONE
//                } else {
//                    viewHolder?.ivInvoiceEdit?.visibility = View.VISIBLE
//                }
//            } else {
//                viewHolder?.ivInvoiceEdit?.setImageResource(R.mipmap.edit_delete)
//            }
//
//            //最后一行分割线隐藏
//            if (position == invoiceNos.size - 1) {
//                viewHolder?.divideLine?.visibility = View.GONE
//            } else {
//                viewHolder?.divideLine?.visibility = View.VISIBLE
//            }
//
//            viewHolder?.ivInvoiceEdit?.setOnClickListener {
//                if (position == 0 && invoiceNos.size < MAX_SIZE) {
//                    invoiceNos.add("")
//                } else {
//                    if (position in invoiceNos.indices) {
//                        invoiceNos.removeAt(position)
//                    }
//                }
//                notifyDataSetChanged()
//            }
//
//            viewHolder?.etInvoiceNo?.addTextChangedListener(
//                    object : TextWatcher {
//                        override fun afterTextChanged(s: Editable?) {
//                            logI("position222afterTextChanged==")
//                        }
//
//                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                            logI("position222beforeTextChanged==")
//                        }
//
//                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                            val pos: Int = viewHolder.etInvoiceNo.tag as Int
//                            logI("position222onTextChanged==" + pos)
//                            if (pos in invoiceNos.indices) {
//                                invoiceNos[pos] = s.toString()
//                                logI("invoiceNos222==" + invoiceNos)
//                            }
//                        }
//                    })
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): InvoiceNosViewHolder {
//        val inflater = LayoutInflater.from(context)
//        val view = inflater.inflate(R.layout.listitem_invoice_nos, parent, false)
//        return InvoiceNosViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = invoiceNos.size
//
//    inner class InvoiceNosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val etInvoiceNo = itemView.findViewById(R.id.etInvoiceNo) as EditText
//        val ivInvoiceEdit = itemView.findViewById(R.id.ivInvoiceEdit) as ImageView
//        val divideLine: View = itemView.findViewById(R.id.divideLine)
//    }
//}