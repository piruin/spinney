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

package me.piruin.spinney.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.Locale;
import me.piruin.spinney.Spinney;

public class SampleActivity extends AppCompatActivity {

  @BindView(R.id.spinney_searchable) Spinney<String> searchableDept;
  @BindView(R.id.spinney_normal) Spinney<String> normalDept;
  @BindView(R.id.spinney_country) Spinney<DatabaseItem> countrySpinney;
  @BindView(R.id.spinney_city) Spinney<DatabaseItem> citySpinney;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);
    ButterKnife.bind(this);

    useSimpleListOfString();
    useListOfDatabaseItemWithFilterByFeature();
  }

  private void useSimpleListOfString() {
    searchableDept.setSearchableItem(Data.department);
    normalDept.setItems(Data.department);
  }

  public void useListOfDatabaseItemWithFilterByFeature() {
    countrySpinney.setSearchableItem(Data.country);
    countrySpinney.setOnItemSelectedListener(new Spinney.OnItemSelectedListener<DatabaseItem>() {
      @Override public void onItemSelected(Spinney view, DatabaseItem selectedItem, int position) {
        citySpinney.clearSelection();
      }
    });

    citySpinney.setItems(Data.cities);
    citySpinney.filterBy(countrySpinney, new Spinney.Condition<DatabaseItem, DatabaseItem>() {
      @Override public boolean filter(DatabaseItem parentItem, DatabaseItem item) {
        return item.getParentId() == parentItem.getId();
      }
    });
    citySpinney.setItemPresenter(new Spinney.ItemPresenter() { //Custom item presenter add Spinney
      @Override public String getLabelOf(Object item, int position) {
        return String.format(Locale.getDefault(), "%d.%s - %s", position,
          ((DatabaseItem) item).getName(),
          countrySpinney.getSelectedItem().getName());
      }
    });
    //setSelectedItem() of parent Spinney must call after filterBy()
    countrySpinney.setSelectedItem(new DatabaseItem(1, "THAILAND"));
  }
}
