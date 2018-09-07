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
import butterknife.OnClick;
import java.util.Locale;
import me.piruin.spinney.Spinney;

public class SampleActivity extends AppCompatActivity {

  @BindView(R.id.spinney_searchable) Spinney<String> searchableDept;
  @BindView(R.id.spinney_normal) Spinney<String> normalDept;
  @BindView(R.id.spinney_country) Spinney<DatabaseItem> countrySpinney;
  @BindView(R.id.spinney_region)
  Spinney<DatabaseItem> regionSpinney;
  @BindView(R.id.spinney_city) Spinney<DatabaseItem> citySpinney;
  @BindView(R.id.spinney_district) Spinney<DatabaseItem> districtSpinney;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);
    ButterKnife.bind(this);

    useSimpleListOfString();
    useListOfDatabaseItemWithFilterByFeature();
    playWithSafeMode();
  }

  private void useSimpleListOfString() {
    searchableDept.setSearchableItem(Data.department);
    searchableDept.setOnItemSelectedListener(new Spinney.OnItemSelectedListener<String>() {
      @Override public void onItemSelected(Spinney view, String selectedItem, int position) {
        normalDept.clearSelection();
      }
    });
    normalDept.setItems(Data.department);
  }

  public void useListOfDatabaseItemWithFilterByFeature() {
    countrySpinney.setSearchableItem(Data.country);
    citySpinney.setItems(Data.cities);
    districtSpinney.setItems(Data.districts);
    regionSpinney.setItems(Data.regions);

    regionSpinney.filterBy(countrySpinney, new Spinney.Condition<DatabaseItem, DatabaseItem>() {
      @Override
      public boolean filter(DatabaseItem selectedCountry, DatabaseItem eachRegion) {
        return eachRegion.getParentId() == selectedCountry.getId();
      }
    });
    citySpinney.filterBy(countrySpinney, new Spinney.Condition<DatabaseItem, DatabaseItem>() {
      @Override public boolean filter(DatabaseItem selectedCountry, DatabaseItem eachCity) {
        return eachCity.getParentId() == selectedCountry.getId();
      }
    });
    districtSpinney.filterBy(citySpinney, new Spinney.Condition<DatabaseItem, DatabaseItem>() {
      @Override public boolean filter(DatabaseItem selectedCity, DatabaseItem eachDistrict) {
        return eachDistrict.getParentId() == selectedCity.getId();
      }
    });

    citySpinney.setItemPresenter(
      new Spinney.ItemPresenter<DatabaseItem>() { //Custom item presenter add Spinney
        @Override public String getLabelOf(DatabaseItem item, int position) {
          return String.format(Locale.getDefault(), "%d.%s - %s", position, item.getName(),
                               countrySpinney.getSelectedItem().getName());
      }
    });

    //setSelectedItem() of parent Spinney must call after filterBy()
    countrySpinney.setSelectedItem(new DatabaseItem(1, "THAILAND"));
  }

  private void playWithSafeMode() {
    try {
      //below will cause IllegalArgumentException cause not enableSafeMode
      countrySpinney.setSelectedItem(new DatabaseItem(2000, "METROPOLIS"));
    } catch (IllegalArgumentException ignore) {
    }

    citySpinney.setSafeModeEnable(true);
    citySpinney.setSelectedItem(new DatabaseItem(60, "GOTHAM"));
  }

  @OnClick(R.id.clear) void onClearClick() {
    countrySpinney.clearSelection();
  }
}
