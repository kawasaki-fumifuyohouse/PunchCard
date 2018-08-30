package com.fumifuyohouse.punchcard.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.fumifuyohouse.punchcard.R
import com.fumifuyohouse.punchcard.model.PunchCard
import io.realm.Realm
import java.text.SimpleDateFormat

/**
 * Created by kawasaki on 2018/04/25.
 */
class ListAdapter(realm: Realm, listener: ListItemListener, startMillis: Long, endMillis: Long) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd (E)")
    private val timeFormat = SimpleDateFormat("HH:mm")

    private val mListener = listener
    private var mStartMillis = startMillis
    private var mEndMillis = endMillis

    private val mData: MutableList<PunchCard> = mutableListOf()
    private var mPreDrawerListener: ViewTreeObserver.OnPreDrawListener? = null
    private val mRealm: Realm = realm

    interface ListItemListener {
        fun editItem(id: String)
    }

    init {
        val footer = PunchCard()
        footer.isFooter = true
        mData.add(footer)
    }

    companion object {
        val VIEW_TYPE_ITEM = 0
        val VIEW_TYPE_FOOTER = 1
        val TAG = "ListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_FOOTER) {
            return FooterViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_footer, parent, false))
        } else {
            return ItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is FooterViewHolder) {
            val footerViewHolder = holder
            if (mPreDrawerListener != null) {
                holder.progressBar.viewTreeObserver.removeOnPreDrawListener(mPreDrawerListener)
                mPreDrawerListener = null
            }
            mPreDrawerListener = ViewTreeObserver.OnPreDrawListener {
                footerViewHolder.progressBar.viewTreeObserver.removeOnPreDrawListener(mPreDrawerListener)
                mPreDrawerListener = null
                dataLoad()
                true
            }
            footerViewHolder.progressBar.viewTreeObserver.addOnPreDrawListener(mPreDrawerListener)

        } else if (holder is ItemViewHolder) {
            val item: PunchCard = mData.get(position) as PunchCard
            if (!item.isFooter) {
                holder.textInDate.text = dateFormat.format(item.in_time)
                holder.textInTime.text = timeFormat.format(item.in_time)
                if (item.out_time != 0L) {
                    holder.textOutDate.text = dateFormat.format(item.out_time)
                    holder.textOutTime.text = timeFormat.format(item.out_time)
                } else {
                    holder.textOutDate.text = ""
                    holder.textOutTime.text = ""
                }

                holder.buttonDelete.setOnClickListener({ deleteItem(position) })
                holder.buttonEdit.setOnClickListener({ mListener.editItem(item.full_date) })
            }

        } else {

        }
    }

    override fun getItemViewType(position: Int): Int {
        val record = mData.get(position)
        if (record.isFooter) {
            return VIEW_TYPE_FOOTER
        } else {
            return VIEW_TYPE_ITEM
        }
    }

    private fun dataLoad() {
        dataLoad(mStartMillis, mEndMillis)
    }

    fun dataLoad(startMillis: Long, endMillis: Long) {
        val MAX = 10
        // TODO ページング
        val allData: Collection<PunchCard>? = mRealm.where(PunchCard::class.java)
                ?.between("in_time", startMillis, endMillis)
                ?.sort("in_time")?.findAll()

        if (mData.size != 0) {
            val record = mData.get(mData.size - 1)
            if (record.isFooter) {
                // footer削除
                mData.remove(record)
            }
        }
        if (allData != null) {
            mData.clear()
            mData.addAll(allData)
        }
        notifyDataSetChanged()
    }

    fun deleteItems(startMillis: Long, endMillis: Long) {
        mRealm.executeTransactionAsync({
            val allData = it.where(PunchCard::class.java)
                    .between("in_time", startMillis, endMillis)
                    .sort("in_time")?.findAll()
            allData?.deleteAllFromRealm()
        }, {
            Log.d(TAG, "DELETE成功")
            dataLoad(startMillis, endMillis)
        }, {
            Log.d(TAG, "DELETE失敗")
            it.printStackTrace()
            dataLoad(startMillis, endMillis)
        })
    }

    private fun deleteItem(position: Int) {
        val record = (mData.get(position) as PunchCard).copy()

        mRealm.executeTransactionAsync({ bgRealm ->
            val result = bgRealm.where(PunchCard::class.java).equalTo("full_date", record.full_date).findAll()
            if (result.size == 0) {
                throw RuntimeException("該当レコードがありません")

            } else {
                result.deleteAllFromRealm()
            }

        }, {
            Log.d(TAG, "DELETE成功")
            mData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mData.size)

        }, {
            Log.d(TAG, "DELETE失敗")
            it.printStackTrace()
        })
    }

    /**
     * Item用ViewHolder
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textInDate: TextView = itemView.findViewById(R.id.value_in_date)
        val textInTime: TextView = itemView.findViewById(R.id.value_in_time)
        val textOutDate: TextView = itemView.findViewById(R.id.value_out_date)
        val textOutTime: TextView = itemView.findViewById(R.id.value_out_time)
        val buttonDelete: ImageView = itemView.findViewById(R.id.button_delete)
        val buttonEdit: ImageView = itemView.findViewById(R.id.button_edit)
    }

    /**
     * footer用ViewHolder
     */
    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}