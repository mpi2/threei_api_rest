import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatRadioModule, MatSelectModule, MatTabChangeEvent } from '@angular/material';
import { ProcedureHeatmapComponent } from '../procedure-heatmap/procedure-heatmap.component';
import { CellHeatmapComponent } from '../cell-heatmap/cell-heatmap.component';
import { HeatmapService } from 'src/app/heatmap.service';

@Component({
  selector: 'threei-data-page',
  templateUrl: './data-page.component.html',
  styleUrls: ['./data-page.component.css'],
  
})
export class DataPageComponent implements OnInit {

  @ViewChild('CellHeatmapComponent')
  private cellHeatmap: CellHeatmapComponent;

  @ViewChild('ProcedureHeatmapComponent')
  private procedureHeatmap: ProcedureHeatmapComponent;


  constructor(private heatmapService: HeatmapService) {
    
   }

  ngOnInit() {
  }

  ngAfterViewInit() {
    //this.cellHeatmap.getCellSubTypesDropdown();
  }

  tabChanged = (tabChangeEvent: MatTabChangeEvent): void => {
    console.log('tabChangeEvent => ', tabChangeEvent);
    console.log('index => ', tabChangeEvent.index);
    if(tabChangeEvent.index==0){
      //this.heatmapService.setCellBusy();
    }
    if(tabChangeEvent.index==0){
      //this.heatmapService.setProcedureBusy();
    }

  }

}
