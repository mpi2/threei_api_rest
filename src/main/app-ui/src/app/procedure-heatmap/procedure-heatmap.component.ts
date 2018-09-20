import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatCard  } from '@angular/material';
import * as Highcharts from 'highcharts/highcharts';
import * as HC_map from 'highcharts/modules/map';
import * as HC_exporting from 'highcharts/modules/exporting';
//import * as HC_CustomEvents from 'highcharts-custom-events';
import { MatRadioModule, MatSelectModule } from '@angular/material';

import { HeatmapService } from '../heatmap.service';
import { ProcedureFilter } from './procedure-filter';

HC_map(Highcharts);
//require('../../js/worldmap')(Highcharts);

HC_exporting(Highcharts);
//HC_ce(Highcharts);

Highcharts.setOptions({
    series: [{
      data: [1, 2, 3]
    }]
  });




@Component({
  selector: 'threei-procedure-heatmap',
  templateUrl: './procedure-heatmap.component.html',
  styleUrls: ['./procedure-heatmap.component.css']
})
export class ProcedureHeatmapComponent implements OnInit {
    Highcharts = Highcharts;
    @ViewChild('searchBox') searchBox;
    constructs: string[];
    cellSubTypeDropdowns: string[];
    cellSubType: any;
    readonly PROCEDURE_TYPE = 'procedure';
  readonly CELL_TYPE = 'cell';
  cellTypesForDropdown: string[];


        data: any[][]=[[]];
        headers: string[];//http response headers
        columnHeaders: string[];
        rowHeaders: string[];
        response: Response;
        constructColumnData: any[];//need any as string for construct and ints for col and row indexes

        updateDemo2 = false;
  usedIndex = 0;
  chartTitle = 'Procedure Heatmap'; // for init - change through titleChange

  procedureChart;
  resourceLoaded: boolean =false;
  
  procedureChartOptions={

    
    chart: {
      type: 'heatmap',
      marginTop: 200,
      marginBottom: 80,
      plotBorderWidth: 1,
      height: 17000,
      width: 1000
  },
  
  
  title: {
      text: 'Procedure Type Heatmap'
  },
  
  xAxis: { 
    opposite: true,
      categories: [
            'Data loading...',
                 ],
      labels: {
          rotation: 90
      },
      reserveSpace: true,
    },
  
    yAxis:[
      {
     categories: ['gene blah1, gene blah2'],
     title: 'gene'
  },{
     linkedTo: 0,
     title: 'construct',
     lineWidth: 2,
     categories: ['constr blah1, contr blah2'],
  }],
  
  colorAxis: {
  
    dataClasses: [{
      from: 0,
      to: 1,
      color: '#ffffff',
      name: 'No Data'
  }, {
      from: 1,
      to: 2,
      color: '#808080',
      name: 'Not enough data'
  }, {
      from: 2,
      to: 3,
      color: '#0000ff',
      name: 'Not Significantly Different'
  }, {
      from: 3,
      to: 4,
      color: '#c4463a',
      name: 'Significantly Different'
  }
  ],
  min: 0,
  max: 4,
  },
  
  legend: {
      align: 'right',
      layout: 'vertical',
      // margin: 0,
      verticalAlign: 'top',
      // y: 25,
      // symbolHeight: 280
  },
  
  
  plotOptions: {
    series: {
        events: {
            click: function (e) {
                // var text = '<b>Clicked</b><br>Series: ' + this.name +
                //         '<br>Point: ' + e.point.name + ' (' + e.point.value + '/km²)';
               
                //may have to use routerLink like for menus to link to our new not created yet parameter page
                  var url = 'http://starwars.com';
                  window.open(url,'_blank');
                    // this.chart.clickLabel.attr({
                    //     text: text
                    // });
                
            }
        }
    }
  },
  
  series: [{
    name: 'Data Loading....',
    borderWidth: 1,
    data: [[0, 0, 'blah'], [0, 1, 1]],
    //, [2, 0, 35], [2, 1, 15], [2, 2, 123], [2, 3, 64], [2, 4, 52], [3, 0, 72], [3, 1, 132], [3, 2, 114], [3, 3, 19], [3, 4, 16], [4, 0, 38], [4, 1, 5], [4, 2, 8], [4, 3, 117], [4, 4, 115], [5, 0, 88], [5, 1, 32], [5, 2, 12], [5, 3, 6], [5, 4, 120], [6, 0, 13], [6, 1, 44], [6, 2, 88], [6, 3, 98], [6, 4, 96], [7, 0, 31], [7, 1, 1], [7, 2, 82], [7, 3, 32], [7, 4, 30], [8, 0, 85], [8, 1, 97], [8, 2, 123], [8, 3, 64], [8, 4, 84], [9, 0, 47], [9, 1, 114], [9, 2, 31], [9, 3, 48], [9, 4, 91]],
    dataLabels: {
        enabled: false,
        color: '#000000'
    }
  }],
  
  
  
};
   
  constructor(private heatmapService: HeatmapService) { 
    
  }

  ngOnInit() {
      
    
  }

  ngAfterViewInit() {
     this.getHeatmapData( this.searchBox);
  }

