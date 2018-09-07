package me.piruin.spinney;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
  private final ItemPresenter presenter;
  private List<T> originalItems;
  private List<T> conditionedItem;
  private List<T> filteredItem;
  private ItemPresenter captionPresenter;
  private boolean isDependencyMode;

  public SpinneyAdapter(Context context, List<T> items) {
    this(context, items, Spinney.defaultItemPresenter);
  }

  public SpinneyAdapter(Context context, List<T> items, ItemPresenter presenter) {
    this(context, R.layout.spinney_item, items, presenter);
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

  public void setCaptionPresenter(@Nullable ItemPresenter captionPresenter) {
    this.captionPresenter = captionPresenter;
  }

  @Override public int getCount() {
    return filteredItem.size();
  }

  @Override public long getItemId(int position) {
    return getItem(position).hashCode();
  }

  @Override public Object getItem(int position) {
    return filteredItem.get(position);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater mInflater =
      (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    Holder holder;
    if (convertView == null) {
      convertView = mInflater.inflate(layoutId, parent, false);
      holder = new Holder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (Holder) convertView.getTag();
    }
    Object item = getItem(position);
    String label = presenter.getLabelOf(item, position);
    String caption = null;
    if (captionPresenter != null) caption = captionPresenter.getLabelOf(item, position);
    holder.bind(label, caption);

    return convertView;
  }

  void clearCondition() {
    if (isDependencyMode) {
      conditionedItem = new ArrayList<>();
      filteredItem = new ArrayList<>();
    } else {
      conditionedItem = new ArrayList<>(originalItems);
      filteredItem = new ArrayList<>(conditionedItem);
    }
    notifyDataSetChanged();
  }

  <K> void updateCondition(@NonNull K parentItem, Spinney.Condition<T, K> condition) {
    conditionedItem = new ArrayList<>();
    for (T item : originalItems) {
      if (condition.filter(parentItem, item)) conditionedItem.add(item);
    }
    filteredItem = new ArrayList<>(conditionedItem);
    notifyDataSetChanged();
  }

  @Override public Filter getFilter() {
    return new FilterByLabel();
  }

  /**
   * @param item to find position
   * @return position (index) of item on original items list
   */
  public int findPositionOf(T item) {
    return originalItems.indexOf(item);
  }

  public boolean isFilteredListContain(@Nullable T item) {
    return filteredItem.contains(item);
  }

  void setDependencyMode(boolean isDependencyMode) {
    this.isDependencyMode = isDependencyMode;
  }

  private static class Holder {
    private final TextView line1;
    private final TextView line2;

    Holder(View itemView) {
      line1 = itemView.findViewById(R.id.spinney_item_line1);
      line2 = itemView.findViewById(R.id.spinney_item_line2);
    }

    void bind(String line1, @Nullable String line2) {
      this.line1.setText(line1);
      if (line2 != null) {
        this.line2.setText(line2);
        this.line2.setVisibility(View.VISIBLE);
      } else {
        this.line2.setVisibility(View.GONE);
      }
    }
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
          if (isFound(presenter, item, query) || isFound(captionPresenter, item, query)) {
            filteredList.add(item);
          }
        }
        results.values = filteredList;
        results.count = filteredList.size();
      }
      return results;
    }

    private boolean isFound(@Nullable ItemPresenter presenter, Object object, String query) {
      return presenter != null && presenter.getLabelOf(object, 0)
        .toLowerCase(locale)
        .contains(query);
    }

    @Override protected void publishResults(CharSequence constraint, final FilterResults results) {
      filteredItem = (List<T>) results.values;
      notifyDataSetChanged();
    }
  }
}
