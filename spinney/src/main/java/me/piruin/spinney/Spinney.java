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

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import java.util.List;

public class Spinney<T> extends AppCompatEditText {

  public static final int MODE_NORMAL = 1;
  public static final int MODE_SEARCHABLE = 2;

  private SearchableListDialog searchableListDialog;

  private int mode = MODE_NORMAL;
  private AlertDialog alertDialog;
  private OnItemSelectedListener<T> itemSelectedListener;

  public Spinney(Context context) {
    super(context);
  }

  public Spinney(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public Spinney(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setSearchableAdapter(final ArrayAdapter<T> adapter) {
    mode = MODE_SEARCHABLE;

    searchableListDialog = new SearchableListDialog(getContext());
    searchableListDialog.setAdapter(adapter);
    searchableListDialog.setOnSearchableItemClickListener(
      new SearchableListDialog.OnSearchItemClick() {

        @Override public void onSearchableItemClicked(Object item, int position) {
          if (item instanceof String) {
            setText((String)item);
          } else {
            setText(item.toString());
          }

          if (itemSelectedListener != null)
            itemSelectedListener.onItemSelected(Spinney.this, (T)item, position);
        }
      });
  }

  @Override public boolean performClick() {
    if (mode == MODE_SEARCHABLE) {
      searchableListDialog.show();
      return true;
    } else if (mode == MODE_NORMAL) {
      alertDialog.show();
      return true;
    }
    return false;
  }

  public void setItems(final List<String> items) {
    CharSequence[] charItems = new CharSequence[items.size()];
    int i = 0;
    for (String item : items) {
      charItems[i++] = item;
    }
    setItems(charItems);
  }

  public void setItems(final CharSequence[] adapter) {
    mode = MODE_NORMAL;
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(getHint());
    builder.setItems(adapter, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialogInterface, int selectedIndex) {
        setText(adapter[selectedIndex]);
      }
    });
    builder.setPositiveButton("close", null);
    alertDialog = builder.create();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    setFocusable(false);
    setClickable(true);
  }

  public void setOnItemSelectedListener(OnItemSelectedListener<T> itemSelectedListener) {
    this.itemSelectedListener = itemSelectedListener;
  }

  public interface OnItemSelectedListener<T> {

    void onItemSelected(Spinney view, T selectedItem, int position);
  }
}
