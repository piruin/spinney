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
import android.widget.ArrayAdapter;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.piruin.spinney.Spinney;

public class SampleActivity extends AppCompatActivity {

  @BindView(R.id.spinney_normal) Spinney<String> normalSpinney;
  @BindView(R.id.spinney_searchable) Spinney<String> searchablespinney;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);
    ButterKnife.bind(this);

    searchablespinney.setSearchableAdapter(
      new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                         new String[] {"NSTDA", "NECTEC", "BIOTEC", "MTEC", "NANOTEC"}));
    searchablespinney.setOnItemSelectedListener(new Spinney.OnItemSelectedListener<String>() {
      @Override public void onItemSelected(Spinney view, String selectedItem, int position) {
        Toast.makeText(SampleActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
      }
    });

    normalSpinney.setItems(new String[] {"NSTDA", "NECTEC", "BIOTEC", "MTEC", "NANOTEC"});
    normalSpinney.setItemPresenter(new Spinney.ItemPresenter() {
      @Override public String getLabelOf(Object item, int position) {
        return item.toString();
      }
    });
    normalSpinney.setOnItemSelectedListener(new Spinney.OnItemSelectedListener<String>() {
      @Override public void onItemSelected(Spinney view, String selectedItem, int position) {
        Toast.makeText(SampleActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
      }
    });

    normalSpinney.filterBy(searchablespinney, new Spinney.Filter<String, String>() {
      @Override public boolean onChanged(String parentItem, String item) {
        return item.equals(parentItem);
      }
    });
  }
}
