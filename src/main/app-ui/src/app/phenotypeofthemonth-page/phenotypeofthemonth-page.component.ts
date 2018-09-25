import { Component, OnInit } from '@angular/core';
import { AppService } from '../service/app.service';

@Component({
  selector: 'app-phenotypeofthemonth-page',
  templateUrl: './phenotypeofthemonth-page.component.html',
  styleUrls: ['./phenotypeofthemonth-page.component.css']
})
export class PhenotypeofthemonthPageComponent implements OnInit {

  public phenoType: string;
  constructor(private appService: AppService) { }

  ngOnInit() {
    this.phenoType = this.appService.phenoType;
  }

}
