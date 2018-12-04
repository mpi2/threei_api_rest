import { Component, OnInit, Input, ViewEncapsulation } from '@angular/core';
import { MatMenuModule, MatToolbarModule,
  MatTooltipModule} from '@angular/material';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'threei-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  // encapsulation: ViewEncapsulation.None;
  @Input()
  topMenu: boolean;
  // constructor(private router: Router) { }
  constructor() { }

  ngOnInit() {
  }

  faqClick = function () {
    console.log('FAQ menu option selected');
    // this.router.navigateByUrl('/user');
  };

  contactClick = function () {
    console.log('contact menu option selected');
  };


  openImagesSearch() {
    const url = environment.chartBaseUrl + 'search/impc_images?kw=*';
   console.log('url for chart=' + url);
   window.open(url, '_blank');
  }
}
