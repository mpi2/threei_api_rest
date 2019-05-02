import { Component, OnInit , ViewEncapsulation} from '@angular/core';
import { MatTabChangeEvent, MatDialog } from '@angular/material';

@Component({
  selector: 'threei-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  encapsulation: ViewEncapsulation.None;

  constructor(public dialog: MatDialog) {}

  ngOnInit() {
  }

  expandOverview() {
    console.log('expand overveiw clicked');
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

  openDialog() {
    const dialogRef = this.dialog.open(OverviewDialog);

    // dialogRef.afterClosed().subscribe(result => {
    //   console.log(`Dialog result: ${result}`);
    // });
  }

}

@Component({
  selector: 'overview-dialog',
  templateUrl: 'overview-dialog.html',
})
export class OverviewDialog {}
