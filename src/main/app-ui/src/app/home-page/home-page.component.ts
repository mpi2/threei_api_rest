import { Component, OnInit , ViewEncapsulation} from '@angular/core';
import { MatTabChangeEvent } from '@angular/material';

@Component({
  selector: 'threei-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  encapsulation: ViewEncapsulation.None;

  constructor() { }

  ngOnInit() {
  }

  tabChanged = (tabChangeEvent: MatTabChangeEvent): void => {
    console.log('tabChangeEvent => ', tabChangeEvent);
    console.log('index => ', tabChangeEvent.index);
    if(tabChangeEvent.index==0){
      // this.heatmapService.setCellBusy();
    }
    if(tabChangeEvent.index==0){
      // this.heatmapService.setProcedureBusy();
    }

  }

}
