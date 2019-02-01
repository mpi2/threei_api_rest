import { Component, OnInit , ViewEncapsulation} from '@angular/core';
import { MatTabChangeEvent } from '@angular/material';

import {FormControl, ReactiveFormsModule, FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'threei-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  encapsulation: ViewEncapsulation.None
  parentForm: FormGroup;
  doAutosuggest = true;
  // parentForm.searchControl = new FormControl();
  // selectControl = new FormControl();

  constructor(private fb: FormBuilder) { }

  ngOnInit() {
    this.parentForm = this.fb.group({ search: ['']});
    this.parentForm.valueChanges.subscribe(newVal => console.log(newVal));
  }

  tabChanged = (tabChangeEvent: MatTabChangeEvent): void => {
    console.log('tabChangeEvent => ', tabChangeEvent);
    console.log('index => ', tabChangeEvent.index);
    if (tabChangeEvent.index === 0) {
      // this.heatmapService.setCellBusy();
    }
    if (tabChangeEvent.index === 0) {
      // this.heatmapService.setProcedureBusy();
    }

  }

}
