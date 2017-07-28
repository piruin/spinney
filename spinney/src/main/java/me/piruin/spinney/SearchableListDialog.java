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

package me.piruin.spinney;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import java.io.Serializable;

public class SearchableListDialog extends Dialog implements OnQueryTextListener {
  private ListView listViewItems;

  private OnItemSelectedListener onItemSelectedListener;

  private SearchView searchView;

  public SearchableListDialog(Context context) {
    super(context);

    setContentView(R.layout.searchable_list_dialog);
    setupSearchView();

    hindSoftKeyboard(context);
  }

  private void hindSoftKeyboard(Context context) {
    InputMethodManager mgr =
      (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
    mgr.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
  }

  private void setupSearchView() {
    searchView = (SearchView)findViewById(R.id.spinney_search);
    searchView.setIconifiedByDefault(false);
    searchView.setOnQueryTextListener(this);
    searchView.setOnCloseListener(new OnCloseListener() {
      @Override public boolean onClose() { return false; }
    });
    searchView.clearFocus();
  }

  void setOnItemSelectedListener(OnItemSelectedListener searchableItem) {
    this.onItemSelectedListener = searchableItem;
  }

  @Override public boolean onQueryTextSubmit(String query) {
    searchView.clearFocus();
    return true;
  }

  @Override public boolean onQueryTextChange(String query) {
    if (TextUtils.isEmpty(query)) {
      ((Filterable)listViewItems.getAdapter()).getFilter().filter(null);
    } else {
      ((Filterable)listViewItems.getAdapter()).getFilter().filter(query);
    }
    return true;
  }

  public void setAdapter(SpinneyAdapter adapter) {
    listViewItems = (ListView)findViewById(R.id.spinney_list);
    listViewItems.setAdapter(adapter);
    listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean shouldDismiss = onItemSelectedListener.onItemSelected(parent.getItemAtPosition(position), position);
        if (shouldDismiss)
          dismiss();
      }
    });
  }

  public interface OnItemSelectedListener<T> extends Serializable {

    /**
     * @param item that have been selected
     * @param position of selected item on list zero-base
     * @return should dialog close itself or not
     */
    boolean onItemSelected(T item, int position);
  }
}
