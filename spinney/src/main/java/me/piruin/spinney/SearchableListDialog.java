package me.piruin.spinney;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import java.io.Serializable;

public class SearchableListDialog extends Dialog implements OnQueryTextListener, OnCloseListener {
  private ListView listViewItems;

  private OnSearchItemClick searchableItem;

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
    searchView.setOnCloseListener(this);
    searchView.clearFocus();
  }

  void setOnSearchableItemClickListener(OnSearchItemClick searchableItem) {
    this.searchableItem = searchableItem;
  }

  @Override public boolean onClose() {
    return false;
  }

  @Override public boolean onQueryTextSubmit(String query) {
    searchView.clearFocus();
    return true;
  }

  @Override public boolean onQueryTextChange(String query) {
    if (TextUtils.isEmpty(query)) {
      ((ArrayAdapter)listViewItems.getAdapter()).getFilter().filter(null);
    } else {
      ((ArrayAdapter)listViewItems.getAdapter()).getFilter().filter(query);
    }
    return true;
  }

  public void setAdapter(ListAdapter adapter) {
    listViewItems = (ListView)findViewById(R.id.spinney_list);
    listViewItems.setAdapter(adapter);
    listViewItems.setTextFilterEnabled(true);
    listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        searchableItem.onSearchableItemClicked(parent.getItemAtPosition(position), position);
        dismiss();
      }
    });
  }

  public interface OnSearchItemClick<T> extends Serializable {
    void onSearchableItemClicked(T item, int position);
  }

  public interface OnSearchTextChanged {
    void onSearchTextChanged(String strText);
  }
}
