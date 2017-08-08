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

/**
 * Dialog with SearchView and ListView design for represent a lot items with filterable function.
 * This dialog use as default dialog for searchable mode of Spinney but it also can use separately
 * as easy as use ordinary dialog
 */
public class SearchableListDialog extends Dialog {

  private OnItemSelectedListener onItemSelectedListener;

  private final SearchView searchView;
  private final ListView listViewItems;

  public SearchableListDialog(Context context) {
    super(context);

    setContentView(R.layout.spinney_searchable_list_dialog);
    searchView = (SearchView) findViewById(R.id.spinney_search);
    searchView.setIconifiedByDefault(false);
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
      }

      @Override public boolean onQueryTextChange(String query) {
        if (TextUtils.isEmpty(query)) {
          ((Filterable) listViewItems.getAdapter()).getFilter().filter(null);
        } else {
          ((Filterable) listViewItems.getAdapter()).getFilter().filter(query);
        }
        return true;
      }
    });
    searchView.setOnCloseListener(new OnCloseListener() {
      @Override public boolean onClose() { return false; }
    });
    searchView.clearFocus();

    listViewItems = (ListView) findViewById(R.id.spinney_list);

    hindSoftKeyboard(context);
  }

  private void hindSoftKeyboard(Context context) {
    InputMethodManager mgr =
      (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    mgr.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
  }

  /**
   * @param onItemSelectedListener to callback when item was selected
   */
  public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
    this.onItemSelectedListener = onItemSelectedListener;
  }

  /**
   * Adapter of item to present on dialog
   *
   * @param adapter to show on ListView of Dialog
   */
  public void setAdapter(final SpinneyAdapter adapter) {
    listViewItems.setAdapter(adapter);
    listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object selectedItem = parent.getItemAtPosition(position);
        boolean shouldDismiss = onItemSelectedListener.onItemSelected(
          selectedItem,
          adapter.findPositionOf(selectedItem));
        if (shouldDismiss)
          dismiss();
      }
    });
  }

  /** @param hint to use as hint on at SearchView of dialog */
  public final void setHint(CharSequence hint) {
    searchView.setQueryHint(hint);
  }

  /**
   * Callback to handle when item of SearchableListDialog was selected
   *
   * @param <T> type of Item
   */
  public interface OnItemSelectedListener<T> extends Serializable {

    /**
     * @param item that have been selected
     * @param position of selected item in original list Not filtered list!
     * @return whether should dialog close itself or not
     */
    boolean onItemSelected(T item, int position);
  }
}
