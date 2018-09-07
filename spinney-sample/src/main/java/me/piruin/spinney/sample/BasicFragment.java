/*
 * Copyright 2018 Piruin Panichphol
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.piruin.spinney.Spinney;

public class BasicFragment extends Fragment {

  @BindView(R.id.spinney_searchable) Spinney<String> searchableDept;
  @BindView(R.id.spinney_normal) Spinney<String> normalDept;

  @Nullable @Override public View onCreateView(
    @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_basic, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    normalDept.setItems(Data.department);

    searchableDept.setSearchableItem(Data.department);
    searchableDept.setOnItemSelectedListener(new Spinney.OnItemSelectedListener<String>() {
      @Override public void onItemSelected(Spinney view, String selectedItem, int position) {
        normalDept.clearSelection();
      }
    });
  }
}
