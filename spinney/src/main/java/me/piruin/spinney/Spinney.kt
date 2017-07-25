/*
 * Copyright 2017 Piruin Panichphol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package me.piruin.spinney

import android.content.Context
import android.graphics.Canvas
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.widget.Filterable
import android.widget.ListAdapter

class Spinney<T> : AppCompatEditText {

  private var searchableListDialog: SearchableListDialog? = null
  private var mode = MODE_NORMAL
  private var alertDialog: AlertDialog? = null
  private var itemPresenter = defaultItemPresenter
  var itemSelectedListener: OnItemSelectedListener<T>? = null

  constructor(context: Context) : this(context, null)

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  fun <K> setSearchableAdapter(adapter: K) where K : ListAdapter, K : Filterable {
    mode = MODE_SEARCHABLE

    searchableListDialog = SearchableListDialog(context)
    searchableListDialog!!.setAdapter(adapter)
    searchableListDialog!!.onItemSelectListener = object : SearchableListDialog.OnItemSelectListener {
      override fun onItemSelected(item: Any, position: Int) {
        setText(itemPresenter.getLabelOf(item, position))

        if (itemSelectedListener != null)
          itemSelectedListener!!.onItemSelected(this@Spinney, item as T, position)
      }
    }
  }

  fun setItemPresenter(itemPresenter: ItemPresenter) {
    this.itemPresenter = itemPresenter
  }

  override fun performClick(): Boolean {
    if (mode == MODE_SEARCHABLE) {
      searchableListDialog!!.show()
      return true
    } else if (mode == MODE_NORMAL) {
      alertDialog!!.show()
      return true
    }
    return false
  }


  fun setItems(adapter: Array<String>) {
    mode = MODE_NORMAL
    val builder = AlertDialog.Builder(context)
    builder.setTitle(hint)
    builder.setItems(adapter) { dialogInterface, selectedIndex ->
      val item = adapter[selectedIndex]
      setText(itemPresenter.getLabelOf(item, selectedIndex))

      if (itemSelectedListener != null)
        itemSelectedListener!!.onItemSelected(this@Spinney, item as T, selectedIndex)
    }
    builder.setPositiveButton("close", null)
    alertDialog = builder.create()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    isFocusable = false
    isClickable = true
  }

  fun setOnItemSelectedListener(itemSelectedListener: OnItemSelectedListener<T>) {
    this.itemSelectedListener = itemSelectedListener
  }

  interface OnItemSelectedListener<in T> {

    fun onItemSelected(view: Spinney<*>, selectedItem: T, position: Int)
  }

  interface ItemPresenter {
    fun getLabelOf(item: Any, position: Int): String
  }

  companion object {

    val MODE_NORMAL = 1
    val MODE_SEARCHABLE = 2

    @JvmStatic var defaultItemPresenter: ItemPresenter = object : ItemPresenter {
      override fun getLabelOf(item: Any, position: Int): String {
        if (item is String) {
          return item
        } else {
          return item.toString()
        }
      }
    }


  }
}
