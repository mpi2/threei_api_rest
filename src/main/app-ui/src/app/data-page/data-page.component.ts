import { Component, OnInit } from '@angular/core';
import { MatRadioModule, MatSelectModule, MatTabChangeEvent } from '@angular/material';

@Component({
  selector: 'threei-data-page',
  templateUrl: './data-page.component.html',
  styleUrls: ['./data-page.component.css']
})
export class DataPageComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  tabChanged = (tabChangeEvent: MatTabChangeEvent): void => {
    console.log('tabChangeEvent => ', tabChangeEvent);
    console.log('index => ', tabChangeEvent.index);
  }

}
