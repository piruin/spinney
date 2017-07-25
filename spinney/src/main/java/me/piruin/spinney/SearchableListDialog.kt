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

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.Filterable
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.SearchView.OnCloseListener
import android.widget.SearchView.OnQueryTextListener

class SearchableListDialog(context: Context) : Dialog(context), OnQueryTextListener, OnCloseListener {

  lateinit var onItemSelectListener: OnItemSelectListener
  private var listViewItems: ListView? = null
  private var searchView: SearchView? = null

  init {
    setContentView(R.layout.searchable_list_dialog)
    setupSearchView()

    hindSoftKeyboard(context)
  }

  private fun hindSoftKeyboard(context: Context) {
    val mgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    mgr.hideSoftInputFromWindow(searchView!!.windowToken, 0)
  }

  private fun setupSearchView() {
    searchView = findViewById(R.id.spinney_search) as SearchView
    searchView!!.setIconifiedByDefault(false)
    searchView!!.setOnQueryTextListener(this)
    searchView!!.setOnCloseListener(this)
    searchView!!.clearFocus()
  }

  override fun onClose(): Boolean {
    return false
  }

  override fun onQueryTextSubmit(query: String): Boolean {
    searchView!!.clearFocus()
    return true
  }

  override fun onQueryTextChange(query: String): Boolean {
    if (TextUtils.isEmpty(query)) {
      (listViewItems!!.adapter as Filterable).filter.filter(null)
    } else {
      (listViewItems!!.adapter as Filterable).filter.filter(query)
    }
    return true
  }

  fun <T> setAdapter(adapter: T)  where T : ListAdapter, T : Filterable {
    listViewItems = findViewById(R.id.spinney_list) as ListView
    listViewItems!!.adapter = adapter
    listViewItems!!.isTextFilterEnabled = true
    listViewItems!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
      onItemSelectListener!!.onItemSelected(parent.getItemAtPosition(position), position)
      dismiss()
    }
  }

  interface OnItemSelectListener {
    fun onItemSelected(item: Any, position: Int)
  }
}
