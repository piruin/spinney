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

public class Spinney<T> extends AppCompatEditText {

  private static ItemPresenter defaultItemPresenter = new ItemPresenter() {
    @Override public String getLabelOf(Object item, int position) {
      return item.toString();
    }
  };

  private Dialog dialog;
  private OnItemSelectedListener<T> itemSelectedListener;
  private ItemPresenter itemPresenter = defaultItemPresenter;
  private SpinneyAdapter<T> adapter;

  public Spinney(Context context) { super(context); }

  public Spinney(Context context, AttributeSet attrs) { super(context, attrs); }

  public Spinney(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public static void setDefaultItemPresenter(@NonNull ItemPresenter defaultItemDisplayer) {
    Spinney.defaultItemPresenter = defaultItemDisplayer;
  }

  public void setSearchableAdapter(@NonNull final SpinneyAdapter<T> adapter) {
    this.adapter = adapter;

    SearchableListDialog searchableListDialog = new SearchableListDialog(getContext());
    searchableListDialog.setAdapter(adapter);
    searchableListDialog.setOnItemSelectedListener(
      new SearchableListDialog.OnItemSelectedListener<T>() {

        @Override
        public boolean onItemSelected(Object item, int position) {
          setText(itemPresenter.getLabelOf(item, position));

          if (itemSelectedListener != null)
            itemSelectedListener.onItemSelected(Spinney.this, (T) item, position);
          return true;
        }
      });
    dialog = searchableListDialog;
  }

  public void setItemPresenter(@NonNull ItemPresenter itemPresenter) {
    this.itemPresenter = itemPresenter;
  }

  @Override public boolean performClick() {
    dialog.show();
    return true;
  }

  public void setItems(@NonNull final List<T> items) {
    adapter = new SpinneyAdapter<>(getContext(), items);
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(getHint());
    builder.setAdapter(adapter,
      new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int selectedIndex) {
          T item = (T) adapter.getItem(selectedIndex);
          setText(itemPresenter.getLabelOf(item, selectedIndex));

          if (itemSelectedListener != null)
            itemSelectedListener.onItemSelected(Spinney.this, item, selectedIndex);
        }
      });
    builder.setPositiveButton("close", null);
    dialog = builder.create();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    setFocusable(false);
    setClickable(true);
  }

  public void setOnItemSelectedListener(@NonNull OnItemSelectedListener<T> itemSelectedListener) {
    this.itemSelectedListener = itemSelectedListener;
  }

  public <K> void filterBy(Spinney<K> parent, final Condition<T, K> filter) {
    parent.setOnItemSelectedListener(new OnItemSelectedListener<K>() {
      @Override public void onItemSelected(Spinney view, K selectedItem, int position) {
        adapter.updateCondition(selectedItem, filter);
      }
    });
  }

  public interface OnItemSelectedListener<T> {
    void onItemSelected(Spinney view, T selectedItem, int position);
  }

  public interface ItemPresenter {
    String getLabelOf(Object item, int position);
  }

  public interface Condition<T, K> {
    boolean filter(K parent, T item);
  }
}
