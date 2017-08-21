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
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import java.util.List;

/**
 * Replacement of vanilla Spinner with Super-power
 *
 * @param <T> Type of Selectable choice to use with Spinney
 */
public class Spinney<T> extends AppCompatEditText {

  static ItemPresenter defaultItemPresenter = new ItemPresenter() {
    @Override public String getLabelOf(Object item, int position) {
      return item.toString();
    }
  };
  private final CharSequence hint;
  /** Dialog object to show selectable item of Spinney can be Searchable or normal List Dialog */
  private Dialog dialog;
  /** OnItemSelectedListener set by Library user */
  private OnItemSelectedListener<T> itemSelectedListener;
  /** Internal OnItemSelectedListener use when filterBy() was called */
  private OnItemSelectedListener<T> _itemSelectedListener;
  private ItemPresenter itemPresenter = defaultItemPresenter;
  private SpinneyAdapter<T> adapter;
  private T selectedItem;
  private boolean safeMode = defaultSafeMode;
  private static boolean defaultSafeMode = false;

  public Spinney(Context context) { this(context, null); }

  public Spinney(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.editTextStyle);
  }

  public Spinney(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    /*
      Save hint at constructor because, after this getHint() will return null
      when use Spinney as child of Support's TextInputLayout.
     */
    hint = getHint();
  }

  /**
   * <pre>
   * Enable safe mode to all spinney use in Application by default.
   * By the way, only use this in case of emergency
   * </pre>
   *
   * @param enable or disable safe mode
   */
  public static void enableSafeModeByDefault(boolean enable) {
    defaultSafeMode = enable;
  }

  /**
   * replace default global ItemPresenter this should be set at Application.onCreate()
   *
   * @param defaultItemDisplayer to present selected object on spinney view
   */
  public static void setDefaultItemPresenter(@NonNull ItemPresenter defaultItemDisplayer) {
    Spinney.defaultItemPresenter = defaultItemDisplayer;
  }

  /**
   * <pre>
   * Use this when number of items is more than user can scan by their eye.
   *
   * This method use inpurt list of item to create SpinneyAdapter if want to custom.
   * See setSearchableAdapter(SpinneyAdpter)
   * </pre>
   *
   * @param items list of item use
   */
  public final void setSearchableItem(@NonNull final List<T> items) {
    setSearchableAdapter(new SpinneyAdapter<>(getContext(), items, itemPresenter));
  }

  /**
   * Call this when build-in SpinneyAdapter not enough for you requirement
   *
   * @param adapter spinneyAdapter to use with SpinneyDialog
   */
  public final void setSearchableAdapter(@NonNull final SpinneyAdapter<T> adapter) {
    this.adapter = adapter;

    SpinneyDialog searchableListDialog = new SpinneyDialog(getContext());
    searchableListDialog.setAdapter(adapter);
    searchableListDialog.setHint(hint);
    searchableListDialog.setOnItemSelectedListener(
      new SpinneyDialog.OnItemSelectedListener<T>() {
        @Override public boolean onItemSelected(@NonNull Object item, int position) {
          whenItemSelected((T) item, position);
          return true;
        }
      });
    dialog = searchableListDialog;
  }

  private void whenItemSelected(@Nullable T item, int selectedIndex) {
    this.selectedItem = item;
    if (item == null) {
      setText(null);
      if (_itemSelectedListener != null)
        _itemSelectedListener.onItemSelected(Spinney.this, item, selectedIndex);
    } else {
      setText(itemPresenter.getLabelOf(item, selectedIndex));
      if (_itemSelectedListener != null)
        _itemSelectedListener.onItemSelected(Spinney.this, item, selectedIndex);
      if (itemSelectedListener != null)
        itemSelectedListener.onItemSelected(Spinney.this, item, selectedIndex);
    }
  }

  /**
   * enable safeMode to tell Spinney not throw exception when set selectedItem that not found in
   * adapter.
   * not recommend this in app that need consistency
   *
   * @param enable or disable saftmode
   */
  public void setSafeModeEnable(boolean enable) {
    this.safeMode = enable;
  }

  /**
   * Just set List of item on Dialog! Don't worry with Adapter Spinney will handler with it
   *
   * @param items list of item use
   */
  public final void setItems(@NonNull final List<T> items) {
    adapter = new SpinneyAdapter<>(getContext(), items, itemPresenter);
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(getHint() != null ? getHint() : hint);
    builder.setAdapter(adapter,
      new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int selectedIndex) {
          T selectedItem = (T) adapter.getItem(selectedIndex);
          whenItemSelected(selectedItem, adapter.findPositionOf(selectedItem));
        }
      });
    dialog = builder.create();
  }

  /**
   *
   * Set parent spinney and Condition to filter selectable item by selected item of parent Spinney
   * <pre>
   * {@code
   * countrySpinney.setSearchableItem(Data.country);
   * citySpinney.setItems(Data.cities);
   * citySpinney.filterBy(countrySpinney, new Spinney.Condition<DatabaseItem, DatabaseItem<>() {
   *   public boolean filter(DatabaseItem selectedCountry, DatabaseItem eachCity) {
   *     return eachCity.getParentId() == selectedCountry.getId();
   *   }});
   * }
   * </pre>
   *
   * Please note you must setSelectedItem() of parent Spinney after call filterBy()
   *
   *
   * @param parent Spinney that it selected item will affect to this spinney
   * @param filter condition to filter item on this spinney by selected item of parent
   * @param <K> type of item on parent Spinney
   *
   */
  public final <K> void filterBy(Spinney<K> parent, final Condition<T, K> filter) {
    parent._itemSelectedListener = new OnItemSelectedListener<K>() {

      @Override public void onItemSelected(Spinney parent, K parentSelectedItem, int position) {
        if (parentSelectedItem == null) {
          clearSelection();
          adapter.clearCondition();
          return;
        }
        adapter.updateCondition(parentSelectedItem, filter);
        if (!adapter.isFilteredListContain(selectedItem)) {
          clearSelection();
        }
      }
    };
    adapter.setDependencyMode(true);
    adapter.clearCondition();
  }

  /** @return selected item, this may be null */
  @Nullable public final T getSelectedItem() { return selectedItem; }

  /**
   * @return check that spinney have item to select or have nothing by filter
   */
  public final boolean isSelectable() {
    return adapter.getCount() > 0;
  }

  @Override public final boolean performClick() {
    dialog.show();
    return true;
  }

  @Override protected final void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    setFocusable(false);
    setClickable(true);
    setLongClickable(false);
  }

  public final void clearSelection() {
    whenItemSelected(null, -1);
  }

  /**
   * Must call after adapter or item have already set
   *
   * @param item to set as selected item
   * @throws IllegalArgumentException if not found item in adapter of spinney, enableSafeMode() to
   * disable this exception. safeMode is disable by default
   */
  public final void setSelectedItem(@NonNull T item) {
    if (adapter == null)
      throw new IllegalStateException("Must set adapter or item before call this");

    int positionOf = adapter.findPositionOf(item);
    if (positionOf >= 0)
      whenItemSelected(item, positionOf);
    else if (!safeMode)
      throw new IllegalArgumentException("Not found specify item");
  }

  /** @return position of selected item, -1 is nothing select */
  public final int getSelectedItemPosition() { return adapter.findPositionOf(selectedItem); }

  /**
   * This getter may help if you really need it. By the way, Use with CAUTION!
   *
   * @return SpinneyAdapter currently use by Spinney
   */
  public final SpinneyAdapter<T> getAdapter() { return adapter; }

  /**
   * ItemPresenter to use only on instance of Spinney. Spinney will use global presenter if this not
   * set
   *
   * @param itemPresenter to control how spinney and (Searchable)listDialog represent selectable
   * item  instead of global ItemPresent
   */
  public final void setItemPresenter(@NonNull ItemPresenter itemPresenter) {
    this.itemPresenter = itemPresenter;
  }

  /** @param itemSelectedListener to callback when item was selected */
  public final void setOnItemSelectedListener(
    @NonNull OnItemSelectedListener<T> itemSelectedListener) {
    this.itemSelectedListener = itemSelectedListener;
  }

  /**
   * Callback like use with vanilla Spinner
   *
   * @param <T> type of Selectable Item. must be same as type as specify at Spinney object
   */
  public interface OnItemSelectedListener<T> {

    /**
     * @param view Spinney view that fire this method
     * @param selectedItem user selected item
     * @param position at current list
     */
    void onItemSelected(Spinney view, T selectedItem, int position);
  }

  /** Control how item used with Spinney should present as String on Spinney view and Dialog */
  public interface ItemPresenter {

    /**
     * Time to parse item to present on Spinney
     *
     * @param item target item to parse
     * @param position of item when it was select
     * @return respresent String of item
     */
    String getLabelOf(Object item, int position);
  }

  /**
   * Injectable condition to control whether item should present on  list of Spinney or not!
   *
   * @param <T> Type of item to check
   * @param <K> Type of value to may use as condition to present T
   */
  public interface Condition<T, K> {

    /**
     * @param value may use as Condition to filter item
     * @param item to check whether it should present or not
     * @return true if item should present, false otherwise
     */
    boolean filter(K value, T item);
  }
}
