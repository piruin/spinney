package me.piruin.spinney;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import me.piruin.spinney.Spinney.ItemPresenter;

public final class SpinneyAdapter<T> extends BaseAdapter implements Filterable {

  public static final ItemPresenter DEFAULT_PRESENTER = new ItemPresenter() {
    @Override public String getLabelOf(Object item, int position) {
      return item.toString();
    }
  };
  private final Context context;
  private final int layoutId;
  private List<T> items;
  private List<T> conditionedItem;
  private final ItemPresenter presenter;
  private List<T> filteredItem;

  public SpinneyAdapter(Context context, List<T> items) {
    this(context, items, DEFAULT_PRESENTER);
  }

  public SpinneyAdapter(Context context, List<T> items, ItemPresenter presenter) {
    this(context, android.R.layout.simple_list_item_1, items, presenter);
  }

  public SpinneyAdapter(Context context, @LayoutRes int layoutId, List<T> items,
    ItemPresenter presenter) {
    super();
    this.context = context;
    this.layoutId = layoutId;
    this.items = items;
    this.conditionedItem = new ArrayList<>(items);
    this.filteredItem = new ArrayList<>(items);
    this.presenter = presenter;
  }

  @Override public int getCount() {
    return filteredItem.size();
  }

  @Override public Object getItem(int position) {
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
    for (T item : items)
      if (condition.filter(value, item)) conditionedItem.add(item);

    notifyDataSetChanged();
  }

  @Override public Filter getFilter() {
    return new Filter() {
      @Override protected FilterResults performFiltering(final CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint == null || constraint.length() == 0) {
          results.values = conditionedItem;
          results.count = conditionedItem.size();
        } else {
          List<T> nlist = new ArrayList<>();
          for (T item : conditionedItem) {
            if (item.toString().contains(constraint)) nlist.add(item);
          }
          results.values = nlist;
          results.count = nlist.size();
        }
        return results;
      }

      @Override
      protected void publishResults(CharSequence constraint, final FilterResults results) {
        if (results.count == 0)
          notifyDataSetInvalidated();
        else {
          filteredItem = (List<T>) results.values;
          notifyDataSetChanged();
        }
      }
    };
  }
}
