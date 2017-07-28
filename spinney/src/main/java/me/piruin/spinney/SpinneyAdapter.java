package me.piruin.spinney;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.piruin.spinney.Spinney.ItemPresenter;

public final class SpinneyAdapter<T> extends BaseAdapter implements Filterable {

  private final Context context;
  private final int layoutId;
  private List<T> originalItems;
  private List<T> conditionedItem;
  private List<T> filteredItem;
  private final ItemPresenter presenter;

  public SpinneyAdapter(Context context, List<T> items) {
    this(context, items, Spinney.defaultItemPresenter);
  }

  public SpinneyAdapter(Context context, List<T> items, ItemPresenter presenter) {
    this(context, android.R.layout.simple_list_item_1, items, presenter);
  }

  public SpinneyAdapter(Context context, @LayoutRes int layoutId, List<T> items,
    ItemPresenter presenter) {
    super();
    this.context = context;
    this.layoutId = layoutId;
    this.originalItems = items;
    this.conditionedItem = new ArrayList<>(items);
    this.filteredItem = new ArrayList<>(items);
    this.presenter = presenter;
  }

  @Override public int getCount() {
    return filteredItem.size();
  }

  @Override public Object getItem(int position) {
    System.out.println("selected item is " + filteredItem.get(position));
    return filteredItem.get(position);
  }

  @Override public long getItemId(int position) {
    return getItem(position).hashCode();
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater mInflater =
      (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (convertView == null) {
      convertView = mInflater.inflate(layoutId, parent, false);
    }
    if (convertView instanceof TextView) {
      ((TextView) convertView).setText(presenter.getLabelOf(getItem(position), position));
    }
    return convertView;
  }

  public <K> void updateCondition(K value, Spinney.Condition<T, K> condition) {
    conditionedItem = new ArrayList<>();
    for (T item : originalItems) {
      if (condition.filter(value, item)) conditionedItem.add(item);
    }
    filteredItem = new ArrayList<>(conditionedItem);
    notifyDataSetChanged();
  }

  @Override public Filter getFilter() {
    return new FilterByLabel();
  }

  /**
   *
   * @param item
   * @return
   */
  public int findPositionOf(T item) {
    return originalItems.indexOf(item);
  }

  private class FilterByLabel extends Filter {

    private final Locale locale = Locale.getDefault();

    @Override protected FilterResults performFiltering(final CharSequence constraint) {
      FilterResults results = new FilterResults();
      if (TextUtils.isEmpty(constraint)) {
        results.values = conditionedItem;
        results.count = conditionedItem.size();
      } else {
        List<T> filteredList = new ArrayList<>();
        String query = constraint.toString().toLowerCase(locale);
        for (T item : conditionedItem) {
          if (presenter.getLabelOf(item, 0).toLowerCase(locale).contains(query))
            filteredList.add(item);
        }
        results.values = filteredList;
        results.count = filteredList.size();
      }
      return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, final FilterResults results) {
      filteredItem = (List<T>) results.values;
      notifyDataSetChanged();
    }
  }
}
