import { Component, OnInit } from '@angular/core';
import { AppService } from '../service/app.service';
import { Router } from '@angular/router';
@Component({
  selector: 'threei-phenotype-of-the-month',
  templateUrl: './phenotype-of-the-month.component.html',
  styleUrls: ['./phenotype-of-the-month.component.css']
})
export class PhenotypeOfTheMonthComponent implements OnInit {

  
  ngOnInit() {
  }

  constructor(private appService: AppService,
    private router: Router) {

}

phenMonthClick() {
// alert('link clicked!');
// Compute month based on date
console.log('app route click called');
this.appService.phenoType = this.getMonth();
this.router.navigate(['/phenotypeofthemonth']);
}

getMonth(): string {
var d = new Date();
let month = new Array();
month[0] = 'January';
month[1] = 'February';
month[2] = 'March';
month[3] = 'April';
month[4] = 'May';
month[5] = 'June';
month[6] = 'July';
month[7] = 'August';
month[8] = 'September';
month[9] = 'October';
month[10] = 'November';
month[11] = 'December';

return month[d.getMonth()];
}


getPhenotype(): string {
var d = new Date();
let n = d.getMonth() + 1;
switch (n) {
case 1 : return 'Adgrd1';
case 2 : return 'Bivm';
case 3 : return 'Chd9';
case 4 : return 'Clpp';
case 5 : return 'Cxcr2';
case 6 : return 'Gimap6';
case 7 : return 'Gmds';
case 8 : return 'PAF';
case 9 : return 'Peptidase D';
case 10 : return 'setd5';
case 11 : return 'Wdtc1';
case 12 : return 'Zfp408';
default :  return 'Cxcr2';

}
}
}
