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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.Arrays;
import java.util.List;
import me.piruin.spinney.Spinney;
import me.piruin.spinney.SpinneyAdapter;

public class SampleActivity extends AppCompatActivity {

  @BindView(R.id.spinney_searchable) Spinney<String> searchableDept;
  @BindView(R.id.spinney_normal) Spinney<String> normalDept;

  @BindView(R.id.spinney_country) Spinney<DatabaseItem> countrySpinney;
  @BindView(R.id.spinney_cities) Spinney<DatabaseItem> citiesSpinney;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);
    ButterKnife.bind(this);

    simpleString();

    specifyType();
  }

  private void simpleString() {
    List<String> department = Arrays.asList("NSTDA", "NECTEC", "BIOTEC", "MTEC", "NANOTEC");

    searchableDept.setSearchableAdapter(
      new SpinneyAdapter<>(this, department));

    normalDept.setItems(department);
    normalDept.setItemPresenter(new Spinney.ItemPresenter() {
      @Override public String getLabelOf(Object item, int position) {
        return item.toString();
      }
    });
  }

  public void specifyType() {
    List<DatabaseItem> country = Arrays.asList(
      new DatabaseItem(1, "THAILAND"),
      new DatabaseItem(2, "JAPAN"),
      new DatabaseItem(3, "SOUTH KOREA"),
      new DatabaseItem(4, "VIETNAM"));

    countrySpinney.setSearchableAdapter(new SpinneyAdapter<>(this, country));
    countrySpinney.setOnItemSelectedListener(new Spinney.OnItemSelectedListener<DatabaseItem>() {
      @Override public void onItemSelected(Spinney view, DatabaseItem selectedItem, int position) {
        Toast.makeText(SampleActivity.this, "Welcome to " + selectedItem.getName(),
          Toast.LENGTH_SHORT).show();
      }
    });

    List<DatabaseItem> cities = Arrays.asList(
      new DatabaseItem(1, "BANGKOK", 1),
      new DatabaseItem(2, "PATTAYA", 1),
      new DatabaseItem(3, "CHIANG MAI", 1),
      new DatabaseItem(4, "TOKYO", 2),
      new DatabaseItem(5, "HOKKAIDO", 2),
      new DatabaseItem(6, "SEOUL", 3),
      new DatabaseItem(7, "HOJIMIN", 4)
    );

    citiesSpinney.setItems(cities);
    citiesSpinney.filterBy(countrySpinney, new Spinney.Condition<DatabaseItem, DatabaseItem>() {
      @Override public boolean filter(DatabaseItem parentItem, DatabaseItem item) {
        return item.getParentId() == parentItem.getId();
      }
    });
  }
}