  getHeatmapData(filter: ProcedureFilter){
    
    console.log('searchbox='+this.searchBox);
    this.resourceLoaded=false;
    //if(this.data.length<=1){
    this.heatmapService.getProcedureHeatmapResponse(filter).subscribe(resp => {
      // display its headers
      this.response = { ... resp.body};
      //console.log('response='+JSON.stringify(resp));
      //this.data = this.response['response']['docs']
      //console.log('response from json file here: '+JSON.stringify(this.response['_embedded'].Data[0]['data']));

      this.data=this.response['data'];
      this.columnHeaders=this.response['columnHeaders'];
    //   this.columnHeaders=[
    //     '<[routerLink]="" (click)="onGoToPage2()">Homozygous viability at P14</a>',
    //     "Homozygous Fertility",
    //     "Haematology",
    //     "Peripheral Blood Leukocytes",
    //     "Spleen",
    //     "Mesenteric Lymph Node",
    //     "Bone Marrow",
    //     "Ear Epidermis",
    //     "Anti-nuclear Antibodies",
    //     "Cytotoxic T Cell Function",
    //     "DSS Challenge",
    //     "Influenza",
    //     "Trichuris Challenge",
    //     "Salmonella Challenge"
    //     ];
      this.rowHeaders=this.response['rowHeaders'];
      //here we need to add a whole column populated with the construct as java has no way of mixing strings and ints in an array
      this.constructs=this.response['constructs'];
      //console.log('construct='+this.constructs+'||');
      //console.log('rowheaders='+this.rowHeaders+'||');
      
    //   for (let index = 0; index < this.constructs.length; index++) {
    //     let cell:[string, number, number];//a new cell for each construct
    //       const construct = this.constructs[index];
    //       cell=[construct, 0, index];
    //       this.data.push(cell);
    //   }
      this.displayProcedureChart();
    
      
    });
  }

  
  //Highcharts = Highcharts;

// change in all places
titleChange = function(event) {
  var v = event;
  this.chartTitle = v;
  this.charts.forEach((el) => {
    el.hcOptions.title.text = v;
  });
  // trigger ngOnChanges
  this.updateDemo2 = true;
};
  



  displayProcedureChart(){
console.log('calling display procedure chart method');
this.procedureChart= {


    xAxis: { 
      opposite: true,
        categories: this.columnHeaders,
        labels: {
            rotation: 90
        },
        reserveSpace: true,
      },
//example of multiple columns working http://jsfiddle.net/0qmt0mkq/
yAxis: [{
    categories: this.rowHeaders,
    title: null
  },{
  categories: this.constructs,
  title: null
  } ],

    colorAxis: {

      dataClasses: [{
        from: 0,
        to: 1,
        color: '#ffffff',
        name: 'No Data'
    }, {
        from: 1,
        to: 2,
        color: '#808080',
        name: 'Not enough data'
    }, {
        from: 2,
        to: 3,
        color: '#0000ff',
        name: 'Not Significantly Different'
    }, {
        from: 3,
        to: 4,
        color: '#c4463a',
        name: 'Significantly Different'
    }
    ],
    min: 0,
    max: 4,
    },

    legend: {
        align: 'right',
        layout: 'vertical',
        // margin: 0,
        verticalAlign: 'top',
        // y: 25,
        // symbolHeight: 280
    },

    tooltip: {
        formatter: function () {
            return '<b>' + this.series.xAxis.categories[this.point.x] + '</b><br/>' +
            this.series.colorAxis.dataClasses[this.point.dataClass].name + '</b><br>'+
            '<b>' + this.series.yAxis.categories[this.point.y] + '</b>';
        }
    },
    plotOptions: {
      series: {
          events: {
              click: function (e) {
                  var gene= e.point.series.yAxis.categories[e.point.y];
                  //var construct=e.point.series.yAxis.categories[e.point.y];
                  var procedure=e.point.series.xAxis.categories[e.point.x];
                  
                  var text = 'gene: ' +gene +
                           ' Procedure: ' + procedure + ' significance=' + e.point.value ;
                 
                  //may have to use routerLink like for menus to link to our new not created yet parameter page
                    var url = 'http://starwars.com';
                    var routerLink='details?'+'procedure='+procedure+'&gene='+gene;
                    window.open(routerLink,'_blank');
                    //   this.chart.clickLabel.attr({
                    //       text: text
                    //   });
                      console.log('text on click='+text);
                  
              }
          }
      }
  },

    series: [{
        name: 'Procedures with significant parameters',
        borderWidth: 1,
        data: this.data,
        //data: [[0, 0, 0], [0, 1, 1], [0, 2, 2], [0, 3, 3], [0, 4, 3], [1, 0, 0], [1, 1, 1], [1, 2, 2], [1, 3, 3], [1, 4, 3]],
        //, [2, 0, 35], [2, 1, 15], [2, 2, 123], [2, 3, 64], [2, 4, 52], [3, 0, 72], [3, 1, 132], [3, 2, 114], [3, 3, 19], [3, 4, 16], [4, 0, 38], [4, 1, 5], [4, 2, 8], [4, 3, 117], [4, 4, 115], [5, 0, 88], [5, 1, 32], [5, 2, 12], [5, 3, 6], [5, 4, 120], [6, 0, 13], [6, 1, 44], [6, 2, 88], [6, 3, 98], [6, 4, 96], [7, 0, 31], [7, 1, 1], [7, 2, 82], [7, 3, 32], [7, 4, 30], [8, 0, 85], [8, 1, 97], [8, 2, 123], [8, 3, 64], [8, 4, 84], [9, 0, 47], [9, 1, 114], [9, 2, 31], [9, 3, 48], [9, 4, 91]],
        dataLabels: {
            enabled: false,
            color: '#000000'
        }
    }]
}
this.resourceLoaded=true;
      //this.Highcharts.setOptions(this.cellChartOptions);
      this.procedureChartOptions=this.procedureChart;
      this.updateDemo2=true;
      this.resourceLoaded=true;
  }//end of display method
  


  

   
      
}
