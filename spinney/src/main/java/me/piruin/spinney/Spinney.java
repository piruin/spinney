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

  /**
   * Dialog object to show selectable item of Spinney can be Searchable or normal List Dialog
   */
  private Dialog dialog;

  /**
   * OnItemSelectedListener set by Library user
   */
  private OnItemSelectedListener<T> itemSelectedListener;

  /**
   * Internal OnItemSelectedListener use when filterBy() was called
   */
  private OnItemSelectedListener<T> _itemSelectedListener;
  private ItemPresenter itemPresenter = defaultItemPresenter;
  private SpinneyAdapter<T> adapter;
  private final CharSequence hint;

  public Spinney(Context context) { this(context, null); }

  public Spinney(Context context, AttributeSet attrs) { this(context, attrs, 0); }

  public Spinney(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    /*
      Save hint at constructor because, after this getHint() will return null
      when use Spinney as child of Support's TextInputLayout.
     */
    hint = getHint();
  }

  /**
   * Use this when number of items is large more than user can scan by their eye
   * SpinneyAdapter is ready to use, Just new instance and set require parameter
   *
   * @param adapter spinneyAdapter to use with SearchableListDialog
   */
  public void setSearchableAdapter(@NonNull final SpinneyAdapter<T> adapter) {
    this.adapter = adapter;

    SearchableListDialog searchableListDialog = new SearchableListDialog(getContext());
    searchableListDialog.setAdapter(adapter);
    searchableListDialog.setHint(hint);
    searchableListDialog.setOnItemSelectedListener(
      new SearchableListDialog.OnItemSelectedListener<T>() {

        @Override
        public boolean onItemSelected(Object item, int position) {
          Spinney.this.onItemSelected((T) item, position);
          return true;
        }
      });
    dialog = searchableListDialog;
  }

  public void setSearchableItem(@NonNull final List<T> items) {
    setSearchableAdapter(new SpinneyAdapter<T>(getContext(), items, itemPresenter));
  }

  /**
   * Lazy mode of Spinney just set List of item! no more adapter require
   *
   * @param items list of item use
   */
  public void setItems(@NonNull final List<T> items) {
    adapter = new SpinneyAdapter<>(getContext(), items, itemPresenter);
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(hint);
    builder.setAdapter(adapter,
      new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int selectedIndex) {
          onItemSelected((T) adapter.getItem(selectedIndex), selectedIndex);
        }
      });
    builder.setPositiveButton("close", null);
    dialog = builder.create();
  }

  private void onItemSelected(T item, int selectedIndex) {
    setText(itemPresenter.getLabelOf(item, selectedIndex));


    if (_itemSelectedListener != null)
      _itemSelectedListener.onItemSelected(Spinney.this, item, selectedIndex);
    if (itemSelectedListener != null)
      itemSelectedListener.onItemSelected(Spinney.this, item, selectedIndex);
  }

  public final <K> void filterBy(Spinney<K> parent, final Condition<T, K> filter) {
    parent._itemSelectedListener = new OnItemSelectedListener<K>() {
      @Override public void onItemSelected(Spinney view, K selectedItem, int position) {
        adapter.updateCondition(selectedItem, filter);
      }
    };
  }

  @Override public final boolean performClick() {
    dialog.show();
    return true;
  }

  @Override protected final void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    setFocusable(false);
    setClickable(true);
  }

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

  /**
   * @param itemSelectedListener to callback when item was selected
   */
  public final void setOnItemSelectedListener(
    @NonNull OnItemSelectedListener<T> itemSelectedListener) {
    this.itemSelectedListener = itemSelectedListener;
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

  /**
   * Control how item used with Spinney should present as String on Spinney view and Dialog
   */
  public interface ItemPresenter {
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
